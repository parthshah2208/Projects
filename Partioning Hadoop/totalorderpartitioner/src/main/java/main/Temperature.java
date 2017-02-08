package main;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.InputSampler;
import org.apache.hadoop.mapreduce.lib.partition.TotalOrderPartitioner;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.yarn.util.Times;
 
public class Temperature extends Configured{
    
    public static class TempMapper extends Mapper<LongWritable, Text, DoubleWritable, Text> {
    	@Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String rowValue = value.toString();
            StringTokenizer st = new StringTokenizer(rowValue, ", ");
            String[] rowList=new String[st.countTokens()];
            int index = 0;
            String row = "";
            while (st.hasMoreTokens()){ 
                rowList[index] = st.nextToken();
                row += rowList[index] + " ";
                index++;
            }
            //emiiting key as temperature and the whole tuple as the value
            if(!rowList[3].equals("TEMP")) //Stripping the first row of the input file
                context.write(new DoubleWritable(Double.parseDouble(rowList[3])), new Text(row));
        }
    }
//    This is the code for custom Partitioner,class not used when we are sampling input using input sampler
//    public static class CustomPartitioner extends Partitioner < DoubleWritable, Text >
//       {
//          @Override
//          public int getPartition(DoubleWritable key, Text value, int numReduceTasks)
//          {
//             String[] str = value.toString().split(" ");
//             double temp = Double.parseDouble((str[3]));
//             
//             if(numReduceTasks == 0)
//             {
//                return 0;
//             }
//             
//             if(temp<=40)
//             {
//                return 0;
//             }
//             else if(temp>40 && temp<=60)
//             {
//                return 1 % numReduceTasks;
//             }
//             else if(temp>60 && temp<=70)
//             {
//                return 2 % numReduceTasks;
//             }
//             else if(temp>70 && temp<=80)
//             {
//                return 3 % numReduceTasks;
//             }
//             else
//             {
//                return 4 % numReduceTasks;
//             }
//          }
//       }
    
    public static class TempReducer extends Reducer<DoubleWritable, Text, Text, Text> {
    	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for(Text val : values){
                context.write(new Text(""), val);
            }
        }
    }
      

    
    
    public static void main(String arg[]) throws Exception
       {
    	Timestamp tstart = new Timestamp(System.currentTimeMillis());
    	Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, arg)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err
					.println("Usage: Jar_Path i/p o/p samplingRate");
			System.exit(1);
		}

		Path inputPath = new Path(otherArgs[0]);
		Path partitionFile = new Path(otherArgs[1] + "_partitions.lst");
		Path outputStage = new Path(otherArgs[1] + "_staging");
		Path outputOrder = new Path(otherArgs[1]);
		double sampleRate = Double.parseDouble(otherArgs[2]);

		FileSystem.get(new Configuration()).delete(outputOrder, true);
		FileSystem.get(new Configuration()).delete(outputStage, true);
		FileSystem.get(new Configuration()).delete(partitionFile, true);

		// Configure job to prepare for sampling
		Job sample = new Job(conf, "Sample");
		sample.setJarByClass(Temperature.class);

		// Use the mapper implementation with zero reduce tasks
		sample.setMapperClass(TempMapper.class);
		sample.setNumReduceTasks(0);

		sample.setOutputKeyClass(DoubleWritable.class);
		sample.setOutputValueClass(Text.class);

		TextInputFormat.setInputPaths(sample, inputPath);

		// Set the output format to a sequence file
		sample.setOutputFormatClass(SequenceFileOutputFormat.class);
		SequenceFileOutputFormat.setOutputPath(sample, outputStage);

		// Submit the job and get completion code.
		int code = sample.waitForCompletion(true) ? 0 : 1;

		if (code == 0) {
			Job job = new Job(conf, "Sample");
			job.setJarByClass(Temperature.class);

			// Here, use the identity mapper to output the key/value pairs in
			// the SequenceFile
			job.setMapperClass(Mapper.class);
			job.setReducerClass(TempReducer.class);

			// Set the number of reduce tasks 
			job.setNumReduceTasks(5);

			// Use Hadoop's TotalOrderPartitioner class
			job.setPartitionerClass(TotalOrderPartitioner.class);

			// Set the partition file
			TotalOrderPartitioner.setPartitionFile(job.getConfiguration(),
					partitionFile);

			job.setOutputKeyClass(DoubleWritable.class);
			job.setOutputValueClass(Text.class);

			// Set the input to the previous job's output
			job.setInputFormatClass(SequenceFileInputFormat.class);
			SequenceFileInputFormat.setInputPaths(job, outputStage);

			// Set the output path to the argument given
			TextOutputFormat.setOutputPath(job, outputOrder);

			//Seperate using an empty String
			job.getConfiguration().set(
					"mapred.textoutputformat.separator", "");

			// Use the InputSampler to go through the output of the previous
			// job, sample it, and create the partition file
			InputSampler.Sampler<DoubleWritable, Text> input = new InputSampler.RandomSampler<DoubleWritable,Text>(sampleRate, 1000,10);
			InputSampler.writePartitionFile(job,input);
			//InputSampler.writePartitionFile(job,new InputSampler.RandomSampler(sampleRate, 10000));

			// Submit the job
			code = job.waitForCompletion(true) ? 0 : 2;
		}

		// Cleanup the partition file and the staging directory
		FileSystem.get(new Configuration()).delete(partitionFile, false);
		FileSystem.get(new Configuration()).delete(outputStage, true);
		Timestamp tend = new Timestamp(System.currentTimeMillis());
		System.out.println(tstart);
		System.out.println(tend);
		System.exit(code);
	}
          
          

}

    
    
    
   
 
   
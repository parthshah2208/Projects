package main;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
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
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
 
public class Temperature extends Configured implements Tool{
    
    public static class TempMapper extends Mapper<LongWritable, Text, DoubleWritable, Text> {

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String rowValue = value.toString(); 						//converting tuple to string 
            StringTokenizer st = new StringTokenizer(rowValue, ", "); 	//using the string tokenized to split using space
            String[] rowList=new String[st.countTokens()];
            int index = 0;
            String row = "";
            while (st.hasMoreTokens()){ 
                rowList[index] = st.nextToken();
                row += rowList[index] + " ";
                index++;
            }
            if(!rowList[3].equals("TEMP"))		//removing the tuple if its the first one
                context.write(new DoubleWritable(Double.parseDouble(rowList[3])), new Text(row));  //parsing key as temp and value as the entire tuple
        }
    }
    
    public static class CustPartitioner extends Partitioner < DoubleWritable, Text > //custom partitioner written for equal temp range
       {
          @Override
          public int getPartition(DoubleWritable key, Text value, int numReduceTasks)
          {
             String[] str = value.toString().split(" "); 		//splitting extra data
             double temp = Double.parseDouble((str[3]));
             
             if(numReduceTasks == 0)
             {
                return 0;
             }
             
             if(temp<=200)							//temp ranges are from 0-200
             {										//200-400
                return 0;							//400-600
             }										//600-800
             else if(temp>200 && temp<=400)			//800-999.99 
             {
                return 1 % numReduceTasks;
             }
             else if(temp>400 && temp<=600)
             {
                return 2 % numReduceTasks;
             }
             else if(temp>600 && temp<=800)
             {
                return 3 % numReduceTasks;
             }
             else
             {
                return 4 % numReduceTasks; //these return statements return which reducer to go to
             }
          }
       }
    
    public static class TempReducer extends Reducer<DoubleWritable, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for(Text val : values){
                context.write(new Text(""), val); //Printing in the o/p file
            }
        }
    }
    
    
    
    @Override
       public int run(String[] arg) throws Exception{
    	Timestamp tstart = new Timestamp(System.currentTimeMillis());
        Job job = Job.getInstance();
        job.setJobName("MyJob");
        job.setJarByClass(Temperature.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapOutputKeyClass(DoubleWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setMapperClass(TempMapper.class);
        job.setPartitionerClass(CustPartitioner.class);
        job.setReducerClass(TempReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setNumReduceTasks(5);
        FileInputFormat.setInputPaths(job,new Path(arg[0]));
		FileOutputFormat.setOutputPath(job,new Path(arg[1]));
      
        job.waitForCompletion(true);
        Timestamp tend = new Timestamp(System.currentTimeMillis());
		System.out.println(tstart);
		System.out.println(tend);
        return 0;

    }
    
    public static void main(String ar[]) throws Exception
       {
          int res = ToolRunner.run(new Configuration(), new Temperature(),ar);
          
          System.exit(0);
       }
}
package project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.io.InputStream;

public class client
{
 
    private static Socket socket;
    public static String content=null;
 
    public static void main(String args[])throws IOException
    {
        try{
            String host = "localhost";
            int port=8080;
            
            InetAddress address = InetAddress.getByName(host);
            socket = new Socket(address, port);
            
			// The path to access the file is given here, i.e ip address and port number, file name.
            Scanner scan = new Scanner(System.in);
            String url;
            System.out.println("Connection  is established with port number \n" 				+port);
            System.out.println(" Enter URL: ");
            url = scan.nextLine();
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url)); //used to open a file in a URL

            
         
            
            in.close();
						//Sending message to the server side
            OutputStream o = socket.getOutputStream();
            OutputStreamWriter outwriter = new OutputStreamWriter(o);
            BufferedWriter bf = new BufferedWriter(outwriter);
            String sendMessage = url+ "\n";
            bf.write(sendMessage);
            bf.flush();
            System.out.println(“Message sent -“+sendMessage);
 
				                    //fetching content from the server 
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = null;
            br=new BufferedReader(isr);  //storing the messages line by line in buffers and then putting them in a string to output it.
            String line= null;
            message = br.readLine();    
	    System.out.println( line);
                           
	 }
        catch (Exception e) 
        {
            e.printStackTrace();
        }


       finally
	{
	
	System.out.println("\n\nConnection terminated");
     	socket.close(); // closing client socket

        }
        

    }
}

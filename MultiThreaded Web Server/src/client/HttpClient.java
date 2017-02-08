

//Shivani Narang (1001121600)

package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class HttpClient 
{

 private static Socket socket;
 public static String content=null;

 public static void main(String args[])throws IOException
 {
     try{
         String host = "localhost";
         int port=9001;
      
         InetAddress address = InetAddress.getByName(host);
         socket = new Socket(address, port);
         
//the url which is to be opened in the browser would be entered by the user
         Scanner in = new Scanner(System.in);
         String url;
         System.out.println("Connection  is established with the server port \n" +port);
         System.out.println(" Enter URL: ");
         url = in.nextLine();
         java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
         
      
         
         in.close();
//Sending message to the server side
         OutputStream os = socket.getOutputStream();
         OutputStreamWriter osw = new OutputStreamWriter(os);
         BufferedWriter bw = new BufferedWriter(osw);
         String sendMessage = url+ "\n";
         bw.write(sendMessage);
         bw.flush();
         System.out.println("Message is sent to server : "+sendMessage);

//fetching content from the server 
         InputStream is = socket.getInputStream();
         InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = null;
               br=  new BufferedReader(isr);
         String message= null;
          
                 message = br.readLine();    
			System.out.print( message);
                        
}
     catch (Exception exception) 
     {
         exception.printStackTrace();
     }


    finally{
	
	System.out.println("\n\nSocket Connection is terminated");
  	socket.close(); // closing client socket

     }
     

 }
 public void NoSuchElementException()
 {
 	
 }
}

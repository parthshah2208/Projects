// Establishing TCP connection

//Shivani Narang (1001121600)

package client;

import java.net.*;
import java.io.*;
import java.util.* ;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
public final class HttpServer
{
	public static int count= 0;
	public static void main (String args[])
	{
		Socket sock;
		ServerSocket socket;
		int port=8080;
		
		System.out.println ("Server is on");
		try
		{
		
			 if(args[0] != null)
	          {
	        	  port= Integer.parseInt(args[0]);
	          }
		  socket = new ServerSocket (port);

		  while (true)
		  	{
		  		System.out.println ("Server: Waiting for connection..");   //waits for TCP connection request
		  		sock = socket.accept();
			    count++;
		  	    System.out.println ("Server: Received connection " + count);
		  	    HttpRequest request = new HttpRequest(sock);	//  HttpRequest object is created that will respond the HTTP request message.
		  	    
		  	   Thread thread = new Thread (request); //creating a thread for each client request
		  	    thread.start(); 
		    }
	    } 
	    catch (Exception e)
        {
        	System.err.println("Server: Exception in main: " + e); //to catch the exceptions if there exists any exceptions
        }
	}
}

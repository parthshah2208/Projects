

package project;


import java.io.*;
import java.net.*;
import java.util.* ;


public final class HttpServer
{
	public static int num= 0;
	public static void main (String args[])
	{
		try
		{

		Socket sock;
		ServerSocket socket;
		int port=9001;
		
		System.out.println ("Server is on");
		socket = new ServerSocket (port);

	        while (true)
		{
		  	System.out.println ("Waiting for connection..");   //waits for TCP connection request
		  	sock = socket.accept();
			num++;
	      	 	System.out.println ("Received connection " + num);
		  	ReqClient request = new ReqClient(sock);	//  HttpRequest object is created that will respond the HTTP request message.
		  	   
		  	Thread thread = new Thread (request); //creating a thread for each client request
		  	thread.start(); 
		    }
	    } 
	    catch (Exception e)
	     {
     		System.out.println(“Fault”); //to catch the exceptions if there exists any exceptions
    	     }
	}
}

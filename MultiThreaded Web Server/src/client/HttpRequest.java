
//Shivani Narang (1001121600)

// this class would be called when HttpServer.java file would be run
package client;

import java.io.*;
import java.net.*;
import java.util.*;

public final class HttpRequest implements Runnable {
 final static String CRLF = "\r\n";   //to represent the end of the line
 Socket socket;

 public HttpRequest(Socket socket) throws Exception
 {
 this.socket = socket;
 }

 //run() method of the Runnable interface.
 public void run()
 {
 try {	
 processRequest();
 } catch (Exception e) {
 System.out.println(e);
 }
 }
	 
 private void processRequest() throws Exception
 {
 InputStream is = socket.getInputStream();   //Starts the input from client machine

 DataOutputStream os = new DataOutputStream(socket.getOutputStream());



 BufferedReader br = new BufferedReader(new InputStreamReader(is)); //reading the input from client

 String requestLine = br.readLine();
 System.out.println(requestLine);

 //To print the IP address of the incoming connection.

 InetAddress incomingAddress = socket.getInetAddress();
 String ipString= incomingAddress.getHostAddress();
 System.out.println("The incoming IP address is:   " + ipString);
 StringTokenizer tokens = new StringTokenizer(requestLine);
 tokens.nextToken();  
 String fileName = tokens.nextToken();
 // Prepend a ?.? so that file request is within the current directory.
 fileName = "." + fileName;

 String headerLine = null;
 while ((headerLine = br.readLine()).length() != 0) { //While the header still has text, print it
 System.out.println(headerLine);
 }

 // Open the requested file.
 FileInputStream fis = null;
 boolean fileExists = true;
 try {
 fis = new FileInputStream(fileName);
 } catch (FileNotFoundException e) {
 fileExists = false;
 }   

 //Constructing response message
 String statusLine = null;
 String contentTypeLine = null;
 String entityBody = null;
 if (fileExists) {
 statusLine = "HTTP/1.1 200 OK: ";
 contentTypeLine = "Content-Type: " +
 contentType(fileName) + CRLF;
 } else {
 statusLine = "HTTP/1.1 404 Not Found: ";
 contentTypeLine = "Content-Type: text/html" + CRLF;
 entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
 }
 //End of response message construction

 // Send the status line.
 os.writeBytes(statusLine);

 // Send the content type line.
 os.writeBytes(contentTypeLine);

 // Send a blank line to indicate the end of the header lines.
 os.writeBytes(CRLF);

 // Send the entity body.
 if (fileExists) {
 sendBytes(fis, os);
 fis.close();
 } else {
 os.writeBytes(entityBody);
 }

 os.close(); //Close input and output streams and socket.
 br.close();
 socket.close();

 }

//Need this one for sendBytes function called in processRequest
private static void sendBytes(FileInputStream fis, OutputStream os)
throws Exception
{
 // Construct a 1K buffer to hold bytes on their way to the socket.
 byte[] buffer = new byte[1024];
 int bytes = 0;

 // Copy requested file into the socket?s output stream.
 while((bytes = fis.read(buffer)) != -1 ) {
 os.write(buffer, 0, bytes);
 }
}
private static String contentType(String fileName)
 {
 if(fileName.endsWith(".htm") || fileName.endsWith(".html"))
 return "text/html";
 if(fileName.endsWith(".jpg"))
 return "text/jpg";
 if(fileName.endsWith(".gif"))
 return "text/gif";
 return "application/octet-stream";
 
 }
}
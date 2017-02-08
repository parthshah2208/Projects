

//
package project;

import java.io.*;
import java.net.*;
import java.util.*;

public final class ReqClient implements Runnable
{
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
}
 catch (Exception e) 
{
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
// Prepend a ‘.’ so that file request is within the current directory.
	fileName = "." + fileName;

	String headerLine = null;

while ((headerLine = br.readLine()).length() != 0)
{ 
	System.out.println(headerLine);//While the header still has text, print it
}

// Open the requested file.
FileInputStream fis = null;
boolean fileExists = true;
try 
{
	fis = new FileInputStream(fileName);
} catch (FileNotFoundException e) 
{
	fileExists = false;
}   

//Constructing response message
String statusLine = null;
String contentTypeLine = null;
String entityBody = null;
if (fileExists) 
{
	statusLine = "HTTP/1.1 200 OK: ";
	contentTypeLine = "Content-Type: " +
	contentType(fileName) + CRLF;
}
 else 
{
	statusLine = "HTTP/1.1 404 Not Found: ";
	contentTypeLine = "Content-Type: text/html" + CRLF;
	entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not 	Found</BODY></HTML>";
}
//End of response message construction

// Sending and printing status line.
os.writeBytes(statusLine);
System.out.println(statusLine);

// Sending and printing content type line.
os.writeBytes(contentTypeLine);

System.out.println(contentTypeLine);
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

//Send bytes function is called in process request fucntion
private static void sendBytes(FileInputStream fis, OutputStream os)
throws Exception
{
// construction of a buffer to hold.
byte[] buffer = new byte[1024];
int bytes = 0;
while((bytes = fis.read(buffer)) != -1 ) //filling from buffer to output stream

{
os.write(buffer, 0, bytes);
}
}
private static String contentType(String fileName) //checks to give us the file type
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

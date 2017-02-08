The Implementation of the project has been done in Java. I have used Eclipse IDE to help me develop the code.

The Project Contains three classes which are Sever.java, Client.java and Reqclient.java. It also contains an Html file which is requested by the client.

Server.java has the code for the server logic. Basically making a server socket and listening to multiple connections at a time.

ReqClient.java is called by Server.java to process every client request.

Client.java is the client side code, which runs on the client side making requests to the server for accessing a file.

Demonstration of the Project.

1.	Open eclipse and choose a workspace.
2.	Copy all the files in the zip folder to the src folder in ur workspace.
3.	Open these files in Eclipse.
4.	Run the server.java file first.
5.	Open the browser to check if is accessing the myfile.html file via port 9001
6.	Run the client.java file by entering the ip address of the server and file name requested.
7.	Once we run this, our browser will pop up with file requested.
8.	If file is not present the browser will say not found.
9.	Run client.java again for multithreaded approach.


Output of the project
•	Server.java

•	Myfile.html on the browser.



•	Client.java (Round trip time printed here)





References:

1.	http://www.cse.chalmers.se/edu/year/2012/course/_courses_2011/EDA343/Assignments/Assignment1/Assignment1_presentation.pdf
2.	https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
3.	http://syntx.io/a-client-server-application-using-socket-programming-in-java/
4.	http://cs.lmu.edu/%7Eray/notes/javanetexamples/
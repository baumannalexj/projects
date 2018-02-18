package jokeserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/*--------------------------------------------------------

1. Name / Date:
ALEXANDER BAUMANN - 1/24/2016
CSC 435 Distributed Systems Winter 2016

2. Java version used, if not the official version for the class:

java version "1.8.0_65"
Java(TM) SE Runtime Environment (build 1.8.0_65-b17)
Java HotSpot(TM) 64-Bit Server VM (build 25.65-b01, mixed mode)

3. Precise command-line compilation examples / instructions:


> javac JokeClientAdmin.java 


4. Precise examples / instructions to run this program:

e.g.:

In separate shell windows:

> java JokeServer 55254 // can put any acceptable port number here
> java JokeClient <serverIP/localhost> 55254
> java JokeClientAdmin <serverIP/localhost> 55255 // client admin port should be +1 to serverport

All acceptable commands are displayed on the various consoles.

This runs across machines (including cross OS),  in which case you have to pass the IP address of
the server to the clients. For exmaple, if the server is running at
111.222.1.33 then you would type:

> java JokeClient 111.222.1.33 55254
> java JokeClientAdmin 111.222.1.33 55255

5. List of files needed for running the program.

e.g.:

 a. JokeOutput.html // not nec to run but included
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

5. Notes:

though I was on a successful and progressive trajectory to save state locally on disk, 
I had to stop short.. A file is created by the client and successfully communicated back to the server
but the client does not yet successfully receive and update the state based on 
which joke or proverb was told. So close though...


** sometimes the client or server 'hang', pressing enter on the corresponding hungup 
console should help it continue as normal.
----------------------------------------------------------*/
public class JokeClientAdmin {

	public static String MODE = "j"; // j= joke, p = proverb, m= maintenence 
	
	static void setMode(String servername, int port){
		Socket socket;
		BufferedReader brFromServer;
		PrintStream psToServer;
		String textFromServer;
		
		try{
			socket = new Socket(servername,port);
			
			brFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			psToServer	=	new PrintStream(socket.getOutputStream());
			
			System.out.println("Telling server which mode to switch to");
			psToServer.println(MODE);
			psToServer.flush();
			
			
			textFromServer= brFromServer.readLine();
			while (textFromServer !=null ){
				System.out.print(" ");
				System.out.println(textFromServer);
				textFromServer= brFromServer.readLine();
			}
			System.out.println();
			System.out.println("Mode changed");
			
		}catch(IOException ioe){
			System.out.println("IOException - Socket Error between admin and server");
			ioe.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		String servername; //should be IP for distant systems
		String userInput;
		int port = 11223+1; // default port is default server port + 1

		//handling multiple command line arguments, original idea by  Pat Walsh 
		switch (args.length){
		case 0:
			servername = "localhost";
			break;
		case 1:
			servername = args[0];
			break;
		case 2:
			servername = args[0];
			port = Integer.parseInt(args[1]) ;
			break;
		default:
			servername = "localhost";
			break;
		}


		System.out.println("ADMIN Client - CSC 435 Winter 2016");
		System.out.println("Using Server: " + servername + ", Port:" + port);


		//instantiate  reader for user input
		BufferedReader bradmininput = new BufferedReader(new InputStreamReader(System.in)); 
		
		try{

			do{				
				System.out.println("What mode would you like the server in? Type 'j' for joke, 'p' for proverb, "
						+ "'m' for maintenance, or type 'status' to see the current mode/status of the server , 'quit' to quit");
				System.out.flush();//clears out any ghost characters 
				userInput = bradmininput.readLine(); // we will change .readline() to while - read one char at a time 
				
				userInput = userInput.trim().toLowerCase();
				if (userInput.equals("shutdown") ){
					System.out.println();
					System.out.println("You shut down the server!!!! we are force quitting this connection");
					System.out.println();
					userInput = "quit";
				}
				else if (! userInput.equals("quit") ){
					
					MODE = userInput;
					System.out.println("Telling the server to change to mode: "+ MODE);
					setMode(servername, port);
				}
			}while (! userInput.trim().toLowerCase().equals("quit"));

		}catch (IOException ioe)	{ioe.printStackTrace();}
	}

}

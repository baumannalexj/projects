package jokeserver;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

/*--------------------------------------------------------

1. Name / Date:
ALEXANDER BAUMANN - 1/24/2016
CSC 435 Distributed Systems Winter 2016

2. Java version used, if not the official version for the class:

java version "1.8.0_65"
Java(TM) SE Runtime Environment (build 1.8.0_65-b17)
Java HotSpot(TM) 64-Bit Server VM (build 25.65-b01, mixed mode)

3. Precise command-line compilation examples / instructions:


> javac JokeClient.java


4. Precise examples / instructions to run this program:

e.g.:

In separate shell windows:

> java JokeServer 55254
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


public class JokeClient {

public static String NAME = "[NO NAME - Client]";

	static void getJokes(String servername, int port, String userInput, int[] stateTracker){
		// this method is where we will pass the "state' everytime
		// we will start with name
		Socket socket;
		BufferedReader brfromServer;
		PrintStream pstoServer;
		String textFromServer;
		
		try{
			// opens a connection to server with port number inputted
			socket = new Socket(servername,port);

			//create and filter IO streams for socket
			brfromServer = new BufferedReader(new InputStreamReader(socket.getInputStream() )); //reads stream from socket
			pstoServer = new PrintStream(socket.getOutputStream());

//			if (userInput.trim().toLowerCase().equals("shutdown")){
//				pstoServer.println("shutdown");
//			}
			
			//build string for server to parse state and name
			StringBuilder sb = new StringBuilder();
			for(int i =0; i<10;i++){
				sb.append(stateTracker[i]);
			}
			sb.append(NAME);
			
			pstoServer.println(sb.toString());
			pstoServer.flush();

			//read lines from server response while waiting
			System.out.println("Waiting for worker:");

			textFromServer= brfromServer.readLine();
			while (textFromServer !=null ){
				System.out.print(" ");
				System.out.println(textFromServer);
				textFromServer= brfromServer.readLine();
			}
			System.out.println();
			System.out.println("FROM:Server Name: "+servername+ ", Port# " +port);

			socket.close();

		}catch(IOException ioe){
			System.out.println("IOException - Socket Error For");
			ioe.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String servername; //should be IP for distant systems
		String userInput;
		int port = 11223; // default port

		//		if (args.length<1) serverName = "localhost"; //default server to local if nothing is entered
		//		else serverName = args[0] ; //else grab the inputted servername

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


		System.out.println("Joke Client - CSC 435 Winter 2016");
		System.out.println("Using Server: " + servername + ", Port:" + port);


		//instantiate  reader for user input
		BufferedReader bruserinput = new BufferedReader(new InputStreamReader(System.in)); 
		int[] stateTracker;
		try{
			System.out.println("What is your name?");
			System.out.flush();//clears out any ghost characters 
			NAME = bruserinput.readLine(); // we will change .readline() to while - read one char at a time
			
			File file = new File(NAME+".txt");
			if (! file.exists()){
				//when you enter your name, client will look for saved state.  if non exists, one will be made
				file.createNewFile();

				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
				Writer writer = new BufferedWriter(osw);
				System.out.println("Initializing file for " + NAME);
				stateTracker = new int[10];
				for(int i =0; i<10;i++){
					stateTracker[i]=0;
					writer.write(Integer.toString(stateTracker[i]) ); // all '0's is initial state (no jokes or proverbs given)
					
				}
				writer.flush(); // need to flush to force the writing
				
				writer.close();
			}else{
				System.out.println("Welcome back " + NAME + "!");
				FileInputStream in = new FileInputStream(file);
				int readin;
				stateTracker = new int[10];

				for(int i = 0; (readin = (in.read()) ) >-1 & i<10; i++){
					readin = Integer.parseInt(String.valueOf(((char) readin))) ; // reads ascii number input to char, to string, parse to int
					stateTracker[i]= (readin); // we go byte by byte, so convert byte to ascii byte equivalent
//					System.out.print(a[i]);
//					System.out.print(a[i]==1);
				}

			}
			FileInputStream in = new FileInputStream(NAME+".txt");
			
	
			do{

				System.out.println("Hello " + NAME +", your current State is: ");
				for(int i = 0; i<stateTracker.length; i++){
					System.out.print(stateTracker[i]);
				}
				System.out.println();
				System.out.println(NAME +", press ENTER for a new joke/proverb or 'quit'  to quit");
				System.out.flush();//clears out any ghost characters 

				userInput = bruserinput.readLine();
				if (userInput.trim().toLowerCase().equals("shutdown") ){
					System.out.println();
					System.out.println("You shut down the server!!!! we are force quitting this connection");
					System.out.println();
					getJokes(servername, port,userInput,stateTracker);
					userInput = "quit";
				}
				else if (! userInput.trim().toLowerCase().equals("quit") ){
					getJokes(servername, port, userInput,stateTracker);
				}
			}while (! userInput.trim().toLowerCase().equals("quit"));

		}catch (IOException ioe)	{ioe.printStackTrace();}
	}

}

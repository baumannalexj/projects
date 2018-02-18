package jokeserver;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Random;




/*--------------------------------------------------------

1. Name / Date:
ALEXANDER BAUMANN - 1/24/2016
CSC 435 Distributed Systems Winter 2016

2. Java version used, if not the official version for the class:

java version "1.8.0_65"
Java(TM) SE Runtime Environment (build 1.8.0_65-b17)
Java HotSpot(TM) 64-Bit Server VM (build 25.65-b01, mixed mode)

3. Precise command-line compilation examples / instructions:


> javac JokeServer.java 



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

*though I was on a successful and progressive trajectory to save state locally on disk, 
I had to stop short.. A file is created by the client and successfully communicated back to the server
but the client does not yet successfully receive and update the state based on 
which joke or proverb was told. So close though...

** sometimes the client or server 'hang', pressing enter on the corresponding hungup 
console should help it continue as normal.

----------------------------------------------------------*/
public class JokeServer {

	public static boolean 	controlSwitch = true; // this will be changed by worker if thread is cancelled
	public static int 		numJokes = 5; 
	public static int 		port = 11223;//default port I chose
	public static String 	MODE = "j";

	public static void main(String[] args) throws IOException{
		int queueLength = 6; // number of requests allowed to queue up, any more at once will be ignored


		// change port if input is given -- java JokeServer 11987
		switch (args.length){
		case 1:
			port = Integer.parseInt(args[0]);
			break;
		default:
			break;
		}
		
		System.out.println("JOKE SERVER - CSC 435 Winter 2016");
		System.out.println("Listening to port "+port);
		
		//create a separate thread for AdminCheck class which sees if the admin is trying to
		// communicate with the Server, and gives it chronological precedence over jokeclient 
		//TODO
		System.out.println("Starting Admin Checker Loop");
		AdminChecker adminChecker = new AdminChecker(); 
		Thread adminThread = new Thread(adminChecker);
		adminThread.start();

		// instantiate socket for the client and the 
		Socket soc; // soc-> >-ket
		ServerSocket ketServer = new ServerSocket(port,queueLength);//instantiates serversocket object


		
		while (controlSwitch){ // while loops is constantly listening
			soc = ketServer.accept(); // waits for next avail client connection
			System.out.println("Socket connection made");
			if (controlSwitch) {
				System.out.println("Spawning Worker");
				new Worker(soc).start(); // starts worker thread to handle
			}
			// can be commented out, will show details
			try{Thread.sleep(10000);} catch(InterruptedException iex) {
				System.out.println("Tried Waiting 10sec, but Interruption Exception");
			}
		}
		System.out.println("Closing Socket");
		ketServer.close(); // close server socket when control switch = false
	}

}

/**
 * 
 * 
 * @author Alexander
 *
 */
class Worker extends Thread{ //define class
	Socket socket;	//worker socket
	Worker(Socket s){this.socket = s;} //constructor for worker
	Random rand;
	static public int numJokes = 5;
	static public int numProverbs = 5;
	static public String NAME = "[NO NAME - SERVER]";


	static void printToClient(PrintStream psoutput,Socket socket,int[] stateTracker){
		
		//communicate with client which mode the server is in and supply the appropriate response
		//TODO need to incorporate stateTracker and randoms to pick a joke/proverb which hasn't been used yet
		//TODO then return/communicate new state to client
		try{
			System.out.println("Worker is in mode: " + JokeServer.MODE);
			
			if((JokeServer.MODE.trim().toLowerCase().equals("m"))){
				System.out.println("MAINTENANCE MODE");
				psoutput.println("SERVER CURRENTLY UNDER MAINTENANCE MODE. TRY AGAIN SOON.");
			}
			else if (JokeServer.MODE.trim().toLowerCase().equals("p")){
				System.out.println("Worker is giving client proverbs");
				psoutput.println("Here are your proverbs:");
					psoutput.println(runProverbs(new Random().nextInt(99999)%numProverbs));
			}
			else {
				System.out.println("Worker is giving client jokes");
				psoutput.println("Here are your jokes:");
					psoutput.println(runJokes(new Random().nextInt(99999)%numJokes));
			}

		}catch(Exception e) {
			System.out.println("Unknown Exception - Worker failed");
		}
	}


	static String runJokes(int i){
		
		//TODO will make these static 'fields'
		String[] jokes = new String[numJokes];
		jokes[0]= "A. "+NAME+",What's brown and sticky? -- a stick!";
		jokes[1]= "B. What's a physicists favorite food, "+NAME+"? -- Fission Chips!";
		jokes[2]= "C. There are 10 types of people in the world, "+NAME+",--those who understand binary, and those who don't!";
		jokes[3]= "D. "+NAME+" ,What do you call a polypeptide chain which hasn't folded yet? -- a pre-tein!";
		jokes[4]= "E."+ NAME+", What starts and ends with 'e' but only has one letter in it? -- an envelope!";

		
		return jokes[i];
	}

	static String runProverbs(int i){
		//TODO will make these static 'fields'
		String[] proverbs = new String[numProverbs];
		proverbs[0]= "A. "+ NAME+", if you chase two rabbits, and you will lose both";
		proverbs[1]= "B. To move a mountain,"+ NAME+", you must first move a stone";
		proverbs[2]= "C. Do or do not,"+ NAME+", there is no try";
		proverbs[3]= "D. When you speak "+ NAME+", you repeat what you know; when you listen,"+ NAME+", you learn something new";
		proverbs[4]= "E. "+ NAME+", A poor craftsman blames their tools";

		return proverbs[i];

	}

	public void run(){

		//1st get the IO streams in and out of the socket inputted	
		PrintStream pstoclient; // out
		BufferedReader brfromClient; //in

		try{
			brfromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pstoclient = new PrintStream(socket.getOutputStream());

			if ( !JokeServer.controlSwitch ){
				System.out.println("Workers says: \"Listener is now shutting down per client.\"");
				pstoclient.println("hey client: Server is now shutting down, constrolSwitch is false");
			}
			else try{
				
				//read from the client and react according to logic below
				String clientInput = brfromClient.readLine();
				if (clientInput.indexOf("shutdown")> -1 ){ // if "shutdown" exists at all
					JokeServer.controlSwitch = false;
					System.out.println("Worker Thread Found Shutdown Request");
					pstoclient.println("hey client: Shutdown request noted by worker");
					pstoclient.println("hey client: please send final shutdown request to server");
				}else{
					int[] stateTracker = new int[10];
					System.out.print("Current State: " );
					//embarrassing way of reading a string passed by client which contains the
					// current state and the NAME for the client
					for (int i =0; i<10; i++){
						stateTracker[i] =  Integer.parseInt(String.valueOf(((char) (clientInput.charAt(i) ) ) ));
						System.out.print(stateTracker[i]);
					}
					
					NAME = clientInput.substring(10);
					System.out.println();
					System.out.println("For: " +NAME);
					
					System.out.println("Worker is getting results -- mode = " + JokeServer.MODE);
					printToClient(pstoclient,socket,stateTracker);
				}

			}catch (IOException ioex){
				System.out.println("IOException - Server Read Error");
				ioex.printStackTrace();
			}
			System.out.println("Closing Worker");
			socket.close(); // close the socket connection, but server remains running if control switch = true
		}catch (IOException ioex){
			System.out.println("IOException " +ioex);
		}
	}
}



//package jokeServer;

class AdminChecker implements Runnable {

	public static boolean adminKeepRunning = true;  //controls whether the loop below should run or not
	
	public void run(){ // need run() since implementing Runnable
		System.out.println("ADMIN CHECK THREAD  -- CSC 453 Distributed Systems Winter 2016");
		
		int queueLength = 6; //take up to this manyrequests
		
		Socket adminSoc;
		
		try{
			ServerSocket ketServer = new ServerSocket(JokeServer.port + 1, queueLength); // admin will be on port +1 to server
			while (adminKeepRunning){
				// keep running to make socket connection
				System.out.println("Socket Connection -- Server is running AdminWorker");
				System.out.println();
				adminSoc = ketServer.accept();
				new AdminWorker(adminSoc).start();
			}
			
		}catch (IOException ioe) {System.out.println(ioe);}
	}
}
///////////////////////
// admin thread
//////////////////////
class AdminWorker extends Thread{ //define class
	Socket socket;	// socket
	AdminWorker(Socket s){this.socket = s;} //constructor
	Random rand;
	
	public void run(){
		//1st get the IO streams in and out of the socket inputted	
		PrintStream pstoadmin; // out
		BufferedReader brfromadmin; //in


		try{
			brfromadmin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pstoadmin = new PrintStream(socket.getOutputStream());

			if ( !JokeServer.controlSwitch ){
				System.out.println("AdminWorkers says: \"Server Listener is now shutting down per client.\"");
				pstoadmin.println("hey admin: Server is now shutting down!");
			}
			else try{
				String whatmode;
				
				 // look for anything typed by admin, set mode accordingly based on switch statement below
				whatmode = brfromadmin.readLine();
				whatmode = whatmode.trim().toLowerCase();
				if (whatmode.indexOf("shutdown")> -1 ){ // if "shutdown" exists at all
					JokeServer.controlSwitch = false;
					System.out.println("Admin Worker Thread Found Shutdown Request");
					pstoadmin.println("hey Admin: Shutdown request noted by worker");
					pstoadmin.println("hey Admin: please send final shutdown request to server");
				}else{
					switch (whatmode){
						case "j":
							System.out.println("AdminWorker Sees You Want To Change Mode To JOKE mode");
							JokeServer.MODE = "j";
							pstoadmin.println("Admin: Server now in JOKE mode");
							break;
						case "p":
							System.out.println("AdminWorker Sees You Want To Change Mode To PROVERB mode");
							JokeServer.MODE = "p";
							pstoadmin.println("Admin: Server now in PROVERB mode");
							break;
						case "m":
							System.out.println("AdminWorker Sees You Want To Change Mode To MAINTENANCE mode");
							JokeServer.MODE = "m";
							pstoadmin.println("Admin: Server now in MAINTENANCE mode.");
							break;
						case "status":
							System.out.println("Admin wants to see current mode/status");
							pstoadmin.println("Admin: you are currently in "+ JokeServer.MODE + " mode.");
							break;
						default:
							System.out.println("Admin entered unacceptable input");
							pstoadmin.println("Admin: your input was and unacceptable mode ("+whatmode+"). "
									+ "\nType 'j' for joke, 'p' for proverb, 'm' for maintenance, or 'status' to get current mode");
							break;
					}
					System.out.println();
					System.out.println("AdminWorker Sees You Want To Change Mode To: " + whatmode);
					JokeServer.MODE = whatmode;
					System.out.println("Server in " + whatmode+ " mode.");
					System.out.println();
				}

			}catch (IOException ioex){
				System.out.println("IOException - Server Read Error");
				ioex.printStackTrace();
			}
			socket.close(); // close the socket connection, but server remains running if control switch = true
		}catch (IOException ioex){
			System.out.println("IOException " +ioex);
		}
	}
}

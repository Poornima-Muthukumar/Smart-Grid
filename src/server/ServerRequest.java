package server;
import java.io.*;
import java.net.*;
import java.util.Arrays;

import server.Server;


public class ServerRequest implements Runnable {

	public Server server;
	public String serverName;
	
	public ServerRequest(String name, Server serverObj) {
			server = serverObj;
			serverName = name;
	}

	public void acceptRequest() throws IOException {
		
		String clientSentence;
		int port = Integer.parseInt(server.details.get(serverName).get(1));
	    ServerSocket welcomeSocket = new ServerSocket(port); 
	    System.out.println(port);
	    
		while(true) {
	            Socket connectionSocket = welcomeSocket.accept();
	            System.out.println("accepted invitation");
	            //infrom client
	            BufferedReader inFromClient = 
	                    new BufferedReader(new InputStreamReader(
	                        connectionSocket.getInputStream()));
	            
	            //outToclient
	            DataOutputStream outToClient = 
	                    new DataOutputStream(
	                        connectionSocket.getOutputStream());
	            
	            clientSentence = inFromClient.readLine();
	            System.out.println("clientSentence" + clientSentence);
	            
	            if(clientSentence.contentEquals("iteration")) {
	            	int iteration = server.calculateIterationRound();
	            	System.out.println("self iteration" +iteration);
	            	outToClient.writeBytes(serverName+":"+iteration+'\n');
	            	
	            } else if(clientSentence.startsWith("request")) {
	            	// return iteration 
	            }
	            
	        }
		
	}
	
	
    public void run() {
        System.out.println("Hello from server thread!");
        
        try {
			acceptRequest();
		} catch (IOException e) {
			System.out.println("failed to accept request");
		}
        
    }
}

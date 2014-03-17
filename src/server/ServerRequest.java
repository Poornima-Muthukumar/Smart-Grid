package server;
import java.io.*;
import java.net.*;

import server.Server;
import server2.Server2;
import server3.Server3;

public class ServerRequest implements Runnable {

	public Server server;
	
	public ServerRequest() {
			server = new Server();	
	}

	public void acceptRequest() throws IOException {
		
		String clientSentence;
	    ServerSocket welcomeSocket = new ServerSocket(server.PORT); 
	    System.out.println(server.PORT);
	    
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
	            	outToClient.writeBytes(server.serverName+":"+iteration+'\n');
	            	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}

package server;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Map;

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
	
	    
		while(true) {
	            Socket connectionSocket = welcomeSocket.accept();
	           
	            //infrom client
	            BufferedReader inFromClient = 
	                    new BufferedReader(new InputStreamReader(
	                        connectionSocket.getInputStream()));
	                  
	            clientSentence = inFromClient.readLine();
	            System.out.println("Sentence" + clientSentence);
	           
	            
	            if(clientSentence.contentEquals("iteration")) {
	            	
	            	 //outToclient
		            DataOutputStream outToClient = 
		                    new DataOutputStream(
		                        connectionSocket.getOutputStream());
		            
	            	int iteration = server.calculateIterationRound();
	            	outToClient.writeBytes(serverName+":"+iteration+'\n');
	            	
	            } else if(clientSentence.contains("request")) {
	            	
	                //output stream
		        	ObjectOutputStream outputStream = new ObjectOutputStream(
		        			connectionSocket.getOutputStream());
	            	 
	            	 String[] result = clientSentence.split(":");
	            	 
	            	 System.out.println("result"+Arrays.toString(result));
	            	 
	            	 int iteration =  Integer.parseInt(result[2]);
	            	 	            	            	 
	            	 int index = iteration % Integer.parseInt(server.details.get(serverName).get(4));
	            	 
	            	 
	            	 index = index % server.aggregatePowerProfile.length;
	            	 
	            	 double[] response = server.aggregatePowerProfile[index];
	            	 
	            	 DataObject obj = new DataObject(serverName, response);
	            	 System.out.println(obj.serverName);
	            	 System.out.println(Arrays.toString(obj.arrayValue));
	            	 
	            	 outputStream.writeObject(obj);
	            	 
	            }
	            else if(clientSentence.contains("speed")) {
	            	
	            	  DataOutputStream outToClient = 
			                    new DataOutputStream(
			                        connectionSocket.getOutputStream());
			            
		            	String speed = server.details.get(serverName).get(4);
		            	outToClient.writeBytes(serverName+":"+speed+'\n');
	            	 
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

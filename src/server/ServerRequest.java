package server;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import server.Server;


public class ServerRequest implements Runnable {

	public Server server;
	public String serverName;
	public Map<String,Integer> powerIndex = new HashMap<String,Integer>();

	
	public ServerRequest(String name, Server serverObj) {
			server = serverObj;
			serverName = name;
	}

	public void acceptRequest() throws IOException {
		
		String clientSentence;
		int port = Integer.parseInt(server.details.get(serverName).get(1));
	    ServerSocket welcomeSocket = new ServerSocket(); 
	    welcomeSocket.setReuseAddress(true);
	    welcomeSocket.bind(new InetSocketAddress(port));
	   
		while(true) {
	            Socket connectionSocket = welcomeSocket.accept();
	           
	            //infrom client
	            BufferedReader inFromClient = 
	                    new BufferedReader(new InputStreamReader(
	                        connectionSocket.getInputStream()));
	                  
	            clientSentence = inFromClient.readLine(); 

	            
	            if(clientSentence.contentEquals("iteration")) {
	            	
	            	 //outToclient
		            DataOutputStream outToClient = 
		                    new DataOutputStream(
		                        connectionSocket.getOutputStream());
		            
	            	int iteration = server.calculateIterationRound();
	            	outToClient.writeBytes(serverName+":"+iteration+'\n');
	            	connectionSocket.close();
	            	
	            } else if(clientSentence.contains("request")) {
	            	
	            	 //outToclient
		            DataOutputStream outToClient = 
		                    new DataOutputStream(
		                        connectionSocket.getOutputStream());
	            	 
	            	 String[] result = clientSentence.split(":");
	            	  
	            	 int iteration =  Integer.parseInt(result[2]);
	            	 	          
	            	 
	            	 if((iteration % Integer.parseInt(server.details.get(serverName).get(4)))==0 && iteration!=0) {
	            		 if(powerIndex.containsKey(result[0])) {
	            			 int a = powerIndex.get(result[0]);
	            			 a++;
	            			 powerIndex.put(result[0], a);
	            		 } else {
	            			 powerIndex.put(result[0], 1);
	            		 }
	            	 } else {
	            		if(!powerIndex.containsKey(result[0])){
	            			powerIndex.put(result[0], 0);
	            		}
	            	 }
	            	   	 
	            	 int index = powerIndex.get(result[0]) % server.aggregatePowerProfile.length;
	            	 
	            	// System.out.println(result[0] + " " +result[2] + " " +index);
	            	 
	            	 double[] response = server.aggregatePowerProfile[index];
	            	 
	            	 outToClient.writeBytes(Arrays.toString(response)+"\n");
	            	 connectionSocket.close();
	            	 
	            }
	            else if(clientSentence.contains("speed")) {
	            	
	            	  DataOutputStream outToClient = 
			                    new DataOutputStream(
			                        connectionSocket.getOutputStream());
			            
		            	String speed = server.details.get(serverName).get(4);
		            	outToClient.writeBytes(serverName+":"+speed+'\n');
		            	
		            	connectionSocket.close();
	            	 
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

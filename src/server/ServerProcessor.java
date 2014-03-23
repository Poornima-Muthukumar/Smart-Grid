package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServerProcessor implements Runnable {

	public DataOutputStream outToClient;
	public BufferedReader inFromClient;
	public Server server;
	public String serverName;
	public Map<String,Integer> powerIndex = new HashMap<String,Integer>();

	public ServerProcessor(BufferedReader inFromClient,
			DataOutputStream outToClient, Server server, String serverName) {
		
		this.outToClient = outToClient;
		this.inFromClient = inFromClient;
		this.server = server;
		this.serverName = serverName;
	
	}

	@Override
	public void run() {
		while(true) { 	
			String clientSentence = null;	
			
	            try {
					clientSentence = inFromClient.readLine();
					if (clientSentence == null) {
						return;
					}
				} catch (IOException e) {
					System.out.println("Not able to read");
					return;
				} 

	            if(clientSentence.contentEquals("iteration")) {
 
	            	int iteration = server.calculateIterationRound();
	            	try {
						outToClient.writeBytes(serverName+":"+iteration+'\n');
					} catch (IOException e) {
						System.out.println("Not able to write");
						return;
					}
	            	
	            	
	            } else if(clientSentence.contains("request")) {
	            	
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
	            	       	        	 
	            	 try {
						outToClient.writeBytes(Arrays.toString(server.aggregatePowerProfile[index])+"\n");
					} catch (IOException e) {
						System.out.println("Not able to write");
						return;
					}
	            	 
	            }
	            else if(clientSentence.contains("speed")) {
	            
		            	String speed = server.details.get(serverName).get(4);
		            	try {
							outToClient.writeBytes(serverName+":"+speed+'\n');
						} catch (IOException e) {
							System.out.println("Not able to write");
							return;
						}

	            }
	            
      	    }
		}
		
	}


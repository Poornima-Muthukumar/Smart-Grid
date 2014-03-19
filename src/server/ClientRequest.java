package server;
import java.io.*;
import java.net.*;
import java.util.Set;

import server.Server;

public class ClientRequest implements Runnable{

	public Server server;
	public String serverName;
	
	public ClientRequest(String name, Server serverObj) {
			server = serverObj;
			serverName = name;
}

	
public void requestIteration() throws NumberFormatException, UnknownHostException, IOException {
		
		
	   Set<String> name = server.details.keySet();
	   System.out.println("name array"+name);
	   String[] nameArray = new String[2];
	   int j=0;
	   for(String val: name) {
		   if(!val.equals(serverName)) {
		   nameArray[j] = val;
		   j++;
		   }
	   }
	   
		int i = 0;
		Socket clientSocket = null; 
		while(true) {
				
				 if(server.details.get(nameArray[i]).get(2) == "false") {	
					 
					 try{
						 
						 System.out.println("before connect"+server.details.get(nameArray[i]).get(0)+server.details.get(nameArray[i]).get(1));
				         clientSocket = new Socket(server.details.get(nameArray[i]).get(0), Integer.parseInt(server.details.get(nameArray[i]).get(1)));
				         server.details.get(nameArray[i]).set(2, "true");
				         
				         DataOutputStream outToServer = new DataOutputStream(
					                clientSocket.getOutputStream());
					        
					     BufferedReader inFromServer = 
					                new BufferedReader(new InputStreamReader(
					                    clientSocket.getInputStream()));
					        
				        outToServer.writeBytes("iteration\n");
				        String input = inFromServer.readLine();
				        System.out.println("FROM SERVER: " + input);
				        String[] result = input.split(":");
				        server.details.get(result[0]).set(3, result[1]);
				        server.totalIteration *= Integer.parseInt(result[1]);
				        System.out.println(server.totalIteration);
				        clientSocket.close();
					}
					 catch(IOException e) {
						 i++;
						 if(i==2) {
							 i=0;
						 }
						 System.out.println("OH NOES");
						 continue;
					 }
				 }
				 i++;
				 if(server.details.get(nameArray[1]).get(2) == "true" && server.details.get(nameArray[0]).get(2) == "true") {
					 break;
				 }
				 else {
					 if(i==2) {
						 i = 0;
					 }
				 }
		}
}
	
	public void run() {
        System.out.println("Hello from client thread!");  
        try {
        	
        	//Step1 - request iteration from other servers.
			requestIteration();
			int selfCount = server.calculateIterationRound();
			server.totalIteration*=selfCount;
			server.details.get(serverName).set(3, Integer.toString(selfCount));
			
			// calcuate aggregate power profile for different appliance configuation for each server. 
			server.calculateAggregatePowerProfile(selfCount);
			
			//calculate individual speed.
			server.calcluateServerSpeed(serverName);
	        System.out.println("TOTAL ITER"+server.totalIteration);
	        
	        //calculate total power profile of the system.
	        
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}

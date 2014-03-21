package server;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import server.Server;

public class ClientRequest implements Runnable{

	public Server server;
	public String serverName;
	public double[] server1Obj;
	public double[] server2Obj;
	
	public ClientRequest(String name, Server serverObj) {
			server = serverObj;
			serverName = name;
}

	
public void setUpConnection(String request) throws NumberFormatException, UnknownHostException, IOException, ClassNotFoundException {
		
		
	   Set<String> name = server.details.keySet();
	   String[] nameArray = new String[2];
	   int j=0;
	   for(String val: name) {
		   if(!val.equals(serverName)) {
		   nameArray[j] = val;
		   j++;
		   }
	   }
	   
	   System.out.println(Arrays.toString(nameArray) + request);
	   
		int i = 0;
		Socket clientSocket = null; 
		while(true) {
				
				 if(server.details.get(nameArray[i]).get(2).equals("false")) {	
					 
					 try{
				         clientSocket = new Socket(server.details.get(nameArray[i]).get(0), Integer.parseInt(server.details.get(nameArray[i]).get(1)));
				         server.details.get(nameArray[i]).set(2, "true");
				            
				        if(request.equals("iteration")) {
					        DataOutputStream outToServer = new DataOutputStream(
						                clientSocket.getOutputStream());
						        
						     BufferedReader inFromServer = 
						                new BufferedReader(new InputStreamReader(
						                    clientSocket.getInputStream()));
						        
					        outToServer.writeBytes(request+"\n");
					        String input = inFromServer.readLine();
					              
				        	String[] result = input.split(":");
					        server.details.get(result[0]).set(3, result[1]);   
					        server.totalIteration *= Integer.parseInt(result[1]);
					        clientSocket.close();
				        }
				        
				        else if(request.contains("request")){
				        	
				        	//output stream
				        	DataOutputStream outToServer = new DataOutputStream(
					                clientSocket.getOutputStream());
				        	
				        	//input stream
				        	ObjectInputStream inputStream = new ObjectInputStream(
				        			clientSocket.getInputStream());
				        	
				        	outToServer.writeBytes(request+"\n");
				        	
				        	
				        	DataObject obj = (DataObject)inputStream.readObject();
				        	System.out.println("after"+obj);
				        	
				        	clientSocket.close();
				        	
				        	/*
				        	if(server1Obj.length == 0) {
				        		server1Obj = obj.arrayValue;
				        		System.out.println(server1Obj);
				        	} else {
				        		server2Obj = obj.arrayValue;
				        		System.out.println(server2Obj);
				        	}*/
				        }
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

	public void calculateMinimumPowerProfile() throws NumberFormatException, UnknownHostException, IOException, ClassNotFoundException {
		
		int arrayIndex = 0;
		int totalIter = server.totalIteration;
		for(int j=0;j<totalIter;j++) {
			
			if(j%Integer.parseInt(server.details.get(serverName).get(4)) == 0 && j!=0) {
				arrayIndex++;
			}
			
			double[] value = new double[24];
			value = server.aggregatePowerProfile[arrayIndex%server.aggregatePowerProfile.length];
			
			
			Set<String> name = server.details.keySet();
			for(String n : name) {
				server.details.get(n).set(2, "false");
			}
					
			try {
				setUpConnection(serverName+":request:"+j);
			} catch (Exception e) {
				System.out.println("failed to create connection");
			}
			
			/*
			server.minimumAggregatePowerValue = new double[24];
			
			for(int i=0;i<24;i++) {
				server.minimumAggregatePowerValue[i] = value[i] + server1Obj[i] + server2Obj[i];
			}
			System.out.println(Arrays.toString(server.minimumAggregatePowerValue));
			
			server.calculatePAR();
			*/
			
		}
		
	}
	

	public void checkStatus() {
	
		
		Set<String> name = server.details.keySet();
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
					
					 if(server.details.get(nameArray[i]).get(2).equals("false") || server.details.get(nameArray[i]).get(4).equals("0")) {	
						 
						 try{
					         clientSocket = new Socket(server.details.get(nameArray[i]).get(0), Integer.parseInt(server.details.get(nameArray[i]).get(1)));
					         server.details.get(nameArray[i]).set(2, "true");
	 
						        DataOutputStream outToServer = new DataOutputStream(
							                clientSocket.getOutputStream());
							        
							     BufferedReader inFromServer = 
							                new BufferedReader(new InputStreamReader(
							                    clientSocket.getInputStream()));
							        
						        outToServer.writeBytes("speed\n");
						        String input = inFromServer.readLine();
						              
					        	String[] result = input.split(":");
						        server.details.get(result[0]).set(4, result[1]);     
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
					 if(server.details.get(nameArray[1]).get(2).equals("true") && server.details.get(nameArray[0]).get(2).equals("true") &&
							 !server.details.get(nameArray[1]).get(4).equals("0") && !server.details.get(nameArray[0]).get(4).equals("0")) {
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
        	int selfCount = server.calculateIterationRound();
			server.totalIteration*=selfCount;
			server.details.get(serverName).set(3, Integer.toString(selfCount));
		     
		    // calcuate aggregate power profile for different appliance configuation for each server. 
			server.calculateAggregatePowerProfile(selfCount);
			
			setUpConnection("iteration");
							
		    System.out.println("TOTAL ITER"+server.totalIteration);   
			
			//calculate individual speed.
			server.calcluateServerSpeed(serverName);
	       
			
			Set<String> name = server.details.keySet();
			for(String n : name) {
				server.details.get(n).set(2, "false");
			}
			
			checkStatus();

	        for (Map.Entry entry : server.details.entrySet()) {
        	    System.out.println(entry.getKey() + ", " + entry.getValue());
        	}
	        	
	        //calculate total power profile of the system.
	         calculateMinimumPowerProfile();
	        
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }


    
}

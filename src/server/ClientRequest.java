package server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.primitives.Doubles;

import server.Server;

public class ClientRequest implements Runnable{

	public Server server;
	public String serverName;
	public List<Double> server1Obj; 
	public List<Double> server2Obj;
	//public Map<String,Socket> clientSocketMap;
	//public Map<String,DataOutputStream> outServer;
	//public Map<String,BufferedReader> inServer;

	
	public ClientRequest(String name, Server serverObj) {
			server = serverObj;
			serverName = name;
			server1Obj = new ArrayList<Double>();
			server2Obj = new ArrayList<Double>();
			//clientSocketMap = new HashMap<String, Socket>();
			//outServer = new HashMap<String, DataOutputStream>();
			//inServer = new HashMap<String, BufferedReader>();
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
	   
	   
		int i = 0;
		
		while(true) {
				
				 if(server.details.get(nameArray[i]).get(2).equals("false")) {	
					 Socket clientSocket = null; 
					 try{
						 
						 /*
						 clientSocket = clientSocketMap.get(nameArray[i]);
						 DataOutputStream outToServer = outServer.get(nameArray[i]);
						 BufferedReader inFromServer = inServer.get(nameArray[i]);
						 
						 System.out.println("clientSocket" + clientSocket);
						 
						 if(clientSocket == null) {
							 clientSocket = new Socket(server.details.get(nameArray[i]).get(0), Integer.parseInt(server.details.get(nameArray[i]).get(1)));
							 clientSocketMap.put(nameArray[i], clientSocket);
							 DataOutputStream a = new DataOutputStream(
						                clientSocket.getOutputStream());
						        
							 BufferedReader b = 
						                new BufferedReader(new InputStreamReader(
						                    clientSocket.getInputStream()));
							 
							 inServer.put(nameArray[i], b);
							 outServer.put(nameArray[i], a);
							 outToServer = a;
							 inFromServer = b;

						 } 
						 */
				         clientSocket = new Socket(server.details.get(nameArray[i]).get(0), Integer.parseInt(server.details.get(nameArray[i]).get(1)));
				         server.details.get(nameArray[i]).set(2, "true");
   
				         DataOutputStream outToServer = new DataOutputStream(
					                clientSocket.getOutputStream());
					        
						 BufferedReader inFromServer = 
					                new BufferedReader(new InputStreamReader(
					                    clientSocket.getInputStream()));
						 
				        if(request.equals("iteration")) {
					      
						        
					        outToServer.writeBytes(request+"\n");
					        String input = inFromServer.readLine();
					              
				        	String[] result = input.split(":");
					        server.details.get(result[0]).set(3, result[1]);   
					        server.totalIteration *= Integer.parseInt(result[1]);
					        clientSocket.close();
				        }
				        
				        else if(request.contains("request")){

				        	outToServer.writeBytes(request+"\n");
				        	String input = inFromServer.readLine();
				        	
				        	
				        	String modifiedInput = input.substring(1, input.length()-1);
				        	String[] result = modifiedInput.split(",");
				        	double[] resultArray = new double[result.length];
				        	
				        	for(int k=0;k<result.length;k++) {
				        		resultArray[k] = Double.parseDouble(result[k]);
				        	}
				        	

				        	if(server1Obj.isEmpty()) {
				        		for(double d: resultArray) {
				        			server1Obj.add(d); 
				        		}	
				        	} else {
				        		for(double d: resultArray) {
				        			server2Obj.add(d); 
				        		}
				        	}
				        	
				        	clientSocket.close();
				        }
					}
					 catch(IOException e) {
						 i++;
						 if(i==2) {
							 i=0;
						 }
						 System.out.println(e.getMessage());
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
			int minIndex = arrayIndex%server.aggregatePowerProfile.length;
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
			
			
			server.minimumAggregatePowerValue = new double[24];
			
			double[] serOneTemp = Doubles.toArray(server1Obj);
			double[] serTwoTemp = Doubles.toArray(server2Obj);
			
			for(int i=0;i<24;i++) {
				
				server.minimumAggregatePowerValue[i] = value[i] + serOneTemp[i] + serTwoTemp[i];
			}
			
			//System.out.println(Arrays.toString(server.minimumAggregatePowerValue));
			
			server1Obj.clear();
			server2Obj.clear();
			
			//System.out.println("array length"+server1Obj.size());
			//System.out.println("iteration"+j+Arrays.toString(server.minimumAggregatePowerValue));
			
			server.calculatePAR(minIndex);
			System.out.println(j+" "+server.PAR);
			
			server.fixConfiguration();
			System.out.println(Arrays.toString(server.appliancePowerProfile[9]));
			System.out.println(Arrays.toString(server.appliancePowerProfile[10]));
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
			
			System.out.println(serverName+Arrays.toString(server.aggregatePowerProfile[0]));
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

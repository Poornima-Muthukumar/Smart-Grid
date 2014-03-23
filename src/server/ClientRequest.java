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
	public String operation;
	public Map<String,Socket> clientSocketMap;
	public Map<String,DataOutputStream> outServer;
	public Map<String,BufferedReader> inServer;
	public int length = 0;
	
	public ClientRequest(String name, Server serverObj, String op) {
			server = serverObj;
			serverName = name;
			server1Obj = new ArrayList<Double>();
			server2Obj = new ArrayList<Double>();
			operation = op;
			clientSocketMap = new HashMap<String, Socket>();
			outServer = new HashMap<String, DataOutputStream>();
			inServer = new HashMap<String, BufferedReader>();
   }

	// The client will either send a request to get iteration from the other two servers or a request to get aggregatePowerProfile from the other servers.
    public void setUpConnection(String request)  {
            
            
        Set<String> name = server.details.keySet();
        String[] nameArray = new String[name.size()-1];
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
                            
                        	//Logic to save socket in order to reuse the same socket instead of creating one everytime.
                            clientSocket = clientSocketMap.get(nameArray[i]);
                            DataOutputStream outToServer = outServer.get(nameArray[i]);
                            BufferedReader inFromServer = inServer.get(nameArray[i]);
                            
                            if(clientSocket == null) {
                                
                                //error connecting
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

                            // clientSocket = new Socket(server.details.get(nameArray[i]).get(0), Integer.parseInt(server.details.get(nameArray[i]).get(1)));
                            server.details.get(nameArray[i]).set(2, "true");
    
                            if(request.equals("iteration")) {

                                //error case
                                try {
                                    outToServer.writeBytes(request+"\n");
                                } catch(IOException e) {
                                    System.out.println("SHUTTING DOWN");
                                    System.exit(2);
                                }
                                
                                
                                String input = null;
                                //error case
                                
                                
                                
                                try {
                                input = inFromServer.readLine();
                                } catch(IOException e) {
                                    System.out.println("SHUTTING DOWN");
                                    System.exit(2);
                                }
                                
                                
                                String[] result = input.split(":");
                                server.details.get(result[0]).set(3, result[1]);                              
                                server.totalIteration *= Integer.parseInt(result[1]);
                            
                            }
                            
                            else if(request.contains("request")){

                                //error
                                try{
                                outToServer.writeBytes(request+"\n");
                                }catch(IOException e) {
                                    System.out.println("SHUTTING DOWN");
                                    System.exit(2);
                                }
                                String input = null;
                                try{
                                //error
                                input = inFromServer.readLine();
                                } catch(IOException e) {
                                    System.out.println("SHUTTING DOWN");
                                    System.exit(2);
                                }
                                
                                String modifiedInput = input.substring(1, input.length()-1);
                                String[] result = modifiedInput.split(",");				        	

                                if(server1Obj.isEmpty()) {
                                    for(String d: result) {
                                        server1Obj.add(Double.parseDouble(d)); 
                                    }	
                                } else {
                                    for(String d: result) {
                                        server2Obj.add(Double.parseDouble(d)); 
                                    }
                                }	
                            }
                        }
                        catch(IOException e) {
                            i++;
                            if(i==length) {
                                i=0;
                            }
                            continue;
                        }
                    }
                    i++;
                    
                    int state = 1;
                    for(int p=0;p<nameArray.length;p++) {
                        if(server.details.get(nameArray[p]).get(2).equals("true")) {
                            state*=1;
                        } else {
                            state*=0;
                        }
                        
                    }
                    if(state==1) {
                        break;
                    }
                    else {
                        if(i==length) {
                            i = 0;
                        }
                    }
            }
    }
    
    //Go through all possible iterations and request aggregate power profile from other servers and calculate PAR/VARIANCE.
    
	public void calculateMinimumPowerProfile()  {
		
		int arrayIndex = 0;
		int totalIter = server.totalIteration;
		server.minimumAggregatePowerValue = new double[24];
		
		for(int j=0;j<totalIter;j++) {
			
			if(j%Integer.parseInt(server.details.get(serverName).get(4)) == 0 && j!=0) {
				arrayIndex++;
			}
			
			int minIndex = arrayIndex%server.aggregatePowerProfile.length;

			
			Set<String> name = server.details.keySet();
			for(String n : name) {
				server.details.get(n).set(2, "false");
			}
							
			//code to set up connection with the other clients to request their aggreagte power profile.
			setUpConnection(serverName+":request:"+j);
			
			//calculate total aggreagte power profile for the system at the end of each iteration.
			for(int i=0;i<24;i++) {
				server.minimumAggregatePowerValue[i] = server.aggregatePowerProfile[minIndex][i] + server1Obj.get(i) + server2Obj.get(i);
			}
			
			//server1Obj and server2Obj arraylist are used to store the aggregate power profile received from the other two servers after every iteration.
			server1Obj.clear();
			server2Obj.clear();
			
			//based on the operation required either compute par or compute variance.
			if(operation.equals("par")) {
				server.calculatePAR(minIndex);
				//System.out.println(j + " " + server.PAR);
			} else if(operation.equals("variance")) {
				server.calculateVariance(minIndex);
				System.out.println(j + " " + server.variance);
			}
		}
		
	}
	

	public void checkStatus() {
	
		
		Set<String> name = server.details.keySet();
		   String[] nameArray = new String[name.size()-1];
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
							 
							 clientSocket = clientSocketMap.get(nameArray[i]);
							 DataOutputStream outToServer = outServer.get(nameArray[i]);
							 BufferedReader inFromServer = inServer.get(nameArray[i]);
							 						 
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
							 
					         server.details.get(nameArray[i]).set(2, "true");
					         	
					         String input  = null;
					         	try {
						        outToServer.writeBytes("speed\n");
						         input = inFromServer.readLine();
					         	} catch(IOException e) {
					         		System.out.println("SHUTTING DOWN");
					       			System.exit(2);
					         	}
						        
					        	String[] result = input.split(":");
						        server.details.get(result[0]).set(4, result[1]);     
						        
					       }		     
						 catch(IOException e) {
							 
							 i++;
							 if(i==length) {
								 i=0;
							 }
							 System.out.println("OH NOES");
							 continue;
						 }
					 }
					 i++;
					 
					 int state = 1;
					 for(int p=0;p<nameArray.length;p++) {
						 
						 
						 if(server.details.get(nameArray[p]).get(2) == "true" && !server.details.get(nameArray[p]).get(4).equals("0")) {
							 state*=1;
						 } else {
							 state*=0;
						 }
						 
					 }
					 if(state==1) {
						 break;
					 }
					 else {
						 if(i==length) {
							 i = 0;
						 }
					 }			
			}
		
	}
	
	public void run() {
			System.out.println("Hello from client thread!");  
        	
			length = server.details.size()-1;
        	//Step1 - request iteration from other servers.
        	int selfCount = server.calculateIterationRound();
        	
			server.totalIteration*=selfCount;
			server.details.get(serverName).set(3, Integer.toString(selfCount));
		     
		    // calcuate aggregate power profile for different appliance configuation for each server. 
			server.calculateAggregatePowerProfile(selfCount);
        	
			
			setUpConnection("iteration");
						
			
			//calculate individual speed.
			server.calcluateServerSpeed(serverName);
	       
			
			Set<String> name = server.details.keySet();
			for(String n : name) {
				server.details.get(n).set(2, "false");
			}
			checkStatus();	
	        //calculate total power profile of the system.
	        calculateMinimumPowerProfile();
	        
	        server.fixConfiguration(operation);
	        if(operation.equals("par")) {
	        System.out.println("FINAL PAR "+server.PAR);
	        System.out.println("APPLIANCE 10 CONFIGURATION" + Arrays.toString(server.appliancePowerProfile[9]));
	        System.out.println("APPLIANCE 11 CONFIGURATION" + Arrays.toString(server.appliancePowerProfile[10]));
	        }
	        else if(operation.equals("variance")) {
	        System.out.println("FINAL VAR "+server.variance);
	        System.out.println("APPLIANCE 10 CONFIGURATION" + Arrays.toString(server.appliancePowerProfile[9]));
	        System.out.println("APPLIANCE 11 CONFIGURATION" + Arrays.toString(server.appliancePowerProfile[10]));
	        }
	}
}

package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Server {

	public static int appliancePowerProfile[][];
	public static double appliancePowerConsumption[];
	public int timeConstraints[][];
	public double[][] aggregatePowerProfile;
	public static int startOfNinth = 0;
	public static int endOfNinth = 0;
	public static int startOfTenth = 0;
	public static int endOfTenth = 0;
	public int totalIteration = 1;
	public Map<String,ArrayList<String>> details;
	

	// final decision.
	public double[] minimumAggregatePowerValue;
	public double[][] mimimumAggregatePowerProfile;
	public double PAR;
	
	
	public Server() {
		 appliancePowerProfile  = new int[11][];
		 
		 for(int i=0;i<11;i++) {
			 appliancePowerProfile[i] = new int[]
					 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		 }
	
		 appliancePowerConsumption = new double[11];
	
		 details = new HashMap<String, ArrayList<String>>();
		 
		 ArrayList<String> value1 = new ArrayList<String>(
				    Arrays.asList("172.23.189.244","6789","false","0","0"));
		 ArrayList<String> value2 = new ArrayList<String>(
				    Arrays.asList("172.23.189.244","8888","false","0","0"));
		 ArrayList<String> value3 = new ArrayList<String>(
				    Arrays.asList("172.23.189.244","9999","false","0","0"));
		 
		 details.put("server1", value1);
		 details.put("server2", value2);
		 details.put("server3", value3);

		 
		 //startOfNinth = 7;
		 //endOfNinth = 18;
		 //startOfTenth = 9;
		 //endOfTenth = 17;
		 
		 startOfNinth = 0;
		 endOfNinth = 0;
		 startOfTenth = 0;
		 endOfTenth = 0;
		 
	}
	
	
	public void calculateAggregatePowerProfile(int iterationCount) {
			
		aggregatePowerProfile = new double[iterationCount][];
	
		for(int i=0;i<iterationCount;i++) {
			aggregatePowerProfile[i] = new double[24];
		}
		
		int sumOfNinth = 0;
		int sumOfTenth = 0;
		 
		for(int i=0;i<24;i++) {
				sumOfNinth+=appliancePowerProfile[9][i];
				sumOfTenth+=appliancePowerProfile[10][i];
			}
		
		
		int iterationForNinth = (endOfNinth - startOfNinth) - sumOfNinth +2; 
		int iterationForTenth = (endOfTenth - startOfTenth) - sumOfTenth +2; 
		
		int startIndexNinth = startOfNinth;
		
		int ninthPowerProfile[][] = new int[iterationForNinth][24];
		
		
		for(int i=0;i<iterationForNinth;i++) {
			ninthPowerProfile[i] = new int[24];
			for(int j=0;j<24;j++) {
				
				
				if(j>=startIndexNinth && j<startIndexNinth+sumOfNinth) {
					ninthPowerProfile[i][j] = 1;
					
				} else {
					ninthPowerProfile[i][j] = 0;
					
				}	
			}
			
			System.out.println(Arrays.toString(ninthPowerProfile[i]));
			startIndexNinth++;
		}
		
		
		
		int tenthPowerProfile[][] = new int[iterationForTenth][24];
		
		
		int startIndexTenth = startOfTenth;
		for(int i=0;i<iterationForTenth;i++) {
			
			tenthPowerProfile[i] = new int[24];
			for(int j=0;j<24;j++) {
				
				
				if(j>=startIndexTenth && j<startIndexTenth+sumOfTenth) {
					tenthPowerProfile[i][j] = 1;
					
				} else {
					tenthPowerProfile[i][j] = 0;
					
				}
				
			}
			
			System.out.println(Arrays.toString(tenthPowerProfile[i]));
			startIndexTenth++;
		}
		
		
		
		int speedOfNinth = (iterationForNinth*iterationForTenth)/iterationForNinth;
		int speedOfTenth = 1;
		

		int profileIndex9 = 0;
		int profileIndex10 = 0;
		System.out.println(iterationForNinth*iterationForTenth + " " + iterationCount);
		for(int i=0;i<iterationForNinth*iterationForTenth;i++) {
			if(i%speedOfNinth == 0 && i!=0) {
				profileIndex9++;
			} 
			if(i%speedOfTenth == 0 && i!=0) {
				profileIndex10++;
			}
			appliancePowerProfile[9] = ninthPowerProfile[profileIndex9%iterationForNinth];
			appliancePowerProfile[10] = tenthPowerProfile[profileIndex10%iterationForTenth];
					
			
			for(int k=0; k < appliancePowerProfile[0].length; k++) {
				double aggregateSum = 0;
				for(int j=0; j< appliancePowerProfile.length;j++) {
					aggregateSum += appliancePowerProfile[j][k] * appliancePowerConsumption[j];
				}
				aggregatePowerProfile[i][k] = aggregateSum;
			}
			
			System.out.println(Arrays.toString(aggregatePowerProfile[i]));
			
		}
		
	
	}
	
	public int calculateIterationRound() {
		
		int sumOfNinth = 0;
		int sumOfTenth = 0;
		 
		for(int i=0;i<24;i++) {
				sumOfNinth+=appliancePowerProfile[9][i];
				sumOfTenth+=appliancePowerProfile[10][i];
			}
		
		int iteration = 1;
		
		int dif1 = (endOfNinth > startOfNinth ) ? endOfNinth - startOfNinth : startOfNinth%12 + endOfNinth;
		int dif2 = (endOfTenth > startOfTenth ) ? endOfTenth - startOfTenth : startOfTenth%12 + endOfTenth;
		iteration *= (dif1 - sumOfNinth) + 2;
		iteration *= (dif2 - sumOfTenth) + 2;
		
		return iteration;
	}
	
	public static void loadFromFile(String fileName) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		    try {
		        String line = br.readLine();
		        System.out.println(line);
                int lineCount = 0;
		        while (line != null) {
		        	String[] result = line.split(",");

		        	if(lineCount == 11) {
		        		
		        		for(int j =0; j<result.length; j++) {
		        			appliancePowerConsumption[j] = Double.parseDouble(result[j]);
			        	}
			        	
		        	} else {
			        	for(int j=0; j<result.length; j++) {
			        		appliancePowerProfile[lineCount][j] = Integer.parseInt(result[j]);
			        	}
		        	}
		        	line = br.readLine();
		        	lineCount++;
		        }
		    } finally {
		        br.close();
		    }
	}
	
	public static void main(String[] args) {
		 //.out.println("Server 1");
		 
		Server serverObj =  new Server();
		
		String serverName = args[0];
		
		if(serverName.equals("server1")) {
			try {
				Server.loadFromFile("serverOneDetails");
			} catch (IOException e) {
				System.out.println("Error reading file");
			} 	
		 } else if(serverName.equals("server2")) {
			 try {
				 Server.loadFromFile("serverTwoDetails");
			} catch (IOException e) {
				System.out.println("Error reading file");
			}
		 } else if(serverName.equals("server3")) {
			 try {
				 Server.loadFromFile("serverThreeDetails");
			} catch (IOException e) {
				System.out.println("Error reading file");
			}
		 }
		
		
		 startOfNinth = Integer.parseInt(args[1]);
		 endOfNinth = Integer.parseInt(args[2]);
		 startOfTenth = Integer.parseInt(args[3]);
		 endOfTenth = Integer.parseInt(args[4]);
		 
		 Thread a = new Thread(new ServerRequest(args[0],serverObj));
		 a.start();
		 Thread b = new Thread(new ClientRequest(args[0],serverObj));
		 b.start();
		 
	}


	public void calcluateServerSpeed(String serverName) {
		if(serverName == "server1") {
			details.get(serverName).set(4,Integer.toString(totalIteration/Integer.parseInt(details.get(serverName).get(3))));
		}
		else if(serverName == "server2") {
			int denom = Integer.parseInt(details.get(serverName).get(3)) * Integer.parseInt(details.get("server1").get(3));
			details.get(serverName).set(4,Integer.toString(totalIteration/denom));
		}
		else if(serverName == "server3") {
			details.get(serverName).set(4,"1");
		}
	}


	public void calculatePAR() {
		
		double max = 0;
		double sum = 0;
		for(int i=0;i<24;i++) {
		
			if(minimumAggregatePowerValue[i] > max) {
				max = minimumAggregatePowerValue[i];
			}
			sum+=minimumAggregatePowerValue[i];
		}
		double average = sum/24;
		double currentPAR = max/average;
		if (currentPAR < PAR) {
			PAR = currentPAR;
			
		}
	}
}

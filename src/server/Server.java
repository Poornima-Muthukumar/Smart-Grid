package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.primitives.Ints;


public class Server {
	
	public  int appliancePowerProfile[][];
	public  double appliancePowerConsumption[];
	public  int timeConstraints[][];
	public  double[][] aggregatePowerProfile;
	public  int startOfNinth = 0;
	public  int endOfNinth = 0;
	public  int startOfTenth = 0;
	public  int endOfTenth = 0;
	public  int totalIteration = 1;
	public int INDEX;
	public int VARIANCE_INDEX;
	public double variance = Double.MAX_VALUE;
	public Map<String,ArrayList<String>> details;
	

	// final decision.
	public  double[] minimumAggregatePowerValue;
	public  double[][] mimimumAggregatePowerProfile;
	public  double PAR = Double.MAX_VALUE;
	
	
	public Map<Integer,ArrayList<Integer>> ninthConfig;
	public Map<Integer,ArrayList<Integer>> tenthConfig;
	 
	/* Server constructor to initialize the values.
	 * 
	 */
	public Server() {
	 	 ninthConfig = new HashMap<Integer,ArrayList<Integer>>();
	 	 tenthConfig = new HashMap<Integer,ArrayList<Integer>>();
	 	 
		 appliancePowerProfile  = new int[11][];
		 
		 for(int i=0;i<11;i++) {
			 appliancePowerProfile[i] = new int[]
					 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		 }
	
		 appliancePowerConsumption = new double[11];
	
		 details = new HashMap<String, ArrayList<String>>();
		 
		 ArrayList<String> value1 = new ArrayList<String>(
				    Arrays.asList("172.23.191.116","6789","false","0","0","0"));
		 ArrayList<String> value2 = new ArrayList<String>(
				    Arrays.asList("172.23.191.116","8888","false","0","0","0"));
		 ArrayList<String> value3 = new ArrayList<String>(
				    Arrays.asList("172.23.191.116","9999","false","0","0","0"));
		 
		 details.put("server1", value1);
		 details.put("server2", value2);
		 details.put("server3", value3);

		 startOfNinth = 0;
		 endOfNinth = 0;
		 startOfTenth = 0;
		 endOfTenth = 0;
		 
	}
	/* This method calculates all possible ways in which a server can arrange its appliances and calculates the aggregate power profile.
	 * Lets say appliance_9 can be arranged in 10 ways and appliance_10 can be arranged in 5 ways so the total iteration for that server is 10*5 = 50 ways.
	 * These 50 possible total aggregate power profile are calculated at the start of the system and based on the speed of the server the system will pick a particular index in the array and return that value to the clients. 
	 */
	
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
		

		int dif1 = (endOfNinth > startOfNinth ) ? endOfNinth - startOfNinth : (endOfNinth+24) - startOfNinth;
		int dif2 = (endOfTenth > startOfTenth ) ? endOfTenth - startOfTenth : (endOfTenth+24) - startOfTenth;
		int iterationForNinth = (dif1 - sumOfNinth) + 1;
		int iterationForTenth = (dif2 - sumOfTenth) + 1;
		
		
		int startIndexNinth = startOfNinth;
		
		int ninthPowerProfile[][] = new int[iterationForNinth][24];
		
		
		for(int i=0;i<iterationForNinth;i++) {
			
			ninthPowerProfile[i] = new int[24];
			int start = startIndexNinth;
			int end = startIndexNinth+sumOfNinth;
			for(int j = start;j < end; j++) {
					ninthPowerProfile[i][j%24] = 1;					
			}
			startIndexNinth++;
		}
		
		
		
		int tenthPowerProfile[][] = new int[iterationForTenth][24];
		int startIndexTenth = startOfTenth;
		
		for(int i=0;i<iterationForTenth;i++) {
			
			tenthPowerProfile[i] = new int[24];
			int start = startIndexTenth;
			int end = startIndexTenth+sumOfTenth;
			for(int j = start;j < end; j++) {
					tenthPowerProfile[i][j%24] = 1;					
			}
			startIndexTenth++;
		}

		int speedOfNinth = (iterationForNinth*iterationForTenth)/iterationForNinth;
		int speedOfTenth = 1;
		

		int profileIndex9 = 0;
		int profileIndex10 = 0;
		
		for(int i=0;i<iterationForNinth*iterationForTenth;i++) {
			
			if(i%speedOfNinth == 0 && i!=0) {
				profileIndex9++;
			} 
			if(i%speedOfTenth == 0 && i!=0) {
				profileIndex10++;
			}
			appliancePowerProfile[9] = ninthPowerProfile[profileIndex9%iterationForNinth];
			appliancePowerProfile[10] = tenthPowerProfile[profileIndex10%iterationForTenth];
					
			ArrayList<Integer> nine = new ArrayList<Integer>();
			ArrayList<Integer> ten = new ArrayList<Integer>();
			
			for(int a: appliancePowerProfile[9]) {
				nine.add(a);
			}
			
			for(int b: appliancePowerProfile[10]) {
				ten.add(b);
			}
			
			
			ninthConfig.put(i, nine);
			tenthConfig.put(i, ten);
			
			
			for(int k=0; k < appliancePowerProfile[0].length; k++) {
				double aggregateSum = 0;
				for(int j=0; j< appliancePowerProfile.length;j++) {
					aggregateSum += appliancePowerProfile[j][k] * appliancePowerConsumption[j];
				}
				aggregatePowerProfile[i][k] = aggregateSum;
			}
			
			
			
		}
		
	
	}
	
	/*Logic to calculate the iteration round for each server 
	based on the different possible arrangements of the appliance 9 and 10 within each server*/
	public int calculateIterationRound() {
		
		int sumOfNinth = 0;
		int sumOfTenth = 0;
		 
		for(int i=0;i<24;i++) {
				sumOfNinth+=appliancePowerProfile[9][i];
				sumOfTenth+=appliancePowerProfile[10][i];
			}
		
		int iteration = 1;
		
		int dif1 = (endOfNinth > startOfNinth ) ? endOfNinth - startOfNinth : (endOfNinth+24) - startOfNinth;
		int dif2 = (endOfTenth > startOfTenth ) ? endOfTenth - startOfTenth : (endOfTenth+24) - startOfTenth;
		iteration *= (dif1 - sumOfNinth) + 1;
		iteration *= (dif2 - sumOfTenth) + 1;
		
		return iteration;
	}
	
	
	// logic to read the power profile of the 11 appliances (9 fixed + 2 flexible) from the file and load them into the 2D array - appliancePowerProfile[11][24].
	// logic to load the power consumption of the 11 appliaces into the 1D array - appliancePowerConsumption[11].
	public void loadFromFile(String fileName) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		    try {
		        String line = br.readLine();
		      
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
	
	/* This is the main function which starts off first.
	 
	 It reads the input arguments and assigns them accordingly.
	 
	 It loads the power profile of the appliances from the file based on the serverName.
	 eg. from serverOneDetails for server1, from serverTwoDetails for server2, from serverThreeDetails for server3.
	 
	 It sets the start and end time for the two flexible appliances(9 and 10).
	  
	 It also creates the Client Thread (serverRequest) which will request the power profile from the other servers.
	 
	 It also creates the Server Thread (clientRequest) which responds to the requests sent by the other clients.
	  */
	public static void main(String[] args) {
		 //.out.println("Server 1");
		 
		Server serverObj =  new Server();
		
		String serverName = args[0];
		
		if(serverName.equals("server1")) {
			try {
				serverObj.loadFromFile("serverOneDetails");
			} catch (IOException e) {
				System.out.println("Error reading file");
			} 	
		 } else if(serverName.equals("server2")) {
			 try {
				 serverObj.loadFromFile("serverTwoDetails");
			} catch (IOException e) {
				System.out.println("Error reading file");
			}
		 } else if(serverName.equals("server3")) {
			 try {
				 serverObj.loadFromFile("serverThreeDetails");
			} catch (IOException e) {
				System.out.println("Error reading file");
			}
		 }
		
		
		serverObj.startOfNinth = Integer.parseInt(args[1]);
		serverObj.endOfNinth = Integer.parseInt(args[2]);
		serverObj.startOfTenth = Integer.parseInt(args[3]);
		serverObj.endOfTenth = Integer.parseInt(args[4]);
		
		 serverObj.details.get("server1").set(0, args[6]);
		 serverObj.details.get("server1").set(1, args[7]);
		 
		 serverObj.details.get("server2").set(0, args[8]);
		 serverObj.details.get("server2").set(1, args[9]);
		 
		 serverObj.details.get("server3").set(0, args[10]);
		 serverObj.details.get("server3").set(1, args[11]);
		 
		 Thread a = new Thread(new ServerRequest(args[0],serverObj));
		 a.start();
		 
		 Thread b = new Thread(new ClientRequest(args[0],serverObj,args[5]));
		 b.start();
		 
		
		 
	}


	//Each client/server will calculate its own speed at which it should go through the total possible iteration logic - similar to the truth table logic explained in the report.
	//The client/server will calculate the speed at the beginning of the iteration round.
	public void calcluateServerSpeed(String serverName) {

			if(serverName.equals("server1")) {
				details.get(serverName).set(4,Integer.toString(totalIteration/Integer.parseInt(details.get(serverName).get(3))));
			}
			else if(serverName.equals("server2")) {

				int denom = Integer.parseInt(details.get(serverName).get(3)) * Integer.parseInt(details.get("server1").get(3));
				details.get(serverName).set(4,Integer.toString(totalIteration/denom));
			}
			else if(serverName.equals("server3")) {
				details.get(serverName).set(4,"1");
			}	
	}
	
	//this method calculates the PAR for each iteration round and 
	// saves the index of the array aggregatePowerProfile if its corresponding PAR was the minimum value that was reached up until that iteration. 
	public void calculatePAR(int minIndex) {
		
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
			INDEX = minIndex;
		}
	}
	
	//this method calculates the variance for each iteration round and 
	// saves the index of the array aggregatePowerProfile if its corresponding Variance was the minimum value that was reached up until that iteration. 
	public void calculateVariance(int minIndex) {
		
		double sum = 0;
		for(int i=0;i<24;i++) {
			sum+=minimumAggregatePowerValue[i];
		}

		double average = sum/24;
		
		double[] val = new double[24];
		
		double sum1=0;
		for(int i=0;i<24;i++) {
			val[i]=Math.pow((minimumAggregatePowerValue[i]-average),2);
			sum1+=val[i];
		}
		
		
		double var = sum1/24;
		if(var < variance) {
			variance = var;
			VARIANCE_INDEX = minIndex;
		}
	}

	
	// Once all the iterations are done, we fix the configuration for appliance 9 and appliance 10 which are the flexible appliances
    //based on the indexes we saved (VARIANCE_INDEX, INDEX) for the iteration which gave us the best configuration.
	public void fixConfiguration(String operation) {
		if(operation.equals("variance")) {
			appliancePowerProfile[9] = Ints.toArray(ninthConfig.get(VARIANCE_INDEX));
			appliancePowerProfile[10] = Ints.toArray(tenthConfig.get(VARIANCE_INDEX));
		} else if(operation.equals("par")) {
			appliancePowerProfile[9] = Ints.toArray(ninthConfig.get(INDEX));
			appliancePowerProfile[10] = Ints.toArray(tenthConfig.get(INDEX));
		}
	}
}

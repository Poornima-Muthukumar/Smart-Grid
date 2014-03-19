package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Server {

	public static int appliancePowerProfile[][];
	public static double appliancePowerConsumption[];
	public int timeConstraints[][];
	public double aggregatePowerProfile[];
	public static int startOfNinth = 0;
	public static int endOfNinth = 0;
	public static int startOfTenth = 0;
	public static int endOfTenth = 0;
	public int totalIteration = 1;
	public Map<String,ArrayList<String>> details;
	
	public Server() {
		 appliancePowerProfile  = new int[][] {	}; 
	
		 appliancePowerConsumption = new double[] {};
	
		 details = new HashMap<String, ArrayList<String>>();
		 
		 ArrayList<String> value1 = new ArrayList<String>(
				    Arrays.asList("172.20.10.2","6789","false","0"));
		 ArrayList<String> value2 = new ArrayList<String>(
				    Arrays.asList("172.20.10.2","8888","false","0"));
		 ArrayList<String> value3 = new ArrayList<String>(
				    Arrays.asList("172.20.10.2","9999","false","0"));
		 
		 details.put("server1", value1);
		 details.put("server2", value2);
		 details.put("server3", value3);
		 
		 aggregatePowerProfile = new double[] 
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		 
		 //startOfNinth = 7;
		 //endOfNinth = 18;
		 //startOfTenth = 9;
		 //endOfTenth = 17;
		 
		 startOfNinth = 0;
		 endOfNinth = 0;
		 startOfTenth = 0;
		 endOfTenth = 0;
		 
	}
	
	
	public void calculateAggregatePowerProfile() {
		
		for(int i=0; i < appliancePowerProfile[0].length; i++) {
			double aggregateSum = 0;
			for(int j=0; j< appliancePowerProfile.length;j++) {
				aggregateSum += appliancePowerProfile[j][i] * appliancePowerConsumption[j];
			}
			aggregatePowerProfile[i] = aggregateSum;
		}
	}
	
	public int calculateIterationRound() {
		
		int sumOfNinth = 0;
		int sumOfTenth = 0;
		
		for(int i=0;i<24;i++) {
			sumOfNinth+=appliancePowerProfile[9][i];
			sumOfTenth+=appliancePowerProfile[10][i];
		}
		
		int iteration = 0;
		
		int dif1 = (endOfNinth > startOfNinth ) ? endOfNinth - startOfNinth : startOfNinth%12 + endOfNinth;
		int dif2 = (endOfTenth > startOfTenth ) ? endOfTenth - startOfTenth : startOfTenth%12 + endOfTenth;
		iteration += (dif1 - sumOfNinth) + 2;
		iteration += (dif2 - sumOfTenth) + 2;
		
		return iteration;
	}
	
	public static void loadFromFile(String fileName) throws IOException {
		
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
			        	for(int j =0; j<result.length; j++) {
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
		 
		 if(args[0] == "server1") {
			try {
				loadFromFile("serverOneDetails");
			} catch (IOException e) {
				System.out.println("Error reading file");
			} 	
		 } else if(args[0] == "server2") {
			 try {
				loadFromFile("serverTwoDetails");
			} catch (IOException e) {
				System.out.println("Error reading file");
			}
		 } else if(args[0] == "server3") {
			 try {
				loadFromFile("serverThreeDetails");
			} catch (IOException e) {
				System.out.println("Error reading file");
			}
		 }
		 
		 startOfNinth = Integer.parseInt(args[1]);
		 endOfNinth = Integer.parseInt(args[2]);
		 startOfTenth = Integer.parseInt(args[3]);
		 endOfTenth = Integer.parseInt(args[4]);
		 
		 Thread a = new Thread(new ServerRequest(args[0]));
		 a.start();
		 Thread b = new Thread(new ClientRequest(args[0]));
		 b.start();
		 
	}
	
}

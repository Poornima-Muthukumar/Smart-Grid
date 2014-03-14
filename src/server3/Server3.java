package server3;

import java.util.ArrayList;

public class Server3 {

	public static int PORT = 9999;
	public static String IP = "172.20.10.2";
	public static String serverName = "server3";
	public int appliancePowerProfile[][];
	public double appliancePowerConsumption[];
	public String serverDetails[][];
	public int timeConstraints[][];
	public double aggregatePowerProfile[];
	public int startOfNinth = 0;
	public int endOfNinth = 0;
	public int startOfTenth = 0;
	public int endOfTenth = 0;
	public int totalIteration = 1;
	
	public Server3() {
		 appliancePowerProfile  = new int[][] {
				{1 ,1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1 ,1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1},
				{1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
				{0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
				{0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1},
				{1 ,1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1 ,1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0 ,0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},		
				
		}; 
	
		 appliancePowerConsumption = new double[] { 0.07, 0.05, 0.1, 0.15, 1.6, 1.5, 2.5, 0.3, 0.04, 2, 1.8 };
	
		 serverDetails = new String[][] {
			 {"server1","172.20.10.2","6789","false"},
			 {"server2","172.20.10.2","8888","false"},
			 
		 };
		 aggregatePowerProfile = new double[] 
			 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		 
		 startOfNinth = 7;
		 endOfNinth = 18;
		 startOfTenth = 9;
		 endOfTenth = 17;
		 
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
		iteration += ((endOfNinth - startOfNinth) - sumOfNinth) + 2;
		iteration += ((endOfTenth - startOfTenth) - sumOfTenth) + 2;
		
		return iteration;
	}
	
	public static void main(String[] args) {
		 System.out.println("Server 3");
		 
		 Thread a = new Thread(new ServerRequest());
		 a.start();
		 Thread b = new Thread(new ClientRequest());
		 b.start();
		 
	}
	
}

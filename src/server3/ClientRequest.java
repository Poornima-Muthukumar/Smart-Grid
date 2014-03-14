package server3;
import java.io.*;
import java.net.*;


public class ClientRequest implements Runnable{

	
	public Server3 server;
	
	public ClientRequest() {
			server = new Server3();

	}

	
public void requestIteration() throws NumberFormatException, UnknownHostException, IOException {
		
		int i = 0;
		while(i < 2) {
			Socket clientSocket = null; 	
			try {	
			 if(server.serverDetails[i][3] == "false") {	
	         clientSocket = new Socket(server.serverDetails[i][1], Integer.parseInt(server.serverDetails[i][2]));
	         server.serverDetails[i][3] = "true";
	         i++;
			 } else {
				 i++;
				 continue;
			 }
			} catch (IOException e) {
			  if(i==1) {
				  i = 0;
			  }
			  System.out.println("OH NOES");
			  continue;
			}
			
	        
	        DataOutputStream outToServer = new DataOutputStream(
	                clientSocket.getOutputStream());
	        
	        BufferedReader inFromServer = 
	                new BufferedReader(new InputStreamReader(
	                    clientSocket.getInputStream()));
	        
	        outToServer.writeBytes("iteration\n");
	        String input = inFromServer.readLine();
	        System.out.println("FROM SERVER: " + input);
	        String[] result = input.split(":");
	        server.totalIteration *= Integer.parseInt(result[1]);
	        System.out.println(server.totalIteration);
	        clientSocket.close();
		}
		
	}
   
	public void run() {
        System.out.println("Hello from client thread!");  
        try {
			requestIteration();
			server.totalIteration*=server.calculateIterationRound();
	        System.out.println("TOTAL ITER"+server.totalIteration);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
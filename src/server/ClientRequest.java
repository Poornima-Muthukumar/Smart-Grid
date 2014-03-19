package server;
import java.io.*;
import java.net.*;
import java.util.Set;

import server.Server;

public class ClientRequest implements Runnable{

	public Server server;
	public String serverName;
	
	public ClientRequest(String name) {
			server = new Server();
			serverName = name;
}

	
public void requestIteration() throws NumberFormatException, UnknownHostException, IOException {
		
		
	   Set<String> name = server.details.keySet();
	   name.remove(serverName);
	   System.out.println("name array"+name);
	   String[] nameArray = (String[]) name.toArray();
	   
		int i = 0;
		Socket clientSocket = null; 
		while(true) {
				
				 if(server.details.get(nameArray[i]).get(2) == "false") {			 
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
			        server.totalIteration *= Integer.parseInt(result[1]);
			        System.out.println(server.totalIteration);
			        clientSocket.close();
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

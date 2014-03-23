package server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import server.Server;


public class ServerRequest implements Runnable {

	public Server server;
	public String serverName;
	public ArrayList<Socket> serverSocket;
	public Map<BufferedReader,DataOutputStream> reader ;

	
	public ServerRequest(String name, Server serverObj) {
			server = serverObj;
			serverName = name;
			serverSocket = new ArrayList<Socket>();
			reader = new HashMap<BufferedReader,DataOutputStream>() ;
	}

	public void acceptRequest() throws IOException {
		
		int port = Integer.parseInt(server.details.get(serverName).get(1));
	    ServerSocket welcomeSocket = new ServerSocket(); 
	    welcomeSocket.setReuseAddress(true);
	    welcomeSocket.bind(new InetSocketAddress(port));
	   
	     while(true) {
	    	  Socket connectionSocket = welcomeSocket.accept();
	    	 // System.out.println(connectionSocket);
	    	  serverSocket.add(connectionSocket);
	    	  BufferedReader inFromClient = 
	                    new BufferedReader(new InputStreamReader(
	                        connectionSocket.getInputStream()));
	    	  
	    	  //outToclient
	            DataOutputStream outToClient = 
	                    new DataOutputStream(
	                        connectionSocket.getOutputStream());
	    	  
	    	  reader.put(inFromClient,outToClient);
	    	  Thread a = new Thread(new ServerProcessor(inFromClient,outToClient,server,serverName));
	 		  a.start();	   
	 		}
		}
	
    public void run() {
        System.out.println("Hello from server thread!");
        try {
			acceptRequest();
		} catch (IOException e) {
			System.out.println("failed to accept request");
		}
        
    }
}

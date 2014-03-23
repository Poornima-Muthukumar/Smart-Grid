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

	/*Server Thread to accept new Socket connections. 
	 Once the server accepts a new socket connection, it saves the socket, DataInputStream, BufferedReader and then creates a new thread(ServerProcessor) that handles input/output to the socket */
	public void acceptRequest() throws IOException {
		
		int port = Integer.parseInt(server.details.get(serverName).get(1));
	    ServerSocket welcomeSocket = new ServerSocket(); 
	    welcomeSocket.setReuseAddress(true);
	    welcomeSocket.bind(new InetSocketAddress(port));
	   
	     while(true) {
	    	  Socket connectionSocket = welcomeSocket.accept();
	    	 
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
        	//While loop to accept incoming requests from the clients.
			acceptRequest();
		} catch (IOException e) {
			System.out.println("failed to accept request");
		}
        
    }
}

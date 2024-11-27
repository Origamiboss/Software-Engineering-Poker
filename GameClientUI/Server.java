package GameClientUI;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import ocsf.server.*;

import sweProject.GameData;

public class Server extends AbstractServer{
	// Data fields for this chat server.
	  private JTextArea log;
	  private JLabel status;
	  private boolean running = false;
	  
	  private ArrayList<GameData> players;
	  private ArrayList<ConnectionToClient> clients;

	  // Constructor for initializing the server with default settings.
	  public Server(int port)
	  {
	    super(port);
	    this.setTimeout(500);
	    players = new ArrayList<GameData>();
	    clients = new ArrayList<ConnectionToClient>();
	  }

	  // Getter that returns whether the server is currently running.
	  public boolean isRunning()
	  {
	    return running;
	  }
	  

	  // When the server starts, update the GUI.
	  public void serverStarted()
	  {
	    
	  }
	  
	  // When the server stops listening, update the GUI.
	   public void serverStopped()
	   {
	     
	   }
	 
	  // When the server closes completely, update the GUI.
	  public void serverClosed()
	  {
	    
	  }

	  // When a client connects or disconnects, display a message in the log.
	  public void clientConnected(ConnectionToClient client)
	  {
		  System.out.println("Client connected: " + client.getId());
			//tell the client to send their name
			try {
				client.sendToClient("Send your name");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }

	  // When a message is received from a client, handle it.
	  public void handleMessageFromClient(Object arg0, ConnectionToClient arg1)
	  {
		  System.out.println(arg0);
		  if(arg0 instanceof GameData) {
			  //check if the client connection exists, if not then add it
			  if(!clients.contains(arg1))
				  clients.add(arg1);
			  
			  
			  //turn the object into GameData
			  GameData clientData = (GameData)arg0;
			  //Update that players data or create it
			  if(!players.contains(clientData)) {
				  boolean newPlayer = true;
				  //find out it is new or not
				  for(GameData gd : players) {
					  //Player Exists so update
					  if(gd.getUsername().equals(clientData.getUsername())) {
						  newPlayer = false;
						  gd.update(clientData);
						  break;
					  }
				  }
				  if(newPlayer) {
					  //then there is a new player so add it
					  players.add(clientData);
				  }
			  }
			  
			  
			  //Now update all clients
			  for(ConnectionToClient c : clients) {
					try {
						c.sendToClient(players);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			  }
			  
		  }
		  if(arg0 instanceof String) {
			  String msg = (String)arg0;
			  if(msg.contains(" is leaving")) {
				  //a player is leaving
				  clients.remove(arg1);
				  //locate the username
				  String user = msg.split(" is leaving")[0];
				  for(GameData gd : players) {
					  if(gd.getUsername().equals(user)) {
						  //remove them from players
						  players.remove(gd);
						  break;
					  }
				  }
				  //update all players
				  for(ConnectionToClient c : clients) {
						try {
							c.sendToClient(players);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				  }
			  }
			  
		  }
	    
	  }
}

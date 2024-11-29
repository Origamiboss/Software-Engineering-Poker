package GameClientUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JPanel;

import ocsf.client.AbstractClient;
import sweProject.GameData;
import sweProject.MainControl;

public class GameClientController extends AbstractClient{
	
	private String ip;
	private int port;
	private GameData myData;
	
	private String status;
	private Server server;
	
	private MainControl main;
	private InitialScreenUI initial;
	private GameClientUI gameui;
	private GameClientControllerPanel gcp;
	private boolean playingGame = false;
	
	public GameClientController(MainControl mainControl, GameClientControllerPanel gcp, InitialScreenUI in, GameClientUI cli) {
		super("localhost",12345);
		myData = new GameData();
		main = mainControl;
		initial = in;
		gameui = cli;
		this.gcp = gcp;  
	}
	public void HostGame(int port) throws IOException {
		//Create a server and accompanying UI
		Server s = new Server(port);
		server = s;
		//start the server listening
		s.listen();
		
		this.port = port;
		ip = GetLocalIPAddress().getHostAddress();
		//set up the initial with the ip
		initial.setServerIp(ip);
		//set up the client
		
		
		setHost(ip);
		setPort(port);
		try
	    {
	      openConnection();
	      status = "Hosting";
	    }
	    catch (IOException e)
	    {
	    	handleError(e.getLocalizedMessage());
	    }
		
		
	}
	public void setGameData(String username, int wealth) {
		myData.setUsername(username);
		myData.setTotalMoneyAmount(wealth);
		myData.setBettedMoney(0);
		myData.setCardsSwapped(0);
	}
	public void JoinGame(String ip, int port) {
		//Join a server
		
		//set up the initial with the ip
		initial.setServerIp(ip);
		//set up the client
		this.ip = ip;
		this.port = port;
		setHost(ip);
		setPort(port);
		try
	    {
	      openConnection();
	      status = "Joining";
	    }
	    catch (IOException e)
	    {
	      handleError(e.getLocalizedMessage());
	    }
	}
	// Method that handles messages from the server.
	public void handleMessageFromServer(Object arg0)
	{
		System.out.println(arg0);
		if(playingGame == false) {
			//check if string
			if(arg0 instanceof String) {
			    //send your data to the server
				if(arg0.toString().equals("Send your name")) {
					//send the data to the server
					try {
						sendToServer(myData);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//server is closing
				if(arg0.toString().equals("Server is closing")) {
					//disconnect
					try {
						this.closeConnection();
						main.openMainPage();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//Start the game
				if(arg0.toString().equals("Starting Game")) {
					//open the clientUI
					gcp.showGameScreen();
					playingGame = true;
				}
				
			}
			if(arg0 instanceof ArrayList<?>) {
				ArrayList<GameData> gameDataList = (ArrayList<GameData>) arg0;
				//update initial
				initial.updateWhoJoined(gameDataList);
			}
		}else {
			//the Game has started
			if(arg0 instanceof ArrayList<?>) {
				ArrayList<GameData> gameDataList = (ArrayList<GameData>) arg0;
				//update initial
				gameui.updatePlayerPanel(gameDataList);
			}
			
		}
	}
	private void handleError(String error) {
		
	}
	public void exit() {
		switch(status) {
		case "Hosting":
			try {
				this.closeConnection();
				//kick all clients
				server.kickClients();
				//close the server
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "Joining":
			try {
				sendToServer(myData.getUsername() + " is leaving");
				this.closeConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
				
		}
	}
	public void StartGame() {
		if(status.equals("Hosting")) {
			//Tell the server to tell the Clients to start the game
			server.StartGame();
		}
		
	}
	
	
	
	private InetAddress GetLocalIPAddress() {

	       try {
	            InetAddress ip = InetAddress.getLocalHost();
	            return ip;
	        } catch (UnknownHostException e) {
	            e.printStackTrace();
	            
	        }
	       	return null;
		}
	
}

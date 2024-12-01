package GameClientUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import GameClientUI.Server.Card;
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
		gameui.setGameClientController(this);
		this.gcp = gcp;  
		
		//make the thread checker
		Thread gameThread = new Thread(new Runnable() {
	        @Override
	        public void run() {
	            try {
					socketChecker();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    });
	}
	private void socketChecker() {
		while(status == "Hosting" || status == "Joining") {
			//run while game exists
			try {
	            // Check if the socket is connected
	            if (!isConnected()) {
	                System.out.println("Socket is closed or disconnected. Attempting to reconnect...");
	                
	                // Try reconnecting to the server
	                try {
	                    openConnection();
	                    System.out.println("Reconnected to the server.");
	                } catch (IOException e) {
	                    // If reconnection fails, wait before retrying
	                    System.out.println("Reconnection failed. Retrying in 5 seconds...");
	                    Thread.sleep(5000); // Retry after 5 seconds
	                }
	            }
	            
	            // Sleep for a while before checking again
	            Thread.sleep(1000); // Check every 1 second
	            
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
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
	    	e.printStackTrace();
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
	      e.printStackTrace();
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
		}
		else {
			//the Game has started
			if(arg0 instanceof ArrayList<?>) {
				ArrayList<?> list = (ArrayList<?>) arg0;
				if(list.get(0) instanceof GameData) {
					ArrayList<GameData> gameDataList = (ArrayList<GameData>) list;
					//update initial
					gameui.updatePlayerPanel(gameDataList);
					//update myself
					for(GameData gd : gameDataList) {
						if(gd.getUsername().equals(myData.getUsername())) {
							myData.update(gd);
						}
					}
					//send the dictionary back
					try {
						sendToServer(gameui.playerIdDictionary);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			//update user and player values
			//example message from server to invoke a function: "updatePlayerBalance: 2, 500"
			else if (arg0 instanceof String) {
	            String message = arg0.toString();
	            if (message.startsWith("Buy in ")) {
	            	//tell the client its time to buy in
	            	gameui.buyIn();
	            }
	            if(message.startsWith("Change cards")) {
	            	//tell the client its time to change
	            	gameui.changeCards();
	            }
	            if(message.startsWith("Bet ")) {
	            	//tell the client its time to change
	            	gameui.betIn();
	            }
	            if(message.startsWith("Winner is")) {
	            	//tell the client its time to change
	            	gameui.judge(message);
	            }
	            if(message.startsWith("showCards:")) {
	            	String msg = message.split("showCards:")[1];
	            	String username = msg.split(";")[0];
	            	String other = msg.split(";")[1];
	            	String[] CardData = other.split(":");
	            	gameui.showAllCards(username,CardData);
	            }
	            if(message.startsWith("Pot:")) {
	            	gameui.updatePot(message.split("Pot:")[1]);
	            }
	            if (message.startsWith("updateUserBalance:")) 
	            {
	            	int balance = Integer.parseInt(message.split(":")[1].trim());
					gcp.updateUserBalance(balance);
	            }
	            if (message.startsWith("updateUserCard:"))
	            {
	            	String[] parts = message.split(":")[1].split(",", 2);
                    int cardIndex = Integer.parseInt(parts[0].trim());
                    String newCardImagePath = parts[1].trim();
                    gcp.updateUserCard(cardIndex, newCardImagePath);
	            }
	            if (message.startsWith("updatePot:")) 
	            {
	            	String amount = message.split(":")[1].trim();
					gcp.updatePot(amount);
	            }
	            if (message.startsWith("updatePlayerBalance:"))
	            {
	            	String[] parts = message.split(":")[1].split(",", 2);
                    int playerIndex = Integer.parseInt(parts[0].trim());
                    int balance = Integer.parseInt(parts[1].trim());
                    gcp.updatePlayerBalance(playerIndex, balance);
	            }
	            if (message.startsWith("updatePlayerCard:"))
	            {
	            	String[] parts = message.split(":")[1].split(",", 3);
	            	int playerIndex = Integer.parseInt(parts[0].trim());
                    int cardIndex = Integer.parseInt(parts[1].trim());
                    String newCardImagePath = parts[2].trim();
                    gcp.updatePlayerCard(playerIndex, cardIndex, newCardImagePath);
	            }
	            if (message.startsWith("updatePlayerBet:"))
	            {
	            	String[] parts = message.split(":")[1].split(",", 2);
                    int playerIndex = Integer.parseInt(parts[0].trim());
                    int betAmount = Integer.parseInt(parts[1].trim());
                    gcp.updatePlayerBet(playerIndex, betAmount);
	            }
	            if (message.startsWith("updatePlayerStatus:"))
	            {
	            	String[] parts = message.split(":")[1].split(",", 2);
                    int cardIndex = Integer.parseInt(parts[0].trim());
                    String folded = parts[1].trim();
                    gcp.updatePlayerStatus(cardIndex, folded);
	            }
	            if (message.startsWith("updateCardsChanged:"))
	            {
	            	String[] parts = message.split(":")[1].split(",", 2);
                    int playerIndex = Integer.parseInt(parts[0].trim());
                    int cardsChanged = Integer.parseInt(parts[1].trim());
                    gcp.updateCardsChanged(playerIndex, cardsChanged);
	            }
	        }
		}
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
		main.openMainPage();
	}
	public void StartGame() {
		if(status.equals("Hosting")) {
			//Tell the server to tell the Clients to start the game
			server.StartGame();
		}
		
	}
	public void buyIn() {
		try {
			sendToServer(myData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void bet(int betAmount) {
		try {
			sendToServer("Bet:" + myData.getUsername() + "," + betAmount);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void fold() {
		try {
			sendToServer("Fold:" + myData.getUsername());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void skip() {
		try {
			sendToServer("skip");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void changeCards(ArrayList<Integer> indexes) {
		//Message should be "Username:1,2,3"
		String data = "Change;" + myData.getUsername() + ":";
		for(int i : indexes) {
			data = data + i + ",";
		}
		if (data.endsWith(",")) {
		    data = data.substring(0, data.length() - 1);
		}
		//remove the , at the end
		try {
			sendToServer(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

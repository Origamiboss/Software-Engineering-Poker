package GameClientUI;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import ocsf.server.*;

import sweProject.GameData;

public class Server extends AbstractServer{
	// Data fields for this Server.
	  private JTextArea log;
	  private JLabel status;
	  
	  private ArrayList<GameData> players;
	  private ArrayList<ConnectionToClient> clients;
	  private ArrayList<GameData> participantsInRound;
	  //The Participants Cards
	  private Map<String, Card[]> hands;
	  
	  //how a latch works is that it will count down from a number before moving on
	  private CountDownLatch latch = new CountDownLatch(0);
	  
	  //get the phase of the server
	  private enum phase {
		  Buy,Change,Bet,Judge,None
	  }
	  private phase gamePhase = phase.None;
	  
	  //How much is the buy in cost
	  static private int buyInCost = 10;
	  
	  // Constructor for initializing the server with default settings.
	  public Server(int port)
	  {
	    super(port);
	    this.setTimeout(500);
	    players = new ArrayList<GameData>();
	    clients = new ArrayList<ConnectionToClient>();
	    participantsInRound = new ArrayList<GameData>();
	  }
	  public void kickClients() {
		//send a closing message to all players
		  for(ConnectionToClient c : clients) {
				try {
					c.sendToClient("Server is closing");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
	  }
	  // When a client connects or disconnects, display a message in the log.
	  public void clientConnected(ConnectionToClient client)
	  {
		  
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
		  //Pre Game server stuff
		  if(gamePhase == phase.None) {
			  if(arg0 instanceof GameData) {
				  //check if the client connection exists, if not then add it
				  if(!clients.contains(arg1)) {
					  clients.add(arg1);
					  latch = new CountDownLatch(clients.size());
				  }
				  
				  
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
				  
				  
				  updatePlayers(players);
				  
			  }
			  if(arg0 instanceof String) {
				  String msg = (String)arg0;
				  if(msg.contains(" is leaving")) {
					  //a player is leaving
					  clients.remove(arg1);
					  latch = new CountDownLatch(clients.size());
					  //locate the username
					  String user = msg.split(" is leaving")[0];
					  for(GameData gd : players) {
						  if(gd.getUsername().equals(user)) {
							  //remove them from players
							  players.remove(gd);
							  break;
						  }
					  }
					  updatePlayers(players);
				  }
				  
			  }
		  }
		  //During Game stuff
		  else {
			  if(arg0 instanceof GameData) {
				  //turn the object into GameData
				  GameData clientData = (GameData)arg0;
				  //Depending on the gamePhase, the different GameData will have different meanings
				  switch(gamePhase) {
				  case phase.Buy:
					  if(clientData.getBettedMoney() == buyInCost)
						  participantsInRound.add(clientData);
					  //generate a hand for the client
					  hands.put(clientData.getUsername(), generateCards());
					  //send the client their new hand
					  updatePlayer(hands.get(clientData),arg1);
					  
					  break;
				  case phase.Bet:
					  //update the GameData with the bet and then update everybody about it
					  for(GameData gd : participantsInRound) {
						  if(gd.getUsername().equals(clientData.getUsername())) {
							  gd.update(clientData);
							  break;
						  }
					  }
					  updatePlayers(participantsInRound);
					  break;
				  }
			  }
			  if(arg0 instanceof String) {
				  String msg = (String)arg0;
				  switch(gamePhase) {
				  case phase.Change:
					  //Message should be "Username:Card|1,Card|2"
					  //split the msg into username and cards
					  String username = msg.split(":")[0];
					  String cardMessage = msg.split(":")[1];
					  String[] cards = cardMessage.split(",");
					  //locate which player wants to replace their cards
					  for(GameData gd : participantsInRound) {
						  if(gd.getUsername().equals(username)) {
							  //replace their cards
							  //Gather the card records
							  Card[] savedCards = hands.get(gd);
							  //Generate new Cards
							  Card[] newCards = generateCards(cards.length);
							  int newCardCounter = 0;
							  //Create dummy cards
							  Card[] changeCards = new Card[cards.length];
							  for(int i=0; i< changeCards.length; i++) {
								  String[] cardData = cards[i].split("|");
								  Card.Suit suit;
								  switch(cardData[0]) {
								  case "Dimond":
									  suit = Card.Suit.Dimond;
									  break;
								  case "Heart":
									  suit = Card.Suit.Heart;
									  break;
								  case "Clover":
									  suit = Card.Suit.Clover;
									  break;
								  case "Spade":
									  suit = Card.Suit.Spade;
									  break;
								  default:
									  suit = Card.Suit.Dimond;
								  }
								  //assign the type of card
								  int type = Integer.parseInt(cardData[1]);
								  
								  changeCards[i] = new Card(suit,type);
							  }
							  //now replace the savedCards with the new cards
							  for(int i = 0; i < savedCards.length; i++) {
								  for(int j = 0; j < changeCards.length; j++) {
									  if(savedCards[i].equals(changeCards[j])) {
										  savedCards[i] = newCards[newCardCounter];
										  newCardCounter++;
									  }
								  }
							  }
							  
							  //save saved cards back to hands and tell the client about their cards
							  hands.put(gd.getUsername(), savedCards);
							  updatePlayer(savedCards, arg1);
							  
							  gd.setCardsSwapped(changeCards.length);
							  //update all players
							  updatePlayers(participantsInRound);
							  break;
						  }
					  }
					  break;
				  }
			  }
			 
		  }
	    
	  }
	  public void StartGame() {
		  //tell all clients that the game is running
		  updatePlayers("Starting Game");
		  
		  //now run this in the background
		  Thread gameThread = new Thread(new Runnable() {
		        @Override
		        public void run() {
		            try {
						runGame();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		    });

		    // Start the thread
		    gameThread.start();
	  }
	  private void runGame() throws InterruptedException {
		  //Give All Players the GameData
		  updatePlayers(players);
		  //run the game until there is 1 player left
		  while(players.size() > 1) {
			  //buy in
			  updatePlayers("Buy in " + buyInCost);
			  gamePhase = phase.Buy;
			  latch.countDown();
			  latch.await();
			  //update the players on who is playing
			  updatePlayers(participantsInRound);
			  
			  //change cards
			  updatePlayers("Change cards");
			  gamePhase = phase.Change;
			  latch.countDown();
			  latch.await();
			  //betting phase
			  updatePlayers("Bet");
			  gamePhase = phase.Bet;
			  latch.countDown();
			  latch.await();
			  //judging phase
			  String winner = decideWinner();
			  
			  updatePlayers("Winner is " + winner);
			  gamePhase = phase.Judge;
			  latch.countDown();
			  latch.await();
		  }
	  }
	  
	  
	  
	  private void updatePlayers(Object obj) {
		//update all players
		  for(ConnectionToClient c : clients) {
				try {
					c.sendToClient(obj);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
	  }
	  private void updatePlayer(Object obj, ConnectionToClient conn) {
		  try {
			  conn.sendToClient(obj);
		  }catch (IOException e) {
			  e.printStackTrace();
		  }
	  }
	  private String decideWinner() {
		  String winner = "";
		  int[] handValues = new int[participantsInRound.size()];
		  for(int i = 0; i < handValues.length; i++) {
			  Card[] hand = hands.get(participantsInRound.get(i).getUsername());
			  //find the point value of the hand
		  }
		  
		  return winner;
	  }
	  //generate a whole new hand
	  private Card[] generateCards() {
		  Card[] hand = new Card[5];
		  
		  
		  return hand;
	  }
	  //generate a couple new cards
	  private Card[] generateCards(int amount) {
		  Card[] hand = new Card[amount];
		  
		  
		  return hand;
	  }
	  //create a card class to hold the card types
	  private class Card {
		  public enum Suit{
			  Dimond,Heart,Clover,Spade
		  }
		  private Suit suit;
		  private int value;
		  public Card(Suit suit, int value){
			  this.suit = suit;
			  this.value = value;
		  }
		  public void setCardType(Suit suit, int value) {
			  this.suit = suit;
			  this.value = value;
		  }
		  public String getCardType() {
			  String cardType = "";
			  
			  switch(suit) {
			  case Suit.Dimond:
				  cardType += "Dimond,";
				  break;
			  case Suit.Heart:
				  cardType += "Heart,";
				  break;
			  case Suit.Clover:
				  cardType += "Clover,";
				  break;
			  case Suit.Spade:
				  cardType += "Spade,";
				  break;
			  }
			  
			  return cardType;
		  }
	  }
}

package GameClientUI;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
	  private Map<String, List<Card>> hands;
	  //<Player Username, Player Id>
	  private Map<String, Integer> playerIdDictionary;
	  
	  //used cards holder
	  private ArrayList<Card> usedCards;
	  
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
	    
	    hands = new HashMap<>();
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
					  if(clientData.getTotalMoneyAmount() >= buyInCost) {
						  clientData.setBettedMoney(buyInCost);
						  clientData.setTotalMoneyAmount(clientData.getTotalMoneyAmount() - buyInCost);
						  participantsInRound.add(clientData);
						  //generate a hand for the client
						  hands.put(clientData.getUsername(), generateCards());
						  //send the client their new hand
						  //Generate the String Array
						  String[] cards = new String[hands.get(clientData.getUsername()).size()];
						  for(int i = 0; i < cards.length; i++) {
							  cards[i] = hands.get(clientData.getUsername()).get(i).getCardType();
							  
							  
							  //send the specific card data
							  String card = "updateUserCard:";
							  card = card + i +",";
							  //locate the card path
							  String[] cardData = cards[i].split(",");
							  //fix cardData[1] with king, queen, jack, or ace
							  if(cardData[1].contains("11")) {
								  cardData[1] = "jack";
							  }else if(cardData[1].contains("12")) {
								  cardData[1] = "queen";
							  }else if(cardData[1].contains("13")) {
								  cardData[1] = "king";
							  }else if(cardData[1].contains("14")) {
								  cardData[1] = "ace";
							  }
							  String cardPath = "/Cards/"+cardData[1]+"_of_"+cardData[0].toLowerCase()+".png";
							  card += cardPath;
							  
							  //give the cards
							  updatePlayer(card,arg1);
							  
						  }
					  }
					  
					  latch.countDown();
					  break;
				  
				  	}
			  
			  }
			  if(arg0 instanceof String) {
				  String msg = (String)arg0;
				  switch(gamePhase) {
				  case phase.Change:
					  //Message should be "Username:1,2,3"
					  //split the msg into username and cards
					  String username = msg.split(":")[0];
					  System.out.println(username);
					  String cardMessage = msg.split(":")[1];
					  System.out.println(cardMessage);
					  String[] cards = cardMessage.split(",");
					  System.out.println(cards);
					  //locate which player wants to replace their cards
					  for(GameData gd : participantsInRound) {
						  if(gd.getUsername().equals(username)) {
							  //replace their cards
							  List<Card> savedCards = hands.get(gd.getUsername());
							  
							  int timeRan = 0;
							  //remove the cards
							  for(String c : cards) {
								  //replace each card
								  int index = Integer.parseInt(c);
								  savedCards.remove(index - timeRan);
								  timeRan++;
							  }
							  //replace the cards
							  for(int i = 0; i < timeRan; i++) {
								  savedCards.add(generateCards(1).get(0));
							  }
							  //save saved cards back to hands and tell the client about their cards
							  hands.put(gd.getUsername(), savedCards);
							  
							  //turn hand into string
							  //Generate the String Array
							  for(int i = 0; i < cards.length; i++) {
								  cards[i] = hands.get(gd.getUsername()).get(i).getCardType();
								  
								  
								  //send the specific card data
								  String card = "updateUserCard:";
								  card = card + i +",";
								  //locate the card path
								  String[] cardData = cards[i].split(",");
								  //fix cardData[1] with king, queen, jack, or ace
								  if(cardData[1].contains("11")) {
									  cardData[1] = "jack";
								  }else if(cardData[1].contains("12")) {
									  cardData[1] = "queen";
								  }else if(cardData[1].contains("13")) {
									  cardData[1] = "king";
								  }else if(cardData[1].contains("14")) {
									  cardData[1] = "ace";
								  }
								  String cardPath = "/Cards/"+cardData[1]+"_of_"+cardData[0].toLowerCase()+".png";
								  card += cardPath;
								  
								  //give the cards
								  updatePlayer(card,arg1);
								  
							  }
							  
							  gd.setCardsSwapped(cards.length);
							  //update all players
							  updatePlayers(participantsInRound);
							  break;
						  }
					  }
					  latch.countDown();
					  break;
				  case phase.Bet:
					  //update the GameData with the bet and then update everybody about it
					  
					  if(msg.contains("Bet:")) {
						  msg = msg.split("Bet:")[0];
						  String[] data = msg.split(",");
						  for(GameData gd : participantsInRound) {
							  if(gd.getUsername().equals(data[0]) && gd.getTotalMoneyAmount() >= Integer.parseInt(data[1])) {
								  int bettedMoney = Integer.parseInt(data[1]);
								  gd.setBettedMoney(gd.getBettedMoney() + bettedMoney);
								  gd.setTotalMoneyAmount(gd.getTotalMoneyAmount() - bettedMoney);
								  break;
							  }
						  }
					  }
					  
					  updatePlayers(participantsInRound);
					  latch.countDown();
					  break;
				  }
				  if(msg.contains("skip")) {
					  latch.countDown();
				  }
			  }
			  if(arg0 instanceof Map) {
				  //recieved playerId
				  playerIdDictionary = (Map)arg0;
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
			  usedCards = new ArrayList<>();
			  //buy in
			  updatePlayers("Buy in " + buyInCost);
			  gamePhase = phase.Buy;
			  latch.await();
			  
			  latch = new CountDownLatch(participantsInRound.size());
			  //update the players on who is playing
			  updatePlayers(participantsInRound);
			  
			  //change cards
			  updatePlayers("Change cards");
			  gamePhase = phase.Change;
			  latch.await();
			  
			  latch = new CountDownLatch(participantsInRound.size());
			  //betting phase
			  updatePlayers("Bet ");
			  gamePhase = phase.Bet;
			  latch.await();
			  
			  latch = new CountDownLatch(participantsInRound.size());
			  //judging phase
			  String winner = decideWinner();
			  
			  updatePlayers("Winner is " + winner);
			  gamePhase = phase.Judge;
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
		  //keep track of highest card type
		  HashMap<String, Card.Suit> types = new HashMap<>();
		  for(int i = 0; i < handValues.length; i++) {
			  List<Card> hand = hands.get(participantsInRound.get(i).getUsername());
			  //find the point value of the hand
			  //Scoring system
			  //0-garbage
			  //20-pair
			  //40-2Pair
			  //60-3 of kind
			  //80-straight
			  //100-flush
			  //120- full house
			  //140- four of kind
			  //160-straight flush
			  //180- royale flush
			  switch(PokerHandEvaluator.evaluateHand(hand)) {
			  
			  }
			  
			  
			  
		  }
		  
		  return winner;
	  }
	  //generate a whole new hand
	  private List<Card> generateCards() {
		  return generateCards(5);
	  }
	  //generate a couple new cards
	  private List<Card> generateCards(int amount) {
		  List<Card> hand = new ArrayList<>();
		  
		  for(int i =0; i < amount; i++) {
			  Card.Suit suit;
			  Random rand = new Random();
			  int value = rand.nextInt(2,14);
			  int suitNum = rand.nextInt(1,4);
			  switch(suitNum) {
			  case 1:
				  suit = Card.Suit.DIAMONDS;
				  break;
			  case 2:
				  suit = Card.Suit.HEARTS;
				  break;
			  case 3:
				  suit = Card.Suit.CLUBS;
				  break;
			  default:
				  suit = Card.Suit.SPADES;
				  break;
			  }
			  boolean cardNotFound = true;
			  Card newCard = new Card(suit,value);
			  for(Card c : usedCards) {
				  if(c.getSuit() == newCard.getSuit() && c.getValue() == newCard.getValue()) {
					  cardNotFound = false;
					  break;
				  }
			  }
			  if(cardNotFound) {
				  hand.add(new Card(suit,value));
			  	  usedCards.add(new Card(suit, value));
			  }
			  else {
				  i--;
			  }
		  }
		  
		  return hand;
	  }
	  //create a card class to hold the card types
	  private class Card {
		  enum Suit {
		        DIAMONDS, HEARTS, CLUBS, SPADES
		    }

		    private Suit suit;
		    private int value;

		    public Card(Suit suit, int value) {
		        this.suit = suit;
		        this.value = value;
		    }

		    public Suit getSuit() {
		        return suit;
		    }

		    public int getValue() {
		        return value;
		    }

		    public String getCardType() {
		        return this.suit + "," + this.value;
		    }
	  }
	  
	  
	  public class PokerHandEvaluator {

		    public static String evaluateHand(List<Card> cards) {
		        // Sort the cards by value (Ace = 14, King = 13, ..., 2 = 2)
		        cards.sort(Comparator.comparingInt(Card::getValue));

		        // Check if all cards have the same suit (Flush)
		        boolean isFlush = cards.stream().allMatch(card -> card.getSuit() == cards.get(0).getSuit());

		        // Check if the values are consecutive (Straight)
		        boolean isStraight = cards.get(4).getValue() - cards.get(0).getValue() == 4 && 
		                             new HashSet<>(Arrays.asList(cards.get(0).getValue(), cards.get(1).getValue(),
		                                                          cards.get(2).getValue(), cards.get(3).getValue(),
		                                                          cards.get(4).getValue())).size() == 5;

		        // Check if Ace is used as low (Ace can be 1 for A, 2, 3, 4, 5 straight)
		        boolean isLowAceStraight = cards.get(0).getValue() == 2 && cards.get(1).getValue() == 3 && 
		                                   cards.get(2).getValue() == 4 && cards.get(3).getValue() == 5 && 
		                                   cards.get(4).getValue() == 14;

		        if (isLowAceStraight) {
		            cards.get(0).value = 1; // Treat Ace as 1 for the straight (A, 2, 3, 4, 5)
		        }

		        // Count occurrences of values
		        Map<Integer, Integer> valueCounts = new HashMap<>();
		        for (Card card : cards) {
		            valueCounts.put(card.getValue(), valueCounts.getOrDefault(card.getValue(), 0) + 1);
		        }

		        if (isFlush && isStraight) {
		            return isRoyalFlush(cards) ? "Royal Flush" : "Straight Flush";
		        } else if (isFourOfAKind(valueCounts)) {
		            return "Four of a Kind";
		        } else if (isFullHouse(valueCounts)) {
		            return "Full House";
		        } else if (isFlush) {
		            return "Flush";
		        } else if (isStraight) {
		            return "Straight";
		        } else if (isThreeOfAKind(valueCounts)) {
		            return "Three of a Kind";
		        } else if (isTwoPair(valueCounts)) {
		            return "Two Pair";
		        } else if (isOnePair(valueCounts)) {
		            return "One Pair";
		        } else {
		            return "High Card";
		        }
		    }

		    private static boolean isRoyalFlush(List<Card> cards) {
		        return cards.get(0).getValue() == 10 && cards.get(1).getValue() == 11 && 
		               cards.get(2).getValue() == 12 && cards.get(3).getValue() == 13 && 
		               cards.get(4).getValue() == 14;
		    }

		    private static boolean isFourOfAKind(Map<Integer, Integer> valueCounts) {
		        return valueCounts.containsValue(4);
		    }

		    private static boolean isFullHouse(Map<Integer, Integer> valueCounts) {
		        return valueCounts.containsValue(3) && valueCounts.containsValue(2);
		    }

		    private static boolean isThreeOfAKind(Map<Integer, Integer> valueCounts) {
		        return valueCounts.containsValue(3);
		    }

		    private static boolean isTwoPair(Map<Integer, Integer> valueCounts) {
		        return valueCounts.values().stream().filter(count -> count == 2).count() == 2;
		    }

		    private static boolean isOnePair(Map<Integer, Integer> valueCounts) {
		        return valueCounts.containsValue(2);
		    }
		}
}

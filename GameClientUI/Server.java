package GameClientUI;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

public class Server extends AbstractServer {
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
	  private int whoBetted = 0;
	  
	  //get the phase of the server
	  private enum phase {
		  Buy,Change,Bet,Judge,None
	  }
	  private phase gamePhase = phase.None;
	  
	  //How much is the buy in cost
	  static private int buyInCost = 10;
	  //highest better
	  private int highestBetter = 0;
	  private int pot = 0;
	  
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
				if(gamePhase == phase.None)
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
				  //reset clientData
				  for(GameData player : players) {
					  if(player.getUsername().equals(clientData.getUsername()))
						  clientData = player;
				  }
				  //Depending on the gamePhase, the different GameData will have different meanings
				  switch(gamePhase) {
				  case phase.Buy:
					  
					  if(clientData.getTotalMoneyAmount() >= buyInCost) {
						  //connect the clientData with playerData
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
						  pot += buyInCost;
						  updatePot();
					  }
					  
					  latch.countDown();
					  break;
				  
				  	}
			  
			  }
			  if(arg0 instanceof String) {
				  String msg = (String)arg0;
				  switch(gamePhase) {
				  case phase.Change:
					// Message should be "Username:1,2,3"
					// Split the msg into username and cards
					if(msg.startsWith("Change;")) {
						msg = msg.split("Change;")[1];
						String username = msg.split(":")[0];
						String cardMessage = msg.split(":")[1];
						// Check if cardMessage is not null or empty before proceeding
						if (cardMessage != null && !cardMessage.isEmpty()) {
						    String[] cards = cardMessage.split(",");
						    
						    // Locate which player wants to replace their cards
						    for (GameData gd : participantsInRound) {
						        if (gd.getUsername().equals(username)) {
						            List<Card> savedCards = hands.get(gd.getUsername());
						            
						            // Remove the cards, iterating in reverse order
						            List<Integer> indicesToRemove = new ArrayList<>();
						            for (String c : cards) {
						                int index = Integer.parseInt(c);
						                indicesToRemove.add(index);
						            }
						            
						            // Remove cards safely by iterating in reverse order to prevent index shifting issues
						            Collections.sort(indicesToRemove, Collections.reverseOrder());
						            for (int index : indicesToRemove) {
						                savedCards.remove(index);
						            }
	
						            // Add new cards
						            savedCards.addAll(generateCards(cards.length));
	
						            // Save the updated cards
						            hands.put(gd.getUsername(), savedCards);
	
						            // Turn hand into string
						            int i = 0;
						            for (Card c : hands.get(gd.getUsername())) {
						                // Send the specific card data
						                String card = "updateUserCard:";
						                card = card + i + ",";
						                i++;
	
						                // Locate the card path
						                String[] cardData = c.getCardType().split(",");
	
						                // Fix cardData[1] with king, queen, jack, or ace
						                if (cardData[1].contains("11")) {
						                    cardData[1] = "jack";
						                } else if (cardData[1].contains("12")) {
						                    cardData[1] = "queen";
						                } else if (cardData[1].contains("13")) {
						                    cardData[1] = "king";
						                } else if (cardData[1].contains("14")) {
						                    cardData[1] = "ace";
						                }
	
						                String cardPath = "/Cards/" + cardData[1] + "_of_" + cardData[0].toLowerCase() + ".png";
						                card += cardPath;
	
						                // Give the cards
						                updatePlayer(card, arg1);
						            }
	
						            gd.setCardsSwapped(indicesToRemove.size());
						            // Update all players
						            updatePlayers(participantsInRound);
						            latch.countDown();
						            break;
						        }
					            
					        }
					    }
					}

					

					break;
				  case phase.Bet:
					  //update the GameData with the bet and then update everybody about it. Only decrement the latch if the round is over
					  //Example msg = "Bet:username,amount"
					  if(msg.contains("Bet:")) {
						  msg = msg.split("Bet:")[1];
						  String[] data = msg.split(",");
						  int bettedMoney = Integer.parseInt(data[1]);
						  for(GameData gd : participantsInRound) {
						        
						        // Check if the player exists and has enough money
						        if (gd.getUsername().equals(data[0]) && gd.getTotalMoneyAmount() >= bettedMoney && bettedMoney > 0 && bettedMoney + gd.getBettedMoney() >= highestBetter) {
						            // Remove the bet amount from the player's total money
						            gd.setTotalMoneyAmount(gd.getTotalMoneyAmount() - bettedMoney);
						            pot+= bettedMoney;
						            // Add the player's previous bet to the new bet to get total bet
						            bettedMoney += gd.getBettedMoney(); 

						            // If the new bet is higher than the current highest, update the highest bet
						            if (bettedMoney > highestBetter) {
						                highestBetter = bettedMoney;
						                whoBetted = 0;
						            }
						            System.out.println("Latch Count Before: " + latch.getCount());
						            // Update the player's bet with the new total bet
						            gd.setBettedMoney(bettedMoney);

						            // Broadcast updates to all players
						            updatePlayers(participantsInRound);
						            whoBetted++;
						            //if everyone has betted or folded then latchDown
						            if(whoBetted >= participantsInRound.size())
						            	latch.countDown();
						            System.out.println("Latch Count After: " + latch.getCount());
						            updatePot();
						            break;
						        }
						  }
					  }
					  //rework
					  if(msg.startsWith("Fold:")) {
						  msg = msg.split("Fold:")[1];
						  GameData rmgd = null;
						  for(GameData gd : participantsInRound) {
							  if(gd.getUsername().equals(msg)) {
								  rmgd = gd;
							  }
						  }
						  if(rmgd != null) {
							  participantsInRound.remove(rmgd);
						  }
						//if everyone has betted or folded then latchDown
						  updatePlayers(participantsInRound);
						  if(whoBetted >= participantsInRound.size())
				            	latch.countDown();
						  
					  }
					  break;
						  
				  }
				  if(gamePhase != phase.Bet && msg.contains("skip")) {
					  latch.countDown();
					  //updatePlayers(participantsInRound);
				  }

				  if(msg.endsWith(" is leaving")) {
					  //locate the username
					  String user = msg.split(" is leaving")[0];
					  for(GameData gd : players) {
						  if(gd.getUsername().equals(user)) {
							  //remove them from players
							  players.remove(gd);
							  break;
						  }
					  }
					  //remove from participants
					  for(GameData gd : participantsInRound) {
						  if(gd.getUsername().equals(user)) {
							  //remove them from players
							  participantsInRound.remove(gd);
							  break;
						  }
					  }
					  //remove connection
					  clients.remove(arg1);
					  updatePlayers(players);
					  //countdown the latch
					  if(gamePhase != phase.Bet) {
						  latch.countDown();
						  System.out.println("Latch Left: " + latch.getCount());
					  }
				  }
					  
				  
			  }
			  if(arg0 instanceof Map) {
				  //recieved playerId
				  playerIdDictionary = (Map)arg0;
			  }
			  for(GameData pl : players) {
				  for(GameData pa: participantsInRound) {
					  //update
					  if(pl.getUsername().equals(pa.getUsername())) {
						  pl.update(pa);
					  }
				  }
			  }
		  }
		  if(latch != null) {
			  System.out.println("Latch Left: " + latch.getCount());
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
		  
		  //run the game until there is 1 player left
		  while(players.size() > 1) {
			  pot = 0;
			  participantsInRound.clear();
			  //Give All Players the GameData
			  updatePlayers(players);
			  latch = new CountDownLatch(players.size());
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
			  latch = new CountDownLatch(1);
			  //betting phase
			  highestBetter = buyInCost;
			  updatePlayers("Bet ");
			  gamePhase = phase.Bet;
			  latch.await();
			  
			  updatePlayers(participantsInRound);
			  
			  latch = new CountDownLatch(participantsInRound.size());
			  //judging phase
			  String winner = decideWinner();
			  Thread.sleep(1000);
			  updatePlayers("Winner is " + winner);
			  //send them all of the cards
			  System.out.println("Sending Cards");
			  for(GameData gd : participantsInRound) {
				  String data = "showCards:";
				  data += gd.getUsername() +";";
				  for(Card c : hands.get(gd.getUsername())) {
					  data += c.getCardType() + ":";
				  }
				  data = data.substring(0, data.length() - 1);
				  System.out.println("Hand:" + data);
				  updatePlayers(data);
				  Thread.sleep(100);
			  }
			  gamePhase = phase.Judge;
			  Thread.sleep(5000);
			  //latch.await();
		  }
	  }
	  
	  
	  
	  private void updatePlayers(Object obj) {
		//update all players
		  for(ConnectionToClient c : clients) {
				try {
					if(c.isAlive()) {
						c.sendToClient(obj);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  }
	  }
	  private void updatePlayer(Object obj, ConnectionToClient conn) {
		  try {
			  if(conn.isAlive())
				  conn.sendToClient(obj);
		  }catch (IOException e) {
			  e.printStackTrace();
		  }
	  }
	  private void updatePot() {
		  updatePlayers("Pot:" + pot);
	  }
	  private String decideWinner() {
		    String winner = "";
		    int highestHandValue = -1;
		    List<String> potentialWinners = new ArrayList<>();

		    // Evaluate each player's hand and determine the hand strength
		    for (GameData player : participantsInRound) {
		        List<Card> hand = hands.get(player.getUsername());
		        String handStrength = PokerHandEvaluator.evaluateHand(hand);

		        // Convert hand strength to a numerical value for comparison
		        int handValue = getHandValue(handStrength);

		        if (handValue > highestHandValue) {
		            // New highest hand, update the winner
		            highestHandValue = handValue;
		            potentialWinners.clear();
		            potentialWinners.add(player.getUsername());
		        } else if (handValue == highestHandValue) {
		            // Tie between players, add to potential winners
		            potentialWinners.add(player.getUsername());
		        }
		    }

		    // If there is a tie, compare the kicker cards (the highest remaining cards)
		    if (potentialWinners.size() == 1) {
		        winner = potentialWinners.get(0);
		    } else {
		        // Handle tie-breaking (e.g., comparing high cards)
		        winner = resolveTie(potentialWinners);
		    }
		    
		    //save all player data
		    for(GameData pl : players) {
				  for(GameData pa: participantsInRound) {
					  //update
					  if(pl.getUsername().equals(pa.getUsername())) {
						  System.out.println(pl.getUsername() + " monet: " + pl.getTotalMoneyAmount());
						  pl.setTotalMoneyAmount(pa.getTotalMoneyAmount());
						  pl.setBettedMoney(0);
						  pl.setCardsSwapped(0);
					  }
				  }
			  }
		    //give the winner the pot
		    for(GameData player : participantsInRound) {
		    	
		    	if(player.getUsername().equals(winner)) {
		    		player.setTotalMoneyAmount(player.getTotalMoneyAmount() + pot);
		    		System.out.println("Winners funds = " + player.getTotalMoneyAmount());
		    	}
		    	System.out.println(player.getUsername() + " monet: " + player.getTotalMoneyAmount());
		    }
		    
		    return winner;
		}

		// Convert the hand strength to a numerical value for comparison
		private int getHandValue(String handStrength) {
		    switch (handStrength) {
		        case "Royal Flush":
		            return 180;
		        case "Straight Flush":
		            return 160;
		        case "Four of a Kind":
		            return 140;
		        case "Full House":
		            return 120;
		        case "Flush":
		            return 100;
		        case "Straight":
		            return 80;
		        case "Three of a Kind":
		            return 60;
		        case "Two Pair":
		            return 40;
		        case "One Pair":
		            return 20;
		        default:
		            return 0; // High Card or invalid hand
		    }
		}

		// Resolve tie by comparing high cards
		private String resolveTie(List<String> tiedPlayers) {
		    List<Card> bestHand = null;
		    String winner = "";

		    // Compare the highest card (or kicker) in each tied player's hand
		    for (String player : tiedPlayers) {
		        List<Card> hand = hands.get(player);
		        // Sort hand in descending order (highest card first)
		        hand.sort((card1, card2) -> Integer.compare(card2.getValue(), card1.getValue()));

		        // If this is the first player or their highest card is better, update the winner
		        if (bestHand == null || hand.get(0).getValue() > bestHand.get(0).getValue()) {
		            bestHand = hand;
		            winner = player;
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
	  public class Card implements Serializable{
		  private static final long serialVersionUID = 1L;  // Unique identifier for the Serializable class
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

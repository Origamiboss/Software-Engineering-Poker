package GameClientUI;

import javax.swing.*;

import GameClientUI.Server.Card;
import GameClientUI.Server.PokerHandEvaluator;
import sweProject.GameData;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameClientUI extends JPanel {
    private JLabel potLabel;
    private JLabel[] playerBalanceLabels;
    private JLabel[] playerBetLabels;
    private JLabel[] playerChangedLabels;
    private JLabel[] playerStatusLabels;
    private JLabel[][] playerCardLabels;
    private JLabel[] userCardLabels;
    private JButton[] changeCardButtons;
    private JTextField betAmountField;
    private JLabel userBalanceLabel;
    
    //Vincent's Additions
    private JPanel playerPanel;
    private String username;
    //<Player Username, Player Id>
    public Map<String, Integer> playerIdDictionary;
    private GameClientController gc;
    
    private ArrayList<Integer> cardsToChange;
    
    
    private enum stageOfGame{
    	BUYIN,CHANGE,BET,JUDGE,NONE
    }
    private stageOfGame stage;
    
    public GameClientUI() {
    	stage = stageOfGame.NONE;
        setLayout(new BorderLayout());

        JTextArea console = new JTextArea();
        console.setEditable(false);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        JScrollPane consoleScrollPane = new JScrollPane(console);
        consoleScrollPane.setPreferredSize(new Dimension(400, 800));
        add(consoleScrollPane, BorderLayout.WEST);

        playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(1, 3));

        playerBalanceLabels = new JLabel[3];
        playerBetLabels = new JLabel[3];
        playerChangedLabels = new JLabel[3];
        playerStatusLabels = new JLabel[3];
        playerCardLabels = new JLabel[3][5];

        for (int i = 0; i < 3; i++) {
            JPanel playerView = createPlayerView("Player " + (i + 1), i);
            playerPanel.add(playerView);
        }
        add(playerPanel, BorderLayout.NORTH);

        potLabel = new JLabel("Pot: $0", SwingConstants.CENTER);
        potLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(potLabel, BorderLayout.CENTER);
        add(createUserView(), BorderLayout.SOUTH);
    }

    private JPanel createPlayerView(String playerName, int playerIndex) {
        JPanel playerView = new JPanel();
        playerView.setLayout(new BorderLayout());

        JPanel playerInfo = new JPanel(new GridLayout(4, 1));
        JLabel playerNameLabel = new JLabel(playerName);
        playerBalanceLabels[playerIndex] = new JLabel("Balance: $");
        playerBetLabels[playerIndex] = new JLabel("Bet: $");
        playerChangedLabels[playerIndex] = new JLabel("Cards Changed: ");
        playerStatusLabels[playerIndex] = new JLabel("Status: Active");
        playerInfo.add(playerNameLabel);
        playerInfo.add(playerBalanceLabels[playerIndex]);
        playerInfo.add(playerBetLabels[playerIndex]);
        playerInfo.add(playerChangedLabels[playerIndex]);

        playerView.add(playerInfo, BorderLayout.WEST);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 5));
        for (int i = 0; i < 5; i++) {
            playerCardLabels[playerIndex][i] = createCardLabel("/Cards/back.png", 50, 50);
            cardsPanel.add(playerCardLabels[playerIndex][i]);
        }
        playerView.add(cardsPanel, BorderLayout.CENTER);

        playerView.add(playerStatusLabels[playerIndex], BorderLayout.SOUTH);

        return playerView;
    }

    private JPanel createUserView() {
        JPanel userView = new JPanel();
        userView.setLayout(new BorderLayout());

        JPanel cardsPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        userCardLabels = new JLabel[5];
        
        JPanel cardButtonsPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        changeCardButtons = new JButton[5];

        for (int i = 0; i < 5; i++) {
        	JButton changeCardButton = new JButton("Change");
        	changeCardButton.setActionCommand(String.valueOf(i));
        	changeCardButton.addActionListener(new ChangeCardListener());
        	changeCardButtons[i] = changeCardButton;
        	cardButtonsPanel.add(changeCardButton);
        }
        userView.add(cardButtonsPanel, BorderLayout.NORTH);
        
        for (int i = 0; i < 5; i++) {
            userCardLabels[i] = createCardLabel("/Cards/back.png", 70, 100);
            cardsPanel.add(userCardLabels[i]); 
        }
        userView.add(cardsPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridLayout(2, 4, 10, 10));

        userBalanceLabel = new JLabel("Enter bet or raise amount: ");
        actionPanel.add(userBalanceLabel);

        betAmountField = new JTextField();
        actionPanel.add(betAmountField);

        JButton raiseButton = new JButton("Bet/Raise");
        raiseButton.addActionListener(new BetRaiseButtonListener());
        actionPanel.add(raiseButton);
        
        JButton buyInButton = new JButton("Buy in");
        buyInButton.addActionListener(new BuyInButtonListener());
        actionPanel.add(buyInButton);
        
        userBalanceLabel = new JLabel("Balance: $0", SwingConstants.CENTER);
        actionPanel.add(userBalanceLabel);

        JButton foldButton = new JButton("Fold");
        foldButton.addActionListener(new FoldButtonListener());
        actionPanel.add(foldButton);


        JButton endTurnButton = new JButton("End Turn");
        endTurnButton.addActionListener(new EndTurnButtonListener());
        actionPanel.add(endTurnButton);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new QuitButtonListener());
        actionPanel.add(quitButton);

        userView.add(actionPanel, BorderLayout.SOUTH);

        return userView;
    }

    private JLabel createCardLabel(String imagePath, int width, int height) {
        java.net.URL imgURL = getClass().getResource(imagePath);
        if (imgURL == null) {
            System.out.println("Image not found: " + imagePath);
            return new JLabel("Image not found");
        }

        ImageIcon originalIcon = new ImageIcon(imgURL);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaledImage));
    }

    public void updateUserCard(int cardIndex, String cardImagePath) {
        if (cardIndex >= 0 && cardIndex < userCardLabels.length) {
            userCardLabels[cardIndex].setIcon(createCardLabel(cardImagePath, 70, 100).getIcon());
        }
    }

    public void updateUserBalance(int balance) {
        userBalanceLabel.setText("Balance: $" + balance);
    }

    public void updatePot(String amount) {
        potLabel.setText("Pot: $" + amount);
    }

    public void updatePlayerBalance(int playerIndex, int balance) {
        if (playerIndex >= 0 && playerIndex < playerBalanceLabels.length) {
            playerBalanceLabels[playerIndex].setText("Balance: $" + balance);
        }
    }

    public void updatePlayerBet(int playerIndex, int bet) {
        if (playerIndex >= 0 && playerIndex < playerBetLabels.length) {
            playerBetLabels[playerIndex].setText("Bet: $" + bet);
        }
    }

    public void updatePlayerStatus(int playerIndex, String status) {
        if (playerIndex >= 0 && playerIndex < playerStatusLabels.length) {
            playerStatusLabels[playerIndex].setText("Status: " + status);
        }
    }
    
    public void updatePlayerChanged(int playerIndex, int cardsChanged) {
        if (playerIndex >= 0 && playerIndex < playerChangedLabels.length) {
            playerChangedLabels[playerIndex].setText("Cards Changed: " + cardsChanged);
            //change those card images
            for(int i = 0; i < cardsChanged; i++) {
            	updatePlayerCard(playerIndex,i,"/Cards/black_joker.png");
            }
        }
    }

    public void updatePlayerCard(int playerIndex, int cardIndex, String cardImagePath) {
        if (playerIndex >= 0 && playerIndex < playerCardLabels.length && cardIndex >= 0 && cardIndex < playerCardLabels[playerIndex].length) {
            playerCardLabels[playerIndex][cardIndex].setIcon(createCardLabel(cardImagePath, 50, 50).getIcon());
        }
    }
    
    //Vincent's Additions
    public void setClientUsername(String username) {
    	this.username = username;
    }
    public void setGameClientController(GameClientController gc) {
    	this.gc = gc;
    }
    public void updatePlayerPanel(ArrayList<GameData> players) {
    	
    	// Initialize the playerIdDictionary if it's not initialized
        if (playerIdDictionary == null) {
            playerIdDictionary = new HashMap<>();
        }
    	
    	//reset the playerView
    	playerPanel.removeAll();
    	//Add the players
    	int Ids = 0;
    	for (GameData gd : players) {
    		//only create the panel if it isn't our own
    		if(!gd.getUsername().equals(username)) {
	    		playerIdDictionary.put(gd.getUsername(), Ids);
	            JPanel playerView = createPlayerView(gd.getUsername(), Ids);
	            //edit the stats of the view
	            updatePlayerBalance(Ids, gd.getTotalMoneyAmount());
	            updatePlayerBet(Ids, gd.getBettedMoney());
	            
	            //update the players cards changed
	            updatePlayerChanged(Ids, gd.getCardsSwapped());
	            Ids++;
	            playerPanel.add(playerView);  
    		}else {
    			//if it is me
    			updateUserBalance(gd.getTotalMoneyAmount());
    		}
        }
    	playerPanel.updateUI();
    }
    
    public void buyIn() {
    	stage = stageOfGame.BUYIN;
    }
    public void changeCards() {
    	stage = stageOfGame.CHANGE;
    	cardsToChange = new ArrayList<>();
    }
    public void betIn() {
    	stage = stageOfGame.BET;
    }
    public void judge(String msg) {
    	stage = stageOfGame.JUDGE;
    	//show winning message
    	potLabel.setText(msg);
    }
    public void showAllCards(String u, String[] hand) {
    	
		int id = playerIdDictionary.get(u);
		for(int i = 0; i<hand.length; i++) {
			  //send the specific card data
			  //locate the card path
			  String[] cardData = hand[i].split(",");
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
			  //update card
			  updatePlayerCard(id, i, cardPath);
		}
    }
    private class ChangeCardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	if(stage == stageOfGame.CHANGE) {
	            int cardIndex = Integer.parseInt(e.getActionCommand());
	            // Notify controller or backend to handle card change
	            if(cardsToChange.contains(cardIndex)) {
	            	cardsToChange.remove(cardIndex);
	            }else {
	            	cardsToChange.add(cardIndex);
	            }
        	}
        }
    }

    private class BetRaiseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	//remove BUYIN once button is established
        	if(stage == stageOfGame.BET) {
	            String betAmount = betAmountField.getText();
	            betAmountField.setText(""); // Clear text field
	            gc.bet(Integer.parseInt(betAmount));
	            
        	}
        }
    }
    
    private class BuyInButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // Handle fold action here
            if(stage == stageOfGame.BUYIN) {
            	gc.buyIn();
	            stage = stageOfGame.NONE;
            }
        }
    }

    private class FoldButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	if(stage == stageOfGame.BET) {
	            // Handle fold action here
	            gc.fold();
	            stage = stageOfGame.NONE;
        	}
        }
    }

    private class EndTurnButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle ending turn logic here
            if(stage == stageOfGame.CHANGE && cardsToChange.size() > 0) {
            	//send the list of cards to server
            	//if the changeCards[i] == 1 then it is a card to change
            	//Message should be "Username:Card|1,Card|2"
            	
            	gc.changeCards(cardsToChange);
            	stage = stageOfGame.NONE;
            }else  {
            	stage = stageOfGame.NONE;
            	gc.skip();
            }
           
        }
    }

    private class QuitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle quit logic here
            gc.exit();
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Game Client UI Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            GameClientUI gameClientUI = new GameClientUI();
            frame.add(gameClientUI);
            frame.setSize(1000, 600);
            frame.setVisible(true);

            gameClientUI.updateUserBalance(500);
            gameClientUI.updatePot("250");
            gameClientUI.updatePlayerBalance(0, 300);
            gameClientUI.updatePlayerBet(0, 50);
            gameClientUI.updatePlayerStatus(0, "Folded");
            gameClientUI.updateUserCard(0, "/Cards/ace_of_spades.png");
            gameClientUI.updatePlayerCard(0, 0, "/Cards/king_of_hearts.png");
        });
    }
}



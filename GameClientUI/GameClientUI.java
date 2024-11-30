package GameClientUI;

import javax.swing.*;

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

        JPanel playerInfo = new JPanel(new GridLayout(3, 1));
        JLabel playerNameLabel = new JLabel(playerName);
        playerBalanceLabels[playerIndex] = new JLabel("Balance: $0");
        playerBetLabels[playerIndex] = new JLabel("Bet: $0");
        playerStatusLabels[playerIndex] = new JLabel("Status: Active");
        playerInfo.add(playerNameLabel);
        playerInfo.add(playerBalanceLabels[playerIndex]);
        playerInfo.add(playerBetLabels[playerIndex]);

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

        

        betAmountField = new JTextField();
        actionPanel.add(betAmountField);

        JButton raiseButton = new JButton("Bet/Raise");
        raiseButton.addActionListener(new BetRaiseButtonListener());
        actionPanel.add(raiseButton);
        
        JButton buyInButton = new JButton("Buy in");
        buyInButton.addActionListener(new BuyInButtonListener());
        actionPanel.add(buyInButton);

        JButton foldButton = new JButton("Fold");
        foldButton.addActionListener(new FoldButtonListener());
        actionPanel.add(foldButton);

        userBalanceLabel = new JLabel("Balance: $0", SwingConstants.CENTER);
        actionPanel.add(userBalanceLabel);

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

    public void updatePot(int amount) {
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
    public void judge() {
    	stage = stageOfGame.JUDGE;
    }
    private class ChangeCardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	if(stage == stageOfGame.CHANGE) {
	            int cardIndex = Integer.parseInt(e.getActionCommand());
	            System.out.println("Card " + cardIndex + " change requested");
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
	            System.out.println("Bet/Raise amount: " + betAmount);
	            betAmountField.setText(""); // Clear text field
	            //test
	            stage = stageOfGame.NONE;
        	}
        }
    }
    
    private class BuyInButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // Handle fold action here
            if(stage == stageOfGame.BUYIN) {
            	System.out.println("Player bought in.");
            	gc.buyIn();
	            stage = stageOfGame.NONE;
            }
        }
    }

    private class FoldButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	if(stage == stageOfGame.BET) {
	            System.out.println("Player has folded.");
	            // Handle fold action here
	            
	            stage = stageOfGame.NONE;
        	}
        }
    }

    private class EndTurnButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Player ended turn");
            // Handle ending turn logic here
            if(stage == stageOfGame.CHANGE) {
            	//send the list of cards to server
            	//if the changeCards[i] == 1 then it is a card to change
            	//Message should be "Username:Card|1,Card|2"
            	
            	gc.changeCards(cardsToChange);
            }else if(stage != stageOfGame.NONE) {
            	stage = stageOfGame.NONE;
            	gc.skip();
            }
           
        }
    }

    private class QuitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Player quit the game");
            // Handle quit logic here
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
            gameClientUI.updatePot(250);
            gameClientUI.updatePlayerBalance(0, 300);
            gameClientUI.updatePlayerBet(0, 50);
            gameClientUI.updatePlayerStatus(0, "Folded");
            gameClientUI.updateUserCard(0, "/Cards/ace_of_spades.png");
            gameClientUI.updatePlayerCard(0, 0, "/Cards/king_of_hearts.png");
        });
    }
}



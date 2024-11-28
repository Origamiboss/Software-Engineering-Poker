package GameClientUI;

import javax.swing.*;
import java.awt.*;

public class GameClientUI {
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Poker Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600, 900);

        // Set main layout
        frame.setLayout(new BorderLayout());

        // Console Window (Left Panel)
        JTextArea console = new JTextArea();
        console.setEditable(false);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        JScrollPane consoleScrollPane = new JScrollPane(console);
        consoleScrollPane.setPreferredSize(new Dimension(400, 800)); // Adjust width and height as needed
        frame.add(consoleScrollPane, BorderLayout.WEST);

        // Player Windows (Top Panel)
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(1, 4));
        for (int i = 0; i < 3; i++) {
            playerPanel.add(createPlayerView("Player " + (i + 1)));
        }
        frame.add(playerPanel, BorderLayout.NORTH);

        // Pot (Center Panel)
        JLabel potLabel = new JLabel("Pot: $0", SwingConstants.CENTER);
        potLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(potLabel, BorderLayout.CENTER);

        // User View (Bottom Panel)
        frame.add(createUserView(), BorderLayout.SOUTH);

        // Show the frame
        frame.setVisible(true);
    }

    // Helper method to create a player view
    private static JPanel createPlayerView(String playerName) {
        JPanel playerView = new JPanel();
        playerView.setLayout(new BorderLayout());

        // Player Info
        JPanel playerInfo = new JPanel(new GridLayout(5, 1));
        playerInfo.add(new JLabel("Name: " + playerName));
        playerInfo.add(new JLabel("Balance: $0"));
        playerInfo.add(new JLabel("Folded: False"));
        playerInfo.add(new JLabel("Cards Changed: 0"));
        playerInfo.add(new JLabel("Bet: $0"));

        playerView.add(playerInfo, BorderLayout.NORTH);

        // Facedown Cards (Back of the cards)
        JPanel cardsPanel = new JPanel(new GridLayout(1, 5));
        for (int i = 0; i < 5; i++) {
            // Use the back of the card image for each card
            JLabel cardBack = createCardLabel("/GameClientUI/back.png", 50, 50); // Path to back of the card image
            cardsPanel.add(cardBack);
        }
        playerView.add(cardsPanel, BorderLayout.CENTER);

        return playerView;
    }


    private static JPanel createUserView() {
        JPanel userView = new JPanel();
        userView.setLayout(new BorderLayout());

        // Panel for Cards and Buttons (Center of userView)
        JPanel cardsAndButtonsPanel = new JPanel();
        cardsAndButtonsPanel.setLayout(new BorderLayout());  // Use BorderLayout for internal organization

        // Cards Panel (Top part of cardsAndButtonsPanel)
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new GridLayout(1, 5, 10, 10)); // 1 row, 5 columns, with spacing between elements
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around the grid

        // Card Image Labels (adjust paths to your images)
        JLabel card1 = createCardLabel("/GameClientUI/ace_of_hearts.png", 100, 150);
        JLabel card2 = createCardLabel("/GameClientUI/2_of_hearts.png", 100, 150);
        JLabel card3 = createCardLabel("/GameClientUI/3_of_hearts.png", 100, 150);
        JLabel card4 = createCardLabel("/GameClientUI/4_of_hearts.png", 100, 150);
        JLabel card5 = createCardLabel("/GameClientUI/5_of_hearts.png", 100, 150);

        // Add card images to the cardsPanel
        cardsPanel.add(card1);
        cardsPanel.add(card2);
        cardsPanel.add(card3);
        cardsPanel.add(card4);
        cardsPanel.add(card5);

        // Change Card Buttons Panel (Bottom part of cardsAndButtonsPanel)
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 5, 10, 10)); // 1 row, 5 columns, with spacing between elements

        // Create and size the "Change Card" buttons
        Dimension buttonSize = new Dimension(100, 20); // Width: 100, Height: 40
        JButton changeButton1 = new JButton("Change Card");
        changeButton1.setPreferredSize(buttonSize);
        JButton changeButton2 = new JButton("Change Card");
        changeButton2.setPreferredSize(buttonSize);
        JButton changeButton3 = new JButton("Change Card");
        changeButton3.setPreferredSize(buttonSize);
        JButton changeButton4 = new JButton("Change Card");
        changeButton4.setPreferredSize(buttonSize);
        JButton changeButton5 = new JButton("Change Card");
        changeButton5.setPreferredSize(buttonSize);

        // Add the change card buttons to the buttonsPanel
        buttonsPanel.add(changeButton1);
        buttonsPanel.add(changeButton2);
        buttonsPanel.add(changeButton3);
        buttonsPanel.add(changeButton4);
        buttonsPanel.add(changeButton5);

        // Add both panels (cards and buttons) to the cardsAndButtonsPanel
        cardsAndButtonsPanel.add(cardsPanel, BorderLayout.SOUTH); // Cards at the center
        cardsAndButtonsPanel.add(buttonsPanel, BorderLayout.CENTER); // Buttons at the bottom

        // Add the cardsAndButtonsPanel to the userView in the CENTER region
        userView.add(cardsAndButtonsPanel, BorderLayout.CENTER);

        // User Actions Panel (Bet, Fold, etc.)
        JPanel actionPanel = new JPanel(new GridLayout(2, 3, 10, 10)); // 2 rows, 3 columns
        JLabel balanceLabel = new JLabel("Balance: $0", SwingConstants.CENTER);
        JTextField betField = new JTextField();
        betField.setPreferredSize(new Dimension(100, 20));
        JButton raiseButton = new JButton("Bet/Raise");
        JButton foldButton = new JButton("Fold");
        JButton changeCardsButton = new JButton("Change Cards");
        JButton quitButton = new JButton("Quit");

        actionPanel.add(balanceLabel);
        actionPanel.add(betField);
        actionPanel.add(raiseButton);
        actionPanel.add(foldButton);
        actionPanel.add(changeCardsButton);
        actionPanel.add(quitButton);

        // Add actionPanel to the SOUTH region of userView
        userView.add(actionPanel, BorderLayout.SOUTH);

        return userView;
    }



    // Helper method to create a JLabel with a card image
    // Helper method to create a JLabel with a card image (scaled with custom size)
    private static JLabel createCardLabel(String imagePath, int width, int height) {
        java.net.URL imgURL = GameClientUI.class.getResource(imagePath);
        if (imgURL == null) {
            System.out.println("Image not found: " + imagePath);
            return new JLabel("Image not found"); // Fallback text
        }

        // Load and scale the image
        ImageIcon originalIcon = new ImageIcon(imgURL);
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH); // Resize
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel cardLabel = new JLabel(scaledIcon);
        cardLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the image
        cardLabel.setVerticalAlignment(SwingConstants.CENTER);
        return cardLabel;
    }

}
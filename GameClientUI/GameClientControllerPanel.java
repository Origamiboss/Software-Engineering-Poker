package GameClientUI;

import javax.swing.*;

import sweProject.MainControl;
import sweProject.Player;

import java.awt.CardLayout;
import java.io.IOException;

public class GameClientControllerPanel extends JPanel {
    private MainControl main;
    private GameClientController control;
    private InitialScreenUI initial;
    private GameClientUI clientUI;
    private Player player;
    private String ip;
    private int port;
    public static int sizex = 500;
    public static int sizey = 500;

    public GameClientControllerPanel(MainControl mainControl) {
        main = mainControl;

        initial = new InitialScreenUI(this);
        clientUI = new GameClientUI();
        control = new GameClientController(main, this, initial, clientUI);
        
        //Add our username to the clientUI
        
        
        
        this.setLayout(new CardLayout());
        this.add(initial, "initial");
        this.add(clientUI, "clientUI");

        // Start with initial view
        showInitialScreen();
    }

    public void showInitialScreen() {
        CardLayout layout = (CardLayout) this.getLayout();
        layout.show(this, "initial");
        clientUI.setVisible(false);
    }

    public void showGameScreen() {
        CardLayout layout = (CardLayout) this.getLayout();
        layout.show(this, "clientUI");
        clientUI.setVisible(true);
    }

    public void updateUserBalance(int balance) {
        clientUI.updateUserBalance(balance);
    }
    
    public void changeUserCard(int cardIndex, String newCardImagePath) {
        clientUI.updateUserCard(cardIndex, newCardImagePath);
    }

    public void updatePot(int amount) {
        clientUI.updatePot(amount);
    }

    public void updatePlayerBalance(int playerIndex, int balance) {
        clientUI.updatePlayerBalance(playerIndex, balance);
    }

    public void updatePlayerCard(int playerIndex, int cardIndex, String cardImagePath) {
        clientUI.updatePlayerCard(playerIndex, cardIndex, cardImagePath);
    }
    
    public void updatePlayerBet(int playerIndex, int betAmount) {
        clientUI.updatePlayerBet(playerIndex, betAmount);
    }

    public void updatePlayerStatus(int playerIndex, String folded) {
        clientUI.updatePlayerStatus(playerIndex, folded);
    }

    public void updateCardsChanged(int playerIndex, int cardsChanged) {
        clientUI.updatePlayerBet(playerIndex, cardsChanged); // Adjust label for cards changed.
    }

    public void isHosting(boolean isHosting) {
        initial.isHosting(isHosting);
        control.setGameData(player.getLoginData().getUsername(), player.getWealth());

        try {
            if (isHosting) {
                control.HostGame(port);
            } else {
                control.JoinGame(ip, port);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayer(Player p) {
        player = p;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }
    public void StartGame() {
    	control.StartGame();
    }
    public void exit() {
        control.exit();
        main.openMainPage();
    }
}


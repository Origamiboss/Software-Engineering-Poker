package sweProject;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import GameClientUI.GameClientController;
import GameClientUI.GameClientControllerPanel;
import InitialPageUI.InitialPageController;
import LoginPageUI.LoginController;
import MainPageUI.MainPageController;
import NewAccountUI.NewAccountController;
import NewAccountUI.NewAccountTest;
import ocsf.client.AbstractClient;

public class MainControl extends JFrame{
	
	private Database db;
    private InitialPageController initial;
    private MainPageController main;
    private NewAccountController account;
    private GameClientControllerPanel gameClient;
    private LoginController login;
    private JPanel contentPanel;  // Container to hold the pages
    
    Player player;

    public MainControl() {
		db = new Database();
        initial = new InitialPageController(this);
        main = new MainPageController(this);
        account = new NewAccountController(this, db);
        gameClient = new GameClientControllerPanel(this);
        login = new LoginController(this,db);
        
        // Set up JFrame properties
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setSize(500, 500);

        // Set up content panel with CardLayout
        contentPanel = new JPanel();
        contentPanel.setLayout(new CardLayout());

        // Add pages to content panel
        contentPanel.add(initial, "Initial");
        contentPanel.add(main, "Main");
        contentPanel.add(account, "Account");
        contentPanel.add(gameClient, "GameClient");
        contentPanel.add(login, "Login");
        
        // Add content panel to JFrame
        this.add(contentPanel, BorderLayout.CENTER);

        // Make the frame visible
        this.setVisible(true);

        // Show the initial page
        openInitial();
    }

    // Methods to switch pages
    public void openInitial() {
        showPage("Initial");
    }

    public void openMainPage() {
        showPage("Main");
    }

    public void openNewAccount() {
        showPage("Account");
    }

    public void openGameClient() {
        showPage("GameClient");
    }
    public void openLogin() {
    	showPage("Login");
    }
    // Helper method to switch pages using CardLayout
    private void showPage(String pageName) {
        CardLayout cl = (CardLayout) (contentPanel.getLayout());
        cl.show(contentPanel, pageName);
    }
	
	public void setPlayer(Player p) {
		player = p;
	}
	//Host Game and Create Client
	public void HostGame(int port) {
		openGameClient();
		gameClient.setIp("localHost");
		gameClient.setPort(port);
		gameClient.setPlayer(player);
		gameClient.isHosting(true);
		
		
	}
	
	//Create Client
	public void JoinGame(String ip, int port) {
		openGameClient();
		gameClient.setIp(ip);
		gameClient.setPort(port);
		gameClient.setPlayer(player);
		gameClient.isHosting(false);
		
		
	}
	//Make Sure server exists
	public boolean validIp(String ip, int port) {
		try {
			testClient abs = new testClient(ip,port);
			abs.openConnection();
			//if Connection works set true
			return true;
		}catch(Exception e) {
			return false;
		}
		
	}
	
	public static void main(String[] args) {
		new MainControl();
	}
	private class testClient extends AbstractClient{
		
		public testClient(String ip, int port){
			super(ip,port);
			setHost(ip);
			setPort(port);
		}

		@Override
		protected void handleMessageFromServer(Object arg0) {
			// TODO Auto-generated method stub
			//Does not care about return values
		}
	}
}

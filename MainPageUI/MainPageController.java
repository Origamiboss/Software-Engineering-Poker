package MainPageUI;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ocsf.client.AbstractClient;
import sweProject.Database;
import sweProject.MainControl;
import sweProject.Player;

public class MainPageController extends JPanel {
	private MainControl main;
	
	private MainPageGUI mainpage;
	private HostGameUI hostpage;
	private JoinGameUI joinpage;
	private dataClient client;
	private String hostName;
	static int sizex = 500;
	static int sizey = 500;
	
	public MainPageController(MainControl mainControl, String host){
		this.setSize(sizex,sizey);
		this.main = mainControl;
		hostName = host;
		client = new dataClient(host,1010);
		mainpage = new MainPageGUI(this);
		hostpage = new HostGameUI(this);
		joinpage = new JoinGameUI(this);
		
		this.add(mainpage);
		this.add(hostpage);
		this.add(joinpage);
		
		mainpage.setVisible(true);
		hostpage.setVisible(false);
		joinpage.setVisible(false);
	}
	
	
	
	public void openHostPanel() {
		//reseterror
		hostpage.setErrorText("");
		mainpage.setVisible(false);
		hostpage.setVisible(true);
		joinpage.setVisible(false);
	}
	public void openJoinPanel() {
		//reset error
		joinpage.setErrorText("");
		mainpage.setVisible(false);
		hostpage.setVisible(false);
		joinpage.setVisible(true);
	}
	public void openMainPanel() {
		mainpage.setVisible(true);
		hostpage.setVisible(false);
		joinpage.setVisible(false);
	}
	public void startGameServer(int portNumber) {
		if(String.valueOf(portNumber).length() == 4)
			main.HostGame(portNumber);
		else
			hostpage.setErrorText("Enter a Valid Port Number");
	}
	public void startGameClient(String ipAddress, int portNumber) {
		if(String.valueOf(portNumber).length() != 4)
			joinpage.setErrorText("Enter a Valid Port Number");
		else {
			if(main.validIp(ipAddress,portNumber))
				main.JoinGame(ipAddress,  portNumber);
			else
				joinpage.setErrorText("Invalid Address");
		}
		
	}
	public void logOff() {
		main.setPlayer(null);
		main.openInitial();
	}
	public void removeAccount() {
		// Show a confirmation dialog
        int result = JOptionPane.showConfirmDialog(
                null, // Parent component (null means no parent)
                "Are you sure you want to delete your account?", // Message
                "Confirm Deletion", // Title of the dialog box
                JOptionPane.YES_NO_OPTION, // Options: Yes or No
                JOptionPane.QUESTION_MESSAGE); // Message type

        // Handle the user's response
        if (result == JOptionPane.YES_OPTION) {
            try {
				client.sendToServer(main.getPlayer());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            // Perform the action (e.g., delete the item)
        } 
	}
	private class dataClient extends AbstractClient{

		public dataClient(String host, int port) {
			super(host, port);
			try {
				openConnection();
				sendToServer("Remove");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		protected void handleMessageFromServer(Object arg0) {
			if(arg0 instanceof String) {
				String msg = (String)arg0;
				if(msg.startsWith("Success")) {
					main.openInitial();
				}
			}
		}
		
	}
}

package MainPageUI;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sweProject.Database;
import sweProject.MainControl;

public class MainPageController extends JPanel {
	private MainControl main;
	
	private MainPageGUI mainpage;
	private HostGameUI hostpage;
	private JoinGameUI joinpage;
	private Database db;
	static int sizex = 500;
	static int sizey = 500;
	
	public MainPageController(MainControl mainControl, Database db){
		this.setSize(sizex,sizey);
		this.main = mainControl;
		this.db = db;
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
            db.removePlayer(main.getPlayer());
            main.openInitial();
            // Perform the action (e.g., delete the item)
        } 
	}
}

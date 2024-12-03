package NewAccountUI;

import sweProject.*;

import java.io.IOException;

import javax.swing.JPanel;

import ocsf.client.AbstractClient;


public class NewAccountController extends JPanel {
	private NewAccountUI ui;
	private MainControl main;
	private String hostName;
	private dataClient client;
	static int sizex = 500;
	static int sizey = 500;
	
	public NewAccountController(MainControl mainControl, String host){
		this.main = mainControl;
		ui = new NewAccountUI(this);
		hostName = host;
		client = new dataClient(host,1010);
		this.add(ui);
	}
	
	//verify if the account exists
	public void VerifyAccount(LoginData accountInfo) {
		try {
			client.sendToServer(accountInfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void CreateAccount(LoginData accountInfo) {
		//if the account doesn't exist, create it
		Player newPlayer = new Player();
		newPlayer.setLoginData(accountInfo);
		//set money
		newPlayer.setWealth(500);
		
		try {
			client.sendToServer(newPlayer);
			ui.resetEverything();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//set the newPlayer as out account
		main.setPlayer(newPlayer);
		//Open the Main Page with the account
		main.openMainPage();
	}
	public void PlayerTaken() {
		ui.writeErrorMsg("Account Username is Taken");
	}
	public void openInitialPanel() {
		//ping the controller to open initial page
		main.openInitial();
	}
	private class dataClient extends AbstractClient{

		public dataClient(String host, int port) {
			super(host, port);
			try {
				openConnection();
				sendToServer("Create Account");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		protected void handleMessageFromServer(Object arg0) {
			if(arg0 instanceof String) {
				String msg = (String)arg0;
				if(msg.startsWith("Player doesn't exist")) {
					ui.CreateAccount();
				}
				if(msg.startsWith("Player exists")) {
					PlayerTaken();
				}
			}
		}
		
	}
}

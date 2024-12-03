package LoginPageUI;

import java.io.IOException;

import javax.swing.JPanel;

import ocsf.client.AbstractClient;
import sweProject.Database;
import sweProject.LoginData;
import sweProject.MainControl;
import sweProject.Player;

public class LoginController extends JPanel{
	private LoginUI ui;
	private String hostName;
	private MainControl main;
	private dataClient client;
	
	static int sizex = 500;
	static int sizey = 500;
	
	public LoginController(MainControl mainControl, String host){
		this.main = mainControl;
		ui = new LoginUI(this);
		hostName = host;
		client = new dataClient(host,1010);
		this.add(ui);
	}
	
	//verify if the account exists
	public void VerifyAccount(LoginData accountInfo) {
		
		try {
			client.openConnection();
			client.sendToServer(accountInfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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
				sendToServer("Login");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		protected void handleMessageFromServer(Object arg0) {
			if(arg0 instanceof String) {
				String msg = (String)arg0;
				if(msg.startsWith("Player doesn't exist")) {
					ui.writeErrorMsg("Invalid Login");
				}
			}
			if(arg0 instanceof Player) {
				Player data = (Player)arg0;
				main.setPlayer(data);
				main.openMainPage();
			}
		}
		
	}
}

package NewAccountUI;

import sweProject.*;

import javax.swing.JPanel;


public class NewAccountController extends JPanel {
	private NewAccountUI ui;
	private Database db;
	private MainControl main;
	
	static int sizex = 500;
	static int sizey = 500;
	
	public NewAccountController(MainControl mainControl, Database db){
		this.main = mainControl;
		ui = new NewAccountUI(this);
		this.db = db;
		
		this.add(ui);
	}
	
	//verify if the account exists
	private boolean VerifyAccount(LoginData accountInfo) {
		
		Player result = db.findPlayer(accountInfo.getUsername());
		
		//if there is existing results then return false. Otherwise return true
		if(result != null)
			return false;
		else
			return true;
	}
	
	public void CreateAccount(LoginData accountInfo) {
		
		//if the account already exists, display an error message
		if(!VerifyAccount(accountInfo)) {
			ui.writeErrorMsg("Account Username is Taken");
			return;
		}
		//if the account doesn't exist, create it
		Player newPlayer = new Player();
		newPlayer.setLoginData(accountInfo);
		
		db.addPlayer(newPlayer);
	}
	public void openInitialPanel() {
		//ping the controller to open initial page
		main.openInitial();
	}
}

package LoginPageUI;

import javax.swing.JPanel;

import sweProject.Database;
import sweProject.LoginData;
import sweProject.MainControl;
import sweProject.Player;

public class LoginController extends JPanel{
	private LoginUI ui;
	private Database db;
	private MainControl main;
	
	static int sizex = 500;
	static int sizey = 500;
	
	public LoginController(MainControl mainControl, Database db){
		this.main = mainControl;
		ui = new LoginUI(this);
		this.db = db;
		
		this.add(ui);
	}
	
	//verify if the account exists
	public void VerifyAccount(LoginData accountInfo) {
		
		Player result = db.findPlayerPassworded(accountInfo.getUsername(), accountInfo.getPassword());
		
		//if there is existing results then return false. Otherwise return true
		if(result != null) {
			main.setPlayer(result);
			main.openMainPage();
		}else {
			ui.writeErrorMsg("Invalid Login");
		}
	}
	public void openInitialPanel() {
		//ping the controller to open initial page
		main.openInitial();
	}
}

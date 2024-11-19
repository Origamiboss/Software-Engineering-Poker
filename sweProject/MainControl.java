package sweProject;
import java.awt.BorderLayout;

import javax.swing.JFrame;

import InitialPageUI.InitialPageController;
import MainPageUI.MainPageController;
import NewAccountUI.NewAccountController;
import NewAccountUI.NewAccountTest;

public class MainControl extends JFrame{
	
	InitialPageController initial;
	MainPageController main;
	NewAccountController account;
	Database db;
	
	MainControl(){
		db = new Database();
		initial = new InitialPageController(this);
		main = new MainPageController(this);
		account = new NewAccountController(this, db);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
	    this.setSize(500,500);
		
	    
		this.add(initial);
		this.add(main);
		this.add(account);
		
		this.setVisible(true);
		openInitial();
	}
	public void openInitial() {
		closeAllPages();
		initial.setVisible(true);
	}
	public void openMainPage() {
		closeAllPages();
		main.setVisible(true);
	}
	public void openNewAccount() {
		closeAllPages();
		account.setVisible(true);
	}
	
	private void closeAllPages() {
		initial.setVisible(false);
		main.setVisible(false);
		account.setVisible(false);
	}
	
	public static void main(String[] args) {
		new MainControl();
	}
}

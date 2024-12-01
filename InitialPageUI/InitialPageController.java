package InitialPageUI;

import javax.swing.JPanel;

import sweProject.MainControl;

public class InitialPageController extends JPanel{
	InitialPageUI ui;
	MainControl main;
	static int sizex = 500;
	static int sizey = 500;
	public InitialPageController(MainControl mainControl){
		this.main= mainControl;
		this.setSize(sizex,sizey);
		ui = new InitialPageUI(this);
		this.add(ui);
	}
	public void openLoginPage() {
		main.openLogin();
	}
	public void openNewAccountPage() {
		main.openNewAccount();
	}
}

package Game;

import javax.swing.JPanel;

public class MainPageController extends JPanel {
	
	private MainPageGUI mainpage;
	private HostGameUI hostpage;
	private JoinGameUI joinpage;
	static int sizex = 500;
	static int sizey = 500;
	
	MainPageController(){
		this.setSize(sizex,sizey);
		
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
		mainpage.setVisible(false);
		hostpage.setVisible(true);
		joinpage.setVisible(false);
	}
	public void openJoinPanel() {
		mainpage.setVisible(false);
		hostpage.setVisible(false);
		joinpage.setVisible(true);
	}
	public void openMainPanel() {
		mainpage.setVisible(true);
		hostpage.setVisible(false);
		joinpage.setVisible(false);
	}
	public void startGameServer(String ipAddress, int portNumber) {
		
	}
	public void startGameClient(String ipAddress, int portNumber) {
		
	}
}

package GameClientUI;

import java.io.IOException;

import javax.swing.JPanel;

import sweProject.MainControl;
import sweProject.Player;

public class GameClientControllerPanel extends JPanel{
	private MainControl main;
	GameClientController control;
	InitialScreenUI initial;
	GameClientUI clientUI;
	String ip;
	int port;
	
	private Player player;
	
	static int sizex = 500;
	static int sizey = 500;
	
	public GameClientControllerPanel(MainControl mainControl) {
		main = mainControl;
	
		initial = new InitialScreenUI(this);
		clientUI = new GameClientUI();
		control = new GameClientController(main,initial,clientUI);
		this.add(initial);
		this.add(clientUI);
		//hide clientUI
		clientUI.setVisible(false);
	}
	public void isHosting(boolean t) {
		initial.isHosting(t);
		
		//set up the player data
		control.setGameData(player.getLoginData().getUsername(), player.getWealth());
		
		//let the client and server be created
		if(t) {
			try {
				control.HostGame(port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			control.JoinGame(ip, port);
		}
	}
	public void setPlayer(Player p) {
		player = p;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void exit() {
		control.exit();
		main.openMainPage();
	}
}

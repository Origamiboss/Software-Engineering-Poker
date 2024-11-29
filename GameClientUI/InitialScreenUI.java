package GameClientUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import sweProject.GameData;

public class InitialScreenUI extends JPanel{
	
	private JLabel serverIp;
	
	private JButton back;
	private JButton startGame;
	
	private JPanel PlayerLog;
	
	private GameClientControllerPanel gc;
	
	public InitialScreenUI(GameClientControllerPanel parent) {
		
		gc = parent;
		this.setLayout(new BorderLayout());
		PlayerLog = new JPanel();
		
		this.setPreferredSize(new Dimension(gc.sizex,gc.sizey));
		JPanel holder = new JPanel();
		holder.setPreferredSize(new Dimension(gc.sizex, 100));  // Set an appropriate height
		holder.setLayout(new FlowLayout(FlowLayout.CENTER));
		serverIp = new JLabel("Server Ip: ");
		holder.add(serverIp);
		this.add(holder, BorderLayout.NORTH);
		
		PlayerLog.setLayout(new FlowLayout(FlowLayout.CENTER));  // Center alignment for added components
		PlayerLog.setPreferredSize(new Dimension(gc.sizex, 100));  // Set an appropriate height
		this.add(PlayerLog, BorderLayout.CENTER);
		
		
		holder = new JPanel();
		
		//if you are hosting, add the startGame button, otherwise its just the back button
		back = new JButton("Back");
		startGame = new JButton("Start Game");
		holder.add(back);
		holder.add(startGame);
		this.add(holder, BorderLayout.SOUTH);
		
		startGame.addActionListener(new EventHandler());
		back.addActionListener(new EventHandler());
	}
	public void isHosting(boolean h) {
		startGame.setVisible(h);
	}
	public void updateWhoJoined(ArrayList<GameData> players) {
		//remove everything from PlayerLog
		PlayerLog.removeAll();
		//add the players
		for(GameData gd : players) {
			JLabel label = new JLabel(gd.getUsername());
		    label.setHorizontalAlignment(SwingConstants.CENTER);  // Center the text in the label
		    PlayerLog.add(label);
		}
		this.updateUI();
	}
	public void setServerIp(String ip) {
		serverIp.setText("Server IP: "+ ip);
		this.updateUI();
	}
	private class EventHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == startGame) {
				//start the Game
				gc.StartGame();
			}
			if(e.getSource() == back) {
				//Go back and maybe kill server
				gc.exit();
			}
		}
	}
}


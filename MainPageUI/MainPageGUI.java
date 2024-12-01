package MainPageUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MainPageGUI extends JPanel {
	
	private MainPageController control;
	
	private JButton joinButton;
	private JButton hostButton;
	private JButton escapeButton;
	private JButton removeAccount;
	
	MainPageGUI(MainPageController parent){
		control = parent;
		this.setPreferredSize(new Dimension(control.sizex,control.sizey));
		
		//set up a layout
		this.setLayout(new BorderLayout());
		
		
		//set up the main page
		JPanel holder = new JPanel();
		escapeButton = new JButton("Log Off");
		removeAccount = new JButton("Delete Account");
		holder.add(escapeButton);
		holder.add(removeAccount);
		this.add(holder, BorderLayout.CENTER);
		
		holder = new JPanel();
		joinButton = new JButton("Join Game");
		hostButton = new JButton("Host Game");
		holder.add(joinButton);
		holder.add(hostButton);
		this.add(holder,BorderLayout.NORTH);
		
		
		joinButton.addActionListener(new EventHandler());
		hostButton.addActionListener(new EventHandler());
		escapeButton.addActionListener(new EventHandler());
		removeAccount.addActionListener(new EventHandler());
		
		
	}
	
	private class EventHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == joinButton) {
				//open the join page
				control.openJoinPanel();
			}
			if(e.getSource() == hostButton) {
				//open the host page
				control.openHostPanel();
			}
			if(e.getSource() == escapeButton) {
				//open the escape page
				control.logOff();
			}
			if(e.getSource() == removeAccount) {
				//remove the account
				control.removeAccount();
			}
		}
	}
}

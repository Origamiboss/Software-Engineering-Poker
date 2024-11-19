package InitialPageUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;



public class InitialPageUI extends JPanel{
	private InitialPageController control;
	
	private JButton loginButton;
	private JButton newAccountButton;
	
	InitialPageUI(InitialPageController parent){
		control = parent;
		this.setPreferredSize(new Dimension(control.sizex,control.sizey));
		
		//set up a layout
		this.setLayout(new BorderLayout());
		
		
		JPanel holder = new JPanel();
		loginButton = new JButton("Login");
		newAccountButton = new JButton("New Account");
		holder.add(loginButton);
		holder.add(newAccountButton);
		this.add(holder,BorderLayout.CENTER);
		
		
		loginButton.addActionListener(new EventHandler());
		newAccountButton.addActionListener(new EventHandler());
		
		
	}
	
	private class EventHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == loginButton) {
				//open the login page
				control.openLoginPage();
			}
			if(e.getSource() == newAccountButton) {
				//open the account page
				control.openNewAccountPage();
			}
		}
	}
}

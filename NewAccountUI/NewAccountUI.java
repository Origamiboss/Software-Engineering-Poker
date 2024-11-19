package NewAccountUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class NewAccountUI extends JPanel{
	private NewAccountController controller;
	
	private JTextField username;
	private JPasswordField password;
	private JPasswordField passwordAgain;
	
	private JButton cancel;
	private JButton submit;
	
	NewAccountUI(NewAccountController controller){
		this.controller = controller;
		
		//write the page
		this.setPreferredSize(new Dimension(controller.sizex,controller.sizey));
		
		//set up a layout
		this.setLayout(new BorderLayout());
		
		//set up the text fields and make it in a grid panel
		JPanel holder = new JPanel();
		//make it a grid panel that is 2 X 3
		GridLayout g = new GridLayout();
		g.setRows(3);
		g.setColumns(2);
		holder.setLayout(g);
		
		
		holder.add(new JLabel("Username: "));
		username = new JTextField(16);
		holder.add(username);
		holder.add(new JLabel("Password: "));
		password = new JPasswordField(16);
		holder.add(password);
		holder.add(new JLabel("Password Again: "));
		passwordAgain = new JPasswordField(16);
		holder.add(passwordAgain);
		
		
		
		
		
		this.add(holder, BorderLayout.NORTH);
		
		holder = new JPanel();
		cancel = new JButton("Cancel");
		submit = new JButton("Submit");
		holder.add(cancel);
		holder.add(submit);
		this.add(holder,BorderLayout.CENTER);
		
		
		cancel.addActionListener(new EventHandler());
		submit.addActionListener(new EventHandler());
		
		
		
	}
	
	private class EventHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == cancel) {
				//open the join page
				
			}
			if(e.getSource() == submit) {
				//open the host page
				
			}
		}
	}
		
	
	
	
	
	
	public void writeErrorMsg(String msg) {
		
	}
}

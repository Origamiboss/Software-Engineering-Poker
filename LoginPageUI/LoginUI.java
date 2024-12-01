package LoginPageUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import sweProject.LoginData;

public class LoginUI extends JPanel{
	private LoginController controller;
	
	private JTextField username;
	private JPasswordField password;
	
	private JButton cancel;
	private JButton submit;
	
	private JLabel error;
	
	LoginUI(LoginController controller){
		this.controller = controller;
		
		//write the page
		this.setPreferredSize(new Dimension(controller.sizex/2,controller.sizey/2));
		
		//set up a layout
		this.setLayout(new BorderLayout());
		
		JPanel holder = new JPanel();
		holder.setPreferredSize(new Dimension(controller.sizex, 50));
		//set up error placement
		error = new JLabel(" ");
		error.setForeground(Color.red);
		holder.add(error);
		this.add(holder, BorderLayout.CENTER);
		
		//set up the text fields and make it in a grid panel
		holder = new JPanel();
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
		
		
		
		
		
		this.add(holder, BorderLayout.NORTH);
		
		holder = new JPanel();
		cancel = new JButton("Cancel");
		submit = new JButton("Submit");
		holder.add(cancel);
		holder.add(submit);
		this.add(holder,BorderLayout.SOUTH);
		
		
		cancel.addActionListener(new EventHandler());
		submit.addActionListener(new EventHandler());
		
		
		
	}
	
	private class EventHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == cancel) {
				//reset everything
				writeErrorMsg("");
				username.setText("");
				password.setText("");
				//open the inital page
				controller.openInitialPanel();
			}
			if(e.getSource() == submit) {
				//reset error message
				writeErrorMsg("");
				//make sure data is submitted properly
				if(username.getText().length() > 0 && password.getText().length() >= 6) {
					LoginData data = new LoginData();
					data.setUsername(username.getText());
					data.setPassword(password.getText());
					controller.VerifyAccount(data);
				}else {
					//error occured
					if(username.getText().length() <= 0) {
						writeErrorMsg("A username must be provided.");
					}else if(password.getText().length() < 6) {
						writeErrorMsg("A password must be at least 6 characters.");
					}
				}
			}
		}
	}
		
	
	
	
	
	
	public void writeErrorMsg(String msg) {
		error.setText(msg);
	}
}

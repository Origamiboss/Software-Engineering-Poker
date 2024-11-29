package MainPageUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;



public class JoinGameUI extends JPanel{

	private JTextField ip;
	private JTextField port;
	private JButton join;
	private JButton back;
	private JLabel error;
	
	private MainPageController control;
	JoinGameUI(MainPageController parent){
		control = parent;
		this.setPreferredSize(new Dimension(control.sizex,control.sizey));
		//set up the HostGameUI Layout
		this.setLayout(new BorderLayout());
		
		
		//label this page
		JLabel label = new JLabel("Join Game");
		label.setHorizontalAlignment(SwingConstants.CENTER);  // Center the text horizontally
		this.add(label, BorderLayout.NORTH);
		
		
		//set up a child Panel
		JPanel childPanel = new JPanel();
		//set up the layout
		GridLayout g = new GridLayout();
		g.setColumns(1);
		g.setRows(8);
		childPanel.setLayout(g);
		
		JPanel holder = new JPanel();
		holder.add(new JLabel("Ip Address: "));
		ip = new JTextField(20);
		holder.add(ip);
		childPanel.add(holder);
		
		holder = new JPanel();
		holder.add(new JLabel("Port Number: "));
		port = new JTextField(6);
		holder.add(port);
		childPanel.add(holder);
		
		holder = new JPanel();
		join = new JButton("Join");
		back = new JButton("Back");
		holder.add(join);
		holder.add(back);
		childPanel.add(holder);
		
		//add error text
		holder = new JPanel();
		error = new JLabel();
		error.setForeground(Color.red);
		holder.add(error);
		childPanel.add(holder);
		
		join.addActionListener(new EventHandler());
		back.addActionListener(new EventHandler());
		
		this.add(childPanel, BorderLayout.CENTER);
	}
	
	private class EventHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == join) {
				//start the server
				//get ip and port
				String ipAddress = ip.getText();
				
				//check port number
				int portNumber;
				try {
					portNumber = Integer.parseInt(port.getText());
				}catch(Exception exc) {
					exc.printStackTrace();
					portNumber = 0;
				}
				
				control.startGameClient(ipAddress, portNumber);
			}
			if(e.getSource() == back) {
				//open the main page
				control.openMainPanel();
			}
		}
	}
	public void setErrorText(String text) {
		error.setText(text);
	}
}

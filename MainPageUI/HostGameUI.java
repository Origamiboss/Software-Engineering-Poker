package MainPageUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class HostGameUI extends JPanel {
	
	private MainPageController control;
	
	private JTextField ip;
	private JTextField port;
	private JButton start;
	private JButton back;
	
	HostGameUI(MainPageController parent){
		control = parent;
		this.setPreferredSize(new Dimension(control.sizex,control.sizey));
		//set up the HostGameUI Layout
		this.setLayout(new BorderLayout());
		
		
		//label this page
		JLabel label = new JLabel("Host Game");
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
		start = new JButton("Start");
		back = new JButton("Back");
		holder.add(start);
		holder.add(back);
		childPanel.add(holder);

		start.addActionListener(new EventHandler());
		back.addActionListener(new EventHandler());
		
		this.add(childPanel, BorderLayout.CENTER);
		
	}
	
	
	private class EventHandler implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == start) {
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
				
				control.startGameServer(ipAddress, portNumber);
			}
			if(e.getSource() == back) {
				//open the main page
				control.openMainPanel();
			}
		}
	}
}
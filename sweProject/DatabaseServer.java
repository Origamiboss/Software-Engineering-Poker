package sweProject;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import ocsf.server.*;

public class DatabaseServer extends AbstractServer{
	private Database db;
	ArrayList<ConnectionToClient> createAccount = new ArrayList<>();
	ArrayList<ConnectionToClient> login = new ArrayList<>();
	ArrayList<ConnectionToClient> remove = new ArrayList<>();
	
	public DatabaseServer(){
		super(1234);
		this.setPort(1010);
		db = new Database();
		
	}
	protected void handleMessageFromClient(Object arg0, ConnectionToClient arg1){
		if(arg0 instanceof Player) {
			Player player = (Player)arg0;
			if(createAccount.contains(arg1)) {
				db.addPlayer(player);
			}else if(remove.contains(arg1)) {
				db.removePlayer(player);
				try {
					arg1.sendToClient("Success");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(arg0 instanceof LoginData) {
			LoginData data = (LoginData)arg0;
			//check if player exists
			if(createAccount.contains(arg1)) {
				//check if player exists
				Player result = db.findPlayer(data.getUsername());
				if(result != null) {
					try {
						arg1.sendToClient("Player exists");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					try {
						arg1.sendToClient("Player doesn't exist");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//check login
			else if(login.contains(arg1)) {
				//check if player exists
				Player result = db.findPlayer(data.getUsername());
				if(result != null) {
					try {
						arg1.sendToClient(result);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					try {
						arg1.sendToClient("Player doesn't exist");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		if(arg0 instanceof String) {
			String msg = (String)arg0;
			if(msg.startsWith("Create Account")) {
				createAccount.add(arg1);
			}
			if(msg.startsWith("Login")) {
				login.add(arg1);
			}
			if(msg.startsWith("Remove")) {
				remove.add(arg1);
			}
		}
	}
	public static void main(String[] args) {
		DatabaseServer server = new DatabaseServer();
	    try {
			server.listen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Start listening for incoming connections.
	    
	    JFrame holder = new JFrame();
	    holder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    holder.setSize(300, 200); // Set the frame size if necessary
	    holder.setVisible(true); // Make the frame visible
	}
}

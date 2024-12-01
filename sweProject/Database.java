package sweProject;

import java.util.*;
import java.sql.*;
import java.io.*;

public class Database {
	
	//private data fields
	private Player player;
	private Connection conn;
	
	//constructor
	Database() {
		player = new Player();
		
		// this is from the database.java file we had in lab7out. I included the query function from that file as well
		try {
			//Create a Properties object
			Properties props = new Properties();
			
			//Open a FileINputStream
			FileInputStream fis = new FileInputStream("sweProject/db.properties");
			
			props.load(fis);
			
			String url = props.getProperty("url");
			String user = props.getProperty("user");
			String pass = props.getProperty("password");
			
			//Set the connection
			conn = DriverManager.getConnection(url, user, pass);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean validatePlayer(Player player) {
		//get the username and password from the passed in player
		LoginData ld = player.getLoginData();
		String username = ld.getUsername();
		String password = ld.getPassword();
		
		//get the usernames from the database
		String query = "select username from user";
		ArrayList<String> usernames = this.query(query);
		
		//check if list is empty
		if(usernames.isEmpty()) {
			System.out.println("There are no registered users");
			return false;
		}
		// check if the entered username is not in the database
		else if(!usernames.contains(username)) {
			System.out.println("username or password is incorrect");
			return false;
		}
		else {
			// get the matching password from the database
			query = "select aes_decrypty(password,256) from users where username = '" + username + "'";
			ArrayList<String> pass = this.query(query);
			String actual = pass.getFirst();
			
			// check if the entered password is equal to the password that went with the entered username in the database
			if(password.equals(actual)) {
				return true;
			}
			else {
				System.out.println("username or password is incorrect");
				return false;
			}
		}
		
	}
	
	
	public boolean removePlayer(Player player) throws SQLException {
		//get the login data and the username and password from the player getting removed
		LoginData ld = player.getLoginData();
		String username = ld.getUsername();
		String password = ld.getPassword();
		
		//get all the usernames that are currently in the database
		String query = "select username from users";
		ArrayList<String> list = this.query(query);
		
		// check if the list is empty
		if(list.isEmpty()) {
			System.out.println("user is not registered");
			return false;
		}
		// check if the list does not have the entered username
		else if(!list.contains(username)) {
			System.out.println("user is not registered");
			return false;
		}
		else {
			// get the matching password for the username entered from the database
			query = "select aes_decrypty(password,256) from users where username = '" + username + "'";
			ArrayList<String> pass = this.query(query);
			String actual = pass.getFirst();
			
			if(pass.equals(actual)) {
				String dml = "delete from user where username = '" + username + "'";
				this.executeDML(dml);
				return true;
			}
			else {
				System.out.println("username or password is incorrect");
				return false;
			}
		}
	}
	
	public boolean addPlayer(Player player ) throws SQLException {
		// get the login data and the username from the player attempting to create an account
		LoginData ld = player.getLoginData();
		String username = ld.getUsername();
		String password = ld.getPassword();
		
		// get all the usernames that are currently in the database
		String query = "select username from users";
		ArrayList<String> list = this.query(query);
		
		// check if the username of potential user is in the database
		if(list.contains(username)) {
			System.out.println("username already exists");
			return false;
		}
		// if user is not already in arraylist add them to the database
		else {
			String dml = "insert into users values(" + username + ",aes_encrypt(" + password + ",256))";
			this.executeDML(dml);
			return true;
		}
	}
	
	public Player findPlayer(String username) {
		//I don't really understand what this function is for?
		//I get what it is supposed to do but I don't understand how that is supposed to be helpful
		//also I don't have like an array of players or anything so I would have to make a new player that is the exact same instead of returning
		//an existing player
		
		Player player = new Player();
		return player;
	}
	
	  public ArrayList<String> query(String query)
	  {
	    //Variable Declaration/Initialization
		  ArrayList<String>list = new ArrayList<String>();
		  int count = 0;
		  int noColumns = 0;
		  try {
			  //Create database statement
			 Statement statement = conn.createStatement();
			 
			 ResultSet rs = statement.executeQuery(query);
			 
			 //Get the ResultSetMetaData (# of columns)
			 ResultSetMetaData rmd = rs.getMetaData();
			 
			 //Get the # of columns
			 noColumns = rmd.getColumnCount();
			 
			 while(rs.next()) {
				 String record = "";
				 
				 //Iterate thru each field
				 for(int i = 0; i < noColumns; i++) {
					 record += rs.getString(i+1);
					 record += ",";
				 }
				 list.add(record);
			 }
			 
			 //Check for empty list
			 if(list.size() == 0) {
				 return null;
			 }
		  }
		  catch(Exception e) {
			 e.printStackTrace();
			 return null;
		  }
		  
		  return list;
	  }

	
	public void executeDML(String dml) throws SQLException {
		//Add your code here
		Statement statement = conn.createStatement();
		statement.execute(dml);
	  }  

}

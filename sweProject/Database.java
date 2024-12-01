package sweProject;

import java.util.*;
import java.sql.*;
import java.io.*;

public class Database {
	
	//private data fields
	private Player player;
	private Connection conn;
	
	//constructor
	public Database() {
		player = new Player();
		
		
		// this is from the database.java file we had in lab7out. I included the query function from that file as well
		try {
			conn = connectToFile("sweProject/db.properties");
		}
		catch (FileNotFoundException e) {
		    
		    try {
		    	conn = connectToFile("Software-Engineering-Poker/sweProject/db.properties");
			
		    }catch(Exception ex) {
		    	System.out.println("Error: db.properties file not found");
		    	ex.printStackTrace();
		    }
		} catch (IOException e) {
		    try {
			    conn = connectToFile("Software-Engineering-Poker/sweProject/db.properties");
				
		    }catch(Exception ex) {
		    	System.out.println("Error reading the db.properties file");
		    	ex.printStackTrace();
		    }
		    
		} catch (SQLException e) {
		    System.out.println("Error connecting to the database");
		    e.printStackTrace();
		} catch (Exception e) {
		    System.out.println("Unexpected error");
		    e.printStackTrace();
		}
	}
	private Connection connectToFile(String path) throws Exception {
		Properties props = new Properties();
	    FileInputStream fis = new FileInputStream(path);
	    
	    props.load(fis);
		
		String url = props.getProperty("url");
		String user = props.getProperty("user");
		String pass = props.getProperty("password");
		//Set the connection
		Connection conn = DriverManager.getConnection(url, user, pass);
	    return conn;
	}
	
	public void removePlayer(Player player) {
		//gather data
		LoginData l = player.getLoginData();
		String username = l.getUsername();
		String password = l.getPassword();
		int wealth = player.getWealth();
		try {
			executeDML("DELETE FROM player WHERE username='" +username+"' AND password='" + password + "'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addPlayer(Player player ) {
		//gather data
		LoginData l = player.getLoginData();
		String username = l.getUsername();
		String password = l.getPassword();
		int wealth = player.getWealth();
		try {
			executeDML("INSERT INTO player VALUES('"+username+"','"+password+"',"+wealth+");");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Player findPlayer(String username) {
		//locate the first datafield
		ArrayList<String> returnedData = query("SELECT username, password, wealth FROM player WHERE username = '" + username + "'");
		//check if there is data
		if(returnedData != null) {
			//Create the Player object
			Player player =  new Player();
			String[] playerData = returnedData.get(0).split(",");
			
			LoginData l = new LoginData();
			l.setUsername(playerData[0]);
			l.setPassword(playerData[1]);
			player.setLoginData(l);
			
			player.setWealth(Integer.parseInt(playerData[2]));
			
			return player;
		}else {
			//there is no return data
			return null;
		}
	}
	public Player findPlayerPassworded(String username, String password) {
		//locate the first datafield
		ArrayList<String> returnedData = query("SELECT username, password, wealth FROM player WHERE username = '" + username + "' AND password = '" + password + "'");

		//check if there is data
		if(returnedData != null) {
			//Create the Player object
			Player player =  new Player();
			String[] playerData = returnedData.get(0).split(",");
			
			LoginData l = new LoginData();
			l.setUsername(playerData[0]);
			l.setPassword(playerData[1]);
			player.setLoginData(l);
			
			player.setWealth(Integer.parseInt(playerData[2]));
			
			return player;
		}else {
			//there is no return data
			return null;
		}
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

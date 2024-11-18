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
			FileInputStream fis = new FileInputStream("lab7out/db.properties");
			
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
	
	
	public void removePlayer(Player player) {
		
	}
	
	public void addPlayer(Player player ) {
		
	}
	
	public Player findPlayer(String plair) {
		Player player =  new Player();
		
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

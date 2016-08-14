package fortress.plugin;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.beans.PropertyVetoException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.zaxxer.hikari.HikariDataSource;

import net.md_5.bungee.api.chat.ComponentBuilder;

public class F_SQLEntityDataQueries 
{
	Connection c = null; // for SQLite mode.
	Connection d = null; // for MySQL mode.
	
	UUID cinputUUID;
	String returnUUID;
	
	HikariDataSource rootHikari;
	F_ChatStringBuilder chatbuilder;
	int DBMode;
	
	public F_SQLEntityDataQueries()
	{
		fortress_root rootPlugin =  (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
		rootHikari = rootPlugin.getHikariDataSource();
		DBMode = rootPlugin.getDatabaseType();
		chatbuilder = new F_ChatStringBuilder();
	}
	
	public void CreateDB()
	{
		c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Opened database successfully");

	      stmt = c.createStatement();
	      String sql = "CREATE TABLE COMPANY " +
	                   "(ID INT PRIMARY KEY     NOT NULL," +
	                   " NAME           TEXT    NOT NULL, " + 
	                   " AGE            INT     NOT NULL, " + 
	                   " ADDRESS        CHAR(50), " + 
	                   " SALARY         REAL)"; 
	      stmt.executeUpdate(sql);
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Table created successfully");
	}
	
	
	
	
	
	
	public UUID GetLivingEntityUUID(UUID inputUUID)
	{
		c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Retrieval of Entity's UUID attempted");

	      stmt = c.createStatement();
	      
	      ResultSet rs = stmt.executeQuery("SELECT ID FROM UUID_LOOKUP " +
	                   "WHERE ID = '" + inputUUID + "'");  
	                   //" AGE            INT     NOT NULL, " + 
	                   //" ADDRESS        CHAR(50), " + 
	                   //" SALARY         REAL)"; 
	      
	      System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
	      if (rs.next() == false)
	      {
	    	  System.out.println("No record found. returning null...");
	    	  return null;
	      }
	      String returnUUID = rs.getString("OWNER");
	      cinputUUID = UUID.fromString(returnUUID);
	      stmt.close();
	      c.close();
	      System.out.println("Select Query run successfully");
	      //return cinputUUID;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Select Query run successfully (2)");
	    return cinputUUID;
	    
	}
	
	
	
	public void SetLivingEntityUUID(UUID inputUUID)
	{
		c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Insert into table UUID_LOOKUP attempted...");

	      stmt = c.createStatement();
	      System.out.println("Instantiation of stmt variable successsful...");
	      String rs = "INSERT INTO UUID_LOOKUP (ID) " +
	                   "VALUES ('" + inputUUID + "');";  
	                   //" AGE            INT     NOT NULL, " + 
	                   //" ADDRESS        CHAR(50), " + 
	                   //" SALARY         REAL)"; 
	      stmt.executeUpdate(rs);
	      //System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
	      //String returnUUID = rs.getString("OWNER");
	      //UUID cinputUUID = UUID.fromString(returnUUID);
	      stmt.close();
	      c.close();
	      System.out.println("Insert Query run successfully (1)");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Insert Query run successfully (2)");
	}
	
	
	
	public void CreateLivingEntityUUIDTable()
	{
		c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Table UUID Lookup created");

	      stmt = c.createStatement();
	      String sql = "CREATE TABLE UUID_LOOKUP " +
	                   "(ID UUID PRIMARY KEY     NOT NULL," +
	                   " OWNER UUID)";  
	                   //" AGE            INT     NOT NULL, " + 
	                   //" ADDRESS        CHAR(50), " + 
	                   //" SALARY         REAL)"; 
	      stmt.executeUpdate(sql);
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Table created successfully");
	}
	
	public void CreateGolemEntityTable()
	{
		c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Golem lookup table created.");

	      stmt = c.createStatement();
	      String sql = "CREATE TABLE GOLEM_LOOKUP " +
	                   "(ID INTEGER PRIMARY KEY     NOT NULL," +
	    		       " GOLEMID UUID," +
	                   " GROUPSTRING String," +
	                   " OWNER String)";  
	                   //" AGE            INT     NOT NULL, " + 
	                   //" ADDRESS        CHAR(50), " + 
	                   //" SALARY         REAL)"; 
	      stmt.executeUpdate(sql);
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Golem lookup table created successfully");
	}
	
	public void CreateMineEntityTable()
	{
		c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Golem lookup table created.");

	      stmt = c.createStatement();
	      String sql = "CREATE TABLE MINE_LOOKUP " +
	                   "(ID INTEGER PRIMARY KEY     NOT NULL," +
	    		       " MINEID UUID," +
	                   " GROUPSTRING String," +
	                   " OWNER String)";  
	                   //" AGE            INT     NOT NULL, " + 
	                   //" ADDRESS        CHAR(50), " + 
	                   //" SALARY         REAL)"; 
	      stmt.executeUpdate(sql);
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Mine lookup table created successfully");
	}
	
	
	/*********************************************************************************
	 *   Fortress Entity Get "Player Owned" Functions
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * ******************************************************************************/
	
	
	
	
	public UUID GetPlayerOwnedGolem(String inputUUID) // (SECURED) (MySQL'd) (switch'd) 
	{
		establishConnection();
		//c = null;
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String selectstring;
	    UUID returnLong = null;
	    
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Checking if player own's a golem...");
	      System.out.println("Value of input parameter: " + inputUUID);
	      
	      switch (DBMode)
	      {
	      	case 0:
	      			selectstring = "SELECT GOLEMID FROM GOLEM_LOOKUP " +
		                   "WHERE OWNER = ?";  
		                   //" AGE            INT     NOT NULL, " + 
		                   //" ADDRESS        CHAR(50), " + 
		                   //" SALARY         REAL)"; 
		                   // inputUUID
		      
			      stmt = d.prepareStatement(selectstring);
			      stmt.setString(1, inputUUID);
			      System.out.println("Insert into table GOLEM_LOOKUP attempted (aftercheck)...");
			      break;
	      		

		    case 1:
			     
			      
			      selectstring = "SELECT GOLEMID FROM GOLEM_LOOKUP " +
			                   "WHERE OWNER = ?";  
			                   //" AGE            INT     NOT NULL, " + 
			                   //" ADDRESS        CHAR(50), " + 
			                   //" SALARY         REAL)"; 
			                   // inputUUID
			      
			      stmt = c.prepareStatement(selectstring);
			      stmt.setString(1, inputUUID);
			      System.out.println("Insert into table GOLEM_LOOKUP attempted (aftercheck)...");
			      break;
	      }
	      
	      rs = stmt.executeQuery();
	
	      if (rs.next() == false)
	      {
	    	  System.out.println("No record found. returning null...");
	    	  return null;
	      }
	      returnLong = returnLong.fromString(rs.getString("GOLEMID"));
	      //cinputUUID = UUID.fromString(returnUUID);
	      //cinputUUID = returnUUID;
	      stmt.close();
	      //c.close();
	      System.out.println("Select Query run successfully");
	      //return cinputUUID;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Select Query run successfully (2)");
	    severConnection();
	    return returnLong;
	}
	
	
	public String DoesPlayerOwnMine(UUID inputLong) // (SECURED) (MySQL'd) (switch'd)
	{
		establishConnection();
		//c = null;
		 PreparedStatement stmt = null;
		 ResultSet rs = null;
		 String selectstring;
	    String returnString = "";
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Checking if player own's a mine...");
	      System.out.println("Value of input parameter: " + inputLong);
	      
	      switch (DBMode)
	      {
	      	  case 0:
	      		  
			      
			      selectstring = "SELECT OWNER FROM FORTRESS.MINE_LOOKUP " +
			                   "WHERE MINEID = ?";  
			                   //" AGE            INT     NOT NULL, " + 
			                   //" ADDRESS        CHAR(50), " + 
			                   //" SALARY         REAL)"; 
			      			   // inputLong
			      stmt = d.prepareStatement(selectstring);
			      stmt.setString(1, inputLong.toString());
			      System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
			      break;
		      case 1:
			     
			      
		    	  selectstring = "SELECT OWNER FROM MINE_LOOKUP " +
		                   "WHERE MINEID = ?";   
			                   //" AGE            INT     NOT NULL, " + 
			                   //" ADDRESS        CHAR(50), " + 
			                   //" SALARY         REAL)"; 
		    	  stmt = c.prepareStatement(selectstring);
			      stmt.setString(1, inputLong.toString());
			      
			      System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
			      break;
	      }
	      
	      rs = stmt.executeQuery();
	      
	      System.out.println("aftercheck 1");
	      if (rs.next() == false)
	      {
	    	  System.out.println("No record found. returning null...");
	    	  
	    	  return null;
	      }
	      System.out.println("aftercheck 2");
	      returnString = rs.getString("OWNER");
	      //cinputUUID = UUID.fromString(returnUUID);
	      //cinputUUID = returnUUID;
	      rs.close();
	      stmt.close();
	      //c.close();
	      System.out.println("Select Query run successfully");
	      //return cinputUUID;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Select Query run successfully (2)");
	    severConnection();
	    return returnString;
	}
	
	
	/*********************************************************************************
	 *  Fortress Entity Get "Registered" Functions
	 *  
	 * 
	 * 
	 * 
	 * 
	 * 
	 * ******************************************************************************/
	
	
	
	
	
	public UUID GetRegisteredGolem(F_IronGolem golem) // (SECURED) (MySQL'd) (switch'd)
	{
		//c = null;
		establishConnection();
		PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String selectstring;
	    UUID returnLong = null;
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Checking if player own's a golem...");
	      System.out.println("Value of input parameter (UUID): " + golem.getUniqueID());
	      
	      switch (DBMode)
	      {
	      	  case 0:
		      		  selectstring = "SELECT GOLEMID FROM FORTRESS.GOLEM_LOOKUP " +
			                   "WHERE GOLEMID = ?";  
			                   //" AGE            INT     NOT NULL, " + 
			                   //" ADDRESS        CHAR(50), " + 
			                   //" SALARY         REAL)";
			                   //  golem.getUniqueID() 
				      stmt = d.prepareStatement(selectstring);
				      stmt.setString(1, golem.getUniqueID().toString());
				      System.out.println("Insert into table GOLEM_LOOKUP attempted (aftercheck)...");
			      break;
		      		  
			  case 1:
			      
			      
				      selectstring = "SELECT GOLEMID FROM GOLEM_LOOKUP " +
				                   "WHERE GOLEMID = ?";  
				                   //" AGE            INT     NOT NULL, " + 
				                   //" ADDRESS        CHAR(50), " + 
				                   //" SALARY         REAL)";
				                   //  golem.getUniqueID() 
				      stmt = c.prepareStatement(selectstring);
				      stmt.setString(1, golem.getUniqueID().toString());
				      System.out.println("Insert into table GOLEM_LOOKUP attempted (aftercheck)...");
			      break;
	      }
	      
	      rs = stmt.executeQuery();
	      
	      if (rs.next() == false)
	      {
	    	  System.out.println("No record found. returning null...");
	    	  return null;
	      }
	      returnLong = returnLong.fromString(rs.getString("GOLEMID"));
	      //cinputUUID = UUID.fromString(returnUUID);
	      //cinputUUID = returnUUID;
	      stmt.close();
	      //c.close();
	      System.out.println("Select Query on GOLEM_LOOKUP run successfully");
	      //return cinputUUID;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Select Query on GOLEM_LOOKUP run successfully (2)");
	    severConnection();
	    return returnLong;
	}
	
	
	
	
	
	
	
	
	public UUID GetRegisteredMine(F_ArrowTrap mine) // (SECURED) (MySQL'd) (switch'd)
	{
		establishConnection();
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String selectstring;
	    UUID returnLong = null;
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Checking if mine is owned by a player...");
	      System.out.println("Value of input parameter: " + mine.getUniqueID());
	      
	      switch (DBMode)
	      {
		      case 0:
		      		
		    	    selectstring = "SELECT MINEID FROM FORTRESS.MINE_LOOKUP " +
			                   "WHERE MINEID = ?";  
		      		//rs = stmt.executeQuery("SELECT MINEID FROM FORTRESS.MINE_LOOKUP " +
		                   //"WHERE MINEID = ?");  
		                   //" AGE            INT     NOT NULL, " + 
		                   //" ADDRESS        CHAR(50), " + 
		                   //" SALARY         REAL)"; 
		      		stmt = d.prepareStatement(selectstring);
		      		stmt.setString(1, mine.getUniqueID().toString());
		      		System.out.println("Insert into table MINE_LOOKUP attempted (aftercheck, MySQL)..(mine).");
		      		break;
		      case 1:
		    	  	selectstring = "SELECT MINEID FROM MINE_LOOKUP " +
		                   "WHERE MINEID = ?";  
		      
		      		//rs = stmt.executeQuery("SELECT MINEID FROM MINE_LOOKUP " +
		                   //"WHERE MINEID = ?");  
		                   //" AGE            INT     NOT NULL, " + 
		                   //" ADDRESS        CHAR(50), " + 
		                   //" SALARY         REAL)"; 
		      		stmt = c.prepareStatement(selectstring);
		      		stmt.setString(1, mine.getUniqueID().toString());
		      		System.out.println("Insert into table MINE_LOOKUP attempted (aftercheck)..(mine).");
		      		break;
	      }
	      
	      rs = stmt.executeQuery();
	      	
	    	  if (rs.next() == false)
	    	  {
	    		  //rs.next();
	    		  System.out.println("No record found. returning null...");
	    		  stmt.close();
	    		  severConnection();
	    		  return null;
	    	  }
	      
	      returnLong = returnLong.fromString(rs.getString("MINEID"));
	      //cinputUUID = UUID.fromString(returnUUID);
	      //cinputUUID = returnUUID;
	      rs.close();
	      stmt.close();
	      //c.close();
	      System.out.println("Select Query on MINE_LOOKUP run successfully");
	      //return cinputUUID;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Select Query on MINE_LOOKUP run successfully (2)");
	    severConnection();
	    return returnLong;
	}
	
	
	public UUID GetRegisteredSensor(F_Sensor sensor) // (SECURED) (MySQL'd) (switch'd)
	{
			establishConnection();
		    Statement stmt = null;
		    UUID returnLong = null;
		    PreparedStatement prepStmt = null;
		    String selectstring;
		    ResultSet rs = null;
		    try {
		      /*Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:test.db");
		      System.out.println("Checking if mine is owned by a player...");
		      System.out.println("Value of input parameter: " + sensor.getUniqueID());
		      stmt = c.createStatement();
		      
		      ResultSet rs = stmt.executeQuery("SELECT UUID FROM SENSOR_LOOKUP " +
		                   "WHERE UUID = '" + sensor.getUniqueID() + "'");  
		                   //" AGE            INT     NOT NULL, " + 
		                   //" ADDRESS        CHAR(50), " + 
		                   //" SALARY         REAL)"; 
		      
		      System.out.println("Insert into table SENSOR_LOOKUP attempted (aftercheck)...(sensor)");
		
		      if (rs.next() == false)
		      {
		    	  System.out.println("No record found. returning null...");
		    	  return null;
		      }
		      returnLong = returnLong.fromString(rs.getString("UUID"));
		      //cinputUUID = UUID.fromString(returnUUID);
		      //cinputUUID = returnUUID;
		      stmt.close();
		      c.close();
		      System.out.println("Select Query on MINE_LOOKUP run successfully");
		      //return cinputUUID;*/
		    	
		    	switch (DBMode)
			      {
			      	case 0:
						      selectstring = "SELECT UUID FROM FORTRESS.SENSOR_LOOKUP " +
						    		  				 "WHERE UUID = ?";
						      
						      //PreparedStatement prepStmt2 = null;
						      
						      
						    	  prepStmt = d.prepareStatement(selectstring);	
							      prepStmt.setString(1,  sensor.getUniqueID().toString());
							      rs = prepStmt.executeQuery();
							      if (rs.next() == false)
							      {
							    	  System.out.println("No record found. returning null...");
							    	  severConnection();
							    	  return null;
							      }
							      returnLong = returnLong.fromString(rs.getString("UUID"));
						      
						      
						      System.out.println("MySQL Fortress lookup successful! (select player from fortress member lookup");
						      
						      //stmt.executeUpdate(sql);
					      break;
			      	case 1: 
				      			  selectstring= "SELECT UUID FROM SENSOR_LOOKUP " +
		    		  				 "WHERE UUID = ?";
					      
							      prepStmt = c.prepareStatement(selectstring);
							      prepStmt.setString(1,  sensor.getUniqueID().toString());
							      //prepStmt.setString(2,  fortressname);
							      rs = prepStmt.executeQuery();
							      if (rs.next() == false)
							      {
							    	  System.out.println("No record found. returning null...");
							    	  severConnection();
							    	  return null;
							      }
							      returnLong = returnLong.fromString(rs.getString("UUID"));
						  break;
			      		
			      } 
		    	
		    	
		    	
		    	
		    	
		    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		    }
		    System.out.println("Select Query on MINE_LOOKUP run successfully (2)");
		    severConnection();
		    return returnLong;
	}
	
	
	
	
	/*********************************************************************************
	 *  Fortress Entity Register Functions
	 * 
	 * 
	 * 
	 * 
	 * 
	 * ******************************************************************************/
	
	
	
	public void RegisterGolem(F_IronGolem golem) // (SECURED) (MySQL'd) (switch'd)
	{
		//c = null;
		establishConnection();
		PreparedStatement prepStmt = null;
		String selectstring;
	    ResultSet rs;
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("New Golem registered.");

	      
	      System.out.println("Instantiation of stmt variable successsful...");
	      
	      switch (DBMode)
	      {
	      case 0:
		      selectstring = "INSERT INTO FORTRESS.GOLEM_LOOKUP (GOLEMID) " +
		                   "VALUES (?);";  
		      prepStmt = d.prepareStatement(selectstring);
		                   //" AGE            INT     NOT NULL, " + 
		                   //" ADDRESS        CHAR(50), " + 
		                   //" SALARY         REAL)"; 
		      			   // golem.getUniqueID()
		      prepStmt.setString(1, golem.getUniqueID().toString());
		      break;
	      case 1:
		      selectstring = "INSERT INTO GOLEM_LOOKUP (GOLEMID) " +
		                   "VALUES (?);";  
		      prepStmt = c.prepareStatement(selectstring);
		                   //" AGE            INT     NOT NULL, " + 
		                   //" ADDRESS        CHAR(50), " + 
		                   //" SALARY         REAL)"; 
		      			   // golem.getUniqueID()
		      prepStmt.setString(1, golem.getUniqueID().toString());
		      break;
	      }
	      
	      
	      
	      prepStmt.executeUpdate();
	      //prepStmt.executeUpdate(rs);
	      //System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
	      //String returnUUID = rs.getString("OWNER");
	      //UUID cinputUUID = UUID.fromString(returnUUID);
	      prepStmt.close();
	      //c.close();
	      System.out.println("Register Golem query run successfully (1)");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    severConnection();
	    System.out.println("Register Golem query run successfully (2)");
	}
	
	
	
	public void updateSensorMode(F_Sensor sensor)
	{
		//c = null;
				establishConnection();
				PreparedStatement prepStmt = null;
				String selectstring;
			    ResultSet rs;
			    try {
			      //Class.forName("org.sqlite.JDBC");
			      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
			      System.out.println("Updating sensor mode...");

			      
			      //System.out.println("Instantiation of stmt variable successsful...");
			      
			      switch (DBMode)
			      {
			      case 0:
				      selectstring = "UPDATE FORTRESS.SENSOR_LOOKUP " +
			                       "SET SENSOR_MODE = ? " +
				                   "WHERE " +
			                       "UUID = ? " +
				                   "AND SENSOR_NAME = ? " +
			                       "AND SENSOR_FORTRESS = ? "
				                   ;  
				      prepStmt = d.prepareStatement(selectstring);
				                   //" AGE            INT     NOT NULL, " + 
				                   //" ADDRESS        CHAR(50), " + 
				                   //" SALARY         REAL)"; 
				      			   // golem.getUniqueID()
				      switch (sensor.SensorMode)
				      {
				      	case 0:
				      		prepStmt.setString(1, "Friendly");
				      		break;
				      	case 1:
				      		prepStmt.setString(1, "Hostile");
				      		break;
				      	case 2:
				      		prepStmt.setString(1, "Neutral");
				      		break;
				      }
				      prepStmt.setString(2, sensor.getUniqueID().toString());
				      prepStmt.setString(3, sensor.getCustomName());
				      prepStmt.setString(4, sensor.FortressAlign);
				      
				      break;
			      case 1:
				      selectstring = "UPDATE SENSOR_LOOKUP " +
			                       "SET SENSOR_MODE = ? " +
				                   "WHERE " +
			                       "UUID = ? " +
				                   "AND SENSOR_NAME = ? " +
			                       "AND SENSOR_FORTRESS = ? "
				                   ;  
				      prepStmt = c.prepareStatement(selectstring);
				                   //" AGE            INT     NOT NULL, " + 
				                   //" ADDRESS        CHAR(50), " + 
				                   //" SALARY         REAL)"; 
				      			   // golem.getUniqueID()
				      switch (sensor.SensorMode)
				      {
				      	case 0:
				      		prepStmt.setString(1, "Friendly");
				      		break;
				      	case 1:
				      		prepStmt.setString(1, "Hostile");
				      		break;
				      	case 2:
				      		prepStmt.setString(1, "Neutral");
				      		break;
				      }
				      prepStmt.setString(2, sensor.getUniqueID().toString());
				      prepStmt.setString(3, sensor.getCustomName());
				      prepStmt.setString(4, sensor.FortressAlign);
				      
				      break;
			      }
			      
			      
			      
			      prepStmt.executeUpdate();
			      //prepStmt.executeUpdate(rs);
			      //System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
			      //String returnUUID = rs.getString("OWNER");
			      //UUID cinputUUID = UUID.fromString(returnUUID);
			      prepStmt.close();
			      //c.close();
			      System.out.println("Sensor mode update query run successfully (1)");
			    } catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      System.exit(0);
			    }
			    severConnection();
			    System.out.println("Sensor mode update query run successfully (2)");
	}
	
	
	
	
	
	public void RegisterMine(F_ArrowTrap mine) // (SECURED) (MySQL'd) (switch'd)
	{
		establishConnection();
		String rs = null;
	    Statement stmt = null;
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("New Mine registered.");
	      switch (DBMode)
	      {
	      case 0:
	    	  stmt = d.createStatement();
		      System.out.println("Instantiation of stmt variable successsful... (MySQL)");
		      rs = "INSERT INTO FORTRESS.MINE_LOOKUP (MINEID) " +
		                   "VALUES ('" + mine.getUniqueID() + "');";  
		                   //" AGE            INT     NOT NULL, " + 
		                   //" ADDRESS        CHAR(50), " + 
		                   //" SALARY         REAL)";
		      break;
	    	  
	      case 1:
		      stmt = c.createStatement();
		      System.out.println("Instantiation of stmt variable successsful...");
		      rs = "INSERT INTO MINE_LOOKUP (MINEID) " +
		                   "VALUES ('" + mine.getUniqueID() + "');";  
		                   //" AGE            INT     NOT NULL, " + 
		                   //" ADDRESS        CHAR(50), " + 
		                   //" SALARY         REAL)";
		      break;
	      }
	      
	      
	      stmt.executeUpdate(rs);
	      //System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
	      //String returnUUID = rs.getString("OWNER");
	      //UUID cinputUUID = UUID.fromString(returnUUID);
	      stmt.close();
	      //c.close();
	      System.out.println("Register Mine query run successfully (1)");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Register Mine query run successfully (2)");
	    severConnection();
	}
	
	
	
	public void updateSensorMode(String fortress, String sensorname, String sensormode, Player sender)
	{
		establishConnection();
		PreparedStatement prepStmt = null;
		String selectstring, modetoset;
		modetoset = null;
		ComponentBuilder successstring = null;
	    ResultSet rs;
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Updating sensor mode...");

	      
	      //System.out.println("Instantiation of stmt variable successsful...");
	      if (sender != null ? checkSensorMembership(fortress, sensorname, sender) == true : true) // this check will allow the console sender to send commands.
	      {
		      switch (DBMode)
		      {
			      case 0:
				      selectstring = "UPDATE FORTRESS.SENSOR_LOOKUP " +
			                       "SET SENSOR_MODE = ? " +
				                   "WHERE " +
				                   "SENSOR_NAME = ? " +
			                       "AND SENSOR_FORTRESS = ? "
				                   ;  
				      prepStmt = d.prepareStatement(selectstring);
				                   //" AGE            INT     NOT NULL, " + 
				                   //" ADDRESS        CHAR(50), " + 
				                   //" SALARY         REAL)"; 
				      			   // golem.getUniqueID()
				      switch (sensormode)
				      {
				      	case "F":
				      		prepStmt.setString(1, "Friendly");
				      		modetoset = "F";
				      		successstring = chatbuilder.returnSensorModeUpdateSuccess(sensorname, fortress, "Friendly");
				      		break;
				      	case "H":
				      		prepStmt.setString(1, "Hostile");
				      		successstring = chatbuilder.returnSensorModeUpdateSuccess(sensorname, fortress, "Hostile");
				      		modetoset = "H";
				      		break;
				      	case "N":
				      		prepStmt.setString(1, "Neutral");
				      		successstring = chatbuilder.returnSensorModeUpdateSuccess(sensorname, fortress, "Neutral");
				      		modetoset = "N";
				      		break;
				      }
				      prepStmt.setString(2, sensorname);
				      prepStmt.setString(3, fortress);
				      
				      
				      
				      break;
			      case 1:
				      selectstring = "UPDATE SENSOR_LOOKUP " +
			                       "SET SENSOR_MODE = ? " +
				                   "WHERE " +
				                   "SENSOR_NAME = ? " +
			                       "AND SENSOR_FORTRESS = ? "
				                   ;  
				      prepStmt = c.prepareStatement(selectstring);
				                   //" AGE            INT     NOT NULL, " + 
				                   //" ADDRESS        CHAR(50), " + 
				                   //" SALARY         REAL)"; 
				      			   // golem.getUniqueID()
				      switch (sensormode)
				      {
				      	case "F":
				      		prepStmt.setString(1, "Friendly");
				      		modetoset = "F";
				      		successstring = chatbuilder.returnSensorModeUpdateSuccess(sensorname, fortress, "Friendly");
				      		break;
				      	case "H":
				      		prepStmt.setString(1, "Hostile");
				      		successstring = chatbuilder.returnSensorModeUpdateSuccess(sensorname, fortress, "Hostile");
				      		modetoset = "H";
				      		break;
				      	case "N":
				      		prepStmt.setString(1, "Neutral");
				      		successstring = chatbuilder.returnSensorModeUpdateSuccess(sensorname, fortress, "Neutral");
				      		modetoset = "N";
				      		break;
				      }
				      prepStmt.setString(2, sensorname);
				      prepStmt.setString(3, fortress);
				      
				      break;
		      }
	      
		      // successful return statement goes here.
		      if (sender != null)
		      {
		    	  sender.spigot().sendMessage(successstring.create()); // send message only if player is online.
		      }
		      prepStmt.executeUpdate();
	      }
	      fortress_root rootPluginRet =  (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
		  HashMap<String, F_DBFortressData> DBFortRef= rootPluginRet.getFortressDataHash();
		  if (DBFortRef.get(fortress) != null)
		  {
			  if (DBFortRef.get(fortress).getSensorHash().containsKey(sensorname))
				{
				  	System.out.println("Sensor name discovered. (2)");
					F_Sensor retrievedsensor = (F_Sensor) DBFortRef.get(fortress).getSensorHash().get(sensorname);		
					retrievedsensor.setTargetingMode(modetoset);
					// remove from the owning fortress' hashmap (entity despawned) -- only do this if its not null.
				}
		  }
	      
	      
	      //prepStmt.executeUpdate(rs);
	      //System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
	      //String returnUUID = rs.getString("OWNER");
	      //UUID cinputUUID = UUID.fromString(returnUUID);
		  if (prepStmt != null)
		  {
			  prepStmt.close();
		  }
		  if (prepStmt == null)
		  {
			  System.out.println("Sensor doesn't exist in that fortress...");
		  }
	      //c.close();
	      System.out.println("Sensor mode update query (command line) run successfully (1)");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    severConnection();
	    System.out.println("Sensor mode update query (command line) run successfully (2)");
	}
	/*********************************************************************************
	 *  Fortress Entity Assignment Functions
	 * 
	 * 
	 * 
	 * 
	 * 
	 * ******************************************************************************/
	
	public void RegisterSensor(F_Sensor sensor) // (SECURED) (MySQL'd) (switch'd)
	{
		c = null;
		establishConnection();
	    PreparedStatement prepStmt = null;
	    try {
			     /* Class.forName("org.sqlite.JDBC");
			      c = DriverManager.getConnection("jdbc:sqlite:test.db");
			      System.out.println("New Sensor registered.");
		
			      stmt = c.createStatement();
			      System.out.println("Instantiation of stmt variable successsful...");
			      String rs = "INSERT INTO SENSOR_LOOKUP (UUID) " +
			                   "VALUES ('" + sensor.getUniqueID() + "');";  
			                   //" AGE            INT     NOT NULL, " + 
			                   //" ADDRESS        CHAR(50), " + 
			                   //" SALARY         REAL)"; 
			      stmt.executeUpdate(rs);
			      //System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
			      //String returnUUID = rs.getString("OWNER");
			      //UUID cinputUUID = UUID.fromString(returnUUID);
			      stmt.close();
			      c.close();
			      System.out.println("Register Sensor query run successfully (1)");*/
	    		switch (DBMode)
		    	{
					case 0:
						  String selectstring2 = "INSERT INTO FORTRESS.SENSOR_LOOKUP (UUID) " +
				  				                 "VALUES (?)";
						  //d = rootHikari.getConnection();
						  prepStmt = d.prepareStatement(selectstring2);	  
						  prepStmt.setString(1, sensor.getUniqueID().toString());
						  //prepStmt.setString(2, sensorUUID.toString());
						  prepStmt.executeUpdate();
						  prepStmt.close();
						  System.out.println("MySQL Fortress lookup successful! (select from sensor lookup) ");
						  //stmt.executeUpdate(sql);
					  break;
					case 1:
					
					      Class.forName("org.sqlite.JDBC");
					      c = DriverManager.getConnection("jdbc:sqlite:test.db");
					      System.out.println("Opened database successfully (checking if entry exists in sensor_table.)");
					      
					      //System.out.println("FORT NAME: " + fortressname);
					      //System.out.println("SENSOR NAME: " + );
					      
					      
					      
					      
					      
					      String selectstring = 
					    		  "INSERT INTO SENSOR_LOOKUP (UUID) " +
			  				                 "VALUES (?)";
					    		  		
					      
					      //stmt.executeUpdate(sql);
					      prepStmt = c.prepareStatement(selectstring);
					      prepStmt.setString(1, sensor.getUniqueID().toString());
					      //prepStmt.setString(2, sensorUUID.toString());
					      prepStmt.executeQuery();
					      prepStmt.close();
				      break;
		    	}
	    	
	    	
	    	
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Register Sensor query run successfully (2)");
	    severConnection();
	}
	
	
	
	public void AssignGolemToPlayer(F_IronGolem golem, String player) // (SECURED) (MySQL'd) (switch'd)
	{
		//c = null;
	    //Statement stmt = null;
		establishConnection();
		String selectstring;
	    PreparedStatement prepStmt = null;
	    
	    fortress_root fortplugin = (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
	    //rootPlugin.getPlayerFortressPrivs().get(player).getFortressAlignment();
	    
	    F_DBFortressData forttochange = fortplugin.getFortressDataHash().get(fortplugin.getPlayerFortressPrivs().get(player).getFortressAlignment()); // get the fort that is referenced by player's current alignment
	    
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Attempting to assign golem to player...(debug)");

	      //stmt = c.createStatement();
	      System.out.println("Instantiation of stmt variable successsful...");
	      
	      
	      
	      switch(DBMode)
	      {
			  case 0:
				      selectstring = "UPDATE FORTRESS.GOLEM_LOOKUP " +
				                  "SET OWNER = ?, " +
				    		      "GOLEM_FORTRESS = ?, " +
				    		      "GOLEM_NAME = ? " +
				                  "WHERE " +
				                  "GOLEMID = ? ";
				                   //"VALUES (" + inputLong + ");";  
				                   //" AGE            INT     NOT NULL, " + 
				                   //" ADDRESS        CHAR(50), " + 
				                   //" SALARY         REAL)"; 
				      //stmt.executeUpdate(rs);
				      
				      prepStmt = d.prepareStatement(selectstring);
				      //prepStmt.setString(1,  verifiedplayer.toString());
				      prepStmt.setString(1,  player);
				      prepStmt.setString(2, fortplugin.getPlayerFortressPrivs().get(player).getFortressAlignment());
				      prepStmt.setString(3, "golem_" + forttochange.returnCurrentGolemID());
				      
				      golem.setGolemFortressGroup(fortplugin.getPlayerFortressPrivs().get(player).getFortressAlignment());
				      golem.setFortressGolemName("golem_" + forttochange.returnCurrentGolemID());
				      
				      forttochange.getGolemHash().put("golem_" + forttochange.returnCurrentGolemID(), golem);
				      // 1.	update the next golem name counter in fortress lookup
				      // 2. (in golem.java) load custom name from DB on entity NBT load. (done)
				      
				      prepStmt.setString(4, golem.getUniqueID().toString());
				      prepStmt.executeUpdate();
				      
				      selectstring = "UPDATE FORTRESS.FORTRESS_LOOKUP SET LAST_GOLEM_ID = LAST_GOLEM_ID + 1 " +
			    		  	   "WHERE FORTRESS = ?";
				      
				      prepStmt = d.prepareStatement(selectstring);
				      prepStmt.setString(1, fortplugin.getPlayerFortressPrivs().get(player).getFortressAlignment());
				      prepStmt.executeUpdate();
				      
			      break;
		      case 1:
				      selectstring = "UPDATE GOLEM_LOOKUP " +
				                  "SET OWNER = ?, " +
				                  "GOLEM_FORTRESS = ?, " +
				    		      "GOLEM_NAME = ? " +
				                  "WHERE " +
				                  "GOLEMID = ? ";
				                   //"VALUES (" + inputLong + ");";  
				                   //" AGE            INT     NOT NULL, " + 
				                   //" ADDRESS        CHAR(50), " + 
				                   //" SALARY         REAL)"; 
				      //stmt.executeUpdate(rs);
				      
				      prepStmt = c.prepareStatement(selectstring);
				      //prepStmt.setString(1,  verifiedplayer.toString());
				      prepStmt.setString(1,  player);
				      prepStmt.setString(2, fortplugin.getPlayerFortressPrivs().get(player).getFortressAlignment());
				      prepStmt.setString(3, "golem_" + forttochange.returnCurrentGolemID());
				     
				      golem.setGolemFortressGroup(fortplugin.getPlayerFortressPrivs().get(player).getFortressAlignment());
				      golem.setFortressGolemName("golem_" + forttochange.returnCurrentGolemID());
				      
				      forttochange.getGolemHash().put("golem_" + forttochange.returnCurrentGolemID(), golem);
				      // 1.	update the next golem name counter in fortress lookup
				      // 2. (in golem.java) load custom name from DB on entity NBT load. (done)
				      
				      prepStmt.setString(4, golem.getUniqueID().toString());
				      prepStmt.executeUpdate();
				      
				      selectstring = "UPDATE FORTRESS.FORTRESS_LOOKUP SET LAST_GOLEM_ID = LAST_GOLEM_ID + 1 " +
			    		  	   "WHERE FORTRESS = ?";
				      
				      prepStmt = c.prepareStatement(selectstring);
				      prepStmt.setString(1, fortplugin.getPlayerFortressPrivs().get(player).getFortressAlignment());
				      prepStmt.executeUpdate();
			      break;
	      }
	      
	      
	      prepStmt.close();
	      
	      
	      
	      //System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
	      //String returnUUID = rs.getString("OWNER");
	      //UUID cinputUUID = UUID.fromString(returnUUID);
	      //stmt.close();
	      //c.close();
	      System.out.println("Assign golem query run successfully. (1)");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    severConnection();
	    System.out.println("Assign golem query run successfully. (2)");
	}
	
	
	
	public void AssignMineToPlayer(UUID inputLong, String fortressname, String player)
	{
		establishConnection();
	    // Statement stmt = null;
	    PreparedStatement prepStmt = null;
	    String selectstring;
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Attempting to assign mine to player...");

	      //stmt = c.createStatement();
	      System.out.println("Instantiation of stmt variable successsful...");
	      
	      switch (DBMode)
	      {
		      case 0:
		    	  selectstring = "UPDATE FORTRESS.MINE_LOOKUP " +
		                  "SET OWNER = ?, " +
		    		      "MINE_FORTRESS = ? " +
		                  "WHERE " +
		                  "MINEID = ? ";
		                   //"VALUES (" + inputLong + ");";  
		                   //" AGE            INT     NOT NULL, " + 
		                   //" ADDRESS        CHAR(50), " + 
		                   //" SALARY         REAL)"; 
			      //stmt.executeUpdate(selectstring);
			      
			      prepStmt = d.prepareStatement(selectstring);
			      //prepStmt.setString(1,  verifiedplayer.toString());
			      prepStmt.setString(1,  player);
			      prepStmt.setString(2,  fortressname);
			      prepStmt.setString(3, inputLong.toString());
		    	  break;
		      case 1:
			      selectstring = "UPDATE MINE_LOOKUP " +
			                  "SET OWNER = ?, " +
			    		      "MINE_FORTRESS = ? " +
			                  "WHERE " +
			                  "MINEID = ? ";
			                   //"VALUES (" + inputLong + ");";  
			                   //" AGE            INT     NOT NULL, " + 
			                   //" ADDRESS        CHAR(50), " + 
			                   //" SALARY         REAL)"; 
			      //stmt.executeUpdate(selectstring);
			      
			      prepStmt = c.prepareStatement(selectstring);
			      //prepStmt.setString(1,  verifiedplayer.toString());
			      prepStmt.setString(1,  player);
			      prepStmt.setString(2,  fortressname);
			      prepStmt.setString(3, inputLong.toString());
			      break;
		      
	      }
	      
	      prepStmt.executeUpdate();
	      
	      prepStmt.close();
	      //System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
	      //String returnUUID = rs.getString("OWNER");
	      //UUID cinputUUID = UUID.fromString(returnUUID);
	      //stmt.close();
	      //c.close();
	      System.out.println("Assign mine query run successfully. (1)");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Assign mine query run successfully. (2)");
	    severConnection();
	}
	
	public F_DataRowSensor retrieveSensorData(String fortressname, UUID sensorUUID) // (SECURED) (MySQL'd) (switch'd)
	{
		establishConnection(); // call method to establish connection
		//c = null;
	    PreparedStatement prepStmt = null;
	    F_DataRowSensor returnrow = new F_DataRowSensor();
	    ResultSet rs = null;
		try {
			
			switch (DBMode)
			{
				case 0:
					  String selectstring2 = "SELECT SENSOR_NAME, SENSOR_MODE from FORTRESS.SENSOR_LOOKUP " +
   		  				 "WHERE " +
   		  				 "SENSOR_FORTRESS = ? AND " +
   		  				 "UUID = ?";
					  //d = rootHikari.getConnection();
					  prepStmt = d.prepareStatement(selectstring2);	  
					  prepStmt.setString(1, fortressname);
					  prepStmt.setString(2, sensorUUID.toString());
					  rs = prepStmt.executeQuery();
					  System.out.println("MySQL Fortress lookup successful! (select from sensor lookup) ");
					  //stmt.executeUpdate(sql);
				  break;
				case 1:
				
				      Class.forName("org.sqlite.JDBC");
				      c = DriverManager.getConnection("jdbc:sqlite:test.db");
				      System.out.println("Opened database successfully (checking if entry exists in sensor_table.)");
				      
				      //System.out.println("FORT NAME: " + fortressname);
				      //System.out.println("SENSOR NAME: " + );
				      
				      
				      
				      
				      
				      String selectstring = 
				    		  		"SELECT SENSOR_NAME, SENSOR_MODE FROM SENSOR_LOOKUP " +
				    		  		"WHERE " +
				    		  		"SENSOR_FORTRESS = ? AND " +
				    		  		"UUID = ?"
				    		  		;
				      
				      //stmt.executeUpdate(sql);
				      prepStmt = c.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      prepStmt.setString(2, sensorUUID.toString());
				      rs = prepStmt.executeQuery();
			      break;
			}  

		      
		      
		      
		      //SQLServTest();
		      
		      
		      
		      
		      
		      
		      if (rs.isBeforeFirst())
		      {
		    	  while (rs.next())
		    	  {
		    		  //System.out.println("SENSOR ADDED TO LIST.");
		    		  //returnlist.add(rs.getString("SENSOR_NAME"));
		    		  returnrow.name = rs.getString("SENSOR_NAME");
		    		  returnrow.mode = rs.getString("SENSOR_MODE");
		    		  prepStmt.close();
		    		  rs.close();
		    		  severConnection();
		    		  System.out.println("SENSOR ENTRY FOUND. ");
		    		  return returnrow;
		    	  }
		      }
		      //prepStmt.close();  


		      //c.close();
		      //return returnlist;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		try{
			prepStmt.close();
		} catch (Exception e)
		{
		  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		  System.exit(0);
		}
		
  	  	severConnection();
		return null;
	}
	
	public boolean checkSensorMembership(String fortressname, String sensorname, Player sender)
	{
		//establishConnection(); // call method to establish connection
		//c = null;
	    PreparedStatement prepStmt = null;
	    F_DataRowSensor returnrow = new F_DataRowSensor();
	    ResultSet rs = null;
		try {
			
			switch (DBMode)
			{
				case 0:
					  String selectstring2 = "SELECT SENSOR_NAME from FORTRESS.SENSOR_LOOKUP " +
   		  				 "WHERE " +
   		  				 "SENSOR_FORTRESS = ? AND " +
   		  				 "SENSOR_NAME = ?";
					  //d = rootHikari.getConnection();
					  prepStmt = d.prepareStatement(selectstring2);	  
					  prepStmt.setString(1, fortressname);
					  prepStmt.setString(2, sensorname);
					  rs = prepStmt.executeQuery();
					  System.out.println("MySQL Fortress lookup successful! (select from sensor lookup) ");
					  //stmt.executeUpdate(sql);
				  break;
				case 1:
				
				      Class.forName("org.sqlite.JDBC");
				      c = DriverManager.getConnection("jdbc:sqlite:test.db");
				      System.out.println("Opened database successfully (checking if entry exists in sensor_table.)");
				      
				      //System.out.println("FORT NAME: " + fortressname);
				      //System.out.println("SENSOR NAME: " + );
				      
				      
				      
				      
				      
				      String selectstring = 
				    		  		"SELECT SENSOR_NAME FROM SENSOR_LOOKUP " +
				    		  		"WHERE " +
				    		  		"SENSOR_FORTRESS = ? AND " +
				    		  		"SENSOR_NAME = ?"
				    		  		;
				      
				      //stmt.executeUpdate(sql);
				      prepStmt = c.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      prepStmt.setString(2, sensorname);
				      rs = prepStmt.executeQuery();
			      break;
			}  

		      
		      
		      
		      //SQLServTest();
		      
		      
		      
		      
		      
			  //System.out.println("Opened database successfully (checking if entry exists in sensor_table.) (1)...");
			  if (rs != null)
			  {
				  //System.out.println("Opened database successfully (checking if entry exists in sensor_table.) (2)...");
			      if (rs.isBeforeFirst() == false)
			      {
			    	  //System.out.println("Opened database successfully (checking if entry exists in sensor_table.) (3)");
			    	  /*while (rs.next())
			    	  {
			    		  //System.out.println("SENSOR ADDED TO LIST.");
			    		  //returnlist.add(rs.getString("SENSOR_NAME"));
			    		  //returnrow.name = rs.getString("SENSOR_NAME");
			    		  //returnrow.mode = rs.getString("SENSOR_MODE");
			    		  prepStmt.close();
			    		  rs.close();
			    		  //severConnection();
			    		  System.out.println("SENSOR ENTRY FOUND, using sensor's name and fortress name.");
			    		  return true;
			    	  }*/
			    	  ComponentBuilder spigotstring = chatbuilder.returnNonExistentSensor(sensorname, fortressname);
			    	  sender.spigot().sendMessage(spigotstring.create());
			    	  rs.close();
			    	  prepStmt.close();
			    	  return false;
			      }   else
			      {
			    	  rs.close();
			    	  prepStmt.close();
			    	  return true;
			      }
			  }
		      //prepStmt.close();  


		      //c.close();
		      //return returnlist;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		try{
			prepStmt.close();
		} catch (Exception e)
		{
		  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		  System.exit(0);
		}
		
  	  	//severConnection();
		return false;
	}
	
	public F_DataRowGolem retrieveGolemData(String fortressname, UUID golemUUID)
	{
		establishConnection();
		PreparedStatement prepStmt = null;
	    F_DataRowGolem returnrow = new F_DataRowGolem();
	    ResultSet rs = null;
	    
	    try {
			
			switch (DBMode)
			{
				case 0:
					  String selectstring2 = "SELECT GOLEM_NAME from FORTRESS.GOLEM_LOOKUP " +
   		  				 "WHERE " +
   		  				 "GOLEM_FORTRESS = ? AND " +
   		  				 "GOLEMID = ?";
					  //d = rootHikari.getConnection();
					  prepStmt = d.prepareStatement(selectstring2);	  
					  prepStmt.setString(1, fortressname);
					  prepStmt.setString(2, golemUUID.toString());
					  rs = prepStmt.executeQuery();
					  System.out.println("MySQL Fortress lookup successful! (select from golem lookup) (MySQL) ");
					  //stmt.executeUpdate(sql);
				  break;
				case 1:
				
				      Class.forName("org.sqlite.JDBC");
				      c = DriverManager.getConnection("jdbc:sqlite:test.db");
				      System.out.println("Opened database successfully (checking if entry exists in sensor_table.)");
				      
				      //System.out.println("FORT NAME: " + fortressname);
				      //System.out.println("SENSOR NAME: " + );
				      
				      
				      
				      
				      
				      String selectstring = 
				    		  		"SELECT GOLEM_NAME FROM GOLEM_LOOKUP " +
				    		  		"WHERE " +
				    		  		"GOLEM_FORTRESS = ? AND " +
				    		  		"GOLEMID = ?"
				    		  		;
				      
				      //stmt.executeUpdate(sql);
				      prepStmt = c.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      prepStmt.setString(2, golemUUID.toString());
				      rs = prepStmt.executeQuery();
			      break;
			}  

		      
		      
		      
		      //SQLServTest();
		      
		      
		      
		      
		      
		      
		      if (rs.isBeforeFirst())
		      {
		    	  while (rs.next())
		    	  {
		    		  //System.out.println("SENSOR ADDED TO LIST.");
		    		  //returnlist.add(rs.getString("SENSOR_NAME"));
		    		  returnrow.name = rs.getString("GOLEM_NAME");
		    		  prepStmt.close();
		    		  rs.close();
		    		  severConnection();
		    		  System.out.println("GOLEM ENTRY FOUND. ");
		    		  return returnrow;
		    	  }
		      }
		      //prepStmt.close();  


		      //c.close();
		      //return returnlist;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		try{
			prepStmt.close();
		} catch (Exception e)
		{
		  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		  System.exit(0);
		}
	    
	    
		severConnection();
		return null;
	}
	
	
	
	public void establishConnection() // (MySQL'd) (switch'd)

    {
    	switch(DBMode)
    	{
    		case 0:
    				try{
    					d = rootHikari.getConnection();
    				} catch ( SQLException e)
    				{
    					System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					    System.exit(0);
    				}
    			
    		    break;
	    	case 1:
				    try{
				    		Class.forName("org.sqlite.JDBC");
							c = DriverManager.getConnection("jdbc:sqlite:test.db");
					} catch ( Exception e ) 
					{
					      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					      System.exit(0);
					}
			    break;
			    
		    
	    }
    	
    	
    	//return true;
    }
	
	
	public void severConnection() // (MySQL'd) (switch'd)
    {
    	switch(DBMode)
    	{
    		case 0:
    				if (d != null)
    				{
    					try{
    						d.close();
    					} catch (SQLException e)
    					{
    						System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    					    System.exit(0);
    					}
    				}
    			break;
    		case 1:
				    try{
							c.close();
					} catch ( Exception e ) 
					{
					      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					      System.exit(0);
					}
				break;
		//return true;
    	}
    }
}

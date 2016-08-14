package fortress.plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_9_R1.Item;
import net.minecraft.server.v1_9_R1.MinecraftKey;
import net.minecraft.server.v1_9_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.NBTTagList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.zaxxer.hikari.HikariDataSource;

public class F_SQLFortressDataQueries {
	
	Connection c = null; // for SQLite connections.
	Connection d = null; // for MySQL connections.
	HikariDataSource rootHikari;
	int DBMode;
	ResultSet rs;
	
	F_SQLFortressDataQueries()
	{
		fortress_root rootPlugin =  (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
		rootHikari = rootPlugin.getHikariDataSource();
		DBMode = rootPlugin.getDatabaseType();
	}
	
	public void createFortressHashmapOnEnable(HashMap<String, F_DBFortressData> fortpermsref) // (SECURED) (MySQL'd) (switch'd)
	{
		// !! don't forget to close RS and statements !!
		establishConnection();
		Connection connToPass = null;
	    Statement stmt = null;
	    try {
	    	switch (DBMode)
			  {
			  	case 0:
			  		  stmt = d.createStatement();
			  	
			  		  rs = stmt.executeQuery("SELECT FORTRESS, HEART_WORLD FROM FORTRESS.FORTRESS_LOOKUP");
			  		  connToPass = d;
			  		System.out.println("Getting fortresses for hashmap load attempted (MySQL)...");
			  	
			  		break;
			  	case 1:
				      //Class.forName("org.sqlite.JDBC");
				      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
				      System.out.println("Loading fortress hashmap...(on Enable)");
				      //System.out.println("Value of input parameter: " + inputLong);
				      stmt = c.createStatement();
				      
				      rs = stmt.executeQuery("SELECT FORTRESS, HEART_WORLD FROM FORTRESS_LOOKUP "); 
				                   //"WHERE MINEID = '" + inputLong + "'");  
				                   //" AGE            INT     NOT NULL, " + 
				                   //" ADDRESS        CHAR(50), " + 
				                   //" SALARY         REAL)"; 
				      
				      System.out.println("Getting fortresses for hashmap load attempted (aftercheck)...");
				    break;
			  }
	      if (rs.next() == false)
	      {
	    	  System.out.println("No record found. returning null...");
	      }	  else
	      {
	    	  System.out.println("FORTRESS LOADED: " + rs.getString("FORTRESS"));
	    	  fortpermsref.put(rs.getString("FORTRESS"), new F_DBFortressData(rs.getString("FORTRESS")));
	    	  loadFortressHeartItems(rs.getString("FORTRESS"), fortpermsref, connToPass);
	    	  while ( rs.next() )
	    	  {
	    		  System.out.println("FORTRESS LOADED: " + rs.getString("FORTRESS"));
	    		  fortpermsref.put(rs.getString("FORTRESS"), new F_DBFortressData(rs.getString("FORTRESS")));
	    		  fortpermsref.get(rs.getString("FORTRESS")).setFortWorld(rs.getString("HEART_WORLD"));
	    		  loadFortressHeartItems(rs.getString("FORTRESS"), fortpermsref, connToPass);
	    	  }
	      }
	      
	      System.out.println("Select Query run successfully (debug)");
	      
	      
	      //returnString = rs.getString("OWNER");
	      
	      
	      
	      
	      
	      //cinputUUID = UUID.fromString(returnUUID);
	      //cinputUUID = returnUUID;
	      stmt.close();
	      //c.close();to
	      System.out.println("Select Query run successfully");
	      //return cinputUUID;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Select Query run successfully (2)");
	    severConnection();
	}
	
	public void createFortressHeartItemIndex(String fortressname) // (SECURED)
	{
		//Connection c = null;
		establishConnection();
		String rs;
	    PreparedStatement prepStmt = null;
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	    	
	      	
	      System.out.println("Creation of fortress heart item index attempted...");

	      
	      System.out.println("Instantiation of stmt variable successsful...");
	      for (int r = 0; r < 6; r++)
	      {
	    	  for (int s = 0; s < 9; s++)
	    	  {
			      switch (DBMode)
			      {
			        case 0:
			        	rs = "INSERT INTO FORTRESS.FORTRESS_HEART_ITEMS (FORTRESS, ITEM_ROW, ITEM_ROW_SLOT) " +
				                   "VALUES (?, ?, ?);";  
			        	prepStmt = d.prepareStatement(rs);
	    		  		break;
			      	case 1:
			      
	    		  		rs = "INSERT INTO FORTRESS_HEART_ITEMS (FORTRESS, ITEM_ROW, ITEM_ROW_SLOT) " +
			                   "VALUES (?, ?, ?);";  
			                   //" AGE            INT     NOT NULL, " + 
			                   //" ADDRESS        CHAR(50), " + 
			                   //" SALARY         REAL)"; 
			      
	    		  		prepStmt = c.prepareStatement(rs);
	    		  		break;
			      	
			      }
			      prepStmt.setString(1, fortressname);
			      prepStmt.setInt(2, r);
			      prepStmt.setInt(3, s);
			      prepStmt.executeUpdate();
			      prepStmt.close();
			     
	    	  }
	      }
	      //System.out.println("Insert into table UUID_LOOKUP attempted (aftercheck)...");
	      //String returnUUID = rs.getString("OWNER");
	      //UUID cinputUUID = UUID.fromString(returnUUID);

	      //c.close();
	      System.out.println("Insert Query run successfully (1)");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    severConnection();
	    System.out.println("Insert Query run successfully (2)");
	}
	
	public void updateFortressHeartItems(String fortressname, HashMap<String, F_DBFortressData> fortdataref) // (SECURED) (MySQL'd) (switch'd)
	{
		Connection heartconn = null;
		establishConnection();
	    PreparedStatement prepStmt = null;
		try {
			  
		      int slotcounter = 0;
		      
		      switch (DBMode)
		      {
		      	  case 0:
		      		  	heartconn = d;
		      		  
		      		  
                    break;		    	  
			      case 1:
			    	  	Class.forName("org.sqlite.JDBC");
			    	  	heartconn = DriverManager.getConnection("jdbc:sqlite:test.db");
			    	  	System.out.println("Attempting to force fortres heart item update...");
		    	  	break;
		      }

		      System.out.println("Instantiation of stmt variable successful...");
		      
		      
		      for (int r = 0; r < 6; r++)
		      {
		    	  for (int s = 0; s < 9; s++)
		    	  {
		    		   	  ItemStack stacktocheck = fortdataref.get(fortressname).getFortressInventory().getItem(slotcounter);
		    		   	  //stacktocheck.getTypeId();
		    		   	  if (stacktocheck != null)
		    		   	  {
		    		   		  ItemMeta metacheck = stacktocheck.getItemMeta();
		    		   		  System.out.println("ITEM STACK NAME (new): " + stacktocheck.getType() + ", " + stacktocheck.getData().getData());
		
		    		   		  
		    		   		  String rs = "UPDATE FORTRESS_HEART_ITEMS " +
		    		   				  "SET ITEM_ID = ?, " +
		    		   				  "ITEM_SUBTYPE = ?, " +
		    		   				"ITEM_COUNT = ?, " +
		    		   				"ITEM_NBT = ? " +
		    		   		
		    		  	      "WHERE FORTRESS = ? " +
		    		  	      "AND ITEM_ROW= ? " +
		    		  	      "AND ITEM_ROW_SLOT= ?" ;
		    		   		  
		    		   		System.out.println("debug 1");
		    		   		  prepStmt = heartconn.prepareStatement(rs);
		    		   		  prepStmt.setInt(1, stacktocheck.getTypeId());
		    		   		  prepStmt.setInt(2, stacktocheck.getData().getData());
		    		   		  prepStmt.setInt(3, stacktocheck.getAmount());
		    		   		  prepStmt.setString(4, toBase64(stacktocheck));

		    		   		  
		    		   		  prepStmt.setString(5, fortressname);
		    		   		System.out.println("debug 2");
		    		   		  prepStmt.setInt(6, r);
		    		   		  prepStmt.setInt(7, s);
		    		   		  prepStmt.executeUpdate();
		    		   		System.out.println("debug 3");
		    		   		  
		    		   		  //stmt.executeUpdate(rs);
		    		   	  }
		    		  	  
		    		   	  if (stacktocheck == null)
		    		   	  {
		    		   		
		    		   		
		    		   		  String rs = "UPDATE FORTRESS_HEART_ITEMS " +
		    		   				  "SET ITEM_ID = null, " + 
		    		   				  "ITEM_SUBTYPE = null, " +
		    		   				"ITEM_COUNT = null " +
		    		  	      "WHERE FORTRESS = ? " +
		    		  	      "AND ITEM_ROW= ? " +
		    		  	      "AND ITEM_ROW_SLOT= ? " ;
		    		   		  
		    		   		  prepStmt = heartconn.prepareStatement(rs);
		    		   		  prepStmt.setString(1, fortressname);
		    		   		  prepStmt.setInt(2, r);
		    		   		  prepStmt.setInt(3, s);
		    		   		  prepStmt.executeUpdate();
		    		   		  
		    		   		  
		    		   	  }
		    		   	  
		    		   	  
		                  
		                  slotcounter++;
		    		  	  
		    	  }
		      }
		      
		      // new code
		      
		      

	          
		      severConnection();
		      System.out.println("Update alignment query run successfully... (updated row)");
		      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	}
	
	public void loadFortressHeartItems(String fortressname, HashMap<String, F_DBFortressData> fortdataref, Connection conn) // (SECURED) (MySQL'd) (switch'd)
	{
		Connection newconn = null;
	    PreparedStatement prepStmt = null;
	    
	    int slotcounter = 0;
	    String selectstring;
	    Integer currID;
	    ResultSet rs = null;
		boolean hadHeartIndex = false;
		
		//Inventory newInventory = Bukkit.createInventory(null, 54, fortressname);
		Inventory fortInventory = fortdataref.get(fortressname).getFortressInventory();
		
		if (fortInventory == null)
		{
			fortInventory = Bukkit.createInventory(null, 54, fortressname);
			fortdataref.get(fortressname).setFortressInventory(fortInventory);
		}
		
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      //System.out.println("Loading fortress hashmap...");
	      //System.out.println("Value of input parameter: " + inputLong);
	      switch (DBMode)
	      {
	        case 0:
	        	for (int r = 0; r < 6; r++)
			      {	
	        	
		        	  selectstring = "SELECT ITEM_ID, ITEM_SUBTYPE, ITEM_COUNT, ITEM_NBT FROM FORTRESS.FORTRESS_HEART_ITEMS "
					      		+ "WHERE " 
					      		+ "FORTRESS = ? " 
					      		+ "AND ITEM_ROW=? ";
		        	  
		        	  newconn = rootHikari.getConnection(); 
					  prepStmt = newconn.prepareStatement(selectstring);
					  prepStmt.setString(1, fortressname);
					  prepStmt.setInt(2, r);
					  //System.out.println("MySQL Debug...(2)");
					  rs = prepStmt.executeQuery();
					  if (rs.isBeforeFirst())
					  {
						  //System.out.println("debug test");
						  for (int s = 0; s < 9; s++)
				    	  {
							  //System.out.println("test");
	
								  rs.next();
								  //ItemStack createdStack = new ItemStack(Material.getMaterial(rs.getInt("ITEM_ID")), rs.getInt("ITEM_COUNT"), (byte)rs.getInt("ITEM_SUBTYPE"));
								  if (rs.getString("ITEM_NBT") != null)
								  {
									  ItemStack createdStack = fromBase64(rs.getString("ITEM_NBT"));
									  fortInventory.clear(slotcounter);
									  fortInventory.setItem(slotcounter, createdStack);
								  }
								  slotcounter++;
							  
				    	  }
						  rs.close();
					  } else
					  {
						  if (!rs.isBeforeFirst() && hadHeartIndex == false)
					      {
					    	  //System.out.println("No Heart index exists for fortress " + fortressname + ".");
					    	  rs.close();
					      }
						  
					  }
					  //rs.close();
					  prepStmt.close();
					  newconn.close();
					  //System.out.println("MySQL Fortress heart load successful. ");
					  
				  
			      }
				  break;
	      	case 1:
			      selectstring = "SELECT ITEM_ID, ITEM_SUBTYPE, ITEM_COUNT FROM FORTRESS_HEART_ITEMS "
			      		+ "WHERE " 
			      		+ "FORTRESS = ? " 
			      		+ " "; 
			                   //"WHERE MINEID = '" + inputLong + "'");  
			                   //" AGE            INT     NOT NULL, " + 
			                   //" ADDRESS        CHAR(50), " + 
			                   //" SALARY         REAL)"; 
			      prepStmt = c.prepareStatement(selectstring);
			      prepStmt.setString(1, fortressname);
			      rs = prepStmt.executeQuery();
			      //System.out.println("Getting fortresses for hashmap load attempted (aftercheck)...(loadFortressHeartItems)");
			      break;
	      }
	      
	      
	      /*if (rs.isBeforeFirst())
	      {
	    	  hadHeartIndex = true;
	    	  System.out.println("Fortress Heart data for " + fortressname + ": ");
		      for (int r = 0; r < 6; r++)
		      {
		    	  for (int s = 0; s < 9; s++)
		    	  {
		    		  if (rs.next() != false)
		    		  {
			    		  currID = rs.getInt("ITEM_ID");
			    		  //System.out.println(rs.getInt("ITEM_ID"));
			    		  if (currID != 0 )
			    		  {
			    			  
			    			  
			    			  
			    			  ItemStack createdStack = new ItemStack(Material.getMaterial(rs.getInt("ITEM_ID")), rs.getInt("ITEM_COUNT"), (byte)rs.getInt("ITEM_SUBTYPE"));
			    			  
			    			  
			    			  System.out.println("Values: " + rs.getInt("ITEM_ID") + ", " + rs.getByte("ITEM_SUBTYPE") + ", " + createdStack.getData());
			    			  
			    			  fortInventory.clear(slotcounter);
			    			  fortInventory.setItem(slotcounter, createdStack);
			    		  }
			    		  
			    		  if (currID == 0)
			    		  {
			    			  fortInventory.clear(slotcounter);
			    		  }
			    		  
			    		  //rs.next();
			    		  slotcounter++;
		    		  }
		    		  //System.out.println("test");
		    	  }
		   
		      }
		      fortdataref.get(fortressname).setFortHadHeart(true);
		      prepStmt.close();
		      rs.close();
		      System.out.println("Load Complete.");
	      }
	      
	      else {
	      if (!rs.isBeforeFirst() && hadHeartIndex == false)
	      {
	    	  System.out.println("No Heart index exists for fortress " + fortressname + ".");
	    	  rs.close();
	      }
	      }*/
	    

	      
	      /*if (rs.next() == false)
	      {
	    	  System.out.println("No record found. returning null...");
	      }	  else
	      {
	    	  System.out.println("FORTRESS LOADED: " + rs.getString("FORTRESS"));
	    	  
	    	  while ( rs.next() )
	    	  {
	    		  System.out.println("FORTRESS LOADED: " + rs.getString("FORTRESS"));
	    		  
	    	  }
	      }*/
	      
	      
	      
	      
	      //returnString = rs.getString("OWNER");
	      
	      
	      
	      
	      
	      //cinputUUID = UUID.fromString(returnUUID);
	      //cinputUUID = returnUUID;
	      //prepStmt.close();
	      //newconn.close();
	      //System.out.println("Select Query run successfully");
	      //return cinputUUID;
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    //System.out.println("Select Query run successfully (2)");
	}

	public void loadMaxDefenseIDs(String fortressname, F_DBFortressData datatoset)  // (SECURED) (MySQL'd) (switch'd)
	{
		establishConnection();
		ResultSet rs = null;
		PreparedStatement prepStmt = null;
		String selectstring;
		try {
			  switch (DBMode)
			  {
			  	case 0:
			  		selectstring = 
    		  		"SELECT LAST_SENSOR_ID, LAST_ARROW_ID, LAST_GOLEM_ID FROM FORTRESS_LOOKUP " +
    		  		"WHERE " +
    		  		"FORTRESS = ?";
    		  		
      
				      //stmt.executeUpdate(sql);
				      prepStmt = d.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      rs = prepStmt.executeQuery();
				      if (rs.isBeforeFirst())
				      {
				    	  rs.next();
				    	  datatoset.nextSensorID = rs.getInt("LAST_SENSOR_ID");
				    	  datatoset.nextArrowID = rs.getInt("LAST_ARROW_ID");
				    	  datatoset.nextGolemID = rs.getInt("LAST_GOLEM_ID");
				      }
			  		  rs.close();
			  		  prepStmt.close();
			  	
			  	  break;
			  	case 1:

				      System.out.println("Opened database successfully (retrieving player's membership list)");
				      selectstring = 
				    		  		"SELECT LAST_SENSOR_ID, LAST_ARROW_ID, LAST_GOLEM_ID FROM FORTRESS_LOOKUP " +
				    		  		"WHERE " +
				    		  		"FORTRESS = ?";
				    		  		
				      
				      //stmt.executeUpdate(sql);
				      prepStmt = c.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      rs = prepStmt.executeQuery();
				      if (rs.isBeforeFirst())
				      {
				    	  rs.next();
				    	  datatoset.nextSensorID = rs.getInt("LAST_SENSOR_ID");
				    	  datatoset.nextArrowID = rs.getInt("LAST_ARROW_ID");
				    	  datatoset.nextGolemID = rs.getInt("LAST_GOLEM_ID");
				      }
				      rs.close();
				      prepStmt.close();
				  break;
			  }
		   
		      
		      
		      
		        

	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		severConnection();
	}
	
	public void createNewSensor(String fortressname, F_Sensor sensor, F_DBFortressData fortdata)   // (SECURED) (MySQL'd) (switch'd)
	{
		establishConnection();
	    PreparedStatement prepStmt = null;
	    
	    // insert into the sensor look up table.
	    try {
			     /* Class.forName("org.sqlite.JDBC");
			      c = DriverManager.getConnection("jdbc:sqlite:test.db");
			      System.out.println("Creation of fortress heart item index attempted...");
		
		
			      
			      
			      
			      System.out.println("Attempting insert into SENSOR_LOOKUP...");
			    		  
			      String rs = "INSERT INTO SENSOR_LOOKUP (UUID, SENSOR_NAME, SENSOR_FORTRESS) " +
		                  "VALUES (?, ?, ?)";  
		                  //" AGE            INT     NOT NULL, " + 
		                  //" ADDRESS        CHAR(50), " + 
		                  //" SALARY         REAL)"; 
		
			      //prepStmt = null;
			      prepStmt = c.prepareStatement(rs);
		
			      prepStmt.setString(1, sensor.getUniqueID().toString());
			      prepStmt.setString(2, "sensor_" + fortdata.nextSensorID);
			      prepStmt.setString(3, fortressname);
			      prepStmt.executeUpdate();
			      
				  
			      
			      
			      sensor.setFortressSensorName("sensor_" + fortdata.nextSensorID);
			      
			      
			      
			      System.out.println("Updating last sensor ID...");
			            
			      String rs2 = "UPDATE FORTRESS_LOOKUP SET LAST_SENSOR_ID = LAST_SENSOR_ID + 1 " +
			    		  	   "WHERE FORTRESS = ?";
		                 
			      prepStmt = null;
			      prepStmt = c.prepareStatement(rs2);
			      prepStmt.setString(1, fortressname);
			      prepStmt.executeUpdate();
			      
			      c.close();*/
			      System.out.println("Insert Query run successfully (1)");
			      
			      switch (DBMode)
			      {
			      	case 0:
			      			  	  String rs = "INSERT INTO FORTRESS.SENSOR_LOOKUP (UUID, SENSOR_NAME, SENSOR_FORTRESS, SENSOR_MODE) " +
				                  "VALUES (?, ?, ?, 'Friendly')";  

						    	  //d = rootHikari.getConnection();
						    	  prepStmt = d.prepareStatement(rs);	
							      prepStmt.setString(1,  sensor.getUniqueID().toString());
							      prepStmt.setString(2, "sensor_" + fortdata.nextSensorID);
							      prepStmt.setString(3, fortressname);
							      prepStmt.executeUpdate();
							      
							      rs = "UPDATE FORTRESS_LOOKUP SET LAST_SENSOR_ID = LAST_SENSOR_ID + 1 " +
						    		  	   "WHERE FORTRESS = ?";
					                 
							      prepStmt = d.prepareStatement(rs);
							      prepStmt.setString(1, fortressname);
							      prepStmt.executeUpdate();
							      
							      prepStmt.close();
						      
							      
						      
							      System.out.println("MySQL Fortress lookup successful! (insert into sensor lookup)");
						      
						      //stmt.executeUpdate(sql);
					      break;
			      	case 1: 
				      		  String selectstring ="INSERT INTO SENSOR_LOOKUP (UUID, SENSOR_NAME, SENSOR_FORTRESS, SENSOR_MODE) " +
				      				"VALUES (?, ?, ?, 'Friendly')";
				      
							      prepStmt = c.prepareStatement(selectstring);
							      prepStmt.setString(1,  sensor.getUniqueID().toString());
							      prepStmt.setString(2, "sensor_" + fortdata.nextSensorID);
							      prepStmt.executeUpdate();
							      
							      
							      selectstring = "UPDATE FORTRESS_LOOKUP SET LAST_SENSOR_ID = LAST_SENSOR_ID + 1 " +
						    		  	   "WHERE FORTRESS = ?";
					                 
							      prepStmt = d.prepareStatement(selectstring);
							      prepStmt.setString(1, fortressname);
							      prepStmt.executeUpdate();
							      
							      
							      prepStmt.close();
			      		
			      }   
			      
			      
			      
			      
			      
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    
	    severConnection();
	    
	    System.out.println("Insert Query run successfully (2)");
	}

	public void removeSensor(String fortressname, F_Sensor sensor, F_DBFortressData fortdata) // (SECURED) (MySQL'd) (switch'd)
	{
		establishConnection();
		String rs;
	    PreparedStatement prepStmt = null;
	    
	    // insert into the sensor look up table.
	    try {
		      /*Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:test.db");
		      //System.out.println("Creation of fortress heart item index attempted...");
	
	
		      System.out.println("Attempting insert into SENSOR_LOOKUP...");
		      
			  String rs = "DELETE FROM SENSOR_LOOKUP " +
				          "WHERE SENSOR_NAME = ? AND SENSOR_FORTRESS=? ";           
	
			  prepStmt = c.prepareStatement(rs);
			  prepStmt.setString(1, sensor.getCustomName());
			  prepStmt.setString(2, sensor.FortressAlign);
			  prepStmt.executeUpdate();
			  
		      fortdata.getSensorHash().remove(sensor.getCustomName());
	
	 
		      c.close();
		      System.out.println("Delete sensor Query run successfully (1)");*/
	    	  switch (DBMode)
		      {
			      	case 0:
			      			rs = "DELETE FROM FORTRESS.SENSOR_LOOKUP " +
						          "WHERE SENSOR_NAME = ? AND SENSOR_FORTRESS=? ";     
						      
						      //d = null;
	
						      
						    
						    	  //d = rootHikari.getConnection();
						    	  prepStmt = d.prepareStatement(rs);	
						    	  prepStmt.setString(1, sensor.getCustomName());
								  prepStmt.setString(2, sensor.FortressAlign);
							      prepStmt.executeUpdate();
							      prepStmt.close();
							      
						      
						      
						      
							      System.out.println("MySQL Fortress lookup successful! (insert into sensor lookup)");
						      
						      //stmt.executeUpdate(sql);
					      break;
			      	case 1: 
			      			rs = "DELETE FROM SENSOR_LOOKUP " +
						          "WHERE SENSOR_NAME = ? AND SENSOR_FORTRESS=? ";     
				      
							      prepStmt = c.prepareStatement(rs);
							      prepStmt.setString(1, sensor.getCustomName());
								  prepStmt.setString(2, sensor.FortressAlign);
							      prepStmt.executeUpdate();
							      prepStmt.close();
		      		
		      }   
	    	
	    	
	    	
	    	  fortdata.getSensorHash().remove(sensor.getCustomName());
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    severConnection();
	}
	
	public List<F_DataRowSensor> returnFortressSensorList(String fortressname) // (SECURED) (MySQL'd) (switch'd)
	{
		establishConnection();
	    PreparedStatement prepStmt = null;
	    List<F_DataRowSensor> returnlist = new ArrayList<F_DataRowSensor>();
	    String selectstring;
	    ResultSet rs = null;
		try {
		      switch (DBMode)
		      {
		      	  case 0:
		      		  selectstring = 
		      						"SELECT SENSOR_NAME, SENSOR_MODE FROM FORTRESS.SENSOR_LOOKUP " +
				    		  		"WHERE " +
				    		  		"SENSOR_FORTRESS = ? "
				    		  		;
		      		  
		      		  prepStmt = d.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      rs = prepStmt.executeQuery();
		      		  
		      		  
		      		  break;
		      
			      case 1:
					Class.forName("org.sqlite.JDBC");
				      c = DriverManager.getConnection("jdbc:sqlite:test.db");
				      
				      System.out.println("Opened database successfully (retrieving fortress's sensor list");
			
				      
				      selectstring = 
				    		  		"SELECT SENSOR_NAME, SENSOR_MODE FROM SENSOR_LOOKUP " +
				    		  		"WHERE " +
				    		  		"SENSOR_FORTRESS = ? "
				    		  		;
				      
				      //stmt.executeUpdate(sql);
				      prepStmt = c.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      rs = prepStmt.executeQuery();
				      break;
		      }
		      if (rs.isBeforeFirst())
		      {
		    	  while (rs.next())
		    	  {
		    		  //System.out.println("SENSOR ADDED TO LIST.");
		    		  F_DataRowSensor returnsensorrow = new F_DataRowSensor();
		    		  returnsensorrow.name = rs.getString("SENSOR_NAME");
		    		  returnsensorrow.mode = rs.getString("SENSOR_MODE");
		    		  returnlist.add(returnsensorrow);
		    	  }
		      }
		        
		      prepStmt.close();
		      rs.close();

		      severConnection();
		      return returnlist;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		severConnection();
		return null;
	}

	public void createNewArrowTrap(String fortressname, F_ArrowTrap sensor, F_DBFortressData fortdata, String playername) // (SECURED) (MySQL'd) (switch'd)

	{
		establishConnection();
		//Connection c = null;
	    //Statement stmt = null;
	    //Statement stmt2 = null;
	    PreparedStatement prepStmt = null;
	    String rs, rs2;
	    
	    // insert into the sensor look up table.
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      System.out.println("Creation of fortress heart item index attempted...");

	      switch (DBMode)
	      {
		      case 0:
		    	  System.out.println("Attempting insert into MINE_LOOKUP...");
	    		  
			      rs = "INSERT INTO FORTRESS.MINE_LOOKUP (MINEID, OWNER, MINE_NAME, MINE_FORTRESS, MINE_TYPE) " +
		                  "VALUES (?, ?, ?, ?, 1)";  
		                  //" AGE            INT     NOT NULL, " + 
		                  //" ADDRESS        CHAR(50), " + 
		                  //" SALARY         REAL)"; 
		
			      //prepStmt = null;
			      prepStmt = d.prepareStatement(rs);
		
			      prepStmt.setString(1, sensor.getUniqueID().toString());
			      prepStmt.setString(2, playername);
			      prepStmt.setString(3, "mine_" + fortdata.nextArrowID);
			      prepStmt.setString(4, fortressname);
			      prepStmt.executeUpdate();
			      
				  
			      
			      
			      sensor.setFortressArrowTrapName("mine_" + fortdata.nextArrowID);
			      
			      
			      
			      System.out.println("Updating last sensor ID...");
			            
			      rs2 = "UPDATE FORTRESS.FORTRESS_LOOKUP SET LAST_ARROW_ID = LAST_ARROW_ID + 1 " +
			    		  	   "WHERE FORTRESS = ?";
		                 
			      prepStmt = null;
			      prepStmt = d.prepareStatement(rs2);
			      prepStmt.setString(1, fortressname);
			      prepStmt.executeUpdate();
		    	  
		    	  break;
		      case 1:
		      
			      System.out.println("Attempting insert into MINE_LOOKUP...");
			    		  
			      rs = "INSERT INTO MINE_LOOKUP (MINEID, OWNER, MINE_NAME, MINE_FORTRESS, MINE_TYPE) " +
		                  "VALUES (?, ?, ?, ?, 1)";  
		                  //" AGE            INT     NOT NULL, " + 
		                  //" ADDRESS        CHAR(50), " + 
		                  //" SALARY         REAL)"; 
		
			      //prepStmt = null;
			      prepStmt = c.prepareStatement(rs);
		
			      prepStmt.setString(1, sensor.getUniqueID().toString());
			      prepStmt.setString(2, playername);
			      prepStmt.setString(3, "mine_" + fortdata.nextArrowID);
			      prepStmt.setString(4, fortressname);
			      prepStmt.executeUpdate();
			      
				  
			      
			      
			      sensor.setFortressArrowTrapName("mine_" + fortdata.nextArrowID);
			      
			      
			      
			      System.out.println("Updating last sensor ID...");
			            
			      rs2 = "UPDATE FORTRESS_LOOKUP SET LAST_ARROW_ID = LAST_ARROW_ID + 1 " +
			    		  	   "WHERE FORTRESS = ?";
		                 
			      prepStmt = null;
			      prepStmt = c.prepareStatement(rs2);
			      prepStmt.setString(1, fortressname);
			      prepStmt.executeUpdate();
			      break;
	      }
	      //c.close();
	      System.out.println("Insert Query run successfully (ADDING NEW ARROW TRAP) (1)");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    
	    
	    severConnection();
	    System.out.println("Insert Query run successfully (ADDING NEW ARROW TRAP) (2)");
	}

	public void removeArrowTrap(String fortressname, F_ArrowTrap mine, F_DBFortressData fortdata) // (SECURED) (MySQL'd) (switch'd)
	{
		establishConnection();
	    PreparedStatement prepStmt = null;
	    String rs;
	    // insert into the sensor look up table.
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      //System.out.println("Creation of fortress heart item index attempted...");
	      switch (DBMode)
	      {
	      	  case 0:
	      		  System.out.println("Attempting delete from MINE_LOOKUP... (MySQL)");
	      		  rs = "DELETE FROM FORTRESS.MINE_LOOKUP " +
				          "WHERE MINE_NAME = ? AND MINE_FORTRESS=? ";           
	
	      		  prepStmt = d.prepareStatement(rs);
	      		  prepStmt.setString(1, mine.getCustomName());
	      		  prepStmt.setString(2, mine.FortressAlign);
	      		  prepStmt.executeUpdate();
	      		  prepStmt.close();
	      		  break;
		      case 1:
			      System.out.println("Attempting delete from MINE_LOOKUP...");
			      
				  rs = "DELETE FROM MINE_LOOKUP " +
					          "WHERE MINE_NAME = ? AND MINE_FORTRESS=? ";           
		
				  prepStmt = c.prepareStatement(rs);
				  prepStmt.setString(1, mine.getCustomName());
				  prepStmt.setString(2, mine.FortressAlign);
				  prepStmt.executeUpdate();
				  prepStmt.close();
				  break;
		  
	      }
	      
	      fortdata.getArrowTrapHash().remove(mine.getCustomName());

 
	      //c.close();
	      System.out.println("Delete ArrowTrap Query run successfully (1)");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    severConnection();
	}

	public List<F_DataRowArrowTrap> returnFortressArrowTrapList(String fortressname) // (SECURED) (MySQL'd) (switch'd)
	{
		establishConnection();
		//Connection c = null;
	    PreparedStatement prepStmt = null;
	    String selectstring;
		try {
		      //Class.forName("org.sqlite.JDBC");
		      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
		      List<String> returnlist = new ArrayList<String>();
		      List<F_DataRowArrowTrap> testlist = new ArrayList<F_DataRowArrowTrap>();
		      
		      System.out.println("Opened database successfully (retrieving fortress's arrow trap list");
		      
		      switch (DBMode)
		      {
			    case 0:
				      selectstring = 
				    		  		"SELECT MINE_NAME, MINE_TYPE FROM FORTRESS.MINE_LOOKUP " +
				    		  		"WHERE " +
				    		  		"MINE_FORTRESS = ? "
				    		  		;
				      
				      //stmt.executeUpdate(sql);
				      prepStmt = d.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      break;
		      	case 1:
				      selectstring = 
				    		  		"SELECT MINE_NAME, MINE_TYPE FROM MINE_LOOKUP " +
				    		  		"WHERE " +
				    		  		"MINE_FORTRESS = ? "
				    		  		;
				      
				      //stmt.executeUpdate(sql);
				      prepStmt = c.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      break;
		      }
		      ResultSet rs = prepStmt.executeQuery();
		      
		      
		      if (rs.isBeforeFirst())
		      {
		    	  
		    	  while (rs.next())
		    	  {
		    		  //System.out.println("MINE ADDED TO LIST.");
		    		  
		    		  F_DataRowArrowTrap arrowrow = new F_DataRowArrowTrap();
		    		  arrowrow.name = rs.getString("MINE_NAME");
		    		  arrowrow.type = rs.getString("MINE_TYPE");
		    		  testlist.add(arrowrow);
		    		  
		    		  
		    		  returnlist.add(rs.getString("MINE_NAME"));
		    	  }
		    	  prepStmt.close();
		    	  rs.close();
		      }
		        

		      //c.close();
		      System.out.println("Opened database successfully (retrieving fortress's arrow trap list) (2)");
		      return testlist;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		System.out.println("Opened database successfully (retrieving fortress's arrow trap list) (3)");
		severConnection();
		return null;
	}

	public void removeGolem(String fortressname, F_IronGolem golem, F_DBFortressData fortdata) // (SECURED) (MySQL'd) (switch'd)

	
	{
		establishConnection();
	    PreparedStatement prepStmt = null;
	    String rs;
	    // insert into the sensor look up table.
	    try {
	      //Class.forName("org.sqlite.JDBC");
	      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
	      //System.out.println("Creation of fortress heart item index attempted...");
	      switch (DBMode)
	      {
	      	  case 0:
	      		  System.out.println("Attempting delete from GOLEM_LOOKUP... (MySQL)");
	      		  rs = "DELETE FROM FORTRESS.GOLEM_LOOKUP " +
				          "WHERE GOLEM_NAME = ? AND GOLEM_FORTRESS=? ";           
	
	      		  prepStmt = d.prepareStatement(rs);
	      		  prepStmt.setString(1, golem.getCustomName());
	      		  prepStmt.setString(2, golem.OwningGroupString);
	      		  prepStmt.executeUpdate();
	      		  prepStmt.close();
	      		  break;
		      case 1:
			      System.out.println("Attempting delete from MINE_LOOKUP...");
			      
				  rs = "DELETE FROM GOLEM_LOOKUP " +
					          "WHERE GOLEM_NAME = ? AND GOLEM_FORTRESS=? ";           
		
				  prepStmt = c.prepareStatement(rs);
				  prepStmt.setString(1, golem.getCustomName());
				  prepStmt.setString(2, golem.OwningGroupString);
				  prepStmt.executeUpdate();
				  prepStmt.close();
				  break;
		  
	      }
	      
	      fortdata.getArrowTrapHash().remove(golem.getCustomName());

 
	      //c.close();
	      System.out.println("Delete Golem Query run successfully (1)");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    severConnection();
	}
	
	public void runNameChangeQuery(String fortressname, String originalname, String newname, int entitytype, F_DBFortressData FortressDBRef) // (SECURED) (MySQL'd) (switch'd)
	{
		System.out.println("Creation of fortress heart item index attempted...");
		establishConnection();
	    //Statement stmt = null;
	    //Statement stmt2 = null;
	    PreparedStatement prepStmt = null;
	    Connection conntopass = null;
	    String rs;
		    try 
		    {
			      //Class.forName("org.sqlite.JDBC");
			      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
			      System.out.println("Creation of fortress heart item index attempted...");
			      
			      switch(DBMode)
	 		 	  {
	 		 			
	 		 			case 0:
	 		 				conntopass = d;
	 		 				break;
	 		 			case 1:
	 		 				conntopass = c;
	 		 				break;
	 		 	  }
			      
			      switch (entitytype)
			   	  {
			   		 	case 0: // 0 = sensors
			   		 		
			   		 		
			   		 		
			   		 		
			   		 		
			   		 		if (checkIfNameValid(fortressname, originalname, 0, conntopass))
			   		 		{
				   		 		switch (DBMode)
				   		 		{
				   		 			case 0: 
				   		 				    rs = "UPDATE FORTRESS.SENSOR_LOOKUP SET SENSOR_NAME = ? " +
						   		 	                "WHERE " +
						   		 				    "SENSOR_NAME = ? AND " +
						   		 	                "SENSOR_FORTRESS = ?";
				   		 				    
				   		 				    prepStmt = d.prepareStatement(rs);
				   		 				break;
					   		 		case 1:
						   		 			rs = "UPDATE SENSOR_LOOKUP SET SENSOR_NAME = ? " +
							   		 	                "WHERE " +
							   		 				    "SENSOR_NAME = ? AND " +
							   		 	                "SENSOR_FORTRESS = ?";
							   		 		
						   		 		
						   		 			prepStmt = c.prepareStatement(rs);
					   		 			break;
				   		 		}
				   		 		System.out.println("Update sensor-name, pre hash.");
				   		 		prepStmt.setString(1, newname);
				   		 		prepStmt.setString(2, originalname);
				   		 		prepStmt.setString(3, fortressname);
				   		 		
				   		 		if (FortressDBRef.HashSensor.containsKey(originalname))
				   		 		{
				   		 			System.out.println("Name change if/then entry.");
				   		 			FortressDBRef.HashSensor.put(newname, FortressDBRef.HashSensor.get(originalname));
				   		 			FortressDBRef.HashSensor.remove(originalname);
				   		 			FortressDBRef.HashSensor.get(newname).setFortressSensorName(newname);
				   		 		}
				   		 		
				   		 		if (FortressDBRef.allHashSensor.containsKey(originalname))
				   		 		{
				   		 			System.out.println("Name change if/then entry.");
				   		 			FortressDBRef.allHashSensor.put(newname, FortressDBRef.HashSensor.get(newname).getUniqueID());
				   		 			FortressDBRef.allHashSensor.remove(originalname);
				   		 			//FortressDBRef.allHashSensor.get(newname).setFortressSensorName(UUID.fromString(newname));
				   		 		}
			   		 		}
			   		 		break;
			   			case 1: // 1 = arrow traps
			   			 
			   				if (checkIfNameValid(fortressname, originalname, 1, conntopass))
			   		 		{
				   		 		switch (DBMode)
				   		 		{
				   		 			case 0: 
				   		 				    rs = "UPDATE FORTRESS.MINE_LOOKUP SET MINE_NAME = ? " +
						   		 	                "WHERE " +
						   		 				    "MINE_NAME = ? AND " +
						   		 	                "MINE_FORTRESS = ?";
				   		 				    
				   		 				    prepStmt = d.prepareStatement(rs);
				   		 				break;
					   		 		case 1:
						   		 			rs = "UPDATE MINE_LOOKUP SET MINE_NAME = ? " +
							   		 	                "WHERE " +
							   		 				    "MINE_NAME = ? AND " +
							   		 	                "MINE_FORTRESS = ?";
							   		 		
						   		 		
						   		 			prepStmt = c.prepareStatement(rs);
					   		 			break;
				   		 		}
				   		 		System.out.println("Update arrow name, pre hash.");
				   		 		prepStmt.setString(1, newname);
				   		 		prepStmt.setString(2, originalname);
				   		 		prepStmt.setString(3, fortressname);
				   		 		
				   		 		if (FortressDBRef.HashArrow.containsKey(originalname))
				   		 		{
				   		 			FortressDBRef.HashArrow.put(newname, FortressDBRef.HashArrow.get(originalname));
				   		 			FortressDBRef.HashArrow.remove(originalname);
				   		 			FortressDBRef.HashArrow.get(newname).setFortressArrowTrapName(newname);
				   		 		}
				   		 		
					   		 	if (FortressDBRef.allHashArrow.containsKey(originalname))
				   		 		{
				   		 			FortressDBRef.allHashArrow.put(newname, FortressDBRef.HashArrow.get(newname).getUniqueID());
				   		 			FortressDBRef.allHashArrow.remove(originalname);
				   		 			//FortressDBRef.HashArrow.get(newname).setFortressArrowTrapName(newname);
				   		 		}
			   		 		}
			   				break;
			   				
			   			case 2: // 2 = golems
			   				
			   				if (checkIfNameValid(fortressname, originalname, 2, conntopass))
			   		 		{
				   		 		switch (DBMode)
				   		 		{
				   		 			case 0: 
				   		 				    rs = "UPDATE FORTRESS.GOLEM_LOOKUP SET GOLEM_NAME = ? " +
						   		 	                "WHERE " +
						   		 				    "GOLEM_NAME = ? AND " +
						   		 	                "GOLEM_FORTRESS = ?";
				   		 				    
				   		 				    prepStmt = d.prepareStatement(rs);
				   		 				break;
					   		 		case 1:
						   		 			rs = "UPDATE MINE_LOOKUP SET GOLEM_NAME = ? " +
							   		 	                "WHERE " +
							   		 				    "GOLEM_NAME = ? AND " +
							   		 	                "GOLEM_FORTRESS = ?";
							   		 		
						   		 		
						   		 			prepStmt = c.prepareStatement(rs);
					   		 			break;
				   		 		}
				   		 		System.out.println("Update golem name, pre hash.");
				   		 		prepStmt.setString(1, newname);
				   		 		prepStmt.setString(2, originalname);
				   		 		prepStmt.setString(3, fortressname);
				   		 		
				   		 		if (FortressDBRef.HashGolem.containsKey(originalname))
				   		 		{
				   		 			FortressDBRef.HashGolem.put(newname, FortressDBRef.HashGolem.get(originalname));
				   		 			FortressDBRef.HashGolem.remove(originalname);
				   		 			FortressDBRef.HashGolem.get(newname).setFortressGolemName(newname);
				   		 		}
				   		 		
					   		 	if (FortressDBRef.allHashGolem.containsKey(originalname))
				   		 		{
				   		 			FortressDBRef.allHashGolem.put(newname, FortressDBRef.HashGolem.get(newname).getUniqueID());
				   		 			FortressDBRef.allHashGolem.remove(originalname);
				   		 			//FortressDBRef.allHashGolem.get(newname).setFortressGolemName(newname);
				   		 		}
			   		 		}
			   				
			   				break;
			   	  }
			      //prepStmt.executeUpdate();
			      if (prepStmt != null)
			      {
			    	  prepStmt.executeUpdate();
			    	  prepStmt.close();
			      }
			      //c.close();
			      System.out.println("Edit name change run successsfully...");
		    } catch ( Exception e ) {
			      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			      //System.exit(0);
			}
	    
	    severConnection();
		      
	}

	public boolean checkIfNameValid(String fortressname, String name, int entitytype, Connection refconn) // (SECURED) (check later?)
	{
		//establishConnection();
	    PreparedStatement prepStmt = null;
	    ResultSet rs = null;
	    String selectstring;
		try {
		      //Class.forName("org.sqlite.JDBC");
		      // = DriverManager.getConnection("jdbc:sqlite:test.db");
		      System.out.println("Opened database successfully (checking if name is in fortress.) ");
		      
		      switch (entitytype)
		      {
			      case 0: // sensors
				      selectstring = 
				    		  		"SELECT SENSOR_NAME FROM FORTRESS.SENSOR_LOOKUP " +
				    		  		"WHERE " +
				    		  		"SENSOR_FORTRESS = ? AND " +
				    		  		"SENSOR_NAME = ?"
				    		  		;
				      
				      //stmt.executeUpdate(sql);
				      prepStmt = refconn.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      prepStmt.setString(2, name);
				      rs = prepStmt.executeQuery();
				      break;
				      
			      case 1: // arrows
			    	  
			    	  selectstring = 
				    		  		"SELECT MINE_NAME FROM FORTRESS.MINE_LOOKUP " +
				    		  		"WHERE " +
				    		  		"MINE_FORTRESS = ? AND " +
				    		  		"MINE_NAME = ?"
				    		  		;
	      
				      //stmt.executeUpdate(sql);
				      prepStmt = refconn.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      prepStmt.setString(2, name);
				      rs = prepStmt.executeQuery();
				      
			    	  break;
			      case 2: // golems
			    	  
			    	  selectstring = 
				    		  		"SELECT GOLEM_NAME FROM FORTRESS.GOLEM_LOOKUP " +
				    		  		"WHERE " +
				    		  		"GOLEM_FORTRESS = ? AND " +
				    		  		"GOLEM_NAME = ?"
				    		  		;

				      //stmt.executeUpdate(sql);
				      prepStmt = refconn.prepareStatement(selectstring);
				      prepStmt.setString(1,  fortressname);
				      prepStmt.setString(2, name);
				      rs = prepStmt.executeQuery();
			    	  break;
		      }
		      
		      
			    	  if (rs.next())
			    	  {
			    		  //System.out.println("SENSOR ADDED TO LIST.");
			    		  //returnlist.add(rs.getString("SENSOR_NAME"));
			    		  System.out.println("check 3.");
			    		  rs.close();
			    		  //severConnection();
			    		  prepStmt.close(); 
			    		  return true;
			    	  }
			      
		       else
		      {
		    	  System.out.println("return check.");
		    	  return false;
		      }
		       

		      //c.close();
		      //return returnlist;
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      //System.exit(0);
	    }
		//severConnection();
		
		return false;
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
    
    public static String toBase64(ItemStack item) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);

        NBTTagList nbtTagListItems = new NBTTagList();
        NBTTagCompound nbtTagCompoundItem = new NBTTagCompound();

        net.minecraft.server.v1_9_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);

        nmsItem.save(nbtTagCompoundItem);

        nbtTagListItems.add(nbtTagCompoundItem);

        try {
			NBTCompressedStreamTools.a(nbtTagCompoundItem, (DataOutput) dataOutput);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return new BigInteger(1, outputStream.toByteArray()).toString(32);
    }
       
    public static ItemStack fromBase64(String data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
        
        NBTTagCompound nbtTagCompoundRoot;
		try {
			nbtTagCompoundRoot = NBTCompressedStreamTools.a(new DataInputStream(inputStream));
			net.minecraft.server.v1_9_R1.ItemStack nmsItem = net.minecraft.server.v1_9_R1.ItemStack.createStack(nbtTagCompoundRoot);
	        ItemStack item = (ItemStack) CraftItemStack.asBukkitCopy(nmsItem);
	        return item;
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //net.minecraft.server.v1_9_R1.ItemStack nmsItem = net.minecraft.server.v1_9_R1.ItemStack.createStack(nbtTagCompoundRoot);
        //ItemStack item = (ItemStack) CraftItemStack.asBukkitCopy(nmsItem);

        return null;
    }

    public List<String> getPermittedWorlds()
    {
    	establishConnection();
    	PreparedStatement prepStmt = null;
	    String selectstring;
	    ResultSet rs = null;
	    List<String> worldlist = new ArrayList<String>();
	    
	    try {
	    	switch (DBMode)
	    	{
	    	case 0:
	    		selectstring = "SELECT WORLD FROM FORTRESS_WORLDS";
	    		prepStmt = d.prepareStatement(selectstring);
	    		rs = prepStmt.executeQuery();
	    		break;
	    	case 1:
	    		selectstring = "SELECT WORLD FROM FORTRESS_WORLDS";
	    		prepStmt = c.prepareStatement(selectstring);
	    		rs = prepStmt.executeQuery();
	    		break;
	    	}
	    	
	    	
	    	if (rs.next() == false)
		      {
		    	  System.out.println("No record found. returning null...");
		    	  severConnection();
		    	  return null;
		      }
	    	
	    	
	    	 worldlist.add(rs.getString("WORLD"));
		      while ( rs.next() )
		      {
		      	worldlist.add(rs.getString("WORLD"));
		      }
	    	
	    	
	    }	catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		}
	    
	    
	    
	    severConnection();
	    return worldlist;
    	
    }

    public void retrieveEntityList(String fortname, F_DBFortressData datatoset)
    {
    	establishConnection();
    	PreparedStatement prepStmt = null;
	    String selectstring;
	    ResultSet rs = null;
	    HashMap<String, UUID> hashMapPointer, hashMapPointer2, hashMapPointer3;
	    
	    try {
	    	switch (DBMode)
	    	{
	    	case 0:
	    		
	    		// get sensor hashmap
	    		selectstring = "SELECT UUID, SENSOR_NAME FROM FORTRESS.SENSOR_LOOKUP WHERE SENSOR_FORTRESS = ?";
	    		prepStmt = d.prepareStatement(selectstring);
	    		prepStmt.setString(1,  fortname);
	    		rs = prepStmt.executeQuery();
	    		if (rs.isBeforeFirst())
			     {
	    			  hashMapPointer = new HashMap<String,UUID>();
			    	  while (rs.next())
			    	  {
			    		  System.out.println("|||||||||||||||| LOADING SENSOR HASH ||||||||||||||||||");
			    		  System.out.println("SENSOR NAME: " + rs.getString("SENSOR_NAME"));
			    		  	
			    		  UUID uuid = UUID.fromString(rs.getString("UUID"));
			    		  hashMapPointer.put(rs.getString("SENSOR_NAME"), uuid);
			    	  }
			    	  datatoset.setAllSensorHashOnEnable(hashMapPointer);
			     }
	    		else if (!rs.isBeforeFirst())
	    		{
	    			hashMapPointer = new HashMap<String,UUID>();
	    			datatoset.setAllSensorHashOnEnable(hashMapPointer);
	    		}
	    		
	    		
	    		// get arrow hashmap
	    		selectstring = "SELECT MINEID, MINE_NAME FROM FORTRESS.MINE_LOOKUP WHERE MINE_FORTRESS = ?";
	    		prepStmt = d.prepareStatement(selectstring);
	    		prepStmt.setString(1,  fortname);
	    		rs = prepStmt.executeQuery();
	    		if (rs.isBeforeFirst())
			     {
	    			  hashMapPointer2 = new HashMap<String,UUID>();
			    	  while (rs.next())
			    	  {
			    		  UUID uuid = UUID.fromString(rs.getString("MINEID"));
			    		  hashMapPointer2.put(rs.getString("MINE_NAME"), uuid);
			    	  }
			    	  datatoset.setAllArrowHashOnEnable(hashMapPointer2);
			     }
	    		else if (!rs.isBeforeFirst())
	    		{
	    			System.out.println(" NO ARROW HASH !!!");
	    			hashMapPointer2 = new HashMap<String,UUID>();
	    			datatoset.setAllArrowHashOnEnable(hashMapPointer2);
	    		}
	    		
	    		// get golem hashmap
	    		selectstring = "SELECT GOLEMID, GOLEM_NAME FROM FORTRESS.GOLEM_LOOKUP WHERE GOLEM_FORTRESS = ?";
	    		prepStmt = d.prepareStatement(selectstring);
	    		prepStmt.setString(1,  fortname);
	    		rs = prepStmt.executeQuery();
	    		if (rs.isBeforeFirst())
			     {
	    			  hashMapPointer3 = new HashMap<String,UUID>();
			    	  while (rs.next())
			    	  {
			    		  UUID uuid = UUID.fromString(rs.getString("GOLEMID"));
			    		  hashMapPointer3.put(rs.getString("GOLEM_NAME"), uuid);
			    	  }
			    	  datatoset.setAllGolemHashOnEnable(hashMapPointer3);
			     }
	    		else if (!rs.isBeforeFirst())
	    		{
	    			
	    			hashMapPointer3 = new HashMap<String,UUID>();
	    			datatoset.setAllGolemHashOnEnable(hashMapPointer3);
	    		}
	    		
	    		break;
	    	case 1:
	    		
	    		
	    		// get sensor hashmap
	    		selectstring = "SELECT UUID, SENSOR_NAME FROM SENSOR_LOOKUP WHERE SENSOR_FORTRESS = ?";
	    		prepStmt = c.prepareStatement(selectstring);
	    		prepStmt.setString(1,  fortname);
	    		rs = prepStmt.executeQuery();
	    		if (rs.isBeforeFirst())
			     {
	    			  hashMapPointer = new HashMap<String,UUID>();
			    	  while (rs.next())
			    	  {
			    		  System.out.println("|||||||||||||||| LOADING SENSOR HASH ||||||||||||||||||");
			    		  UUID uuid = UUID.fromString(rs.getString("UUID"));
			    		  hashMapPointer.put(rs.getString("SENSOR_NAME"), uuid);
			    	  }
			    	  datatoset.setAllSensorHashOnEnable(hashMapPointer);
			     }
	    		else if (!rs.isBeforeFirst())
	    		{
	    			hashMapPointer = new HashMap<String,UUID>();
	    			datatoset.setAllSensorHashOnEnable(hashMapPointer);
	    		}
	    		
	    		
	    		// get arrow hashmap
	    		selectstring = "SELECT MINEID, MINE_NAME FROM MINE_LOOKUP WHERE MINE_FORTRESS = ?";
	    		prepStmt = c.prepareStatement(selectstring);
	    		prepStmt.setString(1,  fortname);
	    		rs = prepStmt.executeQuery();
	    		if (rs.isBeforeFirst())
			     {
	    			  hashMapPointer2 = new HashMap<String,UUID>();
			    	  while (rs.next())
			    	  {
			    		  UUID uuid = UUID.fromString(rs.getString("MINEID"));
			    		  hashMapPointer2.put(rs.getString("MINE_NAME"), uuid);
			    	  }
			    	  datatoset.setAllArrowHashOnEnable(hashMapPointer2);
			     }
	    		else if (!rs.isBeforeFirst())
	    		{
	    			hashMapPointer2 = new HashMap<String,UUID>();
	    			datatoset.setAllSensorHashOnEnable(hashMapPointer2);
	    		}
	    		
	    		// get golem hashmap
	    		selectstring = "SELECT GOLEMID, GOLEM_NAME FROM GOLEM_LOOKUP WHERE GOLEM_FORTRESS = ?";
	    		prepStmt = c.prepareStatement(selectstring);
	    		prepStmt.setString(1,  fortname);
	    		rs = prepStmt.executeQuery();
	    		if (rs.isBeforeFirst())
			     {
	    			  hashMapPointer3 = new HashMap<String,UUID>();
			    	  while (rs.next())
			    	  {
			    		  UUID uuid = UUID.fromString(rs.getString("GOLEMID"));
			    		  hashMapPointer3.put(rs.getString("GOLEM_NAME"), uuid);
			    	  }
			    	  datatoset.setAllGolemHashOnEnable(hashMapPointer3);
			     }
	    		else if (!rs.isBeforeFirst())
	    		{
	    			hashMapPointer3 = new HashMap<String,UUID>();
	    			datatoset.setAllSensorHashOnEnable(hashMapPointer3);
	    		}
	    		break;
	    	}
	    	
	    	
	    }catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      severConnection();
		      System.exit(0);
		}
	    
    	severConnection();
    	//return entitylist;
    	
    }
}


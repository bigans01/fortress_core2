package fortress.plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.zaxxer.hikari.HikariDataSource;

public class F_SQLPlayerDataQueries 
{
	/* This class manages all fortress, fortress member queries to the database.
	 * 
	 * 
	 * 
	 * */
	Connection c = null; // for SQLite connections.
	Connection d = null; // for MySQL connections.

	
	F_ChatStringBuilder stringBuilder = new F_ChatStringBuilder();
	
	// used for checking offline/online mode
	UUID verifiedplayer;
	String verifiedplayername;
	
	ComponentBuilder finalString, finalAdminString;
	HikariDataSource rootHikari;
	int DBMode;
	fortress_root rootPlugin;
	HashMap<String, F_DBFortressData> fortdataref;
	
	F_SQLPlayerDataQueries() // constructor. 
	{
		
		System.out.println("Attempting connect...");
		rootPlugin =  (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
		rootHikari = rootPlugin.getHikariDataSource();
		DBMode = rootPlugin.getDatabaseType();
		fortdataref = rootPlugin.getFortressDataHash();
		//establishConnection();
	}
	
	public List<String> getFortressMemberships(String player, HashMap<String, F_DBPlayerFortressPrivs> fortressPlayerPermissionsRef) // used for system output and loading in game list only, when player enters a game. (SECURED)
	{
		establishConnection();
		List<String> membershipList = new ArrayList<String>();
		//Connection c = null;
	    //Statement stmt = null;
	    PreparedStatement prepStmt = null;
	    String selectstring;
	    
	    try {
		      //Class.forName("org.sqlite.JDBC");
		      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
		      System.out.println("Opened database successfully (retrieving player's membership list)");
	
		      //stmt = c.createStatement();
		      
		      System.out.println("Break test: DBMode is: " + DBMode);
		      switch (DBMode)
		      {
		      	case 0:
		      		selectstring = 
    		  		"SELECT FORTRESS FROM FORTRESS.FORTRESS_MEMBER_LOOKUP " +
    		  		"WHERE " +
    		  		"PLAYER = ?"
    		  		;

      
			      prepStmt = d.prepareStatement(selectstring);
			      //prepStmt.setString(1,  verifiedplayer.toString());
			      prepStmt.setString(1,  player);
			      break;
		      	case 1:
		      
			        selectstring = 
			    		  		"SELECT FORTRESS FROM FORTRESS_MEMBER_LOOKUP " +
			    		  		"WHERE " +
			    		  		"PLAYER = ?"
			    		  		;
	
			      
			      prepStmt = c.prepareStatement(selectstring);
			      //prepStmt.setString(1,  verifiedplayer.toString());
			      prepStmt.setString(1,  player);
			      break;
		      }
		      ResultSet rs = prepStmt.executeQuery();
		      
		      //stmt.executeUpdate(sql);
		      
		      System.out.println("Break test 2");
		      if (rs.next() == false)
		      {
		    	  System.out.println("No record found. returning null...");
		    	  severConnection();
		    	  return null;
		      }
		      
		      
		      
		      // test: adding members via the iterator. 
		      // objects for update the player's in game entry. 
			  //F_DBPlayerFortressPrivs playerPerms = fortressPlayerPermissionsRef.get(player.getName());
			  //List<String> listToUpdate = playerPerms.getPlayerFortressMembership();
		      
			  /*for (Iterator<String> itr = listToUpdate.iterator(); itr.hasNext();)
			  {
				  itr.
			  }*/
			  
		      // add all fortresses this player is a member of to the membershipList.
		      membershipList.add(rs.getString("FORTRESS"));
		      while ( rs.next() )
		      {
		      	membershipList.add(rs.getString("FORTRESS"));
		      }
		      
		      
		      
		      
		      //stmt.close();
		      //c.close();
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    
	    
	    
	    System.out.println("Retrieval of player's membership list performed successfully");
		
	    severConnection();
		return membershipList;
	}
	
	public void listPlayerMemberships(Player player, HashMap<String, F_DBPlayerFortressPrivs> fortressPlayerPermissionsRef, String playertochange, int updateflag) // (SECURED)
	{
		//List<String> membershipList = new ArrayList<String>();
		//Connection c = null;
		establishConnection();
	    //Statement stmt = null;
	    String alignment = null;
	    PreparedStatement prepStmt = null;
	    String selectstring;
	    
	    //F_DBPlayerFortressPrivs playerPerms = fortressPlayerPermissionsRef.get(playertochange);
	    
	    getUUIDData(playertochange);
	    
	    try {
		      //Class.forName("org.sqlite.JDBC");
		      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
		      System.out.println("Opened database successfully (retrieving player's membership list)");
	
		      //stmt = c.createStatement();
		      System.out.println("Opened database successfully (retrieving player's membership list) 2");
		      System.out.println(verifiedplayername);
		      
		      switch (DBMode)
		      {
			      case 0:
				      selectstring = 
				    		  		"SELECT FORTRESS FROM FORTRESS.FORTRESS_MEMBER_LOOKUP " +
				    		  		"WHERE " +
				    		  		"PLAYER = ?"
				    		  		;
				      
				      prepStmt = d.prepareStatement(selectstring);
				      //prepStmt.setString(1,  verifiedplayer.toString());
				      prepStmt.setString(1,  verifiedplayername);
				      
				      break;
		      	case 1:
			      selectstring = 
			    		  		"SELECT FORTRESS FROM FORTRESS_MEMBER_LOOKUP " +
			    		  		"WHERE " +
			    		  		"PLAYER = ?"
			    		  		;
			      
			      prepStmt = c.prepareStatement(selectstring);
			      //prepStmt.setString(1,  verifiedplayer.toString());
			      prepStmt.setString(1,  verifiedplayername);

			      break;
		      }
		      ResultSet rs = prepStmt.executeQuery();
		      System.out.println("Opened database successfully (retrieving player's membership list) 3");
		      //stmt.executeUpdate(sql);
		      alignment = getAlignment();
		      
		      System.out.println("Opened database successfully (retrieving player's membership list) 4");
		      if (rs.next() == false)
		      {
		    	  finalString = new ComponentBuilder("Not a member of any fortresses. ");
		    	  finalString.color(ChatColor.GOLD);
		    	  System.out.println("No record found. returning null...");
		      } else
		      {
		    	  finalString = new ComponentBuilder("Current member list for: ");
		    	  finalString.color(ChatColor.GOLD);
		    	  finalString.append(playertochange);
		    	  finalString.bold(true);
		    	  finalString.color(ChatColor.AQUA);
		    	  finalString.append("\n");
		    	  finalString.color(ChatColor.GOLD);
		      
		    	  
			      finalString.append(rs.getString("FORTRESS"));
			      if (rs.getString("FORTRESS").equals(alignment))
			      {
			    	  finalString.color(ChatColor.DARK_GREEN);
			    	  finalString.bold(true);
			      } else
			      {
			    	  finalString.color(ChatColor.GREEN);
			    	  finalString.bold(false);
			      }
			      
			      while ( rs.next() )
			      {
				      	finalString.append("\n" + rs.getString("FORTRESS"));			      			
				      	if (rs.getString("FORTRESS").equals(alignment))
					    {
					    	finalString.color(ChatColor.DARK_GREEN);
					    	finalString.bold(true);
					    } else
					    {
					    	finalString.color(ChatColor.GREEN);
					    	finalString.bold(false);
					    }		
			      }
		      
		      }
		      
		      
		      //stmt.close();
		      //c.close();
		      player.spigot().sendMessage(finalString.create());
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    severConnection();
	}

	public void updateFortressMembership(Player player, HashMap<String, F_DBPlayerFortressPrivs> fortressPlayerPermissionsRef, String playertochange, String fortresstochange, int updateflag, Connection passedConnection) // (SECURED) (MySQL'd) (switch'd)
	{
		
		if (passedConnection == null)
		{
			establishConnection(); // call method to establish connection
		}
	    Statement stmt = null; // reset the "stmt" variable
	    PreparedStatement prepStmt = null;
	    
	    // retrieve player's in game/memory permissions object(s)
	    //F_DBPlayerFortressPrivs playerPerms = fortressPlayerPermissionsRef.get(player.getName());
	    F_DBPlayerFortressPrivs playerPerms = null;
	    if (fortressPlayerPermissionsRef.containsKey(playertochange))
	    {
	    	playerPerms = fortressPlayerPermissionsRef.get(Bukkit.getServer().getPlayer(playertochange).getName());
	    }
	    List<String> listToUpdate = null;
	    
	    if (playerPerms != null)
	    {
	    	listToUpdate = playerPerms.getPlayerFortressMembership();
	    } 
	    
	    if (playerPerms == null)
	    {
	    	System.out.println("PLAYER PERMS IS NULL");
	    } 
	    
	    
	    System.out.println("PRE-UUID CALL");
	    getUUIDData(playertochange);
	    
	    
	    
	    
	    
	    // switch on the update flag parameter... 0 = attempt to delete person from table (ADMIN), 1 = attempt to add person to table (ADMIN) 
	    switch (updateflag)
	    {
	    	case 0: // delete from fortress table. 
	    	///////////////////////////////////////////////////////////////////////////////////////////////
	    
				    if (updateflag == 0)
				    {
				    	System.out.println("DELETE FLAG IF ENTRY");
				    	if (isPlayerMemberOfFortress(verifiedplayername, fortresstochange, passedConnection, true) && doesFortressExist(fortresstochange))
					    {
						    // update the database entry.
						    try {
						    	Class.forName("org.sqlite.JDBC");
							      
							      System.out.println("Attempting to assign mine to player...(DELETE)");
					
							      
							      // player.getServer().getPlayer(playertochange).getUniqueId()
							      //stmt = c.createStatement();
							      /*System.out.println("Instantiation of stmt variable successsful...(DELETE)");
							      String selectstring = "DELETE FROM FORTRESS_MEMBER_LOOKUP " + 
							                  "WHERE PLAYER_UUID  = ? " + 
							                  "AND FORTRESS = ? ";
							                   //"VALUES (" + inputLong + ");";  
							                   //" AGE            INT     NOT NULL, " + 
							                   //" ADDRESS        CHAR(50), " + 
							                   //" SALARY         REAL)"; 
							      prepStmt = c.prepareStatement(selectstring);
							      prepStmt.setString(1, verifiedplayer.toString());
							      prepStmt.setString(2, fortresstochange);
							      System.out.println("Instantiation of stmt variable successsful...(DELETE) 2");
							      prepStmt.executeUpdate(); // use this for updates in prepared statements!!*/
							      
							      switch (DBMode)
							      {
							      	case 0:
									      String selectstring2 = "DELETE from FORTRESS.FORTRESS_MEMBER_LOOKUP " +
									    		  				 "WHERE PLAYER_UUID = ?" +
									    		                 "AND FORTRESS = ?";
									      //d = rootHikari.getConnection();
									      PreparedStatement prepStmt2 = d.prepareStatement(selectstring2);	  
									      prepStmt2.setString(1,  verifiedplayer.toString());
									      prepStmt2.setString(2,  fortresstochange);
									      prepStmt2.executeUpdate();
									      System.out.println("MySQL Fortress query successful (delete from member lookup)");
									      //d.close();
									      //stmt.executeUpdate(sql);
									      break;
							      	case 1:
							      		  String selectstring = "DELETE FROM FORTRESS_MEMBER_LOOKUP " + 
								                  "WHERE PLAYER_UUID  = ? " + 
								                  "AND FORTRESS = ? ";
								                   //"VALUES (" + inputLong + ");";  
								                   //" AGE            INT     NOT NULL, " + 
								                   //" ADDRESS        CHAR(50), " + 
								                   //" SALARY         REAL)"; 
									      prepStmt = c.prepareStatement(selectstring);
									      prepStmt.setString(1, verifiedplayer.toString());
									      prepStmt.setString(2, fortresstochange);
									      System.out.println("Instantiation of stmt variable successsful...(DELETE) 2");
									      prepStmt.executeUpdate(); // use this for updates in prepared statements!!
									      break;
							      }      
							      
							      
							      //stmt.executeUpdate(rs);
							      /////
							      //UUID playeruuid = player.getServer().getPlayer(playertochange).getUniqueId();
							      /////
							      //stmt.close();
							      
							      System.out.println("Instantiation of stmt variable successsful...(DELETE) 3");
							      String checkAlign = getAlignment(); // check the deleted player's alignment
							      if (checkAlign != null)
							      {
							    	  if (checkAlign.equals(fortresstochange))
							    	  {
							    		  removeAlignment(player, fortressPlayerPermissionsRef, playertochange);
							    	  }
							      }
						
							      System.out.println("Remove member from fortress run successfully.");
							      
							      
							      
							      
						      
						    } catch ( Exception e ) {
						      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
						      System.exit(0);
						    }
						    
						    System.out.println("Remove member from fortress run successfully. (1)");

						    System.out.println("Remove member from fortress run successfully. (2)");
						    //boolean existsFlag = false;
						    
						    finalString = new ComponentBuilder("Fortress member list was updated: ");
					    	finalString.color( ChatColor.GOLD);
					    	
					    	finalAdminString = stringBuilder.constructAdminRemoveMemberSuccessful(verifiedplayername, fortresstochange);
					    	
					    	String playerMsg = "Fortress membership updated: ";
						    
						    
						    //Bukkit.getO
						    
						    // output to the system log. 
					    	
					    	//*****************************
					    	// check if player is member of of the fortress's group already.
					    	//*****************************
					    	
					    	fortress_root fortplugin = (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
							WorldGuardPlugin wgplugin = fortplugin.getWorldGuard();
							
							// set up region variables
							RegionContainer container = wgplugin.getRegionContainer();
							RegionManager regions = container.get(Bukkit.getWorld(fortplugin.getFortressDataHash().get(fortresstochange).getFortWorld()));
							
							if (regions.hasRegion(fortresstochange) == true)
							{
								PermissionManager permissions = PermissionsEx.getPermissionManager();
								PermissionUser userToCheck = permissions.getUser(verifiedplayername);
								PermissionGroup groupToCheck = permissions.getGroup(fortresstochange);
								
								if (userToCheck.inGroup(groupToCheck) == true)
								{
									userToCheck.removeGroup(groupToCheck);
								}
								
							}
					    	
					    	
						    
						    if (Bukkit.getServer().getPlayer(verifiedplayername) != null)
						    {

							    if (!listToUpdate.isEmpty())
							    {
								    for (Iterator<String> itr = listToUpdate.iterator(); itr.hasNext();)
								    {
								    	
								    	String element = itr.next();
								    	if (fortresstochange.equals(element))
								    	{
								    		itr.remove();
								    	}
								    	if (!fortresstochange.equals(element))
								    	{
								    		System.out.println("Remaining member of fortress: " + element);
								    	}
								    }
							    }
					  		    // check if player is online, and send a message to them if they are.  
							    if (player.getServer().getPlayer(playertochange).isOnline())
						    	{
							    	
							    	//ComponentBuilder finalString = new ComponentBuilder("Fortress membership updated: ").create();
							    	 finalString = new ComponentBuilder("Fortress member list was updated: \n");
							    	finalString.color( ChatColor.GOLD);
							    	
							    	//String playerMsg = "Fortress membership updated: ";
							    	
							    	if (!listToUpdate.isEmpty())
								    {
								    	for (Iterator<String> itr = listToUpdate.iterator(); itr.hasNext();)
									    {
									    	String element = itr.next();
									    	if (playerPerms.getFortressAlignment() != null)
									    	{
									    		if (element.equals(playerPerms.getFortressAlignment()))
									    		{
									    			finalString.bold(true);
									    			finalString.append(element + " ");
									    			finalString.color(ChatColor.DARK_GREEN);
									    		} else
									    		{
									    			finalString.bold(false);
									    			finalString.append(element + " ");
											    	finalString.color(ChatColor.GREEN);			
									    		}
									    		
									    		if (itr.hasNext())
									    		{
									    			finalString.append("\n");
									    		}
									    		
									    	} else
									    	{
									    		finalString.bold(false);
									    		finalString.append(element + " ");
									    		finalString.color(ChatColor.GREEN);
									    		
									    		if (itr.hasNext())
									    		{
									    			finalString.append("\n");
									    		}
									    	}
									    } 
								    }
							    	Bukkit.getServer().getPlayer(verifiedplayername).spigot().sendMessage(finalString.create());
	
						    	}
							    player.spigot().sendMessage(finalAdminString.create());
						    } else   
						    {
						    	System.out.println("CHECK MARKER (4)");
						    	/*for (Iterator<String> itr = listToUpdate.iterator(); itr.hasNext();)
							    {
							    	String element = itr.next();
							    	finalString.append(element + " ");
							    	finalString.color(ChatColor.GREEN);
							    	
							    }*/
						    	player.spigot().sendMessage(finalAdminString.create());
						    	
						    }
					    }   
				    	
				    	
						    
				    	else
					    {
				    		ComponentBuilder finalErrorString = stringBuilder.constructErrorStringMemberOrFortDoesntExist(playertochange, fortresstochange);
				    		player.spigot().sendMessage(finalErrorString.create());
					    }
						    
							// testing only: print out the ingame version of the permissions.
				    	System.out.println("INTERMEDIATE");
				    
				    }
				    break;
				    
	    	case 1: // add to the fortress table. 
		    ///////////////////////////////////////////////////////////////////////////////////////////////	 
			
	    
				    
					if(updateflag == 1)
					{
							    	System.out.println("ADD FLAG IF ENTRY");
							    	if (!isPlayerMemberOfFortress(verifiedplayername, fortresstochange, passedConnection, true) && doesFortressExist(fortresstochange))
								    {
								    	// update the database entry.
									    try {
									    	Class.forName("org.sqlite.JDBC");
										      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
										      System.out.println("Attempting to assign mine to player...(INSERT)");
								
										      //stmt = c.createStatement();
										      System.out.println("Instantiation of stmt variable successsful...(INSERT)");
										      /*String selectstring = "INSERT INTO FORTRESS_MEMBER_LOOKUP (PLAYER, PLAYER_UUID, FORTRESS) " + 
										                  "VALUES (?, ?, ?)"; 
										                 
										      prepStmt = c.prepareStatement(selectstring);
										      prepStmt.setString(1, verifiedplayername);
										      prepStmt.setString(2, verifiedplayer.toString());
										      prepStmt.setString(3, fortresstochange);
										      prepStmt.executeUpdate();*/
										      
										      
										      switch (DBMode)
										      {
										      	case 0:
												      String selectstring2 = "INSERT INTO FORTRESS.FORTRESS_MEMBER_LOOKUP (PLAYER, PLAYER_UUID, FORTRESS) " +
												    		                 "VALUES (?, ?, ?)";
												      //Connection d = rootHikari.getConnection();
												      PreparedStatement prepStmt2 = d.prepareStatement(selectstring2);	  
												      prepStmt2.setString(1, verifiedplayername);
												      prepStmt2.setString(2, verifiedplayer.toString());
												      prepStmt2.setString(3, fortresstochange);
												      prepStmt2.executeUpdate();
												      System.out.println("MySQL Fortress query successful (insert into member lookup)");
												      //d.close();
												      //stmt.executeUpdate(sql);
												      break;
										      	case 1:
										      		String selectstring = "INSERT INTO FORTRESS_MEMBER_LOOKUP (PLAYER, PLAYER_UUID, FORTRESS) " + 
											                  "VALUES (?, ?, ?)"; 
											                 
												      prepStmt = c.prepareStatement(selectstring);
												      prepStmt.setString(1, verifiedplayername);
												      prepStmt.setString(2, verifiedplayer.toString());
												      prepStmt.setString(3, fortresstochange);
												      prepStmt.executeUpdate();
										      		
										      		  break;
										      }      
										      
										      
										      
										      
										      //stmt.executeUpdate(selectstring);

										      //stmt.close();
									          
										      //c.close();
										      System.out.println("Add member from fortress run successfully.");
									      
									    } catch ( Exception e ) {
									      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
									      System.exit(0);
									    }
									    
									    // objects for update the player's in game entry. 
									    System.out.println("Add member from fortress run successfully. (1)");

									    System.out.println("Add member from fortress run successfully. (2)");
									    //boolean existsFlag = false;
									    
									    finalString = new ComponentBuilder("Fortress member list was updated: \n");
								    	finalString.color( ChatColor.GOLD);
								    	
								    	finalAdminString = stringBuilder.constructAdminAddMemberSuccessful(verifiedplayername, fortresstochange);
								    	
								    	String playerMsg = "Fortress membership updated: ";
								    	
								    	//*****************************
								    	// check if player is member of of the fortress's group already.
								    	//*****************************
								    	
								    	fortress_root fortplugin = (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
										WorldGuardPlugin wgplugin = fortplugin.getWorldGuard();
										
										// set up region variables
										RegionContainer container = wgplugin.getRegionContainer();
										RegionManager regions = container.get(Bukkit.getWorld(fortplugin.getFortressDataHash().get(fortresstochange).getFortWorld()));
										
										if (regions.hasRegion(fortresstochange) == true)
										{
											PermissionManager permissions = PermissionsEx.getPermissionManager();
											PermissionUser userToCheck = permissions.getUser(verifiedplayername);
											PermissionGroup groupToCheck = permissions.getGroup(fortresstochange);
											
											if (userToCheck.inGroup(groupToCheck) == false)
											{
												userToCheck.addGroup(groupToCheck);
											}
											
										}
									    
									    if (Bukkit.getServer().getPlayer(verifiedplayername) != null)
									    {
									    	System.out.println("CHECK MARKER (1)");
										    if (listToUpdate != null)
										    {
											    if (!listToUpdate.isEmpty() && listToUpdate != null)
											    {
												    for (Iterator<String> itr = listToUpdate.iterator(); itr.hasNext();)
												    {
												    								    	
												    	String element = itr.next();
												    	System.out.println(element);
												    	if (fortresstochange.equals(element))
												    	{
												    		itr.remove();
												    	}
			
												    }
											    }
											    listToUpdate.add(fortresstochange);
										    }
										    
										    // 1/22/2016: listToUpdate needs to be created if null, otherwise error here.
										    System.out.println("CHECK MARKER (3)");
										    if (listToUpdate == null)
										    {
										    	
										    	System.out.println("LIST WAS NULL!!!!");
										    	List<String> newList =  new ArrayList<String>();
										    	newList.add(fortresstochange);
										    	playerPerms.setPlayerFortressMembership(newList);
										    	System.out.println("NEW LIST WAS CREATED.");
										    	listToUpdate = playerPerms.getPlayerFortressMembership();
										    }
										    
										    
										    for (Iterator<String> itr = listToUpdate.iterator(); itr.hasNext();)
										    {
										    	String element = itr.next();
										    	if (playerPerms.getFortressAlignment() != null)
										    	{
										    		if (element.equals(playerPerms.getFortressAlignment()))
										    		{
										    			finalString.append(element + " ");
										    			finalString.color(ChatColor.DARK_GREEN);
										    			finalString.bold(true);
										    		} else
										    		{
										    			finalString.bold(false);
										    			finalString.append(element + " ");
												    	finalString.color(ChatColor.GREEN);			
										    		}
										    		
										    		if (itr.hasNext())
										    		{
										    			finalString.append("\n");
										    		}
										    		
										    	} else
										    	{
										    		finalString.bold(false);
										    		finalString.append(element + " ");
										    		finalString.color(ChatColor.GREEN);
										    		
										    		if (itr.hasNext())
										    		{
										    			finalString.append("\n");
										    		}
										    	}
										    	
										    	
										    	
										    	
										    	
										    	
										    	
										    	//finalString.append(element + " ");
										    	//finalString.color(ChatColor.GREEN);
										    	
										    }
										    
										    //listToUpdate.add(fortresstochange);
										    
										    
									    	
										    
										    
										    
										    //if (player.getServer().getPlayer(playertochange).isOnline())
									    	//{
										    	
	
										    	
	
											    //player.spigot().sendMessage( new ComponentBuilder( "Hello " ).color( ChatColor.RED ).bold( true ).append( "world" ).color( ChatColor.BLUE ).append( "!" ).color( ChatColor.RED ).create() );
										    	Bukkit.getServer().getPlayer(verifiedplayername).spigot().sendMessage(finalString.create());
	
									    	//}
										    
										    player.spigot().sendMessage(finalAdminString.create());
										    //player.spigot().sendMessage(finalString.create());
									    } 
									    
									    else   
									    {
									    	System.out.println("CHECK MARKER (4)");
									    	/*for (Iterator<String> itr = listToUpdate.iterator(); itr.hasNext();)
										    {
										    	String element = itr.next();
										    	finalString.append(element + " ");
										    	finalString.color(ChatColor.GREEN);
										    	
										    }*/
									    	player.spigot().sendMessage(finalAdminString.create());
									    	
									    }
								    }
							    	
							    	else
								    {
							    		ComponentBuilder finalErrorString = stringBuilder.constructErrorStringMemberAlreadyMember(verifiedplayername, fortresstochange);
							    		player.spigot().sendMessage(finalErrorString.create());
								    }
							    
				    }
				    break;
				    
	    	case 2: // RESERVED FOR LATER USE 
			///////////////////////////////////////////////////////////////////////////////////////////////
	    		    break;

	    	case 3: // RESERVED FOR LATER USE 
			///////////////////////////////////////////////////////////////////////////////////////////////
	    			break;
	    }
	    if (passedConnection == null)
		{
	    	severConnection();
		}
	}
	
	public void updateFortress(Player player, HashMap<String, F_DBPlayerFortressPrivs> fortressPlayerPermissionsRef, String fortresstochange, String fortressowner, int updateflag) // (SECURED) (MySQL'd)
	{
		
		establishConnection(); // call method to establish connection
	    Statement stmt = null; // reset the "stmt" variable
	    
	    // retrieve player's in game/memory permissions object(s)
	    F_DBPlayerFortressPrivs playerPerms = fortressPlayerPermissionsRef.get(player.getName());
	    List<String> listToUpdate = playerPerms.getPlayerFortressMembership();
		
	    PreparedStatement prepStmt = null;
	    
		
		switch (updateflag)
		{
			case 0:
				System.out.println("REMOVE FORTRESS (ADMIN) IF ENTRY");
				
				if (doesFortressExist(fortresstochange))
				{
					try
					{
						System.out.println("Attempting to remove fortress...");
						String selectMembers, delMembers, delEntities, delHeart, delFortress;
						PreparedStatement prepStmtSelectMembers, prepStmtDelMembers, prepStmtDelEntities, prepStmtDelHeart, prepStmtDelFortress;
						switch (DBMode)
						{
							case 0:
								// 1. Get Hikari connection
								//Connection d = rootHikari.getConnection();
								
								// 2. Remove fortress members
								
								// 2.1 Remove members from the fortress if they are online.
								selectMembers = "SELECT PLAYER FROM FORTRESS.FORTRESS_MEMBER_LOOKUP " +
												"WHERE FORTRESS = ?";
								prepStmtSelectMembers = d.prepareStatement(selectMembers);
								prepStmtSelectMembers.setString(1, fortresstochange);
								ResultSet rs = prepStmtSelectMembers.executeQuery();
								System.out.println("START: removal of members.");
								while (rs.next() == true) // iterate only if there is a record.
								{
									F_SQLPlayerDataQueries rootPlayerDataQueries = rootPlugin.getFortressQueryController().get(player.getName()); // retrieve the query handler for the player that sent the message.
									rootPlayerDataQueries.updateFortressMembership(player, rootPlugin.getPlayerFortressPrivs(), rs.getString(1), fortresstochange, updateflag, d); // update the first entry in the result set 
									System.out.println("Removal of first member successful...");
									/*while (rs.) == true) // get any additional members and remove them.
									{
										System.out.println("Deleting 2nd members and onward...");
										rootPlayerDataQueries.updateFortressMembership(player, rootPlugin.getPlayerFortressPrivs(), rs.getString(1), fortresstochange, updateflag);
									}*/
								}
								rs.close(); // close the list of players retrieved. 
								System.out.println("RS Close pass...");
								
								delMembers = 	"DELETE FROM FORTRESS.FORTRESS_MEMBER_LOOKUP " +
											   	"WHERE FORTRESS = ?";
								prepStmtDelMembers = d.prepareStatement(delMembers);
								prepStmtDelMembers.setString(1, fortresstochange);
								prepStmtDelMembers.executeUpdate();
								System.out.println("Removal of all members successful...");
								
								// 3. Reset the alignment/other properties of entities belonging to the fortress
								delEntities = "DELETE FROM FORTRESS.MINE_LOOKUP " +
											  "WHERE FORTRESS = ?";
								prepStmtDelEntities = d.prepareStatement(delMembers);
								prepStmtDelEntities.setString(1, fortresstochange);
								prepStmtDelEntities.executeUpdate();
								
								
								F_DBFortressData fortToUpdate = fortdataref.get(fortresstochange);
								HashMap<String, F_ArrowTrap> arrowHash = fortToUpdate.getArrowTrapHash();
								System.out.println("START: update of arrow traps.");
								if (arrowHash.isEmpty() == false)
								{
									Iterator arrowIt = arrowHash.entrySet().iterator();
									while (arrowIt.hasNext())
									{
										Map.Entry pair = (Map.Entry)arrowIt.next();
										F_ArrowTrap trapToEdit = (F_ArrowTrap) pair.getValue();
										trapToEdit.resetAlignment();
										trapToEdit.resetOwner();
									}
								}
								System.out.println("Reset of all arrow traps successful...");
								
								
								// 4. Remove fortress heart items, and the heart itself
								System.out.println("START: delete of fortress heart items.");
								delHeart = "DELETE FROM FORTRESS.FORTRESS_HEART_ITEMS " + 
										   "WHERE FORTRESS= ?";
								prepStmtDelHeart = d.prepareStatement(delHeart);
								prepStmtDelHeart.setString(1, fortresstochange);
								prepStmtDelHeart.executeUpdate();
								
								fortdataref.get(fortresstochange).testInventory = null;
								
								// 5. Remove fortress region(s)
								WorldGuardPlugin wgplugin = rootPlugin.getWorldGuard();
								RegionContainer container = wgplugin.getRegionContainer();
								RegionManager regions = container.get(player.getWorld());
								regions.removeRegion(fortresstochange);
								
								System.out.println("Removal of region successful...");
								
								// 6. Remove fortress group
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex group " + fortresstochange + " delete");
								System.out.println("Removal of fortress group successful...");
								
								
								// 7. Remove fortress 
								System.out.println("START: delete from fortress lookup.");
								delFortress = "DELETE FROM FORTRESS.FORTRESS_LOOKUP " +
								              "WHERE FORTRESS = ?";
								prepStmtDelFortress = d.prepareStatement(delFortress);
								prepStmtDelFortress.setString(1, fortresstochange);
								prepStmtDelFortress.executeUpdate();
								System.out.println("Removal of fortress finalized...");
								
								break;
							case 1:
								break;
						}
						
						
					} catch ( Exception e ) {
					  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					  System.exit(0);
					}
					
				}
				break;
			case 1: // create fortress (ADMIN)
			///////////////////////////////////////////////////////////////////////////////////////////////
				System.out.println("ADD FORTRESS (ADMIN) IF ENTRY");
				// 1. Check to see if fortress exists. If it already exists, return a message to user. 
				if (!doesFortressExist(fortresstochange))
				{
					//2. Create the fortress entry, and assign the owner equal to "fortressowner" argument.
					try {
				    	//Class.forName("org.sqlite.JDBC");
					      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
					      System.out.println("Attempting to create new fortress...");
			
					      
					      
					      
					      
					      switch (DBMode)
					      {
					      		case 0:
					      			  String selectstring2 = "INSERT INTO FORTRESS.FORTRESS_LOOKUP (FORTRESS, FORTRESS_CREATOR, HEART_WORLD) " +
				    		                 "VALUES (?, ?, ?)";
								      PreparedStatement prepStmt2 = d.prepareStatement(selectstring2);	  
								      prepStmt2.setString(1,  fortresstochange);
								      prepStmt2.setString(2,  player.getName());
								      prepStmt2.setString(3,  player.getWorld().getName());
								      prepStmt2.executeUpdate();
								      System.out.println("MySQL Fortress lookup successful! (insert new name into fortress_lookup");
								      break;
					      		case 1:
					      			  String selectstring = "INSERT INTO FORTRESS_LOOKUP (FORTRESS, FORTRESS_CREATOR, HEART_WORLD) " + 
							                  "VALUES (?, ?, ?)"; 
							      
					      			  PreparedStatement prepStmt3 = c.prepareStatement(selectstring);
					      			  prepStmt3.setString(1,  fortresstochange);
					      			  prepStmt3.setString(2,  player.getName());
					      			  prepStmt3.setString(3,  player.getWorld().getName());
					      			  prepStmt3.executeUpdate();
					      			  break;
					      }

					      
					      System.out.println("Add member from fortress run successfully.");
					      ComponentBuilder finalString = stringBuilder.constructStringFortressCreated(fortresstochange);
					      player.spigot().sendMessage(finalString.create());
					      // send message to player that fortress was successfully created. 
					      
				      
				    } catch ( Exception e ) {
				      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
				      System.exit(0);
				    }
					
					// update the fortress data hashmap with the new fortress
					//fortress_root rootPlugin =  (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
					//HashMap<String, F_DBFortressData> fortdataref = rootPlugin.getFortressDataHash();
					fortdataref.put(fortresstochange, new F_DBFortressData(fortresstochange));
					fortdataref.get(fortresstochange).setFortWorld(player.getWorld().getName());
					
			        //String linkTest = rootPlugin.rootLinkTest();
					
				}
				
				else //if (doesFortressExist(fortressname))
				{
					//3. Send message to player creating the fortress if there is already a fortress.
					ComponentBuilder finalString = stringBuilder.constructErrorStringFortressAlreadyExists(fortresstochange);
					player.spigot().sendMessage(finalString.create());
				}
				
				
				// 3. Update the player's in-game data to be synchronized with database queries.
				break;
		}
		
		severConnection();
	}
	
	public boolean doesFortressExist(String fortToCheck) // (SECURED) (MySQL'd) (switch'd)
	{
		//Connection c = null;
	    //Statement stmt = null;
		//Connection d = null;
	    PreparedStatement prepStmt = null;
	    
		try {
		      Class.forName("org.sqlite.JDBC");
		      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
		      System.out.println("Opened database successfully (checking if fortress exists) (New MySQL)");
	
		      
		      
		      
		      
		      //stmt = c.createStatement();
		      /*String selectstring = 
		    		  		"SELECT FORTRESS FROM FORTRESS_LOOKUP " +
		    		  		"WHERE " +
		    		  		"FORTRESS = ?";
		    		  		
		      
		      prepStmt = c.prepareStatement(selectstring);
		      //prepStmt.setString(1,  verifiedplayer.toString());
		      prepStmt.setString(1,  fortToCheck);
		      ResultSet rs;*/
		      
		      ResultSet rs = null;
		      
		      
		      
		      switch (DBMode)
		      {
		      	case 0:
				      String selectstring2 = "SELECT FORTRESS from FORTRESS.FORTRESS_LOOKUP " +
				    		  				 "WHERE " +
				    		                 "FORTRESS = ?";
				      //d = rootHikari.getConnection();
				      PreparedStatement prepStmt2 = d.prepareStatement(selectstring2);	  
				      prepStmt2.setString(1,  fortToCheck);
				      rs = prepStmt2.executeQuery();
				      System.out.println("MySQL Fortress lookup successful! (select from fortress lookup) ");
				      //stmt.executeUpdate(sql);
				      break;
		      	case 1:
			      	 String selectstring =    "SELECT FORTRESS FROM FORTRESS_LOOKUP " +
							    		  		"WHERE " +
							    		  		"FORTRESS = ?";
    		  		
      
				      prepStmt = c.prepareStatement(selectstring);
				      //prepStmt.setString(1,  verifiedplayer.toString());
				      prepStmt.setString(1,  fortToCheck);
				      rs = prepStmt.executeQuery();
				      break;
		      }      
		      
		      if (rs.next() == false)
			  {
				   System.out.println("Fortress record not found. Returning false.");
				   if (rs != null)
				   {
					   rs.close();
				   }
				   //attemptConnectionClose(d);
				   return false;
			  }
		     
		      

		      
		      
		      
		      rs.close();
		      System.out.println("MySQL Fortress lookup successful! (rs.close debug) ");
		      //attemptConnectionClose(d);
		      //stmt.close();
		      //c.close();
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		System.out.println("TRUE ENTRY CHECK");
		return true;
	}
	
	public boolean isPlayerMemberOfFortress(String playerToCheck, String fortressname, Connection passedConnection, boolean carryFlag) // (SECURED)  (MySQL'd) (switch'd)
	{
		if (passedConnection == null && carryFlag == false)
		{
			establishConnection(); // call method to establish connection
			getUUIDData(playerToCheck);
		}
		//Connection d = null;
	    //Statement stmt = null;
	    PreparedStatement prepStmt = null;
	    System.out.println("IS PLAYER MEMBER ENTRY");
	    ResultSet rs2 = null;
	    //((CraftWorld)Bukkit.getWorld("world")).getHandle())
	   
	    
	    
		if (doesFortressExist(fortressname) == true)
		{
			try {
			      Class.forName("org.sqlite.JDBC");
			      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
			      System.out.println("Opened database successfully (checking if player is member of fortress)");
			      System.out.println(playerToCheck);
			      System.out.println(fortressname);
			      System.out.println("Pre-create statement.");
			      //stmt = c.createStatement();
			      /*String selectstring = 
			    		  
			    		  "SELECT PLAYER FROM FORTRESS_MEMBER_LOOKUP " +
				    		  		"WHERE " +
				    		  		"PLAYER_UUID = ? AND " +
				    		  		"FORTRESS = ? "
				    		  		;
			      
			      prepStmt = c.prepareStatement(selectstring);
			      prepStmt.setString(1,  verifiedplayer.toString());
			      prepStmt.setString(2,  fortressname);
			      rs2 = prepStmt.executeQuery();*/
			      
			      switch (DBMode)
			      {
			      	case 0:
						      String selectstring2 = "SELECT PLAYER from FORTRESS.FORTRESS_MEMBER_LOOKUP " +
						    		  				 "WHERE " +
						    		  				"PLAYER_UUID = ? AND " +
								    		  		"FORTRESS = ? ";
						      
						      //d = null;
						      PreparedStatement prepStmt2 = null;
						      
						    
						    	  //d = rootHikari.getConnection();
						    	  prepStmt2 = d.prepareStatement(selectstring2);	
							      prepStmt2.setString(1,  verifiedplayer.toString());
							      System.out.println("Remove member debug (UUID): " + verifiedplayer.toString());
							      prepStmt2.setString(2,  fortressname);
							      System.out.println("Remove member debug (Fort name): " + fortressname);
							      rs2 = prepStmt2.executeQuery();
						      
						      
						      
						      System.out.println("MySQL Fortress lookup successful! (select player from fortress member lookup");
						      
						      //stmt.executeUpdate(sql);
					      break;
			      	case 1: 
				      		  String selectstring = 
				    		  
				    		  "SELECT PLAYER FROM FORTRESS_MEMBER_LOOKUP " +
					    		  		"WHERE " +
					    		  		"PLAYER_UUID = ? AND " +
					    		  		"FORTRESS = ? "
					    		  		;
				      
						      prepStmt = c.prepareStatement(selectstring);
						      prepStmt.setString(1,  verifiedplayer.toString());
						      prepStmt.setString(2,  fortressname);
						      rs2 = prepStmt.executeQuery();
			      		
			      }   
			      
			      
			      

			      if (rs2.next() == false)
			      {
			    	  System.out.println("MATCH IS NOT EQUAL");
			    	  //stmt.close();
			    	  if (rs2 != null)
			    	  {
			    		  rs2.close();
			    	  }
				      
				      // case switch for closing MySQL connection goes here.
				      //attemptConnectionClose(d);
			    	  if (passedConnection == null && carryFlag == false)
				      {
				    	    System.out.println("WARNING: connection is null");
							severConnection(); // call method to establish connection
				      }
				      
			    	  return false;
			      } 
			      
			    
			      
			      System.out.println("------- Player is a member of the fortress. -------");
			      
			      
			      
			      
			      //stmt.close();
			      rs2.close();	      
			      //c.close();
			      //attemptConnectionClose(d); // case switch for closing MySQL connection goes here.
			      if (passedConnection == null && carryFlag == false)
			      {
			    	    System.out.println("WARNING: connection is null");
						severConnection(); // call method to establish connection
			      }
			      return true;
		    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		    }
		}
		else if (doesFortressExist(fortressname) == false)
		{
			System.out.println("Fortress doesn't exist. (isPlayerMemberOfFortress");
			if (passedConnection == null && carryFlag == false)
		     {
					severConnection(); // call method to establish connection
		     }
			return false;
		}
		if (passedConnection == null && carryFlag == false)
	     {
				severConnection(); // call method to establish connection
	     }
		return false;
	}

	public boolean checkIfPlayerIsCreator(String playerToCheck)
	{
		establishConnection();
		
		PreparedStatement prepStmt = null;
	    System.out.println("IS PLAYER MEMBER ENTRY");
	    ResultSet rs2 = null;
	    String selectstring2 = null;
	    PreparedStatement prepStmt2 = null;
	    
	    try{
	    	Class.forName("org.sqlite.JDBC");
	    	System.out.println("Opened database successfully (checking if player is creator of a fortress)");
	    	switch (DBMode)
		    {
		      	case 0:
		      		 selectstring2 = "SELECT FORTRESS_CREATOR from FORTRESS.FORTRESS_LOOKUP " +
   		  				 "WHERE " +
   		  				"FORTRESS_CREATOR = ?";
     
		      		
		      		prepStmt2 = d.prepareStatement(selectstring2);
		      		prepStmt2.setString(1, playerToCheck);
		      		rs2 = prepStmt2.executeQuery();
		      		
		      		break;
		      	case 1:
		      		
		      		 selectstring2 = "SELECT FORTRESS_CREATOR from FORTRESS_LOOKUP " +
	   		  				 "WHERE " +
	   		  				"FORTRESS_CREATOR = ?";
	     
			      		
			      		prepStmt2 = c.prepareStatement(selectstring2);
			      		prepStmt2.setString(1, playerToCheck);
			      		rs2 = prepStmt2.executeQuery();
		      		break;
		    }
	    	
	    	if (rs2.next() == false)
		      {
	    		System.out.println("Player is NOT a creator of a fortress!");
	    		severConnection();
	    		return false;
		      }
	    	System.out.println("Player has already created a fortress!");
	    	severConnection();
	    	return true;
	    } catch ( Exception e ) {
		  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		  //System.exit(0);
		}
	    severConnection();
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

    public void getUUIDData(String playertocheck)

    {
    	System.out.println("UUID CALL");
    	if (Bukkit.getServer().getPlayer(playertocheck) == null)
    	{
    		verifiedplayer = Bukkit.getServer().getOfflinePlayer(playertocheck).getUniqueId();
    		verifiedplayername = Bukkit.getServer().getOfflinePlayer(playertocheck).getName();
    		System.out.println("entered player was offline, data retrieved/generated... ");
    	} else
    	{
    		verifiedplayer = Bukkit.getServer().getPlayer(playertocheck).getUniqueId();
    		verifiedplayername = Bukkit.getServer().getPlayer(playertocheck).getName();
    	}
    }

    public String getFortressAlignmentFromDB(String player, HashMap<String, F_DBPlayerFortressPrivs> fortressPlayerPermissionsRef)  // (SECURED)
    {
    	establishConnection();
    	//Connection c = null;
	    //Statement stmt = null;
	    System.out.println("IS PLAYER MEMBER ENTRY");
	    String returnString = "";
	    PreparedStatement prepStmt = null;
	    getUUIDData(player);
	    
	    //((CraftWorld)Bukkit.getWorld("world")).getHandle())
	    
	    
	    
		
			try {
			      //Class.forName("org.sqlite.JDBC");
			      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
			      System.out.println("Opened database successfully (checking if player is aligned to a fortress)");
			      System.out.println(player);
			      //System.out.println(fortressName);
			      System.out.println("Pre-create statement....(new)");
			      //stmt = c.createStatement();
			      String selectstring;
			      System.out.println("DB mode: " + DBMode);
			      switch (DBMode)
			      {
				      case 0:
				    	  selectstring = 
		    		  		"SELECT ALIGNMENT FROM FORTRESS.PLAYER_LOOKUP " +
		    		  		"WHERE " +
		    		  		"PLAYER_UUID = ? " 
		    		  		;
		      
					      prepStmt = d.prepareStatement(selectstring);
					      prepStmt.setString(1,  verifiedplayer.toString());
					      System.out.println("BREAK 0");
					      break;
				      case 1:
				    	  selectstring = 
		    		  		"SELECT ALIGNMENT FROM PLAYER_LOOKUP " +
		    		  		"WHERE " +
		    		  		"PLAYER_UUID = ? " 
		    		  		;
				    	  System.out.println("BREAK 1");
					      prepStmt = c.prepareStatement(selectstring);
					      prepStmt.setString(1,  verifiedplayer.toString());
					      break;
			      }
			      
			      System.out.println("Pre-create statement....(new) 2");
			      ResultSet rs2 = prepStmt.executeQuery();  
			      System.out.println("Pre-create statement....(new) 3");
			      if (rs2.next() == false)
			      {
			    	  System.out.println("PLAYER NOT ALIGNED TO ANY FORTRESS, OR PLAYER RECORD NOT FOUND.");
			    	  //stmt.close();
				      rs2.close();
			    	  return null;
			      } 
			      
			    
			      
			      System.out.println("PLAYER IS ALIGNED TO A FORTRESS.");
			      System.out.println("HELLO");
			      returnString = rs2.getString("ALIGNMENT");
			      
			      
			      
			      //stmt.close();
			      rs2.close();
			      //c.close();
			      
			      
		    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		    }
		severConnection();
		return returnString;
    }

    public void setAlignment(Player player, HashMap<String, F_DBPlayerFortressPrivs> fortressPlayerPermissionsRef, String playertochange, String fortresstochange, int updateflag) // (SECURED)
    {
    	establishConnection(); // call method to establish connection
	    Statement stmt = null; // reset the "stmt" variable
	    PreparedStatement prepStmt = null;
	    String selectstring;
	    
	    // retrieve player's in game/memory permissions object(s)
	    F_DBPlayerFortressPrivs playerPerms = null;
	    if (fortressPlayerPermissionsRef.containsKey(playertochange))
	    {
	    	playerPerms = fortressPlayerPermissionsRef.get(Bukkit.getServer().getPlayer(playertochange).getName());
	    }
	    //List<String> listToUpdate = null;
	    
	    
	    //List<String> listToUpdate = playerPerms.getPlayerFortressMembership();
	    
	    System.out.println("PRE-UUID CALL");
	    getUUIDData(playertochange);
	    
	    switch (updateflag)
	    {
	    	case 0:  // set alignment (ADMIN)
	    	///////////////////////////////////////////////////////////////////////////////////////////////
	    		String adminAlignMessage; 
	    		if (doesFortressExist(fortresstochange)) 
	    		{
	    			// do the following if the player already has an entry.
		    		if (doesPlayerHaveAlignmentEntry(playertochange))
		    		{
		    			// update the database entry.
					    try {
					    	Class.forName("org.sqlite.JDBC");
						      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
						      System.out.println("Attempting to update player alignment...");
				
						      //stmt = c.createStatement();
						      
						      switch (DBMode)
						      {
							      case 0:
								      System.out.println("Instantiation of stmt variable successful...");
								      selectstring = "UPDATE FORTRESS.PLAYER_LOOKUP " +
								    		  	  "SET ALIGNMENT = ? " +
								                  "WHERE PLAYER_UUID = ? "; 
								                 
								      prepStmt = d.prepareStatement(selectstring);
								      prepStmt.setString(1, fortresstochange);
								      prepStmt.setString(2, verifiedplayer.toString());
								      break;
							      case 1:
								      System.out.println("Instantiation of stmt variable successful...");
								      selectstring = "UPDATE PLAYER_LOOKUP " +
								    		  	  "SET ALIGNMENT = ? " +
								                  "WHERE PLAYER_UUID = ? "; 
								                 
								      prepStmt = c.prepareStatement(selectstring);
								      prepStmt.setString(1, fortresstochange);
								      prepStmt.setString(2, verifiedplayer.toString());
								      break;
						      }
						      prepStmt.executeUpdate(); // use this for updates in prepared statements!!
						      
						      //stmt.executeUpdate(selectstring);
	
						      //stmt.close();
					          
						      //c.close();
						      System.out.println("Update alignment query run successfully... (updated row)");
						      
					    } catch ( Exception e ) {
					      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					      System.exit(0);
					    }

					    finalAdminString = stringBuilder.constructAdminAlignmentChangeSuccessful(verifiedplayername, fortresstochange);
					    player.spigot().sendMessage(finalAdminString.create());
					    //update the in-game entry, IF they are online
					    if (fortressPlayerPermissionsRef.containsKey(playertochange))
					    {
					    	playerPerms.setFortressAlignment(fortresstochange);
					    	finalString = stringBuilder.constructStringAlignmentChanged(fortresstochange);
					    	Bukkit.getServer().getPlayer(playertochange).spigot().sendMessage(finalString.create());
					    }
		    		} else
		    			
		    		// do the following if the player doesn't have an entry.
		    		{
		    			try {
					    	Class.forName("org.sqlite.JDBC");
						      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
						      System.out.println("Attempting to update player alignment...");
				
						      //stmt = c.createStatement();
						      System.out.println("Instantiation of stmt variable successful...");
						      
						      switch (DBMode)
						      {
						      case 0:
							      selectstring = "INSERT INTO PLAYER_LOOKUP (PLAYER_UUID, ALIGNMENT) " +
							    		  	  "VALUES " +
		 					    		  	  "(?, ?)"; 
							                 
							      //stmt.executeUpdate(selectstring);
							      prepStmt = d.prepareStatement(selectstring);
							      prepStmt.setString(1, verifiedplayer.toString());
							      prepStmt.setString(2, fortresstochange);
							      break;
						      case 1:
							      selectstring = "INSERT INTO PLAYER_LOOKUP (PLAYER_UUID, ALIGNMENT) " +
							    		  	  "VALUES " +
		 					    		  	  "(?, ?)"; 
							                 
							      //stmt.executeUpdate(selectstring);
							      prepStmt = c.prepareStatement(selectstring);
							      prepStmt.setString(1, verifiedplayer.toString());
							      prepStmt.setString(2, fortresstochange);
							      break;
						      }
						      
						      prepStmt.executeUpdate(); // use this for updates in prepared statements!!
	
						      //stmt.close();
					          
						      //c.close();
						      System.out.println("Update alignment query run successfully...(new row)");
					      
					    } catch ( Exception e ) {
					      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					      System.exit(0);
					    }
					    
					    
		    			finalAdminString = stringBuilder.constructAdminAlignmentChangeSuccessful(verifiedplayername, fortresstochange);
					    player.spigot().sendMessage(finalAdminString.create());
					    //update the in-game entry, IF they are online
					    if (fortressPlayerPermissionsRef.containsKey(playertochange))
					    {
					    	playerPerms.setFortressAlignment(fortresstochange);
					    	finalString = stringBuilder.constructStringAlignmentChanged(fortresstochange);
					    	Bukkit.getServer().getPlayer(playertochange).spigot().sendMessage(finalString.create());
					    }
		    		}
	    		}
	    	
	    
	    }
	    severConnection();
    }

    public String getAlignment() // used only when printing list of a player's fortress memberships. (called by listPlayerMemberships)  // (SECURED)
    {
    	//Statement stmt = null;
    	String returnstring = null;
    	PreparedStatement prepStmt = null;
    	String selectstring;
		
		try {
		      //Class.forName("org.sqlite.JDBC");
		      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
		      System.out.println("Opened database successfully (checking if fortress exists)");
	
		      //stmt = c.createStatement();
		      switch (DBMode)
		      {
			      case 0:
				      selectstring = 
				    		  		"SELECT ALIGNMENT FROM FORTRESS.PLAYER_LOOKUP " +
				    		  		"WHERE " +
				    		  		"PLAYER_UUID = ? " +
				    		  		"AND ALIGNMENT IS NOT NULL"
				    		  		;
				      
				      //stmt.executeUpdate(sql);
				      prepStmt = d.prepareStatement(selectstring);
				      prepStmt.setString(1,  verifiedplayer.toString());
				      break;
			      case 1:
				      selectstring = 
				    		  		"SELECT ALIGNMENT FROM PLAYER_LOOKUP " +
				    		  		"WHERE " +
				    		  		"PLAYER_UUID = ? " +
				    		  		"AND ALIGNMENT IS NOT NULL"
				    		  		;
				      
				      //stmt.executeUpdate(sql);
				      prepStmt = c.prepareStatement(selectstring);
				      prepStmt.setString(1,  verifiedplayer.toString());
				      break;
		      }
		      ResultSet rs = prepStmt.executeQuery();
		      
		      if (rs.next() == false)
		      {
		    	  System.out.println("PLAYER IS NOT ALIGNED TO ANY FORTRESS.");
		    	  return null;
		      }
		      
		      System.out.println("PLAYER IS ALIGNED TO FORTRESS: ");
		      
		      
		      returnstring = rs.getString("ALIGNMENT");
		      
		      rs.close();
		      //stmt.close();
		      //c.close();
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		
		return returnstring;
    }

    public void removeAlignment(Player player, HashMap<String, F_DBPlayerFortressPrivs> fortressPlayerPermissionsRef, String playertochange) // (SECURED) (MySQL'd) (switch'd)
    {
    	//Statement stmt = null; // reset the "stmt" variable
    	PreparedStatement prepStmt = null;
    	
	    // retrieve player's in game/memory permissions object(s)
	    F_DBPlayerFortressPrivs playerPerms = null;
	    String selectstring;
	    if (fortressPlayerPermissionsRef.containsKey(playertochange))
	    {
	    	playerPerms = fortressPlayerPermissionsRef.get(Bukkit.getServer().getPlayer(playertochange).getName());
	    }
	    //List<String> listToUpdate = null;
    	
    	try {
	    		//Class.forName("org.sqlite.JDBC");
		      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
		      System.out.println("Attempting to update player alignment...");

		      //stmt = c.createStatement();
		      System.out.println("Attempting to set player alignment to null....");
		      System.out.println(verifiedplayer.toString());
		      switch (DBMode)
		      {
		          case 0:
		        	  selectstring = "UPDATE FORTRESS.PLAYER_LOOKUP " +
			    		  	  "SET ALIGNMENT = NULL " +
			                  "WHERE PLAYER_UUID = ? "; 
			      
		        	  prepStmt = d.prepareStatement(selectstring);
		        	  prepStmt.setString(1, verifiedplayer.toString());
		        	  break;
		        	  
			      case 1:
				      selectstring = "UPDATE PLAYER_LOOKUP " +
				    		  	  "SET ALIGNMENT = NULL " +
				                  "WHERE PLAYER_UUID = ? "; 
				      
				      prepStmt = c.prepareStatement(selectstring);
				      prepStmt.setString(1, verifiedplayer.toString());
				      break;
		      }
		      prepStmt.executeUpdate(); // use this for updates in prepared statements!!
		      
		      //stmt.executeUpdate(rs);

		      //stmt.close();
	          
		      //c.close();
		      System.out.println("Remove alignment query run successfully... (updated row)");
		      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
    	
    	if (fortressPlayerPermissionsRef.containsKey(playertochange))
	    {
	    	playerPerms.setFortressAlignment(null);
	    	//finalString = stringBuilder.constructStringAlignmentChanged(fortresstochange);
	    	//Bukkit.getServer().getPlayer(playertochange).spigot().sendMessage(finalString.create());
	    }
    }
    
    public boolean doesPlayerHaveAlignmentEntry(String player) // (SECURED)
    {
    	//Connection c = null;
	    //Statement stmt = null;
	    PreparedStatement prepStmt = null;
	    getUUIDData(player);
	    String selectstring;
		
		try {
		      //Class.forName("org.sqlite.JDBC");
		      //c = DriverManager.getConnection("jdbc:sqlite:test.db");
		      System.out.println("Opened database successfully (checking if fortress exists -- SQLITE)");
	
		      /*stmt = c.createStatement();
		      ResultSet rs = stmt.executeQuery(
		    		  		"SELECT PLAYER_UUID FROM PLAYER_LOOKUP " +
		    		  		"WHERE " +
		    		  		"PLAYER_UUID = '" + verifiedplayer + "'"
		    		  		);*/
		      
		      //stmt.executeUpdate(sql);
		      //System.out.println("DBMode check: " + DBMode);
		      switch (DBMode)
		      {
			      case 0:
				      selectstring = "SELECT PLAYER_UUID FROM FORTRESS.PLAYER_LOOKUP " +
			    		  		"WHERE " +
			    		  		"PLAYER_UUID = ?";
				      prepStmt = d.prepareStatement(selectstring);
				      prepStmt.setString(1,  verifiedplayer.toString());
				      break;
			      case 1:
				      selectstring = "SELECT PLAYER_UUID FROM PLAYER_LOOKUP " +
			    		  		"WHERE " +
			    		  		"PLAYER_UUID = ?";
				      prepStmt = c.prepareStatement(selectstring);
				      prepStmt.setString(1,  verifiedplayer.toString());
				      break;
		      }
		      ResultSet rs = prepStmt.executeQuery();
		      
		      
		      if (rs.next() == false)
		      {
		    	  System.out.println("PLAYER IS NOT ALIGNED TO ANY FORTRESS.");
		    	  return false;
		      }
		      
		      System.out.println("PLAYER IS ALIGNED TO FORTRESS: ");

		      
		      
		      
		      rs.close();
		      //stmt.close();
		      //c.close();
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
		
		return true;
    }
    
    public void updateFortHeartLocation(String fortname, Player player, int x, int y, int z)
    {
    	establishConnection();
    	
    	PreparedStatement prepStmt = null;
    	String selectstring;
    	
		try {
			System.out.println("Attempting to update fortress heart location...");
			switch (DBMode)
		      {
				case 0:
					selectstring = "UPDATE FORTRESS.FORTRESS_LOOKUP " +
			    		  	  "SET HEART_X = ?, " +
			    		  	  "HEART_Y = ?, " +
			    		  	  "HEART_Z = ?, " +
			    		  	  "HEART_WORLD = ? " +
			                  "WHERE FORTRESS = ? "; 
					
					prepStmt = d.prepareStatement(selectstring);
					prepStmt.setInt(1, x);
					prepStmt.setInt(2, y);
					prepStmt.setInt(3, z);
					prepStmt.setString(4, player.getWorld().getName());
					prepStmt.setString(5, fortname);
					
					break;
				case 1:
					
					selectstring = "UPDATE FORTRESS_LOOKUP " +
			    		  	  "SET HEART_X = ?, " +
			    		  	  "HEART_Y = ?, " +
			    		  	  "HEART_Z = ?, " +
			    		  	  "HEART_WORLD = ? " +
			                  "WHERE FORTRESS = ? "; 
					
					prepStmt = c.prepareStatement(selectstring);
					prepStmt.setInt(1, x);
					prepStmt.setInt(2, y);
					prepStmt.setInt(3, z);
					prepStmt.setString(4, player.getWorld().getName());
					prepStmt.setString(5, fortname);
					
					break;
		      }
			
			  prepStmt.executeUpdate(); // use this for updates in prepared statements!!
			  
		}
		
		catch ( Exception e ) 
		{
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		}
		
		
		finalString = stringBuilder.returnSuccessfulMovedHeart(fortname, x, y, z);
    	Bukkit.getServer().getPlayer(player.getName()).spigot().sendMessage(finalString.create());
    	severConnection();
    }

    public Location getFortHeartLocation(String fortname, Player player)
    
    {
    	establishConnection();
    	PreparedStatement prepStmt = null;
    	String selectstring;
    	Location chestLoc;
		try {
			System.out.println("Attempting to retrieve fortress heart location...");
			switch (DBMode)
		      {
				case 0:
					selectstring = "SELECT HEART_X, HEART_Y, HEART_Z, HEART_WORLD FROM FORTRESS.FORTRESS_LOOKUP " +
			                  "WHERE FORTRESS = ? ";  
					
					prepStmt = d.prepareStatement(selectstring);
					prepStmt.setString(1, fortname);
					
					break;
				case 1:
					
					selectstring = "SELECT HEART_X, HEART_Y, HEART_Z, HEART_WORLD FROM FORTRESS_LOOKUP " +
			                  "WHERE FORTRESS = ? "; 
					
					prepStmt = c.prepareStatement(selectstring);
					prepStmt.setString(1, fortname);
					
					break;
		      }
			
			  ResultSet rs = prepStmt.executeQuery(); // use this for updates in prepared statements!!
			  
			  if (rs.next() == false)
		      {
		    	  System.out.println("No record found. returning null...");
		    	  return null;
		      }
			  
			  chestLoc = new Location(Bukkit.getWorld(rs.getString("HEART_WORLD")), rs.getDouble("HEART_X"), rs.getDouble("HEART_Y"), rs.getDouble("HEART_Z"));
			  severConnection();
			  return chestLoc;
			  //chestLoc.setX(rs.getDouble("HEART_X"));
			  
			  
		}
		
		catch ( Exception e ) 
		{
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		}
		
    	severConnection();
    	return null;
    }
    
    public void attemptConnectionClose(Connection conn)

    
    {
    	switch (DBMode)
	      {
	      	case 0:
	      		if (conn != null)
	      		{
	      			try {
	      				conn.close();
	      			} catch (SQLException e)
	      			{
	      				e.printStackTrace();
	      			}
	      		}
	      		break;
	      	case 1:
	      		break;
	      }
    }

    public void permitFortsInWorld(String worldname, Player player)
    {
   	 	establishConnection();
   	 	PreparedStatement prepStmt = null;
	    String selectstring;
	    String preselect;
	    ResultSet rs = null;
	    
	    try {
	    	System.out.println("Attempting to permit forts in a world. ");
	    	switch (DBMode)
	    	{
	    		case 0:
	    			preselect = "SELECT WORLD FROM FORTRESS.FORTRESS_WORLDS WHERE WORLD = ?";
	    			prepStmt = d.prepareStatement(preselect);
	    			prepStmt.setString(1, worldname);
	    			rs = prepStmt.executeQuery();
	    			//prepStmt.close();
	    			if (rs.next() == false)
	    			{
	    				selectstring = "INSERT INTO FORTRESS.FORTRESS_WORLDS (WORLD) VALUES (?) ";
	    				prepStmt = d.prepareStatement(selectstring);
	    				prepStmt.setString(1, worldname);
	    				prepStmt.executeUpdate();
	    				
	    				if (rootPlugin.getBuildPermitList() == null)
	    				{
	    					List<String> newList = new ArrayList<String>();
	    					newList.add(worldname);
	    					rootPlugin.createBuildPermitList(newList);
	    				} else
	    				{
	    					rootPlugin.getBuildPermitList().add(worldname);
	    				}
	    				//prepStmt.close();
	    				break;
	    			}
	    			break;
	    		case 1:
	    			preselect = "SELECT WORLD FROM FORTRESS_WORLDS WHERE WORLD = ?";
	    			prepStmt = c.prepareStatement(preselect);
	    			prepStmt.setString(1, worldname);
	    			rs = prepStmt.executeQuery();
	    			//prepStmt.close();
	    			if (rs.next() == false)
	    			{
	    				selectstring = "INSERT INTO FORTRESS_WORLDS (WORLD) VALUES (?) ";
	    				prepStmt = d.prepareStatement(selectstring);
	    				prepStmt.setString(1, worldname);
	    				prepStmt.executeUpdate();
	    				
	    				if (rootPlugin.getBuildPermitList() == null)
	    				{
	    					List<String> newList = new ArrayList<String>();
	    					newList.add(worldname);
	    					rootPlugin.createBuildPermitList(newList);
	    				} else
	    				{
	    					rootPlugin.getBuildPermitList().add(worldname);
	    				}
	    				break;
	    			}
	    			break;
	    	}
	    	//prepStmt.executeUpdate();
	    	prepStmt.close();
	    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		}
	    
	    ComponentBuilder finalString = stringBuilder.returnWorldNowPermitted(worldname);
	    Bukkit.getServer().getPlayer(player.getName()).spigot().sendMessage(finalString.create());
   	 	severConnection();
    }
    
    public void denyFortsInWorld(String fortressname, Player player)
    {
    	establishConnection();
   	 	PreparedStatement prepStmt = null;
	    String selectstring;
	    
	    try{
	    	System.out.println("Attempting to deny forts in a world. ");
	    	switch (DBMode)
	    	{
	    		case 0:
	    			selectstring = "DELETE FROM FORTRESS.FORTRESS_WORLDS WHERE WORLD = ? ";
	    			prepStmt = d.prepareStatement(selectstring);
	    			prepStmt.setString(1, fortressname);
	    			prepStmt.executeUpdate();
	    			removeFortInPermitList(fortressname);
	    			break;
	    		case 1:
	    			selectstring = "DELETE FROM FORTRESS_WORLDS WHERE WORLD = ? ";
	    			prepStmt = c.prepareStatement(selectstring);
	    			prepStmt.setString(1, fortressname);
	    			prepStmt.executeUpdate();
	    			removeFortInPermitList(fortressname);
	    			break;
	    	}
	    	//prepStmt.executeUpdate();
	    	prepStmt.close();
	    }catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		}
	    
	    ComponentBuilder finalString = stringBuilder.returnWorldNowDenied(fortressname);
	    Bukkit.getServer().getPlayer(player.getName()).spigot().sendMessage(finalString.create());
	    severConnection();
    }

    public void removeFortInPermitList(String fortressname)
    {
    	List<String> permitList = rootPlugin.getBuildPermitList();
    	boolean foundFlag = false;
    	int indexRef = 0; 
    	for (int i = 0; i < permitList.size(); i++)
    	{
    		if (fortressname.equals(permitList.get(i)))
    		{
    			//return fortressname;
    			foundFlag = true;
    			indexRef = i;
    		}
    		
    	}
    	
    	if (foundFlag == true)
    	{
    		permitList.remove(indexRef);
    	}
    	//return null;
    }
}

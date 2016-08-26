package fortress.plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;














import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
//import de.inventivegames.particle.ParticleEffect;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.EntityZombie;
import net.minecraft.server.v1_9_R1.Material;
import net.minecraft.server.v1_9_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_9_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_9_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*; 							// used for Field data type.

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

//import fortress.plugin.fortress_root.CustomZombie;

public class fortress_root extends JavaPlugin implements Listener
{
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	private HikariDataSource hikari;
	private FileConfiguration special;
	
	
	public int DBConnectType = 0;  // the type of database the plugin connects to. 0 = MySQL, 1= SQLite
	F_ChatStringBuilder rootstringbuilder;
	
	public ProtocolManager fortProtocolManager;
	
	//F_SQLEntityDataQueries InitDB = new F_SQLEntityDataQueries(); // spawns an instance of the SQL query controller. May need to spawn multiple objects if there are too many performance problems.
	F_SQLEntityDataQueries InitDB;
	//F_SQLFortressDataQueries FortressDB = new F_SQLFortressDataQueries(); 
	F_SQLFortressDataQueries FortressDB;
	
	F_FortRegionManager rootRegionManager = new F_FortRegionManager();
	//ProtocolManager fortProtocolManager = ProtocolLibrary.getProtocolManager();
	HashMap<String, F_DBPlayerFortressPrivs> FortressPlayerPermissions = new HashMap<String, F_DBPlayerFortressPrivs>();  // insert line for player fortress permissions hashmap here.
	HashMap<String, F_SQLPlayerDataQueries> FortressPlayerQueryController = new HashMap<String, F_SQLPlayerDataQueries>(); // hashmap that points to each player's query controller. 
	HashMap<String, F_DBFortressData> FortressDataController = new HashMap<String, F_DBFortressData>(); // hashmap for storing fortress metadata.
	
	List<String> buildPermitList; // list of worlds that normal users can build fortresses in.
	
	public void onEnable()
	{
		
		// **************************************packet listener set up
		fortProtocolManager = ProtocolLibrary.getProtocolManager();

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.ENTITY_METADATA){
			@Override
			public void onPacketSending(PacketEvent event)
			{
				
				PacketContainer packet = event.getPacket();
				//WrappedPlayServerEntityMetadata wrappeddata = WrappedPlayServerEntityMetadata(packet);
				org.bukkit.entity.Entity entity = packet.getEntityModifier(event.getPlayer().getWorld()).read(0);
				F_DBPlayerFortressPrivs packetFortRef = FortressPlayerPermissions.get(event.getPlayer().getName());
				//System.out.println("yey packets!! " + entity.getUniqueId() + "---" + event.getPlayer().getName());
				if ( entity.getCustomName() != "" )
				{
					WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity);
					Serializer serializer = Registry.get(String.class);
					WrappedDataWatcherObject object = new WrappedDataWatcherObject(0, serializer);
					if (packetFortRef.checkIfAlliedWithEntity(entity.getUniqueId(), event.getPlayer().getName()) && entity.getType() == EntityType.RABBIT)
					{					
						watcher.setObject(3, true);			
						//System.out.println("packet discovered as allied!!!!: " + packet.getEntityModifier(event.getPlayer().getWorld()));
					}
					
					if (!packetFortRef.checkIfAlliedWithEntity(entity.getUniqueId(), event.getPlayer().getName()) && entity.getType() == EntityType.RABBIT)
					{
						watcher.setObject(3, false);
					}
				}
				//if (FortressPlayerPermissions.get(event.getPlayer().getName()).)
			}
		});
		
		
		
		
		if (!this.getDataFolder().exists())
		{
			this.getDataFolder().mkdirs();
		}
		
		
		File defaultfile = new File(getDataFolder(), "fortress.yml");
		if (!defaultfile.exists()) 
		{
			getLogger().info("config.yml not found, creating!!!");
			System.out.println(getDataFolder());
			defaultfile.getParentFile().mkdirs();
			//saveResource("config.yml", false);
			
			special = new YamlConfiguration();
			
			try{
				defaultfile.createNewFile();
				System.out.println(defaultfile.getAbsolutePath());
				saveResource("fortress.yml", true); // initial save from the Jar
				
			} catch(IOException e)
			{
				e.printStackTrace();
			}
			
			
			
			try{
				special.load(defaultfile);
				
			} catch(Exception e)
			{
				e.printStackTrace();
			}
			
			// set file configs here
			getMainConfig().set("database.MySQL.username", "Fortress");
			getMainConfig().set("database.MySQL.password", "Fortress");
			getMainConfig().set("database.MySQL.URL", "Fortress");
			getMainConfig().set("database.MySQL.poolsize", "Fortress");
			getMainConfig().set("database.MySQL.fortresspriority", "5");
			
			try{
				special.save(defaultfile);
				
			} catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			//special.save(defaultfile);
			//saveResource("fortress.yml", true);
			
			//getMainConfig().set("database-name", "something");
			//saveResource("config.yml", false);
			
			//this.saveDefaultConfig();
			//getConfig().set("database-name", "Fortress");
		} else
		{
			getLogger().info("config.yml found, loading!");
			special = new YamlConfiguration();
			
			try{
				special.load(defaultfile);
				
			} catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		switch (DBConnectType) // switch for choosing the correct database type. 
		{
			case 0:
				setupHikariDataSource();
				testConnect();
				break;
		}
		InitDB = new F_SQLEntityDataQueries();
		FortressDB = new F_SQLFortressDataQueries(); 
		rootstringbuilder = new F_ChatStringBuilder();
		
		

		FortressDB.createFortressHashmapOnEnable(FortressDataController); // load up the fortress data hashmap.
		
		buildPermitList = FortressDB.getPermittedWorlds(); // load up the build permit list, by reading the database.
		
		F_EventListener F_EL = new F_EventListener(FortressPlayerPermissions, FortressDataController);
		F_EventListenerLogOnOrOff F_PL = new F_EventListenerLogOnOrOff(FortressPlayerPermissions, FortressPlayerQueryController); // register a listener that has the permissions index passed to it.  
		
		PluginDescriptionFile pdffile= this.getDescription();
		this.logger.info(pdffile.getName() + "has been Enabled!!!!");
		//getServer().getPluginManager().registerEvents(F_)
		getServer().getPluginManager().registerEvents(F_EL, this);
		getServer().getPluginManager().registerEvents(F_PL, this);
		//EntityTypes.spawnEntity(new CustomZombie(((CraftWorld)Bukkit.getWorld("world")).getHandle()), new Location(Bukkit.getWorld("world"), 100, 100, 100));
		EntityTypes.pTest(); // initialize ENUM for new entities.
	}
	
	public FileConfiguration getMainConfig() {
		return special;
	}
	
	public void onDisable()
	{
		PluginDescriptionFile pdffile= this.getDescription();
		saveFortressHeartsOnDisable();
		
		this.logger.info(pdffile.getName() + " has been Disabled!!!!");
	}
	
	
	// *******************************************
	// command calls
	// *******************************************
	
	public boolean onCommand(CommandSender sender, Command cmd,  String label, String[] args)
	{
		//if(cmd.getName().equalsIgnoreCase("hi"))
		
		if(label.equalsIgnoreCase("fortadmin") && args.length > 0)
		{
			
			
			// attempts to remove a person's membership from a fortress. 
			if (args[0].equals("removemember") && args.length == 3) // need to pass: player, FortressPlayerPermissions, the player to change, the fortress to change
			{
				System.out.println("COMMAND TEST: 1 (remove)");
				if (args[1] != null && args[2] != null)
				{
					System.out.println("COMMAND TEST: 2 (remove)");
					Player player = (Player) sender;
					F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName());  // get the individual SQL controller assigned to the player running the command.
					playerDataRetriever.updateFortressMembership(player, FortressPlayerPermissions, args[1], args[2], 0, null); // false indicates this person is being deleted from a fortress. 
					
				}
				
				
				
			}
			
			
			// attempts to remove a person's membership from a fortress. 
			if (args[0].equals("addmember") && args.length == 3 ) // need to pass: player, FortressPlayerPermissions, the player to change, the fortress to change
			{
				System.out.println("COMMAND TEST: 2");
				if (args[1] != null && args[2] != null)
				{
					System.out.println("COMMAND TEST: 4");
					Player player = (Player) sender;
					F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName());  // get the individual SQL controller assigned to the player running the command.
					playerDataRetriever.updateFortressMembership(player, FortressPlayerPermissions, args[1], args[2], 1, null); // true indicates a person is geing added to a fortress. 
					
				} else
				{
					System.out.println("format!");
				}

			}
			
			
			if (args[0].equals("createfort") && args.length == 3)
			{
				System.out.println("COMMAND TEST: 23");
				if (args[1] != null && args[2] != null && FortressPlayerPermissions.containsKey(args[2]))
				{
					System.out.println("COMMAND TEST: 4");
					Player player = (Player) sender;
					F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName()); // get the individual SQL controller assigned to the player running the command.
					playerDataRetriever.updateFortress(player, FortressPlayerPermissions, args[1], args[2], 1); // true indicates this is an admin command.
					
					// need if statements here, only execute if a fortress is in the hashmap.
					if (FortressDataController.containsKey(args[1]))
					{
						System.out.println("Fortress hash contains entry, preparing next chest and creating heart item index.");
						FortressPlayerPermissions.get(args[2]).setCreateFortressOnNextClick(true);
						FortressPlayerPermissions.get(args[2]).setCreateFortressString(args[1]);
						//FortressDB.createFortressHeartItemIndex(args[1]);
						System.out.println("Fortress hash contains entry, preparing next chest and creating heart item index. (2)");
					}
					
					System.out.println("Particle effect test...!!"); // working
					System.out.println("Particle effect test 2...!!"); // working
					player.getWorld().playEffect(player.getLocation(), Effect.FLAME, 500);
					player.getWorld().playEffect(player.getLocation(), Effect.INSTANT_SPELL, 500);
				}
				
				else if (!FortressPlayerPermissions.containsKey(args[2]))
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnPlayerNotOnlineForFortCreate(args[2]);
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			
			if (args[0].equals("removefort") && args.length == 2)
			{
				System.out.println("COMMAND TEST: Remove fort attempt");
				if (args[1] != null)
				{
					System.out.println("COMMAND TEST: Remove fort attempt entry ");
					Player player = (Player) sender;
					F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName()); // get the individual SQL controller assigned to the player running the command.
					playerDataRetriever.updateFortress(player, FortressPlayerPermissions, args[1], null, 0); // true indicates this is an admin command.
				}
			}
			
			if (args[0].equals("setalign") && args.length == 3)
			{
				System.out.println("COMMAND TEST: 5");
				if (args[1] != null && args[2] != null)
				{
					System.out.println("COMMAND TEST: 6");
					Player player = (Player) sender;
					F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName()); // get the individual SQL controller assigned to the player running the command.
					playerDataRetriever.setAlignment(player, FortressPlayerPermissions, args[1], args[2], 0); // true indicates this is an admin command.
					
				}
			}
			
			if (args[0].equals("listforts") && args.length == 2)
			{
				System.out.println("COMMAND TEST: 7");
				if (args[1] != null)
				{
					System.out.println("COMMAND TEST: 8");
					Player player = (Player) sender;
					F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName()); // get the individual SQL controller assigned to the player running the command.
					playerDataRetriever.listPlayerMemberships(player, FortressPlayerPermissions, args[1], 0);
					
				}
			}
			
			if (args[0].equals("listsensors") && args.length == 2)
			{
				System.out.println("COMMAND TEST: 11");
				if (args[1] != null && FortressDataController.containsKey(args[1]))
				{
					System.out.println("COMMAND TEST: 12 (new)");
					Player player = (Player) sender;
					//F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName()); // get the individual SQL controller assigned to the player running the command.
					//playerDataRetriever.listPlayerMemberships(player, FortressPlayerPermissions, args[1], 0);
					FortressDataController.get(args[1]).listSensors(args[1], player);
					
					
				} else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(args[1]);
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			
			if (args[0].equals("listarrows") && args.length == 2)
			{
				System.out.println("COMMAND TEST: 13");
				if (args[1] != null && FortressDataController.containsKey(args[1]))
				{
					System.out.println("COMMAND TEST: 14");
					Player player = (Player) sender;
					//F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName()); // get the individual SQL controller assigned to the player running the command.
					//playerDataRetriever.listPlayerMemberships(player, FortressPlayerPermissions, args[1], 0);
					FortressDataController.get(args[1]).listArrowTraps(args[1], player);
					
					
				} else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(args[1]);
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
						
			if (args[0].equals("editfort"))
			{
				
			}
			
			if (args[0].equals("forceheartsave"))
			{
				System.out.println("COMMAND TEST: 9");
				if (args[1] != null && FortressDataController.containsKey(args[1]))
				{
					System.out.println("COMMAND TEST: 10");
					Player player = (Player) sender;
					FortressDB.updateFortressHeartItems(args[1], FortressDataController);
				}else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(args[1]);
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			
			if (args[0].equals("forceheartload"))
			{
				System.out.println("COMMAND TEST: 11");
				if (args[1] != null && FortressDataController.containsKey(args[1]))
				{
					System.out.println("COMMAND TEST: 12");
					Player player = (Player) sender;
					FortressDB.loadFortressHeartItems(args[1], FortressDataController, tryHikariConnection());
				}else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(args[1]);
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			
			// ----------------------------------------------- TESTED, DEBUGGED --------------------------------------------------------
			if (args[0].equals("editsensorname"))
			{
				System.out.println("COMMAND TEST: 13");
				if (args.length == 4)
				{
					if (args[1] != null  && FortressDataController.containsKey(args[1]) && args.length == 4)
					{
						if (FortressDataController.get(args[1]).getAllSensorHashMap() == null)
						{
							System.out.println("WARNING HASH SENSOR ISNULL");
						}
						if (FortressDataController.get(args[1]).getAllSensorHashMap().containsKey(args[2]))
						{
							if (!FortressDataController.get(args[1]).getAllSensorHashMap().containsKey(args[3]))
							{
								System.out.println("COMMAND TEST: 14");
								Player player = (Player) sender;
								FortressDataController.get(args[1]).changeName(args[1], args[2], args[3], 0);
								ComponentBuilder syntaxstring = rootstringbuilder.returnSuccessfulSensorRename(args[2], args[3], args[1]);
								player.spigot().sendMessage(syntaxstring.create());
							}
							else if (FortressDataController.get(args[1]).getAllSensorHashMap().containsKey(args[3]))
							{
								ComponentBuilder syntaxstring = rootstringbuilder.returnSensorNameAlreadyExists(args[3], args[1]);
								Player playertosend = (Player) sender;
								playertosend.spigot().sendMessage(syntaxstring.create());
							}
						}
						else if (!FortressDataController.get(args[1]).getAllSensorHashMap().containsKey(args[2]))
						{
							System.out.println("No sensor exists with that name!");
							ComponentBuilder syntaxstring = rootstringbuilder.returnSensorNotFound(args[2], args[1]);
							Player playertosend = (Player) sender;
							playertosend.spigot().sendMessage(syntaxstring.create());
						}
						
						
					}else
					{
						ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(args[1]);
						Player playertosend = (Player) sender;
						playertosend.spigot().sendMessage(syntaxstring.create());
					}
				} else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnEditSensorNameSyntaxNew();
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
				
			}
			
			// ***************************************************************************************************************
			
			
			
			// ----------------------------------------------- TESTED, DEBUGGED --------------------------------------------------------
			if (args[0].equals("editsensormode"))    // FORMAT: /fortadmin editsensormode <fortressname> <sensor name> <mode: F, H, N>
			{
				System.out.println("COMMAND TEST: 19");
				if (args.length == 4)
				{
					if (args[1] != null && args.length == 4 && FortressDataController.containsKey(args[1]))
					{
						if (FortressDataController.get(args[1]).getAllSensorHashMap().containsKey(args[2]))
						{
							if (args[3].equals("F") || args[3].equals("H") || args[3].equals("N"))
							{
								System.out.println("COMMAND TEST: 20");
								if (sender instanceof Player)
								{
									Player player = (Player) sender;
									InitDB.updateSensorMode(args[1], args[2], args[3], player);
								}
								if (sender instanceof ConsoleCommandSender)
								{
									InitDB.updateSensorMode(args[1], args[2], args[3], null);
								}
								
							} else
							{
								ComponentBuilder syntaxstring = rootstringbuilder.returnEditSensorNameSyntax();
								Player playertosend = (Player) sender;
								playertosend.spigot().sendMessage(syntaxstring.create());
							}
						} else if (!FortressDataController.get(args[1]).getAllSensorHashMap().containsKey(args[2]))
						{
							System.out.println("No sensor exists with that name!");
							ComponentBuilder syntaxstring = rootstringbuilder.returnSensorNotFound(args[2], args[1]);
							Player playertosend = (Player) sender;
							playertosend.spigot().sendMessage(syntaxstring.create());
						}
					} else
					{
						ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(args[1]);
						Player playertosend = (Player) sender;
						playertosend.spigot().sendMessage(syntaxstring.create());
					}
				} else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnEditSensorNameSyntax();
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			// ***************************************************************************************************************
			
			
			
			// ----------------------------------------------- TESTED, DEBUGGED --------------------------------------------------------
			if (args[0].equals("editarrowname"))   // FORMAT: /fortadmin editarrowname <fortressname> <original name> <new name>
			{
				System.out.println("COMMAND TEST: 15");
				if (args.length == 4)
				{
					if (args[1] != null  && FortressDataController.containsKey(args[1]) && args.length == 4)
					{
						if (FortressDataController.get(args[1]).getAllArrowHashMap() == null)
						{
							System.out.println("WARNING ARROW SENSOR ISNULL");
						}
						if (FortressDataController.get(args[1]).getAllArrowHashMap().containsKey(args[2]))
						{
							if (!FortressDataController.get(args[1]).getAllArrowHashMap().containsKey(args[3]))
							{
								System.out.println("COMMAND TEST: 14");
								Player player = (Player) sender;
								FortressDataController.get(args[1]).changeName(args[1], args[2], args[3], 1);
								ComponentBuilder syntaxstring = rootstringbuilder.returnSuccessfulArrowRename(args[2], args[3], args[1]);
								player.spigot().sendMessage(syntaxstring.create());
							}
							else if (FortressDataController.get(args[1]).getAllArrowHashMap().containsKey(args[3]))
							{
								ComponentBuilder syntaxstring = rootstringbuilder.returnArrowNameAlreadyExists(args[3], args[1]);
								Player playertosend = (Player) sender;
								playertosend.spigot().sendMessage(syntaxstring.create());
							}
						}
						else if (!FortressDataController.get(args[1]).getAllArrowHashMap().containsKey(args[2]))
						{
							System.out.println("No sensor exists with that name!");
							ComponentBuilder syntaxstring = rootstringbuilder.returnArrowNotFound(args[2], args[1]);
							Player playertosend = (Player) sender;
							playertosend.spigot().sendMessage(syntaxstring.create());
						}
						
						
					}else
					{
						ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(args[1]);
						Player playertosend = (Player) sender;
						playertosend.spigot().sendMessage(syntaxstring.create());
					}
				} else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnEditArrowNameSyntaxNew();
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			
			// ***************************************************************************************************************
			
			if (args[0].equals("editgolemname"))  // FORMAT: /fortadmin editgolemname <fortressname> <original name> <new name>
			{
				System.out.println("COMMAND TEST: 17");
				if (args[1] != null)
				{
					System.out.println("COMMAND TEST: 18");
					Player player = (Player) sender;
					FortressDataController.get(args[1]).changeName(args[1], args[2], args[3], 2);
				} else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(args[1]);
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			
			if (args[0].equals("moveheart"))
			{
				System.out.println("COMMAND TEST: 19");
				Player player = (Player) sender;
				FortressPlayerPermissions.get(player.getName()).setFortHeartLocationChangeFlag(true);
				
			}
			
			if (args[0].equals("allowforts") && args.length == 2)
			{
				//System.out.println("ALLOW FORT TEST");
				Player player = (Player) sender;
				F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName());
				playerDataRetriever.permitFortsInWorld(args[1], player);
			}
			
			if (args[0].equals("denyforts") && args.length == 2)
			{
				//System.out.println("ALLOW FORT TEST");
				Player player = (Player) sender;
				F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName());
				playerDataRetriever.denyFortsInWorld(args[1], player);
			}
			
			if  (args[0].equals("permitlist") && args.length == 1)
			{
				Player player = (Player) sender;
				displayPermitList(player);
			}
			
			if (args[0].equals("spawnmine") && args.length == 1)
			{
				Player p = (Player) sender;
				//Location loc = p.getLocation().add(p.getLocation().getDirection().setY(0).normalize().multiply(30)); //spawns objects 30 blocks in front of player (useful code for later?)
				
				//EntityPlayer EPlayer = (EntityPlayer)p;
				
				
				Location floc = (Location)p.getTargetBlock((Set)null, 100).getLocation().clone().add(0.5, 1, 0.5);

				EntityTypes.spawnEntity(new F_ArrowTrap(((CraftWorld)Bukkit.getWorld(p.getWorld().getName())).getHandle()), floc); 
			}
			
			if (args[0].equals("spawnsensor") && args.length == 1)
			{
				Player p = (Player) sender;
				//Location loc = p.getLocation().add(p.getLocation().getDirection().setY(0).normalize().multiply(30)); //spawns objects 30 blocks in front of player (useful code for later?)
				
				//EntityPlayer EPlayer = (EntityPlayer)p;
				
				//p.getTargetBlock((HashSet<Byte>)null, 100);
				Location floc = (Location)p.getTargetBlock((Set)null, 100).getLocation().clone().add(0.5, 1, 0.5);
				
				
				EntityTypes.spawnEntity(new F_Sensor(((CraftWorld)Bukkit.getWorld(p.getWorld().getName())).getHandle()), floc); 
			}
			
			if (args[0].equals("spawngolem") && args.length == 1)
			{
				Player p = (Player) sender;
				//Location loc = p.getLocation().add(p.getLocation().getDirection().setY(0).normalize().multiply(30)); //spawns objects 30 blocks in front of player (useful code for later?)
				
				//EntityPlayer EPlayer = (EntityPlayer)p;
				
				//p.getTargetBlock((HashSet<Byte>)null, 100);
				Location floc = (Location)p.getTargetBlock((Set)null, 100).getLocation().clone().add(0.5, 1, 0.5);
				//Location lookingloc = 
				//EntityTypes.spawnEntity(new F_Mine(((CraftWorld)Bukkit.getWorld("world")).getHandle()), new Location(Bukkit.getWorld("world"), 92, 82, 63));
				EntityTypes.spawnEntity(new F_IronGolem(((CraftWorld)Bukkit.getWorld(p.getWorld().getName())).getHandle()), floc); 
				//EntityTypes.spawnEntity(new F_IronGolem(((CraftWorld)Bukkit.getWorld("world")).getHandle()), new Location(Bukkit.getWorld("world"), 92, 82, 63));
			}
			
			
		}
		
		if(label.equalsIgnoreCase("fort") && args.length > 0)
		{
			if (args[0].equals("createfort") && args.length == 2) // FORMAT: /fort createfort
			{
				Player player = (Player) sender;
				if (FortressPlayerQueryController.get(sender.getName()).checkIfPlayerIsCreator(sender.getName()) == false)
				{
					if (FortressPlayerPermissions.get(sender.getName()).checkPlayerCurrency(player, 20) == true)
					{
						System.out.println("STANDARD CREATEFORT CALLED");
						F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName()); // get the individual SQL controller assigned to the player running the command.
						playerDataRetriever.updateFortress(player, FortressPlayerPermissions, args[1], sender.getName(), 1); // true indicates this is an admin command.
						if (FortressDataController.containsKey(args[1]))
						{
							System.out.println("Fortress hash contains entry, preparing next chest and creating heart item index.");
							FortressPlayerPermissions.get(sender.getName()).setCreateFortressOnNextClick(true);
							FortressPlayerPermissions.get(sender.getName()).setCreateFortressString(args[1]);
							//FortressDB.createFortressHeartItemIndex(args[1]);
							System.out.println("Fortress hash contains entry, preparing next chest and creating heart item index. (2)");
						}
						
						System.out.println("Particle effect test...!!"); // working
						System.out.println("Particle effect test 2...!!"); // working
						player.getWorld().playEffect(player.getLocation(), Effect.FLAME, 500);
						player.getWorld().playEffect(player.getLocation(), Effect.INSTANT_SPELL, 500);
					
						// set the current fortress alignment equal to the fortress that was just created
						playerDataRetriever.setAlignment(player, FortressPlayerPermissions, sender.getName(), args[1], 0); // true indicates this is an admin command.
					} else
					{
						ComponentBuilder stringToSend = rootstringbuilder.returnNotEnoughForFort(20);
						player.spigot().sendMessage(stringToSend.create());
					}
				} else
				{
					ComponentBuilder stringToSend = rootstringbuilder.returnAlreadyCreatedFort();
					player.spigot().sendMessage(stringToSend.create());
				}
			}
			
			if (args[0].equals("addmember") && args.length == 2)
			{
				Player player = (Player) sender;
				if ( FortressPlayerQueryController.get(sender.getName()).isPlayerMemberOfFortress(sender.getName(), FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), null, false) == true)
				{
					
					F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName());  // get the individual SQL controller assigned to the player running the command.
					//System.out.println("****FIRST ADDMEMBER PASS COMPLETE****"); // debug only
					playerDataRetriever.updateFortressMembership(player, FortressPlayerPermissions, args[1], FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), 1, null); // true indicates a person is geing added to a fortress. 
				} else
				{
					ComponentBuilder stringToSend = rootstringbuilder.returnInvalidMember(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
					player.spigot().sendMessage(stringToSend.create());
				}
			}
			
			if (args[0].equals("removemember") && args.length == 2)
			{
				Player player = (Player) sender;
				if ( FortressPlayerQueryController.get(sender.getName()).isPlayerMemberOfFortress(sender.getName(), FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), null, false) == true)
				{
					
					F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName());  // get the individual SQL controller assigned to the player running the command.
					//System.out.println("****FIRST REMOVEMEMBER PASS COMPLETE****");
					playerDataRetriever.updateFortressMembership(player, FortressPlayerPermissions, args[1], FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), 0, null); // true indicates a person is geing added to a fortress. 
				} else
				{
					ComponentBuilder stringToSend = rootstringbuilder.returnInvalidMember(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
					player.spigot().sendMessage(stringToSend.create());
				}
			}
			
			if (args[0].equals("spawnmine"))
			{
				Player player = (Player) sender;
				if ( FortressPlayerQueryController.get(sender.getName()).isPlayerMemberOfFortress(sender.getName(), FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), null, false) == true && FortressPlayerPermissions.get(sender.getName()).checkPlayerCurrency(player, 2) == true)
				{
					//F_FortRegionManager fortManager = new F_FortRegionManager();
					if (rootRegionManager.isPlayerInFort(player, FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()) == true)
					{
						System.out.println("check successful.");
						
						Player p = (Player) sender;
						Location floc = (Location)p.getTargetBlock((Set)null, 100).getLocation().clone().add(0.5, 1, 0.5);
						EntityTypes.spawnEntity(new F_ArrowTrap(((CraftWorld)Bukkit.getWorld(p.getWorld().getName())).getHandle()), floc); 
						
						ComponentBuilder stringToSend = rootstringbuilder.returnMinePlaced();
						player.spigot().sendMessage(stringToSend.create());
					} else
					{
						ComponentBuilder stringToSend = rootstringbuilder.returnMineNotInAlignedRegion();
						player.spigot().sendMessage(stringToSend.create());
					}
				}
				else if (FortressPlayerPermissions.get(sender.getName()).checkPlayerCurrency(player, 2) == false)
				{
					System.out.println("Player didn't mean minimum requirements for mine!");
					ComponentBuilder stringToSend = rootstringbuilder.returnNotEnoughForMine(2);
					player.spigot().sendMessage(stringToSend.create());
				}
					
			}
			
			if (args[0].equals("spawnsensor"))
			{
				Player player = (Player) sender;
				if ( FortressPlayerQueryController.get(sender.getName()).isPlayerMemberOfFortress(sender.getName(), FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), null, false) == true && FortressPlayerPermissions.get(sender.getName()).checkPlayerCurrency(player, 2) == true)
				{
					//F_FortRegionManager fortManager = new F_FortRegionManager();
					if (rootRegionManager.isPlayerInFort(player, FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()) == true)
					{
						System.out.println("check successful.");
						
						Player p = (Player) sender;
						Location floc = (Location)p.getTargetBlock((Set)null, 100).getLocation().clone().add(0.5, 1, 0.5);
						EntityTypes.spawnEntity(new F_Sensor(((CraftWorld)Bukkit.getWorld(p.getWorld().getName())).getHandle()), floc); 
						
						ComponentBuilder stringToSend = rootstringbuilder.returnSensorPlaced();
						player.spigot().sendMessage(stringToSend.create());
					} else
					{
						ComponentBuilder stringToSend = rootstringbuilder.returnMineNotInAlignedRegion();
						player.spigot().sendMessage(stringToSend.create());
					}
				}
				else if (FortressPlayerPermissions.get(sender.getName()).checkPlayerCurrency(player, 2) == false)
				{
					System.out.println("Player didn't mean minimum requirements for sensor!");
					ComponentBuilder stringToSend = rootstringbuilder.returnNotEnoughForSensor(2);
					player.spigot().sendMessage(stringToSend.create());
				}
			}
			
			if (args[0].equals("listforts"))
			{
				Player player = (Player) sender;
				
				F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName()); // get the individual SQL controller assigned to the player running the command.
				playerDataRetriever.listPlayerMemberships(player, FortressPlayerPermissions, sender.getName(), 0);
				
				
			}
			
			if (args[0].equals("editarrowname"))
			{
				if (args.length == 3)
				{
					if (args[1] != null  && FortressDataController.containsKey(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()) && args.length == 3)
					{
						if (FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllArrowHashMap() == null)
						{
							System.out.println("WARNING ARROW SENSOR ISNULL");
						}
						if (FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllArrowHashMap().containsKey(args[1]))
						{
							if (!FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllArrowHashMap().containsKey(args[2]))
							{
								System.out.println("COMMAND TEST: 14");
								Player player = (Player) sender;
								FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).changeName(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), args[1], args[2], 1);
								ComponentBuilder syntaxstring = rootstringbuilder.returnSuccessfulArrowRename(args[1], args[2], FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
								player.spigot().sendMessage(syntaxstring.create());
							}
							else if (FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllArrowHashMap().containsKey(args[2]))
							{
								ComponentBuilder syntaxstring = rootstringbuilder.returnArrowNameAlreadyExists(args[2], FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
								Player playertosend = (Player) sender;
								playertosend.spigot().sendMessage(syntaxstring.create());
							}
						}
						else if (!FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllArrowHashMap().containsKey(args[1]))
						{
							System.out.println("No sensor exists with that name!");
							ComponentBuilder syntaxstring = rootstringbuilder.returnArrowNotFound(args[1], FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
							Player playertosend = (Player) sender;
							playertosend.spigot().sendMessage(syntaxstring.create());
						}
						
						
					}else
					{
						ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
						Player playertosend = (Player) sender;
						playertosend.spigot().sendMessage(syntaxstring.create());
					}
				} else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnEditArrowNameSyntaxNew();
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			
			if (args[0].equals("editsensorname"))
			{
				if (args.length == 3)
				{
					if (args[1] != null  && FortressDataController.containsKey(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()) && args.length == 3)
					{
						if (FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllSensorHashMap() == null)
						{
							System.out.println("WARNING HASH SENSOR ISNULL");
						}
						if (FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllSensorHashMap().containsKey(args[1]))
						{
							if (!FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllSensorHashMap().containsKey(args[2]))
							{
								System.out.println("COMMAND TEST: 14");
								Player player = (Player) sender;
								FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).changeName(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), args[1], args[2], 0);
								ComponentBuilder syntaxstring = rootstringbuilder.returnSuccessfulSensorRename(args[1], args[2], FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
								player.spigot().sendMessage(syntaxstring.create());
							}
							else if (FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllSensorHashMap().containsKey(args[2]))
							{
								ComponentBuilder syntaxstring = rootstringbuilder.returnSensorNameAlreadyExists(args[2], FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
								Player playertosend = (Player) sender;
								playertosend.spigot().sendMessage(syntaxstring.create());
							}
						}
						else if (!FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllSensorHashMap().containsKey(args[1]))
						{
							System.out.println("No sensor exists with that name!");
							ComponentBuilder syntaxstring = rootstringbuilder.returnSensorNotFound(args[1], FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
							Player playertosend = (Player) sender;
							playertosend.spigot().sendMessage(syntaxstring.create());
						}
						
						
					}else
					{
						ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
						Player playertosend = (Player) sender;
						playertosend.spigot().sendMessage(syntaxstring.create());
					}
				} else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnEditSensorNameSyntaxNew();
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			
			if (args[0].equals("editsensormode"))
			{
				if (args.length == 3)
				{
					if (args[1] != null && args.length == 3 && FortressDataController.containsKey(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()))
					{
						if (FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllSensorHashMap().containsKey(args[1]))
						{
							if (args[2].equals("F") || args[2].equals("H") || args[2].equals("N"))
							{
								System.out.println("COMMAND TEST: 20");
								if (sender instanceof Player)
								{
									Player player = (Player) sender;
									InitDB.updateSensorMode(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), args[1], args[2], player);
								}
								if (sender instanceof ConsoleCommandSender)
								{
									InitDB.updateSensorMode(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), args[1], args[2], null);
								}
								
							} else
							{
								ComponentBuilder syntaxstring = rootstringbuilder.returnEditSensorNameSyntax();
								Player playertosend = (Player) sender;
								playertosend.spigot().sendMessage(syntaxstring.create());
							}
						} else if (!FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).getAllSensorHashMap().containsKey(args[1]))
						{
							System.out.println("No sensor exists with that name!");
							ComponentBuilder syntaxstring = rootstringbuilder.returnSensorNotFound(args[1], FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
							Player playertosend = (Player) sender;
							playertosend.spigot().sendMessage(syntaxstring.create());
						}
					} else
					{
						ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
						Player playertosend = (Player) sender;
						playertosend.spigot().sendMessage(syntaxstring.create());
					}
				} else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnEditSensorNameSyntax();
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			
			if (args[0].equals("listsensors"))
			{
				if (FortressDataController.containsKey(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()))
				{
					System.out.println("COMMAND TEST: 12 (new)");
					Player player = (Player) sender;
					//F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName()); // get the individual SQL controller assigned to the player running the command.
					//playerDataRetriever.listPlayerMemberships(player, FortressPlayerPermissions, args[1], 0);
					FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).listSensors(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), player);
					
					
				} else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			
			if (args[0].equals("listarrows"))
			{
				if (FortressDataController.containsKey(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()))
				{
					System.out.println("COMMAND TEST: 14");
					Player player = (Player) sender;
					//F_SQLPlayerDataQueries playerDataRetriever = FortressPlayerQueryController.get(sender.getName()); // get the individual SQL controller assigned to the player running the command.
					//playerDataRetriever.listPlayerMemberships(player, FortressPlayerPermissions, args[1], 0);
					FortressDataController.get(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment()).listArrowTraps(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment(), player);
					
					
				} else
				{
					ComponentBuilder syntaxstring = rootstringbuilder.returnFortDoesNotExist(FortressPlayerPermissions.get(sender.getName()).getFortressAlignment());
					Player playertosend = (Player) sender;
					playertosend.spigot().sendMessage(syntaxstring.create());
				}
			}
			
			if (args[0].equals("moveheart"))
			{
				System.out.println("COMMAND TEST: 19");
				Player player = (Player) sender;
				FortressPlayerPermissions.get(player.getName()).setFortHeartLocationChangeFlag(true);
			}
		}
		

		
		if(label.equalsIgnoreCase("forthelp") && args.length == 0)
		{
			Player player = (Player) sender;
			TextComponent URLmsg = new TextComponent("Fortress tutorial video: ");
			URLmsg.setColor(ChatColor.GOLD);
			TextComponent URLmsg2 = new TextComponent(" YouTube ");
			//URLmsg.setColor(ChatColor.WHITE);
			URLmsg2.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "http://spigotmc.org" ) );
			URLmsg2.setColor(ChatColor.WHITE);
			URLmsg.addExtra(URLmsg2);
			player.spigot().sendMessage(URLmsg);
			
			// fort help index header
			ComponentBuilder indexString = rootstringbuilder.returnFortHelpIndex();
			player.spigot().sendMessage(indexString.create());
			
			
			// 1st line below forthelp index
			TextComponent createfort = rootstringbuilder.fortHelpCreatefort();
			TextComponent addmember = rootstringbuilder.fortHelpAddmember();
			TextComponent removemember = rootstringbuilder.fortHelpRemovemember();
			
			
			player.spigot().sendMessage(createfort, addmember, removemember);
		}
		
		if(label.equalsIgnoreCase("forthelp") && args.length == 1)
		{
			Player player = (Player) sender;
			if (args[0].equals("createfort"))
			{
				ComponentBuilder fstring = rootstringbuilder.returnDescCreatefort();
				player.spigot().sendMessage(fstring.create());
			}
			
			
		}
		
		if(label.equalsIgnoreCase("fortadmin") && args.length == 0)
		{
			return false;
		}
		
		
		
		
		
		
		/*if(label.equalsIgnoreCase("hi"))
		{
			PluginDescriptionFile pdffile= this.getDescription();
			//ParticleEffect PE;
			Player p = (Player) sender;
			//ParticleEffect.sendToPlayer(p, new Location(Bukkit.getWorld("world"), 100, 100, 100), 1.0F, 1.0F, 1.0F, 1.0F, 5, false);
			p.sendMessage("TEST!");
			this.logger.info(pdffile.getName() + "command executed...");
			EntityTypes.spawnEntity(new CustomZombie(((CraftWorld)Bukkit.getWorld(p.getWorld().getName())).getHandle()), new Location(Bukkit.getWorld(p.getWorld().getName()), 100, 100, 100));
			//ParticleEffect.
			//ParticleEffect.sendToPlayer(p, new Location(Bukkit.getWorld("world"), 100, 100, 100), 1.0f, 1.0f, 1.0f, 1.0f, 1, true);
			//ParticleEffect.CLOUD.sendToPlayer(p, new Location(Bukkit.getWorld("world"), 0.2F, 0.2F, 0.2F, 0.2F, 1, true);
			//ParticleEffect.CLOUD.sendToPlayer(p, new Location(Bukkit.getWorld("world"), 100, 100, 100), 5.2F, 5.2F, 5.2F, 5.2F, 1, true);
			
		}*/
		
		/*if(label.equalsIgnoreCase("golem"))
		{
			Player p = (Player) sender;
			Location loc = p.getLocation().add(p.getLocation().getDirection().setY(0).normalize().multiply(30)); //spawns objects 30 blocks in front of player (useful code for later?)
			
			//EntityPlayer EPlayer = (EntityPlayer)p;
			
			//p.getTargetBlock((HashSet<Byte>)null, 100);
			Location floc = (Location)p.getTargetBlock((Set)null, 100).getLocation().clone().add(0.5, 1, 0.5);
			//Location lookingloc = 
			//EntityTypes.spawnEntity(new F_Mine(((CraftWorld)Bukkit.getWorld("world")).getHandle()), new Location(Bukkit.getWorld("world"), 92, 82, 63));
			EntityTypes.spawnEntity(new F_IronGolem(((CraftWorld)Bukkit.getWorld(p.getWorld().getName())).getHandle()), floc); 
			//EntityTypes.spawnEntity(new F_IronGolem(((CraftWorld)Bukkit.getWorld("world")).getHandle()), new Location(Bukkit.getWorld("world"), 92, 82, 63));
		}*/
		
		/*if(label.equalsIgnoreCase("fspawnmine"))
		{
			Player p = (Player) sender;
			Location loc = p.getLocation().add(p.getLocation().getDirection().setY(0).normalize().multiply(30)); //spawns objects 30 blocks in front of player (useful code for later?)
			
			//EntityPlayer EPlayer = (EntityPlayer)p;
			
			
			Location floc = (Location)p.getTargetBlock((Set)null, 100).getLocation().clone().add(0.5, 1, 0.5);

			EntityTypes.spawnEntity(new F_ArrowTrap(((CraftWorld)Bukkit.getWorld(p.getWorld().getName())).getHandle()), floc); 
		}*/
		
		/*if(label.equalsIgnoreCase("fspawnsensor"))
		{
			Player p = (Player) sender;
			Location loc = p.getLocation().add(p.getLocation().getDirection().setY(0).normalize().multiply(30)); //spawns objects 30 blocks in front of player (useful code for later?)
			
			//EntityPlayer EPlayer = (EntityPlayer)p;
			
			//p.getTargetBlock((HashSet<Byte>)null, 100);
			Location floc = (Location)p.getTargetBlock((Set)null, 100).getLocation().clone().add(0.5, 1, 0.5);
			
			
			EntityTypes.spawnEntity(new F_Sensor(((CraftWorld)Bukkit.getWorld(p.getWorld().getName())).getHandle()), floc); 
			
		}*/
		
		/*if(label.equalsIgnoreCase("fspawnheart"))
		{
			Player p = (Player) sender;
			Location floc = (Location)p.getTargetBlock((Set)null, 100).getLocation().clone().add(0.5, 1, 0.5);
			EntityTypes.spawnEntity(new F_Heart(((CraftWorld)Bukkit.getWorld(p.getWorld().getName())).getHandle()), floc); 
		}*/
		
		
		
		
		/*if(label.equalsIgnoreCase("createdb"))
		{
			//SQLTest InitDB = new SQLTest();
			InitDB.CreateDB();
		}*/
		//return true;
		
		/*if(label.equalsIgnoreCase("createtable"))
		{
			//SQLTest InitDB = new SQLTest();
			InitDB.CreateLivingEntityUUIDTable();
		}*/
		
		/*if(label.equalsIgnoreCase("creategolemtable"))
		{
			//SQLTest InitDB = new SQLTest();
			InitDB.CreateGolemEntityTable();
		}*/
		
		/*if(label.equalsIgnoreCase("createminetable"))
		{
			//SQLTest InitDB = new SQLTest();
			InitDB.CreateMineEntityTable();
		}*/
		
		return true;
	}

	
	
	// *******************************************
	// custom entity definitions
	// *******************************************
	
	
    public void sneakText(Player s)
    {
    	
    }
    
    public String rootLinkTest()
    {
    	String returnString = "SENSOR HAS CALLED FORTRESS_ROOT METHOD SUCCESSFULLY";
    	return returnString;
    }
	
    public HashMap<String, F_DBPlayerFortressPrivs> getPlayerFortressPrivs()
    {
    	return FortressPlayerPermissions;
    }
    
    public HashMap<String, F_DBFortressData> getFortressDataHash()
    {
    	return FortressDataController;
    }
    
    public HashMap<String, F_SQLPlayerDataQueries> getFortressQueryController()
    {
    	return FortressPlayerQueryController;
    }
    
    public F_SQLFortressDataQueries getFortressDBQueryExecutor()
    {
    	return FortressDB;
    }
    
    public void saveFortressHeartsOnDisable()
    {
    	Iterator it = FortressDataController.entrySet().iterator();
    	F_DBFortressData pointer;
    	while (it.hasNext())
    	{
    		Map.Entry<String, F_DBFortressData> entry = (Map.Entry<String, F_DBFortressData>)it.next();
    		pointer = entry.getValue();
    		
    		if (pointer.fortHadHeart)
    		{
    			System.out.println("Saving " + pointer);
    			FortressDB.updateFortressHeartItems(entry.getKey(), FortressDataController);
    		}
    	}
    }
    
    public void setupHikariDataSource()
    {
    	System.out.println(getMainConfig().getString("database.MySQL.username"));
    	System.out.println(getMainConfig().getString("database.MySQL.password"));
    	System.out.println(getMainConfig().getString("database.MySQL.URL"));
    	System.out.println(getMainConfig().getString("database.MySQL.poolsize"));
    	
    	String username = "********";
    	String password = "********";
    	
    	HikariConfig config = new HikariConfig();
    	
    	/*config.setJdbcUrl("jdbc:mysql://localhost:number/fortress");
    	config.setUsername(username);
    	config.setPassword(password);
    	
    	hikari = new HikariDataSource(config);
    	hikari.setMaximumPoolSize(10);*/
    	
    	config.setJdbcUrl(getMainConfig().getString("database.MySQL.URL"));
    	config.setUsername(getMainConfig().getString("database.MySQL.username"));
    	config.setPassword(getMainConfig().getString("database.MySQL.password"));
    	
    	hikari = new HikariDataSource(config);
    	hikari.setMaximumPoolSize(10);
    	
    	
    	System.out.println("|||||| HIKARI DATA SOURCE SET UP SUCCESSFULLY. ||||||");
    }
    
    
    public void testConnect()
    {
    	/*System.out.println("COMMAND TEST: CONNECT!!!!! (2)");
    	String address = "******";
    	String name = "fortress";
    	String username = "******";
    	String password = "***********";
    	
    	HikariConfig config = new HikariConfig();
    	System.out.println("COMMAND TEST: CONNECT!!!!! (3)");
    	config.setJdbcUrl("jdbc:mysql://localhost:number/fortress");
    	config.setUsername(username);
    	config.setPassword(password);
    	
    	
    	hikari = new HikariDataSource(config);
    	System.out.println("COMMAND TEST: CONNECT!!!!! (4)");
    	
    	hikari.setMaximumPoolSize(10);*/
    	//hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
    	//hikari.addDataSourceProperty("serverName", address);
    	//hikari.addDataSourceProperty("port", ****);
    	//hikari.addDataSourceProperty("databaseName", name);
    	//hikari.addDataSourceProperty("user", username);
        //hikari.addDataSourceProperty("password", password);
    	
    	Connection connection = null;
    	
    	try {
    		connection = hikari.getConnection();
    		System.out.println("COMMAND TEST: CONNECT SUCCESSFUL!");
    	}  catch (SQLException e) {
    		e.printStackTrace();
    	} finally
    	{
    		if (connection != null)
    		{
    			try
    			{
    				connection.close();
    			}
    			catch (SQLException e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    public HikariDataSource getHikariDataSource()
    {
    	return hikari;
    }
    
    public int getDatabaseType()
    {
    	return DBConnectType;
    }
    
    public Connection tryHikariConnection()
    {
    	Connection conn = null;
    	try{
    		conn = hikari.getConnection();
    		
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	return conn;
    }
    
    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
     
        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }
     
        return (WorldGuardPlugin) plugin;
    }
    
    public List<String> getBuildPermitList()
    {
    	return buildPermitList;
    }
    
    public void createBuildPermitList(List<String> inList)
    {
    	buildPermitList = inList;
    }
    
    public void displayPermitList(Player player)
    {
    	System.out.println("Retrieving permit list...");
    	List<String> permitList = getBuildPermitList();
    	if  (permitList != null)
    	{
	    	if (permitList.size() > 0)
	    	{
	    		ComponentBuilder listHeader = rootstringbuilder.returnPermitListHeader();
	    		player.spigot().sendMessage(listHeader.create());
	    		for (int i = 0; i < permitList.size(); i++)
	        	{
	    			ComponentBuilder listEntry = rootstringbuilder.returnPermitListEntry(permitList.get(i));
	    			player.spigot().sendMessage(listEntry.create());
	        	}
	    	}
    	}
    	else if (permitList == null)
    	{
    		System.out.println("No worlds permitted.");
    		ComponentBuilder listEntry = rootstringbuilder.returnEmptyPermitList();
			player.spigot().sendMessage(listEntry.create());
    	}
    }
    
    public F_FortRegionManager getPluginRegionManager()
    {
    	return rootRegionManager;
    }
}




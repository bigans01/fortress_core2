/* This listener file controls events related to clicking on entities.
 * It  handles but is not limited to handling:
 * --Fortress Golems
 * --Arrow Mines
 * --Sensors
 * 
 * 
 * */

package fortress.plugin;

import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecraft.server.v1_9_R1.BlockLever;
import net.minecraft.server.v1_9_R1.EntityGolem;
import net.minecraft.server.v1_9_R1.EntityIronGolem;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.TileEntityChest;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionsData;
import ru.tehkode.permissions.PermissionsGroupData;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.block.CraftChest;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftGolem;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftIronGolem;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftRabbit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class F_EventListener implements Listener{
	
	// initial variables.
	public final Logger logger = Logger.getLogger("Minecraft");
	F_SQLEntityDataQueries FortressDBBridge = new F_SQLEntityDataQueries();
	F_Sensor BigAnsSensor;
	boolean BigAnsLastClickedSensor;
	Block assignedSensorLever;
	HashMap<String, F_DBPlayerFortressPrivs> PermissionsIndex;
	HashMap<String, F_DBFortressData> FortressDataIndex;
	HashMap<String, F_SQLPlayerDataQueries> FortressPlayerQueryController;
	
	F_SQLFortressDataQueries FortressDB = new F_SQLFortressDataQueries(); 
	F_ChatStringBuilder listenerChatBuilder = new F_ChatStringBuilder();
	
	
	fortress_root rootPlugin =  (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
	
	//for testing, later for hashmap use.
	Inventory testInventory = Bukkit.createInventory(null, 18, "testinv");
	
	F_EventListener(HashMap<String, F_DBPlayerFortressPrivs> permissionsref, HashMap<String, F_DBFortressData> fortdataref)
	{
		testInventory.setItem(0, new ItemStack(Material.DIRT, 1));
		this.PermissionsIndex = permissionsref;
		this.FortressDataIndex = fortdataref;
		this.FortressPlayerQueryController = rootPlugin.getFortressQueryController();
	}
	
	
	
	
	
	
	@EventHandler
    public void onPlayerShift(PlayerToggleSneakEvent e) throws Throwable
	{
    	//Object PO = ParticleEffect.class;
    	
    	///PluginDescriptionFile pdffile= this.getDescription();
    	//this.logger.info(pdffile.getName() + "Sneak executed...");
        Player player = e.getPlayer();
        Location Kordinat = player.getEyeLocation();
        //sneakText(e);
        
        //Class<?> c = Class.forName("ParticleEffect.class");
        //Method method = c.getDeclaredMethod("sendToPlayer", new Class[] {});
        //method.invoke(PO, player, Kordinat, 5.2F, 5.2F, 5.2F, 5.2F, 1, true);
        
        //ParticleEffect.HUGE_EXPLOSION.sendToPlayer(player, Kordinat, 5.2F, 5.2F, 5.2F, 5.2F, 500, true);
        //ParticleEffect.CLOUD.sendToPlayer(player, new Location(Bukkit.getWorld("world"), 100, 100, 100), 5.2F, 5.2F, 5.2F, 5.2F, 1, true);
        //ParticleEffect.EXPLODE.sendToPlayer(player, new Location(Bukkit.getWorld("world"), 100, 100, 100), 5.2F, 5.2F, 5.2F, 5.2F, 1000, true);
      
    }
	
	
	
	@EventHandler
	public void onCZombieTargetPlayer(EntityTargetLivingEntityEvent event)
	{
		if (event.getEntity() instanceof CustomZombie)
		{
			//event.getEntity().getLocation().getWorld().playEffect(paramLocation, paramEffect, paramInt);
			//event.getTarget().playEffect(Effect.STEP_SOUND);
			event.getEntity().setCustomName("BOB IS TARGETED");
			//ParticleEffect.HUGE_EXPLOSION.sendToPlayer(event.getTarget(), new Location(Bukkit.getWorld("world"), 100, 100, 100), 5.2F, 5.2F, 5.2F, 5.2F, 1000, true);
		}
		//event.getEntity().setCustomName("BOB IS TARGETED");
		
	}
	
	@EventHandler
	public void onSensorInteracted(BlockPlaceEvent event)
	{
		//if (event.getBlockPlaced().getType() == Material.LEVER && BigAnsLastClickedSensor == true)
		if (event.getBlockPlaced().getType() == Material.LEVER && PermissionsIndex.get(event.getPlayer().getName()).isLastClickedSet() == true)
		{
			this.logger.info("LAST ENTITY CLICKED WAS A SENSOR...attempting to assign lever to sensor."); 
			assignedSensorLever = event.getBlockPlaced(); // store in a variable? can remove later...this variable will assign the lever to the sensor.
			
			PermissionsIndex.get(event.getPlayer().getName()).getLastClickedSensor().setPairedBlock(assignedSensorLever);
			listenerChatBuilder.returnSensorPairedWithLever(PermissionsIndex.get(event.getPlayer().getName()).getLastClickedSensor().getCustomName(), PermissionsIndex.get(event.getPlayer().getName()).getFortressAlignment() , event.getBlockPlaced().getX(), event.getBlockPlaced().getY(), event.getBlockPlaced().getZ());
			//BigAnsSensor.setPairedBlock(assignedSensorLever); // send the paired block to the sensor.
			
			PermissionsIndex.get(event.getPlayer().getName()).setLastClickedSensorFlag(false);
			//BigAnsLastClickedSensor = false; // reset placed sensor flag.
			ComponentBuilder finalString2 = listenerChatBuilder.returnSensorPairedWithLever(PermissionsIndex.get(event.getPlayer().getName()).getLastClickedSensor().getCustomName(), PermissionsIndex.get(event.getPlayer().getName()).getFortressAlignment() , event.getBlockPlaced().getX(), event.getBlockPlaced().getY(), event.getBlockPlaced().getZ());
			event.getPlayer().spigot().sendMessage(finalString2.create());
			
			
		}
	}
	
	//@EventHandler 
	/*public void onLeverInteracted(BlockRedstoneEvent event)
	{
		//this.logger.info("Object is lever. (REDSTONE EVENT CALLED)");
		if (event.getBlock().getType() == Material.LEVER && BigAnsLastClickedSensor == true)
		//if (event.getBlock().getType() == Material.LEVER && PermissionsIndex.get(event. == true)
		{
			this.logger.info("LAST ENTITY CLICKED WAS A SENSOR...attempting to assign lever to sensor. (right-click)");
			assignedSensorLever = event.getBlock(); // store in a variable? can remove later...this variable will assign the lever to the sensor.
			System.out.println(event.getBlock().getX());
			System.out.println(event.getBlock().getY());
			System.out.println(event.getBlock().getZ());
			BigAnsSensor.setPairedBlock(assignedSensorLever); // send the paired block to the sensor. 
			BigAnsLastClickedSensor = false; // reset placed sensor flag. 
		}
	}*/
	
	@EventHandler
	public void onChestOpen(PlayerInteractEvent event)
	{
		 // fortress heart creation checks
		 if (PermissionsIndex.get(event.getPlayer().getName()) != null )
		 {
			 if (event.getClickedBlock() != null)
			 {
				if (event.getClickedBlock().getType() == Material.CHEST && PermissionsIndex.get(event.getPlayer().getName()).willPlaceFortressHeartNextClick() == true && PermissionsIndex.get(event.getPlayer().getName()).getFortHeartLocationChangeFlag() == false)
				{
						
						BlockState chestState = event.getClickedBlock().getState();
						
						Chest chest = (Chest)chestState;
						chest.getBlockInventory().getName();
						if (chest.getBlockInventory().getName().equals("container.chest"))
						{
							System.out.println("INTERACT CALL");
							System.out.println(event.getClickedBlock());
							
							//chest.getBlockInventory().
							
							System.out.println(chest.getBlockInventory().getName());
							
							// the following 3 lines sets the name of the chest.
							CraftChest convchest = (CraftChest) chest;
							TileEntityChest NMSChest = convchest.getTileEntity();
							NMSChest.a(PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString()); // change the inventory name of the chest. 
							
							// insert code for inserting XYZ of chest here
							
							//FortressPlayerQueryController.get(event.getPlayer().getName()).getFortHeartLocation(PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString(), event.getPlayer());
							
							FortressPlayerQueryController.get(event.getPlayer().getName()).updateFortHeartLocation(PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString(), event.getPlayer(), convchest.getX(), convchest.getY(), convchest.getZ());
							
							
							
							
							FortressDB.createFortressHeartItemIndex(PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString());
							//PermissionsIndex.get(event.getPlayer().getName()).setCreateFortressString("");
							
							System.out.println(chest.getBlockInventory().getName());
							
							
							chest.getBlockInventory();
							event.setCancelled(true);
							//PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString()
							FortressDataIndex.get(PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString()).createNewFortressInventory(PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString());
							FortressDataIndex.get(PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString()).setFortHadHeart(true);
							event.getPlayer().openInventory(FortressDataIndex.get(PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString()).getFortressInventory());
							
							//PermissionsIndex.get(event.getPlayer().getName()).setCreateFortressString("");
							
							// reset the click value for creating the fortress.
							PermissionsIndex.get(event.getPlayer().getName()).setCreateFortressOnNextClick(false);
						}
						
						
						
						
						
						//********************************
						//attempt to create group, named after the fortress
						//********************************
						
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex group " + PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString() + " create");	 // create the group.
						//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex group " + PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString() + " user add " + event.getPlayer().getName() + " " + event.getPlayer().getWorld().getName()); //add the user that created the group to the group.

						//********************************
						//attempt to setup the WorldGuard region around the chest.
						//********************************
						
						fortress_root fortplugin = (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
						WorldGuardPlugin wgplugin = fortplugin.getWorldGuard();

						
						// set up region variables
						RegionContainer container = wgplugin.getRegionContainer();
						RegionManager regions = container.get(event.getPlayer().getWorld());
						
						BlockVector l1 = new BlockVector(chest.getX()-15, chest.getY()-15, chest.getZ()-15);
						BlockVector l2 = new BlockVector(chest.getX()+15, chest.getY()+15, chest.getZ()+15);
						
						
						ProtectedRegion regiontoadd = new ProtectedCuboidRegion(PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString(), l1, l2);
						
						// add the pex group to the worldguard region.
						DefaultDomain newregionmembers = regiontoadd.getMembers();
						//regiontoadd.isMember
						newregionmembers.addGroup(PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString());
						
						// add to the region manager.
						regions.addRegion(regiontoadd);
						
						// next: add player to that fortress group
						F_SQLPlayerDataQueries playerDataRetriever = (fortplugin.getFortressQueryController()).get(event.getPlayer().getName());
						playerDataRetriever.updateFortressMembership(event.getPlayer(), fortplugin.getPlayerFortressPrivs(), event.getPlayer().getName(), PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString(), 1, null);
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex group " + PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString() + " user add " + event.getPlayer().getName()); //add the user that created the group to the group. " " + event.getPlayer().getWorld().getName()
						//System.out.println(fortplugin.getMainConfig().getInt("fortresspriority"));
						//System.out.println(fortplugin.getMainConfig().getString("fortresspriority"));
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex group " + PermissionsIndex.get(event.getPlayer().getName()).getCreateFortressString() + " weight " + fortplugin.getMainConfig().getInt("database.MySQL.fortresspriority"));
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex group COMMON user add " + event.getPlayer().getName()); //add the user that created the group to the group. " " + event.getPlayer().getWorld().getName()
				}
				
				
				if (PermissionsIndex.get(event.getPlayer().getName()).getFortHeartLocationChangeFlag() == true && rootPlugin.getPluginRegionManager().isPlayerInFort(event.getPlayer(), rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment()) == true)
				{
					
					System.out.println("ATTEMPTING TO CHANGE HEART LOCATION...");
					BlockState chestState = event.getClickedBlock().getState();
					Chest chest = (Chest)chestState;
					chest.getBlockInventory().getName();
					if (chest.getBlockInventory().getName().equals("container.chest")) // do the following only if the name isn't a standard chest's name -- also check to see that chest is within bounds.
					{
						CraftChest convchest = (CraftChest) chest;
						TileEntityChest NMSChest = convchest.getTileEntity();
						
						
						
						
						
						// get the location of the old hearrt, put it into oldLoc...and change its name
						Location oldLoc = FortressPlayerQueryController.get(event.getPlayer().getName()).getFortHeartLocation(PermissionsIndex.get(event.getPlayer().getName()).getFortressAlignment(), event.getPlayer());
						BlockState blockState = Bukkit.getWorld(event.getPlayer().getWorld().getName()).getBlockAt(oldLoc).getState();
						Chest oldChest = (Chest) blockState;
						
						oldChest.getBlockInventory().getName();
						System.out.println("OLD CHEST NAME: " + oldChest.getBlockInventory().getName());
						CraftChest oldConvChest = (CraftChest) oldChest;
						TileEntityChest oldNMSChest = oldConvChest.getTileEntity();
						oldNMSChest.a("container.chest");
						System.out.println("RESET OF PREVIOUS HEART COMPLETE.");
					
						
						
						
						// get the location of the new heart, set its name
						FortressPlayerQueryController.get(event.getPlayer().getName()).updateFortHeartLocation(PermissionsIndex.get(event.getPlayer().getName()).getFortressAlignment(), event.getPlayer(), convchest.getX(), convchest.getY(), convchest.getZ());
						CraftChest newConvChest = (CraftChest) chest;
						TileEntityChest newNMSChest = newConvChest.getTileEntity();
						newNMSChest.a(PermissionsIndex.get(event.getPlayer().getName()).getFortressAlignment());
						
						//event.getPlayer().getWorld().getBlockAt(arg0);
						System.out.println("FORT HEART UPDATED...RESETTING HEART CHANGE FLAG");
						
						PermissionsIndex.get(event.getPlayer().getName()).setFortHeartLocationChangeFlag(false);
					
					}
				}
				
				
			 }
		 }
		
		 if (event.getClickedBlock() != null) // this is where the fort heart's inventory will be opened
		 {
			if (event.getClickedBlock().getType() == Material.CHEST)
			{
				BlockState chestState = event.getClickedBlock().getState();
				Chest chest = (Chest)chestState;
				System.out.println(chest.getBlockInventory().getName());
				if (!chest.getBlockInventory().getName().equals("container.chest") && event.getPlayer().getItemInHand().getType() != Material.HOPPER)
				{
					//event.setCancelled(true);
					//event.getPlayer().openInventory(testInventory);
					System.out.println("INTERACT CALL (2) (fortress heart)");
					System.out.println(chest.getBlockInventory().getName());
					if (FortressDataIndex.containsKey(chest.getBlockInventory().getName()))
					{
						if (FortressDataIndex.get(chest.getBlockInventory().getName()).getFortressInventory() != null)
						{
							event.setCancelled(true);
							event.getPlayer().openInventory(FortressDataIndex.get(chest.getBlockInventory().getName()).getFortressInventory());
						}
					}
					/*else if (!FortressDataIndex.containsKey(chest.getBlockInventory().getName()))
					{
						event.getPlayer().openInventory(chest.getBlockInventory());
					}*/
					
				}
			}
		 }
		
	}
	
	
	
	// handles hopper movement into the chest.
	@EventHandler
	public void onItemMove(InventoryMoveItemEvent event)
	{
		if (!event.getDestination().getName().equals("container.chest") && FortressDataIndex.containsKey(event.getDestination().getName()))
		{
			
		
			//System.out.println(event.getDestination().getName());
			
			//System.out.println("MOVEITEM CALL (2)");
				
			//if (event.getItem().getData().getItemType() == Material.GOLD_INGOT || event.getItem().getData().getItemType() == Material.IRON_INGOT)
			//{
				Inventory invToAddTo;
				if (event.getDestination() != null)
				{
				//Inventory invToAddTo = FortressDataIndex.get(event.getDestination().getName()).getFortressInventory();
					//Do the following if there are no empty slots left.
					boolean exitWhile = false;
					invToAddTo = FortressDataIndex.get(event.getDestination().getName()).getFortressInventory();
					if (invToAddTo.firstEmpty() == -1)
					{
						if (invToAddTo.contains(event.getItem().getData().getItemType()))
						{
							//System.out.println("Item  Movement, Contains entry ");
							HashMap<Integer,? extends ItemStack> checkedStackMap = invToAddTo.all(event.getItem().getData().getItemType());
							Iterator stackIterator = checkedStackMap.entrySet().iterator();
							while(stackIterator.hasNext() && exitWhile == false)
							{
								//System.out.println("Item  Movement, While entry ");
								Map.Entry<Integer,? extends ItemStack> stackPointer = (Map.Entry<Integer,? extends ItemStack>)stackIterator.next();
								if (stackPointer.getValue().getAmount() < 64)
								{
									//System.out.println("Item  Movement, Amount entry ");
									invToAddTo.addItem(event.getItem());
									event.getDestination().removeItem(event.getItem());
									exitWhile = true;
								}
							}
						}
							
						if (exitWhile == false)
						{
							//System.out.println("Item move event cancelled..(inner) " + invToAddTo.firstEmpty() + ", " + invToAddTo.contains(event.getItem()));
							event.setCancelled(true);
						}
					}
						
						
					
				
						
					else
					{
						ItemStack item = event.getItem().clone();
						invToAddTo.addItem(item);
						event.getDestination().removeItem(item);
						//System.out.println("empty slot value: " + invToAddTo.firstEmpty());
					}	
						
						
						
						/*Iterator it = FortressDataController.entrySet().iterator();
				    	F_DBFortressData pointer;
				    	while (it.hasNext())
				    	{
				    		Map.Entry<String, F_DBFortressData> entry = (Map.Entry<String, F_DBFortressData>)it.next();
				    		pointer = entry.getValue();
				    		if (pointer.fortHadHeart)
				    		{
				    			FortressDB.updateFortressHeartItems(entry.getKey(), FortressDataController);
				    		}
				    	}*/
				} 
			//}
			
			else
			{
				System.out.println("Event cancelled. ");
				event.setCancelled(true);
			}
			
			
		
		}
		
		/*if (event.getDestination().getName().equals("container.chest"))
		{
			System.out.println("MOVEITEM CALL (3)");
			ItemStack item = event.getItem();
			event.getDestination().removeItem(item);
		}*/
	}
	
	
	@EventHandler
	public void onGolemInteracted(PlayerInteractEntityEvent event)
	{
		if (!(event.getHand() == EquipmentSlot.HAND))
		{
			return;
		}
		
		this.logger.info("INTERACT event called..."); // DEBUG: interact event is called.
		
		Object tester = event.getRightClicked(); // get the class (before conversion) of the entity that was right clicked.
		
		
		this.logger.info("CLASS OF RIGHT  CLICKED: " + tester); // DEBUG: Will show the parent Craft class that this entity is descended from...aka, CraftIronGolem, etc...

		
		
		/********************************************************
		 * GOLEM EVENT HANDLING 
		 * 
		 * 
		 * 
		 * 
		 * *******************************************************/
		if (tester instanceof CraftIronGolem)	// if instance of a certain descendent (i.e., CraftIronGolem), go into here.
			{
				
				
				this.logger.info("IF/THEN PASS (GOLEM): ");
				Object targetgolem = ((CraftGolem)tester).getHandle(); // get the true handle, aka custom class of the entity. In this case, F_IronGolem.
				
				this.logger.info("GOLEM'S CLASS: " + targetgolem);
				
				
				
				
				if (targetgolem instanceof F_IronGolem)					// checks to see if the iron golem is a custom iron golem (F_IronGolem). if true, do stuff
				{
					
					
					
					this.logger.info("ENTITY IS GOLEM!!");
					this.logger.info("C VOID command executed...");
					//event.getRightClicked().setCustomName("GOLEM IS TARGETED");
					F_IronGolem ftarget = (F_IronGolem)targetgolem;
					ftarget.setOwner(event.getPlayer());
					try {
						//ParticleEffect.FLAME.sendToPlayer(event.getPlayer(), event.getRightClicked().getLocation(), 0.00F, 0.005F, 0.00F, 0.05F, 500, true);
						System.out.println("Try pre-query.");
						/* If the player clicks a golem, check to see if that player already has a golem.  
						 * 1. Set up variable to store string (long type)
						 * 2. Retrieve clicked entity's UUID from NBT.
						 * 
						 * */
						long testcompound= ftarget.getDBID(); // 1.
						F_SQLEntityDataQueries select_query = new F_SQLEntityDataQueries();
						UUID select_query_result = select_query.GetPlayerOwnedGolem(event.getPlayer().getName());
						if (select_query_result == null)
						{	
							// need to put try error handle here.
							System.out.println("No golem is owned by bigans01, attempting to register...");
							if (FortressDBBridge.GetRegisteredGolem(ftarget) == null)
							{
								FortressDBBridge.RegisterGolem(ftarget);
							}
							
							// new function here for adding to hashmap...need to call root plugin and create a new hashmap.
							
							FortressDBBridge.AssignGolemToPlayer(ftarget, event.getPlayer().getName());
							
						}
						
				                //NBTTagCompound testcompound= this.getNBTTag();
				                
				                System.out.println("This entity's ID OWNER is:" + select_query_result);
				        
				        System.out.println("Try successful.");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//ftarget.passenger = getH
					((CraftPlayer) event.getPlayer()).getHandle().startRiding(ftarget);
					
				}
		}
		
		
		
		
		/********************************************************
		 * MINE AND SENSOR EVENT HANDLING -- can handle multiple types of mines/sensors here (in the future)
		 * 
		 * 
		 * 
		 * 
		 * *******************************************************/
		
		if (tester instanceof CraftRabbit)
		{
					
					this.logger.info("IF/THEN PASS (RABBIT): ");
					Object targetgolem = ((CraftRabbit)tester).getHandle(); // get the true handle, aka custom class of the entity. In this case, F_IronGolem.
					this.logger.info("GOLEM'S CLASS: " + targetgolem);
					
					/********************************************************
					 *  MINE TRAP HANDLING
					 * 
					 * 
					 * *****************************************************/
					
					// ARROW TRAP EVENT HANDLING
					if (targetgolem instanceof F_ArrowTrap)
						{
							
								this.logger.info("ENTITY IS RABBIT!!");
								this.logger.info("BEGINNING RABBIT INTERACTIONS...");
								//event.getRightClicked().setCustomName("MINE IS TARGETED");
								
								
								fortress_root rootPlugin =  (fortress_root) event.getPlayer().getServer().getPluginManager().getPlugin("Fortress");
								F_ArrowTrap ftarget = (F_ArrowTrap)targetgolem;
								
								System.out.println("CURRENT ALIGNMENT: " + ftarget.getAlignment());
								System.out.println("CURRENT OWNER: " + ftarget.getOwner());
								if (rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment() != null ? rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment().equals(ftarget.getAlignment()) || ftarget.getAlignment().equals("") : false)
								{
									//ftarget.setOwner(event.getPlayer());
									try {
										
										System.out.println("Mine pre-query section reached...");
										UUID testcompound= ftarget.getUniqueID(); // 1.
										F_SQLEntityDataQueries select_query = new F_SQLEntityDataQueries();
										String select_query_result = select_query.DoesPlayerOwnMine(testcompound);
										if (select_query_result == null)
										{
											System.out.println("This mine will be owned by bigans01, attempting to register...");
											FortressDBBridge.AssignMineToPlayer(ftarget.getUniqueID(), ftarget.FortressAlign, event.getPlayer().getName()); // code to be deprecated later?
											
										}
										
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									if (ftarget.getOwner().equals("N/Atest"))
									{
										System.out.println("Owner is N/Atest.");
										ftarget.setOwner(event.getPlayer());
										ftarget.setGoals();
										ftarget.setAlignment(rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment());
										
										fortress_root fortplugin = (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
										F_DBFortressData forttochange = fortplugin.getFortressDataHash().get(rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment()); // get the fort that is referenced by player's current alignment
										F_SQLFortressDataQueries rootDBExecutor = fortplugin.getFortressDBQueryExecutor();
										
										forttochange.getArrowTrapHash().put("mine_" + forttochange.returnCurrentArrowID(), ftarget);
										ftarget.setArrowTrapNameOnCreate("mine_" + forttochange.returnCurrentArrowID());
										
										System.out.println("Owner is N/Atest. (2)");
										forttochange.addArrowTrap(rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment(), ftarget, event.getPlayer().getName());
										
										
									}
									
								} else //if (!rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment().equals(ftarget.getAlignment()) && ftarget.getAlignment() != "")
								{
									ComponentBuilder finalString = new ComponentBuilder("You are not aligned to that sensor's fortress.");
									finalString.color(ChatColor.GOLD);
									event.getPlayer().spigot().sendMessage(finalString.create());
								}
						}
					
					/********************************************************
					 * SENSOR EVENT HANDLING -- can handle multiple types of mines here (in the future)
					 * 
					 * 
					 * 
					 * 
					 * *******************************************************/
					
					// REDSTONE SENSOR EVENT HANDLING 
					if (targetgolem instanceof F_Sensor)
					{
								
								// rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment()
								this.logger.info("ENTITY IS REDSTONE SENSOR!!");
								this.logger.info("BEGINNING REDSTONE SENSOR INTERACTIONS...");
								//event.getRightClicked().setCustomName("SENSOR IS TARGETED");
								
								fortress_root rootPlugin =  (fortress_root) event.getPlayer().getServer().getPluginManager().getPlugin("Fortress");
								F_Sensor ftarget = (F_Sensor)targetgolem;
								System.out.println("IS MATCH: " + rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment());
								System.out.println("IS MATCH (2): " + ftarget.getAlignment());
								System.out.println("SENSOR NAME " + ftarget.getCustomName());
								System.out.println("Unique ID: " + ((F_Sensor) targetgolem).getUniqueID());
								// (rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment().equals(ftarget.getAlignment()) || ftarget.getAlignment() == "")
								if (rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment() != null ? rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment().equals(ftarget.getAlignment()) || ftarget.getAlignment().equals("N/A") : false)
								//if (rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment().equals(ftarget.getAlignment()) || ftarget.getAlignment() == "")
								{
									
						
									this.logger.info("INTERACTIONS (1)...");
									// determine what to do when right clicking the block. 
									if (ftarget.hasPairedBlock() == false || (ftarget.hasPairedBlock() == true && ftarget.returnPairedBlock().getChunk().isLoaded()))
									{
										this.logger.info("INTERACTIONS (5)...");
										
										if (ftarget.hasPairedBlock() == true && ftarget.returnPairedBlock().getChunk().isLoaded())
										{
											if (ftarget.returnPairedBlock().getType() != Material.LEVER)
											{
												//this.logger.info("INTERACTIONS (2)...");
												this.logger.info("SENSOR'S RECEIVING BLOCK NOT A LEVER...flag to set next placed lever is now ACTIVE.");
												
												// NEW hashmap settings
												PermissionsIndex.get(event.getPlayer().getName()).setLastClickedSensorFlag(true);
												PermissionsIndex.get(event.getPlayer().getName()).setLastClickedSensor(ftarget);
												
												ComponentBuilder finalString2 = listenerChatBuilder.returnSensorReadyForPairing(ftarget.getCustomName(), rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment());
												event.getPlayer().spigot().sendMessage(finalString2.create());
												
												//BigAnsLastClickedSensor = true;
												//BigAnsSensor = ftarget;
											} 
											this.logger.info("INTERACTIONS (7)...");
											
											if  (ftarget.returnPairedBlock().getType() == Material.LEVER && (event.getPlayer().getName().equals(ftarget.getOwner())))
											{
												//this.logger.info("INTERACTIONS (3)...");
												ftarget.cycleTargetingMode();
												ComponentBuilder finalString = new ComponentBuilder("Sensor targeting mode set to: ");
												finalString.color(ChatColor.GOLD);
												
												// send colored message to player, based on the sensor's targeting mode. 
												switch(ftarget.getTargetingMode())
												{
													case 0:
														finalString.append("Friendly").color(ChatColor.AQUA).bold(true);
														break;
													case 1:
														finalString.append("Hostile").color(ChatColor.RED).bold(true);
														break;
													case 2:
														finalString.append("Neutral").color(ChatColor.WHITE).bold(true);
														break;
												}
												event.getPlayer().spigot().sendMessage(finalString.create());
												
												System.out.println("TARGETING MODE SET TO : " + ftarget.getTargetingMode());
											}
										}
										
										if (ftarget.hasPairedBlock() == false)
										{
											//this.logger.info("INTERACTIONS (8)...");
											this.logger.info("SENSOR HAS NO PAIRED REDSTONE LEVER...flag to set next placed lever is now ACTIVE.");
											
											PermissionsIndex.get(event.getPlayer().getName()).setLastClickedSensorFlag(true);
											PermissionsIndex.get(event.getPlayer().getName()).setLastClickedSensor(ftarget);
											
											ComponentBuilder finalString2 = listenerChatBuilder.returnSensorReadyForPairing(ftarget.getCustomName(), rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment());
											event.getPlayer().spigot().sendMessage(finalString2.create());
											//BigAnsLastClickedSensor = true;
											//BigAnsSensor = ftarget;
										}
										
									}
									
									//this.logger.info("INTERACTIONS (4)...");
									// perform cycling on the sensor only if the player interacting is the owner. LATER: add groups.
									/*if (ftarget.hasPairedBlock() == true && (event.getPlayer().getName().equals(ftarget.getOwner())) )
									
										ftarget.cycleTargetingMode();
										System.out.println(ftarget.getTargetingMode());
									}*/
									
									// if the sensor has no owner, assign it.
									if (ftarget.getOwner().equals("") || ftarget.getAlignment().equals(""))
									{
										ftarget.setOwner(event.getPlayer());
										ftarget.setGoals();
										//fortress_root rootPlugin =  (fortress_root) event.getPlayer().getServer().getPluginManager().getPlugin("Fortress");
										
										ftarget.setAlignment(rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment());
										fortress_root fortplugin = (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
										F_DBFortressData forttochange = fortplugin.getFortressDataHash().get(rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment()); // get the fort that is referenced by player's current alignment
										F_SQLFortressDataQueries rootDBExecutor = fortplugin.getFortressDBQueryExecutor();
										
										forttochange.getSensorHash().put("sensor_" + forttochange.returnCurrentSensorID(), ftarget);// add this sensor to the fort's (ACTIVE) hashmap reference for sensors.
										ftarget.setSensorNameOnCreate("sensor_" + forttochange.returnCurrentSensorID());
										// add this sensor to the database
										forttochange.addSensor(rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment(), ftarget);
										
										// increment the max sensor value by 1 in the memory's hashmap version
										// increment the max sensor value by 1 in the database
										
										System.out.println("SENSOR ASSIGNED.");
										System.out.println("Sensor aligned with: " + ftarget.getAlignment());
										
										
										ComponentBuilder finalString = new ComponentBuilder("Sensor has been aligned to: ");
										ComponentBuilder finalString2 = listenerChatBuilder.returnSensorReadyForPairing(ftarget.getCustomName(), rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment());
										finalString.color(ChatColor.GOLD);
										finalString.append(ftarget.getAlignment());
										finalString.color(ChatColor.DARK_GREEN);
										finalString.bold(true);
										
										event.getPlayer().spigot().sendMessage(finalString.create());
										event.getPlayer().spigot().sendMessage(finalString2.create());
										
										
									}
								} else //if (!rootPlugin.getPlayerFortressPrivs().get(event.getPlayer().getName()).getFortressAlignment().equals(ftarget.getAlignment()) && ftarget.getAlignment() != "")
								{
									ComponentBuilder finalString = new ComponentBuilder("You are not aligned to that sensor's fortress.");
									finalString.color(ChatColor.GOLD);
									event.getPlayer().spigot().sendMessage(finalString.create());
								}
					}
					
					
					
		}
		
		
		
		
		
		
		
		if (event.getRightClicked() instanceof EntityIronGolem)
		{
			this.logger.info("C VOID command executed...");
			//event.getEntity().getLocation().getWorld().playEffect(paramLocation, paramEffect, paramInt);
			//event.getTarget().playEffect(Effect.STEP_SOUND);
			event.getRightClicked().setCustomName("GOLEM IS TARGETED");
			
		}
		
		
		if (event.getRightClicked() instanceof CustomZombie)
		{
			this.logger.info("C VOID command executed...");
			//event.getEntity().getLocation().getWorld().playEffect(paramLocation, paramEffect, paramInt);
			//event.getTarget().playEffect(Effect.STEP_SOUND);
			event.getRightClicked().setCustomName("ZOMBIE IS TARGETED");
			//ParticleEffect.HUGE_EXPLOSION.sendToPlayer(event.getTarget(), new Location(Bukkit.getWorld("world"), 100, 100, 100), 5.2F, 5.2F, 5.2F, 5.2F, 1000, true);
		}
	}
	
	@EventHandler
	public void onFortressEntityDeath(EntityDeathEvent event)
	{

		if (event.getEntity() instanceof CraftRabbit) // ARROWTRAP AND SENSOR DEATH HANDLING
		{
			Object targetsensor = ((CraftRabbit)event.getEntity()).getHandle();
			if (targetsensor instanceof F_Sensor)
			{
				
				System.out.println("SENSOR DIED");
				F_Sensor currsensor = (F_Sensor) targetsensor;
				fortress_root fortplugin = (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
				F_DBFortressData forttochange = fortplugin.getFortressDataHash().get(currsensor.FortressAlign);
				if (forttochange != null)
				{
					System.out.println("SENSOR REMOVAL ATTEMPT");
					forttochange.removeSensor(currsensor.FortressAlign, currsensor);
					forttochange.getUUIDList().remove(currsensor.getUniqueID());
				}
			}
			
			if (targetsensor instanceof F_ArrowTrap)
			{
				System.out.println("ARROW TRAP DIED");
				F_ArrowTrap currmine = (F_ArrowTrap) targetsensor;
				fortress_root fortplugin = (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
				F_DBFortressData forttochange = fortplugin.getFortressDataHash().get(currmine.FortressAlign);
				if (forttochange != null)
				{
					forttochange.removeArrowTrap(currmine.FortressAlign, currmine);
					forttochange.getUUIDList().remove(currmine.getUniqueID());
				}
			}
			
		}
		
		if (event.getEntity() instanceof CraftIronGolem)
		{
			Object targetgolem = ((CraftIronGolem)event.getEntity()).getHandle();
			if (targetgolem instanceof F_IronGolem)
			{
				System.out.println("GOLEM DIED");
				F_IronGolem currgolem = (F_IronGolem) targetgolem;
				fortress_root fortplugin = (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
				F_DBFortressData forttochange = fortplugin.getFortressDataHash().get(currgolem.OwningGroupString);
				if (forttochange != null)
				{
					System.out.println("Attempting to remove golem...");
					forttochange.removeGolem(currgolem.OwningGroupString, currgolem);
				}
				
			}
			
		}
		
		
	}
	
}

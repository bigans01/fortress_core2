package fortress.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class F_DBFortressData {
	F_SQLFortressDataQueries FortressDB = new F_SQLFortressDataQueries(); 
	
	Inventory testInventory;
	HashMap<String, F_DBFortressData> FortDataRef;
	F_ChatStringBuilder chatstringbuilder = new F_ChatStringBuilder();
	
	// following hashmaps are for sensors that have been loaded in their respective chunks.
	HashMap<String, F_Sensor> HashSensor;
	HashMap<String, F_ArrowTrap> HashArrow;
	HashMap<String, F_IronGolem> HashGolem;
	HashMap<UUID, Object> fortEntityUUIDList;
	
	 // following list is for all sensors, regardless of if they are loaded
	HashMap<String, UUID> allHashSensor;
	HashMap<String, UUID> allHashArrow;
	HashMap<String, UUID> allHashGolem;
	
	boolean fortHadHeart = false;
	
	public int nextSensorID;
	public int nextArrowID;
	public int nextGolemID;
	
	public int heartX;
	public int heartY;
	public int heartZ;
	public String fortWorld;
	
     F_DBFortressData(String fortname)
     {
    	 //testInventory = Bukkit.createInventory(null, 54, fortname);
    	 
    	 
    	 fortress_root fortplugin = (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
    	 //FortDataRef = fortplugin.getFortressDataHash().get(fortname);
    	 fortplugin.FortressDB.loadMaxDefenseIDs(fortname, this);
    	 fortplugin.FortressDB.retrieveEntityList(fortname, this);
    	 
    	 HashSensor = new HashMap<String, F_Sensor>();
    	 HashArrow = new HashMap<String, F_ArrowTrap>();
    	 HashGolem = new HashMap<String, F_IronGolem>();
    	 

    	 fortEntityUUIDList = new HashMap<UUID, Object>();
    	 
    	 //testInventory.setItem(0, new ItemStack(Material.DIRT, 1));
    	 
     }
     
     public Inventory getFortressInventory()
     {
    	 return testInventory;
     }
     
     public void setFortressInventory(Inventory inv)
     {
    	 testInventory = inv;
     }
     
     public void createNewFortressInventory(String fortname)
     {
    	 testInventory = Bukkit.createInventory(null, 54, fortname);
     }
     
     public void setFortHadHeart(boolean flag)
     {
    	 fortHadHeart = flag;
     }
     
     public boolean doesFortHaveHeart()
     {
    	 return fortHadHeart;
     }
     
     public int returnCurrentSensorID()
     {
    	 return nextSensorID;
     }
     
     public int returnCurrentArrowID()
     {
    	 System.out.println("ARROW ID: " + nextArrowID);
    	 return nextArrowID;
     }
     
     public int returnCurrentGolemID()
     {
    	 return nextGolemID;
     }
     
     public HashMap<String, F_Sensor> getSensorHash()
     {
    	 return HashSensor;
     }
     
     public HashMap<String, F_ArrowTrap> getArrowTrapHash()
     {
    	 return HashArrow;
     }
     
     public HashMap<String, F_IronGolem> getGolemHash()
     {
    	 return HashGolem;
     }
     
     
     public void addSensor(String fortressname, F_Sensor sensor)
     {
    	 FortressDB.createNewSensor(fortressname, sensor, this); // run queries to execute new sensor.
    	 newAllSensorHashMapEntry(sensor.getCustomName(), sensor.getUniqueID());
    	 nextSensorID++;	// increment by 1 here (in-game reference)
     }
     
     public void removeSensor(String fortressname, F_Sensor sensor)
     {
    	 FortressDB.removeSensor(fortressname, sensor, this);
    	 if (allHashSensor != null && allHashSensor.containsKey(sensor.getName()))
    	 {
    		 allHashSensor.remove(sensor.getName());
    	 }

    	 //HashSensor.remove(sensor.getCustomName());
     }
     
     public HashMap<UUID, Object> getUUIDList()
     {
    	 return fortEntityUUIDList; 
     }
     
     public void removeArrowTrap(String fortressname, F_ArrowTrap mine)
     {
    	 FortressDB.removeArrowTrap(fortressname, mine, this);
    	 if (allHashArrow != null && allHashArrow.containsKey(mine.getName()))
    	 {
    		 allHashSensor.remove(mine.getName());
    	 }
     }
     
     public void removeGolem(String fortressname, F_IronGolem golem)
     {
    	 FortressDB.removeGolem(fortressname, golem, this);
    	 if (allHashGolem != null && allHashGolem.containsKey(golem.getName()))
    	 {
    		 allHashGolem.remove(golem.getName());
    	 }
     }
     
     
     public void listSensors(String fortressname, Player sender)
     {
    	List<F_DataRowSensor> returnedstring = FortressDB.returnFortressSensorList(fortressname);
    	
    	if (returnedstring != null) // only do this if a list was actually returned. 
    	{
    		ComponentBuilder headerString = chatstringbuilder.constructSensorListHeader(fortressname);
	    	Iterator it = returnedstring.iterator();
	    	while (it.hasNext())
	    	{
	    		
	    		F_DataRowSensor entry = (F_DataRowSensor) it.next();
	    		if (HashSensor.containsKey(entry.name))
	    		{	
	    			chatstringbuilder.addOnlineToConstructedSensorList(headerString, entry);
	    			if (it.hasNext())
	    			{
	    				headerString.append("\n");
	    			}
	    			//System.out.println(entry);
	    		}	
	    		else
	    		{
	    			chatstringbuilder.addOfflineToConstructedSensorList(headerString, entry);
	    			if (it.hasNext())
	    			{
	    				headerString.append("\n");
	    			}
	    			//System.out.println("INACTIVE: " + entry);
	    		}
	    	}
	    	sender.spigot().sendMessage(headerString.create());
    	}	
	    	/*Iterator it = HashSensor.entrySet().iterator();
	     	F_Sensor pointer;
	     	while (it.hasNext())
	     	{
	     		Map.Entry<String, F_Sensor> entry = (Map.Entry<String, F_Sensor>)it.next();
	     		pointer = entry.getValue();
	     		System.out.println(entry.getKey());
	     	 
     		
     	}*/
     }
     
     public void listArrowTraps(String fortressname, Player sender)
     {
    	 List<F_DataRowArrowTrap> returnedstring = FortressDB.returnFortressArrowTrapList(fortressname);
    	 
    	 ComponentBuilder headerStringTwo = chatstringbuilder.constructArrowTrapListHeader(fortressname);
	    	Iterator it = returnedstring.iterator();
	    	System.out.println("Arrow trap list loaded, attempting to print...");
	    	while (it.hasNext())
	    	{
	    		
	    		F_DataRowArrowTrap entry = (F_DataRowArrowTrap) it.next();
	    		//System.out.println("ENTRY VAL: " + entry);
	    		if (HashArrow.containsKey(entry.name))
	    		{	
	    			//System.out.println("Arrow trap add attempt...");
	    			chatstringbuilder.addOnlineToConstructedArrowTrapList(headerStringTwo, entry);
	    			if (it.hasNext())
	    			{
	    				headerStringTwo.append("\n");
	    			}
	    			//System.out.println(entry);
	    			System.out.println("Name: " + entry.name + "Type: " + entry.type);
	    		}	
	    		else
	    		{
	    			//System.out.println("Arrow trap add attempt...(2)");
	    			chatstringbuilder.addOfflineToConstructedArrowTrapList(headerStringTwo, entry);
	    			//System.out.println("Arrow trap add attempt...(2 finish)");
	    			if (it != null)
	    			{
	    				//System.out.println("Arrow trap add attempt...(eror check)");
		    			if (it.hasNext())
		    			{
		    				//System.out.println("Arrow trap add attempt...(2 final)");
		    				headerStringTwo.append("\n");
		    				//ComponentBuilder temprow = new ComponentBuilder(entry);
		    				//sender.spigot().sendMessage(temprow.create());
		    				//System.out.println("Arrow trap add attempt...(2 final end)");
		    			}
	    			//System.out.println("INACTIVE: " + entry);
		    		System.out.println("Name: " + entry.name + "Type: " + entry.type);
	    			}
	    		}
	    	}
	    	sender.spigot().sendMessage(headerStringTwo.create());
 	}	
	    	/*Iterator it = HashSensor.entrySet().iterator();
	     	F_Sensor pointer;
	     	while (it.hasNext())
	     	{
	     		Map.Entry<String, F_Sensor> entry = (Map.Entry<String, F_Sensor>)it.next();
	     		pointer = entry.getValue();
	     		System.out.println(entry.getKey());
	     	 
  		
  	}*/
     
     
     public void addArrowTrap(String fortressname, F_ArrowTrap arrowtrap, String playername)
     {
    	 FortressDB.createNewArrowTrap(fortressname, arrowtrap, this, playername);
    	 newAllArrowHashMapEntry(arrowtrap.getCustomName(),arrowtrap.getUniqueID());
    	 nextArrowID++;		// increment by 1 here (in-game reference)
     }
     
     public void changeName(String fortname, String originalname, String newname, int entitytype) // used to change names of entities defending a fortress.
     {
    	 // 1. do a switch check
    	 switch (entitytype)
    	 {
    		 case 0: // 0 = sensors
    			 //System.out.println(FortressDB.checkIfNameValid(fortname, originalname, 0));
    			 //HashSensor.put(newname, HashSensor.get(originalname)); // insert new hashmap entry
    			 //HashSensor.remove(originalname); // remove old entry
    			 //HashSensor.get(newname).setFortressSensorName(newname); // set the sensor's new name
    			 FortressDB.runNameChangeQuery(fortname, originalname, newname, entitytype, this);// do database update
    			 break;
    		 case 1: // 1 = arrow traps
    			 FortressDB.runNameChangeQuery(fortname, originalname, newname, entitytype, this);// do database update
    			 break;
    		 case 2: // 2 = golems
    			 FortressDB.runNameChangeQuery(fortname, originalname, newname, entitytype, this);// do database update
    			 break;
    	 }
    	 // 2. pass parameters to query to update the data
    	 // 3. put a new entry into the corresponding hashmap
    	 // 4. remove old entry from the corresponding hashmap 
     }
     
     public void setAllSensorHashOnEnable(HashMap<String, UUID> hashmap)
     {
    	 allHashSensor = hashmap;
     }
     
     public void setAllArrowHashOnEnable(HashMap<String, UUID> hashmap)
     {
    	 allHashArrow = hashmap;
     }
     
     public void setAllGolemHashOnEnable(HashMap<String, UUID> hashmap)
     {
    	 allHashGolem = hashmap;
     }
     
     public HashMap<String, UUID> getAllSensorHashMap()
     {
    	 return allHashSensor;
     }
     
     public HashMap<String, UUID> getAllArrowHashMap()
     {
    	 return allHashArrow;
     }
     
     public HashMap<String, UUID> getAllGolemHashMap()
     {
    	 return allHashGolem;
     }
     
     public void newAllSensorHashMapEntry(String string, UUID uuid)
     {
    	 if (allHashSensor == null)
    	 {
    		 System.out.println("|||||||SENSOR HASH WAS NULL, POPULATIING ||||||");
    		 allHashSensor = new HashMap<String, UUID>();
    		 allHashSensor.put(string, uuid);
    	 } else if (allHashSensor != null)
    	 {
    		 System.out.println("||||||| POPULATIING SENSOR HASH ||||||");
    		 allHashSensor.put(string, uuid);
    	 }
     }
     
     public void newAllArrowHashMapEntry(String string, UUID uuid)
     {
    	 if (allHashArrow == null)
    	 {
    		 allHashArrow = new HashMap<String, UUID>();
    		 allHashArrow.put(string, uuid);
    	 } else if (allHashArrow != null)
    	 {
    		 allHashArrow.put(string, uuid);
    	 }
     }
     
     public void newAllGolemHashMapEntry(String string, UUID uuid)
     {
    	 if (allHashGolem == null)
    	 {
    		 allHashGolem = new HashMap<String, UUID>();
    		 allHashGolem.put(string, uuid);
    	 } else if (allHashGolem != null)
    	 {
    		 allHashGolem.put(string, uuid);
    	 }
     }
     
     public String getFortWorld()
     {
    	 return fortWorld;
     }
     
     public void setFortWorld(String world)
     {
    	 fortWorld = world;
     }
}

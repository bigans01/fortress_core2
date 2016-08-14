package fortress.plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.server.v1_9_R1.World;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class F_EventListenerLogOnOrOff implements Listener {
	
	HashMap<String, F_DBPlayerFortressPrivs> PermissionsIndex;
	HashMap<String, F_SQLPlayerDataQueries> DBControllerIndex;
	
	F_EventListenerLogOnOrOff(HashMap<String, F_DBPlayerFortressPrivs> PermissionsReference, HashMap<String, F_SQLPlayerDataQueries> DBControllerReference) // pass the world or plugin here. 
	{
		PermissionsIndex = PermissionsReference;
		DBControllerIndex = DBControllerReference;
	}
	
	@EventHandler
	public void onPlayerSpawnedItem(BlockDispenseEvent event)
	{
		System.out.println("Item spawned!!");
	
	}
	
	@EventHandler
	public void onPlayerJoinedServer(PlayerJoinEvent event)
	{
		// STEP 1: retrieve player's fortress membership list from database.
		
		F_DBPlayerFortressPrivs PlayerData = new F_DBPlayerFortressPrivs();
		F_DBPlayerFortressPrivs FinalData;
		F_SQLPlayerDataQueries QueryLookup = new F_SQLPlayerDataQueries(); 
		System.out.println(event.getPlayer().getName() + "debug 1 ");
		
		PlayerData.setPlayerFortressMembership(QueryLookup.getFortressMemberships(event.getPlayer().getName(), PermissionsIndex)); // retrieve the data from the database, pull it into the list of this object 
		//QueryLookup.getFortressMemberships(event.getPlayer().getName());
		
		/// adding to hashmap comes last. Call method from plugin. Pass plugin to this classes' constructor. 
		Set PermissionsSet = PermissionsIndex.entrySet();
		Iterator PermsIterator = PermissionsSet.iterator();
		
		
		PermissionsIndex.put(event.getPlayer().getName(), PlayerData); // add to the server's player hashmap.
		System.out.println(event.getPlayer().getName() + "'s data has been read. Allied to following fortress(s): ");
		
		FinalData = PermissionsIndex.get(event.getPlayer().getName());
		
		
		
		System.out.println(event.getPlayer().getName() + "'s data has been read. Allied to following fortress(s): 2::::");
		if (FinalData.getPlayerFortressMembership() != null)
		{
			System.out.println(FinalData.getPlayerFortressMembership().size());
			for (int i = 0; i < FinalData.getPlayerFortressMembership().size(); i++)
			{
				System.out.println(FinalData.getPlayerFortressMembership().get(i));
			}
		}
		
		
		// STEP 2: add player and a SQL object to his/her hashmap entry. 
		DBControllerIndex.put(event.getPlayer().getName(), QueryLookup);
		
		
		//set the alignment that was read from the DB:
		FinalData.setFortressAlignment(DBControllerIndex.get(event.getPlayer().getName()).getFortressAlignmentFromDB(event.getPlayer().getName(), PermissionsIndex));
		
	}
	
	
	@EventHandler
	public void onPlayerLeftServer(PlayerQuitEvent event)
	{
		F_SQLPlayerDataQueries termConnection = DBControllerIndex.get(event.getPlayer().getName());
		termConnection.severConnection();
	}
}

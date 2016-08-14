package fortress.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.minecraft.server.v1_9_R1.EntityMagmaCube;
import net.minecraft.server.v1_9_R1.ItemStack;

public class F_Heart extends EntityMagmaCube implements Listener{
	public F_Heart(net.minecraft.server.v1_9_R1.World world)
	{
		super(world);
		this.j(true); // pick up loot flag. 
		//this.get
	}
	
	@EventHandler
	public void onPlayerSpawnedItem(BlockDispenseEvent event)
	{
		System.out.println("Item spawned!! (heart)");
	
	}
	
	
	
	

}

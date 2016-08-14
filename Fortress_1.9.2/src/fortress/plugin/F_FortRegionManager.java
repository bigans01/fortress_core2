package fortress.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class F_FortRegionManager {
	boolean isPlayerInFort(Player player, String fortName)
	{
		fortress_root fortplugin = (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
		WorldGuardPlugin wgplugin = fortplugin.getWorldGuard();
		
		for(ProtectedRegion r : wgplugin.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation())) {
            //Check if region is the correct one through r.getId() if by name
            //Do the firework thing
			
			if (r.getId().equalsIgnoreCase(fortName)) // add check for multiple fortress regions here
			{
				System.out.println("Player is inside the fortress' region...");
				return true;
			}
			
			
        }
		
		return false;
	}
}

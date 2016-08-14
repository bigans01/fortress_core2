package fortress.plugin;

import static fortress.plugin.Utils2.getPrivateField;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityZombie;
import net.minecraft.server.v1_9_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_9_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_9_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;

import java.lang.reflect.*; 							// used for Field data type.

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public enum EntityTypes
{
    //NAME("Entity name", Entity ID, yourcustomclass.class);
    CUSTOM_ZOMBIE("CZombie", 54, CustomZombie.class), //You can add as many as you want.
    FORTRESS_GOLEM("FGolem", 99, F_IronGolem.class),
    FORTRESS_MINE("FMine",  67, F_ArrowTrap.class),
    FORTRESS_SENSOR("FSensor", 60, F_Sensor.class),
    FORTRESS_HEART("FHeart", 62, F_Heart.class);
    
    
    private EntityTypes(String name, int id, Class<? extends Entity> custom)
    {
    	
        addToMaps(custom, name, id);
    }
    
    private static String pname;

    public static void spawnEntity(Entity entity, Location loc)
   {
     entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
     //entity.setOnFire(10);a
     //entity.setCustomName("Ol Bob");
     //entity.setCustomNameVisible(true);
     ((CraftWorld)loc.getWorld()).getHandle().addEntity(entity);
   }
  

    private static void addToMaps(Class clazz, String name, int id)
    {
    	//this.logger.info(pdffile.getName() + "Custom Entities Registering...");
        //getPrivateField is the method from above.
        //Remove the lines with // in front of them if you want to override default entities (You'd have to remove the default entity from the map first though).
        ((Map)getPrivateField("c", net.minecraft.server.v1_9_R1.EntityTypes.class, null)).put(name, clazz);
        ((Map)getPrivateField("d", net.minecraft.server.v1_9_R1.EntityTypes.class, null)).put(clazz, name);
        //((Map)getPrivateField("e", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(Integer.valueOf(id), clazz);
        ((Map)getPrivateField("f", net.minecraft.server.v1_9_R1.EntityTypes.class, null)).put(clazz, Integer.valueOf(id));
        //((Map)getPrivateField("g", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, Integer.valueOf(id));
    }
    
    public static void pTest()
    {
    	pname = "test";
    }	
}

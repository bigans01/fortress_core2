package fortress.plugin;

import net.minecraft.server.v1_9_R1.*;
import java.util.logging.Logger;

import org.bukkit.Location;

public class PathfinderGoalWalkToLoc extends PathfinderGoal 
{
	
	private double speed;
	public final Logger logger = Logger.getLogger("Minecraft");
	private EntityInsentient entity;

	private Location loc;

	private Navigation navigation;

	public PathfinderGoalWalkToLoc(EntityInsentient entity, Location loc, double speed)
	   {
	     this.entity = entity;
	     this.loc = loc;
	     this.navigation = (Navigation) this.entity.getNavigation();
	     this.speed = speed;
	   }
	
	@Override
	public boolean a()
	{
		return true;
	}

	@Override
	public boolean g()
	{
		//this.logger.info("I VOID command executed...");
		return true;
	}
	
	@Override
	public void c()
    {
		this.logger.info("C VOID command executed...");
        PathEntity pathEntity = this.navigation.a(loc.getX(), loc.getY(), loc.getZ());
        this.navigation.a(pathEntity, speed);
        
    }
	

}

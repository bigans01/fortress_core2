package fortress.plugin;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_9_R1.EntityCreature;
import net.minecraft.server.v1_9_R1.EntityIronGolem;
import net.minecraft.server.v1_9_R1.GenericAttributes;
import net.minecraft.server.v1_9_R1.PathfinderGoal;
import net.minecraft.server.v1_9_R1.RandomPositionGenerator;
import net.minecraft.server.v1_9_R1.Vec3D;
import org.inventivetalent.*;
import org.inventivetalent.particle.ParticleEffect;

public class F_GolemPathfinderGoalFollowOwner extends PathfinderGoal
{
	private EntityCreature a;
	  private double b;
	  private double c;
	  private double d;
	  private double e;
	  private int f;
	  private boolean g;
	  
	  public int ownerset;
	  
	  Player custom_owner;
	  
	  // the custom entity this goal belongs to 
	  EntityCreature custom_entity;
	  
	  // the current target of the entity that this goal belongs to
	  Entity custom_entity_target;
	  
	  public F_GolemPathfinderGoalFollowOwner(EntityCreature paramEntityCreature, double paramDouble)
	  {
	    this(paramEntityCreature, paramDouble, 120);
	  }
	  
	  public F_GolemPathfinderGoalFollowOwner(EntityCreature paramEntityCreature, double paramDouble, int paramInt)
	  {
	    this.a = paramEntityCreature;
	    this.e = paramDouble;
	    this.f = paramInt;
	    a(1);
	  }
	  
	  public boolean a()
	  {
		  //System.out.println("-----------------GOAL CALL---------------------");
	    if (!this.g)
	    {
	      if (this.a.bK() >= 100) {
	        return false;
	      }
	      if (this.a.getRandom().nextInt(this.f) != 0) {
	        return false;
	      }
	    }
	    Vec3D localVec3D = RandomPositionGenerator.a(this.a, 10, 7);
	    if (localVec3D == null) {
	      return false;
	    }
	    
	    // 92, 82, 74 
	    //this.b = localVec3D.a;
	    //this.c = localVec3D.b;          -- original random vector results
	    //this.d = localVec3D.c;
	    this.b = 92;
	    this.c = 82;
	    this.d = 74;
	    if (ownerset == 3)
	    {
	    	//System.out.println("-----------------OWNER SET CHECK---------------------");
	    	
	    	if (custom_entity_target == null)
	    	{
	    		//System.out.println("-----------------NO CURRENT LIVE TARGET--------------------");
	    		custom_entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.40D);
	    	}
	    	
	    	if (custom_entity_target != null)
	    	{
	    		System.out.println("-----------------TARGET IS DEAD---------------------");
		    	if (custom_entity_target.isDead() == true)
		    	{
		    		System.out.println("-----------------TARGET IS DEAD---------------------");
		    		custom_entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(1.00D);
		    	}
	    	}
	    	this.b = this.custom_owner.getLocation().getX();
	    	this.c = this.custom_owner.getLocation().getY();
	    	this.d = this.custom_owner.getLocation().getZ();
	    	
	    	try {
				//ParticleEffect.FLAME.sendToPlayer(custom_owner, custom_entity.getBukkitEntity().getLocation(), 0.00F, 0.005F, 0.00F, 0.05F, 50, true);
	    		List<Player> testcollect = new ArrayList<Player>();
	    		testcollect.add(custom_owner);
	    		ParticleEffect.FLAME.send(testcollect, custom_entity.getBukkitEntity().getLocation(), 0.00, 0.005, 0.00, 0.05, 50);
	  
	    		//ParticleEffect.FLAME
	    		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    this.g = false;
	    return true;
	  }
	  
	  public boolean b()
	  {
	    return !this.a.getNavigation().n();
	  }
	  
	  public void c()
	  {
	    this.a.getNavigation().a(this.b, this.c, this.d, this.e);
	  }
	  
	  public void f()
	  {
	    this.g = true;
	  }
	  
	  public void setTimeBetweenMovement(int paramInt)
	  {
	    this.f = paramInt;
	  }
	  
	  //set owner for custom F Golem.
	  public void setCustomOwner(Player player, F_IronGolem golem)
	  {
		  ownerset = 3;
		  custom_owner = player;
		  custom_entity = (EntityCreature)golem;
		  setTimeBetweenMovement(8); // set pathfinding call interval. smaller = faster.
	  }
	  
	//set owner for custom F Golem.
	  public void setMineOwner(Player player, F_ArrowTrap golem)
	  {
		  ownerset = 3;
		  custom_owner = player;
		  custom_entity = (EntityCreature)golem;
		  setTimeBetweenMovement(8); // set pathfinding call interval. smaller = faster.
	  }
	  
	  
	  public void setCurrentTarget(Entity entity)
	  {
		  custom_entity_target = entity;
	  }
	  
}

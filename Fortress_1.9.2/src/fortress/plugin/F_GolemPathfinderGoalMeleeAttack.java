package fortress.plugin;

import java.util.Random;
import java.util.logging.Logger;

import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityCreature;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.EnumHand;
import net.minecraft.server.v1_9_R1.PathEntity;
import net.minecraft.server.v1_9_R1.PathfinderGoal;
import net.minecraft.server.v1_9_R1.World;

public class F_GolemPathfinderGoalMeleeAttack extends PathfinderGoal
{
	World a;
	protected EntityCreature b;
	int c;
	double d;
	boolean e;
	PathEntity f;
	Class<? extends Entity> g;
	private int h;
	private double i;
	private double j;
	private double k;
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public F_GolemPathfinderGoalMeleeAttack(EntityCreature paramEntityCreature, Class<? extends Entity> paramClass, double paramDouble, boolean paramBoolean)
	{
	  this(paramEntityCreature, paramDouble, paramBoolean);
	  this.g = paramClass;
	}
	
	public F_GolemPathfinderGoalMeleeAttack(EntityCreature paramEntityCreature, double paramDouble, boolean paramBoolean)
	{
	  this.b = paramEntityCreature;
	  this.a = paramEntityCreature.world;
	  this.d = paramDouble;
	  this.e = paramBoolean;
	  a(3);
	}
	
	public boolean a()
	{
	  EntityLiving localEntityLiving = this.b.getGoalTarget();
	  if (localEntityLiving instanceof EntityHuman)
	  {
		  F_IronGolem ConvertGolem = (F_IronGolem)this.b;
		  EntityHuman GolemHumanTarget = (EntityHuman)localEntityLiving; 
		  this.logger.info("GOLEM TARGETING HUMAN! (a)");
		  if (ConvertGolem.getOwner().equals(GolemHumanTarget.getName())) // determines if the golem will continue attacking the human, or stop. 
		  {
			  this.logger.info("TARGETED HUMAN IS NOW ALLIED, CANCELLING ATTACK?...");
			  this.b.setGoalTarget(null); // resets, ("clears") the target if the human suddenly becomes allied while attacking. 
			  return false;
		  }
	  }
	  
	  if (localEntityLiving == null) {
	    return false;
	  }
	  if (!localEntityLiving.isAlive() || localEntityLiving instanceof F_ArrowTrap ) {  // testing: set OR statement to be instance of F_Mine. --> || localEntityLiving instanceof F_Mine
		  this.logger.info("GOLEM FOUND MINE!! A was called, attacking will be stopped.");
	    return false;
	  }
	  if ((this.g != null) && (!this.g.isAssignableFrom(localEntityLiving.getClass()))) {
	    return false;
	  }
	  this.f = this.b.getNavigation().a(localEntityLiving);
	  return this.f != null;
	}
	
	public boolean b()
	{
	  EntityLiving localEntityLiving = this.b.getGoalTarget();
	  
	  if (localEntityLiving instanceof EntityHuman)
	  {
		  F_IronGolem ConvertGolem = (F_IronGolem)this.b;
		  EntityHuman GolemHumanTarget = (EntityHuman)localEntityLiving; 
		  this.logger.info("GOLEM TARGETING HUMAN! (b)");
		  if (ConvertGolem.getOwner().equals(GolemHumanTarget.getName()))
		  {
			  this.logger.info("TARGETED HUMAN IS NOW ALLIED...");
			  return false;
		  }
	  }
	  
	  if (localEntityLiving == null) {
	    return false;
	  }
	  if (!localEntityLiving.isAlive()) {
		  this.logger.info("GOLEM FOUND MINE!!"); // testing: set OR statement to be instance of F_Mine. --> || localEntityLiving instanceof F_Mine 
	    return false;
	  }
	  if (!this.e)
	  {
	    if (this.b.getNavigation().n()) {
	      return false;
	    }
	    return true;
	  }
	  if (!this.b.f(new BlockPosition(localEntityLiving))) {
	    return false;
	  }
	  return true;
	}
	
	public void c()
	{
	  this.b.getNavigation().a(this.f, this.d);
	  this.h = 0;
	}
	
	public void d()
	{
	  this.b.getNavigation().n();
	}
	
	public void e()
	{
		this.logger.info("GOLEM ATTACKING...");
	  EntityLiving localEntityLiving = this.b.getGoalTarget();
	  this.b.getControllerLook().a(localEntityLiving, 30.0F, 30.0F);
	  double d1 = this.b.e(localEntityLiving.locX, localEntityLiving.getBoundingBox().b, localEntityLiving.locZ);
	  double d2 = a(localEntityLiving);
	  this.h -= 1;
	  if (((this.e) || (this.b.getEntitySenses().a(localEntityLiving))) && 
	    (this.h <= 0) && (
	    ((this.i == 0.0D) && (this.j == 0.0D) && (this.k == 0.0D)) || (localEntityLiving.e(this.i, this.j, this.k) >= 1.0D) || (this.b.getRandom().nextFloat() < 0.05F)))
	  {
	    this.i = localEntityLiving.locX;
	    this.j = localEntityLiving.getBoundingBox().b;
	    this.k = localEntityLiving.locZ;
	    this.h = (4 + this.b.getRandom().nextInt(7));
	    if (d1 > 1024.0D) {
	      this.h += 10;
	    } else if (d1 > 256.0D) {
	      this.h += 5;
	    }
	    if (!this.b.getNavigation().a(localEntityLiving, this.d)) {
	      this.h += 15;
	    }
	  }
	  this.c = Math.max(this.c - 1, 0);
	  if ((d1 <= d2) && (this.c <= 0))
	  {
	    this.c = 20;
	    /*if (this.b.bA() != null) {
	      this.b.bw();
	    }*/
	    this.b.a(EnumHand.MAIN_HAND);
	    this.b.r(localEntityLiving);
	  }
	}
	
	protected double a(EntityLiving paramEntityLiving)
	{
	  return this.b.width * 2.0F * (this.b.width * 2.0F) + paramEntityLiving.width;
	}
}


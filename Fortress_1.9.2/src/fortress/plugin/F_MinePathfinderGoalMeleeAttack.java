package fortress.plugin;


import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Enchantment;
import net.minecraft.server.v1_9_R1.EnchantmentManager;
import net.minecraft.server.v1_9_R1.Enchantments;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityArrow;
import net.minecraft.server.v1_9_R1.EntityCreature;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.EntityTippedArrow;
import net.minecraft.server.v1_9_R1.EnumParticle;
import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.Items;
import net.minecraft.server.v1_9_R1.MathHelper;
import net.minecraft.server.v1_9_R1.PathEntity;
import net.minecraft.server.v1_9_R1.PathfinderGoal;
import net.minecraft.server.v1_9_R1.World;

public class F_MinePathfinderGoalMeleeAttack extends PathfinderGoal
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
	F_ArrowTrap castedTrap;
	HashMap<UUID, org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow> firedArrowHash;
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public F_MinePathfinderGoalMeleeAttack(EntityCreature paramEntityCreature, Class<? extends Entity> paramClass, double paramDouble, boolean paramBoolean, HashMap<String, F_DBPlayerFortressPrivs> permsref)
	{
	  this(paramEntityCreature, paramDouble, paramBoolean, permsref);
	  this.g = paramClass;
	  castedTrap = (F_ArrowTrap)this.b;
	  firedArrowHash = castedTrap.getArrowHash();
	}
	
	public F_MinePathfinderGoalMeleeAttack(EntityCreature paramEntityCreature, double paramDouble, boolean paramBoolean, HashMap<String, F_DBPlayerFortressPrivs> permsref)
	{
	  this.b = paramEntityCreature;
	  this.a = paramEntityCreature.world;
	  this.d = paramDouble;
	  this.e = paramBoolean;
	  castedTrap = (F_ArrowTrap)this.b;
	  firedArrowHash = castedTrap.getArrowHash();
	  a(3);
	}
	
	public boolean a()
	{
	  EntityLiving localEntityLiving = this.b.getGoalTarget();
	  
	  if (localEntityLiving == null) {
		    return false;
		  }
	  
	  
	  if (localEntityLiving instanceof EntityHuman)
	  {
		  F_ArrowTrap ConvertGolem = (F_ArrowTrap)this.b;
		  EntityHuman GolemHumanTarget = (EntityHuman)localEntityLiving; 
		  this.logger.info("MINE TARGETING HUMAN! (a)");
		  //ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName())
		  //ConvertGolem.getOwner().equals(GolemHumanTarget.getName())
		  
		  if (ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName())) // determines if the golem will continue attacking the human, or stop. 
		  {
			  this.logger.info("TARGETED HUMAN IS NOW ALLIED WITH MINE, CANCELLING ATTACK?...");
			  //if (localEntityLiving != null)
			  //{
				  this.b.setGoalTarget(null); // resets, ("clears") the target if the human suddenly becomes allied while attacking. 
			  //}
			  //this.b.setGoalTarget(null); // resets, ("clears") the target if the human suddenly becomes allied while attacking. 
			  return false;
		  }
	  }
	  
	  
	  if (!localEntityLiving.isAlive() || localEntityLiving instanceof F_ArrowTrap ) {  // testing: set OR statement to be instance of F_Mine. --> || localEntityLiving instanceof F_Mine
		  this.logger.info("MINE FOUND HUMAN!! A was called, attacking will be stopped.");
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
		  F_ArrowTrap ConvertGolem = (F_ArrowTrap)this.b;
		  EntityHuman GolemHumanTarget = (EntityHuman)localEntityLiving; 
		  //this.logger.info("MINE TARGETING HUMAN! (b)");
		  //this.logger.info(ConvertGolem.getOwner());
		  //this.logger.info(GolemHumanTarget.getName());
		  //System.out.println(ConvertGolem.getOwner() == GolemHumanTarget.getName());
		  //System.out.println(ConvertGolem.getOwner().equals(GolemHumanTarget.getName()));
		 
		  
		  //ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName())
		  //ConvertGolem.getOwner().equals(GolemHumanTarget.getName())
		  if (ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName()))
		  {
			  this.logger.info("TARGETED HUMAN IS NOW ALLIED WITH MINE...");
			  return false;
		  }
	  }
	  
	  if (localEntityLiving == null) {
	    return false;
	  }
	  if (!localEntityLiving.isAlive()) {
		  this.logger.info("MINE FOUND HUMAN (isAlive)!!"); // testing: set OR statement to be instance of F_Mine. --> || localEntityLiving instanceof F_Mine 
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
		//this.logger.info("MINE ATTACKING...");
	  EntityLiving localEntityLiving = this.b.getGoalTarget();
	  //this.b.getControllerLook().a(localEntityLiving, 30.0F, 30.0F);
	  //double d1 = this.b.e(localEntityLiving.locX, localEntityLiving.getBoundingBox().b, localEntityLiving.locZ);
	  //double d2 = a(localEntityLiving);
	  /*this.h -= 1;
	  if (((this.e) || (this.b.getEntitySenses().a(localEntityLiving))) && 
	    (this.h <= 0) && (
	    ((this.i == 0.0D) && (this.j == 0.0D) && (this.k == 0.0D)) || (localEntityLiving.e(this.i, this.j, this.k) >= 1.0D) || (this.b.bc().nextFloat() < 0.05F)))
	  {
	    this.i = localEntityLiving.locX;
	    this.j = localEntityLiving.getBoundingBox().b;
	    this.k = localEntityLiving.locZ;
	    this.h = (4 + this.b.bc().nextInt(7));
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
	    if (this.b.bA() != null) {
	      this.b.bw();
	    }
	    this.b.r(localEntityLiving);
	  }*/
	  
	  
	  
	  
	  
	  /* ARROW FIRING */
	  /* ARGUMENT EXPLANATION:
	   * 1. The world the event occurs in.
	   * 2. Entity firing the arrow.
	   * 3. Target of the arrow.
	   * 4. ????
	   * 5. Damage of the arrow?
	   * new ItemStack(Items.BOW)???
	   * */
	  
	  //this.logger.info("ARROW FIRING...");
	  //EntityArrow entityarrow = new EntityArrow(this.b.world, this.b, this.b.getGoalTarget(), 1.6F, 14 - this.b.world.getDifficulty().a() * 4);
	  ItemStack MineArrows = new ItemStack(Items.ARROW);
	  
	  /* bA() returns an itemstack; a new one will have to be created. See EntityInsentient class. */
	  /*int i = EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_DAMAGE.id, MineArrows);
	  int j = EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK.id, MineArrows);
   
	  //entityarrow.b(f * 2.0F + this.random.nextGaussian() * 0.25D + this.world.getDifficulty().a() * 0.11F);
	  entityarrow.b(2.0F + 1 * 0.25D + this.b.world.getDifficulty().a() * 0.11F);  // <---- new experimental line.
	  
	  if (i > 0) 
	  {
		  entityarrow.b(entityarrow.j() + i * 0.5D + 0.5D);
	  }
	    
	  if (j > 0) 
	  {
		  entityarrow.setKnockbackStrength(j);
	  }
	   
	  if ((EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_FIRE.id, MineArrows) > 0))
	  {
		  EntityCombustEvent event = new EntityCombustEvent(entityarrow.getBukkitEntity(), 100);
		  this.b.world.getServer().getPluginManager().callEvent(event);
	  
		  if (!event.isCancelled()) 
		  {
			  entityarrow.setOnFire(event.getDuration());
		  }
	  }*/
	   
	 
	  EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.b.world, this.b);
	    double d0 = this.b.getGoalTarget().locX - this.b.locX;
	    double d1 = this.b.getGoalTarget().getBoundingBox().b + this.b.getGoalTarget().length / 3.0F - entitytippedarrow.locY;
	    double d2 = this.b.getGoalTarget().locZ - this.b.locZ;
	    double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
	    
	    entitytippedarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, 14 - this.b.world.getDifficulty().a() * 4);
	    int i = EnchantmentManager.a(Enchantments.ARROW_DAMAGE, this.b);
	    int j = EnchantmentManager.a(Enchantments.ARROW_KNOCKBACK, this.b);
	    
	    entitytippedarrow.c(1 * 2.0F + 1 * 0.25D + this.b.world.getDifficulty().a() * 0.11F);  // arrow's damage? 
	    
	    
	    if (i > 0) {
	      entitytippedarrow.c(entitytippedarrow.k() + i * 0.5D + 0.5D);
	    }
	    if (j > 0) {
	      entitytippedarrow.setKnockbackStrength(j);
	    }
	    if ((EnchantmentManager.a(Enchantments.ARROW_FIRE, this.b) > 0) /*|| (getSkeletonType() == 1)*/)
	    {
	      EntityCombustEvent event = new EntityCombustEvent(entitytippedarrow.getBukkitEntity(), 100);
	      this.b.world.getServer().getPluginManager().callEvent(event);
	      if (!event.isCancelled()) {
	        entitytippedarrow.setOnFire(event.getDuration());
	      }
	    }
	    
	    entitytippedarrow.setOnFire(100);
	  
	  /* ABOVE IS NEW */
	  
	  /*ARGUMENT PARAMETERS EXPLANATION:
	   *  1. The entity shooting the arrow
	   *  2. The item stack to shoot
	   *  3. The arrow entity to fire
	   *  4. ??? 
	   * 
	   * */
	   
	  EntityShootBowEvent event = org.bukkit.craftbukkit.v1_9_R1.event.CraftEventFactory.callEntityShootBowEvent(this.b, MineArrows, entitytippedarrow, 0.8F);
	  //b.world.addParticle(EnumParticle.SMOKE_LARGE, b.locX + (this.random.nextDouble() - 0.5D) * b.width, b.locY + this.random.nextDouble() * b.length, b.locZ + (this.random.nextDouble() - 0.5D) * b.width, (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D, new int[0]);
	  //b.world.addParticle(EnumParticle.SMOKE_LARGE, b.locX  * b.width, b.locY  * b.length, b.locZ * b.width,  2.0D, -2.0D, 2.0D, new int[0]);
	  
	  if (event.isCancelled()) 
	  {
		  event.getProjectile().remove();
		  return;
	  }
	     
	  if (event.getProjectile() == entitytippedarrow.getBukkitEntity()) 
	  {
	       this.b.world.addEntity(entitytippedarrow);
	       event.getProjectile().setTicksLived(900);
	       
	       //try
	       //{
	       //System.out.println("--->>>> ARROW UUID: " + event.getProjectile().getUniqueId());
	       //firedArrowHash.put(event.getProjectile().getUniqueId(),(org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow) event.getProjectile());
	       //} catch ( Exception e ) {
	 	     // System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      //System.exit(0);
		    //}
	       
	    	   //firedArrowHash.put(event.getProjectile().getUniqueId(),(net.minecraft.server.v1_9_R1.EntityTippedArrow) event.getProjectile());
	      
	       firedArrowHash.put(event.getProjectile().getUniqueId(),(org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow) event.getProjectile());
	       clearFiredArrows();
	  }
	      
	  
	  //makeSound("random.bow", 1.0F, 1.0F / (bc().nextFloat() * 0.4F + 0.8F));
	  //this.b.makeSound("random.bow", 1.0F, 1.0F / 1.0F * 0.4F + 0.8F);
	  
	  //this.b.a(SoundEffects.fn, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
	  
	}
	
	protected double a(EntityLiving paramEntityLiving)
	{
	  return this.b.width * 2.0F * (this.b.width * 2.0F) + paramEntityLiving.width;
	}
	
	protected void clearFiredArrows()
	{
	  
		if (firedArrowHash.isEmpty() == false)
		{
			for(Entry<UUID, org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow> entry : firedArrowHash.entrySet())
			{
				UUID trgtArrowKey = entry.getKey();
				org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow trgtArrowVal = entry.getValue();
				if (trgtArrowVal.isOnGround() == true)
				{
					/*try{
						firedArrowHash.remove(trgtArrowKey);
						trgtArrowVal.remove();
					}catch ( Exception e ) {
				 	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
					      //System.exit(0);
					}*/
					trgtArrowVal.remove();
					//firedArrowHash.remove(trgtArrowKey);
					//System.out.println("arrow check");
					
				}
			}
		}
	}
}



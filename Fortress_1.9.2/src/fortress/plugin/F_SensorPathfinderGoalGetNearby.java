package fortress.plugin;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_9_R1.block.CraftBlock;

import net.minecraft.server.v1_9_R1.BlockLever;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.BlockStateBoolean;
import net.minecraft.server.v1_9_R1.BlockStateInteger;
import net.minecraft.server.v1_9_R1.Enchantment;
import net.minecraft.server.v1_9_R1.EnchantmentManager;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityArrow;
import net.minecraft.server.v1_9_R1.EntityCreature;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.Items;
import net.minecraft.server.v1_9_R1.PathEntity;
import net.minecraft.server.v1_9_R1.PathfinderGoal;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.material.Lever;

public class F_SensorPathfinderGoalGetNearby extends PathfinderGoal {
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
	private Block RedstoneDeviceTarget; // location of the redstone device. 
	private Block RedstoneAttachedBlock;
	F_SensorSetLeverOnOff setLeverOnOff = new F_SensorSetLeverOnOff();
	HashMap<String, F_DBPlayerFortressPrivs> PermsIndex;
	String targetAlignment;
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public F_SensorPathfinderGoalGetNearby(EntityCreature paramEntityCreature, Class<? extends Entity> paramClass, double paramDouble, boolean paramBoolean, HashMap<String, F_DBPlayerFortressPrivs> permsref)
	{
	  this(paramEntityCreature, paramDouble, paramBoolean, permsref);
	  this.g = paramClass;
	  
	}
	
	public F_SensorPathfinderGoalGetNearby(EntityCreature paramEntityCreature, double paramDouble, boolean paramBoolean, HashMap<String, F_DBPlayerFortressPrivs> permsref)
	{
	  this.b = paramEntityCreature;
	  this.a = paramEntityCreature.world;
	  this.d = paramDouble;
	  this.e = paramBoolean;
	  this.PermsIndex = permsref;
	  a(3);
	  
	}
	
	public boolean a()
	{
	  EntityLiving localEntityLiving = this.b.getGoalTarget();
	  
	  
	  //this.logger.info("SENSOR LOOP CALLED (a)");
	  //System.out.println("PRE-CAST");
	  F_Sensor initSensorPointer = (F_Sensor)b;
	  //this.logger.info("SENSOR LOOP CALLED (a) 2");
	  // if the entity has no paired sensor to begin with, return null. 
	  if (initSensorPointer.returnPairedBlock() == null && initSensorPointer.hasPairedBlock() == false)
	  {
		  //this.logger.info("SENSOR LOOP CALLED (a) 3");
		  return false;
	  }
	  
	  /* STEP 1: Ensure that a sensor can reach its lever before moving on to actual targeting. If no condition (see below) is met, return false. 
	   * 
	   * 
	   * 
	   * 
	   * */
	  
	  
	  // --------------------------------------------------------------------------------
	  // CONDITION 1: NEW SENSOR AND LEVER RECEIVER 
	  // Sensor has a paired block, and it is returned, but it did not come from NBT data.
	  // --------------------------------------------------------------------------------
	  //
	  // First, do checks to ensure the chunk containing the block is loaded, AND that it actually has a paired block to begin (argument 2), 
	  // BUT the paired xyz is not set (arguments 3, 4, and 5; this would happen if the entity hasn't attempted to find the block from the xyz locations chunk. This is to avoid crashing.)
	  //
	  //
	  // This will result in a server-stopping crash if not resolved. Only do this if it actually has an assigned block.
	  if (initSensorPointer.returnPairedBlock() != null && initSensorPointer.hasPairedBlock() == true && initSensorPointer.PairedX==0 && initSensorPointer.PairedY==0 && initSensorPointer.PairedZ==0 ) // if this value is null, the sensor has either A.) just loaded into the world or B.) hasn't actually acquired a target for this goal yet. && initSensorPointer.hasPairedBlock() == true 
		  
	  {
		  //System.out.println("CHECK 1");
		  if (initSensorPointer.returnPairedBlock().getChunk().isLoaded())	  
		  {
			  RedstoneDeviceTarget = initSensorPointer.returnPairedBlock();
			  //System.out.println("CHECK 2");
		  }
		  //else if (!a.getWorld().getBlockAt(13, 78, -43).getChunk().isLoaded())
		  else if (!initSensorPointer.returnPairedBlock().getChunk().isLoaded())
		  {
			  System.out.println("CHECK 3");
			  return false;
		  }
		
	  }
	  //System.out.println(initSensorPointer.PairedX); //debug only

	  
	  
	  
	  // --------------------------------------------------------------------------------
	  // CONDITION 2: SENSOR LOADS RECEIVER FROM NBT
	  //  Sensor has a paired block to be loaded from NBT. Only signals receiver if that chunk is loaded. 
	  // --------------------------------------------------------------------------------
	  // the below statement executes immediately after NBT load, but only until the redstone lever is loaded.
	  if (initSensorPointer.returnPairedBlock() == null && initSensorPointer.hasPairedBlock() == true && initSensorPointer.PairedX!=0 && initSensorPointer.PairedY!=0 && initSensorPointer.PairedZ!=0 )
	  {
		  System.out.println("CHECK 4");
		  Block targetLever = a.getWorld().getBlockAt(initSensorPointer.PairedX, initSensorPointer.PairedY, initSensorPointer.PairedZ);
		  if (a.getWorld().getBlockAt(initSensorPointer.PairedX, initSensorPointer.PairedY, initSensorPointer.PairedZ).getChunk().isLoaded())
		  {
			  System.out.println("CHECK 5");
			  RedstoneDeviceTarget = a.getWorld().getBlockAt(initSensorPointer.PairedX, initSensorPointer.PairedY, initSensorPointer.PairedZ);
			  initSensorPointer.setPairedBlock(RedstoneDeviceTarget);
			  
		  }
		  else if (!a.getWorld().getBlockAt(initSensorPointer.PairedX, initSensorPointer.PairedY, initSensorPointer.PairedZ).getChunk().isLoaded())
		  {
			  System.out.println("CHECK 6");
			  return false;
		  }
		  
	  }
	  
	  // --------------------------------------------------------------------------------
	  // CONDITION 3: SENSOR THAT IS ALREADY LOADED AND HAS A RECEIVER GETS ITS RECEIVER CHANGED
	  //  Sensor has a paired block to be loaded from NBT. Only signals receiver if that chunk is loaded. 
	  // --------------------------------------------------------------------------------
	  if (initSensorPointer.returnPairedBlock() != null && initSensorPointer.hasPairedBlock() == true && initSensorPointer.PairedX!=0 && initSensorPointer.PairedY!=0 && initSensorPointer.PairedZ!=0 )
	  {
		  //System.out.println("CHECK 4");
		  Block targetLever = a.getWorld().getBlockAt(initSensorPointer.PairedX, initSensorPointer.PairedY, initSensorPointer.PairedZ);
		  if (a.getWorld().getBlockAt(initSensorPointer.PairedX, initSensorPointer.PairedY, initSensorPointer.PairedZ).getChunk().isLoaded())
		  {
			  //System.out.println("CHECK 7");
			  RedstoneDeviceTarget = a.getWorld().getBlockAt(initSensorPointer.PairedX, initSensorPointer.PairedY, initSensorPointer.PairedZ);
			  initSensorPointer.setPairedBlock(RedstoneDeviceTarget);
			  
		  }
		  else if (!a.getWorld().getBlockAt(initSensorPointer.PairedX, initSensorPointer.PairedY, initSensorPointer.PairedZ).getChunk().isLoaded())
		  {
			  //System.out.println("CHECK 8");
			  return false;
		  }
	  }
	  
	  /* STEP 2: Once an above condition is met (ensuring that the sensor is sync'ed to its lever), do the below calculations. 
	   * 
	   * 
	   * 
	   * 
	   * */
	  
	  //System.out.println(RedstoneDeviceTarget);
	  
	  // turn lever off if this isnt met
	  if (localEntityLiving == null) 
	  {
		  //this.logger.info("SENSOR HAS NO TARGET. (a)");
		  if (RedstoneDeviceTarget != null)
		  {
			  if (RedstoneDeviceTarget.getTypeId() == 69)
			  {
				  setLeverOnOff.F_SensorSetLeverOnOff(this.a, RedstoneDeviceTarget, false); // true indicates to turn the power on. 
			  }
		  }
		  return false;
	  }
	  
	  // NOTE: 1/17/2016. Will need to change this based on the sensor's mode. (allies, neutral, enemies)
	  // turn lever on if something in here is met. 
	  if (localEntityLiving instanceof EntityHuman)
	  {
		  F_Sensor ConvertGolem = (F_Sensor)this.b;
		  EntityHuman GolemHumanTarget = (EntityHuman)localEntityLiving; 
		  this.logger.info("SENSOR TARGETING HUMAN! (a)");
		//System.out.println(RedstoneDeviceTarget);
		  
		  switch (ConvertGolem.getTargetingMode())
		  {
		  
		  	//!ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName())
      		//!ConvertGolem.getOwner().equals(GolemHumanTarget.getName()) ||
		  	case 0: // friendly -- do not activate redstone signal (that is, true) on hostile targets. 
		  		if (!ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName()))
				  {
					  //this.logger.info("TARGETED HUMAN IS NOW ALLIED WITH SENSOR...");
		  			  this.b.setGoalTarget(null);
					  return false;
				  }
		  	break;
		  	
		  	//ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName())
      		//ConvertGolem.getOwner().equals(GolemHumanTarget.getName()) ||
		  	
		  	case 1: // hostile -- do not activate redstone signal (that is, true) on friendly targets. 
		    
		  		if (ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName()))
				  {
					  //this.logger.info("TARGETED HUMAN IS NOW ALLIED WITH SENSOR...");
		  			  this.b.setGoalTarget(null);
					  return false;
				  }
		  		break;
		  	
		  		
		  }
		  
					  if (RedstoneDeviceTarget != null)
					  {
			  			  if (RedstoneDeviceTarget.getTypeId() == 69)
						  {
							  setLeverOnOff.F_SensorSetLeverOnOff(this.a, RedstoneDeviceTarget, true); // true indicates to turn the power on. 
						  }
					  }
					  
					  /*if (ConvertGolem.getOwner().equals(GolemHumanTarget.getName())) // determines if the golem will continue attacking the human, or stop. 
					  {
						  this.logger.info("TARGETED HUMAN IS NOW ALLIED WITH SENSOR, CANCELLING ATTACK?...");
						  //if (localEntityLiving != null)
						  //{
							  this.b.setGoalTarget(null); // resets, ("clears") the target if the human suddenly becomes allied while attacking. 
						  //}
						  //this.b.setGoalTarget(null); // resets, ("clears") the target if the human suddenly becomes allied while attacking. 
						  return false;
					  }*/
	  }
	  
	  
	  if (!localEntityLiving.isAlive() || localEntityLiving instanceof F_Sensor ) {  // testing: set OR statement to be instance of F_Sensor. --> || localEntityLiving instanceof F_Sensor
		  this.logger.info("SENSOR FOUND HUMAN!! A was called, attacking will be stopped.");
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
		  F_Sensor ConvertGolem = (F_Sensor)this.b;
		  EntityHuman GolemHumanTarget = (EntityHuman)localEntityLiving;
		 
		  targetAlignment = PermsIndex.get(GolemHumanTarget.getName()).getFortressAlignment();
		  //this.logger.info("TARGET ALIGNMENT: (b) " + targetAlignment);
		  //this.logger.info(ConvertGolem.getOwner());
		  //this.logger.info(GolemHumanTarget.getName());
		  //System.out.println(ConvertGolem.getOwner() == GolemHumanTarget.getName());
		  //System.out.println(ConvertGolem.getOwner().equals(GolemHumanTarget.getName()));
		  
		  
		  switch (ConvertGolem.getTargetingMode())
		  {
		  	//!ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName())
    		//!ConvertGolem.getOwner().equals(GolemHumanTarget.getName()) ||
		  	case 0: // friendly -- do not activate redstone signal (that is, true) on hostile targets. 
		  		if (!ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName()))
				  {
					  //this.logger.info("TARGETED HUMAN IS NOW ALLIED WITH SENSOR...");
					  return false;
				  }
		  		break;
		  	//ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName())
		  	//ConvertGolem.getOwner().equals(GolemHumanTarget.getName()) ||
		  	case 1: // hostile -- do not activate redstone signal (that is, true) on friendly targets. 
		  		if (ConvertGolem.isTargetMemberOfAlignedFortress(GolemHumanTarget.getName()))
				  {
					  this.logger.info("TARGETED HUMAN IS NOW ALLIED WITH SENSOR...");
					  return false;
				  }
		  		break;
		  }
		  
		  
		  /*if (ConvertGolem.getOwner().equals(GolemHumanTarget.getName()))
		  {
			  //this.logger.info("TARGETED HUMAN IS NOW ALLIED WITH SENSOR...");
			  return false;
		  }*/
	  }
	  
	  if (localEntityLiving == null) {
	    return false;
	  }
	  if (!localEntityLiving.isAlive()) {
		  this.logger.info("SENSOR FOUND HUMAN (isAlive)!!"); // testing: set OR statement to be instance of F_Sensor. --> || localEntityLiving instanceof F_Sensor 
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
		//this.logger.info("SENSOR ATTACKING...");
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
	  
	  /*EntityArrow entityarrow = new EntityArrow(this.b.world, this.b, this.b.getGoalTarget(), 1.6F, 14 - this.b.world.getDifficulty().a() * 4);
	  ItemStack MineArrows = new ItemStack(Items.ARROW);
	  
	  /* bA() returns an itemstack; a new one will have to be created. See EntityInsentient class. 
	  int i = EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_DAMAGE.id, MineArrows);
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
	       
	  /*ARGUMENT PARAMETERS EXPLANATION:
	   *  1. The entity shooting the arrow
	   *  2. The item stack to shoot
	   *  3. The arrow entity to fire
	   *  4. ??? 
	   * 
	   * */
	   
	  /*EntityShootBowEvent event = org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory.callEntityShootBowEvent(this.b, MineArrows, entityarrow, 0.8F);
	  
	  if (event.isCancelled()) 
	  {
		  event.getProjectile().remove();
		  return;
	  }
	     
	  if (event.getProjectile() == entityarrow.getBukkitEntity()) 
	  {
	       this.b.world.addEntity(entityarrow);
	  }
	      
	  
	  //makeSound("random.bow", 1.0F, 1.0F / (bc().nextFloat() * 0.4F + 0.8F));
	  this.b.makeSound("random.bow", 1.0F, 1.0F / 1.0F * 0.4F + 0.8F); */
	  
	  
	  
	}
	
	protected double a(EntityLiving paramEntityLiving)
	{
	  return this.b.width * 2.0F * (this.b.width * 2.0F) + paramEntityLiving.width;
	}
}

package fortress.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import com.google.common.base.Predicate;

import static fortress.plugin.Utils2.getPrivateField;
import net.minecraft.server.v1_9_R1.Block;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.DamageSource;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityCreeper;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntityIronGolem;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.GenericAttributes;
import net.minecraft.server.v1_9_R1.IBlockData;
import net.minecraft.server.v1_9_R1.IMonster;
import net.minecraft.server.v1_9_R1.Material;
import net.minecraft.server.v1_9_R1.MathHelper;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.PathfinderGoalFollowOwner;
import net.minecraft.server.v1_9_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_9_R1.TileEntity;
import net.minecraft.server.v1_9_R1.World;
import net.minecraft.server.v1_9_R1.EnumParticle;
import net.minecraft.server.v1_9_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_9_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalOfferFlower;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.PathfinderGoalDefendVillage;
import net.minecraft.server.v1_9_R1.World;

import org.bukkit.event.entity.EntityTargetEvent;

import java.lang.reflect.*; 							// used for Field data type.

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class F_IronGolem extends EntityIronGolem 
{
	//PathfinderGoalRandomStrollCustom pgoal = new PathfinderGoalRandomStrollCustom(this, 1.4D);  // set the pathfinder goal object for movement, so it can be referenced by this class later.
	F_GolemPathfinderGoalFollowOwner pgoal;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	// display name of the owner.
    String ownerName = new String();
     
    Player owner;			// actual player object of the owner (this needs to be pointing to the correct player object on load, somehow)
    String isOwnerSet = new String(); 	// true/false flag to see if owner is set already
    String canTeleport = new String();  // set if the golem can teleport to owner when far away.
    
    // database specific parameters. 
    F_SQLEntityDataQueries FortressDBHandler;
    long DBIdentifier;
    UUID IsRegistered; // for storing the result of the golem's registry check. 
    String OwningGroupString = "";
	
    
    Entity currentTarget;			// the current entity the golem is targeting.
    
    Entity custom_entity_target;	// entity of the golem's owner
    Double owner_x;          // location of owner's x/y
    Double owner_z;
    
    Double golem_x, golem_z; // location of golem's x/y
	private int bv;
	private int bw;
    
    // constructor.
	public F_IronGolem(net.minecraft.server.v1_9_R1.World world)
	{
		super(world);
		FortressDBHandler = new F_SQLEntityDataQueries();
		System.out.println("--------------------------------- golem constructor called. ---------------------------------");
		fortress_root rootPluginRet =  (fortress_root) world.getServer().getPluginManager().getPlugin("Fortress");
         
        ownerName = "N/Atest";
        isOwnerSet = "N";
        canTeleport = "N";
        //Owner_Location 
        
        System.out.println("Constructor called."); // debugging only. 
        

	}
	
	@Override
	protected void r()
	{
		//System.out.println("--------------------------------- r() called. ---------------------------------"); // debugging only. 
		pgoal = new F_GolemPathfinderGoalFollowOwner(this, 1.4D); // goal for having the golem its owner.
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        //this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
        //this.goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.6D, true));
        this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        //this.goalSelector.a(5, new PathfinderGoalOfferFlower(this));
        //this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));       
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        //this.targetSelector.a(1, new PathfinderGoalDefendVillage(this));
        //this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        /*this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 10, false, true, new Predicate()
        {
          public boolean a(EntityInsentient entityinsentient)
          {
            return (entityinsentient != null) && (IMonster.e.apply(entityinsentient)) && (!(entityinsentient instanceof EntityCreeper));
          }
          
          public boolean apply(Object object)
          {
            return a((EntityInsentient)object);
          }
        }));*/
        
        
        
        
        
        //this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        //this.goalSelector.a(2, new PathfinderGoalRandomStrollCustom(this, 1.4D));    // NEW GOAL
        this.targetSelector.a(3, new F_GolemPathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 10, false, true, IMonster.e));
        this.goalSelector.a(7, pgoal);    // from pgoal declaration above
        
        // TARGET SELECTION
        this.targetSelector.a(3, new F_GolemPathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true)); // causes golem to attack any eligible human. WORKING GOAL
        
        // MELEE ATTACKS
        this.goalSelector.a(1, new F_GolemPathfinderGoalMeleeAttack(this, 1.0D, true)); //
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
	}
	
	@Override
	public void n()
	  {
	    super.n();
	    if (this.bv > 0) {
	      this.bv -= 1;
	    }
	    if (this.bw > 0) {
	      this.bw -= 1;
	    }
	    if ((this.motX * this.motX + this.motZ * this.motZ > 2.500000277905201E-7D) && (this.random.nextInt(5) == 0))
	    {
	      int i = MathHelper.floor(this.locX);
	      int j = MathHelper.floor(this.locY - 0.20000000298023224D);
	      int k = MathHelper.floor(this.locZ);
	      IBlockData iblockdata = this.world.getType(new BlockPosition(i, j, k));
	      if (iblockdata.getMaterial() != Material.AIR) {
	        this.world.addParticle(EnumParticle.BLOCK_CRACK, this.locX + (this.random.nextFloat() - 0.5D) * this.width, getBoundingBox().b + 0.1D, this.locZ + (this.random.nextFloat() - 0.5D) * this.width, 4.0D * (this.random.nextFloat() - 0.5D), 0.5D, (this.random.nextFloat() - 0.5D) * 4.0D, new int[] { Block.getCombinedId(iblockdata) });
	        this.world.addParticle(EnumParticle.DRAGON_BREATH, this.locX + (this.random.nextFloat() - 0.5D) * this.width, getBoundingBox().b + 0.1D, this.locZ + (this.random.nextFloat() - 0.5D) * this.width, 4.0D * (this.random.nextFloat() - 0.5D), 0.5D, (this.random.nextFloat() - 0.5D) * 4.0D, new int[] { Block.getCombinedId(iblockdata) });
	      }
	    }
	    
	    
	    if (isAlive()) 
		{
	    	//System.out.println("Alive loop check .....  : " + isOwnerSet);
			float f = this.random.nextFloat() - this.random.nextFloat();
			float f1 = this.random.nextFloat() - this.random.nextFloat();
			float f2 = this.random.nextFloat() - this.random.nextFloat();
			//for (int i = 0; i < 20; i++) 
			//{
				//   double t0 = this.random.nextGaussian() * 0.02D;
			     //  double t1 = this.random.nextGaussian() * 0.02D;
			      // double t2 = this.random.nextGaussian() * 0.02D;
		          // this.world.addParticle(EnumParticle.EXPLOSION_NORMAL, this.locX + this.random.nextFloat() * this.width * 2.0F - this.width, this.locY + this.random.nextFloat() * this.length, this.locZ + this.random.nextFloat() * this.width * 2.0F - this.width, t0, t1, t2, new int[0]);
			//}
			//this.world.addParticle(EnumParticle.EXPLOSION_NORMAL, this.locX + this.random.nextFloat() * this.width * 2.0F - this.width, this.locY + this.random.nextFloat() * this.length, this.locZ + this.random.nextFloat() * this.width * 2.0F - this.width, d0, d1, d2, new int[0]);
			//this.setOnFire(1);
			if (this.ticksLived < 60)
			{
				damageEntity(DamageSource.DROWN, 2.0F);
			}
			
			
			
			
			
			
			// Sets the entity's owner on server restart. Constantly checks for an owner until one is set.
			
			//System.out.println("Outside of if statement, Owner is set to....." + isOwnerSet);
			if (this.isOwnerSet == "N")
			{
				//System.out.println("Owner is set to....." + isOwnerSet);
				//System.out.println("Ownername during isOwnerSet Call: " + ownerName);
				
				// constantly attempt to load the owner of this customEntity; tick will continue to call this until ownerName = "Y"
				if (Bukkit.getServer().getPlayerExact(ownerName) != null)
				{
					
					this.setOwner(Bukkit.getServer().getPlayerExact(ownerName));
					
				}
			}
			
			
			
			
			// Teleport to player if they are too far away, outside of pathfinding range; only check if there is an actual owner set. 
			if (this.canTeleport == "Y" && this.isOwnerSet == "Y")
			{
				System.out.println("Teleport call attempted.");
				owner_x = custom_entity_target.getBukkitEntity().getLocation().getX();
				owner_z = custom_entity_target.getBukkitEntity().getLocation().getZ();
				
				golem_x = this.getBukkitEntity().getLocation().getX();			
				golem_z = this.getBukkitEntity().getLocation().getZ();

				
			}
				
			
			
			
		}
	  }
	
	

	
  	// set speed of the Fortress Golem, default settings. 
	@Override
	protected void initAttributes()
	{
	    super.initAttributes();
	    getAttributeInstance(GenericAttributes.maxHealth).setValue(100.0D);
	    getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(2.75D);
	}
	  
	
	
	
	
	
	// set new attributes.
	@Override
  	protected void C(Entity entity)
	{
	    if (((entity instanceof IMonster)) && (!(entity instanceof EntityCreeper)) && (getRandom().nextInt(20) == 0)) {
	      setGoalTarget((EntityLiving)entity, EntityTargetEvent.TargetReason.COLLISION, true);
	      
	      getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(2.75D);
	      currentTarget = entity;
	    }
	    super.C(entity);
	}	
	  
	  // ************************************NBT EDITING AREA***************************************
	  
	
	
	
	
	  
	  // Execute new  NBT save method after the super's
	@Override
	public void e(NBTTagCompound nbttagcompound)
	{
		//System.out.println("PRE-super call" + ownerName);
	    super.e(nbttagcompound);
	    System.out.println("POST-super call " + ownerName);
	    System.out.println("Player Display name: " + ownerName);
	    nbttagcompound.setString("FOwner", ownerName); // set the compound tag (test)
	    nbttagcompound.setString("OwningGroupString", OwningGroupString);
	    System.out.println("POST Set string call " + ownerName);
	}
		  
	  
	  
	// Load data from NBT on the entity load. 
	@Override
	public void f(NBTTagCompound nbttagcompound)
	{
		super.f(nbttagcompound);
		System.out.println("Entity NBT Load initiated... (GOLEM)");
		this.ownerName = nbttagcompound.getString("FOwner");
		this.DBIdentifier = nbttagcompound.getLong("UUIDMost");
		this.OwningGroupString = nbttagcompound.getString("OwningGroupString");
		System.out.println("Entity NBT Loaded FOwner: " + ownerName);
		System.out.println("Entity NBT Loaded UUID: " + DBIdentifier);
		
		// get the golem's group string from database, to make sure they are in-sync. This should only be done on entity instantiation/construction, and should be called only
        // under certain circumstances, to reduce lag. if the golem doesn't exist in the lookup table, add it. Should only be done after NBT data is read. (this is done in lines above)
		IsRegistered = FortressDBHandler.GetRegisteredGolem(this);
		
		try
		{
			if (IsRegistered == null)
			{
			    
				FortressDBHandler.RegisterGolem(this);
				System.out.println("Try call successful. " + ownerName);
			} 
			
			if (IsRegistered != null)	
			{
				System.out.println("Golem row found (NBT  load)");
				System.out.println("owning group: " + this.OwningGroupString);
				F_DataRowGolem retrievedrow = FortressDBHandler.retrieveGolemData(this.OwningGroupString, this.getUniqueID());
				if (retrievedrow != null)
				{
					setFortressGolemName(retrievedrow.name); // set the name
					
					fortress_root rootPluginRet =  (fortress_root) world.getServer().getPluginManager().getPlugin("Fortress"); // set these values on load.
					HashMap<String, F_DBFortressData> DBFortRef= rootPluginRet.getFortressDataHash();
					if (DBFortRef.get(OwningGroupString) != null)
					{
						DBFortRef.get(OwningGroupString).getGolemHash().put(retrievedrow.name, this);
						//System.out.println("Sensor successfully registered with Fortress' hashmap.");
					}
				}
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	  
	
	// Mount operations for fortress golem
	@Override
	public void g(float sideMot, float forMot) 
	{
		//System.out.println(this.passengers.size());
		//this.passengers.size();
		
		if ((this.motX * this.motX + this.motZ * this.motZ > 2.500000277905201E-7D) && (this.random.nextInt(5) == 0))
	    {
	      int i = MathHelper.floor(this.locX);
	      int j = MathHelper.floor(this.locY - 0.20000000298023224D);
	      int k = MathHelper.floor(this.locZ);
	      IBlockData iblockdata = this.world.getType(new BlockPosition(i, j, k));
	      //if (iblockdata.getMaterial() != Material.AIR) {
	    	//System.out.println("Particle test");
	        this.world.addParticle(EnumParticle.DRAGON_BREATH, this.locX + (this.random.nextFloat() - 0.5D) * this.width, getBoundingBox().b + 0.1D, this.locZ + (this.random.nextFloat() - 0.5D) * this.width, 4.0D * (this.random.nextFloat() - 0.5D), 0.5D, (this.random.nextFloat() - 0.5D) * 4.0D, new int[] { Block.getCombinedId(iblockdata) });
	      //}
	    }
		
		
		if (this.passengers.size() == 0 ) // || !(this.passengers.get(0) instanceof EntityHuman)
		{
	        super.g(sideMot, forMot);
	        this.P = 0.5F;    // Make sure the entity can walk over half slabs, instead of jumping
	        return;
	    }
	 
	    EntityHuman human = (EntityHuman) this.passengers.get(0);
	    /*if (human.getBukkitEntity() != Bukkit.getPlayerExact("bigans01").getPlayer()) {
	        // Same as before
	        super.g(sideMot, forMot);
	        this.P = 0.5F;
	        return;
	    }*/
	    
	    this.lastYaw = this.yaw = this.passengers.get(0).yaw;
	    this.pitch = this.passengers.get(0).pitch * 0.5F;
	 
	    // Set the entity's pitch, yaw, head rotation etc.
	    this.setYawPitch(this.yaw, this.pitch); //[url]https://github.com/Bukkit/mc-dev/blob/master/net/minecraft/server/Entity.java#L163-L166[/url]
	    this.aK = this.aI = this.yaw;
	 
	    this.P = 1.0F;    // The custom entity will now automatically climb up 1 high blocks
	 
	    sideMot = ((EntityLiving) this.passengers.get(0)).bd * 0.5F; // CHANGE OK
	    forMot = ((EntityLiving) this.passengers.get(0)).be;
	 
	    if (forMot <= 0.0F) {
	        forMot *= 0.25F;    // Make backwards slower
	    }
	    sideMot *= 0.75F;    // Also make sideways slower
	 
	    float speed = 0.35F;    // 0.2 is the default entity speed. I made it slightly faster so that riding is better than walking
	    this.k(speed);    // Apply the speed
	    super.g(sideMot, forMot);    // Apply the motion to the entity*/
	 
	 
	    /*try {
            ReflectionUtilities.FieldAccess jumpField = ReflectionUtilities.getField(EntityLiving.class, "aY");
            if (jumpField != null && this.onGround) {    // Wouldn't want it jumping while in the air would we?
                if (jumpField.get(Boolean.class, this.passenger)) {
                    double jumpHeight = 0.5D;
                    this.motY = jumpHeight;    // Used all the time in NMS for entity jumping
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
	}
	  
	
	
	  
	// Sets the owner for this entity, and signals the custom movement pgoal to know that it is set. 
	public void setOwner(Player player)
 	{
		this.logger.info("set Owner called...");
		this.owner = player;
	  
		// set the owner (a player object) into the pgoal
		pgoal.setCustomOwner(owner, this);
	  
	  
		ownerName = player.getDisplayName();
		System.out.println("Player Display name: " + ownerName);
		this.isOwnerSet = "Y";
 	}

	public String getOwner()
	{
		return ownerName;
	}
	  
	public long getDBID()
	{
		return DBIdentifier;
	}
	
	public void setFortressGolemName(String name)
	{
		this.setCustomName(name);
		this.setCustomNameVisible(true);
	}
	
	public void setGolemFortressGroup(String group)
	{
		OwningGroupString = group;
	}
}

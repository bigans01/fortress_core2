package fortress.plugin;

import static fortress.plugin.Utils2.getPrivateField;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import net.minecraft.server.v1_9_R1.Block;





import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.block.Block;
//import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Blocks;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntityRabbit;
import net.minecraft.server.v1_9_R1.IMonster;
import net.minecraft.server.v1_9_R1.Item;
import net.minecraft.server.v1_9_R1.Items;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.PathfinderGoalBreed;
import net.minecraft.server.v1_9_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R1.PathfinderGoalTempt;
import net.minecraft.server.v1_9_R1.SoundEffect;
import net.minecraft.server.v1_9_R1.SoundEffects;
import net.minecraft.server.v1_9_R1.World;

public class F_Sensor extends EntityRabbit {
	F_GolemPathfinderGoalFollowOwner pgoal = new F_GolemPathfinderGoalFollowOwner(this, 1.4D);  // set the pathfinder goal object for movement, so it can be referenced by this class later.
	public final Logger logger = Logger.getLogger("Minecraft");
	
	// display name of the owner.
    String ownerName = new String();
     
    Player owner;								// actual player object of the owner (this needs to be pointing to the correct player object on load, somehow)
    String isOwnerSet = new String(); 			// true/false flag to see if owner is set already
    String canTeleport = new String();  		// set if the golem can teleport to owner when far away.
    boolean isAssignedToPairedBlock = false; 	// evaluates to true if the paired block is loaded for the sensor. this is to prevent a crash in the loaded chunk. 
    byte activeFlag = 0;
    
    // database specific parameters. 
    F_SQLEntityDataQueries FortressDBHandler;
    long DBIdentifier;
    UUID IsRegistered;
    String OwningGroupString = "";
    String FortressAlign = "N/A";
    String FortressSensorName = "N/A";
    
    // for paired block management (and NBT Load)
    int PairedX=0;
    int PairedY=0;
    int PairedZ=0;
    
    // the sensor's targeting mode. 0 = friendly, 1 = hostile, 2 = neutral
    byte SensorMode = 0;
    HashMap<String, F_DBPlayerFortressPrivs> PermissionsIndex;
    
    
    World sensorWorld;
    
    Block PairedBlock; //paired redstone lever location
    
    Entity currentTarget;			// the current entity the golem is targeting.
    
    Entity custom_entity_target;	// entity of the golem's owner
    Double owner_x;          // location of owner's x/y
    Double owner_z;
    
	
	public F_Sensor(net.minecraft.server.v1_9_R1.World world)
	{
		super(world);
		FortressDBHandler = new F_SQLEntityDataQueries();
		sensorWorld = world;
		
		fortress_root rootPlugin =  (fortress_root) world.getServer().getPluginManager().getPlugin("Fortress");
        String linkTest = rootPlugin.rootLinkTest();
        System.out.println(linkTest);
         PermissionsIndex = rootPlugin.getPlayerFortressPrivs();
		
		//Block PairedBlock = world.getWorld().getBlockAt(13, 78, -43); // this block represents the redstone block the sensor will be paired to. 
        //List goalB = (List)getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        //List goalC = (List)getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        //List targetB = (List)getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        //List targetC = (List)getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
         
       Set goalB = (Set)getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
         Set goalC = (Set)getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
         Set targetB = (Set)getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
         Set targetC = (Set)getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
        
        //this.goalSelector.a(0, new PathfinderGoalFloat(this));
        //this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
        //this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, EntityVillager.class, 1.0D, true));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        //this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, false));
        //this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 3.0D));
        //PathfinderGoalRandomStrollCustom pgoal = new PathfinderGoalRandomStrollCustom(this, 1.4D);
        // StrollCustom has a method which sets the destination of the golem and where it goes, every time it is called. 
        //this.goalSelector.a(7, new PathfinderGoalRandomStrollCustom(this, 1.4D));    // NEW GOAL
        
        this.goalSelector.a(7, pgoal);    // from pgoal declaration above
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        
        // get list of attackable targets nearby
        //this.targetSelector.a(3, new F_SensorAcquireNearbyTarget(this, EntityInsentient.class, 10, false, true, IMonster.e, PermissionsIndex));
        //this.targetSelector.a(3, new F_SensorAcquireNearbyTarget(this, EntityHuman.class, true, PermissionsIndex));
        //this.goalSelector.a(1, new F_SensorPathfinderGoalGetNearby(this, 1.0D, true, PermissionsIndex));
        //this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true)); // 
         
        ownerName = "";
        isOwnerSet = "N";
        canTeleport = "N";
        //Owner_Location 
        
        // plugin call tests...used later to link this entity to a list of player groups. (done later)
        
        //long result = getUniqueID().getMostSignificantBits();
        //System.out.println(result);
        
        //HashMap<String> hmap = new HashMap<String>();
	}
	
	// Sets the owner for this entity, and signals the custom movement pgoal to know that it is set. 
		public void setOwner(Player player)
	 	{
			this.logger.info("set Owner called...");
			this.owner = player;
		  
			// set the owner (a player object) into the pgoal
			//pgoal.setMineOwner(owner, this);
		  
		  
			ownerName = player.getDisplayName();
			System.out.println("Player Display name: " + ownerName);
			this.isOwnerSet = "Y";

	 	}
	
	
	public String getOwner()
	{
		return ownerName;
	}
	
	
	@Override
	public int getRabbitType() 
	{
		return 99; // make "mine" always spawn as hostile
	}
	
	@Override
	public void b(NBTTagCompound nbttagcompound) // NBT Save call
	{

		//nbttagcompound.setInt("RabbitType", getRabbitType());
		//nbttagcompound.setInt("MoreCarrotTicks", this.bu);
		
		System.out.println("F_Sensor PRE-super call" + ownerName);
	    super.b(nbttagcompound);
	    System.out.println("F_Sensor POST-super call " + ownerName);
	    System.out.println("F_Sensor's Player Display name: " + ownerName);
	    nbttagcompound.setString("FOwner", ownerName); // set the compound tag (test)
	    nbttagcompound.setString("OwningGroupString", OwningGroupString);
	    nbttagcompound.setString("FortressAlign", FortressAlign);
	    nbttagcompound.setString("FortressSensorName", FortressSensorName);
	    nbttagcompound.setBoolean("IsPairedToBlock", isAssignedToPairedBlock);
	    nbttagcompound.setByte("activeFlag", activeFlag);
	    nbttagcompound.setByte("SensorMode", SensorMode);
	    System.out.println("Value of PairedBlock: " + PairedBlock);
	    
	    
	    if (isAssignedToPairedBlock == true && PairedBlock != null) // write paired block location to NBT.
	    {
	    	System.out.println(PairedBlock.getX());
	    	System.out.println(PairedBlock.getY());
	    	System.out.println(PairedBlock.getZ());
	    	nbttagcompound.setInt("PairedBlockX", PairedBlock.getX());
	    	nbttagcompound.setInt("PairedBlockY", PairedBlock.getY());
	    	nbttagcompound.setInt("PairedBlockZ", PairedBlock.getZ());
	    }
	    
	    fortress_root rootPluginRet =  (fortress_root) world.getServer().getPluginManager().getPlugin("Fortress");
		HashMap<String, F_DBFortressData> DBFortRef= rootPluginRet.getFortressDataHash();
		if (!FortressAlign.equals("N/A"))
		{
			if (DBFortRef.get(FortressAlign) != null)
			{
				if (DBFortRef.get(FortressAlign).getSensorHash().containsKey(FortressSensorName))
				{
					DBFortRef.get(FortressAlign).getSensorHash().remove(FortressSensorName);				// remove from the owning fortress' hashmap (entity despawned) -- only do this if its not null.
					DBFortRef.get(FortressAlign).getUUIDList().remove(this.getUniqueID());
				}
			}
		}
	    System.out.println("F_Sensor POST Set string call " + ownerName);
	}
		    
    @Override
	public void a(NBTTagCompound nbttagcompound)  // NBT Load call
	{
		
		//setRabbitType(nbttagcompound.getInt("RabbitType"));
		//this.bu = nbttagcompound.getInt("MoreCarrotTicks");
		
    	super.a(nbttagcompound);
		System.out.println("F_Sensor NBT Load initiated...");
		this.ownerName = nbttagcompound.getString("FOwner");
		this.DBIdentifier = nbttagcompound.getLong("UUIDMost");
		this.OwningGroupString = nbttagcompound.getString("OwningGroupString");
		this.FortressAlign = nbttagcompound.getString("FortressAlign");
		this.SensorMode = nbttagcompound.getByte("SensorMode");
		this.activeFlag = nbttagcompound.getByte("activeFlag");
		if (activeFlag == 1)
		{
			loadGoals();
		}
		
		System.out.println("F_Sensor NBT Loaded FOwner: " + ownerName);
		System.out.println("F_Sensor NBT Loaded UUID: " + DBIdentifier);
		System.out.println("F_Sensor NBT Loaded Fortress: " + FortressAlign);
		this.isAssignedToPairedBlock = nbttagcompound.getBoolean("IsPairedToBlock");
		
		
		if (isAssignedToPairedBlock == true) // write paired block location to NBT.
		{
		    	//nbttagcompound.setInt("PairedBlockX", PairedBlock.getX());
		    	//nbttagcompound.setInt("PairedBlockY", PairedBlock.getY());
		    	//nbttagcompound.setInt("PairedBlockY", PairedBlock.getZ());
			//System.out.println("F_Sensor PAIRED X: " + nbttagcompound.getInt("PairedBlockX"));
			//System.out.println("F_Sensor PAIRED Y: " + nbttagcompound.getInt("PairedBlockY"));
			//System.out.println("F_Sensor PAIRED Z: " + nbttagcompound.getInt("PairedBlockZ"));
			PairedX = nbttagcompound.getInt("PairedBlockX");
			PairedY = nbttagcompound.getInt("PairedBlockY");
			PairedZ = nbttagcompound.getInt("PairedBlockZ");
			//while (PairedBlock == null)
			//{
				//if (sensorWorld.getWorld().getBlockAt(nbttagcompound.getInt("PairedBlockX"),nbttagcompound.getInt("PairedBlockY"),nbttagcompound.getInt("PairedBlockZ")).getChunk().isLoaded());
				//{//initSensorPointer.returnPairedBlock().getChunk().isLoaded()
					//PairedBlock = sensorWorld.getWorld().getBlockAt(nbttagcompound.getInt("PairedBlockX"),nbttagcompound.getInt("PairedBlockY"),nbttagcompound.getInt("PairedBlockZ"));
			//	}
			//}
		}
		// get the golem's group string from database, to make sure they are in-sync. This should only be done on entity instantiation/construction, and should be called only
        // under certain circumstances, to reduce lag. if the golem doesn't exist in the lookup table, add it. Should only be done after NBT data is read. (this is done in lines above)
		
		this.FortressSensorName = nbttagcompound.getString("FortressSensorName");
		
		//System.out.println("DEBUG: '" + FortressSensorName + "'");
		//System.out.println("DEBUG: '" + FortressAlign + "'");
		
		if (!FortressSensorName.equals("N/A") && !FortressAlign.equals("N/A"))
		{
			fortress_root rootPluginRet =  (fortress_root) world.getServer().getPluginManager().getPlugin("Fortress");
			HashMap<String, F_DBFortressData> DBFortRef= rootPluginRet.getFortressDataHash();
			if (DBFortRef.get(FortressAlign) != null)
			{
				DBFortRef.get(FortressAlign).getSensorHash().put(FortressSensorName, this);
				DBFortRef.get(FortressAlign).getUUIDList().put(this.getUniqueID(), this);
				//System.out.println("Sensor successfully registered with Fortress' hashmap.");
			}
			//System.out.println("Sensor successfully registered with Fortress' hashmap.");
		}
		
		IsRegistered = FortressDBHandler.GetRegisteredSensor(this);
		
		System.out.println("Pre-try check (Sensor)" + DBIdentifier);
		System.out.println("Value of FortressAlign before check: " + FortressAlign);
		try
		{
			if (IsRegistered == null && !FortressAlign.equals("N/A"))
			{
				
				FortressDBHandler.RegisterSensor(this);
				//System.out.println("Try call successful. (Sensor)" + ownerName);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// set the custom name of the fortress entity, on its NBT load.
		F_DataRowSensor retrievedrow = FortressDBHandler.retrieveSensorData(this.FortressAlign, this.getUniqueID());
		if (retrievedrow != null)
		{
			setFortressSensorName(retrievedrow.name);
			if (retrievedrow.mode != null)
			{
				switch (retrievedrow.mode)
				{
					case "Friendly":
						SensorMode = 0;
						break;
					case "Hostile":
						SensorMode = 1;
						break;
					case "Neutral":
						SensorMode = 2;
						break;
				}
			}
		}
		
		
	}
    @Override
    public void M() {
    	     /*if (this.moveController.b() > 0.8D) {
    	      a(EnumRabbitState.SPRINT);
    	     } else if (this.bt != EnumRabbitState.ATTACK) {
    	    a(EnumRabbitState.HOP);
    	    }
      
     if (this.bs > 0) {
          this.bs -= 1;
        }
        
        if (this.bu > 0) {
          this.bu -= this.random.nextInt(3);
         if (this.bu < 0) {
         this.bu = 0;
        }
       }
    	   
    	    if (this.onGround) {
           if (!this.br) {
            a(false, EnumRabbitState.NONE);
          cw();
        }
         
          if ((getRabbitType() == 99) && (this.bs == 0)) {
          EntityLiving entityliving = getGoalTarget();
        
       if ((entityliving != null) && (h(entityliving) < 16.0D)) {
        a(entityliving.locX, entityliving.locZ);
          this.moveController.a(entityliving.locX, entityliving.locY, entityliving.locZ, this.moveController.b());
           b(EnumRabbitState.ATTACK);
          this.br = true;
         }
       }
     
      ControllerJumpRabbit entityrabbit_controllerjumprabbit = (ControllerJumpRabbit)this.g;
       
      if (!entityrabbit_controllerjumprabbit.c()) {
        if ((this.moveController.a()) && (this.bs == 0)) {
              PathEntity pathentity = this.navigation.j();
    	        Vec3D vec3d = new Vec3D(this.moveController.d(), this.moveController.e(), this.moveController.f());
    	        
             if ((pathentity != null) && (pathentity.e() < pathentity.d())) {
               vec3d = pathentity.a(this);
          }
         
    	        a(vec3d.a, vec3d.c);
    	          b(this.bt);
           }
    	    } else if (!entityrabbit_controllerjumprabbit.d()) {
         ct();
     }*/
    	}
    
    @Override
    public void setRabbitType(int i) {
    	/* 281 */     if (i == 99) {
    	/* 282 */       //this.goalSelector.a(this.bm);
    	/* 283 */       //this.goalSelector.a(4, new PathfinderGoalKillerRabbitMeleeAttack(this));
    	/* 284 */       //this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
    	/* 285 */       //this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    	/* 286 */       //this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityWolf.class, true));
    	/* 287 */       //if (!hasCustomName()) 
    					//{
    	/* 288 */         //setCustomName(LocaleI18n.get("entity.KillerBunny.name"));
    	/*     */       //}
    	/*     */     }
    }
    
    
    @Override
    public void initializePathFinderGoals()
    /*     */   {
    /*  30 */     //this.navigation.a(2.5F);
    /*  31 */     //this.goalSelector.a(1, new PathfinderGoalFloat(this));
    /*  32 */     //this.goalSelector.a(1, new PathfinderGoalRabbitPanic(this, 1.33D));
    /*  33 */     this.goalSelector.a(2, new PathfinderGoalTempt(this, 1.0D, Items.CARROT, false));
    /*  34 */     this.goalSelector.a(2, new PathfinderGoalTempt(this, 1.0D, Items.GOLDEN_CARROT, false));
    /*  35 */     this.goalSelector.a(2, new PathfinderGoalTempt(this, 1.0D, Item.getItemOf(Blocks.YELLOW_FLOWER), false));
    /*  36 */     this.goalSelector.a(3, new PathfinderGoalBreed(this, 0.8D));
    /*  37 */     //this.goalSelector.a(5, new PathfinderGoalEatCarrots(this));
    /*  38 */     //this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 0.6D));
    /*  39 */     this.goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
    /*  40 */     //this.bm = new PathfinderGoalRabbitAvoidTarget(this, EntityWolf.class, 16.0F, 1.33D, 1.33D);
    /*  41 */     //this.goalSelector.a(4, this.bm);
    /*     */   }
    
    @Override // new hurt sound (set to skeleton hurt)
    protected SoundEffect bR()
    {
      return SoundEffects.fm;
    }
    
    //	/*     */   @Override
    //	/*     */   protected String bp() {
    //	/* 213 */     return "mob.skeleton.death";
    //	/*     */   }
    
    protected SoundEffect bS() // on death sound (set to skeleton death)
    {
      return SoundEffects.fi;
    }
    
    
    
    
	public long getDBID()
	{
		return DBIdentifier;
	}
	
	public Block returnPairedBlock()
	{
		return PairedBlock;
	}
	
	public void setPairedBlock(Block inputBlock)
	{
		PairedBlock = inputBlock;
		isAssignedToPairedBlock = true;
	}
	
	public boolean hasPairedBlock()
	{
		if (isAssignedToPairedBlock == true)
		{
			return true;
		}
		return false;
	}
	
	public byte getTargetingMode()
	{
		return SensorMode;
	}
	
	public void cycleTargetingMode()    // cycles through the targeting modes. 
	{
		switch (SensorMode)
		{
			case 0:
				SensorMode = 1;
				FortressDBHandler.updateSensorMode(this);
				break;
			case 1:
				SensorMode = 2;
				FortressDBHandler.updateSensorMode(this);
				break;
			case 2:
				SensorMode = 0;
				FortressDBHandler.updateSensorMode(this);
				break;
		}
		
	}
	
	public void setTargetingMode(String mode)
	{
		switch (mode)
		{
			case "F":
				SensorMode = 0;
				FortressDBHandler.updateSensorMode(this);
				break;
			case "H":
				SensorMode = 1;
				FortressDBHandler.updateSensorMode(this);
				break;
			case "N":
				SensorMode = 2;
				FortressDBHandler.updateSensorMode(this);
				break;
		}
	}
	
	public void setGoals()
	{
		switch (activeFlag)
		{
			case 0:
				this.targetSelector.a(3, new F_SensorAcquireNearbyTarget(this, EntityInsentient.class, 10, false, true, IMonster.e, PermissionsIndex));
		        this.targetSelector.a(3, new F_SensorAcquireNearbyTarget(this, EntityHuman.class, true, PermissionsIndex));
		        this.goalSelector.a(1, new F_SensorPathfinderGoalGetNearby(this, 1.0D, true, PermissionsIndex));
				activeFlag = 1;
				break;
			case 1:
				
				activeFlag = 0;
				break;
		}
		
	}
	
	public void loadGoals()
	{
		this.targetSelector.a(3, new F_SensorAcquireNearbyTarget(this, EntityInsentient.class, 10, false, true, IMonster.e, PermissionsIndex));
        this.targetSelector.a(3, new F_SensorAcquireNearbyTarget(this, EntityHuman.class, true, PermissionsIndex));
        this.goalSelector.a(1, new F_SensorPathfinderGoalGetNearby(this, 1.0D, true, PermissionsIndex));
	}
	
	public void setAlignment(String fort)
	{
		this.FortressAlign = fort;
	}
	
	public String getAlignment()
	{
		return FortressAlign;
	}
	
	public boolean isTargetMemberOfAlignedFortress(String player)
	{
		List<String> MemberOfFortressList = PermissionsIndex.get(player).getPlayerFortressMembership();
		if (MemberOfFortressList != null)
		{
			if (!MemberOfFortressList.isEmpty())
		    {
		    	for (Iterator<String> itr = MemberOfFortressList.iterator(); itr.hasNext();)
			    {
			    	String element = itr.next();
			    	if (element.equals(getAlignment()))
			    	{
			    		return true;
			    	}
			    	
			    } 
		    }
		}
		return false;
	}
	
	public void setFortressSensorName(String name)
	{
		FortressSensorName = name;
		this.setCustomName(FortressSensorName);
	}
	
	public void setSensorNameOnCreate(String name)
	{
		this.setCustomName(name);
		this.setCustomNameVisible(true);
	}
	
	
}

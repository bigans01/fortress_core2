package fortress.plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Logger;

import static fortress.plugin.Utils2.getPrivateField;
import net.minecraft.server.v1_9_R1.Block;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Blocks;
import net.minecraft.server.v1_9_R1.DamageSource;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityCreeper;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntityIronGolem;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.EntityRabbit;
import net.minecraft.server.v1_9_R1.EntitySilverfish;
import net.minecraft.server.v1_9_R1.EntityWolf;
import net.minecraft.server.v1_9_R1.GenericAttributes;
import net.minecraft.server.v1_9_R1.IMonster;
import net.minecraft.server.v1_9_R1.Item;
import net.minecraft.server.v1_9_R1.Items;
import net.minecraft.server.v1_9_R1.LocaleI18n;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.PathEntity;
import net.minecraft.server.v1_9_R1.PathfinderGoalBreed;
import net.minecraft.server.v1_9_R1.PathfinderGoalFollowOwner;
import net.minecraft.server.v1_9_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_9_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_9_R1.PathfinderGoalTempt;
import net.minecraft.server.v1_9_R1.SoundEffect;
import net.minecraft.server.v1_9_R1.SoundEffects;
import net.minecraft.server.v1_9_R1.TileEntity;
import net.minecraft.server.v1_9_R1.Vec3D;
import net.minecraft.server.v1_9_R1.World;
import net.minecraft.server.v1_9_R1.EnumParticle;
import net.minecraft.server.v1_9_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_9_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_9_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.World;


//import net.minecraft.server.v1_9_R1.EntityRabbit.PathfinderGoalEatCarrots;
//import net.minecraft.server.v1_9_R1.EntityRabbit.PathfinderGoalRabbitAvoidTarget;
//import net.minecraft.server.v1_9_R1.EntityRabbit.PathfinderGoalRabbitPanic;

import net.minecraft.server.v1_9_R1.EntityRabbit.ControllerJumpRabbit;
//import net.minecraft.server.v1_9_R1.EntityRabbit.EnumRabbitState;













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

public class F_ArrowTrap extends EntityRabbit {
	F_GolemPathfinderGoalFollowOwner pgoal = new F_GolemPathfinderGoalFollowOwner(this, 1.4D);  // set the pathfinder goal object for movement, so it can be referenced by this class later.
	public final Logger logger = Logger.getLogger("Minecraft");
	
	// display name of the owner.
    String ownerName = new String();
     
    Player owner;			// actual player object of the owner (this needs to be pointing to the correct player object on load, somehow)
    String isOwnerSet = new String(); 	// true/false flag to see if owner is set already
    String canTeleport = new String();  // set if the golem can teleport to owner when far away.
    byte activeFlag = 0;
    
    // database specific parameters. 
    F_SQLEntityDataQueries FortressDBHandler;
    long DBIdentifier;
    UUID IsRegistered;
    String OwningGroupString = "";
    String FortressAlign = "";
    String FortressArrowTrapName = "";
    HashMap<UUID, org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow> firedArrows = new HashMap<UUID, org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow>();
    
    HashMap<String, F_DBPlayerFortressPrivs> PermissionsIndex;
    
    Entity currentTarget;			// the current entity the golem is targeting.
    
    Entity custom_entity_target;	// entity of the golem's owner
    Double owner_x;          // location of owner's x/y
    Double owner_z;
    
	
	public F_ArrowTrap(net.minecraft.server.v1_9_R1.World world)
	{
		super(world);
		FortressDBHandler = new F_SQLEntityDataQueries();
        Set goalB = (Set)getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        Set goalC = (Set)getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        Set targetB = (Set)getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        Set targetC = (Set)getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
        
        fortress_root rootPlugin =  (fortress_root) world.getServer().getPluginManager().getPlugin("Fortress");
        PermissionsIndex = rootPlugin.getPlayerFortressPrivs();
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
        //this.targetSelector.a(3, new F_MinePathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 10, false, true, IMonster.e, PermissionsIndex));
        //this.targetSelector.a(3, new F_MinePathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, PermissionsIndex));
        //this.goalSelector.a(1, new F_MinePathfinderGoalMeleeAttack(this, 1.0D, true, PermissionsIndex));
        //this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true)); // 
         
        ownerName = "N/Atest";
        isOwnerSet = "N";
        canTeleport = "N";
        //Owner_Location 
        

	}
	
	// Sets the owner for this entity, and signals the custom movement pgoal to know that it is set. 
		public void setOwner(Player player)
	 	{
			this.logger.info("set Owner called...");
			this.owner = player;
		  
			// set the owner (a player object) into the pgoal
			pgoal.setMineOwner(owner, this);
		  
		  
			ownerName = player.getDisplayName();
			System.out.println("Player Display name: " + ownerName);
			this.isOwnerSet = "Y";
	 	}
	
	
	public String getOwner()
	{
		return ownerName;
	}
	
	public void resetOwner()
	{
		this.owner = null;
		this.ownerName = "N/Atest";
		this.isOwnerSet = "N";
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
		
		System.out.println("F_Mine PRE-super call" + ownerName);
	    super.b(nbttagcompound);
	    System.out.println("F_Mine POST-super call " + ownerName);
	    System.out.println("F_Mine's Player Display name: " + ownerName);
	    nbttagcompound.setString("FOwner", ownerName); // set the compound tag (test)
	    nbttagcompound.setString("OwningGroupString", OwningGroupString);
	    nbttagcompound.setByte("activeFlag", activeFlag);
	    nbttagcompound.setString("FortressAlign", FortressAlign);
	    nbttagcompound.setString("FortressArrowTrapName", FortressArrowTrapName);
	    
	    //System.out.println("F_Mine POST Set string call " + ownerName);
	    fortress_root rootPluginRet =  (fortress_root) world.getServer().getPluginManager().getPlugin("Fortress");
		HashMap<String, F_DBFortressData> DBFortRef= rootPluginRet.getFortressDataHash();
	    if (!FortressAlign.equals("N/A"))
		{
	    	if (DBFortRef.get(FortressAlign) != null)
			{
				if (DBFortRef.get(FortressAlign).getArrowTrapHash().containsKey(FortressArrowTrapName))
				{
					DBFortRef.get(FortressAlign).getArrowTrapHash().remove(FortressArrowTrapName);				// remove from the owning fortress' hashmap (entity despawned) -- only do this if its not null.
					DBFortRef.get(FortressAlign).getUUIDList().remove(this.getUniqueID());
				}
			}
		}
	    
	}
		    
    @Override
	public void a(NBTTagCompound nbttagcompound)  // NBT Load call
	{
		
		//setRabbitType(nbttagcompound.getInt("RabbitType"));
		//this.bu = nbttagcompound.getInt("MoreCarrotTicks");
		
    	super.a(nbttagcompound);
		System.out.println("F_Mine NBT Load initiated...");
		this.ownerName = nbttagcompound.getString("FOwner");
		this.DBIdentifier = nbttagcompound.getLong("UUIDMost");
		this.OwningGroupString = nbttagcompound.getString("OwningGroupString");
		this.FortressAlign = nbttagcompound.getString("FortressAlign");
		this.activeFlag = nbttagcompound.getByte("activeFlag");
		this.FortressArrowTrapName = nbttagcompound.getString("FortressArrowTrapName");
		if (activeFlag == 1)
		{
			loadGoals();
		}
		
		//System.out.println("F_Mine NBT Loaded FOwner: " + ownerName);
		//System.out.println("F_Mine NBT Loaded UUID: " + DBIdentifier);
		
		// get the golem's group string from database, to make sure they are in-sync. This should only be done on entity instantiation/construction, and should be called only
        // under certain circumstances, to reduce lag. if the golem doesn't exist in the lookup table, add it. Should only be done after NBT data is read. (this is done in lines above)
		IsRegistered = FortressDBHandler.GetRegisteredMine(this);
		
		//System.out.println("DEBUG (ARROW TRAP): '" + FortressArrowTrapName + "'");
		//System.out.println("DEBUG (ARROW TRAP): '" + FortressAlign + "'");
		
		if (!FortressAlign.equals("") && !FortressArrowTrapName.equals(""))
		{
			fortress_root rootPluginRet =  (fortress_root) world.getServer().getPluginManager().getPlugin("Fortress");
			HashMap<String, F_DBFortressData> DBFortRef= rootPluginRet.getFortressDataHash();
			if (DBFortRef.get(FortressAlign) != null)
			{
				DBFortRef.get(FortressAlign).getArrowTrapHash().put(FortressArrowTrapName, this);
				DBFortRef.get(FortressAlign).getUUIDList().put(this.getUniqueID(), this);
				System.out.println("Arrow trap successfully registered with Fortress' hashmap.");
			}
		}
		
		
		
		//System.out.println("Pre-try check " + DBIdentifier);
		try
		{
			if (IsRegistered == null)
			{
				
				FortressDBHandler.RegisterMine(this);
				System.out.println("Try call successful. (Mine)" + ownerName);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    	//ParticleEffect.FLAME.sendToPlayer(event.getPlayer(), event.getRightClicked().getLocation(), 0.00F, 0.005F, 0.00F, 0.05F, 500, true);
    	this.world.addParticle(EnumParticle.SMOKE_LARGE, this.locX + (this.random.nextDouble() - 0.5D) * this.width, this.locY + this.random.nextDouble() * this.length, this.locZ + (this.random.nextDouble() - 0.5D) * this.width, (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D, new int[0]);
    	
    	// clear any arrow that has landed, when there is no target.
	    	if (firedArrows.isEmpty() == false)
	    	{
	    		
		    	if (this.getGoalTarget() == null)
		    	{
		    		//System.out.println("arrow clearing test");
		    		clearLeftoverArrows();
		    	}
	    	}
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
    
    //@Override
    //protected String bo() 
    //{
    //	/* 209 */     return "mob.skeleton.hurt";
    //	/*     */   }
    
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
    
    
    public void setGoals()
	{
		switch (activeFlag)
		{
			case 0:
				this.targetSelector.a(2, new F_MinePathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 10, false, true, IMonster.e, PermissionsIndex));
		        this.targetSelector.a(1, new F_MinePathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, PermissionsIndex));
		        this.goalSelector.a(1, new F_MinePathfinderGoalMeleeAttack(this, 1.0D, true, PermissionsIndex));
				activeFlag = 1;
				break;
			case 1:
				
				activeFlag = 0;
				break;
		}
		
	}
    
    public void loadGoals()
	{
    	this.targetSelector.a(3, new F_MinePathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 10, false, true, IMonster.e, PermissionsIndex));
        this.targetSelector.a(3, new F_MinePathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, PermissionsIndex));
        this.goalSelector.a(1, new F_MinePathfinderGoalMeleeAttack(this, 1.0D, true, PermissionsIndex));
    	
	}
    
	public void setAlignment(String fort)
	{
		this.FortressAlign = fort;
	}
	
	public void resetAlignment()
	{
		this.FortressAlign = "";
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
			//System.out.println("IF ENTRY OUTPUT"); 
			if (!MemberOfFortressList.isEmpty())
		    {
		    	for (Iterator<String> itr = MemberOfFortressList.iterator(); itr.hasNext();)
			    {
			    	String element = itr.next();
			    	// System.out.println("Current value: " + element);
			    	if (element.equals(getAlignment()))
			    	{
			    		//System.out.println("PLAYER MATCHED TO FORTRESS ENTRY!");
			    		return true;
			    	}
			    	
			    } 
		     }  else if (MemberOfFortressList.isEmpty())
		     {
		    	 //System.out.println("WARNING: player's fortress list is empty!");
		     }
			
		}
		else {
			//System.out.println("WARNING: player's fortress list null!");
		}
		
		return false;
	}
	
	
    
	public long getDBID()
	{
		return DBIdentifier;
	}
	
	public void setFortressArrowTrapName(String name)
	{
		FortressArrowTrapName = name;
		this.setCustomName(FortressArrowTrapName);
	}
	
	public void setArrowTrapNameOnCreate(String name)
	{
		this.setCustomName(name);
		this.setCustomNameVisible(true);
	}
	
	public HashMap<UUID, org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow> getArrowHash()
	{
		return firedArrows;
	}
	
	public void clearLeftoverArrows()
	{
		if (firedArrows.isEmpty() == false)
		{
			boolean clearFlag = true;
			for(Entry<UUID, org.bukkit.craftbukkit.v1_9_R1.entity.CraftArrow> entry : firedArrows.entrySet())
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
				
				if (trgtArrowVal.isOnGround() == false)
				{
					clearFlag = false;
				}
				
			}
			if (clearFlag == true)
			{
				firedArrows.clear();
			}
		}
	}
}

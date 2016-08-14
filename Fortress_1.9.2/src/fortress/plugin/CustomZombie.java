package fortress.plugin;

import java.util.List;
import java.util.logging.Logger;

//import static fortress.utils.Utils2.getPrivateField;
import net.minecraft.server.v1_9_R1.Block;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.DamageSource;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_9_R1.World;
import net.minecraft.server.v1_9_R1.EntityZombie;
import net.minecraft.server.v1_9_R1.EnumParticle;
import net.minecraft.server.v1_9_R1.IBlockData;
import net.minecraft.server.v1_9_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_9_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_9_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_9_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_9_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_9_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_9_R1.SoundEffect;
import net.minecraft.server.v1_9_R1.SoundEffects;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.World;

import java.lang.reflect.*; 							// used for Field data type.

import org.bukkit.Effect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomZombie extends EntityZombie
{
	public CustomZombie(net.minecraft.server.v1_9_R1.World world) //You can also directly use the nms world class but this is easier if you are spawning this entity.
    {
        super(world);
        //List goalB = (List)getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        //List goalC = (List)getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        //List targetB = (List)getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        //List targetC = (List)getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();
        //this.goalSelector.a(0, new PathfinderGoalFloat(this));
        //this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0D, false));
        //this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, EntityVillager.class, 1.0D, true));
        //this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        //this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, false));
        //this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 0.0D));
        //this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        //this.goalSelector.a(8, new PathfinderGoalWalkToLoc(this, 8.0F));
        //this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        //this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
        //this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 0, true));
        //this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, 0, false));
        
        int R;
    }
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	// this override changes the zombie's sound when hurt. Changed to skeleton
	@Override
	protected SoundEffect bR()
	  {
	    return isVillager() ? SoundEffects.hz : SoundEffects.ho;
	  }
	
	// this method might be called every tick? From class LivingEntity. Appears to be so!
	// -- ticksLived < 60 means damage for 3 seconds after its created.
	@Override
	protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) 
	{
		if (isAlive()) 
		{
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
			
			//if (this.ticksLived > 120)
			//{
				 //double d1 = this.random.nextGaussian() * 0.02D;
			     //double d2 = this.random.nextGaussian() * 0.02D;
			     //double d3 = this.random.nextGaussian() * 0.02D;
				//this.world.addParticle(EnumParticle.EXPLOSION_NORMAL, this.locX + this.random.nextFloat() * this.width * 2.0F - this.width, this.locY + this.random.nextFloat() * this.length, this.locZ + this.random.nextFloat() * this.width * 2.0F - this.width, d1, d2, d3, new int[0]);
			//}
		}
		//damageEntity(DamageSource.DROWN, 0.5F);
		//this. getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation(), Effect.SMOKE, 500);
	}
	
	
}

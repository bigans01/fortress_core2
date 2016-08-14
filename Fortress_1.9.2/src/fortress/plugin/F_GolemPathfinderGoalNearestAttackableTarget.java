package fortress.plugin;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import fortress.plugin.F_GolemPathfinderGoalNearestAttackableTarget.DistanceComparator;
import net.minecraft.server.v1_9_R1.AxisAlignedBB;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityCreature;
import net.minecraft.server.v1_9_R1.EntityCreeper;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityLiving;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.EntitySkeleton;
import net.minecraft.server.v1_9_R1.EntityZombie;
import net.minecraft.server.v1_9_R1.EnumItemSlot;
import net.minecraft.server.v1_9_R1.IEntitySelector;
import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.Items;
import net.minecraft.server.v1_9_R1.PathfinderGoalTarget;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class F_GolemPathfinderGoalNearestAttackableTarget<T extends EntityLiving> extends PathfinderGoalTarget
{
	protected final Class<T> a;
	private final int g;
	protected final DistanceComparator b;
	protected Predicate<? super T> c;
	protected EntityLiving d;
	protected F_IronGolem ConvertGolem;
    
	public final Logger logger = Logger.getLogger("Minecraft");
   
	public F_GolemPathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, boolean flag)
	{
		this(entitycreature, oclass, flag, false);
	}
	
	public F_GolemPathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, boolean flag, boolean flag1) 
	{
		this(entitycreature, oclass, 10, flag, flag1, null);
	}
	
	public F_GolemPathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, int i, boolean flag, boolean flag1, final Predicate<? super T> predicate) 
	{
		super(entitycreature, flag, flag1);
		this.a = oclass;
		this.g = i;
		this.b = new DistanceComparator(entitycreature);
		a(1);
        this.ConvertGolem = (F_IronGolem)entitycreature;
		this.logger.info("GOLEM TARGETING ACTIVE...");
	    this.c = new Predicate() 
	    {
		    public boolean a(T t0) 
		    {
		        if ((predicate != null) && (!predicate.apply(t0))) 
		        {
			          return false;
			    }
		        
		        
		        if ((t0 instanceof EntityHuman)) 
		        {
		        	if (ConvertGolem.getOwner() == t0.getName())
		        	{
		        		return false;
		        	}
		        	
		        	//public final Logger loggerpred = Logger.getLogger("Minecraft");
		        	//this.loggerpred.info("GOLEM TARGETING HUMAN!!...");
		        	double d0 = F_GolemPathfinderGoalNearestAttackableTarget.this.f();
			      
		        	if (t0.isSneaking()) 
		        	{
			            d0 *= 0.800000011920929D;
			        }
	
		        	
		        	if (t0.isInvisible()) 
		        	{
		        		float f = ((EntityHuman)t0).cG();
			             
			            if (f < 0.1F) {
			               f = 0.1F;
			            }
			           
			            d0 *= 0.7F * f;
		        	}
			          
		        	if (t0.g(F_GolemPathfinderGoalNearestAttackableTarget.this.e) > d0 || (t0 instanceof F_ArrowTrap)) 
		        	{
		        		return false;
		        	}
		        }
		       
		       return F_GolemPathfinderGoalNearestAttackableTarget.this.a(t0, false);
		    }

		    public boolean apply(Object object)
		    {
		        return a((T)object); // MOST RECENT CHANGE/FIX HERE; it was expecting a T (predicate). 1/9/2016
		    }
		  };
	}
	

  public boolean a() {
     if ((this.g > 0) && (this.e.getRandom().nextInt(this.g) != 0)) {
       return false;
     }
    double d0 = f();
     List list = this.e.world.a(this.a, this.e.getBoundingBox().grow(d0, 4.0D, d0), com.google.common.base.Predicates.and(this.c, IEntitySelector.d));
    
     Collections.sort(list, this.b);
     if (list.isEmpty()) {
       return false;
    }
     this.d = ((EntityLiving)list.get(0));
     return true;
  }
   
 
   public void c()
   {
     this.e.setGoalTarget(this.d, (this.d instanceof EntityPlayer) ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
     super.c();
   }
   
    public static class DistanceComparator implements java.util.Comparator<Entity>
   {
     private final Entity a;
     
     public DistanceComparator(Entity entity) {
       this.a = entity;
     }
     
     public int a(Entity entity, Entity entity1) {
       double d0 = this.a.h(entity);
       double d1 = this.a.h(entity1);
       
      return d0 > d1 ? 1 : d0 < d1 ? -1 : 0;
     }
  
    public int compare(Entity object, Entity object1) {
      return a(object, object1);
     }
   }
}

package fortress.plugin;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Lever;

import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.World;

public class F_SensorSetLeverOnOff {
	
	private Block RedstoneDeviceTarget;
	public void F_SensorSetLeverOnOff(World world, Block block, boolean power_mode)
	{
			// STEP 1: Set lever power to true.
		  RedstoneDeviceTarget = block;
		  BlockState state = RedstoneDeviceTarget.getState();
		  Lever convertedBlock = (Lever) RedstoneDeviceTarget.getState().getData();
		  int targetBitData = (int) RedstoneDeviceTarget.getData();
		  if (power_mode && (targetBitData & 0x08) != 0x08)
		  {
			  //targetBitData |= 0x08;
			  convertedBlock.setPowered(true);
		  }
		  else if (!power_mode && (targetBitData & 0x08) == 0x08)
		  {
			  convertedBlock.setPowered(false);
		  }
		  
		  
		  //convertedBlock.setPowered(true);
		  state.setData(convertedBlock);
		  //state.setData(targetBitData);
		  state.update();
		  
		  // STEP 2: change all nearby blocks. (see documentation.)
		  
		  int iData = (int) RedstoneDeviceTarget.getData(); //retrieve byte data.
		  
		  int i1 = iData & 7;
		  Location l = RedstoneDeviceTarget.getLocation(); // get the location of the lever.
		  
		  int i = (int) l.getX();
          int j = (int) l.getY();
          int k = (int) l.getZ();
          
          if (i1 == 1)
          {
              world.applyPhysics(new BlockPosition(i-1, j, k), world.c(new BlockPosition(i-1, j, k)).getBlock());
          }
          else if (i1 == 2)
          {
        	  world.applyPhysics(new BlockPosition(i+1, j, k), world.c(new BlockPosition(i+1, j, k)).getBlock());
          }
          else if (i1 == 3)
          {
        	  world.applyPhysics(new BlockPosition(i, j, k-1), world.c(new BlockPosition(i, j, k-1)).getBlock());
          }
          else if (i1 == 4)
          {
        	  world.applyPhysics(new BlockPosition(i, j, k+1), world.c(new BlockPosition(i, j, k+1)).getBlock());
          }
          else
          {
        	  world.applyPhysics(new BlockPosition(i, j-1, k), world.c(new BlockPosition(i, j-1, k)).getBlock());
          }
          
		  
	}
}

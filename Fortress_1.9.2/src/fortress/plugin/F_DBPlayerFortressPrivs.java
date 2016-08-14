package fortress.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class F_DBPlayerFortressPrivs {
	
	List<String> MemberOfFortressList = new ArrayList<String>();
	String AlignedFortress;
	String CreateFortressString;
	boolean lastClickedSensorCheck;
	boolean changeFortHeartLocation;
	F_Sensor lastClickedSensor;
	boolean setFortressNextChest = false;
	fortress_root rootPlugin;
	
	F_DBPlayerFortressPrivs() // constructor 
	{
		lastClickedSensorCheck = false;
		rootPlugin =  (fortress_root) Bukkit.getServer().getPluginManager().getPlugin("Fortress");
	}
	
	public boolean checkIfAlliedWithEntity(UUID entityUUID, String player)
	{
		if (MemberOfFortressList != null)
		{
			if (MemberOfFortressList.isEmpty() == false)
			{
				HashMap<String, F_DBFortressData> fortressDataHashMap = rootPlugin.getFortressDataHash();
				for (int i = 0; i < MemberOfFortressList.size(); i++)
				{
					//HashMap<String, F_DBFortressData> fortressDataHashMap = rootPlugin.getFortressDataHash();
					F_DBFortressData fortDataPointer = fortressDataHashMap.get(MemberOfFortressList.get(i));
					if (fortDataPointer.fortEntityUUIDList.containsKey(entityUUID))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void setPlayerFortressMembership(List<String> inputlist)
	{
		MemberOfFortressList = inputlist; // TODO: will need to copy, not reference contents from the retrieved list 
	}
	
	public List<String> getPlayerFortressMembership()
	{
		return MemberOfFortressList;
	}
	
	public void setFortressAlignment(String fortress)
	{
		AlignedFortress = fortress;
	}
	
	public String getFortressAlignment()
	{
		return AlignedFortress;
	}
	
	public boolean willPlaceFortressHeartNextClick()
	{
		return setFortressNextChest;
	}
	
	public void setCreateFortressOnNextClick(boolean value)
	{
		setFortressNextChest = value;
	}
	
	public void setCreateFortressString(String value)
	{
		CreateFortressString = value;
	}
	
	public String getCreateFortressString()
	{
		return CreateFortressString;
	}
	
	public boolean isLastClickedSet()
	{
		return lastClickedSensorCheck;
	}
	
	public F_Sensor getLastClickedSensor()
	{
		return lastClickedSensor;
	}
	
	public void setLastClickedSensor(F_Sensor sensor)
	{
		lastClickedSensor = sensor;
	}
	
	public void setLastClickedSensorFlag(boolean value)
	{
		lastClickedSensorCheck = value;
	}
	
	public void setFortHeartLocationChangeFlag(boolean value)
	{
		changeFortHeartLocation = value;
	}
	
	public boolean getFortHeartLocationChangeFlag()
	{
		return changeFortHeartLocation;
	}
	
	public boolean checkPlayerCurrency(Player player, int amount)
	{
		Inventory playerInv = player.getInventory();
		if (playerInv.contains(Material.GOLD_INGOT, amount))
		{
			//playerInv.
			boolean stopflag = false; // tells the while loop to stop comparisons
			int startAmt = amount; // initial counter
			int oldAmt = 0;
			HashMap<Integer, ? extends ItemStack> itemHash = playerInv.all(Material.GOLD_INGOT); // iterate through all stacks
			Iterator hashIt = itemHash.entrySet().iterator();
			while (startAmt > 0)
			{
				stopflag = false;
				System.out.println("Beginning next stack check...");
				Map.Entry currentPair = (Map.Entry)hashIt.next();
				ItemStack curStack = (ItemStack)currentPair.getValue();
				if ((curStack.getAmount() - startAmt < 0) && stopflag == false) // if the cost is greater than the stack size, get rid of the stack.
				{
					System.out.println("--CHECK 1, Amount: " + curStack.getAmount());
					startAmt -= curStack.getAmount();
					playerInv.clear((Integer)currentPair.getKey());
					
					//startAmt -= curStack.getAmount();
					//System.out.println("--FIRST ITEM IF CHECK--");
					stopflag = true;
					
				}
				else if ((curStack.getAmount() >= startAmt) && stopflag == false)
				{
					System.out.println("--CHECK 2, Amount: " + curStack.getAmount());
					if (curStack.getAmount() > startAmt)
					{
						System.out.println("2 a");
						//startAmt -= curStack.getAmount();
						oldAmt = curStack.getAmount();
						curStack.setAmount(curStack.getAmount() - startAmt);
						startAmt -= oldAmt;
						System.out.println("startAmt is: " + startAmt);
						playerInv.setItem((Integer)currentPair.getKey(), curStack);
					}
					else if (curStack.getAmount() == startAmt)
					{
						System.out.println("2 b");
						startAmt -= curStack.getAmount();
						playerInv.clear((Integer)currentPair.getKey());
					}
					
					//System.out.println("--CHECK 2, Amount: " + curStack.getAmount());
					//startAmt -= curStack.getAmount();
					//System.out.println("--SECOND ITEM IF CHECK--");
					stopflag = true;
					
				}
				
				
				
				
			}
			
			return true;
		}
		return false;
	}
}


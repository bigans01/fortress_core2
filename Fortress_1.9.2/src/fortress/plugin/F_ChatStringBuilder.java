package fortress.plugin;

import org.bukkit.entity.Horse.Color;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class F_ChatStringBuilder {
	public ComponentBuilder constructErrorStringFortressAlreadyExists(String fortresstochange)
	{
		ComponentBuilder finalString = new ComponentBuilder("ERROR:");
		finalString.bold(true);
		finalString.color(ChatColor.RED);
		finalString.append(" fortress ");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		finalString.append(fortresstochange);
		finalString.bold(true);
		finalString.color(ChatColor.GREEN);
		finalString.append(" already exists. ");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		return finalString;
	}
	
	public ComponentBuilder constructStringFortressCreated(String fortresstochange)
	{
		ComponentBuilder finalString = new ComponentBuilder("Fortress ");
		finalString.color(ChatColor.GOLD);
	    finalString.append(fortresstochange);
	    finalString.bold(true);
	    finalString.color(ChatColor.GREEN);
	    finalString.append(" was successfully created. Protected region will be created around the next right-clicked wooden chest.");
	    finalString.bold(false);
	    finalString.color(ChatColor.GOLD);
	    return finalString;
	}
	
	public ComponentBuilder constructErrorStringMemberOrFortDoesntExist(String playertocheck, String fortresstochange)
	{
		ComponentBuilder finalString = new ComponentBuilder("ERROR: ");
		finalString.bold(true);
		finalString.color(ChatColor.RED);
		finalString.append(playertocheck);
		finalString.color(ChatColor.AQUA);
		finalString.append(" is not a member of ");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		finalString.append(fortresstochange);
		finalString.bold(true);
		finalString.color(ChatColor.GREEN);
		finalString.append(", or Fortress ");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		finalString.append(fortresstochange);
		finalString.bold(true);
		finalString.color(ChatColor.GREEN);
		finalString.append(" doesn't exist.");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		return finalString;
	}
	
	public ComponentBuilder constructAdminAddMemberSuccessful(String playertocheck, String fortresstochange)
	{
		ComponentBuilder finalString = new ComponentBuilder("<ADMIN>: ");
		finalString.bold(true);
		finalString.color(ChatColor.RED);
		finalString.append(playertocheck);
		//finalString.bold(true);
		finalString.color(ChatColor.AQUA);
		finalString.append(" was added to Fortress ");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		finalString.append(fortresstochange);
		finalString.bold(true);
		finalString.color(ChatColor.GREEN);
		return finalString;
	}
	
	public ComponentBuilder constructAdminRemoveMemberSuccessful(String playertocheck, String fortresstochange)
	{
		ComponentBuilder finalString = new ComponentBuilder("<ADMIN>: ");
		finalString.bold(true);
		finalString.color(ChatColor.RED);
		finalString.append(playertocheck);
		//finalString.bold(true);
		finalString.color(ChatColor.AQUA);
		finalString.append(" was removed from Fortress ");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		finalString.append(fortresstochange);
		finalString.bold(true);
		finalString.color(ChatColor.GREEN);
		return finalString;
	}
	
	public ComponentBuilder constructAdminAlignmentChangeSuccessful(String playertocheck, String fortresstochange)
	{
		ComponentBuilder finalString = new ComponentBuilder("<ADMIN>: ");
		finalString.bold(true);
		finalString.color(ChatColor.RED);
		finalString.append(playertocheck);
		//finalString.bold(true);
		finalString.color(ChatColor.AQUA);
		finalString.append(" was realigned to Fortress ");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		finalString.append(fortresstochange);
		finalString.bold(true);
		finalString.color(ChatColor.GREEN);
		return finalString;
	}
	
	public ComponentBuilder constructStringAlignmentChanged(String fortresstochange)
	{
		ComponentBuilder finalString = new ComponentBuilder("Fortress alignment has been set to: ");
		finalString.color(ChatColor.GOLD);
		finalString.append(fortresstochange);
		finalString.bold(true);
		finalString.color(ChatColor.DARK_GREEN);
		return finalString;
	}
	
	
	public ComponentBuilder constructErrorStringMemberAlreadyMember(String playertocheck, String fortresstochange)
	{
		ComponentBuilder finalString = new ComponentBuilder("ERROR: ");
		finalString.bold(true);
		finalString.color(ChatColor.RED);
		finalString.append(playertocheck);
		finalString.color(ChatColor.AQUA);
		finalString.append(" is already a member of ");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		finalString.append(fortresstochange);
		finalString.bold(true);
		finalString.color(ChatColor.GREEN);
		finalString.append(", or Fortress ");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		finalString.append(fortresstochange);
		finalString.bold(true);
		finalString.color(ChatColor.GREEN);
		finalString.append(" doesn't exist.");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		return finalString;
	}
	
	public ComponentBuilder constructSensorListHeader(String fortresstocheck)
	{
		ComponentBuilder finalString = new ComponentBuilder("Sensor list for fortress ");
		finalString.color(ChatColor.GOLD);
		finalString.append(fortresstocheck);
		finalString.bold(true);
		finalString.color(ChatColor.GREEN);
		finalString.append(": \n");
		finalString.color(ChatColor.GOLD);
		finalString.bold(false);
		return finalString;
	}
	
	public ComponentBuilder constructArrowTrapListHeader(String fortresstocheck)
	{
		ComponentBuilder finalString = new ComponentBuilder("Mine list for fortress ");
		finalString.color(ChatColor.GOLD);
		finalString.append(fortresstocheck);
		finalString.bold(true);
		finalString.color(ChatColor.GREEN);
		finalString.append(": \n");
		finalString.color(ChatColor.GOLD);
		finalString.bold(false);
		return finalString;
	}
	
	public ComponentBuilder addOnlineToConstructedSensorList(ComponentBuilder stringToAddTo, F_DataRowSensor datarow)
	{
		String underscores= new String();
		if (datarow.name.length() <= 15)
		{
			final int check = datarow.name.length();
			for (int x = 0; x < (15-check); x++)
			{
				underscores = underscores + "_";
			}
		}
		stringToAddTo.append(datarow.name);
		stringToAddTo.color(ChatColor.YELLOW);
		stringToAddTo.append(underscores);
		stringToAddTo.color(ChatColor.BLACK);
		
		if (datarow.mode != null)
		{
			stringToAddTo.append("(");
			stringToAddTo.color(ChatColor.YELLOW);
			
			switch (datarow.mode)
			{
				case "Hostile":
					stringToAddTo.append(datarow.mode);
					stringToAddTo.bold(true);
					stringToAddTo.color(ChatColor.RED);
					break;
				case "Friendly":
					stringToAddTo.append(datarow.mode);
					stringToAddTo.bold(true);
					stringToAddTo.color(ChatColor.AQUA);
					break;
				case "Neutral":
					stringToAddTo.append(datarow.mode);
					stringToAddTo.bold(true);
					stringToAddTo.color(ChatColor.WHITE);
					break;
				
			}
			
			
			stringToAddTo.append(")");
			stringToAddTo.bold(false);
			stringToAddTo.color(ChatColor.YELLOW);
		} else
		{
			stringToAddTo.append("-");
			stringToAddTo.color(ChatColor.RED);
			stringToAddTo.append(" ");
		}
		
		//stringToAddTo.color(ChatColor.WHITE);
		//stringToAddTo.append("\n");
		return stringToAddTo;
	}
	
	public ComponentBuilder addOfflineToConstructedSensorList(ComponentBuilder stringToAddTo, F_DataRowSensor datarow)
	{
		String underscores= new String();
		if (datarow.name.length() <= 15)
		{
			final int check = datarow.name.length();
			for (int x = 0; x < (15-check); x++)
			{
				underscores = underscores + "_";
			}
		}
		stringToAddTo.append(datarow.name);
		stringToAddTo.color(ChatColor.DARK_GRAY);
		stringToAddTo.append(underscores);
		stringToAddTo.color(ChatColor.BLACK);
		
		if (datarow.mode != null)
		{
			stringToAddTo.append("(");
			stringToAddTo.color(ChatColor.YELLOW);
			
			switch (datarow.mode)
			{
			case "Hostile":
				stringToAddTo.append(datarow.mode);
				stringToAddTo.bold(true);
				stringToAddTo.color(ChatColor.RED);
				break;
			case "Friendly":
				stringToAddTo.append(datarow.mode);
				stringToAddTo.bold(true);
				stringToAddTo.color(ChatColor.AQUA);
				break;
			case "Neutral":
				stringToAddTo.append(datarow.mode);
				stringToAddTo.bold(true);
				stringToAddTo.color(ChatColor.WHITE);
				break;
			}
			
			
			stringToAddTo.append(")");
			stringToAddTo.bold(false);
			stringToAddTo.color(ChatColor.YELLOW);
		} else
		{
			stringToAddTo.append("-");
			stringToAddTo.color(ChatColor.RED);
			stringToAddTo.append(" ");
		}
		
		//stringToAddTo.color(ChatColor.WHITE);
		//stringToAddTo.append("\n");
		return stringToAddTo;
	}
	
	public ComponentBuilder addOnlineToConstructedArrowTrapList(ComponentBuilder stringToAddToTwo, F_DataRowArrowTrap arrowtrap)
	{

				String underscores= new String();
				if (arrowtrap.name.length() <= 15)
				{
					final int check = arrowtrap.name.length();
					for (int x = 0; x < (15-check); x++)
					{
						underscores = underscores + "_";
					}
				}
				

				stringToAddToTwo.append(arrowtrap.name);

				System.out.println(arrowtrap.name.length());
				stringToAddToTwo.color(ChatColor.YELLOW);
				stringToAddToTwo.append(underscores);
				stringToAddToTwo.color(ChatColor.BLACK);
				
				//stringToAddToTwo.append(arrowtrap.type);
				if (arrowtrap.type != null)
				{
					stringToAddToTwo.append(arrowtrap.type);
				} else
				{
					stringToAddToTwo.append("-");
					stringToAddToTwo.color(ChatColor.RED);
					stringToAddToTwo.append(" ");
					
				}
				
				stringToAddToTwo.color(ChatColor.WHITE);
				return stringToAddToTwo;
	}
	
	public ComponentBuilder addOfflineToConstructedArrowTrapList(ComponentBuilder stringToAddToTwo, F_DataRowArrowTrap arrowtrap)
	{
		//String teststr = arrowtrap.name + "\t";
		//stringToAddToTwo.append(arrowtrap.name);
		String underscores= new String();
		if (arrowtrap.name.length() <= 15)
		{
			final int check = arrowtrap.name.length();
			for (int x = 0; x < (15-check); x++)
			{
				underscores = underscores + "_";
			}
		}
		

		stringToAddToTwo.append(arrowtrap.name);

		System.out.println(arrowtrap.name.length());
		stringToAddToTwo.color(ChatColor.GRAY);
		stringToAddToTwo.append(underscores);
		stringToAddToTwo.color(ChatColor.BLACK);
		
		if (arrowtrap.type != null)
		{
			stringToAddToTwo.append(arrowtrap.type);
		} else
		{
			stringToAddToTwo.append("-");
			stringToAddToTwo.color(ChatColor.RED);
			stringToAddToTwo.append(" ");
			
		}
		
		
		stringToAddToTwo.color(ChatColor.WHITE);
		return stringToAddToTwo;
	}

	public ComponentBuilder returnEditSensorNameSyntax()
	{
		ComponentBuilder finalString = new ComponentBuilder("Syntax for editsensormode: ");
		finalString.color(ChatColor.GOLD);
		finalString.append("<");
		finalString.append("fortress");
		finalString.color(ChatColor.GREEN);
		finalString.append("> <");
		finalString.color(ChatColor.GOLD);
		finalString.append("sensorname");
		finalString.color(ChatColor.YELLOW);
		finalString.append("> <");
		finalString.color(ChatColor.GOLD);
		
		finalString.append("F");
		finalString.color(ChatColor.AQUA);
		finalString.bold(true);
		finalString.append("|");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		
		finalString.append("H");
		finalString.color(ChatColor.RED);
		finalString.bold(true);
		finalString.append("|");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		
		finalString.append("N");
		finalString.color(ChatColor.WHITE);
		finalString.bold(true);
		finalString.append(">");
		finalString.bold(false);
		finalString.color(ChatColor.GOLD);
		
		return finalString;
	}

	public ComponentBuilder returnNonExistentSensor(String sensorname, String fortressname)
	{
		ComponentBuilder finalString = new ComponentBuilder("ERROR: ");
		finalString.color(ChatColor.RED);
		finalString.bold(true);
		finalString.append("sensor ");
		finalString.color(ChatColor.GOLD);
		finalString.bold(false);
		finalString.append(sensorname);
		finalString.color(ChatColor.YELLOW);
		finalString.bold(true);
		finalString.append(" not found in fortress ");
		finalString.color(ChatColor.GOLD);
		finalString.bold(false);
		finalString.append(fortressname);
		finalString.color(ChatColor.GREEN);
		finalString.bold(true);
		return finalString;
	}
	
	public ComponentBuilder returnSensorModeUpdateSuccess(String sensorname, String fortressname, String mode)
	{
		ComponentBuilder finalString = new ComponentBuilder("Sensor ");
		finalString.color(ChatColor.GOLD);
		finalString.append(sensorname);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" in fortress ");
		finalString.color(ChatColor.GOLD);
		finalString.append(fortressname);
		finalString.color(ChatColor.GREEN);
		finalString.append(" now has targeting mode set to ");
		finalString.color(ChatColor.GOLD);
		finalString.append(mode);
		switch (mode)
		{
			case "Friendly":
				finalString.color(ChatColor.AQUA);
				break;
			case "Hostile":
				finalString.color(ChatColor.RED);
				break;
			case "Neutral":
				finalString.color(ChatColor.WHITE);
				break;
		}
		return finalString;
	}
	
	public ComponentBuilder returnInvalidMember(String fortressname)
	{
		ComponentBuilder finalString = new ComponentBuilder("You are not a member of the currently aligned fortress, ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(fortressname);
		finalString.color(ChatColor.GREEN);
		finalString.bold(true);
		return finalString;
	}
	
	public ComponentBuilder returnAlreadyCreatedFort()
	{
		ComponentBuilder finalString = new ComponentBuilder("You have already created a fortress.");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public ComponentBuilder returnMinePlaced()
	{
		ComponentBuilder finalString = new ComponentBuilder("Mine placed successfully.");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public ComponentBuilder returnSensorPlaced()
	{
		ComponentBuilder finalString = new ComponentBuilder("Mine placed successfully.");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public ComponentBuilder returnMineNotInAlignedRegion()
	{
		ComponentBuilder finalString = new ComponentBuilder("Mines must be placed in an aligned region.");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public ComponentBuilder returnSuccessfulMovedHeart(String fortressname, int heartX, int heartY, int heartZ)
	{
		ComponentBuilder finalString = new ComponentBuilder("New fortress heart for " + fortressname + " placed at: ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		
		finalString.append("" + heartX);
		finalString.color(ChatColor.YELLOW);
		finalString.append(", ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		
		finalString.append("" + heartY);
		finalString.color(ChatColor.YELLOW);
		finalString.append(", ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		
		finalString.append("" + heartZ);
		finalString.color(ChatColor.YELLOW);
		//finalString.color(ChatColor.AQUA);
		
		
		return finalString;
		
	}
	
	public ComponentBuilder returnSensorReadyForPairing(String sensorName, String fortName)
	{
		ComponentBuilder finalString = new ComponentBuilder("Sensor ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(sensorName);
		finalString.color(ChatColor.YELLOW);
		
		finalString.append(" in Fortress ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		
		finalString.append(fortName);
		finalString.color(ChatColor.GREEN);
		
		finalString.append(" will be paired with next placed lever.");
		finalString.color(ChatColor.LIGHT_PURPLE);
		
		
		return finalString;
	}
	
	public ComponentBuilder returnSensorPairedWithLever(String sensorName, String fortName, int x, int y, int z)
	{
		ComponentBuilder finalString = new ComponentBuilder("Sensor ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(sensorName);
		finalString.color(ChatColor.YELLOW);
		
		finalString.append(" in Fortress ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		
		finalString.append(fortName);
		finalString.color(ChatColor.GREEN);
		
		finalString.append(" is now paired with the lever at: ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		
		finalString.append("" + x);
		finalString.color(ChatColor.YELLOW);
		finalString.append(", ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		
		finalString.append("" + y);
		finalString.color(ChatColor.YELLOW);
		finalString.append(", ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		
		finalString.append("" + z);
		finalString.color(ChatColor.YELLOW);
		return finalString;
	}
	
	public ComponentBuilder returnNotEnoughForMine(int amount)
	{
		ComponentBuilder finalString = new ComponentBuilder("You need ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append("" + amount);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" to purchase a mine.");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public ComponentBuilder returnNotEnoughForSensor(int amount)
	{
		ComponentBuilder finalString = new ComponentBuilder("You need ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append("" + amount);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" to purchase a sensor.");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public ComponentBuilder returnNotEnoughForFort(int amount)
	{
		ComponentBuilder finalString = new ComponentBuilder("You need ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append("" + amount);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" to create a fortress.");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public ComponentBuilder returnFortDoesNotExist(String fortname)
	{
		ComponentBuilder finalString = new ComponentBuilder("Fortress ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(fortname);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" doesn't exist.");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
		
	}
	
	public ComponentBuilder returnWorldNowPermitted(String worldname)
	{
		ComponentBuilder finalString = new ComponentBuilder("Fortresses can now be built by non-admins in ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(worldname);
		finalString.color(ChatColor.YELLOW);
		return finalString;
	}
	
	public ComponentBuilder returnWorldNowDenied(String worldname)
	{
		ComponentBuilder finalString = new ComponentBuilder("Fortresses cannot be built by non-admins in ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(worldname);
		finalString.color(ChatColor.YELLOW);
		return finalString;
	}
	
	public ComponentBuilder returnPermitListHeader()
	{
		ComponentBuilder  finalString = new ComponentBuilder("Current permitted worlds for building Fortresses: ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public ComponentBuilder returnPermitListEntry(String fortressname)
	{
		ComponentBuilder finalString = new ComponentBuilder(fortressname);
		finalString.color(ChatColor.YELLOW);
		return finalString;
	}
	
	public ComponentBuilder returnEmptyPermitList()
	{
		ComponentBuilder finalString = new ComponentBuilder("No worlds permitted to build Fortresses.");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public ComponentBuilder returnPlayerNotOnlineForFortCreate(String playername)
	{
		ComponentBuilder finalString = new ComponentBuilder(playername);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" is not in-game. (name is case sensitive!)");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public ComponentBuilder returnSensorNotFound(String sensorname, String fortressname)
	{
		ComponentBuilder finalString = new ComponentBuilder("Sensor ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(sensorname);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" was not found in Fortress ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(fortressname);
		finalString.color(ChatColor.YELLOW);
		return finalString;
	}
	
	public ComponentBuilder returnArrowNotFound(String sensorname, String fortressname)
	{
		ComponentBuilder finalString = new ComponentBuilder("Arrow trap ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(sensorname);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" was not found in Fortress ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(fortressname);
		finalString.color(ChatColor.YELLOW);
		return finalString;
	}
	
	public ComponentBuilder returnSensorNameAlreadyExists(String sensorname, String fortressname)
	{
		ComponentBuilder finalString = new ComponentBuilder("A sensor with the name ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(sensorname);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" already exists in the Fortress ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(fortressname);
		finalString.color(ChatColor.YELLOW);
		return finalString;
	}
	
	public ComponentBuilder returnArrowNameAlreadyExists(String sensorname, String fortressname)
	{
		ComponentBuilder finalString = new ComponentBuilder("An arrow trap with the name ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(sensorname);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" already exists in the Fortress ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(fortressname);
		finalString.color(ChatColor.YELLOW);
		return finalString;
	}
	
	public ComponentBuilder returnSuccessfulSensorRename(String oldname, String newname, String fortress)
	{
		ComponentBuilder finalString = new ComponentBuilder("Sensor ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(oldname);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" renamed to ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(newname);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" , in Fortress ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(fortress);
		finalString.color(ChatColor.YELLOW);
		return finalString;
		
		
	}
	
	public ComponentBuilder returnSuccessfulArrowRename(String oldname, String newname, String fortress)
	{
		ComponentBuilder finalString = new ComponentBuilder("Mine ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(oldname);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" renamed to ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(newname);
		finalString.color(ChatColor.YELLOW);
		finalString.append(" , in Fortress ");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append(fortress);
		finalString.color(ChatColor.YELLOW);
		return finalString;
	}
	
	public  ComponentBuilder returnEditSensorNameSyntaxNew()
	{
		ComponentBuilder finalString = new ComponentBuilder ("Syntax: editsensorname <");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append("fortress");
		finalString.color(ChatColor.GREEN);
		finalString.append("> <");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append("current name");
		finalString.color(ChatColor.YELLOW);
		finalString.append("> <");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append("new name");
		finalString.color(ChatColor.YELLOW);
		finalString.append(">");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public  ComponentBuilder returnEditArrowNameSyntaxNew()
	{
		ComponentBuilder finalString = new ComponentBuilder ("Syntax: editarrowname <");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append("fortress");
		finalString.color(ChatColor.GREEN);
		finalString.append("> <");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append("current name");
		finalString.color(ChatColor.YELLOW);
		finalString.append("> <");
		finalString.color(ChatColor.LIGHT_PURPLE);
		finalString.append("new name");
		finalString.color(ChatColor.YELLOW);
		finalString.append(">");
		finalString.color(ChatColor.LIGHT_PURPLE);
		return finalString;
	}
	
	public ComponentBuilder returnFortHelpIndex()
	{
		
		// header/title
		ComponentBuilder finalString = new ComponentBuilder("------------------"); //Fortress Help------------------\n");
		finalString.color(ChatColor.GREEN);
		finalString.append("Fortress Help");
		finalString.color(ChatColor.DARK_AQUA);
		finalString.append("-----------------------\n");
		finalString.color(ChatColor.GREEN);
		
		// info of header
		finalString.append("|    ");
		finalString.append("Click on a command below, or type /forthelp <command name> for descriptions on that command.  "); //| \n| on that command. Yellow commands are admin only! ");
		finalString.append("|");
		finalString.color(ChatColor.GREEN);
		finalString.append("\n|");
		finalString.color(ChatColor.WHITE);
		finalString.append(" on that command. Yellow commands are admin only!");
		finalString.color(ChatColor.GREEN);
		
		// footer
		finalString.append("-----------------------------------------------------");
		
		return finalString;
	}
	
	public TextComponent fortHelpCreatefort()
	{
		TextComponent finalText = new TextComponent("createfort   ");
		finalText.setColor(ChatColor.AQUA);
		finalText.setClickEvent( new ClickEvent (ClickEvent.Action.RUN_COMMAND, "/forthelp createfort"));
		return finalText;
	}
	
	public TextComponent fortHelpAddmember()
	{
		TextComponent finalText = new TextComponent("addmember    ");
		finalText.setColor(ChatColor.AQUA);
		finalText.setClickEvent( new ClickEvent (ClickEvent.Action.RUN_COMMAND, "/forthelp addmember"));
		return finalText;
	}
	
	public TextComponent fortHelpRemovemember()
	{
		TextComponent finalText = new TextComponent("removemember ");
		finalText.setColor(ChatColor.AQUA);
		finalText.setClickEvent( new ClickEvent (ClickEvent.Action.RUN_COMMAND, "/forthelp removemember"));
		return finalText;
	}
	
	public ComponentBuilder returnDescCreatefort()
	{
		ComponentBuilder finalString = new ComponentBuilder("--------"); //Fortress Help------------------\n");
		finalString.color(ChatColor.DARK_BLUE);
		finalString.append("createfort");
		finalString.color(ChatColor.AQUA);
		finalString.append("--------");
		finalString.color(ChatColor.DARK_BLUE);
		finalString.append("\n");
		
		finalString.append("Description: ");
		finalString.color(ChatColor.BLUE);
		finalString.append(" creates a new fort, and prepares the next clicked chest to be the fortress' heart.");
		finalString.color(ChatColor.WHITE);
		finalString.append("\n");
		
		finalString.append("Usage: ");
		finalString.color(ChatColor.BLUE);
		finalString.append(" Admins: ");
		finalString.color(ChatColor.YELLOW);
		finalString.append("/fortadmin createfort <fort name> <owner>");
		finalString.color(ChatColor.WHITE);
		finalString.append("\n");
		finalString.append("            Normal: ");
		finalString.color(ChatColor.AQUA);
		finalString.append("/fort createfort <fort name>");
		finalString.color(ChatColor.WHITE);
		
		return finalString;
		
	}
}

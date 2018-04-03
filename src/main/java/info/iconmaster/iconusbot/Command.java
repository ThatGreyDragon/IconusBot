package info.iconmaster.iconusbot;

import java.util.HashMap;
import java.util.Map;

import info.iconmaster.iconusbot.commands.*;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public abstract class Command {
	public String name;
	public String desc;
	public String longDesc;
	public boolean adminOnly = false;
	
	public Command(String name, String desc, String longDesc) {
		this.name = name;
		this.desc = desc;
		this.longDesc = longDesc;
	}
	
	public Command(String name, String desc, String longDesc, boolean adminOnly) {
		this.name = name;
		this.desc = desc;
		this.longDesc = longDesc;
		this.adminOnly = adminOnly;
	}
	
	public abstract void execute(UserData user, IChannel channel, String[] args);
	
	public static final Map<String,Command> commandRegistry = new HashMap<>();
	
	public static void register(Command cmd) {
		commandRegistry.put(cmd.name, cmd);
	}
	
	public static void registerCommands() {
		register(new CommandRegister());
		register(new CommandCritters());
		register(new CommandCritter());
		register(new CommandIncubate());
		register(new CommandHatch());
		register(new CommandName());
		register(new CommandEnergy());
		
		register(new CommandEcho());
		register(new CommandClean());
		register(new CommandReload());
		register(new CommandNewDay());
		
		register(new CommandHelp());
		register(new CommandSource());
	}
	
	public boolean refuseIfNotAdmin(UserData user, IChannel channel) {
		if (!user.isAdmin()) {
			IconusBot.INSTANCE.sendMessage(channel, user.getName()+": You do not have the permission to use this command.");
			return true;
		}
		
		return false;
	}
	
	public boolean refuseIfWrongArgCount(UserData user, IChannel channel, String[] args, int min, int max) {
		int len = args.length;
		
		if (len < min) {
			IconusBot.INSTANCE.sendMessage(channel, user.getName()+": Too few parameters supplied - Expected between "+min+" and "+max+".");
			return true;
		}
		
		if (len > max) {
			IconusBot.INSTANCE.sendMessage(channel, user.getName()+": Too many parameters supplied - Expected between "+min+" and "+max+".");
			return true;
		}
		
		return false;
	}
	
	public UserData refuseIfUserLookupFailed(IChannel channel, UserData user, String s) {
		IUser lookup = IconusBot.INSTANCE.lookupUser(channel, s);
		if (lookup == null) {
			IconusBot.INSTANCE.sendMessage(channel, user.getName()+": Sorry, I couldn't find someone going by '"+s+"'.");
			return null;
		}
		
		return IconusBot.INSTANCE.getUserData(lookup);
	}
	
	public boolean refuseIfCritterLookupFailed(IChannel channel, UserData user, Critter c, String s) {
		if (c == null) {
			IconusBot.INSTANCE.sendMessage(channel, user.getName()+": Critter name '"+s+"' unknown or ambiguous. Use `!critters` to get a list of critters.");
			return true;
		}
		
		return false;
	}
	
	public boolean refuseIfNotEnoughEnergy(IChannel channel, UserData user, int n) {
		if (n == 0) return false;
		
		if (n > user.energy) {
			IconusBot.INSTANCE.sendMessage(channel, user.getName()+": You do not have enough "+UserData.ENERGY_EMOJI+" to perform this action!\n"
					+ "It requires "+Utils.repeatEmoji(UserData.ENERGY_EMOJI, n)+", but you only have "+Utils.repeatEmoji(UserData.ENERGY_EMOJI, user.energy)+".");
			return true;
		}
		
		return false;
	}
}

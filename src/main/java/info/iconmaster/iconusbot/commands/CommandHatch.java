package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.Critter;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandHatch extends Command {

	public CommandHatch() {
		super("hatch", "Hatches a critter egg", "USAGE: !hatch *name*\n"
				+ "When an egg is ready to hatch, use this command to see what critter's inside!\n"
				+ "*name* is a part of the critter's name, or the critter's number in the `!critters` list.");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		if (refuseIfWrongArgCount(user, channel, args, 1, 1)) return;
		Critter c = user.lookupCritter(args[0]);
		if (refuseIfCritterLookupFailed(channel, user, c, args[0])) return;
		
		if (!c.isEgg) {
			IconusBot.INSTANCE.sendMessage(channel, user.getName()+": You can't hatch this critter again!");
			return;
		}
		
		if (!c.readyToHatch()) {
			IconusBot.INSTANCE.sendMessage(channel, user.getName()+": This egg isn't ready to hatch yet. Give it time, or `!incubate` it...");
			return;
		}
		
		c.isEgg = false;
		IconusBot.INSTANCE.writeUserData();
		
		IconusBot.INSTANCE.sendMessage(channel, user.getName()+": Congratulations! Your egg has hatched. Welcome to the world, "+(c.name == null ? "critter" : c.name)+"!", c.getEmbed());
	}
}

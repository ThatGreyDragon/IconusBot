package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.Critter;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandCritter extends Command {

	public CommandCritter() {
		super("critter", "Display information on a critter", "USAGE: !critter *name*\n"
				+ "Displays a critter. *name* is a part of the critter's name, or the critter's number in the `!critters` list.");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		if (refuseIfWrongArgCount(user, channel, args, 1, 1)) return;
		Critter c = user.lookupCritter(args[0]);
		if (refuseIfCritterLookupFailed(channel, user, c, args[0])) return;
		
		String usageMessage;
		if (c.isEgg) {
			usageMessage = "Use `!incubate` to speed up the hatching process.";
		} else {
			usageMessage = "Use `!play`, `!feed`, etc. to interact with them.";
		}
		
		IconusBot.INSTANCE.sendMessage(channel, user.getName()+": Here's your critter! "+usageMessage, c.getEmbed());
	}
}

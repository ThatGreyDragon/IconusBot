package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.Critter;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandName extends Command {

	public CommandName() {
		super("name", "Give a critter a name", "USAGE: !name *id* *name*\n"
				+ "Gives a name or updates the name of a critter you own.\n"
				+ "*id* is a part of the critter's old name, or the critter's number in the `!critters` list.\n"
				+ "*name* can only be one word long. The maximum name size is 32 characters.");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		if (refuseIfWrongArgCount(user, channel, args, 2, 2)) return;
		Critter c = user.lookupCritter(args[0]);
		if (refuseIfCritterLookupFailed(channel, user, c, args[0])) return;
		
		String name = args[1];
		if (name.length() > 32) {
			IconusBot.INSTANCE.sendMessage(channel, user.getName()+": Sorry, this name is too long!");
			return;
		}
		
		c.name = name;
		IconusBot.INSTANCE.writeUserData();
		
		IconusBot.INSTANCE.sendMessage(channel, user.getName()+": Your critter's name is now "+name+". Hello, "+name+"!", c.getEmbed());
	}
}

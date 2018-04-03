package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.Critter;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandIncubate extends Command {
	
	public static final int ENERGY_COST = 6;

	public CommandIncubate() {
		super("incubate", "Incubate a critter egg", "USAGE: !incubate *name*\n"
				+ "Incubates an egg, speeding up the hatching process. You can only incubate every so often.\n"
				+ "*name* is a part of the critter's name, or the critter's number in the `!critters` list.");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		if (refuseIfWrongArgCount(user, channel, args, 1, 1)) return;
		Critter c = user.lookupCritter(args[0]);
		if (refuseIfCritterLookupFailed(channel, user, c, args[0])) return;
		
		if (!c.isEgg) {
			IconusBot.INSTANCE.sendMessage(channel, user.getName()+": You can't incubate fully grown critters!");
			return;
		}
		
		if (refuseIfNotEnoughEnergy(channel, user, ENERGY_COST)) return;
		
		c.timesIncubated++;
		IconusBot.INSTANCE.writeUserData();
		
		IconusBot.INSTANCE.sendMessage(channel, user.getName()+": You give your egg some much-needed warmph, speeding up the hatcing process."+user.expendEnergy(ENERGY_COST), c.getEmbed());
	}
}

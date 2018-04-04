package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.Critter;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.ItemStack;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandUse extends Command {

	public CommandUse() {
		super("use", "Uses an item.", "USAGE: !use *item* [*critter*]\n"
				+ "Uses an item, optionally on a critter.\n"
				+ "The parameters can either be ID numbers in the lists or a part of the name itself.");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		if (refuseIfWrongArgCount(user, channel, args, 1, 2)) return;
		ItemStack i = user.lookupItem(args[0]);
		if (refuseIfItemLookupFailed(channel, user, i, args[0])) return;
		
		Critter c = null;
		if (args.length == 2) {
			c = user.lookupCritter(args[1]);
			if (refuseIfCritterLookupFailed(channel, user, c, args[1])) return;
		}
		
		if (c != null) {
			if (!i.usableOnCritter() && !i.edible()) {
				IconusBot.INSTANCE.sendMessage(channel, user.getName()+": You can't give "+i.toString()+" to your critter!");
				return;
			}
		} else {
			if (!i.usable()) {
				IconusBot.INSTANCE.sendMessage(channel, user.getName()+": You can't use "+i.toString()+"!"+(i.usableOnCritter() || i.edible() ? " But maybe a critter can..." : ""));
				return;
			}
		}
		
		i.split(1).use(channel, user, c);
	}
}

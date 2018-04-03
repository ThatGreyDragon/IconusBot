package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.Critter;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import info.iconmaster.iconusbot.Utils;
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
		
		StringBuilder sb = new StringBuilder(user.getName());
		sb.append(": Here is some info about ");
		if (c.name != null) {
			sb.append(c.name);
		} else {
			sb.append("this critter");
		}
		sb.append(":\n");
		
		if (c.name != null) {
			sb.append("**");
			sb.append(c.name);
			sb.append("**\n");
		}
		
		sb.append("\tOwner: ");
		sb.append(c.owner.getName());
		sb.append("\n");
		
		sb.append("\tSpecies: ");
		sb.append(c.getSpeciesName());
		sb.append("\n");
		
		if (!c.isEgg) {
			sb.append("\tWeight: ");
			sb.append(c.getWeight());
			sb.append(" (");
			sb.append(c.getWeightClass());
			sb.append(")\n");
			
			sb.append("\tMood: ");
			sb.append(c.getMoodIndicator());
			sb.append("\n");
			
			sb.append("**Stats**\n");
			
			sb.append("\tStrength: "); sb.append(Utils.repeatString(Critter.STRENGTH_EMOJI, c.strength)); sb.append("\n");
			sb.append("\tDexterity: "); sb.append(Utils.repeatString(Critter.DEXTERITY_EMOJI, c.dexterity)); sb.append("\n");
			sb.append("\tCharisma: "); sb.append(Utils.repeatString(Critter.CHARISMA_EMOJI, c.charisma)); sb.append("\n");
			sb.append("\tStomach: "); sb.append(Utils.repeatString(Critter.STOMACH_EMOJI, c.stomach)); sb.append("\n");
			sb.append("\tMetabolism: "); sb.append(Utils.repeatString(Critter.METABOLISM_EMOJI, c.metabolism)); sb.append("\n");
		}
		
		IconusBot.INSTANCE.sendMessage(channel, sb.toString(), c.getEmbed());
	}
}

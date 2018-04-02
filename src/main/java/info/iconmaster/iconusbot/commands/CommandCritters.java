package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.Critter;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandCritters extends Command {

	public CommandCritters() {
		super("critters", "List all your critters", "USAGE: !critters\n"
				+ "Lists all your critters. Use `!critter` to get more information on a critter.");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		if (refuseIfWrongArgCount(user, channel, args, 0, 0)) return;
		
		StringBuilder sb = new StringBuilder(user.getName());
		sb.append(": You have ");
		sb.append(user.critters.size());
		sb.append(" critters:\n\n");
		
		int i = 0;
		for (Critter c : user.critters) {
			sb.append('\t');
			sb.append(i+1);
			sb.append(". ");
			sb.append(c.name == null ? "<no name>" : c.name);
			sb.append(" : ");
			sb.append(c.getSpeciesName());
			sb.append("\n");
			i++;
		}
		
		sb.append("\nUse `!critter` to view a specific critter.");
		
		IconusBot.INSTANCE.sendMessage(channel, sb.toString());
	}
}

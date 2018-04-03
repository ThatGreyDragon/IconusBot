package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.ItemStack;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandItems extends Command {

	public CommandItems() {
		super("items", "List all your items.", "USAGE: !items\n"
				+ "Lists all the items you own.");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		if (refuseIfWrongArgCount(user, channel, args, 0, 0)) return;
		
		StringBuilder sb = new StringBuilder(user.getName());
		sb.append(": You have ");
		sb.append(user.items.size());
		sb.append(" items:\n\n");
		
		int i = 0;
		for (ItemStack item : user.items) {
			sb.append('\t');
			sb.append(i+1);
			sb.append(". ");
			sb.append(item.toString());
			sb.append(": ");
			sb.append(item.desc());
			sb.append("\n");
			i++;
		}
		
		IconusBot.INSTANCE.sendMessage(channel, sb.toString());
	}
}

package info.iconmaster.iconusbot.commands;

import java.text.DecimalFormat;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import info.iconmaster.iconusbot.Utils;
import sx.blah.discord.handle.obj.IChannel;

public class CommandEnergy extends Command {

	public CommandEnergy() {
		super("energy", "Displays how much energy you have left.", "USAGE: !energy\n"
				+ "Displays your energy and maximum energy.");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		if (refuseIfWrongArgCount(user, channel, args, 0, 0)) return;
		
		StringBuilder sb = new StringBuilder(user.getName());
		
		sb.append(": You have ");
		sb.append(Utils.repeatEmoji(UserData.ENERGY_EMOJI, user.energy));
		sb.append(".\nEvery day, you get ");
		sb.append(Utils.repeatEmoji(UserData.ENERGY_EMOJI, user.maxEnergy));
		sb.append(".\nYou're ");
		sb.append(new DecimalFormat("#.##").format(user.energy/(double)user.maxEnergy*100.0));
		sb.append("% full.");
		
		IconusBot.INSTANCE.sendMessage(channel, sb.toString());
	}
}

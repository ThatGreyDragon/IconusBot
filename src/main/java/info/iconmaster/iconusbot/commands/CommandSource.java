package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandSource extends Command {

	public CommandSource() {
		super("source", "IconusBot is open source!", "USAGE: !source");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		IconusBot.INSTANCE.sendMessage(channel, "**IconusBot is open source!** Want to look at the code? Want to contribute?\n"
				+ "Find IconusBot on GitHub at https://github.com/ThatGreyDragon/IconusBot !");
	}
}

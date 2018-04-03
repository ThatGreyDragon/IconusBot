package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandReload extends Command {

	public CommandReload() {
		super("reload", "Reloads the global save file.", "USAGE: !reload\n"
				+ "This command reloads the global save files. Useful for making manual changes without restarting the bot.", true);
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		IconusBot.INSTANCE.loadSettings();
		IconusBot.INSTANCE.readUserData();
		IconusBot.INSTANCE.sendMessage(channel, "`userdata.json` and `settings.json` successfully reloaded.");
	}
}

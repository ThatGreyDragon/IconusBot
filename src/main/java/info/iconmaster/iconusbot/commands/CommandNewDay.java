package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandNewDay extends Command {

	public CommandNewDay() {
		super("newday", "Advance the hands of time...", "USAGE: !newday\n"
				+ "Immediately causes a new day to dawn, giving everyone energy and triggering random events.", true);
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		IconusBot.INSTANCE.doNewDay();
	}
}

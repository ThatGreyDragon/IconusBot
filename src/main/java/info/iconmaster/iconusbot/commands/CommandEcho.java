package info.iconmaster.iconusbot.commands;

import java.util.Arrays;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandEcho extends Command {

	public CommandEcho() {
		super("echo", "Forces me to say something.", "USAGE: !echo [args...]\n"
				+ "This command echoes text given to it.", true);
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		IconusBot.INSTANCE.sendMessage(channel, Arrays.asList(args).stream().reduce("", (a,b)->a+" "+b));
	}
}

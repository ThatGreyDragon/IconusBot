package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandHelp extends Command {

	public CommandHelp() {
		super("help", "Prints this help message.", "USAGE: !help [topic]\n"
				+ "With 0 parameters: This command prints all the commands understood by IconusBot.\n"
				+ "With 1 parameter: This command gives a longer description of how to use a specific command.\n");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		if (refuseIfWrongArgCount(user, channel, args, 0, 1)) return;
		
		if (args.length == 0) {
			StringBuilder sb = new StringBuilder("Hello there! I'm IconusBot, beep boop. Here's a list of commands you can give me:\n\n");
			
			for (Command cmd : commandRegistry.values()) {
				if (cmd.adminOnly && !user.isAdmin()) continue;
				
				sb.append("\t`!");
				sb.append(cmd.name);
				sb.append("`: ");
				sb.append(cmd.desc);
				sb.append('\n');
			}
			
			sb.append("\nSay `!help <topic>` to get help on a specific topic.");
			
			IconusBot.INSTANCE.sendMessage(channel, sb.toString());
		} else {
			Command command = Command.commandRegistry.get(args[0]);
			
			if (command == null) {
				IconusBot.INSTANCE.sendMessage(channel, user.getName()+": I'm sorry, I don't know what !"+args[0]+" means...");
				return;
			}
			
			IconusBot.INSTANCE.sendMessage(channel, command.longDesc);
		}
	}
}

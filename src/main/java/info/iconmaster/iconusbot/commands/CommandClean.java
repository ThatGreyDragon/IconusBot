package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RateLimitException;

public class CommandClean extends Command {

	public CommandClean() {
		super("clean", "Cleans the message history.", "USAGE: !clean\nCleans the message history.", true);
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		for (IMessage message : channel.getFullMessageHistory()) {
			System.out.println("Deleting message: "+message.getContent());
			loop: do {
				try {
					message.delete();
				} catch (RateLimitException ex) {
					System.out.println("Hit rate limit.");
					try {
						Thread.sleep(ex.getRetryDelay()+100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue loop;
				}
			} while (false);
		}
		System.out.println("Done deleting all messages.");
	}
}

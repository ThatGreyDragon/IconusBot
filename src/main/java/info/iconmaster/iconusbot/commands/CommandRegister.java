package info.iconmaster.iconusbot.commands;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.Critter;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandRegister extends Command {

	public CommandRegister() {
		super("register", "Begin your critter-having journey!", "USAGE: !register\n"
				+ "If you haven't signed up yet, this command lets you obtain your very own critter egg to hatch!");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		if (refuseIfWrongArgCount(user, channel, args, 0, 0)) return;
		
		if (user.registered) {
			IconusBot.INSTANCE.sendMessage(channel, user.getName()+": You're aready playing this game. Sorry!");
			return;
		}
		
		Critter egg = new Critter();
		egg.owner = user;
		user.critters.add(egg);
		// data.registered = true;
		IconusBot.INSTANCE.writeUserData();
		
		IconusBot.INSTANCE.sendMessage(channel, user.getName()+": Wecome to Critters! Here's your egg. Raise it well.\n"
				+ "Use `!critters` to view your critters. Use `!name` to give it a name.", egg.getEmbed());
	}
}

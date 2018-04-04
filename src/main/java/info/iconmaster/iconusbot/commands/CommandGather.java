package info.iconmaster.iconusbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import info.iconmaster.iconusbot.Command;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.Item;
import info.iconmaster.iconusbot.ItemStack;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class CommandGather extends Command {
	
	public static final int ENERGY_COST = 2;

	public CommandGather() {
		super("gather", "Gather some items using your energy.", "USAGE: !gather\n"
				+ "Gather some items using your energy.");
	}
	
	@Override
	public void execute(UserData user, IChannel channel, String[] args) {
		if (refuseIfWrongArgCount(user, channel, args, 0, 0)) return;
		if (refuseIfNotEnoughEnergy(channel, user, ENERGY_COST)) return;
		
		List<ItemStack> loot = new ArrayList<>();
		Random r = new Random();
		int nLoot = 1+r.nextInt(3);
		
		for (int i = 0; i < nLoot; i++) {
			ItemStack item = new ItemStack(Item.registry.get(r.nextBoolean() ? "apple" : "pear"), 1+r.nextInt(3));
			loot.add(item);
		}
		
		StringBuilder sb = new StringBuilder(user.getName());
		
		sb.append(": You take some time to rummage around. You find the following items:\n");
		for (ItemStack item : loot) {
			sb.append("\t");
			sb.append(item.toString());
			sb.append(": ");
			sb.append(item.desc());
			sb.append("\n");
		}
		
		for (ItemStack item : loot) {
			item.giveTo(user);
		}
		
		sb.append(user.expendEnergy(ENERGY_COST));
		IconusBot.INSTANCE.sendMessage(channel, sb.toString());
	}
}

package info.iconmaster.iconusbot.items;

import info.iconmaster.iconusbot.Critter;
import info.iconmaster.iconusbot.IconusBot;
import info.iconmaster.iconusbot.Item;
import info.iconmaster.iconusbot.ItemStack;
import info.iconmaster.iconusbot.UserData;
import sx.blah.discord.handle.obj.IChannel;

public class ItemFood extends Item {
	double weight;
	
	public ItemFood(String id, String icon, String name, String desc, double weight) {
		super(id, icon, name, desc, true);
		this.weight = weight;
		this.edible = true;
	}
	
	@Override
	public void use(ItemStack stack, IChannel channel, UserData user, Critter critter) {
		String msg = user.getName()+": "+critter.getName()+" ate the "+toString(stack)+" right up!";
		
		critter.weight += weight;
		stack.reduceStackSize(1);
		
		IconusBot.INSTANCE.sendMessage(channel, msg, critter.getEmbed());
	}
}

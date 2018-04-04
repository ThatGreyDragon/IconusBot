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
		critter.weight += weight;
		stack.reduceStackSize(1);
		IconusBot.INSTANCE.sendMessage(channel, user.getName()+": "+critter.getName()+" ate it right up!", critter.getEmbed());
	}
}

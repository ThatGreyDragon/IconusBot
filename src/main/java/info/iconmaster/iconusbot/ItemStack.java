package info.iconmaster.iconusbot;

import java.util.Optional;

import org.json.JSONObject;

import sx.blah.discord.handle.obj.IChannel;

public class ItemStack {
	public Item item;
	public int stackSize;
	public UserData owner;
	
	public ItemStack(Item item) {
		this(null, item, 1);
	}
	
	public ItemStack(Item item, int stackSize) {
		this(null, item, stackSize);
	}
	
	public ItemStack(UserData owner, Item item) {
		this(owner, item, 1);
	}
	
	public ItemStack(UserData owner, Item item, int stackSize) {
		this.owner = owner;
		this.item = item;
		this.stackSize = stackSize;
	}
	
	public String icon() {
		return item.icon(this);
	}
	public String name() {
		return item.name(this);
	}
	public String desc() {
		return item.desc(this);
	}
	public boolean stackable() {
		return item.stackable(this);
	}
	public boolean usable() {
		return item.usable(this);
	}
	public boolean usableOnCritter() {
		return item.usableOnCritter(this);
	}
	public boolean edible() {
		return item.edible(this);
	}
	
	public JSONObject save() {
		return item.save(this);
	}
	
	@Override
	public String toString() {
		return item.toString(this);
	}
	
	public void use(IChannel channel, UserData user, Critter critter) {
		item.use(this, channel, user, critter);
	}
	
	public void reduceStackSize(int n) {
		stackSize -= n;
		if (stackSize <= 0 && owner != null) {
			owner.items.remove(this);
		}
		IconusBot.INSTANCE.writeUserData();
	}
	
	public void removeFromInventory() {
		if (owner == null) return;
		owner.items.remove(this);
		owner = null;
		
		IconusBot.INSTANCE.writeUserData();
	}
	
	public void giveTo(UserData user) {
		if (owner != null) {
			owner.items.remove(this);
		}
		owner = user;
		
		Optional<ItemStack> existingStack = owner.items.stream().filter(stack->stack.item == this.item).findFirst();
		if (!stackable() || !existingStack.isPresent()) {
			owner.items.add(this);
		} else {
			existingStack.get().stackSize += this.stackSize;
		}
		
		IconusBot.INSTANCE.writeUserData();
	}
}

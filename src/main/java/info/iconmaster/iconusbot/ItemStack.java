package info.iconmaster.iconusbot;

import org.json.JSONObject;

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
	
	public JSONObject save() {
		return item.save(this);
	}
	
	@Override
	public String toString() {
		return item.toString(this);
	}
}

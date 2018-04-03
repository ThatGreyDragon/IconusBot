package info.iconmaster.iconusbot;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class Item {
	public String id;
	protected String icon, name, desc;
	protected boolean stackable, usable;
	
	public Item(String id) {
		this.id = id;
	}
	
	public Item(String id, String icon, String name, String desc, boolean stackable) {
		this(id);
		this.icon = icon;
		this.name = name;
		this.desc = desc;
		this.stackable = stackable;
	}
	
	public String icon(ItemStack item) {
		return icon;
	}
	public String name(ItemStack item) {
		return name;
	}
	public String desc(ItemStack item) {
		return desc;
	}
	public boolean stackable(ItemStack item) {
		return stackable;
	}
	public boolean usable(ItemStack item) {
		return usable;
	}
	
	public String toString(ItemStack item) {
		return icon(item)+" **"+name(item)+(stackable(item) ? (" x "+item.stackSize) : "")+"**";
	}

	public JSONObject save(ItemStack item) {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("stackSize", item.stackSize);
		return json;
	}
	
	public void load(ItemStack item, JSONObject json) {
		item.stackSize = json.getInt("stackSize");
	}
	
	public static Map<String, Item> registry = new HashMap<>();
	
	public static void register(Item item) {
		registry.put(item.id, item);
	}
	
	public static ItemStack load(UserData owner, JSONObject json) {
		String id = json.getString("id");
		Item item = registry.get(id);
		ItemStack stack = new ItemStack(item);
		item.load(stack, json);
		return stack;
	}
	
	public static ItemStack load(JSONObject json) {
		return load((UserData) null, json);
	}
	
	public static void registerItems() {
		register(new Item("test", ":tools:", "Test Item", "This is a (possibly quite long) test description.", true));
	}
}

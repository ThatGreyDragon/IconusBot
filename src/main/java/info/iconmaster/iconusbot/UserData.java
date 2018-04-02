package info.iconmaster.iconusbot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import sx.blah.discord.handle.obj.IUser;

public class UserData {
	public IUser user;
	public List<Critter> critters = new ArrayList<>();
	public boolean registered;
	
	public UserData(IUser user, JSONObject json) {
		this.user = user;
		
		if (json != null) {
			registered = json.getBoolean("registered");
			
			for (Object obj : json.getJSONArray("critters")) {
				critters.add(new Critter(this, (JSONObject) obj));
			}
		}
	}
	
	public JSONObject save() {
		JSONObject json = new JSONObject();
		
		json.put("registered", registered);
		
		JSONArray crittersJson = new JSONArray();
		critters.forEach((c)->crittersJson.put(c.save()));
		json.put("critters", crittersJson);
		
		return json;
	}
	
	public Critter lookupCritter(String s) {
		try {
			int asInt = Integer.parseUnsignedInt(s);
			if (asInt >= 1 && asInt <= critters.size()) {
				return critters.get(asInt-1);
			}
		} catch (NumberFormatException ex) {
			// ignore
		}
		
		List<Critter> matches = critters.stream().filter(c->c.name != null && c.name.toLowerCase().contains(s.toLowerCase())).collect(Collectors.toList());
		if (matches.size() != 1) return null;
		
		return matches.get(0);
	}
	
	public String getName() {
		return user.getName();
	}
	
	public boolean isAdmin() {
		return IconusBot.INSTANCE.admins.contains(user.getStringID());
	}
}

package info.iconmaster.iconusbot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import sx.blah.discord.handle.obj.IUser;

public class UserData {
	public static final String ENERGY_EMOJI = ":zap:";
	public static final int DEFAULT_MAX_ENERGY = 10;
	
	public IUser user;
	public List<Critter> critters = new ArrayList<>();
	public boolean registered;
	public int energy, maxEnergy;
	
	public UserData(IUser user, JSONObject json) {
		this.user = user;
		
		if (json != null) {
			registered = json.getBoolean("registered");
			energy = json.getInt("energy");
			maxEnergy = json.getInt("maxEnergy");
			
			for (Object obj : json.getJSONArray("critters")) {
				critters.add(new Critter(this, (JSONObject) obj));
			}
		} else {
			registered = false;
			energy = maxEnergy = DEFAULT_MAX_ENERGY;
		}
	}
	
	public JSONObject save() {
		JSONObject json = new JSONObject();
		
		json.put("registered", registered);
		json.put("energy", energy);
		json.put("maxEnergy", maxEnergy);
		
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
	
	public String expendEnergy(int n) {
		if (n == 0) return "";
		energy -= n;
		IconusBot.INSTANCE.writeUserData();
		return "\nYou spent "+Utils.repeatEmoji(ENERGY_EMOJI, n)+" on this action. You have "+Utils.repeatEmoji(ENERGY_EMOJI, energy)+" remaining for today.";
	}
}

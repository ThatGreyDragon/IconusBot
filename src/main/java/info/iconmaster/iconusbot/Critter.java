package info.iconmaster.iconusbot;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sx.blah.discord.util.AttachmentPartEntry;
import sx.blah.discord.util.EmbedBuilder;

public class Critter {
	public UUID id;
	public String name;
	public boolean isEgg;
	public UserData owner;
	public LocalDateTime timeCreated;
	public Color[] pallette;
	public int timesIncubated;
	
	public Critter() {
		this.id = UUID.randomUUID();
		this.isEgg = true;
		this.timeCreated = LocalDateTime.now();
		this.pallette = new Color[] {randomColor(), randomColor(), randomColor()};
	}
	
	public Critter(UserData owner, JSONObject json) {
		this.owner = owner;
		
		id = UUID.fromString(json.getString("id"));
		isEgg = json.getBoolean("isEgg");
		timeCreated = LocalDateTime.parse(json.getString("timeCreated"));
		pallette = new Color[] {new Color(json.getJSONArray("pallette").getInt(0)), new Color(json.getJSONArray("pallette").getInt(1)), new Color(json.getJSONArray("pallette").getInt(2))};
		
		try {
			name = new String(Base64.decodeBase64(json.getString("name")), "UTF-8");
		} catch (UnsupportedEncodingException | JSONException ex) {}
		
		if (isEgg) {
			timesIncubated = json.getInt("timesIncubated");
		}
	}
	
	public Color randomColor() {
		Random r = new Random();
		return new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
	}
	
	public JSONObject save() {
		JSONObject json = new JSONObject();
		
		json.put("id", id.toString());
		json.put("isEgg", isEgg);
		json.put("timeCreated", timeCreated.toString());
		
		JSONArray colorArray = new JSONArray();
		for (Color c : pallette) {
			colorArray.put(c.getRGB());
		}
		json.put("pallette", colorArray);
		
		if (name != null) {
			try {
				json.put("name", Base64.encodeBase64String(name.getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		if (isEgg) {
			json.put("timesIncubated", timesIncubated);
		}
		
		return json;
	}
	
	public String getSpeciesName() {
		return isEgg ? "Egg" : "Dragon";
	}
	
	public static class CritterEmbed extends EmbedBuilder {
		public List<AttachmentPartEntry> files = new ArrayList<>();
	}
	
	public CritterEmbed getEmbed() {
		CritterEmbed builder = new CritterEmbed();
		
		String title = "";
		if (name != null) title = name+", ";
		if (owner != null) title += owner.user.getName()+"'s ";
		if (isEgg) {
			title += "egg";
		} else {
			title += "critter";
		}
	    builder.withAuthorName(title);
	    
	    if (isEgg) {
	    	String eggStatus;
	    	double percent = getPercentHatched();
	    	if (percent < .2) {
	    		eggStatus = "It lies dormant and growing...";
	    	} else if (percent < .5) {
	    		eggStatus = "It feels warm to the touch.";
	    	} else if (percent < .8) {
	    		eggStatus = "It shakes softly in your hands.";
	    	} else if (percent < 1.0) {
	    		eggStatus = "You can see cracks appear in the shell...";
	    	} else {
	    		eggStatus = "It's ready to hatch! Use `!hatch` to welcome your new critter into the world.";
	    	}
	    	
	    	builder.withDesc("This is a critter egg. "+eggStatus);
	    } else {
	    	builder.withDesc("This is a critter.");
	    }
	    
	    builder.appendField("Born On", timeCreated.format(DateTimeFormatter.ofPattern("hh:mm, dd MMM uuuu")), true);
	    builder.appendField("Species", getSpeciesName(), true);
	    
	    try {
	    	BufferedImage image = CritterImage.getCritterImage(this);
	    	
	    	ByteArrayOutputStream os = new ByteArrayOutputStream();
	    	ImageIO.write(image, "png", os);
	    	
			builder.files.add(new AttachmentPartEntry("critter.png", new ByteArrayInputStream(os.toByteArray())));
			builder.withImage("attachment://critter.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return builder;
	}
	
	public double getPercentHatched() {
		Random r = new Random(id.hashCode());
		
		LocalDateTime timeHatches = timeCreated.plusHours(12+r.nextInt(12));
		
		Duration max = Duration.between(timeCreated, timeHatches);
		Duration elapsed = Duration.between(timeCreated, LocalDateTime.now().plusHours(timesIncubated*4));
		return (double)elapsed.toMinutes()/(double)max.toMinutes();
	}
	
	public boolean readyToHatch() {
		return getPercentHatched() >= 1.0;
	}
}

package info.iconmaster.iconusbot;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
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
	public static final int MAX_MOOD = 100;
	
	public static final String STRENGTH_EMOJI = ":muscle:";
	public static final String DEXTERITY_EMOJI = ":eye:";
	public static final String CHARISMA_EMOJI = ":sparkles:";
	public static final String STOMACH_EMOJI = ":meat_on_bone:";
	public static final String METABOLISM_EMOJI = ":fire:";
	
	public UUID id;
	public String name;
	public boolean isEgg;
	public UserData owner;
	public LocalDateTime timeCreated;
	public Color[] pallette;
	public int timesIncubated;
	public double weight;
	public int mood;
	
	public int strength, dexterity, charisma, stomach, metabolism;
	
	public Critter() {
		this.id = UUID.randomUUID();
		this.isEgg = true;
		this.timeCreated = LocalDateTime.now();
		this.pallette = new Color[] {randomColor(), randomColor(), randomColor()};
		this.mood = MAX_MOOD;
		
		Random r = new Random();
		this.weight = 100.0 + r.nextDouble()*40;
		
		this.strength = 1+r.nextInt(5);
		this.dexterity = 1+r.nextInt(5);
		this.charisma = 1+r.nextInt(5);
		this.stomach = 1+r.nextInt(5);
		this.metabolism = 1+r.nextInt(5);
	}
	
	public Critter(UserData owner, JSONObject json) {
		this.owner = owner;
		
		id = UUID.fromString(json.getString("id"));
		isEgg = json.getBoolean("isEgg");
		timeCreated = LocalDateTime.parse(json.getString("timeCreated"));
		pallette = new Color[] {new Color(json.getJSONArray("pallette").getInt(0)), new Color(json.getJSONArray("pallette").getInt(1)), new Color(json.getJSONArray("pallette").getInt(2))};
		weight = json.getDouble("weight");
		mood = json.getInt("mood");
		
		strength = json.getInt("strength");
		dexterity = json.getInt("dexterity");
		charisma = json.getInt("charisma");
		stomach = json.getInt("stomach");
		metabolism = json.getInt("metabolism");
		
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
		json.put("weight", weight);
		json.put("mood", mood);
		
		json.put("strength", strength);
		json.put("dexterity", dexterity);
		json.put("charisma", charisma);
		json.put("stomach", stomach);
		json.put("metabolism", metabolism);
		
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
	
	public String getWeight() {
		return new DecimalFormat("#.#").format(weight)+" lbs";
	}
	
	public String getWeightClass() {
		if (weight < 100.0) {
			return "Emaciated";
		} else if (weight < 200.0) {
			return "Lean";
		} else if (weight < 300.0) {
			return "Chubby";
		} else if (weight < 400.0) {
			return "Fat";
		} else if (weight < 500.0) {
			return "Morbidly Obese";
		} else {
			return "Blob";
		}
	}
	
	public String getImageSuffix() {
		if (weight < 200.0) {
			return "1";
		} else if (weight < 400.0) {
			return "2";
		} else {
			return "3";
		}
	}
	
	public String getMoodIndicator() {
		if (mood < 20) {
			return ":angry:";
		} else if (mood < 40) {
			return ":frowning:";
		} else if (mood < 60) {
			return ":neutral_face:";
		} else if (mood < 80) {
			return ":smiley:";
		} else {
			return ":smile:";
		}
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
	    
	    if (!isEgg) {
	    	builder.appendField("Weight", getWeight()+" ("+getWeightClass()+")", true);
	    	builder.appendField("Mood", getMoodIndicator(), true);
	    }
	    
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
	
	public String getName() {
		return name == null ? "the critter" : name;
	}
}

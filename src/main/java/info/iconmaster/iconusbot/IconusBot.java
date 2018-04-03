package info.iconmaster.iconusbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import info.iconmaster.iconusbot.Critter.CritterEmbed;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.AttachmentPartEntry;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class IconusBot {
	// constants
	public static IconusBot INSTANCE;
	
	// main function
	public static void main(String[] args) {
		Command.registerCommands();
		
		System.out.println("Connecting...");
		
		try {
			INSTANCE = new IconusBot(new String(Files.readAllBytes(Paths.get("token.txt"))));
			
			IUser user = INSTANCE.client.getOurUser();
			System.out.println("Connected as " + user.getName() + "#" + user.getDiscriminator() + " - (" + user.getStringID() + ")");
		} catch (IOException e) {
			System.err.println("Could not start IconusBot because token.txt is missing or inaccessible:");
			e.printStackTrace();
			return;
		}
		
		System.out.println("Loading admin list...");
		
		try {
			INSTANCE.admins = new HashSet<>();
			for (String line : Files.readAllLines(Paths.get("admins.txt"))) {
				INSTANCE.admins.add(line);
			}
		} catch (IOException e) {
			System.err.println("admins.txt is missing or inaccessible, using default admin list:");
			e.printStackTrace();
			
			INSTANCE.admins = new HashSet<String>() {{
				add(INSTANCE.client.getOurUser().getStringID());
			}};
		}
		
		System.out.println("System fully loaded.");
	}
	
	// instance stuff
	public IDiscordClient client;
	public Set<String> admins;
	public Map<String, UserData> userdata;
	public JSONObject settings;
	public Timer dailyTimer;
	
	public IconusBot(String token) {
		// connect to Discord
		this.client = new ClientBuilder().withToken(token).build();
		client.getDispatcher().registerListener(new Object() {
			@EventSubscriber
			public void onMessageReceived(MessageReceivedEvent event) {
				String content = event.getMessage().getContent();
				if (content.startsWith("!")) {
					try {
						String[] args = content.substring(1).split(" ");
						doCommand(event.getAuthor(), event.getChannel(), args);
					} catch (Throwable e) {
						System.err.println("An error occured in parsing a message: ");
						e.printStackTrace();
						
						try {
							sendMessage(event.getChannel(), "Sorry, an internal error occured. Try again?");
						} catch (Throwable e2) {
							System.err.println("An error occured in handling an error that occured while parsing a message: ");
							e2.printStackTrace();
						}
					}
				}
			}
		});
		client.login();
		while (!client.isReady()) {}
		
		// load settings
		loadSettings();
		readUserData();
		
		// setup daily events
		dailyTimer = new Timer("IconusBotDailyTimer", true);
		dailyTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				LocalTime rollover = rolloverTime();
				LocalTime now = LocalTime.now();
				if (now.getHour() == rollover.getHour() && now.getMinute() == rollover.getMinute()) {
					doNewDay();
				}
			}
		}, 0, 1000*60);
	}
	
	public IMessage sendMessage(IChannel channel, String message) {
		return RequestBuffer.request(() -> {
			try {
				return channel.sendMessage(message);
			} catch (DiscordException e) {
				System.err.println("Message could not be sent with error: ");
				e.printStackTrace();
				
				try {
					return channel.sendMessage("An internal error occured. Try again, perhaps?");
				} catch (DiscordException e2) {
					// do nothing; We Tried
				}
				
				return null;
			}
		}).get();
	}
	
	public IMessage sendMessage(IChannel channel, String message, EmbedObject embed, AttachmentPartEntry... files) {
		return RequestBuffer.request(() -> {
			try {
				return channel.sendFiles(message, false, embed, files);
			} catch (DiscordException e) {
				System.err.println("Message could not be sent with error: ");
				e.printStackTrace();
				
				try {
					return channel.sendMessage("An internal error occured. Try again, perhaps?");
				} catch (DiscordException e2) {
					// do nothing; We Tried
				}
				
				return null;
			}
		}).get();
	}
	
	public IMessage sendMessage(IChannel channel, String message, CritterEmbed embed) {
		return sendMessage(channel, message, embed.build(), embed.files.toArray(new AttachmentPartEntry[embed.files.size()]));
	}
	
	public void readUserData() {
		userdata = new HashMap<>();
		File file = new File("userdata.json");
		
		try {
			if (!file.exists()) {
				file.createNewFile();
				PrintStream ps = new PrintStream(file);
				ps.print("{}");
				ps.close();
				
				System.out.println("Userdata did not exist; created new file at "+file.getCanonicalPath()+".");
				return;
			}
			
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			
			JSONObject map = new JSONObject(new String(data));
			
			for (String key : map.toMap().keySet()) {
				JSONObject usermap = null;
				try {
					usermap = map.getJSONObject(key);
				} catch (JSONException ex) {
					// do nothing
				}
				
				userdata.put(key, new UserData(client.fetchUser(Long.parseUnsignedLong(key)), usermap));
			}
			
			System.out.println("Successfully read userdata at "+file.getCanonicalPath()+".");
		} catch (Throwable e) {
			try {
				System.out.println("Failed to read userdata at "+file.getCanonicalPath()+":");
				e.printStackTrace();
			} catch (IOException e2) {
				System.out.println("Failed to read userdata, unknown path:");
				e.printStackTrace();
			}
		}
	}
	
	public void writeUserData() {
		JSONObject jsonMap = new JSONObject();
		
		for (UserData info : userdata.values()) {
			JSONObject jsonUser = info.save();
			
			jsonMap.put(info.user.getStringID(), jsonUser);
		}
		
		try {
			FileWriter fw = new FileWriter(new File("userdata.json"));
			jsonMap.write(fw, 4, 0);
			fw.close();
			
			System.out.println("Userdata saved successfully.");
		} catch (Throwable e) {
			System.out.println("Failed to write userdata:");
			e.printStackTrace();
		}
	}
	
	public UserData getUserData(IUser user) {
		if (!userdata.containsKey(user.getStringID())) {
			UserData newData = new UserData(user, null);
			userdata.put(user.getStringID(), newData);
			return newData;
		}
		
		return userdata.get(user.getStringID());
	}
	
	public IUser lookupUser(IChannel channel, String s) {
		try {
			IUser user = client.fetchUser(Long.parseUnsignedLong(s));
			if (user != null) {
				return user;
			}
		} catch (NumberFormatException e) {
			// ignore
		}
		
		for (IUser member : channel.getUsersHere()) {
			if (member.getName().toLowerCase().contains(s.toLowerCase())) {
				return member;
			}
			
			String nick = member.getNicknameForGuild(channel.getGuild());
			if (nick != null && nick.toLowerCase().contains(s.toLowerCase())) {
				return member;
			}
		}
		
		return null;
	}
	
	public void doCommand(IUser user, IChannel channel, String[] args) {
		Command command = Command.commandRegistry.get(args[0]);
		
		if (command == null) {
			sendMessage(channel, user.getName()+": I'm sorry, I don't know what !"+args[0]+" means... Try !help if you're stuck?");
			return;
		}
		
		UserData userdata = getUserData(user);
		
		if (command.adminOnly && !userdata.isAdmin()) {
			sendMessage(channel, user.getName()+": You do not have the permission to use this command.");
			return;
		}
		
		command.execute(userdata, channel, Arrays.copyOfRange(args, 1, args.length));
	}
	
	public void doNewDay() {
		for (UserData user : userdata.values()) {
			user.energy = user.maxEnergy;
		}
		writeUserData();
		
		for (IChannel channel : announcmentChannels()) {
			doNewDay(channel);
		}
	}
	
	public void doNewDay(IChannel channel) {
		sendMessage(channel, "**It's a brand new day!**\nEveryone's "+UserData.ENERGY_EMOJI+" has been restored. Carpe diem!");
	}
	
	public void loadSettings() {
		File settingsFile = new File("settings.json");
		if (!settingsFile.exists()) {
			try {
				System.out.println("settings.json not found. Copying it over from defaults...");
				Files.copy(new File("settings.default.json").toPath(), settingsFile.toPath());
			} catch (IOException e) {
				System.err.println("An error occured while copying settings.default.json:");
				e.printStackTrace();
				return;
			}
		}
		
		try {
			FileInputStream fis = new FileInputStream(settingsFile);
			byte[] data = new byte[(int) settingsFile.length()];
			fis.read(data);
			fis.close();
			settings = new JSONObject(new String(data));
		} catch (IOException e) {
			System.err.println("An error occured while reading settings.json:");
			e.printStackTrace();
			return;
		}
		
		System.out.println("Successfuly read settings.json.");
	}
	
	public Set<IChannel> announcmentChannels() {
		Set<IChannel> set = new HashSet<>();
		
		for (Object item : settings.getJSONArray("announcmentChannels")) {
			if (item instanceof String) {
				IChannel c = client.getChannelByID(Long.parseUnsignedLong((String) item));
				if (c == null) {
					throw new IllegalArgumentException("Nonexistent channel in announcmentChannels: "+item);
				}
				set.add(c);
			} else {
				throw new IllegalArgumentException("Unexpected item in announcmentChannels of "+item.getClass());
			}
		}
		
		return set;
	}
	
	public LocalTime rolloverTime() {
		String s = settings.getString("rolloverTime");
		return LocalTime.parse(s);
	}
}

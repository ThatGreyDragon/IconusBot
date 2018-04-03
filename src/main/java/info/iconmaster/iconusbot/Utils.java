package info.iconmaster.iconusbot;

public class Utils {
	private Utils() {}
	
	public static String repeatString(String s, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	public static String repeatEmoji(String s, int n) {
		if (n > 8) {
			return n+" "+s;
		} else {
			return repeatString(s, n);
		}
	}
}

package info.iconmaster.iconusbot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CritterImage {
	public static final int IMAGE_SIZE = 128;
	public static final Color[] PALLETTE = new Color[] {new Color(64, 64, 64), new Color(128, 128, 128), new Color(192, 192, 192)};
	
	public static BufferedImage getCritterImage(Critter c) {
		try {
			BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			
			BufferedImage baseImage = ImageIO.read(new File(c.isEgg ? "assets/egg.png" : "assets/dragon"+c.getImageSuffix()+".png"));
			
			for (int x = 0; x < baseImage.getWidth(); x++) {
				for (int y = 0; y < baseImage.getHeight(); y++) {
					int pixel = baseImage.getRGB(x, y);
					for (int i = 0; i < PALLETTE.length; i++) {
						if (pixel == PALLETTE[i].getRGB()) {
							baseImage.setRGB(x, y, c.pallette[i].getRGB());
						}
					}
				}
			}
			
			g.drawImage(baseImage, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
			
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}

package org.contentmine.image.pixel;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.contentmine.image.ImageUtil;

public class ImageFloodFill extends FloodFill {

	private BufferedImage image;
	private int threshold;
	
	public ImageFloodFill(BufferedImage image, int threshold) {
		super(image.getWidth(), image.getHeight());
		this.image = image;
		this.threshold = threshold;
	}
	
	public ImageFloodFill(BufferedImage image) {
		super(image.getWidth(), image.getHeight());
		this.image = image;
		this.threshold = 128;
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("ERROR: Pass filename as argument.");
			return;
		}
		String filename = args[0];
		BufferedImage image = ImageUtil.readImage(new File(filename));
		FloodFill floodFill = new ImageFloodFill(image);
		floodFill.fillIslands();
	}
	
	@Override
	protected boolean isBlack(int posX, int posY) {
		if (image != null) {
			int color = image.getRGB(posX, posY);
			int brightness = (color & 0xFF) + ((color >> 2) & 0xFF) + ((color >> 4) & 0xFF);
			brightness /= 3;
			return brightness < threshold;
		} else {
			return false;
		}
	}
	
}
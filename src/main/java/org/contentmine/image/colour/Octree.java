package org.contentmine.image.colour;

import java.awt.image.BufferedImage;

import org.contentmine.image.ImageUtil;
import org.contentmine.image.colour.jhlabs.QuantizeFilter;

public class Octree {

	private int[] inPixels;
	private int[] outPixels;
	private int numColors = 0;
	private boolean dither = false;
	private boolean serpentine = false;
	private int width;
	private int height;
	private BufferedImage inImage;
	private QuantizeFilter quantizeFilter;
	private BufferedImage outImage;
	

	public Octree readImage(BufferedImage inImage) {
		this.inImage = inImage;
		inPixels = ImageUtil.getPixels(inImage);
		outPixels = new int[inPixels.length];
		this.width = inImage.getWidth();
		this.height = inImage.getHeight();
		return this;
	}

	public Octree setColourCount(int numColors) {
		this.numColors = numColors;
		return this;
	}

	public Octree setDither(boolean b) {
		this.dither = b;
		return this;
	}

	public Octree setSerpentine(boolean b) {
		this.serpentine = b;
		return this;
	}

	public Octree quantize() {
		quantizeFilter = new QuantizeFilter();
		quantizeFilter.quantize(inPixels, outPixels, width, height, numColors, dither, serpentine);
		outImage = ImageUtil.create(inImage.getType(), outPixels, width, height);
		return this;
	}

	public BufferedImage getOutImage() {
		return outImage;
	}

}

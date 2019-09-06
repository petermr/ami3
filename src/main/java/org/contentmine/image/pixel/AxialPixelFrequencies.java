package org.contentmine.image.pixel;

import java.awt.image.BufferedImage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.RealArray;

/** holds frequencies of pixels by image coordinate.
 * 
 * @author pm286
 *
 */
public class AxialPixelFrequencies {
    

	private static final Logger LOG = Logger.getLogger(AxialPixelFrequencies.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private MainPixelProcessor mainPixelProcessor;
	private IntArray xFrequencies;
	private IntArray yFrequencies;

	public AxialPixelFrequencies(MainPixelProcessor mainPixelProcessor) {
		this.mainPixelProcessor = mainPixelProcessor;
	}
	
	public void calculateAxialPixelFrequenciesFromImage() {
		calculateXAxialPixelFrequenciesFromImage();
		calculateYAxialPixelFrequenciesFromImage();
	}
	
	private void calculateXAxialPixelFrequenciesFromImage() {
		BufferedImage image = mainPixelProcessor.getImage();
		if (image != null && mainPixelProcessor.getBinarize()) {
			xFrequencies = new IntArray(image.getWidth());
			for (int i = 0; i < image.getWidth(); i++) {
				int totalBlack = 0;
				for (int j = 0; j < image.getHeight(); j++) {
					if ((image.getRGB(i, j) & 0x00ffffff) == 0) {
						totalBlack++;
					}
				}
				xFrequencies.setElementAt(i, totalBlack);
			}
		}
	}
	private void calculateYAxialPixelFrequenciesFromImage() {
		BufferedImage image = mainPixelProcessor.getImage();
		if (image != null && mainPixelProcessor.getBinarize()) {
			yFrequencies = new IntArray(image.getHeight());
			for (int i = 0; i < image.getHeight(); i++) {
				int totalBlack = 0;
				for (int j = 0; j < image.getWidth(); j++) {
					if ((image.getRGB(j, i) & 0x00ffffff) == 0) {
						totalBlack++;
					}
				}
				yFrequencies.setElementAt(i, totalBlack);
			}
		}
	}

	public String toString() {
		String s = "";
		s += xFrequencies.toString()+"\n";
		s += yFrequencies.toString()+"\n";
		return s;
	}

	public IntArray getXFrequencies() {
		return xFrequencies;
	}

	public IntArray getYFrequencies() {
		return yFrequencies;
	}

}

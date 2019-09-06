package org.contentmine.image.processing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageUtil;
 
/**
 * Image histogram equalization
 * 
References:

    R. Crane, A Simplified Approach To Image Processing, United States of America: Prentice Hall PTR, 1997, page 47-49 [↩]
    Histogram equalization in theory: http://fourier.eng.hmc.edu/e161/lectures/contrast_transform/node2.html [↩]
 *
 * Author: Bostjan Cigan (http://zerocool.is-a-geek.net)
 *
 * tweaks by pm286 to remove static and change read/write
 */
 
public class HistogramEqualization {

	private static final Logger LOG = Logger.getLogger(HistogramEqualization.class);

    private BufferedImage originalImage, equalizedImage;

	public HistogramEqualization() {
		
	}
	
	public HistogramEqualization(BufferedImage image) {
		setImage(image);
	}
	
    /**
     * 
     * @param inputFile (system knows type)
     * @throws IOException
     */
	public void readImage(File inputFile) throws IOException {
		LOG.trace(inputFile);
		if (inputFile == null || !inputFile.exists() || inputFile.isDirectory() || inputFile.isHidden()) {
			throw new IOException("cannot read file (does not exist or is not readable): "+inputFile);
		}
		this.originalImage = ImageUtil.readImage(inputFile);
	}
	
	public void setImage(BufferedImage image) {
		this.originalImage = image;
	}
 
	public BufferedImage histogramEqualization() {
 
        int red;
        int green;
        int blue;
        int alpha;
        int newPixel = 0;
 
        // Get the Lookup table for histogram equalization
        ArrayList<int[]> histLUT = histogramEqualizationLUT(originalImage);
 
        equalizedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
 
        for(int i=0; i<originalImage.getWidth(); i++) {
            for(int j=0; j<originalImage.getHeight(); j++) {
 
                // Get pixels by R, G, B
                alpha = new Color(originalImage.getRGB (i, j)).getAlpha();
                red = new Color(originalImage.getRGB (i, j)).getRed();
                green = new Color(originalImage.getRGB (i, j)).getGreen();
                blue = new Color(originalImage.getRGB (i, j)).getBlue();
 
                // Set new pixel values using the histogram lookup table
                red = histLUT.get(0)[red];
                green = histLUT.get(1)[green];
                blue = histLUT.get(2)[blue];
 
                // Return back to original format
                newPixel = colorToRGB(alpha, red, green, blue);
 
                // Write pixels into image
                equalizedImage.setRGB(i, j, newPixel);
 
            }
        }
 
        return equalizedImage;
 
    }
 
    // Get the histogram equalization lookup table for separate R, G, B channels
    private ArrayList<int[]> histogramEqualizationLUT(BufferedImage input) {
 
        // Get an image histogram - calculated values by R, G, B channels
        ArrayList<int[]> imageHist = imageHistogram(input);
 
        // Create the lookup table
        ArrayList<int[]> imageLUT = new ArrayList<int[]>();
 
        // Fill the lookup table
        int[] rhistogram = new int[256];
        int[] ghistogram = new int[256];
        int[] bhistogram = new int[256];
 
        for(int i=0; i<rhistogram.length; i++) rhistogram[i] = 0;
        for(int i=0; i<ghistogram.length; i++) ghistogram[i] = 0;
        for(int i=0; i<bhistogram.length; i++) bhistogram[i] = 0;
 
        long sumr = 0;
        long sumg = 0;
        long sumb = 0;
 
        // Calculate the scale factor
        float scale_factor = (float) (255.0 / (input.getWidth() * input.getHeight()));
 
        for(int i=0; i<rhistogram.length; i++) {
            sumr += imageHist.get(0)[i];
            int valr = (int) (sumr * scale_factor);
            if(valr > 255) {
                rhistogram[i] = 255;
            }
            else rhistogram[i] = valr;
 
            sumg += imageHist.get(1)[i];
            int valg = (int) (sumg * scale_factor);
            if(valg > 255) {
                ghistogram[i] = 255;
            }
            else ghistogram[i] = valg;
 
            sumb += imageHist.get(2)[i];
            int valb = (int) (sumb * scale_factor);
            if(valb > 255) {
                bhistogram[i] = 255;
            }
            else bhistogram[i] = valb;
        }
 
        imageLUT.add(rhistogram);
        imageLUT.add(ghistogram);
        imageLUT.add(bhistogram);
 
        return imageLUT;
 
    }
 
    /** convert image to RGB channels
     * 
     * @param input image
     * @return histogram values for separate R, G, B channels
     */
    public static ArrayList<int[]> imageHistogram(BufferedImage input) {
 
        int[] rhistogram = new int[256];
        int[] ghistogram = new int[256];
        int[] bhistogram = new int[256];
 
        for(int i=0; i<rhistogram.length; i++) rhistogram[i] = 0;
        for(int i=0; i<ghistogram.length; i++) ghistogram[i] = 0;
        for(int i=0; i<bhistogram.length; i++) bhistogram[i] = 0;
 
        for(int i=0; i<input.getWidth(); i++) {
            for(int j=0; j<input.getHeight(); j++) {
 
                int red = new Color(input.getRGB (i, j)).getRed();
                int green = new Color(input.getRGB (i, j)).getGreen();
                int blue = new Color(input.getRGB (i, j)).getBlue();
 
                // Increase the values of colors
                rhistogram[red]++; ghistogram[green]++; bhistogram[blue]++;
 
            }
        }
 
        ArrayList<int[]> hist = new ArrayList<int[]>();
        hist.add(rhistogram);
        hist.add(ghistogram);
        hist.add(bhistogram);
 
        return hist;
 
    }
 
    /** Convert R, G, B, Alpha to standard 8 bit
     * 
     * @param alpha
     * @param red
     * @param green
     * @param blue
     * @return standard 8 bit
     */
    public static int colorToRGB(int alpha, int red, int green, int blue) {
 
        int newPixel = 0;
        newPixel += alpha; newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;
 
        return newPixel;
 
    }
 
    /** run from commandline
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	 
    	HistogramEqualization histogramEQ = new HistogramEqualization();
        File infileName = new File(args[0]);
        String outfileName = args[1];
        histogramEQ.readImage(infileName);
        histogramEQ.histogramEqualization();
        ImageIOUtil.writeImageQuietly(histogramEQ.equalizedImage, outfileName);
    }

	public BufferedImage getEqualized() {
		return equalizedImage;
	}

}

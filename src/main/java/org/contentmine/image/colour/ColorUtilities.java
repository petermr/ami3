package org.contentmine.image.colour;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.apache.log4j.Logger;

/** not yet working
 * 
 * @author pm286
 *
 */
public class ColorUtilities {
	private final static Logger LOG = Logger.getLogger(ColorUtilities.class);


	public static final int ARGB_WHITE = 0xffffffff;
    public static final int RGB_WHITE = 255 + 255*256 + 255*256*256;
	public static final int RGB_BLACK = 0;
	
	static final public double Y0 = 100;
	static final public double gamma = 3;
	static final public double Al = 1.4456;
	static final public double Ach_inc = 0.16;

	private BufferedImage colorFrame;
    private int width;
    private int height;
    
    private BufferedImage grayFrame = 
        new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    
	public void filter1() {
	   BufferedImageOp grayscaleConv = 
	      new ColorConvertOp(colorFrame.getColorModel().getColorSpace(), 
	                         grayFrame.getColorModel().getColorSpace(), null);
	   grayscaleConv.filter(colorFrame, grayFrame);
	}
	
	// OR
	protected void filter() {       
        WritableRaster raster = grayFrame.getRaster();

        for(int x = 0; x < raster.getWidth(); x++) {
            for(int y = 0; y < raster.getHeight(); y++){
                int argb = colorFrame.getRGB(x,y);
                int r = getRed(argb);
                int g = getGreen(argb);
                int b = getBlue(argb);

                int l = (int) (.299 * r + .587 * g + .114 * b);
                raster.setSample(x, y, 0, l);
            }
        }
    }

	/** gets green channnel only.
	 * masks out alpha
	 * @param argb
	 * @return green
	 */

	/** gets red channnel only.
	 * masks out alpha
	 * @param argb
	 * @return red
	 */
	public static int getRed(int argb) {
		return (argb >> 16) & 0xff;
	}
	
	/** sets red channnel only.
	 * @param argb
	 * 
	 * @return red
	 */
	public static int setRed(int argb, int red) {
		red = red << 16;
		argb = argb & 0xff00ffff;
		argb += red;
		return argb;
	}
	
	public static int getGreen(int argb) {
		return (argb >>  8) & 0xff;
	}

	/** sets green channnel only.
	 * @param argb
	 * 
	 * @return green 0-255
	 */
	public static int setGreen(int argb, int green) {
		green = green << 8;
		argb = argb & 0xffff00ff;
		argb += green;
		return argb;
	}
	
	/** gets blue channnel only.
	 * masks out alpha
	 * @param argb
	 * @return blue
	 */

	public static int getBlue(int argb) {
		return argb & 0xff;
	}
	
	/** sets blue channnel only.
	 * @param argb
	 * 
	 * @return blue 0-255
	 */
	public static int setBlue(int argb, int blue) {
		argb = argb & 0xffffff00;
		argb += blue;
		return argb;
	}
	

		
	private void binarizeImage(BufferedImage image, int minBlack, int maxBlack) {
		Integer height = image.getHeight();
		Integer width = image.getWidth();
		Raster raster = image.getRaster();
		int numdata = raster.getNumDataElements();
		int[] values = new int[numdata];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				LOG.trace(i+" "+j);
				values = raster.getPixel(i, j, values);
				int value = ColorUtilities.getValue(values);
				if (value >= minBlack && value <= maxBlack) {
					values = new int[]{0, 0, 0, 255};
				} else {
					values = new int[]{255, 255, 255, 255};
				}
				image.setRGB(i, j, ColorUtilities.getValue(values));
			}
		}
	}

	private static int getValue(int[] pix) {
		int sum = 0;
		for (Integer i : pix) {
			sum += i;
		}
		sum /= pix.length;
		return sum;
	}
	
	//TODO this and the method below need checking regarding raster types
	public static void convertTransparentToWhite(BufferedImage image) {
		if (image != null) {
			for (int i = 0; i < image.getWidth(); i++) {
				for (int j = 0; j < image.getHeight(); j++) {
					int rgb = image.getRGB(i, j);
					int trans = getAlphaChannel(rgb);
					if (trans == 0) {
						rgb = ARGB_WHITE;
					}
					image.setRGB(i, j, rgb);
				}
			}
		}
	}

	private static int getAlphaChannel(int rgb) {
		return rgb & 0xff000000;
	}

	/** flips black pixels to white and vice versa.
	 * 
	 * @param image
	 */
	public static void flipWhiteBlack(BufferedImage image) {
		if (image != null) {
			Raster raster = image.getRaster();
			raster.getSampleModel();
			int numData = raster.getNumDataElements();
			if (numData == 1) {
				int[] pix = new int[0];
				for (int i = 0; i <image.getWidth(); i++) {
					for (int j = 0; j <image.getHeight(); j++) {
						int rgb = image.getRGB(i, j);
						rgb = removeAlpha(rgb);
						if (rgb == 0) {
							rgb = RGB_WHITE;
						} else if (rgb == RGB_WHITE) {
							rgb = 0;
						}
						image.setRGB(i, j, rgb);
					}
				}
			} else if (numData == 3) {
				int[] pix = new int[numData];
				for (int i = 0; i <image.getWidth(); i++) {
					for (int j = 0; j <image.getHeight(); j++) {
						pix = raster.getPixel(i, j, pix);
						for (int k = 0;k <pix.length; k++) {
							int rgb = RGB_BLACK;
							if (pix[0]+pix[1]+pix[2] == 0) {
								rgb = RGB_WHITE;
							}
							image.setRGB(i, j, rgb);
						}
					}
				}
			} else {
				throw new RuntimeException("I don't understand Raster yet "+numData);
			}
		}
	}

	public static int removeAlpha(int rgb) {
		return rgb & 0x00ffffff;
	}

	/** creates a 6-character representation of color.
	 * leading zero-fill 
	 * @param rgb
	 * @return
	 */
	public static String createHexColor(int rgb) {
		String color = createPaddedHex(rgb);
		return "#"+color;
	}

	public static String createPaddedHex(int rgb) {
		String color = Integer.toHexString(rgb);
		while (color.length() < 6) {
			color = "0"+color;
		}
		return color;
	}

	/** not tested
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static Integer createRGB(int r, int g, int b) {
		int i = (r << 16) + (g << 8) + b;
		return new Integer(i);
	}

	/** creates a 6 hex-digit string padded with leading zeroes.
	 * 
	 * @param hexColorS hex such as abf ; no hashes, ampersands, etc
	 * @return padded string 000abf 
	 */
	public static String padWithLeadingZero(String hexColorS) {
		hexColorS = "000000"+hexColorS;
		hexColorS = hexColorS.substring(hexColorS.length() - 6);
		return hexColorS;
	}

	public static boolean isEqual(RGBColor colorRGB1, RGBColor colorRGB, int[] deltaValues) {
		int red1 = colorRGB1.getRed();
		int red = colorRGB.getRed();
		if (Math.abs(red1 - red) > deltaValues[0]) return false;
		int green1 = colorRGB1.getGreen();
		int green = colorRGB.getGreen();
		if (Math.abs(green1 - green) > deltaValues[1]) return false;
		int blue1 = colorRGB1.getBlue();
		int blue = colorRGB.getBlue();
		if (Math.abs(blue1 - blue) > deltaValues[2]) return false;
		return true;
	}

	/** are two colors equal within tolerances?
	 * 
	 * @param rgb1
	 * @param rgb2
	 * @param deltaRgb
	 * @return
	 */
	public static boolean isEqual(int rgb1, int rgb2) {
		return rgb2 == rgb1;
	}
	

}

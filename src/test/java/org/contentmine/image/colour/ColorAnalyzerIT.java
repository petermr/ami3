package org.contentmine.image.colour;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageUtil;
import org.junit.Test;

import boofcv.alg.color.ColorHsv;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import georegression.metric.UtilAngle;

public class ColorAnalyzerIT {
	private static Logger LOG = Logger.getLogger(ColorAnalyzerIT.class);

	@Test
	public void testPosterizeMadagascar() throws IOException {
		ColorAnalyzerTest.testPosterize0("madagascar");
	}

	@Test
	public void testPosterizeSpect5() throws IOException {
		ColorAnalyzerTest.testPosterize0("spect5");
	}

	@Test
	public void testMoleculeGrayScale() {
		String fileRoot = "histogram";
	
		File moleculeFile = new File(ImageAnalysisFixtures.LINES_DIR, "IMG_20131119a.jpg");
		BufferedImage image = UtilImageIO.loadImage(moleculeFile.toString());
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
		BufferedImage grayImage = colorAnalyzer.getGrayscaleImage();
		
		for (Integer nvalues : new Integer[]{4,8,16,32,64,128}) {
			BufferedImage imageOut = ImageUtil.flattenImage(grayImage, nvalues);
			colorAnalyzer.readImageDeepCopy(imageOut);
			SVGG g = colorAnalyzer.createColorFrequencyPlot();
			SVGSVG.wrapAndWriteAsSVG(g, new File("target/" + fileRoot + "/postermol"+nvalues+".svg"));
			ImageIOUtil.writeImageQuietly(imageOut, new File("target/" + fileRoot + "/postermol"+nvalues+".png"));
		}
		
	}

	@Test
		/** posterize blue/black line plot with antialising
		 * Problem is dithered colours
		 * 
		 * @param filename
		 * @throws IOException
		 */
		public void testPosterizeCochrane() throws IOException {
			ColorAnalyzer colorAnalyzer = new ColorAnalyzer();
			colorAnalyzer.readImage(new File(ImageAnalysisFixtures.PLOT_DIR,  "cochrane/xyplot2.png"));
			colorAnalyzer.setOutputDirectory(new File("target/"+"cochrane/xyplot2"));
			colorAnalyzer.setStartPlot(1);
			colorAnalyzer.setMaxPixelSize(1000000);
	//		colorAnalyzer.setIntervalCount(4);
			colorAnalyzer.setIntervalCount(2);
			colorAnalyzer.setEndPlot(15);
			colorAnalyzer.setMinPixelSize(3000);
	//		colorAnalyzer.setMinPixelSize(300);
			colorAnalyzer.flattenImage();
			colorAnalyzer.analyzeFlattenedColours();
		}

	@Test
	public void testPosterize3Colour() throws IOException {
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer();
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				String panel = "panel"+x+"_"+y;
				colorAnalyzer.readImage(new File(ImageAnalysisFixtures.COMPOUND_DIR,  panel + ".png"));
				colorAnalyzer.setOutputDirectory(new File("target", "posterize/flatten/"+panel+"/"));
				colorAnalyzer.setStartPlot(1);
				colorAnalyzer.setMaxPixelSize(1000000);
				colorAnalyzer.setIntervalCount(32);
				colorAnalyzer.setEndPlot(15);
				colorAnalyzer.setMinPixelSize(3000);
				colorAnalyzer.flattenImage();
				colorAnalyzer.analyzeFlattenedColours();
			}
		}
	}
	
	@Test
	public void testGray_H_V_WithHSV() throws IOException {
		int minChroma = 128;
		boolean[] includeGrays = {true, false};
		boolean saturate = true;
		for (boolean includeGray : includeGrays) {
			for (int ph = 0; ph < 2; ph++) {
				for (int pv = 0; pv < 2; pv++) {
					String panel = "panel"+ph+"_"+pv;
					File input = new File(ImageAnalysisFixtures.COMPOUND_DIR,  panel + ".png");
					ColorAnalyzer colorAnalyzer = new ColorAnalyzer();
					colorAnalyzer.setMinGray(32)
								.setMaxGray(224)
								.setMaxGrayChroma(32)
								.setIncludeGray(includeGray)
								.setSaturate(saturate && !includeGray)
								.readImage(input);
		//			colorAnalyzer.setOutputDirectory(new File("target", "posterize/gray/"+panel+"/"));
					colorAnalyzer.applyGray();
					File outFile = new File(ImageAnalysisFixtures.COMPOUND_DIR,  
							"gray/"+panel+"_"+minChroma+"_"+includeGray + ".png");
					colorAnalyzer.writeGrayImage(outFile);
				}
			}
		}
	}

	@Test
	public void testPosterizeSeveralWithHSV() {
		int minChroma = 128;
		boolean saturate = false;

		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				String panel = "panel"+x+"_"+y;
				BufferedImage image = ImageUtil.readImage(new File(ImageAnalysisFixtures.COMPOUND_DIR,  panel + ".png"));
				ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image)
						.setMinChroma(minChroma)
						.setSaturate(saturate);
				colorAnalyzer.applySaturation();
				File outFile = new File(ImageAnalysisFixtures.COMPOUND_DIR,  panel+"_"+minChroma + ".png");
				colorAnalyzer.writeSaturateImage(outFile);
			}
		}
	}

	/** cycles though chroma and 'saturate' to find best combination
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPosterizeSeveralWithHSVParams() throws IOException {
		boolean[] saturates = {true, false};
		int delta = 64;

		for (boolean saturate : saturates) {
			for (int minChroma = delta; minChroma < 256;  minChroma += delta) {
				for (int ph = 0; ph < 2; ph++) {
					for (int pv = 0; pv < 2; pv++) {
						String panel = "panel"+ph+"_"+pv;
						BufferedImage image = ImageUtil.readImage(new File(ImageAnalysisFixtures.COMPOUND_DIR,  panel + ".png"));
						ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
						colorAnalyzer.setSaturate(saturate)
								.setMinChroma(minChroma)
								.applySaturation();
						File outFile = new File(ImageAnalysisFixtures.COMPOUND_DIR,  panel+"_"+minChroma+"_" + saturate + ".png");
						colorAnalyzer.writeSaturateImage(outFile);
					}
				}
			}
		}
	}

	/**
	 * Selectively displays only pixels which have a similar hue and saturation values to what is provided.
	 * This is intended to be a simple example of color based segmentation.  Color based segmentation can be done
	 * in RGB color, but is more problematic due to it not being intensity invariant.  More robust techniques
	 * can use Gaussian models instead of a uniform distribution, as is done below.
	 * 
	 * Messy - probably obsolete
	 */
	public static void showSelectedColor( BufferedImage image , float hue , float saturation ) {
		Planar<GrayF32> input = ConvertBufferedImage.convertFromPlanar(image,null,true,GrayF32.class);
		Planar<GrayF32> hsv = input.createSameShape();

		// Convert into HSV
//		ColorHsv.rgbToHsv(input,hsv);
		if (true) throw new RuntimeException("showSelectedColor probably obsolete");
		selectHSVPixels(image, hue, saturation, input, hsv);

	}

	private static BufferedImage selectHSVPixels(BufferedImage image, float hue, float saturation, Planar<GrayF32> input,
			Planar<GrayF32> hsv) {
		// Euclidean distance squared threshold for deciding which pixels are members of the selected set
		float maxDist2 = 0.4f * 0.4f;
		// Extract hue and saturation bands which are independent of intensity
		GrayF32 hueBand = hsv.getBand(0);
		GrayF32 satBand = hsv.getBand(1);

		// Adjust the relative importance of Hue and Saturation.
		// Hue has a range of 0 to 2*PI and Saturation from 0 to 1.
		float adjustUnits = (float)(Math.PI/2.0);

		// step through each pixel and mark how close it is to the selected color
		BufferedImage output = new BufferedImage(input.width,input.height,BufferedImage.TYPE_INT_RGB);
		for( int y = 0; y < hsv.height; y++ ) {
			for( int x = 0; x < hsv.width; x++ ) {
				// Hue is an angle in radians, so simple subtraction doesn't work
				float dh = UtilAngle.dist(hueBand.unsafe_get(x,y), hue);
				float ds = (satBand.unsafe_get(x,y) - saturation) * adjustUnits;
				System.out.println(dh+" "+ds);

				// this distance measure is a bit naive, but good enough for to demonstrate the concept
//				float dist2 = dh*dh + ds*ds;
//				if( dist2 <= maxDist2 ) {
//					output.setRGB(x,y,image.getRGB(x,y));
//				}
			}
		}
		return output;
	}

//	public static void main( String args[] ) {
//		BufferedImage image = UtilImageIO.loadImage(UtilIO.pathExample("sunflowers.jpg"));
//
//		// Let the user select a color
//		printClickedColor(image);
//		// Display pre-selected colors
//		showSelectedColor(
//image,1f,1f);
//		showSelectedColor("Green",image,1.5f,0.65f);
//	}
}

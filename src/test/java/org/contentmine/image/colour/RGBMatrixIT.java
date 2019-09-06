package org.contentmine.image.colour;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageUtil;
import org.junit.Test;

import junit.framework.Assert;

public class RGBMatrixIT {
	private static final Logger LOG = Logger.getLogger(RGBMatrixTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	@Test
	public void testGrayScaleAndFilters() throws IOException {
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer();
		colorAnalyzer.readImage(new File(SVGHTMLFixtures.EARLY_CHEM_DIR, "adrenaline0.png"));
		BufferedImage grayImage = colorAnalyzer.getGrayscaleImage();
		ImageIOUtil.writeImageQuietly(grayImage, new File(
				SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, "adrenaline0Gray.png"));
		RealMatrix sharpenFilter = new RealMatrix(
				// sharpen
				new double[][] {
					new double[] {-1./9.,-1./9.,-1./9.,}, 
					new double[] {-1./9., 17./9.,-1./9.,}, 
					new double[] {-1./9.,-1./9.,-1./9.,}, 
				}
				);
		RGBImageMatrix rgbMatrix = RGBImageMatrix.extractMatrix(grayImage);
		RGBImageMatrix rgbMatrix1 = rgbMatrix.applyFilter(sharpenFilter);
		BufferedImage newImage = rgbMatrix1.createImage(RGBImageMatrix.TYPE13);
		ImageIOUtil.writeImageQuietly(newImage, new File(
				SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, "adrenaline0GraySharp.png"));
		RealMatrix edgex = new RealMatrix(
//				new double[][] {
//					new double[] {1.0, 0.0, -1.0}, 
//					new double[] {2.0, 0.0, -2.0}, 
//					new double[] {1.0, 0.0, -1.0}, 
//				}
				new double[][] {
				new double[] {1.0, -1.0}, 
			}
		);
		RealMatrix edgey = new RealMatrix(
				new double[][] {
				new double[] {1.0},
				new double[] {-1.0}, 
			}
		);
		RGBImageMatrix rgbMatrix2 = rgbMatrix1.applyFilter(edgex);
		newImage = rgbMatrix2.createImage(RGBImageMatrix.TYPE13);
		ImageIOUtil.writeImageQuietly(newImage, new File(
				SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, "adrenaline0GrayEdgex.png"));
		rgbMatrix2 = rgbMatrix1.applyFilter(edgey);
		newImage = rgbMatrix2.createImage(RGBImageMatrix.TYPE13);
		ImageIOUtil.writeImageQuietly(newImage, new File(
				SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, "adrenaline0GrayEdgey.png"));
		rgbMatrix2 = rgbMatrix1.applyFilter(edgex);
		rgbMatrix2 = rgbMatrix2.applyFilter(edgey);
		newImage = rgbMatrix2.createImage(RGBImageMatrix.TYPE13);
		ImageIOUtil.writeImageQuietly(newImage, new File(
				SVGHTMLFixtures.EARLY_CHEM_TARGET_DIR, "adrenaline0GrayEdgexy.png"));

	}

}

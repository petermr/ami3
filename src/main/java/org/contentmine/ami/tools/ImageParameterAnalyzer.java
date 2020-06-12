package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.image.AnnotatedImage;
import org.contentmine.image.ImageUtil;

/**
	  	minheight("Minimum height of image (pixels)"),
	  	maxheight("Maximum height of image (pixels)"),
	  	minwidth("Minimum width of image (pixels)"),
	  	maxwidth("Maximum width of image (pixels)"),
	  	minpixf("Minimum fraction of non-background pixels"),
	  	maxpixf("Maximum fraction of non-background pixels"),
	  	minpix("Minimum number of non-background pixels"),
	  	maxpix("Maximum number of non-background pixels"),
	  	strings("Strings in image"),
 * 
 * @author pm286
 *
 */

public class ImageParameterAnalyzer {
	private static final Logger LOG = Logger.getLogger(ImageParameterAnalyzer.ImageParameters.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

    public enum ImageParameters {
	    match("Directory with potential matches"),
	  	minheight("Minimum height of image (pixels)"),
	  	maxheight("Maximum height of image (pixels)"),
	  	minwidth("Minimum width of image (pixels)"),
	  	maxwidth("Maximum width of image (pixels)"),
	  	minpixf("Minimum fraction of non-background pixels"),
	  	maxpixf("Maximum fraction of non-background pixels"),
	  	minpix("Minimum number of non-background pixels"),
	  	maxpix("Maximum number of non-background pixels"),
	  	strings("Strings in image"),
	  	whitethresh("lower threshold for white (1-255)"),
	  	graytol("tolerance for r == g == b (1-255)"),
	  	;
	  	private String title;
			private ImageParameters(String title) {
	  		this.title = title;
	  	}
		public String getTitle() {
			return title;
		}
	}

	private Map<ImageParameters, String> map;
	private Integer graytol;
	private Integer maxheight;
	private Integer minheight;
	private Integer maxwidth;
	private Integer minwidth;
	private Integer maxnonwhitepix;
	private Integer minnonwhitepix;
	private Double maxnonwhitepixf;
	private Double minnonwhitepixf;
	private Integer whitethresh;
	private List<String> strings;
//	private BufferedImage image;

	public ImageParameterAnalyzer() {
		
	}
	
	public ImageParameterAnalyzer setMap(Map<ImageParameters, String> map) {
		this.map = map;
		extractParameters();
		return this;
	}

	private void extractParameters() {
		graytol = getInteger(ImageParameters.graytol);
		maxheight = getInteger(ImageParameters.maxheight);
		minheight = getInteger(ImageParameters.minheight);
		maxwidth = getInteger(ImageParameters.maxwidth);
		minwidth = getInteger(ImageParameters.minwidth);
		maxnonwhitepix = getInteger(ImageParameters.maxpix);
		minnonwhitepix = getInteger(ImageParameters.minpix);
		maxnonwhitepixf = getDouble(ImageParameters.maxpixf);
		minnonwhitepixf = getDouble(ImageParameters.minpixf);
		whitethresh = getInteger(ImageParameters.whitethresh);
		strings = getStrings(ImageParameters.strings);
	}

	private Integer getInteger(ImageParameters key) {
		String s = map.get(key);
		return s == null ? null : Integer.parseInt(s);
	}

	private Double getDouble(ImageParameters key) {
		String s = map.get(key);
		return s == null ? null : Double.parseDouble(s);
	}

	private List<String> getStrings(ImageParameters key) {
		String s = map.get(key);
		return s == null ? null : new ArrayList<String>(Arrays.asList(s.split("\\|")));
	}

	public boolean matches(AnnotatedImage annotatedImage) {
		boolean matches = true;
		// size of image
		if (maxheight != null && annotatedImage.getHeight() > maxheight) {
			System.err.println("maxheight");
			return false;
		}
		if (maxwidth != null && annotatedImage.getWidth() > maxwidth) {
			System.err.println("maxwidth");
			return false;
		}
		if (minheight != null && annotatedImage.getHeight() < minheight) {
			System.err.println("minheight");
			return false;
		}
		if (minwidth != null && annotatedImage.getWidth() < minwidth) {
			System.err.println("minwidth");
			return false;
		}
		
		long totalpix = annotatedImage.getWidth() * annotatedImage.getHeight();
		long whitepix = annotatedImage.getWhiteCount(whitethresh);
		long nonwhitepix = totalpix - whitepix;
		System.err.println("T: "+totalpix+"/W: "+whitepix+"/NW: "+nonwhitepix);
		// non-white versus total pixels
		if (maxnonwhitepix != null && nonwhitepix > maxnonwhitepix) {
			System.err.println("maxnonwhite");
			return false;
		}
		if (maxnonwhitepixf != null && ((double) nonwhitepix / (double) totalpix) > maxnonwhitepixf) {
			System.err.println("maxnonwhitef");
			return false;
		}
		if (minnonwhitepix != null && nonwhitepix < minnonwhitepix) {
			System.err.println("minnonwhite");
			return false;
		}
		if (minnonwhitepixf != null && ((double) nonwhitepix / (double) totalpix) < minnonwhitepixf) {
			System.err.println("minnonwhitef");
			return false;
		}
		
		// is the image gray?
		int graypix = annotatedImage.getGrayCount(graytol);
		if (graytol != null && graypix != nonwhitepix) {
			System.err.println("graypix");
			return false;
		}
		return true;
	}

}

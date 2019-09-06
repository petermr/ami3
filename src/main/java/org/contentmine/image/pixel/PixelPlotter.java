package org.contentmine.image.pixel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;

/** plots Pixel objects such as PixelList, PixelIsland, PixelGraph.
 * 
 * Manages colours, width, filenames, etc.
 * 
 * @author pm286
 *
 */
public class PixelPlotter {
	
	public abstract static class PixelColourer {
		
		public abstract List<String> colour(Pixel pixel);
		
	}

	private final static Logger LOG = Logger.getLogger(PixelPlotter.class);
	
	public static final String DEFAULT_OUTPUT_DIRECTORY = "target/pixels/";
	public static final String DEFAULT_COLOUR = "red";
		
	public static final String[] DEFAULT_COLOURS = {
		"red",
		"green",
		"blue",
		"yellow",
		"cyan",
		"magenta",
	};

	private String currentColour;
	private String[] currentColourArray;
	private String directoryFilename;
	private String currentRoot;
	private String serial;
	private File outputDirectory;

	private Double opacity;
	private String stroke;
	private Double strokeWidth;

	private String name;
	
	public PixelPlotter() {
		setDefaults();
	}

	private void setDefaults() {
		setCurrentColour(DEFAULT_COLOUR);
		setCurrentColourArray(DEFAULT_COLOURS);
		directoryFilename = DEFAULT_OUTPUT_DIRECTORY;
		setSerial("");
		setOpacity((Double)1.0);
		setStroke((String)"none");
		setStrokeWidth((Double)0.0);
	}


	/**
	 * @return the opacity
	 */
	public Double getOpacity() {
		return opacity;
	}

	/**
	 * @param opacity the opacity to set
	 */
	public void setOpacity(Double opacity) {
		this.opacity = opacity;
	}

	/**
	 * @return the stroke
	 */
	public String getStroke() {
		return stroke;
	}

	/**
	 * @param stroke the stroke to set
	 */
	public void setStroke(String stroke) {
		this.stroke = stroke;
	}

	/**
	 * @return the strokeWidth
	 */
	public Double getStrokeWidth() {
		return strokeWidth;
	}

	/**
	 * @param strokeWidth the strokeWidth to set
	 */
	public void setStrokeWidth(Double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	private void setCurrentColour(String colour) {
		this.currentColour = colour;
	}

	/** the serial number/id of plot.
	 * 
	 * Incorporated into file names so no spaces or weird characters, please
	 * 
	 * Default is ""
	 * 
	 * @param string
	 */
	public void setSerial(String string) {
		this.serial = string;
	}

	public void setDirectoryFilename(String filename) {
		this.directoryFilename = filename;
	}

	public void setCurrentColourArray(String[] colours) {
		this.currentColourArray = colours;
	}
	
	public File getOutputFile(String suffix) {
		getOutputDirectory();
		StringBuilder sb = new StringBuilder();
		sb.append(currentRoot);
		if (!serial.equals("")) {
			sb.append("_"+serial);
		}
		if (suffix != null && !suffix.trim().equals("")) {
			if (!suffix.startsWith(".")) {
				sb.append(".");
			}
			sb.append(suffix);
		}
		return new File(outputDirectory, sb.toString());
	}

	/**
	 * @return the currentRoot
	 */
	public String getCurrentRoot() {
		return currentRoot;
	}

	/**
	 * @param currentRoot the currentRoot to set
	 */
	public void setCurrentRoot(String currentRoot) {
		this.currentRoot = currentRoot;
	}

	/**
	 * @return the ringColours
	 */
	public String[] getRingColours() {
		return currentColourArray;
	}

	/**
	 * @return the directoryFilename
	 */
	public String getDirectoryFilename() {
		return directoryFilename;
	}

	/**
	 * @return the serial
	 */
	public String getSerial() {
		return serial;
	}

	public File getOutputDirectory() {
		outputDirectory = new File(directoryFilename);
		return outputDirectory;
	}
	
	public void setOutputDirectory(File dir) {
		this.outputDirectory = dir;
	}

	public SVGG plot(SVGG g, PixelRingList pixelRingList) {
		return plot(g, pixelRingList, currentColourArray);
	}

	public SVGG plot(SVGG g, PixelRingList pixelRingList, String[] colourArray) {
		g = ensureSVGG(g);
		int i = 0;
		for (PixelRing pixelRing : pixelRingList) {
			plot(g, pixelRing, colourArray[i % colourArray.length]);
			i++;
		}
		return g;
	}
	public SVGG plot(SVGG g, PixelList pixelList) {
		return plot(g, pixelList, DEFAULT_COLOUR);
	}

	public SVGG plot(SVGG g, PixelList pixelList, String colour) {
		g = ensureSVGG(g);
		for (Pixel pixel : pixelList) {
			plot(g, pixel, colour);
		}
		return g;
	}

	public SVGG plot(SVGG g, Pixel pixel, String colour) {
		g = ensureSVGG(g);
		SVGRect rect = plotPixel(pixel, colour);
		g.appendChild(rect);
		return g;
	}

	public void plotPixelsToFile(PixelList pixelList) {
		SVGG g = plotPixels(pixelList);
		File file = getOutputFile("svg");
		LOG.trace("output "+file.getAbsolutePath());
		SVGSVG.wrapAndWriteAsSVG(g, file);
	}

	public SVGG plotPixels(PixelList pixelList) {
		return plotPixels(pixelList, currentColour);
	}

	public SVGG plotPixels(PixelList pixelList, String pixelColor) {
		SVGG g = new SVGG();
		LOG.trace("pixelList " + pixelList.size());
		for (Pixel pixel : pixelList) {
			SVGRect rect = plotPixel(pixel, pixelColor);
			g.appendChild(rect);
		}
		return g;
	}

	public SVGG plotPixels(PixelList pixelList, PixelColourer pixelColor) {
		SVGG g = new SVGG();
		LOG.trace("pixelList " + pixelList.size());
		for (Pixel pixel : pixelList) {
			List<SVGRect> rectangles = plotPixel(pixel, pixelColor.colour(pixel));
			for (SVGRect rectangle : rectangles) {
				g.appendChild(rectangle);
			}
		}
		return g;
	}

	public SVGRect plotPixel(Pixel pixel) {
		return plotPixel(pixel, currentColour);
	}

	public SVGRect plotPixel(Pixel pixel, String pixelColour) {
		SVGRect rect = pixel.getSVGRect();
		rect.setFill(pixelColour);
		if (opacity != null) {
			rect.setOpacity(opacity);
		}
		if (stroke != null) {
			rect.setStroke(stroke);
		}
		if (strokeWidth != null) {
			rect.setStrokeWidth(strokeWidth);
		}
		return rect;
	}

	public List<SVGRect> plotPixel(Pixel pixel, List<String> pixelColours) {
		List<SVGRect> rectangles = new ArrayList<SVGRect>();
		int i = 0;
		for (String colour : pixelColours) {
			SVGRect rect = pixel.getSVGRect(1 / ((double) pixelColours.size()), 1, "black");
			rect.setX(rect.getX() + (i++ * (1 / ((double) pixelColours.size()))));
			rect.setFill(colour);
			rectangles.add(rect);
			if (opacity != null) {
				rect.setOpacity(opacity);
			}
			if (stroke != null) {
				rect.setStroke(stroke);
			}
			if (strokeWidth != null) {
				rect.setStrokeWidth(strokeWidth);
			}
		}
		return rectangles;
	}

	private SVGG ensureSVGG(SVGG g) {
		return (g == null) ? new SVGG() : g;
	}

	public void plotPixelsToFile(PixelRingList pixelRingList) {
		SVGG g = new SVGG();
		plot(g, pixelRingList);
		SVGSVG.wrapAndWriteAsSVG(g, getOutputFile("svg"));
	}

	public void plotIsland(PixelIsland island, String serial) {
		island.setPixelPlotter(this);
		setSerial(serial);
		PixelRingList ringList = island.getOrCreateInternalPixelRings();
		plotPixelsToFile(ringList);
	}

	public void setName(String name) {
		this.name = name;
	}
}

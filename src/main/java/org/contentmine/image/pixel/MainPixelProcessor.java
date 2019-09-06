package org.contentmine.image.pixel;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.image.ArgIterator;
import org.contentmine.image.ImageParameters;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.processing.Thinning;

/** manager for all pixel operations.
 * 
 * often contained within ImageProcessor
 * 
 * used to be PixelIslandList but that became over-complex.
 * Also the pixelIslandList might be null but this shouldn't be.
 * 
 * NOTE: setters return PixelProcessor so they can be chained, e.g.
 *     PixelProcessor processor = new PixelProcessor(image).setMaxisland(3).setOutputDir(file)
 * 
 * @author pm286
 *
 */
public class MainPixelProcessor {

	private final static Logger LOG = Logger.getLogger(MainPixelProcessor.class);

	public static final String ISLAND = "-y";
	public static final String ISLAND1 = "--island";
	public final static String TOLERANCE1 = "--tolerance";

	private PixelIslandList pixelIslandList;
	private int maxIsland;
	private boolean debug;
	private ImageProcessor imageProcessor;
	private BufferedImage image;
	private File outputDir;
	private int selectedIslandIndex;
	private ImageParameters parameters;
	private AxialPixelFrequencies axialPixelFrequencies;

	private boolean isBinarised;

	
	public MainPixelProcessor(ImageProcessor imageProcessor) {
		this.imageProcessor = imageProcessor;
		this.parameters = imageProcessor.getParameters();
		setDefaults();
	}
	
	public MainPixelProcessor(BufferedImage image) {
		this.image = image;
	}

	public void setDefaults() {
		this.setMaxIsland(getDefaultMaxIsland());
		this.setSelectedIsland(-1); // because 0 is a valid island
		this.parameters = new ImageParameters();
	}
	
	public void clearVariables() {
		pixelIslandList = null;
		image = null;
	}

	private int getDefaultMaxIsland() {
		return 100;
	}

	public BufferedImage getImage() {
		if (image == null) {
			if (imageProcessor != null) {
				image = imageProcessor.getImage();
			}
		}
		return image;
	}
	/** messy.
	 * If we have set thinning to null, then we don't use superthinning
	 * @param thinning
	 * @return
	 */
	public PixelIslandList getOrCreatePixelIslandList(Thinning thinning) {
		return getOrCreatePixelIslandList((thinning != null));
	}

	/** get PixelIslandList.
	 * 
	 * create it if it doesn't exist.
	 * 
	 * this default is NOT to superthin after creating islands
	 * 
	 * @return
	 */
	public PixelIslandList getOrCreatePixelIslandList() {
		return getOrCreatePixelIslandList(false);
	}
		
	/** get pixelIslandList.
	 * 
	 * if not existing, do floodfill on pixels
	 * 
	 * if superthinning (i.e. cleaning thick edges and pixelNuclei) is required carry it
	 * out here. Ideally it should be with the Z-S thinning, but the code is written for pixels after 
	 * pixelIslands are created.
	 * 
	 * @param superThinning 
	 * @return
	 */
	public PixelIslandList getOrCreatePixelIslandList(boolean superThinning) {
		if (pixelIslandList == null && getImage() != null) {
			FloodFill floodFill = new ImageFloodFill(image);
			floodFill.setMaxIslands(maxIsland);
			LOG.trace("flood fill finished");
			floodFill.setDiagonal(true);
			pixelIslandList = floodFill.getIslandList();
			ImageParameters parameters = getParameters();
			if (parameters != null) {
				pixelIslandList.removeIslandsWithBBoxesLessThan(parameters.getMinimumIslandSize());
				LOG.trace("after remove islands: " + pixelIslandList.size());
			}
			if (superThinning) {
				pixelIslandList.thinThickStepsOld();
				pixelIslandList.trimOrthogonalStubs();
				pixelIslandList.doSuperThinning();
			}
			pixelIslandList.setMainProcessor(this);
			pixelIslandList.setParentIslandList(this);
		}
		return pixelIslandList;
	}

	public MainPixelProcessor setMaxIsland(int maxIsland) {
		this.maxIsland = maxIsland;
		return this;
	}

	public int getMaxIsland() {
		return maxIsland;
	}

	public MainPixelProcessor setOutputDir(File outputDir) {
		this.outputDir = outputDir;
		return this;
	}

	public File getOutputDir() {
		if (outputDir == null) {
			outputDir = imageProcessor.getOutputDir();
		}
		return outputDir;
	}


	public boolean processArg(ArgIterator argIterator) {
		boolean found = true;
		String arg = argIterator.getCurrent();
		if (false) {
			
		} else if (arg.equals(ImageProcessor.DEBUG) || arg.equals(ImageProcessor.DEBUG1)) {
			this.debug = true;
		} else if (arg.equals(TOLERANCE1)) {
			Double value = argIterator.getDoubleValue();
			if (value != null) {
				this.parameters.setSegmentTolerance(value);
			}
		} else if (arg.equals(MainPixelProcessor.ISLAND) || arg.equals(MainPixelProcessor.ISLAND1)) {
			Integer value = argIterator.getSingleIntegerValue();
			if (value != null) {
				setSelectedIsland(value);
			}
			
		} else {
			found = false;
		}
		return found;
	}

	public void setSelectedIsland(int island) {
		this.selectedIslandIndex = island;
		LOG.trace("PPxx "+this.hashCode()+" "+island);
	}

	public int getSelectedIslandIndex() {
		return selectedIslandIndex;
	}

	public void debug() {
		System.err.println("pixelIslandList   "+pixelIslandList);
		System.err.println("maxIsland         "+maxIsland);
//		System.err.println("pixelGraphList    "+pixelGraphList);
	}

	public void setParameters(ImageParameters parameters) {
		this.parameters = parameters;
	}

	public ImageParameters getParameters() {
		return parameters;
	}

	public AxialPixelFrequencies getOrCreateAxialPixelFrequencies() {
		if (axialPixelFrequencies == null) {
			axialPixelFrequencies = new AxialPixelFrequencies(this);
			axialPixelFrequencies.calculateAxialPixelFrequenciesFromImage();
		}
		return axialPixelFrequencies;
		
	}

	public boolean getBinarize() {
		return imageProcessor.getBinarize();
	}

	public AxialPixelFrequencies getFrequencies() {
		return axialPixelFrequencies;
	}

}

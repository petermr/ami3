package org.contentmine.ami.tools.template;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.image.ImageUtil;

public class ImageTemplateElement extends AbstractTemplateElement {


	private static final Logger LOG = Logger.getLogger(ImageTemplateElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String TAG = "image";
	
	private enum Direction {
		horizontal, 
		vertical
	}
	private static final String NULL = "null";
	
	public static final String BORDERS    = "borders";
	public static final String EXTENSION  = "extension";
	public static final String SECTIONS   = "sections";
	public static final String SOURCE     = "source";
	public static final String SPLIT      = "split";

	private IntArray borders;
	private List<String> sections;
	private String source;
	private Direction splitDirection;
	private File sourceFile;
	private String extension;
	

	public ImageTemplateElement() {
		super(TAG);
	}
	
	@Override
	public void process() {
//		System.out.println(">> "+this.toXML());
		boolean ok = true;
		try {
			parseAttributes();
		} catch (RuntimeException e) {
			ok = false;
//			e.printStackTrace();
			LOG.debug("Cannot create images: "+e.getClass()+"/"+e.getMessage());
		}
		if(ok) {
			if (splitDirection != null) {
				splitImage();
			}
			super.process();
		}
	}

	private void splitImage() {
		if (sourceFile == null) {
			System.err.println("Null source image file");
			return;
		}
		if (!sourceFile.exists()) {
			System.err.println("Source image file does not exist: "+sourceFile);
			return;
		}
		BufferedImage image = ImageUtil.readImage(sourceFile);
		File parentFile = sourceFile.getParentFile();
		int imageSize = Direction.horizontal.equals(splitDirection) ? image.getHeight() : image.getWidth();
		// add both edge borders
		borders.insertElementAt(0,  0);
		borders.addElement(imageSize);
		LOG.trace("borders: "+borders);
		for (int border = 0; border < borders.size() - 1; border++) {
			splitImageAtBorders(image, parentFile, border);
		}
	}

	private void splitImageAtBorders(BufferedImage image, File parentFile, int border) {
		int lower = borders.elementAt(border);
		int higher = borders.elementAt(border + 1);
		String sectionName = sections.get(border);
		int xoff = Direction.horizontal.equals(splitDirection) ? 0 : lower;
		int yoff = Direction.horizontal.equals(splitDirection) ? lower : 0;
		int newWidth = Direction.horizontal.equals(splitDirection) ? image.getWidth() : higher;
		int newHeight = Direction.horizontal.equals(splitDirection) ? higher : image.getHeight();
		IntRange yr = new IntRange(yoff, newHeight);
		IntRange xr = new IntRange(xoff, newWidth);
		if (xr == null || !xr.isValid() || yr == null || !yr.isValid()) {
			LOG.debug("Bad ranges: "+xoff+"/"+newWidth+" // "+yoff+"/"+newHeight);
		} else if (NULL.equals(sectionName)) {
			// skip null sections
		} else {
			BufferedImage newImage = ImageUtil.clipSubImage(image, new Int2Range(xr, yr));
			String basename = FilenameUtils.getBaseName(sourceFile.toString());
			File splitFile = new File(parentFile, basename + "." + sectionName + "." + extension);
			ImageUtil.writeImageQuietly(newImage, splitFile);
//			System.out.println("writing: "+splitFile);
		}
	}

	private void parseAttributes() {
		LOG.trace("processing "+this.getLocalName()+"Template");
		splitDirection = Direction.valueOf(AbstractTemplateElement.getNonNullAttributeValue(this, SPLIT));
		if (splitDirection == null) {
			throw new RuntimeException("must have "+SPLIT+" with one of "+Direction.values());
		}
		String nonNullAttributeValue = AbstractTemplateElement.getNonNullAttributeValue(this, BORDERS);
		LOG.debug(">"+nonNullAttributeValue);
		borders = new RealArray(nonNullAttributeValue).createIntArray();
		if (borders.size() == 0) {
			throw new RuntimeException("must have at least one border: "+this.toXML());
		}
		String attVal = AbstractTemplateElement.getNonNullAttributeValue(this, SECTIONS);
		sections = new ArrayList<String>(Arrays.asList(attVal.trim().split("\\s+")));
		if (sections.size() - borders.size() != 1) {
			System.err.println("must have exactly one more section than border: ["+attVal + "] // "+borders);
			return;
		}
		extension = AbstractTemplateElement.getNonNullAttributeValue(this, EXTENSION);
		if (extension == null) {
			throw new RuntimeException("must have file extension");
		}
		source = AbstractTemplateElement.getNonNullAttributeValue(this, SOURCE);
		sourceFile = currentDir ==  null ? null : new File(currentDir, source);
		if (sourceFile == null || !sourceFile.exists()) {
			System.err.println("must have existing sourcefile: "+sourceFile);
		}
	}
}

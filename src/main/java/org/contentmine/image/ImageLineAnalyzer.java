package org.contentmine.image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;

public class ImageLineAnalyzer {


	private static final Logger LOG = Logger.getLogger(ImageLineAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private int extent0;
	private int extent1;
	private BufferedImage image;
	private Axis2 axis;

	private ImageLineAnalyzer() {
	}
	
	public ImageLineAnalyzer(BufferedImage image) {
		init();
		this.setImage(image);
		
	}
	
	private void init() {
	}

	public void set(BufferedImage image, Axis2 axis) {
		this.image = image;
		this.axis = axis;
		setExtents();
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public Axis2 getAxis() {
		return axis;
	}

	public void setAxis(Axis2 axis) {
		this.axis = axis;
		setExtents();
	}



	public List<IntRange> getLinesAt(Axis2 axis, int value, int colour) {
		setAxis(axis);
		if (value < 0 || value >= extent1) {
			throw new RuntimeException("bad value: "+value);
		}
		int i = 0;
		int min = -1;
		int max = -1;
		boolean inRange = false;
		IntRange intRange = null;
		List<IntRange> rangeList = new ArrayList<>();
		while (i <= extent0) {
			int x = Axis2.X.equals(axis) ? i : value;
			int y = Axis2.X.equals(axis) ? value : i;
			Integer col = (i == extent0) ? null : image.getRGB(x, y) & 0x00FFFFFF;
			if (col != null && col == colour) {
				if (!inRange) {
					min = i;
					inRange = true;
				}
			} else {
				if (inRange) {
					max = i - 1;
					intRange = new IntRange(min, max);
					rangeList.add(intRange);
				}
				inRange = false;
				intRange = null;
			}
			i++;
		}
		return rangeList;
	}

	/** find thin black lines running across and split at them.
	 * 
	 * @param image
	 * @param axis that lines are paralell to
	 * @param lineFraction
	 * @param offset
	 * @return
	 */
	public List<BufferedImage> splitAtMajorLines(Axis2 axis, double lineFraction, int offset) {
		setAxis(axis);
		IntArray blackHorizontal = projectOnto(axis.otherAxis(), 0);
		Map<Integer, Integer> largeHorizontalMap = 
				blackHorizontal.getMapOfValuesOver((int)(lineFraction*image.getWidth()));
		IntArray.createSingleElementBins(largeHorizontalMap);
		List<Integer>borders = new ArrayList<>(largeHorizontalMap.keySet());
		Collections.sort(borders);
		// add offset to borders
		for (int i = 0; i < borders.size(); i++) {
			borders.set(i, Math.max(0, borders.get(i) - offset));
		}
		// image 0 is zero height so skip
		List<BufferedImage> imageList = splitImageAlong(axis, borders);
		imageList.remove(0);
		return imageList;
	}

	/** splits image at given border.
	 * 
	 * @param image
	 * @param axis measuring the steps, e.g. Axis2.X will have constant height 
	 * @param splitCoordinate coordinate to split at
	 * @return
	 */
	
	public List<BufferedImage> splitImageAlong(Axis2 axis, int splitCoordinate) {
		return splitImageAlong(axis, Arrays.asList(new Integer[] {splitCoordinate}));
	}

	/** splits image at given borders.
	 * will generally create (borders.size() + 1) new images
	 * @param image
	 * @param axis measuring the steps, e.g. Axis2.X will have constant height chunks
	 * @param splitCoordinates steps along the axis
	 * @return
	 */
	
	public List<BufferedImage> splitImageAlong(Axis2 axis, List<Integer> splitCoordinates) {
		setAxis(axis);
		List<BufferedImage> imageList = new ArrayList<>();
		if (splitCoordinates.size() == 0) {
			imageList.add(image);
			return imageList;
		}
		List<Integer> borders1 = new ArrayList<Integer>(splitCoordinates);
		Collections.sort(borders1);
		setExtents();
		if (borders1.get(0) < 0 || borders1.get(borders1.size() - 1) >= extent0) {
			throw new RuntimeException("section lines out of range");
		}
		for (int i = 0; i <= borders1.size(); i++) {
			int min = (i == 0) ? 0 : borders1.get(i - 1);
			int max = (i == borders1.size()) ? extent0 : borders1.get(i);
			IntRange intRange0 = new IntRange(0, extent1);
			IntRange intRange1 = new IntRange(min , max );
			Int2Range boundingBox = Axis2.Y.equals(axis) ?  new Int2Range(intRange0, intRange1 ) :
				new Int2Range(intRange1, intRange0);
			BufferedImage subImage = ImageUtil.clipSubImage(image, boundingBox);
			imageList.add(subImage);
		}
		return imageList;
		
	}

	private void setExtents() {
		extent0 = Axis2.X.equals(axis) ? image.getWidth() : image.getHeight();
		extent1 = Axis2.X.equals(axis) ? image.getHeight() : image.getWidth();
	}

	/** extracts a vector (row or col) from an image.
	 * 
	 * @param image
	 * @param axis Axis2.X selects row-based, Axis2.Y col-based 
	 * @param rowcol serial of row or column
	 * @return
	 */
	public int[] getRGBVector(Axis2 axis, int rowcol) {
		setAxis(axis);
		if (rowcol < 0 || rowcol >= extent1) {
			throw new RuntimeException("rowcol "+rowcol+" on axis: "+axis+" out of range ");
		}
		int[] vector = new int[extent0];
		for (int i = 0; i < extent0; i++) {
			int rgb = Axis2.X.equals(axis) ? image.getRGB(i, rowcol) : image.getRGB(rowcol, i);
			vector[i] = rgb;
		}
		return vector;
	}

	/** project ONTO given axis pixel by pixel
	 * 
	 * @param axis
	 * @param image
	 * @param axis0 TODO
	 * @param color
	 * @return
	 */
	public IntArray projectOnto(Axis2 axis, int color) {
		Axis2 otherAxis = axis.otherAxis();
		setAxis(otherAxis);
		IntArray colorArray = new IntArray();
		for (int v = 0; v < extent1; v++) {
			int[] vector = getRGBVector(otherAxis, v);
			colorArray.addElement(ImageUtil.countColor(vector, color));
		}
		return colorArray;
	}

	public void replaceRow(IntArray intArray) {
		Axis2 axis = Axis2.X;
		for (int rowcol : intArray) {
			setExtents();
			if (rowcol < 0 || rowcol >= extent1) {
				throw new RuntimeException("rowcol "+rowcol+" on axis: "+axis+" out of range ");
			}
			for (int i = 0; i < extent0; i++) {
				int x = Axis2.X.equals(axis) ? i : rowcol;
				int y = Axis2.X.equals(axis) ? rowcol : i;
				image.setRGB(x, y, 0x00ffffff);
			}
		}
	}

	/** split image at LH of bottom of plot.
	 * really hairy! don't use this except for Forest Plots (will relocate later)
	 * @param colour colour of line (normally black)
	 * @param minimumLength of lines to select
	 * @param offset to apply after finding lh value
	 * @param axis coordinate of split
	 * @return
	 */
	public List<BufferedImage> splitAtLeftOfBottomLine(int colour, int minimumLength, int offset, Axis2 axis) {
		IntArray horizontalLinesY = projectOnto(axis.otherAxis(), colour);
		Map<Integer, Integer> horizontal = horizontalLinesY.getMapOfValuesOver(minimumLength);
		List<Integer> values = new ArrayList<>(horizontal.keySet());
		Collections.sort(values);
		// get last line
		int y = values.get(values.size() - 1);
		List<IntRange> rangeList = getLinesAt(axis, y, colour);
		IntRange plotAxis = rangeList.get(rangeList.size() - 1); // last line 
		List<BufferedImage> imageList = splitImageAlong(axis, plotAxis.getMin() + offset);
		return imageList;
	}

}

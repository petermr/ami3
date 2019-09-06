package org.contentmine.image.pixel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.image.ImageParameters;
import org.contentmine.image.ImageUtil;

/**
 * connected list of pixels.
 * 
 * It is possible to traverse all pixels without encountering "gaps". May
 * contain "holes" (e.g letter "O").If there are objects within the hole (e.g.
 * "copyright" 0x00A9 which has "C" inside a circle) they may be initially be in
 * a separate island - we may coordinate this later
 * 
 * Islands can consist of:
 * <ul>
 * <li>A single pixel</li>
 * <li>A connected chain of pixels (with 2 terminal pixels)</li>
 * <li>A tree of pixels with braching nodes (3-8 connected, but likely 3)</li>
 * <li>The above with nuclei (ganglia) in chains or nodes; the nuclei arise from
 * incomplete thinning and are to be reduced to single pixels or chains while
 * retaining connectivity</li>
 * </ul>
 * 
 * @author pm286
 * 
 */
public class PixelIsland implements Iterable<Pixel> {

	private final static Logger LOG = Logger.getLogger(PixelIsland.class);

	public enum PlotType {
		INTERNAL_RINGS,
		ISLAND,
		OUTLINE,
		RIDGE,
		THINNED,
	}
	public static final int NEIGHBOUR8 = -1;
	
	private static final String DEFAULT_OUTPUT_DIRECTORY_FILENAME = "target/island/";
	private static final String DEFAULT_INTERNAL_RING_NAME = "internalRings";
	private static final String DEFAULT_ISLAND_NAME = "island";
	private static final String DEFAULT_OUTLINE_NAME = "outline";
	private static final String DEFAULT_RIDGE_NAME = "ridge";
	private static final String DEFAULT_THINNED_NAME = "thinned";
	
	

	PixelIslandList islandList;
	PixelList pixelList; // these may have original coordinates
	private boolean allowDiagonal = false;
	private Int2Range int2range;
	private Real2Range real2range;
	private Int2 leftmostCoord;
	Map<Int2, Pixel> pixelByCoordMap; // find pixel or null
	private PixelList terminalPixels;

	private String pixelColor = "red";
	private PixelSet cornerSet;
	private PixelList emptyPixelList;
	private PixelList singleHoleList;
	private PixelNucleusFactory nucleusFactory;
	private PixelList orthogonalStubList;
	private PixelGraph pixelGraph;
	private PixelNodeList nodeList;
	private PixelEdgeList edgeList;

	private SVGG svgg;
	private String id;

	private PixelRingList internalPixelRings;

	// plotting stuff
	private PixelPlotter pixelPlotter;
	private PixelIslandAnnotation islandAnnotation;

	private List<IslandRingList> islandRingListList;

	private ImageParameters imageParameters;
	
	
	
	public PixelIsland() {
		ensurePixelList();
		setDefaults();
	}

	private void setDefaults() {
		pixelPlotter = new PixelPlotter();
		setIslandAnnotation(new PixelIslandAnnotation());
		getIslandAnnotation().setPlotInternalRings(true);
		getIslandAnnotation().setIslandName(DEFAULT_ISLAND_NAME);
		getIslandAnnotation().setPlotIsland(true);
		getIslandAnnotation().setOutlineName(DEFAULT_OUTLINE_NAME);
		getIslandAnnotation().setPlotOutline(true);
		getIslandAnnotation().setRidgeName(DEFAULT_RIDGE_NAME);
		getIslandAnnotation().setPlotRidge(true);
		getIslandAnnotation().setThinnedName(DEFAULT_THINNED_NAME);
		getIslandAnnotation().setPlotThinned(true);
	}

	@Deprecated // shallow copy, use createSeparateIslandWithClonedPixels
	public PixelIsland(PixelList pixelList) {
		this(pixelList, false);
	}
			
	public static PixelIsland createSeparateIslandWithClonedPixels(PixelList pixelList, boolean diagonal) {
		PixelIsland cloneIsland = new PixelIsland();
		for (Pixel pixel : pixelList) {
			Pixel clonePixel = new Pixel(pixel);
			clonePixel.setIsland(cloneIsland);
			cloneIsland.addPixelWithoutComputingNeighbours(clonePixel);
		}
		cloneIsland.setDiagonal(diagonal);
		return cloneIsland;
	}

	private void ensurePixelList() {
		if (pixelList == null) {
			this.pixelList = new PixelList();
		}
	}

	/**
	 * 
	 * @param pixelList
	 * @param diagonal
	 *            were diagonal neighbours allowed in creating the pixelList?
	 */
	@Deprecated //(shallow copy, not always what was wanted)
	public PixelIsland(PixelList pixelList, boolean diagonal) {
		this.pixelList = pixelList;
		this.allowDiagonal = diagonal;
		pixelList.setIsland(this);
		createMapAndRanges();
	}

	public PixelIsland(PixelIsland island) {
		this(island.getPixelList());
		this.allowDiagonal = island.allowDiagonal;
		this.islandList = island.islandList;
	}

	public Real2Range getBoundingBox() {
		if (real2range != null) {
			return real2range;
		}
		ensurePixelList();
		Real2Range r2r = new Real2Range();
		for (Pixel pixel : pixelList) {
			r2r.add(new Real2(pixel.getInt2()));
		}
		real2range = r2r;
		return r2r;
	}

	public Int2Range getIntBoundingBox() {
		if (int2range != null) {
			return int2range;
		}
		ensurePixelList();
		Int2Range i2r = new Int2Range();
		for (Pixel pixel : pixelList) {
			i2r.add(pixel.getInt2());
		}
		int2range = i2r;
		return i2r;
	}

	public void addPixelAndComputeNeighbourNeighbours(Pixel pixel) {
		ensurePixelList();
		this.pixelList.add(pixel);
		createMapAndRanges(pixel);
		// FIXME - why???
		pixel.createNeighbourNeighbourList(this);
	}

	public void addPixelWithoutComputingNeighbours(Pixel pixel) {
		ensurePixelList();
		this.pixelList.add(pixel);
	}

	@Deprecated // does not control neighbour list
	// use addPixelWithoutComputingNeighbours or addPixelAndComputeNeighbourNeighbours
	public void addPixel(Pixel pixel) {
		
		ensurePixelList();
		this.pixelList.add(pixel);
		createMapAndRanges(pixel);
		// FIXME - why???
		pixel.createNeighbourNeighbourList(this);
	}

	private void createMapAndRanges() {
		ensurePixelList();
		for (Pixel pixel : pixelList) {
			createMapAndRanges(pixel);
		}
	}
	
	void ensurePopulatedMapAndRanges() {
		ensurePixelByCoordMap();
		if (pixelByCoordMap.size() == 0) {
			createMapAndRanges(pixelList);
		}
	}

	private void createMapAndRanges(PixelList pixelList) {
		for (Pixel pixel : pixelList) {
			createMapAndRanges(pixel);
		}
	}
	private void createMapAndRanges(Pixel pixel) {
		ensureInt2Range();
		ensureReal2Range();
		ensurePixelByCoordMap();
		Int2 int2 = pixel.getInt2();
		pixelByCoordMap.put(int2, pixel);
		int2range.add(int2);
		real2range.add(new Real2(int2));
		if (leftmostCoord == null || leftmostCoord.getX() < int2.getX()) {
			leftmostCoord = int2;
		}
		pixel.setIsland(this);
	}

	private void ensureInt2Range() {
		if (this.int2range == null) {
			int2range = new Int2Range();
		}
	}

	private void ensureReal2Range() {
		if (real2range == null) {
			real2range = new Real2Range();
		}
	}

	public int size() {
		ensurePixelList();
		return this.pixelList.size();
	}

	public Pixel getPixelByCoord(Int2 coord) {
		ensurePopulatedMapAndRanges();
		Pixel pixel = getPixelByCoordMap().get(coord);
		return pixel;
	}
	
	public Map<Int2, Pixel> getPixelByCoordMap() {
		ensurePixelByCoordMap();
		return pixelByCoordMap;
	}

	private void ensurePixelByCoordMap() {
		if (pixelByCoordMap == null) {
			pixelByCoordMap = new HashMap<Int2, Pixel>();
		}
	}

	public PixelList getPixelList() {
		ensurePixelList();
		return pixelList;
	}

	PixelList getTerminalPixels() {
		terminalPixels = getPixelsWithNeighbourCount(1);
		return terminalPixels;
	}

	public PixelList getPixelsWithNeighbourCount(int neighbourCount) {
		PixelList pixels = new PixelList();
		for (Pixel pixel : pixelList) {
			int nCount = getNeighbourCount(pixel);
			if (neighbourCount == nCount) {
				pixels.add(pixel);
			}
		}
		return pixels;
	}

	private int getNeighbourCount(Pixel pixel) {
		return pixel.getOrCreateNeighbours(this).size();
	}

	public void setDiagonal(boolean diagonal) {
		this.allowDiagonal = diagonal;
	}

	/**
	 * private List<Pixel> pixelList; boolean allowDiagonal = false; private
	 * Int2Range int2range; private Int2 leftmostCoord; Map<Int2, Pixel>
	 * pixelByCoordMap;
	 * 
	 * @param pixel
	 */
	public boolean remove(Pixel pixel) {
		boolean remove = false;
		if (pixelList.remove(pixel)) {
			//Leaves int2range, real2range and leftmostCoord dirty
			int2range = null;
			real2range = null;
			leftmostCoord = null;
			Int2 coord = pixel.getInt2();
			pixelByCoordMap.remove(coord);
			pixel.removeFromNeighbourNeighbourList(this);
			pixel.clearNeighbours();
			remove = true;
		}
		return remove;
	}

	/**
	 * remove steps and leave diagonal connections.
	 * 
	 * A step is: 1-2 ..3-4
	 * 
	 * where 2 and 3 have 3 connections (including diagonals and no other
	 * neighbours)
	 * 
	 * we want to remove either 2 or 3
	 * 
	 * @return pixels removed
	 */
	public PixelSet removeSteps() {
		PixelSet removed = new PixelSet();
		for (Pixel pixel : pixelList) {
			if (removed.contains(pixel)) {
				continue;
			}
			PixelList pixelNeighbours = pixel.getOrCreateNeighbours(this);
			if (pixelNeighbours.size() == 3) { // could be step or tJunction
				for (int i = 0; i < pixelNeighbours.size(); i++) {
					Pixel pi = pixelNeighbours.get(i);
					if (pi.isOrthogonalNeighbour(pixel)) {
						int j = (i + 1) % 3;
						Pixel pj = pixelNeighbours.get(j);
						int k = (i + 2) % 3;
						Pixel pk = pixelNeighbours.get(k);
						if (pj.isKnightsMove(pk, pi)) {
							removed.add(pixel);
							LOG.trace("removed: " + pixel);
							// this.remove(pixel);
						}
					}
				}
			}
		}
		for (Pixel pixel : removed) {
			this.remove(pixel);
		}
		return removed;
	}

	public SVGG createSVG() {
		SVGG g = new SVGG();
		for (Pixel pixel : pixelList) {
			g.appendChild(pixel.getSVGRect());
		}
		return g;
	}

	public void setPixelColor(String color) {
		this.pixelColor = color;
	}

	/**
	 * plost pixels as rectangles filled with pixelColor.
	 * 
	 * @return
	 */
	public SVGG plotPixels() {
		SVGG g = pixelPlotter.plotPixels(this.getPixelList(), this.pixelColor);
		return g;
	}

//	public static SVGG plotPixels(PixelList pixelList, String pixelColor) {
//		SVGG g = new SVGG();
//		LOG.trace("pixelList " + pixelList.size());
//		for (Pixel pixel : pixelList) {
//			SVGRect rect = pixel.getSVGRect();
//			rect.setFill(pixelColor);
//			LOG.trace(rect.getBoundingBox());
//			g.appendChild(rect);
//		}
//		return g;
//	}

	public boolean fitsWithin(RealRange xSizeRange, RealRange ySizeRange) {
		double wmax = xSizeRange.getMax();
		double wmin = xSizeRange.getMin();
		double hmax = ySizeRange.getMax();
		double hmin = ySizeRange.getMin();
		Real2Range ibox = getBoundingBox();
		double width = ibox.getXRange().getRange();
		double height = ibox.getYRange().getRange();
		boolean include = ((width <= wmax && width >= wmin) && (height <= hmax && height >= hmin));
		return include;
	}

	public boolean fitsWithin(Real2Range range) {
		return range.includes(getBoundingBox());
	}

	/**
	 * computes correlations and outputs images.
	 * 
	 * @param island2
	 *            must be binarized
	 * @param title
	 *            if not null creates title.svg
	 * @return correlation
	 */
	public double binaryIslandCorrelation(PixelIsland island2, String title) {
		Int2Range bbox1 = this.getIntBoundingBox();
		Int2Range bbox2 = island2.getIntBoundingBox();
		int xRange1 = bbox1.getXRange().getRange();
		int yRange1 = bbox1.getYRange().getRange();
		int xMin1 = bbox1.getXRange().getMin();
		int yMin1 = bbox1.getYRange().getMin();
		int xRange2 = bbox2.getXRange().getRange();
		int yRange2 = bbox2.getYRange().getRange();
		int xMin2 = bbox2.getXRange().getMin();
		int yMin2 = bbox2.getYRange().getMin();
		int xrange = Math.max(xRange1, xRange2);
		int yrange = Math.max(yRange1, yRange2);
		LOG.trace(xrange + " " + yrange);
		double score = 0.;
		File file = new File("target/correlate/");
		SVGG g = new SVGG();
		for (int i = 0; i < xrange; i++) {
			int x1 = xMin1 + i;
			int x2 = xMin2 + i;
			for (int j = 0; j < yrange; j++) {
				int y1 = yMin1 + j;
				int y2 = yMin2 + j;
				Int2 i2 = new Int2(x1, y1);
				Pixel pixel1 = pixelByCoordMap.get(i2);
				Pixel pixel2 = island2.pixelByCoordMap.get(new Int2(x2, y2));
				if (pixel1 != null) {
					g.appendChild(addRect(i2, "red"));
				}
				if (pixel2 != null) {
					g.appendChild(addRect(i2, "blue"));
				}
				if (pixel1 != null && pixel2 != null) {
					g.appendChild(addRect(i2, "purple"));
					score++;
				} else if (pixel1 == null && pixel2 == null) {
					score++;
				} else {
					score--;
				}
			}
		}
		if (title != null) {
			File filex = new File(file, title + ".svg");
			filex.getParentFile().mkdirs();
			SVGSVG.wrapAndWriteAsSVG(g, filex);
		}
		return score / (xrange * yrange);
	}

	private SVGRect addRect(Int2 i2, String color) {
		double x = i2.getX();
		double y = i2.getY();
		SVGRect rect = new SVGRect(new Real2(x, y), new Real2(x + 1, y + 1));
		rect.setStroke("none");
		rect.setFill(color);
		return rect;
	}

	/**
	 * clips rectangular image from rawImage corresponding to this.
	 * 
	 * WARNING. still adjusting inclusive/exclusive clip
	 * 
	 * @param rawImage
	 *            to clip from
	 * @return image in raw image with same bounding box as this
	 */
	public BufferedImage clipSubimage(BufferedImage rawImage) {
		Int2Range i2r = getIntBoundingBox();
		// may have clipped 1 pixel too much...
		IntRange ix = i2r.getXRange();
		IntRange iy = i2r.getYRange();
		i2r = new Int2Range(new IntRange(ix.getMin(), ix.getMax() + 1),
				new IntRange(iy.getMin(), iy.getMax() + 1));
		BufferedImage subImage = ImageUtil.clipSubImage(rawImage, i2r);
		return subImage;
	}

	/** creates RGB image.
	 * 
	 * @return
	 */
	public BufferedImage createImage() {
		return createImage(BufferedImage.TYPE_INT_RGB);
	}



	public BufferedImage createImage(int imageType) {
		Int2Range bbox = this.getIntBoundingBox();
		int xmin = bbox.getXRange().getMin();
		int ymin = bbox.getYRange().getMin();
		int w = bbox.getXRange().getRange() + 1; // this was a bug
		int h = bbox.getYRange().getRange() + 1; // and this
		BufferedImage image = null;
		if (w == 0 || h == 0) {
			LOG.trace("zero pixel image");
			return image;
		}
		image = new BufferedImage(w, h, imageType);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				image.setRGB(i, j, 0xffffff);
			}
		}
		int wrote = 0;
		for (Pixel pixel : this.getPixelList()) {
			Int2 xy = pixel.getInt2();
			int x = xy.getX() - xmin;
			int y = xy.getY() - ymin;
			if (x < w && y < h) {
				image.setRGB(x, y, 0);
				wrote++;
			} else {
				LOG.error("Tried to write pixel outside image area "+xy);
			}
		}
		LOG.trace("created image, size: " + pixelList.size()+" "+wrote);
		return image;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("pixels " + ((pixelList == null) ? null : pixelList.size()));
		sb.append("; int2range " + int2range);
		return sb.toString();
	}

	public void removePixels(PixelList pixelList) {
		for (Pixel pixel : pixelList) {
			this.remove(pixel);
		}
	}

	public String getPixelColor() {
		return pixelColor;
	}

	public List<PixelIsland> findPixelLakes() {
		throw new RuntimeException("NYI");
	}
	
	/** finds all pixels exposed to whitespace.
	 * 
	 * defined as a pixel with < 8 neighbours.
	 * 
	 * includes external and internal space.
	 * 
	 * @return
	 */
	public PixelList createExposedPixelList() {
		PixelList exposedList = new PixelList();
		for (Pixel pixel : pixelList) {
			PixelList neighbours = pixel.getOrCreateNeighbours(this);
			if (neighbours.size() < 8) {
				exposedList.add(pixel);
			}
		}
		return exposedList;
	}

	/** repeatedly gets shells of pixels neighbouring the island.
	 * 
	 * @param thickness number of shells to grow.
	 * 
	 * @return all pixels 
	 */
	public PixelRingList getNeighbouringShells(int thickness) {
		PixelRingList rings = new PixelRingList();
		PixelList exposedList = createExposedPixelList();
		for (int i = 0; i < thickness; i++) {
			PixelList neighbours = exposedList.getOrCreateNeighbours();
			exposedList = new PixelList();
			for (Pixel neighbour : neighbours) {
				if (!contains(neighbour)) {
					addPixelWithoutComputingNeighbours(neighbour);
					exposedList.add(neighbour);
				}
			}
			rings.add(new PixelRing(exposedList));
		}
		return rings;
	}
			

	public void findRidge() {
		markEdges();
	}

	/** mark all pixels which have an exposure to the outside.
	 * 
	 * set value to ffffff (white) by default and 1 if < 8 neighbours
	 * 
	 */
	private void markEdges() {
		PixelList exposedList = new PixelList();
		for (Pixel pixel : pixelList) {
			pixel.setValue(NEIGHBOUR8);
			PixelList neighbours = pixel.getOrCreateNeighbours(this);
			int size = neighbours.size();
			if (size < 8) {
				exposedList.add(pixel);
			}
		}
		for (Pixel pixel : exposedList) {
			pixel.setValue(1);
		}
	}

	/** get list of all pixels with value v.
	 * 
	 * @param v
	 * @return
	 */
	public PixelList getPixelsWithValue(int v) {
		PixelList valueList = new PixelList();
		for (Pixel pixel : pixelList) {
			if (pixel.getValue() == v) {
				valueList.add(pixel);
			}
		}
		return valueList;
	}

	/** for each pixel of value v in list (ring) increment neighbours.
	 * 
	 * @param startPixels
	 * @param v
	 * @return
	 */
	public PixelList growFrom(PixelList startPixels, int v) {
		PixelList growList = new PixelList();
		for (Pixel start : startPixels) {
			if (start.getValue() != v) {
				throw new RuntimeException("bad pixel " + start.getValue());
			}
			PixelList neighbours = start.getOrCreateNeighbours(this);
			for (Pixel neighbour : neighbours) {
				if (neighbour.getValue() == NEIGHBOUR8) {
					neighbour.setValue(v + 1);
					growList.add(neighbour);
				}
			}
		}
		return growList;
	}
	
	/** find rings round ridge.
	 * 
	 * @return
	 */
	public PixelRingList getOrCreateInternalPixelRings() {
//		if (internalPixelRings == null) {
			internalPixelRings = new PixelRingList();
			internalPixelRings.setIsland(this);
			setDiagonal(true);
			findRidge();
			PixelList pixelList = getPixelsWithValue(1);
			int ring = 1;
			while (pixelList.size() > 0) {
				internalPixelRings.add(new PixelRing(pixelList));
				pixelList = growFrom(pixelList, ring++);
			}
//		}
		return internalPixelRings;
	}

	public Pixel get(int i) {
		return pixelList == null || i < 0 || i >= pixelList.size() ? null
				: pixelList.get(i);
	}

	/** removes the pixels from an incompletely thinned island.
	 * 
	 */
	public void removeStepsIteratively() {
		while (true) {
			PixelSet removed = removeSteps();
			if (removed.size() == 0) {
				break;
			}
		}
	}

	public Iterator<Pixel> iterator() {
		return pixelList.iterator();
	}

	/**
	 * plots pixels onto SVGG with current (or default) colour.
	 * 
	 * @return
	 */
	@Deprecated
	public SVGG getSVGG() {
		return getOrCreateSVGG();
	}

	/**
	 * plots pixels onto SVGG with current (or default) colour.
	 * 
	 * @return
	 */
	public SVGG getOrCreateSVGG() {
		ensurePixelPlotter();
		if (svgg == null) {
			svgg = pixelPlotter.plotPixels(pixelList, pixelColor);
		}
		return svgg;
	}

	private void ensurePixelPlotter() {
		if (pixelPlotter == null) {
			pixelPlotter = new PixelPlotter();
		}
	}

	//FIXME this is timeconsuming if not thinned before use
	public void removeCorners() {
		int count = 0;
		while (true) {
			makeCornerSet();
			LOG.trace("cornerSet: "+cornerSet.size()+"; "+this.size());
			if (cornerSet.size() == 0)
				break;
			removeCornerSet();
			count++;
		}
		LOG.trace("removeCornerCount "+count);
	}

	/**
	 * this may be better or complementary to triangles;
	 * 
	 * finds all corners of form
	 * 
	 * ++ +
	 * 
	 */
	private PixelSet makeCornerSet() {
		cornerSet = new PixelSet();
		for (Pixel pixel : this) {
			PixelList orthogonalNeighbours = pixel
					.getOrthogonalNeighbours(this);
			// two orthogonal at right angles?
			if (orthogonalNeighbours.size() == 2) {
				Pixel orthNeigh0 = orthogonalNeighbours.get(0);
				Pixel orthNeigh1 = orthogonalNeighbours.get(1);
				// corner?
				if (orthNeigh0.isDiagonalNeighbour(orthNeigh1)) {
					PixelList diagonalNeighbours = pixel
							.getDiagonalNeighbours(this);
					// is this a diagonal Y-junction?
					boolean add = true;
					for (Pixel diagonalNeighbour : diagonalNeighbours) {
						if (diagonalNeighbour.isKnightsMove(orthNeigh0)
								&& diagonalNeighbour.isKnightsMove(orthNeigh1)) {
							LOG.trace("skipped diagonal Y Junction: "
									+ diagonalNeighbour + "/" + pixel + "/"
									+ orthNeigh0 + "//" + orthNeigh1);
							add = false;
							break; // Y-junction
						}
					}
					if (add) {
						cornerSet.add(pixel);
					}
				}
			}
		}
		return cornerSet;
	}

	/**
	 * removes all corners not next to each other.
	 * 
	 * in some cases may not take the same route and so may give different
	 * answers but the result should always have no corners.
	 */
	public void removeCornerSet() {
		ensureCornerSet();
		while (!cornerSet.isEmpty()) {
			Pixel pixel = cornerSet.iterator().next();
			PixelList neighbours = pixel.getOrCreateNeighbours(this);
			// remove neighbours from set - if they are corners, if not no-op
			for (Pixel neighbour : neighbours) {
				cornerSet.remove(neighbour);
			}
			cornerSet.remove(pixel);
			this.remove(pixel);
		}
	}

	private void ensureCornerSet() {
		if (cornerSet == null) {
			makeCornerSet();
		}
	}

	public PixelGraph getOrCreateGraph() {
		PixelGraph graph = new PixelGraph(this);
		return graph;
	}

	/** sets all pixels in island to black in image
	 * 
	 * subtracts offset
	 * 
	 * @param image
	 * @param xy0 offest to subtract
	 * @param y0
	 */
	void setToBlack(BufferedImage image, Int2 xy0) {
		for (Pixel pixel : getPixelList()) {
			pixel.setToBlack(image, xy0);
		}
	}

	/** get all pixels which could be nodes.
	 * 
	 * At preset this is all except those with 2 connections
	 * 
	 * @return
	 */
	public PixelList getNucleusCentrePixelList() {
		PixelList pixels = new PixelList();
		for (Pixel pixel : pixelList) {
			int neighbourCount = getNeighbourCount(pixel);
			if (neighbourCount != 2) {
				pixels.add(pixel);
			}
		}
		return pixels;
	}

	/** remove orthogonal stub
	 *
	 * single pixel on a surface
	 * 
	 * @return list of pixels removed
	 */
	public PixelList trimOrthogonalStubs() {
		getOrCreateOrthogonalStubList();
		for (Pixel stub : orthogonalStubList) {
			remove(stub);
		}
		return orthogonalStubList;
	}
	
	/** fill empty single pixel.
	 *
	 * "hole" must have 4 orthogonal neighbours
	 */
	public PixelList fillSingleHoles() {
		if (singleHoleList == null) {
			singleHoleList = new PixelList();
			PixelList emptyList = getEmptyPixels();
			for (Pixel pixel : emptyList) {
				PixelList neighbours = this.getFilledOrthogonalNeighbourPixels(pixel);
				if (neighbours.size() == 4) {
					Pixel newPixel = new Pixel(pixel.getInt2().getX(), pixel.getInt2().getY());
					singleHoleList.add(newPixel);
					this.addPixelAndComputeNeighbourNeighbours(newPixel);
				}
			}
		}
		return singleHoleList;
	}

	private PixelList getFilledOrthogonalNeighbourPixels(Pixel pixel) {
		Int2 coord = pixel == null ? null : pixel.getInt2();
		PixelList filledList = null;
		if (coord != null) {
			filledList = new PixelList();
			ensurePixelByCoordMap();
			addPixel(filledList, pixelByCoordMap.get(coord.subtract(new Int2(1,0))));
			addPixel(filledList, pixelByCoordMap.get(coord.subtract(new Int2(0,1))));
			addPixel(filledList, pixelByCoordMap.get(coord.plus(new Int2(1,0))));
			addPixel(filledList, pixelByCoordMap.get(coord.plus(new Int2(0,1))));

		}
		if (filledList.size() > 0) {
			LOG.trace("filled "+filledList.size());
		}
		return filledList;
	}

	/** add pixel if not null
	 * 
	 * @param list
	 * @param pixel
	 */
	private void addPixel(PixelList list, Pixel pixel) {
		if (pixel != null) {
			list.add(pixel);
		}
		
	}

	public PixelList getEmptyPixels() {
		if (emptyPixelList == null) {
			emptyPixelList = new PixelList();
			Int2Range box = this.getIntBoundingBox();
			IntRange xRange = box.getXRange();
			int xmin = xRange.getMin();
			int xmax = xRange.getMax();
			IntRange yRange = box.getYRange();
			int ymin = yRange.getMin();
			int ymax = yRange.getMax();
			Map<Int2, Pixel> pixelByCoordMap = getPixelByCoordMap();
			for (int i = xmin; i <= xmax; i++) {
				for (int j = ymin; j <= ymax; j++) {
					Pixel pixel = pixelByCoordMap.get(new Int2(i, j));
					if (pixel == null) {
						emptyPixelList.add(new Pixel(i, j));
					}
				}
			}
			LOG.trace("empty "+emptyPixelList.size());
		}
		return emptyPixelList;
	}

	public void doSuperThinning() {
		removeSteps();
		doTJunctionThinning();
	}

	public boolean getDiagonal() {
		return allowDiagonal;
	}

	public void recomputeNeighbours() {
		for (Pixel pixel : this) {
			pixel.recomputeNeighbours(this);
		}
	}

	/** does island contain a pixel?
	 * 
	 * crude
	 * 
	 * @param pixel
	 * @return
	 */
	public boolean contains(Pixel pixel) {
		ensurePixelList();
		for (Pixel pixel0 : pixelList) {
			if (pixel.equals(pixel0)) {
				return true;
			}
		}
		return false;
	}

	public void trimCornerPixels() {
		boolean a = allowDiagonal;
		PixelList connected5List = getPixelsWithNeighbourCount(5);
		if (connected5List .size() > 0) {
			for (Pixel pixel : connected5List) {
				if (pixel.form5Corner(this)) {
					this.remove(pixel);
				}
			}
		}
	}
	
	/** adds translation to all pixels
	 * 
	 * @param translation
	 */
	public PixelIsland createTranslate(Int2 translation) {
		throw new RuntimeException("NYI");
	}

	public SVGG createSVGFromEdges() {
		SVGG g = new SVGG();
		SVGText text = new SVGText(new Real2(20., 20.), "SVG Edges NYI");
		return g;
	}

	/** superthin the nucleus by removing 3-coordinated and higher pixels.
	 * 
	 * @return the removed pixels
	 */
	public PixelNucleusList doTJunctionThinning() {
		getOrCreateNucleusFactory();
		PixelNucleusList nucleusList = nucleusFactory.getOrCreateNucleusList();
		nucleusList.doTJunctionThinning(this);
		return nucleusList;
	}

	public PixelList getOrCreateOrthogonalStubList() {
		if (orthogonalStubList == null) {
			orthogonalStubList = new PixelList();
			for (Pixel pixel : this) {
				if (isOrthogonalStub(pixel)) {
					orthogonalStubList.add(pixel);
				}
			}
		}
		return orthogonalStubList;
	}

	public PixelNucleusFactory getOrCreateNucleusFactory() {
		if (nucleusFactory == null) {
			nucleusFactory = new PixelNucleusFactory(this);
			nucleusFactory.setIsland(this);
		}
		
		return nucleusFactory;
	}


	public PixelNodeList getOrCreateNodeList() {
		if (nodeList == null) {
			nodeList = getOrCreateNucleusFactory().getOrCreateNodeListFromNuclei();
		}
		return nodeList;
	}

	public PixelEdgeList getOrCreateEdgeList() {
		getOrCreateNodeList();
		if (edgeList == null) {
			edgeList = getOrCreateNucleusFactory().createPixelEdgeListFromNodeList();
			if (edgeList.size() == 0 && nodeList.size() > 0 && size() > 1) {
				PixelGraph graph = this.getOrCreateGraph();
				edgeList = graph.getOrCreateEdgeList();
			}
		}
		return edgeList;
	}

	private PixelEdgeList createCyclicEdges() {
		LOG.debug("*************** nodeList: " + nodeList.size());
		for (PixelNode node : nodeList) {
			PixelEdgeList edgeList1 = createPixelEdgeListFromCycles(node);
			edgeList.addAll(edgeList1);
		}
		return edgeList;
	}

	private PixelEdgeList createPixelEdgeListFromCycles(PixelNode node) {
		PixelEdgeList edgeList = new PixelEdgeList();
		PixelList neighbours = node.getDiagonalNeighbours(this);
		neighbours.addAll(node.getOrthogonalNeighbours(this));
		LOG.debug("NEIG"+neighbours);
		if (neighbours.size() == 0) {
			LOG.debug("SINGLE POINT");
		} else if (neighbours.size() == 1) {
			LOG.debug("acyclic " + pixelList.size());
		} else if (neighbours.size() == 2) {
			LOG.debug("CYCLE? " + pixelList.size());
		} else {
			LOG.debug(" multiple neighbours: " + neighbours.size() + "/" + this.pixelList.size());
		}
		return edgeList;
	}

	private boolean isOrthogonalStub(Pixel pixel) {
		PixelList neighbours = pixel.getOrCreateNeighbours(this);
		if (neighbours.size() == 3) {
			LOG.trace("3 neighbours "+pixel+"; neighbours "+neighbours);
			if (neighbours.hasSameCoords(0) || neighbours.hasSameCoords(1)) {
				LOG.trace("orthogonal stub "+pixel+"; "+neighbours);
				return true;
			}
		}
		return false;
	}

	private void drawPixels(int serial, String[] color, SVGG gg, int col1, PixelSet set) {
		PixelList pixelList;
		Set<String> ss;
		pixelList = new PixelList();
		pixelList.addAll(set);
		if (pixelList.size() > 1) {
			SVGG g = pixelList.draw(null, color[(serial + col1) % color.length]);
			gg.appendChild(g);
		}
	}

	public void setNucleusFactory(PixelNucleusFactory factory) {
		this.nucleusFactory = factory;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setIslandList(PixelIslandList islandList) {
		this.islandList = islandList;
	}

	public ImageParameters getParameters() {
		getIslandList();
		return islandList == null ? getDefaultParameters() : islandList.getParameters();
	}

	private ImageParameters getDefaultParameters() {
		if (imageParameters == null) {
			imageParameters = new ImageParameters();
		}
		return imageParameters;
	}

	PixelIslandList getIslandList() {
		if (islandList == null) {
			LOG.trace("Island does not have IslandList");
		}
		return islandList;
	}

	/** removes minorIslands 
	 * does not reset maps yet...
	 * 
	 * @param size
	 */
	public void removeMinorIslands(int size) {
		pixelList.setIsland(this);
		pixelList.removeMinorIslands(size);
	}

	// ============= PLOTTING =========
	
	/** plots island with options set in PixelPlotter.
	 * 
	 * @return
	 */
	public SVGG plotIsland() {
		SVGG g = new SVGG();
		getOrCreateInternalPixelRings();
		plot(g, getIslandAnnotation().isPlotInternalRings(), getIslandAnnotation().getInternalRingName(), internalPixelRings);
		plot(g, getIslandAnnotation().isPlotOutline(), getIslandAnnotation().getOutlineName(), internalPixelRings.getOrCreateOutline());
//		plot(g, plotFirstOutline, getFirstOutlineName(), internalPixelRings.getOrCreateFirstOutline());
		return g;
	}

	private void plot(SVGG g, boolean plotMe, String name, PixelRingList pixelRingList) {
		g = ensureSVGG(g);
		if (plotMe && name != null && pixelRingList != null) {
			pixelPlotter.plot(g, pixelRingList);
		}
	}

	private void plot(SVGG g, boolean plotMe, String name, PixelList pixelList) {
		g = ensureSVGG(g);
		if (plotMe && name != null && pixelList != null) {
			pixelPlotter.plot(g, pixelList);
		}
	}

	private SVGG createAndPlotInternalPixelRings(SVGG g) {
		g = ensureSVGG(g);
		PixelRingList pixelRingList = getOrCreateInternalPixelRings();
		pixelPlotter.plot(g, pixelRingList);
		return g;
	}

	private SVGG ensureSVGG(SVGG g) {
		return g == null ? new SVGG() : g;
	}

	public void setPixelPlotter(PixelPlotter pixelPlotter) {
		this.pixelPlotter = pixelPlotter;
	}
	
//	/**
//	 * @return the plotInternalRings
//	 */
//	public boolean isPlotInternalRings() {
//		return plotInternalRings;
//	}
//
//	/**
//	 * @param plotInternalRings the plotInternalRings to set
//	 */
//	public void setPlotInternalRings(boolean plotInternalRings) {
//		this.plotInternalRings = plotInternalRings;
//	}
//
//	/** get root name for internal rings.
//	 * 
//	 * @return name
//	 */
//	public String getInternalRingName() {
//		return internalRingName;
//	}
//
//	/** root of filenames with internalRings.
//	 * 
//	 * @param name if not null draws rings and outputs file with that name
//	 */
//	public void setInternalRingName(String name) {
//		this.internalRingName = name;
//	}
//
//	/**
//	 * @return the islandName
//	 */
//	public String getIslandName() {
//		return islandName;
//	}
//
//	/**
//	 * @param islandName the islandName to set
//	 */
//	public void setIslandName(String islandName) {
//		this.islandName = islandName;
//	}
//
//	/**
//	 * @return the plotIsland
//	 */
//	public boolean isPlotIsland() {
//		return plotIsland;
//	}
//
//	/**
//	 * @param plotIsland the plotIsland to set
//	 */
//	public void setPlotIsland(boolean plotIsland) {
//		this.plotIsland = plotIsland;
//	}
//
//	/**
//	 * @return the outlineName
//	 */
//	public String getOutlineName() {
//		return outlineName;
//	}
//
//	/**
//	 * @param outlineName the outlineName to set
//	 */
//	public void setOutlineName(String outlineName) {
//		this.outlineName = outlineName;
//	}
//
//	/**
//	 * @return the plotOutline
//	 */
//	public boolean isPlotOutline() {
//		return plotOutline;
//	}
//
//	/**
//	 * @param plotOutline the plotOutline to set
//	 */
//	public void setPlotOutline(boolean plotOutline) {
//		this.plotOutline = plotOutline;
//	}
//
//	/**
//	 * @return the ridgeName
//	 */
//	public String getRidgeName() {
//		return ridgeName;
//	}
//
//	/**
//	 * @param ridgeName the ridgeName to set
//	 */
//	public void setRidgeName(String ridgeName) {
//		this.ridgeName = ridgeName;
//	}
//
//	/**
//	 * @return the plotRidge
//	 */
//	public boolean isPlotRidge() {
//		return plotRidge;
//	}
//
//	/**
//	 * @param plotRidge the plotRidge to set
//	 */
//	public void setPlotRidge(boolean plotRidge) {
//		this.plotRidge = plotRidge;
//	}
//
//	/**
//	 * @return the thinnedName
//	 */
//	public String getThinnedName() {
//		return thinnedName;
//	}
//
//	/**
//	 * @param thinnedName the thinnedName to set
//	 */
//	public void setThinnedName(String thinnedName) {
//		this.thinnedName = thinnedName;
//	}
//
//	/**
//	 * @return the plotThinned
//	 */
//	public boolean isPlotThinned() {
//		return plotThinned;
//	}
//
//	/**
//	 * @param plotThinned the plotThinned to set
//	 */
//	public void setPlotThinned(boolean plotThinned) {
//		this.plotThinned = plotThinned;
//	}

	/** creates edge from string representation.
	 * 
	 * {(2,0)(1,0)(0,1)(-1,2)(0,3)(0,4)}/[(2,0)(0,4)]
	 * <-         pixelList           -> <- nodes ->
	 * 
	 * @param edge
	 * @param edgeS TODO
	 * @param pixelEdge TODO
	 * @return
	 */
	public PixelEdge createEdge(String edgeS) {
		if (edgeS == null) return null;
		PixelEdge edge = null;
		Matcher matcher = PixelEdge.EDGE_PATTERN.matcher(edgeS);
		if (matcher.matches()) {
			edge = new PixelEdge(this);
			String pixelListS = matcher.group(1);
			edge.pixelList = PixelList.createPixelList(pixelListS, this);
			String nodeListS = matcher.group(2);
			edge.nodeList = PixelNodeList.createNodeList(nodeListS, this);
		}
		return edge;
	}

	public PixelGraph copyGraphAndTidy() {
		PixelGraph graph = new PixelGraph(this);
		// these fail if they are actually in the constructor. No idea why yet.
		graph.tidyEdgePixelLists();
		graph.compactCloseNodes(3);
		return graph;
	}

	public PixelIslandAnnotation getIslandAnnotation() {
		return islandAnnotation;
	}

	public void setIslandAnnotation(PixelIslandAnnotation islandAnnotation) {
		this.islandAnnotation = islandAnnotation;
	}

	public boolean removeEdge(PixelEdge edge) {
		if (edgeList != null) {
			return edgeList.remove(edge);
		}
		return false;
	}

	public boolean removePixelEdgeList(PixelEdgeList edgeList) {
		boolean remove = false;
		for (PixelEdge edge : edgeList) {
			remove |= this.removeEdge(edge);
		}
		return remove;
	}

	/** sorts pixelList 
	 * 
	 */
	public void sortXY() {
		if (pixelList != null) pixelList.sortXY();
	}

	/** sorts pixelList 
	 * 
	 */
	public void sortYx() {
		if (pixelList != null) pixelList.sortYX();
	}

	/** get list of all the ringLists in an island.
	 * 
	 * Each ringList is the rings at a given "contour" pixel level, starting at 0.
	 * 
	 * @return list of IslandRingLists 
	 */
	public List<IslandRingList> getOrCreateIslandRingListList() {
		if (islandRingListList == null) {
			islandRingListList = new ArrayList<>();
			PixelRingList pixelRings = getOrCreateInternalPixelRings();
			LOG.debug("rings "+pixelRings.size());
			for (int i = 0; i < pixelRings.size(); i++) {
				IslandRingList islandRingList = pixelRings.get(i).getIslandRings();
				LOG.debug("ring "+i+" "+islandRingList.size());
				islandRingListList.add(islandRingList);
			}
		}
		return islandRingListList;
	}
	
	/** get initial level at which number of rings in ringList is largest.
	 * 
	 * @return
	 */
	public int getLevelForMaximumRingCount() {
		List<IslandRingList> ringListList = getOrCreateIslandRingListList();
		int minsize = -1;
		int levelSize = ringListList.size();
		int maxLevel = levelSize - 1;
		for (int level = 0; level < levelSize; level++) {
			IslandRingList ringList = ringListList.get(level);
			int size = ringList.size();
			if (size > minsize) {
				minsize = size;
			} else if (size < minsize) {
				// started to decline, take previous ringList
				maxLevel = level - 1;
				break;
			}
		}
		// FIXME - need to manage single symbol islands 
		if (maxLevel == levelSize) maxLevel--;
		// backtrack to first time maximum is reached
		int level = maxLevel;
		for (; level >= 0; level--) {
			IslandRingList ringList = ringListList.get(level);
			int size = ringList.size();
			if (size < minsize) {
				return level + 1;
			}
		}
		return 0;
	}

//	/** create rings of pixels starting at the outside.
//	 * 
//	 * list.get(0) is outermost ring.
//	 * list.get(1) touches it...
//	 * @return
//	 */
//	public List<PixelList> createNestedRings() {
//		setDiagonal(true);
//		findRidge();
//		int value = 1;
//		List<PixelList> pixelListList = new ArrayList<PixelList>();
//		PixelList list = getPixelsWithValue(value);
//		while (list.size() > 0) {
//			pixelListList.add(list);
//			list = growFrom(list, value);
//			value++;
//		}
//		return pixelListList;
//	}

}

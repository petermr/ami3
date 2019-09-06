package org.contentmine.image.pixel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.CHESConstants;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Real2RangeList;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.image.ImageParameters;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelComparator.ComparatorType;
import org.contentmine.image.processing.Thinning;
import org.contentmine.image.processing.ZhangSuenThinning;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * container for collection of PixelIslands.
 * 
 * @author pm286
 * 
 */
public class PixelIslandList implements Iterable<PixelIsland> {

	private static final int MAXPIXEL = 6000;
	private final static Logger LOG = Logger.getLogger(PixelIslandList.class);

	public enum Operation {
		BINARIZE, DEHYPOTENUSE, THIN
	}
				

	private List<PixelIsland> list;
	private String pixelColor;
	private SVGG svgg;
	private boolean debug = false;
	private MainPixelProcessor mainProcessor;
	private boolean diagonal;
	private PixelGraphList graphList;
	private List<PixelList> outlineList;
	private List<String> defaultColorList;
	private Real2RangeList bboxList;

	public PixelIslandList() {
		list = new ArrayList<PixelIsland>();
		init();
	}

	public PixelIslandList(List<PixelIsland> newList) {
		list = newList;
		init();
	}

	private void init() {
		defaultColorList = Arrays.asList(CHESConstants.DEFAULT_COLORS);
	}

	public PixelIslandList(Collection<PixelIsland> collection) {
		this(new ArrayList<PixelIsland>(collection));
	}

	public void setMainProcessor(MainPixelProcessor processor) {
		this.mainProcessor = processor;
	}

	public int size() {
		return list.size();
	}

	public Iterator<PixelIsland> iterator() {
		return list.iterator();
	}

	public PixelIsland get(int i) {
		return list == null || i >= list.size() ? null : list.get(i);
	}

	/** add and make sure pixelIsland has a pixelIslandList for communications.
	 * 
	 * @param pixelIsland
	 */
	public void add(PixelIsland pixelIsland) {
		if(pixelIsland.islandList == null) {
			pixelIsland.setIslandList(this);
		}
		list.add(pixelIsland);
	}

	public List<PixelIsland> getList() {
		return list;
	}

	/**
	 * find all separated islands.
	 * 
	 * creates a FloodFill and extracts Islands from it. diagonal set to true
	 * 
	 * @param image
	 * @return
	 * @throws IOException
	 */
	public static PixelIslandList createSuperThinnedPixelIslandListNew(
			BufferedImage image) {
		MainPixelProcessor pixelProcessor = new MainPixelProcessor(image);
		PixelIslandList islandList = pixelProcessor.getOrCreatePixelIslandList();
		islandList.doSuperThinning();
		return islandList;
	}

	/**
	 * find all separated islands.
	 * 
	 * creates a FloodFill and extracts Islands from it. diagonal set to true
	 * 
	 * defaults to control = ""
	 * 
	 * @param image
	 * @return null if image is null
	 * @throws IOException
	 */
	public static PixelIslandList createSuperThinnedPixelIslandList(
			BufferedImage image) {
		return createSuperThinnedPixelIslandList(image, "");
	}

	/**
	 * find all separated islands.
	 * 
	 * creates a FloodFill and extracts Islands from it. diagonal set to true
	 * 
	 * if control contains" "Y" - normalizeYjunctions
	 * 
	 * @param image
	 * @param control
	 * @return null if image is null
	 * @throws IOException
	 */
	public static PixelIslandList createSuperThinnedPixelIslandList(
			BufferedImage image, String control) {

		PixelIslandList islandList = null;
		if (image != null) {
			MainPixelProcessor pixelProcessor = new MainPixelProcessor(image);
			islandList = pixelProcessor.getOrCreatePixelIslandList();
			islandList.setDiagonal(true);
			SVGSVG.wrapAndWriteAsSVG(islandList.createSVGG(), new File(
					"target/nodesEdges/original.svg"));
			islandList.thinThickStepsOld();
			SVGSVG.wrapAndWriteAsSVG(islandList.createSVGG(), new File(
					"target/nodesEdges/afterDeThick57.svg"));
			islandList.fillSingleHoles();
			SVGSVG.wrapAndWriteAsSVG(islandList.createSVGG(), new File(
					"target/nodesEdges/afterFillHoles.svg"));
			islandList.thinThickStepsOld();
			SVGSVG.wrapAndWriteAsSVG(islandList.createSVGG(), new File(
					"target/nodesEdges/afterDeThick57a.svg"));
			islandList.trimOrthogonalStubs();
			SVGSVG.wrapAndWriteAsSVG(islandList.createSVGG(), new File(
					"target/nodesEdges/afterTrimStubs.svg"));
//			if (control.contains("T")) {
//				islandList.doTJunctionThinning();
//				SVGSVG.wrapAndWriteAsSVG(islandList.createSVGG(), new File(
//					"target/nodesEdges/afterTJunctThin.svg"));
//			}
//			if (control.contains("Y")) {
//				islandList.rearrangeYJunctions();
//				SVGSVG.wrapAndWriteAsSVG(islandList.getOrCreateSVGG(),
//						new File("target/nodesEdges/afterYJunction.svg"));
//			}
		}
		return islandList;
	}

	public Pixel getPixelByCoord(Int2 coord) {
		Pixel pixel = null;
		for (PixelIsland island : this) {
			Pixel pixel1 = island.getPixelByCoord(coord);
			if (pixel1 != null) {
				if (pixel == null) {
					pixel = pixel1;
				} else {
					throw new RuntimeException("Pixel occurs in two island: "
							+ coord);
				}
			}
		}
		return pixel;
	}

	private void recomputeNeighbours() {
		for (PixelIsland island : this) {
			island.recomputeNeighbours();
		}
	}

	public PixelIsland getIslandByPixel(Pixel pixel) {
		// ensureIslandByPixelMap();
		for (PixelIsland island : this) {
			if (island.contains(pixel)) {
				return island;
			}
		}
		return null;
	}

	public void setDiagonal(boolean b) {
		this.diagonal = b;
		for (PixelIsland island : this) {
			island.setDiagonal(b);
		}
	}

	public PixelIslandList smallerThan(Real2 box) {
		List<PixelIsland> newList = new ArrayList<PixelIsland>();
		for (PixelIsland island : list) {
			Real2Range bbox = island.getBoundingBox();
			if (bbox.getXRange().getRange() < box.getX()
					&& bbox.getYRange().getRange() < box.getY()) {
				newList.add(island);
			} else {
				LOG.trace("omitted " + bbox);
			}
		}
		return new PixelIslandList(newList);
	}

	/**
	 * create list of islands falling within dimension ranges.
	 * 
	 * if island dimensions (width, height) fit with x/ySizeRange add to list
	 * 
	 * @param xSizeRange
	 *            range of xSizes inclusive
	 * @param ySizeRange
	 *            range of ySizes inclusive
	 * @return
	 */
	public PixelIslandList isContainedIn(RealRange xSizeRange,
			RealRange ySizeRange) {
		List<PixelIsland> newList = new ArrayList<PixelIsland>();
		for (PixelIsland island : list) {
			if (island.fitsWithin(xSizeRange, ySizeRange)) {
				newList.add(island);
			}
		}
		return new PixelIslandList(newList);
	}

	public Multimap<Integer, PixelIsland> createCharactersByHeight() {
		Multimap<Integer, PixelIsland> map = ArrayListMultimap.create();
		for (PixelIsland island : list) {
			Integer height = (int) (double) island.getBoundingBox().getYRange()
					.getRange();
			map.put(height, island);
		}
		return map;
	}

	public double correlation(int i, int j) {
		return list.get(i).binaryIslandCorrelation(list.get(j), i + "-" + j);
	}

	public SVGG plotPixels() {
		return plotPixels(null); // may change this
	}

	public SVGG plotPixels(Transform2 t2) {
		SVGG g = new SVGG();
		for (PixelIsland island : this) {
			String saveColor = island.getPixelColor();
			island.setPixelColor(this.pixelColor);
			SVGG gg = island.plotPixels();
			if (t2 != null) {
				gg.setTransform(t2);
			}	
			island.setPixelColor(saveColor);
			g.appendChild(gg);
		}
		return g;
	}

	public void setPixelColor(String color) {
		this.pixelColor = color;
	}

	public PixelList getPixelList() {
		PixelList pixelList = new PixelList();
		if (list != null) {
			for (PixelIsland island : list) {
				PixelList pList = island.getPixelList();
				pixelList.addAll(pList);
			}
		}
		return pixelList;
	}

	public List<PixelRingList> createRingListList() {
		List<PixelRingList> ringListList = new ArrayList<PixelRingList>();
		for (PixelIsland island : this) {
			PixelRingList ringList = island.getOrCreateInternalPixelRings();
			ringListList.add(ringList);
		}
		return ringListList;
	}

	public void sortX() {
		Collections.sort(list, new PixelIslandComparator(ComparatorType.LEFT,
				ComparatorType.TOP));
	}

	/**
	 * sorts Y first, then X.
	 * 
	 */
	public void sortYX() {
		Collections.sort(list, new PixelIslandComparator(ComparatorType.TOP,
				ComparatorType.LEFT));
	}

	/**
	 * sorts Y first, then X.
	 * 
	 * @param tolerance
	 *            error allowed (especially in Y)
	 */
	public void sortYX(double tolerance) {
		Collections.sort(list, new PixelIslandComparator(ComparatorType.TOP,
				ComparatorType.LEFT, tolerance));
	}

	/**
	 * attempts to sort on bottom of text boxes.
	 * 
	 * this may get corrupted by characters with descenders
	 * 
	 * @param d
	 */
	public void sortYXText(double tolerance) {
		Collections.sort(list, new PixelIslandComparator(ComparatorType.BOTTOM,
				ComparatorType.RIGHT, tolerance));
		// Collections.reverse(list);
	}

	public void sortSize() {
		Collections.sort(list, new PixelIslandComparator(ComparatorType.SIZE));
	}

	public Real2Range getBoundingBox() {
		Real2Range boundingBox = new Real2Range();
		for (PixelIsland island : list) {
			boundingBox.plusEquals(island.getBoundingBox());
		}
		return boundingBox;
	}

	/**
	 * translates pixelIslandLIst to origin and creates image.
	 * 
	 * @return
	 */
	public BufferedImage createImageAtOrigin() {
		return createImageAtOrigin(this.getBoundingBox());

	}

	public BufferedImage createImageAtOrigin(Real2Range bbox) {
		BufferedImage image = null;
		if (bbox.getXRange() != null || bbox.getYRange() != null) {
			int x0 = (int) (double) bbox.getXMin();
			int y0 = (int) (double) bbox.getYMin();
			int width = (int) (double) bbox.getXRange().getRange() + 1;
			int height = (int) (double) bbox.getYRange().getRange() + 1;
			image = ImageUtil.createARGBBufferedImage(width, height);
			if (image == null) return image;
			clearImage(width, height, image);
			for (PixelIsland pixelIsland : this) {
				pixelIsland.setToBlack(image, new Int2(x0, y0));
			}
		}
		return image;
	}

	private void clearImage(int width, int height, BufferedImage image) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				image.setRGB(i, j, 0x00ffffff);
			}
		}
	}

	public SVGG getOrCreateSVGG() {
		if (this.svgg == null) {
			createSVGG();
		}
		return svgg;
	}

	private SVGG createSVGG() {
		this.svgg = new SVGG();
		for (PixelIsland pixelIsland : list) {
			svgg.appendChild(pixelIsland.getOrCreateSVGG().copy());
		}
		return svgg;
	}

	/**
	 * reverses order of list.
	 * 
	 */
	public void reverse() {
		Collections.reverse(list);
	}

	/**
	 * removes all unnecessary steps while keeping minimum connectivity.
	 * 
	 */
	@Deprecated
	// use removeCorners()
	public void removeStepsIteratively() {
		for (PixelIsland island : list) {
			// island.removeStepsIteratively();
			// may be better...
			island.removeCorners();
			LOG.trace("after remove corners " + island.size());
		}
	}

	/**
	 * removes all unnecessary steps while keeping minimum connectivity.
	 * 
	 */
	private void removeCorners() {
		for (PixelIsland island : list) {
			island.removeCorners();
		}
	}

	/**
	 * gets sizes of islands in current order.
	 * 
	 * @return
	 */
	public IntArray getSizes() {
		IntArray array = new IntArray(list.size());
		for (int i = 0; i < list.size(); i++) {
			array.setElementAt(i, list.get(i).size());
		}
		return array;
	}

	/**
	 * thin all "thick steps".
	 * 
	 * Zhang-Suen thinning sometimes leaves uneccesarily thick lines with
	 * "steps".
	 * 
	 * remove all thick steps to preserve 2-connectivity (including diagonal)
	 * except at branches.
	 * 
	 */
	public void thinThickStepsOld() {
		LOG.trace("removing steps; current Pixel size()"
				+ this.getPixelList().size());
//		removeStepsIteratively();
		this.removeCorners();
		createCleanIslandList();
	}

	/**
	 * thin all "thick steps".
	 * 
	 * Zhang-Suen thinning sometimes leaves unnecessarily thick lines with
	 * "steps".
	 * 
	 * remove all thick steps to preserve 2-connectivity (including diagonal)
	 * except at branches.
	 * 
	 */
	public void doSuperThinning() {
		List<PixelIsland> newIslandList = new ArrayList<PixelIsland>();
		for (PixelIsland island : this) {
			PixelIsland newIsland = new PixelIsland(island.getPixelList());
			newIsland.setDiagonal(island.getDiagonal());
			newIsland.doSuperThinning();
			newIslandList.add(newIsland);
		}
		this.list = newIslandList;
	}

	private void createCleanIslandList() {
		List<PixelIsland> newIslandList = new ArrayList<PixelIsland>();
		for (PixelIsland island : this) {
			PixelIsland newIsland = new PixelIsland(island.getPixelList());
			newIsland.islandList = this;
			newIsland.setDiagonal(diagonal);
			newIslandList.add(newIsland);
			this.list = newIslandList;
		}
	}

//	@Deprecated
//	public PixelGraphList analyzeEdgesAndPlot() throws IOException {
//		PixelGraphList pixelGraphList = new PixelGraphList();
//		thinThickStepsOld();
//		File outputDir = pixelProcessor.getOutputDir();
//		outputDir.mkdirs();
//		ImageIOUtil.writeImageQuietly(createImageAtOrigin(), new File(outputDir,
//				"cleaned.png"));
//		// main tree
//		SVGG g = new SVGG();
//		for (int i = 0; i < Math.min(size(), pixelProcessor.getMaxIsland()); i++) {
//			LOG.debug("============ island " + i + "=============");
//			PixelIsland island = get(i);
//			BufferedImage image1 = island.createImage();
//			if (image1 == null)
//				continue;
//			ImageIOUtil.writeImageQuietly(image1, new File(outputDir, "cleaned"
//					+ i + ".png"));
//			g.appendChild(island.createSVG());
//			PixelGraph graph = island.getOrCreateGraph();
//			graph.setParameters(parameters);
//			PixelEdgeList edgeList = graph.createEdgeListOld();
//			for (PixelEdge edge : edgeList) {
//				g.appendChild(edge.createPixelSVG("red"));
//			}
//			pixelGraphList.add(graph);
//		}
//		SVGSVG.wrapAndWriteAsSVG(g, new File(outputDir, "graphAndChars.svg"));
//		return pixelGraphList;
//	}

	public PixelGraphList getOrCreateGraphList() {
//		this.debugIslands();
		if (graphList == null) {
			graphList = new PixelGraphList();
			doSuperThinning();
			// main tree
			for (int i = 0; i < Math.min(size(), mainProcessor.getMaxIsland()); i++) {
				PixelIsland island = get(i);
				PixelGraph graph = island.getOrCreateGraph();
				graphList.add(graph);
			}
		}
		LOG.trace("pixelGraphList: "+graphList.size());
		return graphList;
	}

	public void debug() {
		// System.err.println("maxIsland:    "+this.maxIsland);
	}

	/**
	 * create PixelIslandList from String.
	 * 
	 * @param size
	 *            of font
	 * @param string
	 *            to write
	 * @param font
	 *            font
	 * @param control
	 *            of thinning
	 * @return
	 */
	public static PixelIslandList createPixelIslandListFromString(double size,
			String string, String font) {
		SVGText text = new SVGText(new Real2(size / 2.0, 3.0 * size / 2.0),
				string);
		text.setFontFamily(font);
		text.setFontSize(size);
		int height = (int) (text.getFontSize() * 2.0);
		int width = (int) (text.getFontSize() * 2.0);
		BufferedImage image = text.createImage(width, height);
		Thinning thinning = new ZhangSuenThinning(image);
		thinning.doThinning();
		image = thinning.getThinnedImage();
		PixelIslandList pixelIslandList = PixelIslandList
				.createSuperThinnedPixelIslandList(image);
		return pixelIslandList;
	}

	public ImageParameters getParameters() {
		getMainProcessor();
		return mainProcessor.getParameters();
	}

	private MainPixelProcessor getMainProcessor() {
		if (mainProcessor == null) {
			throw new RuntimeException("Must have Main Processor");
		}
		return mainProcessor;
	}

	/**
	 * fill holes with 4 orthogonal neighbours
	 * 
	 */
	public void fillSingleHoles() {
		for (PixelIsland island : this) {
			island.fillSingleHoles();
			island.trimCornerPixels();
		}
	}

	/**
	 * remove 3 connected single pixels on "surface" of island
	 * 
	 */
	public PixelList trimOrthogonalStubs() {
		PixelList stubs = new PixelList();
		for (PixelIsland island : this) {
			PixelList stubs0 = getOrCreateOrthogonalStubList();
			island.trimOrthogonalStubs();
			stubs.addAll(stubs0);
		}
		return stubs;
	}

	private PixelList getOrCreateOrthogonalStubList() {
		PixelList stubs = new PixelList();
		for (PixelIsland island : this) {
			PixelList stubs0 = island.getOrCreateOrthogonalStubList();
			stubs.addAll(stubs0);
		}
		return stubs;
	}

	/**
	 * do TJunction thinning on all islands.
	 * 
	 */
	public void doTJunctionThinning() {
		for (PixelIsland island : this) {
			island.doTJunctionThinning();
		}
	}

	public PixelIslandList sortBySizeDescending() {
		sortSize();
		reverse();
		return this;
	}

	public PixelIsland getLargestIsland() {
		sortBySizeDescending();
		PixelIsland island = get(0); // the tree
		return island;
	}

	public PixelGraphList analyzeEdgesAndPlot() {
		LOG.error("NYI");
		return new PixelGraphList();
	}

	public void setParentIslandList(MainPixelProcessor mainPixelProcessor) {
		for (PixelIsland island : this) {
			island.setIslandList(this);
		}
	}

	public void debugIslands() {
		for (PixelIsland island : this) {
			if (island.islandList == null) {
				LOG.debug("******NULL ISLAND LIST");
			}
		}
		LOG.trace("DEBUG ISLAND LIST");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("islands: " + size());
		for (PixelIsland island : this) {
			sb.append("[" + island.size() + "; " + island.getIntBoundingBox() + "]");
		}
		return sb.toString();
	}

	public void removeIslandsWithBBoxesLessThan(Real2Range minimumIslandSize) {
		for (int i = list.size() - 1; i >= 0; i--) {
			PixelIsland island = list.get(i);
			Real2Range bbox = island.getBoundingBox();
			if (bbox.isLessThan(minimumIslandSize)) {
				list.remove(island);
			}
			
		}
	}
	
	/** removes islands with box dimensions both less than int2.
	 * 
	 * @param int2
	 */
	public void removeIslandsWithBBoxesLessThan(Int2 int2) {
		removeIslandsWithBBoxesLessThan(new Real2Range(new RealRange(0, int2.getX()), new RealRange(0, int2.getY())));
	}

	public void removeIslandsWithLessThanPixelCount(int minimumSize) {
		for (int i = list.size() - 1; i >= 0; i--) {
			PixelIsland island = list.get(i);
			if (island.size() < minimumSize) {
				list.remove(island);
			}
		}
	}

	/** 
	 * gets outside pixels as lists (actually List<PixelRing>
	 * @return
	 */
	public List<PixelList> getOrCreateOutlineList() {
		if (outlineList == null) {
			outlineList = new ArrayList<PixelList>();
			List<PixelRing> pixelRingList = getOrCreatePixelRings();
			for (PixelRing pixelRing : pixelRingList) {
				outlineList.add(pixelRing);
			}
		}
		return outlineList;
	}

	public List<PixelRing> getOrCreatePixelRings() {
		List<PixelRing> pixelRingList = new ArrayList<PixelRing>();
		for (PixelIsland pixelIsland : this) {
			PixelRing outer = pixelIsland.getOrCreateInternalPixelRings().get(0);
			pixelRingList.add(outer);
		}
		return pixelRingList;
	}

	public void addOutline(PixelList outline) {
		getOrCreateOutlineList();
		outlineList.add(outline);
	}

	public void setColourIslands() {
		setColourIslands(defaultColorList);
	}

	public void setColourIslands(List<String> colorList) {
		int i = 0;
		for (PixelIsland island : this) {
			PixelIslandAnnotation islandAnnotation = island.getIslandAnnotation();
			islandAnnotation.setPlotColor(colorList.get(i++ % colorList.size()));
		}
	}

	public Real2RangeList getOrCreateBoundingBoxList() {
		if (bboxList == null) {
			bboxList = new Real2RangeList();
			for (PixelIsland pixelIsland : this) {
				bboxList.add(pixelIsland.getBoundingBox());
			}
		}
		return bboxList;
	}

	/** create List of PixelRingLists for each PixelIsland.
	 * 
	 * @return
	 */
	public LocalSummitList createInternalPixelRingListList() {
		LocalSummitList pixelRingListList = new LocalSummitList();
		sortBySizeDescending();
		for (PixelIsland pixelIsland : this) {
			PixelRingList pixelRingList = pixelIsland.getOrCreateInternalPixelRings();
			pixelRingListList.add(pixelRingList);
		}
		return pixelRingListList;
	}

	public static PixelIslandList createPixelIslandList(PixelList pixelList) {
		PixelIslandList islandList = new PixelIslandList();
		if (pixelList != null) { 
			DiagramAnalyzer diagramAnalyzer = DiagramAnalyzer.createDiagramAnalyzer(pixelList);
			islandList = diagramAnalyzer.createDefaultPixelIslandList();
			LOG.debug("PIL " + islandList);
			LOG.debug("PL "+diagramAnalyzer.getPixelList());
		}
		return islandList;
	}

	/** create PixelRingList aggregated over all PixelIslands.
	 * 
	 * @return
	 */
	public PixelRingList createAggregatedInternalPixelRingList() {
		LocalSummitList pixelRingListList = createInternalPixelRingListList();
		PixelRingList aggregatedPixelRingList = new PixelRingList();
		for (PixelRingList pixelRingList : pixelRingListList) {
			aggregatedPixelRingList.addAll(pixelRingList);
		}
		return aggregatedPixelRingList;
	}

}
package org.contentmine.image.diagram;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Angle.Units;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntRangeArray;
import org.contentmine.eucl.euclid.Line2;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.builder.SimpleBuilder;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.cache.LineCache;
import org.contentmine.graphics.svg.linestuff.LineMerger.MergeMethod;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ArgIterator;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.colour.ColorUtilities;
import org.contentmine.image.pixel.AxialPixelFrequencies;
import org.contentmine.image.pixel.LocalSummitList;
import org.contentmine.image.pixel.MainPixelProcessor;
import org.contentmine.image.pixel.Pixel;
import org.contentmine.image.pixel.PixelEdge;
import org.contentmine.image.pixel.PixelEdgeList;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelGraphList;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelList;
import org.contentmine.image.pixel.PixelListFloodFill;
import org.contentmine.image.pixel.PixelOutliner;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.contentmine.image.pixel.PixelSegment;
import org.contentmine.image.pixel.PixelSegmentList;
import org.contentmine.image.processing.PrincipalComponentAnalysis;
import org.contentmine.image.processing.Thinning;
import org.contentmine.image.processing.ZhangSuenThinning;

/**
 * general analyzer of pixel diagrams.
 * also contains command stuff which needs separating.
 * 
 * 
 * Often subclassed (e.g. {hylogenetic Trees, molecules)
 * 
 * @author pm286
 *
 */

public class DiagramAnalyzer {

	private static final String ROOT_REGEX = "\\$\\{root\\}";

	private final static Logger LOG = Logger.getLogger(DiagramAnalyzer.class);

	private static final String TARGET = "target/";

	// keep these ??
	public static final String DEBUG1 = "--debug";
	public static final String EXTENSIONS1 = "--extensions";
	public static final String INPUT1 = "--input";
	public final static String LOGFILE1 = "--logfile";
	public final static String MAX_INPUT1 = "--max";
	public static final String OUTPUT1 = "--output";
	public final static String SKIP1 = "--skip";
	public final static String SVG1 = "--svg";
	
	//private double lowerFontSizeVariation = 0.3;
	private double extraHeightForBrackets = 1.35;
	private int highOCRThreshold = 120;
	private int lowOCRThreshold = 75;
	private double relativeLineLengthForAngleNormalisation = 0.65;
	private double angleJitterThreshold = 0.4;
	private double energyContentThreshold = 0.4;
	private double overlapEpsilon = 0.00001;
	private double textJoiningPositionToleranceRelativeToFontSize = 0.07;
	private double textJoiningFontSizeTolerance = 0.85;
	
	private double tittleSize = 0.25;
	private double tittleDistance = 0.5;
	private double splitCharacterRelativeWidthDifferenceTolerance = 0.2;
	private double equalsSignParallelTolerance = 20;

	protected boolean debug;
	protected ImageProcessor imageProcessor;
	protected DiagramLog diagramLog;
	private int maxInput;
	protected String skipFileString;
	protected File skipFile;
	private String outputFilename;
	private File inputFile;
	// FIXME - get rid of file stuff
	protected String fileRoot;
	private String[] extensions;
	private boolean recurse;
	private SVGParameters svgParameters;

	private SVGG svgg;
	protected PixelGraphList pixelGraphList;
	private String logFile;

	private File inputDir;

	public DiagramAnalyzer() {
		setDefaults();
		clearVariables();
	}

	protected void setDefaults() {
		this.debug = false;
		this.maxInput = Integer.MAX_VALUE;
		this.extensions = new String[] {};
		this.recurse = true;
		this.skipFileString = null;
		ensureImageProcessor();

	}

	public void clearVariables() {
		this.skipFile = null;
		this.inputFile = null;
		this.fileRoot = null;

		imageProcessor.clearVariables();
	}

	protected void usage() {
		System.err.println("  general options:");
		System.err.println("       " + ImageProcessor.DEBUG + " "
				+ ImageProcessor.DEBUG1 + "        set debug on");
		System.err.println();
		imageProcessor.usage();
	}

	protected ImageProcessor ensureImageProcessor() {
		if (imageProcessor == null) {
			this.imageProcessor = new ImageProcessor();
		}
		return imageProcessor;

	}

	public DiagramAnalyzer setImage(BufferedImage img) {
		ensureImageProcessor().setImage(img);
		return this;
	}

	public void debug() {
		imageProcessor.debug();
	}

	protected boolean parseArgs(ArgIterator argIterator) {
		boolean found = true;
		if (argIterator.size() == 0) {
			usage();
		} else {
			while (argIterator.hasNext()) {
				String arg = argIterator.getCurrent();
				if (debug || arg.equals(DEBUG1)) {
					LOG.debug(argIterator.getCurrent());
				}
				if (arg.equals(DEBUG1)) {
					argIterator.getValues();
					debug = true;
				} else if (arg.equals(EXTENSIONS1)) {
					List<String> exts = argIterator.getValues();
					setExtensions(exts.toArray(new String[0]));
				} else if (arg.equals(INPUT1)) {
					String filename = argIterator.getSingleValue();
					setInputFilename(filename);
				} else if (arg.equals(LOGFILE1)) {
					logFile = argIterator.getSingleValue();
				} else if (arg.equals(MAX_INPUT1)) {
					setMaxInput(argIterator.getSingleIntegerValue());
				} else if (arg.equals(OUTPUT1)) {
					setOutputFilename(argIterator.getSingleValue());
				} else if (arg.equals(SKIP1)) {
					setSkipFile(argIterator.getSingleValue());
				} else if (arg.startsWith(SVG1)) {
					ensureSVGParameters().parseArgs(arg,
							argIterator.getValues());
				} else {
					found &= parseArgAndAdvance(argIterator);
				}
			}
		}
		if (debug) {
			this.debug();
		}
		return found;
	}

	private SVGParameters ensureSVGParameters() {
		if (this.svgParameters == null) {
			svgParameters = new SVGParameters();
		}
		return svgParameters;
	}

	private void setExtensions(String[] exts) {
		this.extensions = exts;
	}

	private void setMaxInput(Integer maxInput) {
		this.maxInput = maxInput;
	}

	private void setOutputFilename(String output) {
		this.outputFilename = output;
	}

	private void setInputFilename(String input) {
		this.inputFile = new File(input);
	}

	protected boolean parseArgAndAdvance(ArgIterator argIterator) {
		throw new RuntimeException("Must override parseArgAndAdvance");
	}

	protected void runCommandsIteratively() {
		this.fileRoot = null;
		List<File> inputFiles = getInputFiles();
//		LOG.debug("input"+inputFiles);
		if (inputFiles.size() == 0) {
            LOG.debug("No input file given");
		} else if (inputFiles.size() == 1) {
			this.inputFile = inputFiles.get(0);
			this.fileRoot = FilenameUtils.getBaseName(inputFile.toString());
			runCommandsAndOutput();
		} else {
			int count = 0;
			for (File file : inputFiles) {
				clearVariables();
				inputFile = file;
//				LOG.debug("===========INPUT " + inputFile + "================");
				this.fileRoot = FilenameUtils.getBaseName(inputFile.toString());
				this.skipFile = generateFileWithSubstitutions(skipFileString);
				runCommandsAndOutput();
				count++;
				if (count >= maxInput) {
					LOG.debug("maximum count reached: " + count);
					break;
				}
			}
		}
	}

	private void runCommandsAndOutput() {
		if (skipFile != null && skipFile.exists()) {
			LOG.debug("skipped: " + skipFile.toString());
		} else {
			runCommands();
			processSVG();
		}
	}

	private void processSVG() {
		LOG.trace("PROCESSSVG");
		ensureSVGParameters();
		svgg = new SVGG();
		ensureImageProcessor();
		PixelIslandList pixelIslandList = imageProcessor.getOrCreatePixelIslandList();
		PixelGraphList graphList = pixelIslandList.getOrCreateGraphList();
		LOG.trace("graphs: "+graphList.size());
		if (svgParameters.isDrawPixels()) {
			svgg.appendChild(pixelIslandList.getOrCreateSVGG());
		}
		String[] colours = new String[] { "blue" };
		if (svgParameters.isDrawEdges()) {
			for (PixelGraph graph : graphList) {
				graph.drawEdges(colours, svgg);
			}
		}
		colours = new String[] { "green" };
		if (svgParameters.isDrawNodes()) {
			for (PixelGraph graph : graphList) {
				graph.drawNodes(colours, svgg);
			}
		}
		String filename = svgParameters.getFilename();
		if (filename != null) {
			File file = generateFileWithSubstitutions(filename);
			SVGSVG.wrapAndWriteAsSVG(svgg, file);
		}
	}

	protected File generateFileWithSubstitutions(String fileString) {
		File file = null;
		if (fileString != null) {
			fileString = fileString.replaceAll(ROOT_REGEX, fileRoot);
			LOG.trace(" file: " + fileString);
			file = new File(fileString);
		}
		return file;
	}

	private List<File> getInputFiles() {
		List<File> fileList = new ArrayList<File>();
		if (inputFile == null) {
			// empty list
		} else if (!inputFile.isDirectory()) {
			fileList.add(inputFile);
		} else {
			fileList = new ArrayList<File>(FileUtils.listFiles(inputFile,
					this.extensions, this.recurse));
			LOG.trace("files " + fileList.size());
		}
		return fileList;
	}

	protected boolean runCommands() {
		throw new RuntimeException("Must override runCommands");
	}

	protected void setSkipFile(String skipFile) {
		this.skipFileString = skipFile;
	}

	public void setInputFile(File file) {
		this.inputFile = file;
		getImageProcessor().setInputFile(file);
	}

	public BufferedImage getImage() {
		return imageProcessor.getImage();
	}

	public int getMaxIsland() {
		return ensurePixelProcessor().getMaxIsland();
	}

	public ImageProcessor getImageProcessor() {
		ensureImageProcessor();
		return imageProcessor;
	}

	public MainPixelProcessor ensurePixelProcessor() {
		MainPixelProcessor pixelProcessor = getImageProcessor()
				.ensureMainPixelProcessor();
		LOG.trace("pp " + pixelProcessor.hashCode() + " "
				+ pixelProcessor.getSelectedIslandIndex());
		return pixelProcessor;
	}

	public DiagramAnalyzer setOutputDir(File file) {
		ensureImageProcessor();
		imageProcessor.setOutputDir(file);
		return this;
	}

	public File getOutputDir() {
		ensureImageProcessor();
		return imageProcessor.getOutputDir();
	}

	public DiagramAnalyzer setThinning(Thinning thinning) {
		ensureImageProcessor();
		imageProcessor.setThinning(thinning);
		return this;
	}

	public Thinning getThinning() {
		ensureImageProcessor();
		return imageProcessor.getThinning();
	}

	public DiagramAnalyzer setBinarize(boolean binarize) {
		ensureImageProcessor();
		imageProcessor.setBinarize(binarize);
		return this;
	}


	public DiagramAnalyzer setBase(String base) {
		ensureImageProcessor();
		this.imageProcessor.setBase(base);
		return this;
	}

	public String getBase() {
		ensureImageProcessor();
		return imageProcessor.getBase();
	}

	public int getThreshold() {
		return imageProcessor.getThreshold();
	}

	public void setMaxIsland(int maxIsland) {
		this.getMainPixelProcessor().setMaxIsland(maxIsland);
	}

	public MainPixelProcessor getMainPixelProcessor() {
		ensureImageProcessor();
		return imageProcessor.ensureMainPixelProcessor();
	}

	public PixelIslandList getOrCreatePixelIslandList() {
		ensureImageProcessor();
		return imageProcessor.getOrCreatePixelIslandList();
	}

	public PixelIslandList getOrCreateSortedPixelIslandList() {
		ensureImageProcessor();
		PixelIslandList orCreatePixelIslandList = imageProcessor.getOrCreatePixelIslandList();
		return orCreatePixelIslandList.sortBySizeDescending();
	}

	public void readAndProcessInputFile(File file) {
		imageProcessor.readAndProcessFile(file);
	}

	public void readAndProcessImage(BufferedImage image) {
		imageProcessor.processImage(image);
	}

	public void processImageFile() {
		ensureImageProcessor();
		if (imageProcessor.getImage() == null && inputFile != null) {
			imageProcessor.setInputFile(inputFile);
			imageProcessor.setImage(null);
			imageProcessor.processImageFile();
		}
	}

	public void parseArgs(String[] args) {
		ArgIterator iterator = new ArgIterator(args);
		this.parseArgs(iterator);
	}

	public void parseArgsAndRun(String[] args) {
		try {
			ensureImageProcessor();
			this.parseArgs(args);
			this.runCommandsIteratively();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e);
		} finally {
			finish();
		}
	}

	private void finish() {
//		if (diagramLog != null) {
//			if (diagramLog.hasMessage()) {
//				diagramLog.write();
//			}
//		}
	}

	public PixelGraphList getOrCreateGraphList() {
		// this is required because the commands are now obsolete and we have to load the image
		if (pixelGraphList == null) {
			getOrCreateImageProcessorFromInputFile();
			PixelIslandList pixelIslandList = imageProcessor.getOrCreatePixelIslandList();
			pixelIslandList.sortBySizeDescending();
			pixelIslandList.setDiagonal(true);
			pixelGraphList = pixelIslandList.getOrCreateGraphList();
		}
		return pixelGraphList;
	}

	private ImageProcessor getOrCreateImageProcessorFromInputFile() {
		if ((imageProcessor == null || imageProcessor.getImage() == null) &&
				inputFile != null) {
			imageProcessor = ImageProcessor.createDefaultProcessorAndProcess(inputFile);
		}
		return imageProcessor;
	}
	
	public SVGSVG convertPixelsToSVG() {
		int sizeReductionThreshold = 2;
		SVGSVG svg = new SVGSVG();
		imageProcessor = new ImageProcessor();
		imageProcessor.processImageFile(inputFile);
		imageProcessor.getPixelProcessor().setMaxIsland(Integer.MAX_VALUE);
		PixelIslandList thinnedPixelIslandList = getOrCreatePixelIslandList();
		thinnedPixelIslandList.setDiagonal(true);
		pixelGraphList = thinnedPixelIslandList.getOrCreateGraphList();
		PixelGraphList graphs =  pixelGraphList;
		imageProcessor = new ImageProcessor();
		imageProcessor.setThinning(null);
		imageProcessor.processImageFile(inputFile);
		imageProcessor.getPixelProcessor().setMaxIsland(Integer.MAX_VALUE);
		PixelIslandList nonThinnedPixelIslandList = getOrCreatePixelIslandList();
		
		makeListsHaveSameOrder(thinnedPixelIslandList, nonThinnedPixelIslandList);
		Set<Integer> used = new HashSet<Integer>();
		Map<PixelIsland, SVGLine> linesThatMayBeText = handleSingleLines(graphs, nonThinnedPixelIslandList, used);
		doOCR(svg, nonThinnedPixelIslandList, used, linesThatMayBeText);
		
		List<Integer> ringsForShapes = new ArrayList<Integer>();
		List<PixelRingList> ringLists = new ArrayList<PixelRingList>();
		List<List<SVGPolygon>> polygonListList = new ArrayList<List<SVGPolygon>>();
		for (int i = 0; i < nonThinnedPixelIslandList.size(); i++) {
			PixelIsland island = nonThinnedPixelIslandList.get(i);
			List<SVGPolygon> polygonList = new ArrayList<SVGPolygon>();
			polygonListList.add(polygonList);
			if (used.contains(i)) {
				ringLists.add(new PixelRingList());
				ringsForShapes.add(-1);
				continue;
			}
			int previousIslandCount = 1;
			int previousSize = 0;
			boolean islandBroken = false;
			PixelRingList rings = island.getOrCreateInternalPixelRings();
			boolean shapesFound = false;
			PixelIslandList islandsOfOuterRing = null;
			int initialSize = 0;
			for (int ringNumber = 0; ringNumber < rings.size(); ringNumber++) {
				PixelRing ring = rings.get(ringNumber);
				PixelIsland newIsland = PixelIsland.createSeparateIslandWithClonedPixels(ring, true);
				newIsland.removeMinorIslands(3);
				PixelList newRing = newIsland.getPixelList();
				PixelListFloodFill fill = new PixelListFloodFill(newRing);
				fill.setDiagonal(true);
				PixelIslandList ringIslands = fill.getIslandList();
				int size = (int) (getBoundingBox(newIsland).getXRange().getRange() * getBoundingBox(newIsland).getYRange().getRange());
				if (ringNumber == 0) {
					islandsOfOuterRing = ringIslands;
					initialSize = size;
				}
				if (!islandBroken && ringNumber != 0 && (ringIslands.size() > previousIslandCount || size < previousSize / sizeReductionThreshold)) {
					islandBroken = true;
				} else if ((!islandBroken && ringNumber == rings.size() - 1 && size < initialSize / sizeReductionThreshold) || (islandBroken && ringIslands.size() <= previousIslandCount + 1)) {
					if (ringNumber == rings.size() - 1 && !islandBroken) {
						ringNumber = 0;
						ringIslands = islandsOfOuterRing;
					}
					ringsForShapes.add(ringNumber);
					processOutliners(svg, polygonList, ringNumber, ringIslands);
					shapesFound = true;
					break;
				}
				previousIslandCount = ringIslands.size();
				previousSize = size;
			}
			// end
			ringLists.add(rings);
			if(!shapesFound) {
				ringsForShapes.add(rings.size());
			}
		}
		
		getSegments(svg, graphs, used, ringsForShapes, ringLists, polygonListList);
		return svg;
	}

	private void processOutliners(SVGSVG svg, List<SVGPolygon> polygonList, int ringNumber, PixelIslandList ringIslands) {
		for (PixelIsland shape : ringIslands) {
			PixelOutliner outliner = new PixelOutliner(shape.getPixelList());
			outliner.setMinPolySize(0);
			outliner.setMaxIter(Integer.MAX_VALUE);
			SVGPolygon polygon = outliner.createOutline().get(0);
//			SimpleBuilder.abstractPolygon(polygon, 155); // no idea what the number is
			new SimpleBuilder().normalizeToSmallestMeaningfulPoly(polygon, true);
			SVGPolygon polygon2 = offsetPolygon(polygon, ringNumber);
			svg.appendChild(polygon2);
			polygonList.add(polygon2);
			polygon2.setFill("black");
		}
	}

	private void getSegments(SVGSVG svg, PixelGraphList graphs, Set<Integer> used, List<Integer> ringsForShapes,
			List<PixelRingList> ringLists, List<List<SVGPolygon>> polygonListList) {
		for (int i = 0; i < ringLists.size(); i++) {
			if (used.contains(i)) {
				continue;
			}
			PixelGraph graph = graphs.get(i);
			double distance = Math.sqrt(2 * ringsForShapes.get(i) * ringsForShapes.get(i));
			edge: for (PixelEdge pixelEdge : graph.getOrCreateEdgeList()) {
				PixelSegmentList segments = pixelEdge.getOrCreateSegmentList(6.0, 19, 0.7);
				if (segments.size() == 1) {
					for (PixelEdge otherPixelEdge : graph.getOrCreateEdgeList()) {
						PixelSegmentList otherSegments = otherPixelEdge.getOrCreateSegmentList(6.0, 19, 0.7);
						Line2 otherLine = otherSegments.get(0).getEuclidLine();
						Line2 line = segments.get(0).getEuclidLine();
						if (otherPixelEdge != pixelEdge && otherSegments.size() == 1 && otherLine.isParallelOrAntiParallelTo(line, new Angle (20, Units.DEGREES))) {
							if (line.getLength() < otherLine.getLength() * 0.1) {
								double dist1 = line.getXY(0).getDistance(otherLine.getXY(0));
								double dist2 = line.getXY(0).getDistance(otherLine.getXY(1));
								double dist3 = line.getXY(1).getDistance(otherLine.getXY(0));
								double dist4 = line.getXY(1).getDistance(otherLine.getXY(1));
								if (dist1 < 2 || dist2 < 2 || dist3 < 2 || dist4 < 2) {
									continue edge;
								}
							}
						}
					}
				}
				processSegments(svg, polygonListList, i, distance, segments);
			}
		}
	}

	private void processSegments(SVGSVG svg, List<List<SVGPolygon>> polygonListList, int i, double distance,
			PixelSegmentList segments) {
		segment: for (PixelSegment segment : segments) {
			SVGLine line = segment.getSVGLine();
			Double length = line.getLength();
			if (length < distance) {
				continue segment;
			}
			Real2 midPoint = line.getMidPoint();
			Real2 quarterPoint1 = midPoint.getMidPoint(line.getXY(0));
			Real2 quarterPoint2 = midPoint.getMidPoint(line.getXY(1));
			for (SVGPolygon polygon : polygonListList.get(i)) {
				Real2[] corners = polygon.getBoundingBox().getLLURCorners();
				if (polygon.containsPoint(midPoint, 0) && (length < corners[0].getDistance(corners[1]) / 2 ||
						(polygon.containsPoint(quarterPoint1, 0) && polygon.containsPoint(quarterPoint2, 0)))) {
					continue segment;
				} else {
					for (SVGLine polygonEdge : polygon.getLineList()) {
						Real2 intersection = polygonEdge.getIntersection(line);
						double lambda1 = polygonEdge.getEuclidLine().getLambda(intersection);
						double lambda2 = line.getEuclidLine().getLambda(intersection);
						if (lambda1 > 0 && lambda1 < 1 && lambda2 > 0 && lambda2 < 1) {
							if (polygon.containsPoint(line.getXY(0), 0)) {
								line.setXY(intersection, 0);
							} else if (polygon.containsPoint(line.getXY(1), 0)) {
								line.setXY(intersection, 1);
							}
							midPoint = line.getMidPoint();
							quarterPoint1 = midPoint.getMidPoint(line.getXY(0));
							quarterPoint2 = midPoint.getMidPoint(line.getXY(1));
							break;
						}
					}
					LOG.debug("finished");
				}
			}
			line.setStroke("black");
			line.setStrokeWidth(1.0);
			svg.appendChild(line);
		}
	}

	private SVGPolygon offsetPolygon(SVGPolygon polygon, int amount) {
		List<Real2> newPoints = new ArrayList<Real2>();
		SVGLine previousLine = null;
		SVGLine previousPreviousLine = null;
		for (SVGLine line : polygon.getLineList()) {
			Vector2 normal = new Vector2(line.getEuclidLine().getUnitVector());
			normal.transformBy(new Transform2(new Angle(90, Units.DEGREES)));
			normal.multiplyEquals(amount);
			Real2 point1 = line.getXY(0).plus(normal);
			Real2 point2 = line.getXY(1).plus(normal);
			SVGLine newLine = new SVGLine(point1, point2);
			if (previousPreviousLine != null) {
				Real2 intersection = newLine.getIntersection(previousLine);
				double lambda1 = previousLine.getEuclidLine().getLambda(intersection);
				double lambda2 = newLine.getEuclidLine().getLambda(intersection);
				if (lambda1 > 0 && lambda1 < 1 && lambda2 > 0 && lambda2 < 1 && !Double.isNaN(intersection.getX())) {
					intersection = newLine.getIntersection(previousPreviousLine);
				} else if (Double.isNaN(intersection.getX())) {
					intersection = newLine.getXY(0);
				}
				newPoints.add(intersection);
				previousPreviousLine = previousLine;
				previousLine = newLine;
			} else if (previousLine != null) {
				previousPreviousLine = previousLine;
				previousLine = newLine;
			} else {
				previousLine = newLine;
			}
		}
		
		SVGLine line = polygon.getLineList().get(0);
		Vector2 normal = new Vector2(line.getEuclidLine().getUnitVector());
		normal.transformBy(new Transform2(new Angle(90, Units.DEGREES)));
		normal.multiplyEquals(amount);
		Real2 point1 = line.getXY(0).plus(normal);
		Real2 point2 = line.getXY(1).plus(normal);
		SVGLine newLine = new SVGLine(point1, point2);
		if (previousPreviousLine != null) {
			Real2 intersection = newLine.getIntersection(previousLine);
			double lambda1 = previousLine.getEuclidLine().getLambda(intersection);
			double lambda2 = newLine.getEuclidLine().getLambda(intersection);
			if (lambda1 > 0 && lambda1 < 1 && lambda2 > 0 && lambda2 < 1 && !Double.isNaN(intersection.getX())) {
				intersection = newLine.getIntersection(previousPreviousLine);
			} else if (Double.isNaN(intersection.getX())) {
				intersection = newLine.getXY(0);
			}
			newPoints.add(intersection);
			previousPreviousLine = previousLine;
			previousLine = newLine;
		}
		previousPreviousLine = previousLine;
		previousLine = newLine;

		line = polygon.getLineList().get(1);
		normal = new Vector2(line.getEuclidLine().getUnitVector());
		normal.transformBy(new Transform2(new Angle(90, Units.DEGREES)));
		normal.multiplyEquals(amount);
		point1 = line.getXY(0).plus(normal);
		point2 = line.getXY(1).plus(normal);
		newLine = new SVGLine(point1, point2);
		if (previousPreviousLine != null) {
			Real2 intersection = newLine.getIntersection(previousLine);
			double lambda1 = previousLine.getEuclidLine().getLambda(intersection);
			double lambda2 = newLine.getEuclidLine().getLambda(intersection);
			if (lambda1 > 0 && lambda1 < 1 && lambda2 > 0 && lambda2 < 1 && !Double.isNaN(intersection.getX())) {
				intersection = newLine.getIntersection(previousPreviousLine);
			} else if (Double.isNaN(intersection.getX())) {
				intersection = newLine.getXY(0);
			}
			newPoints.add(intersection);
			previousPreviousLine = previousLine;
			previousLine = newLine;
		}
		
		SVGPolygon polygon2 = new SVGPolygon(polygon);
		polygon2.setReal2Array(new Real2Array(newPoints));
		return polygon2;
	}
	
	private void doOCR(SVGSVG svg, PixelIslandList nonThinnedPixelIslandList, Set<Integer> used, Map<PixelIsland, SVGLine> linesThatMayBeText) {
		throw new RuntimeException("OCR removed");
//		OCRManager manager = new OCRManager();
//		double textHeight = findMaximumHeightOfText(nonThinnedPixelIslandList, manager);
//		
//		manager = new OCRManager();
//		Map<Integer, SVGText> texts = doOCRForUnambiguousIslands(svg, nonThinnedPixelIslandList, used, linesThatMayBeText, manager, textHeight);
//		
//		outer: for (int i = 0; i < nonThinnedPixelIslandList.size(); i++) {
//			PixelIsland island = nonThinnedPixelIslandList.get(i);
//			Real2Range box = getBoundingBox(island);
//			int height = (int)(double) box.getYRange().getRange();
//			if (linesThatMayBeText.get(island) != null && texts.get(i) == null) {
//				SVGLine secondLine = null;
//				if (height <= textHeight * extraHeightForBrackets) {
//					for (int j = 0; j < nonThinnedPixelIslandList.size(); j++) {
//						if (i == j) {
//							continue;
//						}
//						PixelIsland island2 = nonThinnedPixelIslandList.get(j);
//						Real2Range box2 = getBoundingBox(island2);
//						double average = (box2.getXRange().getRange() + box.getXRange().getRange()) / 2;
////	RENAME				if (box.getXRange().intersectsWith(box2.getXRange()) && box2.getCentroid().getDistance(box.getCentroid()) < textHeight && Math.abs(box2.getXRange().getRange() - box.getXRange().getRange()) / average < splitCharacterRelativeWidthDifferenceTolerance) {
//						if (box.getXRange().intersects(box2.getXRange()) && box2.getCentroid().getDistance(box.getCentroid()) < textHeight && Math.abs(box2.getXRange().getRange() - box.getXRange().getRange()) / average < splitCharacterRelativeWidthDifferenceTolerance) {
//							if (linesThatMayBeText.get(island2) != null && linesThatMayBeText.get(island).isParallelOrAntiParallelTo(linesThatMayBeText.get(island2), new Angle(equalsSignParallelTolerance, Units.DEGREES))) {
//								box.plusEquals(box2);
//								height = (int)(double) box.getYRange().getRange();
//								secondLine = linesThatMayBeText.get(island2);
//								linesThatMayBeText.remove(island2);
//							}
//						}
//					}
//				}
//				if (textHeight != 0 && height <= textHeight * extraHeightForBrackets) {
//					SVGText text = scan(manager, box, highOCRThreshold);
//					if (text != null && text.getText().matches("[-_=\\/'\\.,\\|lI1(){}\\[\\]]")) {
//						/*SVGText[] nearestFour = new SVGText[4];
//						double[] distancesOfNearestFour = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
//						for (SVGText other : texts.values()) {
//							double distance = other.getXY().getDistance(text.getXY());
//							Double previous = null;
//							SVGText previousText = null;
//							for (int j = 0; j < 4; j++) {
//								if (previous == null && distance < distancesOfNearestFour[j]) {
//									previous = distancesOfNearestFour[j];
//									previousText = nearestFour[j];
//									distancesOfNearestFour[j] = distance;
//									nearestFour[j] = other;
//									continue;
//								}
//								if (previous != null) {
//									double oldPrevious = previous;
//									SVGText oldPreviousText = previousText;
//									previous = distancesOfNearestFour[j];
//									previousText = nearestFour[j];
//									distancesOfNearestFour[j] = oldPrevious;
//									nearestFour[j] = oldPreviousText;
//								}
//							}
//						}
//						for (SVGText other : nearestFour) {
//							double average = (text.getFontSize() + other.getFontSize()) / 2;
//							if (Math.abs(text.getY() - other.getY()) < textJoiningPositionToleranceRelativeToFontSize * average) {
//								svg.appendChild(text);
//								continue outer;
//							}
//						}
//						SVGText nearest = null;
//						double distanceOfNearest = Double.MAX_VALUE;
//						for (SVGText other : texts.values()) {
//							double distance = other.getXY().getDistance(text.getXY());
//							if (distance < distanceOfNearest) {
//								distanceOfNearest = distance;
//								nearest = other;
//							}
//						}
//						if (nearest != null) {
//							double average = (text.getFontSize() + nearest.getFontSize()) / 2;
//							if (Math.abs(text.getY() - nearest.getY()) < textJoiningPositionToleranceRelativeToFontSize * average) {
//								svg.appendChild(text);
//								continue outer;
//							}
//						}*/
//						SVGText nearest = null;
//						SVGText secondNearest = null;
//						double distanceOfNearest = Double.MAX_VALUE;
//						double distanceOfSecondNearest = Double.MAX_VALUE;
//						for (SVGText other : texts.values()) {
//							double distance = other.getXY().getDistance(text.getXY());
//							if (distance < distanceOfNearest) {
//								distanceOfSecondNearest = distanceOfNearest;
//								secondNearest = nearest;
//								distanceOfNearest = distance;
//								nearest = other;
//							} else if (distance < distanceOfSecondNearest) {
//								distanceOfSecondNearest = distance;
//								secondNearest = other;
//							}
//						}
//						if (nearest != null) {
//							double average = (text.getFontSize() + nearest.getFontSize()) / 2;
//							double minimum = Math.min(text.getFontSize(), nearest.getFontSize());
//							double maximum = Math.max(text.getFontSize(), nearest.getFontSize());
//							if (Math.abs(text.getY() - nearest.getY()) < textJoiningPositionToleranceRelativeToFontSize * average && minimum / maximum > textJoiningFontSizeTolerance) {
//								svg.appendChild(text);
//								continue outer;
//							}
//						}
//						if (secondNearest != null) {
//							double average = (text.getFontSize() + secondNearest.getFontSize()) / 2;
//							double minimum = Math.min(text.getFontSize(), secondNearest.getFontSize());
//							double maximum = Math.max(text.getFontSize(), secondNearest.getFontSize());
//							if (Math.abs(text.getY() - secondNearest.getY()) < textJoiningPositionToleranceRelativeToFontSize * average && minimum / maximum > textJoiningFontSizeTolerance) {
//								svg.appendChild(text);
//								continue outer;
//							}
//						}
//						manager.cancel(text);
//					}
//				}
//				svg.appendChild(linesThatMayBeText.get(island));
//				if (secondLine != null) {
//					svg.appendChild(secondLine);
//				}
//			}
//		}
//		
//
//		manager.handleAmbiguousTexts(textJoiningPositionToleranceRelativeToFontSize, textJoiningFontSizeTolerance);
	}

	private Real2Range getBoundingBox(PixelIsland temp) {
		return temp.getBoundingBox().getReal2RangeExtendedInX(0, 1).getReal2RangeExtendedInY(0, 1);
	}

//	private Map<Integer, SVGText> doOCRForUnambiguousIslands(SVGSVG svg, PixelIslandList nonThinnedPixelIslandList, Set<Integer> used, Map<PixelIsland, SVGLine> linesThatMayBeText, OCRManager manager, double textHeight) {
//		throw new RuntimeException("OCR removed");
//		//Set<Integer> partOfSplitCharacter = new HashSet<Integer>();
//		Map<Integer, SVGText> texts = new HashMap<Integer, SVGText>();
//		//Map<Integer, SVGText> circlesForPercents = new HashMap<Integer, SVGText>();
//		for (int i = 0; i < nonThinnedPixelIslandList.size(); i++) {
//			if (texts.get(i) != null) {
//				continue;
//			}
//			PixelIsland island = nonThinnedPixelIslandList.get(i);
//			Real2Range box = getBoundingBox(island);
//			Real2Range originalBox = new Real2Range(getBoundingBox(island));
//			//int height = (int)(double) box.getYRange().getRange();
//			if (textHeight != 0 && getBoundingBox(island).getYRange().getRange() <= textHeight * extraHeightForBrackets) {
//				boolean skip = false;
//				/*if (partOfSplitCharacter.contains(i)) {
//					continue;
//				}*/
//				if (texts.get(i) != null) {
//					continue;
//				}
//				if (used.contains(i)) {
//					skip = true;
//				}
//				List<Integer> otherParts = new ArrayList<Integer>();
//				for (int j = 0; j < nonThinnedPixelIslandList.size(); j++) {
//					if (i == j) {
//						continue;
//					}
//					PixelIsland island2 = nonThinnedPixelIslandList.get(j);
//					Real2Range box2 = getBoundingBox(island2);
//					//skip &= !lookForMultiIslandCharacter(svg, used, linesThatMayBeText, manager, textHeight, circlesForPercents, island, island2, box, box2, j);
//					if (tittleTest(box, textHeight)) {
//// RENAME				if (box.getXRange().intersectsWith(box2.getXRange()) && box2.getCentroid().getDistance(box.getCentroid()) < textHeight) {
//						if (box.getXRange().intersects(box2.getXRange()) && box2.getCentroid().getDistance(box.getCentroid()) < textHeight) {
//							boolean tittleSizeTest = tittleTest(box2, textHeight);
//							if (tittleSizeTest) {
//								box.plusEquals(box2);
//								//used.add(i);
//								//used.add(j);
//								//linesThatMayBeText.remove(island);
//								//linesThatMayBeText.remove(island2);
//								//texts.put(j, null);
//								//partOfSplitCharacter.add(j);
//								otherParts.add(j);
//								skip = false;
//								break;
//							} else {
//								SVGText text = scan(manager, box2, highOCRThreshold);
//								manager.cancel(text);
//								if (text != null && text.getText().equals(",")) {
//									box.plusEquals(box2);
//									//used.add(i);
//									//used.add(j);
//									//linesThatMayBeText.remove(island);
//									//linesThatMayBeText.remove(island2);
//									//texts.put(j, null);
//									//partOfSplitCharacter.add(j);
//									otherParts.add(j);
//									skip = false;
//									break;
//								}
//							}
//						}
//					} else if (box2.getYRange().getRange() < box.getYRange().getRange() && box2 != box) {
//						if (box2.intersectionWith(box) != null) {
//							//skip &= !lookForPercentageSign(svg, used, linesThatMayBeText, manager, circlesForPercents, island, box, box2, j);
//							SVGText circle = texts.get(j);
//							if (circle == null) {
//								SVGText text = scan(manager, box2, highOCRThreshold);
//								manager.cancel(text);
//								if (text != null && text.getText().matches("O|0|o")) {
//									box.plusEquals(box2);
//									//used.add(j);
//									//circlesForPercents.put(j, text);
//									//linesThatMayBeText.remove(island);
//									otherParts.add(j);
//									skip = false;
//								}
//							} else if (circle.getText().matches("O|0|o")) {
//								//svg.removeChild(circle);
//								box.plusEquals(box2);
//								//used.add(j);
//								//linesThatMayBeText.remove(island);
//								otherParts.add(j);
//								skip = false;
//							}
//						} else {
//							//skip &= !lookForLowerCaseI(used, linesThatMayBeText, textHeight, island, island2, box, box2, j);
//							Real2 middleOfTop = new Real2(box.getCentroid().getX(), box.getYMin());
//							boolean tittleSizeTest = tittleTest(box2, textHeight);
//// RENAMED					if (box.getXRange().intersectsWith(box2.getXRange()) && tittleSizeTest && box2.getCentroid().getDistance(middleOfTop) < textHeight * tittleDistance) {
//							if (box.getXRange().intersects(box2.getXRange()) && tittleSizeTest && box2.getCentroid().getDistance(middleOfTop) < textHeight * tittleDistance) {
//								box.plusEquals(box2);
//								//used.add(j);
//								//linesThatMayBeText.remove(island);
//								//linesThatMayBeText.remove(island2);
//								otherParts.add(j);
//								skip = false;
//								break;
//							}
//						}
//					}
//				}
//				if (!skip) {
//					SVGText text = scan(manager, box, highOCRThreshold);
//					if (text != null && (box.isEqualTo(originalBox, 0.000001) || text.getText().matches("[ij;:%]"))) {
//						for (Integer part : otherParts) {
//							SVGText other = texts.get(part);
//							if (other != null ){
//								manager.cancel(other);
//								svg.removeChild(other);
//							}
//							used.add(part);
//							texts.put(part, text);
//						}
//						svg.appendChild(text);
//						texts.put(i, text);
//						/*if (text.getText().matches("O|0|o")) {
//							circlesForPercents.put(i, text);
//						}*/
//						/*for (Pixel pixel : island.getPixelList()) {
//							imageProcessor.getImage().setRGB(pixel.getInt2().getX(), pixel.getInt2().getY(), -1);						
//						}*/
//						used.add(i);
//					} else if (linesThatMayBeText.get(island) == null && !box.isEqualTo(originalBox, 0.000001)) {
//						text = scan(manager, originalBox, highOCRThreshold);
//						if (text != null) {
//							svg.appendChild(text);
//							texts.put(i, text);
//							used.add(i);
//						}
//					}
//				}
//			}
//		}
//		return texts;
//	}

//	private boolean tittleTest(Real2Range box2, double textHeight) {
//		return box2.getXRange().getRange() < textHeight * tittleSize && box2.getYRange().getRange() < textHeight * tittleSize;
//	}

	// deleted because of OCR removal
//	private double findMaximumHeightOfText(PixelIslandList nonThinnedPixelIslandList, OCRManager manager) {
//		double largestHeight = 0;
//		for (PixelIsland island : nonThinnedPixelIslandList) {
//			Real2Range box = getBoundingBox(island);
//			SVGText text = scan(manager, box, lowOCRThreshold);
//			double height = box.getYRange().getRange();
//			if (text != null && height > largestHeight && text.getText().matches("[a-hkm-zA-HKM-Z2-9]")) {//!text.getText().matches("\\||\\[|\\]|I|l|1|/")) {
//				largestHeight = height;
//			}
//		}
//		return largestHeight;
//	}

	private Map<PixelIsland, SVGLine> handleSingleLines(PixelGraphList graphs, PixelIslandList nonThinnedPixelIslandList, Set<Integer> used) {
		List<PixelIsland> difficultLines = new ArrayList<PixelIsland>();
		Map<PixelIsland, SVGLine> linesThatMayBeText = new HashMap<PixelIsland, SVGLine>();
		for (int i = 0; i < graphs.size(); i++) {
			PixelGraph graph = graphs.get(i);
			PixelEdgeList edges = graph.getOrCreateEdgeList();
			List<PixelEdge> longEdges = new ArrayList<PixelEdge>();
			for (PixelEdge edge : edges) {
				if (edge.size() > 3 || edges.size() == 1) {
					longEdges.add(edge);
				}
			}
			if (longEdges.size() <= 1) {
				PixelSegmentList segments = new PixelSegmentList();
				if (longEdges.size() == 1) {
					segments = longEdges.get(0).getOrCreateSegmentList(6.0, 19, 0.7);
				}
				if (segments.size() <= 1) {
					used.add(i);
					PixelIsland pixelIsland = nonThinnedPixelIslandList.get(i);
					if (segments.size() == 1) {
						PrincipalComponentAnalysis pc = new PrincipalComponentAnalysis();
						PixelList pixels = pixelIsland.getPixelList();
						pc.setup(pixels.size(), 2);
						for (Pixel p : pixels) {
							pc.addSample(new double[]{p.getInt2().getX(), p.getInt2().getY()});
						}
						pc.computeBasis(2);
						SVGLine initialLine = segments.get(0).getSVGLine();
						
						if (pc.getEnergyContent(1) < pc.getEnergyContent(0) * energyContentThreshold) {
							growLine(initialLine, pixelIsland);
							//svg.appendChild(initialLine);
							linesThatMayBeText.put(pixelIsland, initialLine);
						} else {
							difficultLines.add(pixelIsland);
						}
					} else {
						//svg.appendChild(new SVGLine(new Real2(thinnedPixelIslandList.get(i).getPixelList().get(0).getInt2()), new Real2(thinnedPixelIslandList.get(i).getPixelList().get(1).getInt2())));
						difficultLines.add(pixelIsland);
					}
					//smallLines.add(segments.get(0).getSVGLine());
				}
			}
		}
		/*UnionFind<SVGLine> smallLineCollections = UnionFind.create(smallLines);
		for (int i = 0; i < smallLines.size(); i++) {
			SVGLine line1 = smallLines.get(i);
			double[] firstPrimary = primaryBasisVectors.get(i);
			for (int j = 0; j < smallLines.size(); j++) {
				SVGLine line2 = smallLines.get(j);
				double[] secondPrimary = primaryBasisVectors.get(i);
				if (new Vector2(firstPrimary[0], firstPrimary[1]).isParallelTo(new Vector2(firstPrimary[0], firstPrimary[1]), 0.1)) {
					
				}
			}
		}*/
		averageSlopes(linesThatMayBeText);
		handleDifficultLines(difficultLines, linesThatMayBeText);
		return linesThatMayBeText;
	}

	private void averageSlopes(Map<PixelIsland, SVGLine> linesThatMayBeText) {
		List<SVGLine> copiedLines = new ArrayList<SVGLine>();
		for (SVGLine line : linesThatMayBeText.values()) {
			copiedLines.add(new SVGLine(line));
		}
		for (SVGLine line1 : linesThatMayBeText.values()) {
			double smallestDistance = Double.MAX_VALUE;
			double secondSmallestDistance = Double.MAX_VALUE;
			SVGLine nearestLine = null;
			SVGLine secondNearestLine = null;
			Real2 midPoint1 = line1.getMidPoint();
			for (SVGLine line2 : copiedLines) {
				if (line1.getXY(0).isEqualTo(line2.getXY(0), 0.0000000001) && line1.getXY(1).isEqualTo(line2.getXY(1), 0.0000000001)) {
					continue;
				}
				Real2 midPoint2 = line2.getMidPoint();
				double distance = midPoint1.getDistance(midPoint2);
				if (distance < smallestDistance) {
					secondSmallestDistance = smallestDistance;
					secondNearestLine = nearestLine;
					smallestDistance = distance;
					nearestLine = line2;
				} else if (distance < secondSmallestDistance) {
					secondSmallestDistance = distance;
					secondNearestLine = line2;
				}
			}
			if (nearestLine != null && secondNearestLine != null) {
				if (nearestLine.getLength() / line1.getLength() >= relativeLineLengthForAngleNormalisation && nearestLine.getLength() / line1.getLength() <= 1 / relativeLineLengthForAngleNormalisation && secondNearestLine.getLength() / line1.getLength() >= relativeLineLengthForAngleNormalisation && secondNearestLine.getLength() / line1.getLength() <= 1 / relativeLineLengthForAngleNormalisation) {
					if ((nearestLine.overlapsWithLine(line1, overlapEpsilon) || line1.overlapsWithLine(nearestLine, overlapEpsilon)) && (secondNearestLine.overlapsWithLine(line1, overlapEpsilon) || line1.overlapsWithLine(secondNearestLine, overlapEpsilon))) {
						changeSlopeBasedOnNearbyLines(line1, nearestLine, secondNearestLine);
					}
				}
			}
		}
	}

	private void changeSlopeBasedOnNearbyLines(SVGLine line1, SVGLine nearestLine, SVGLine secondNearestLine) {
		Line2 horizontal = new Line2(new Real2(0, 0), new Real2(1, 0));
		Angle firstAngle = nearestLine.getEuclidLine().getAngleMadeWith(horizontal);
		firstAngle.normalizeTo2Pi();
		Angle secondAngle = secondNearestLine.getEuclidLine().getAngleMadeWith(horizontal);
		secondAngle.normalizeTo2Pi();
		Angle secondAngle2 = secondAngle.plus(new Angle(180, Units.DEGREES));
		secondAngle2.normalizeTo2Pi();
		Angle thisAngle = line1.getEuclidLine().getAngleMadeWith(horizontal);
		
		double firstAngleDiv = firstAngle.subtract(thisAngle).getAngle() / Math.PI;
		double secondAngleDiv = secondAngle.subtract(thisAngle).getAngle() / Math.PI;
		double firstAngleDiff = Math.abs(Math.round(firstAngleDiv) - firstAngleDiv);
		double secondAngleDiff = Math.abs(Math.round(secondAngleDiv) - secondAngleDiv);
		if (firstAngleDiff * Math.PI < angleJitterThreshold && secondAngleDiff * Math.PI < angleJitterThreshold) {
			Angle rotate1 = firstAngle.plus(secondAngle).multiplyBy(0.5).subtract(thisAngle);
			Angle rotate2 = firstAngle.plus(secondAngle2).multiplyBy(0.5).subtract(thisAngle);
			double rotate1Div = rotate1.getAngle() / Math.PI;
			double rotate2Div = rotate2.getAngle() / Math.PI;
			double rotate1Diff = Math.abs(Math.round(rotate1Div) - rotate1Div);
			double rotate2Diff = Math.abs(Math.round(rotate2Div) - rotate2Div);
			Angle rotate = (rotate1Diff < rotate2Diff ? rotate1 : rotate2);
			LOG.trace("Rotating " + line1 + " by " + rotate);
			line1.applyTransformPreserveUprightText(Transform2.getRotationAboutPoint(rotate.multiplyBy(-1), line1.getMidPoint()));
		}
	}

	private void handleDifficultLines(List<PixelIsland> difficultLines, Map<PixelIsland, SVGLine> linesThatMayBeText) {
		for (PixelIsland ambiguous : difficultLines) {
			double smallestDistance = Double.MAX_VALUE;
			Pixel centre = ambiguous.getPixelList().getCentralPixel();
			SVGLine nearestLine = null;
			for (SVGLine line : linesThatMayBeText.values()) {
				Real2 midPoint = line.getMidPoint();
				double distance = new Real2(centre.getInt2()).getDistance(midPoint);
				if (distance < smallestDistance) {
					smallestDistance = distance;
					nearestLine = line;
				}
			}
			SVGLine initialLine = new SVGLine(new Real2(centre.getInt2()).plus(nearestLine.getEuclidLine().getUnitVector().multiplyBy(0.5)), new Real2(centre.getInt2()).subtract(nearestLine.getEuclidLine().getUnitVector().multiplyBy(0.5)));
			growLine(initialLine, ambiguous);
			LOG.trace("Making new line " + initialLine);
			//svg.appendChild(initialLine);
			linesThatMayBeText.put(ambiguous, initialLine);
		}
	}

	private void makeListsHaveSameOrder(PixelIslandList thinnedPixelIslandList, PixelIslandList nonThinnedPixelIslandList) {
		for (int i = 0; i < thinnedPixelIslandList.size(); i++) {
			Real2Range boundingBox = getBoundingBox(nonThinnedPixelIslandList.get(i));
			if (!thinnedPixelIslandList.get(i).fitsWithin(boundingBox)) {
				for (int j = i + 1; j < nonThinnedPixelIslandList.size(); j++) {
					boundingBox = getBoundingBox(nonThinnedPixelIslandList.get(j));
					if (thinnedPixelIslandList.get(i).fitsWithin(boundingBox)) {
						Collections.swap(nonThinnedPixelIslandList.getList(), i, j);
					}
				}
			}
		}
	}

	//removed because of OCR removal
//	private SVGText scan(OCRManager manager, Real2Range box, int highThreshold) {
//		int xMin = (int)(double) box.getXMin();
//		int yMin = (int)(double) box.getYMin();
//		int width = (int)(double) box.getXRange().getRange();
//		int height = (int)(double) box.getYRange().getRange();
//		BufferedImage islandImage = imageProcessor.getImage().getSubimage(xMin, yMin, width, height);
//		SVGText text = manager.scan(islandImage, new Real2Range(new RealRange(xMin, xMin + width), new RealRange(yMin, yMin + height)), 128, highThreshold);
//		return text;
//	}

	private void growLine(SVGLine initialLine, PixelIsland pixelIsland) {
		Real2 point = initialLine.getEuclidLine().createPointOnLine(-1.0, 0);
		while (stillInIsland(pixelIsland, point)) {
			initialLine.setXY(point, 0);
			point = initialLine.getEuclidLine().createPointOnLine(-1.0, 0);
		}
		point = initialLine.getEuclidLine().createPointOnLine(-1.0, 1);
		while (stillInIsland(pixelIsland, point)) {
			initialLine.setXY(point, 1);
			point = initialLine.getEuclidLine().createPointOnLine(-1.0, 1);
		}
	}
	
	private boolean stillInIsland(PixelIsland pixelIsland, Real2 real2) {
		for (Pixel p : pixelIsland) {
			if (real2.getX() >= p.getInt2().getX() && real2.getX() <= p.getInt2().getX() + 1 && real2.getY() >= p.getInt2().getY() && real2.getY() <= p.getInt2().getY() + 1) {
				return true;
			}
		}
		return false;
	}

	private void onwardsToLimitingPixel(List<ListIterator<Pixel>> outlinerIterators, int i, Pixel limit, int limitOfTouchingPixels, PixelList output, boolean skipping) {
		if (i >= outlinerIterators.size()) {
			return;
		}
		Pixel currentPixel = outlinerIterators.get(i).next();
		Pixel startPixel = currentPixel;
		while (true) {
			if (!skipping && i == outlinerIterators.size() - 1) {
				output.add(currentPixel);
			}
			if (limit != null && (currentPixel != startPixel || !skipping) && currentPixel.isNeighbour(limit)) {
				if (skipping && i == outlinerIterators.size() - 1) {
					addCap(output, startPixel.getInt2(), currentPixel.getInt2());
				}
				onwardsToLimitingPixel(outlinerIterators, i + 1, currentPixel, 4, output, skipping);
				return;
			}
			if (!skipping && currentPixel.getOrthogonalNeighbours(currentPixel.getIsland()).size() != 2) {
				Pixel rootPixel = currentPixel;
				onwardsToLimitingPixel(outlinerIterators, i + 1, currentPixel, 4, output, false);
				int numberOfTouchingPixels = 1;
				PixelList potentialOutput = new PixelList();
				outer: while (true) {
					if ((currentPixel.equals(rootPixel) && numberOfTouchingPixels >= 2)) {
						break;
					} else if (currentPixel.isOrthogonalNeighbour(rootPixel) && numberOfTouchingPixels >= 3) {//currentPixel.getOrthogonalNeighbours(currentPixel.getIsland()).size() == 2) {
						while (true) {
							currentPixel = outlinerIterators.get(i).next();
							if (currentPixel == rootPixel) {
								numberOfTouchingPixels++;
								potentialOutput.add(currentPixel);
								break outer;
							}
							if (!currentPixel.isNeighbour(rootPixel) && currentPixel != rootPixel) {
								outlinerIterators.get(i).previous();
								break outer;
							}
							numberOfTouchingPixels++;
							potentialOutput.add(currentPixel);
						}
					}
					numberOfTouchingPixels++;
					currentPixel = outlinerIterators.get(i).next();
					potentialOutput.add(currentPixel);
				}
				if (numberOfTouchingPixels <= limitOfTouchingPixels) {
					if (i == outlinerIterators.size() - 1) {
						output.addAll(potentialOutput);
					}
					onwardsToLimitingPixel(outlinerIterators, i + 1, currentPixel, numberOfTouchingPixels + 2, output, false);
				} else {
					int startAfterSkip = potentialOutput.size() - (limitOfTouchingPixels % 2 == 0 ? 0 : 1) - limitOfTouchingPixels / 2;
					if (i == outlinerIterators.size() - 1) {
						for (int pixel = 0; pixel < limitOfTouchingPixels / 2 - 1; pixel++) {
							output.add(potentialOutput.get(pixel));
						}
						for (int pixel = startAfterSkip; pixel < potentialOutput.size(); pixel++) {
							output.add(potentialOutput.get(pixel));
						}
					}
					if (limitOfTouchingPixels / 2 - 1 > 1) {
						onwardsToLimitingPixel(outlinerIterators, i + 1, potentialOutput.get(limitOfTouchingPixels / 2 - 2), 4, output, false);
					}
					onwardsToLimitingPixel(outlinerIterators, i + 1, potentialOutput.get(startAfterSkip), 0, output, true);
					if (startAfterSkip < potentialOutput.size() - 1) {
						onwardsToLimitingPixel(outlinerIterators, i + 1, potentialOutput.get(potentialOutput.size() - 1), 4, output, false);
					}
				}
			}
			if (outlinerIterators.get(i).hasNext()) {
				currentPixel = outlinerIterators.get(i).next();
			} else {
				break;
			}
		}
		onwardsToLimitingPixel(outlinerIterators, i + 1, null, 4, output, false);
	}

	private List<Pixel> findStartingPixels(List<PixelList> outerRings, PixelList innerRing) {
		List<Pixel> pixels = new ArrayList<Pixel>();
		if (outerRings.size() == 1) {
			pixels.add(outerRings.get(0).findExtremePixels().get(0));
			return pixels;
		}
		Int2 direction1;
		Int2 direction2;
		//for (int i = 0; i < allRings.get(0).size(); i++) {
		innermost: for (Pixel pixel : outerRings.get(0)) {
			pixel.clearNeighbours();
			if (pixel.getOrCreateNeighbours(innerRing.getPixelIsland()).size() == 0 && pixel.getOrthogonalNeighbours(innerRing.getPixelIsland()).size() != 2) {
				pixel.clearNeighbours();
				pixel.setIsland(outerRings.get(0).getPixelIsland());
				continue;
			}
			pixel.clearNeighbours();
			pixel.setIsland(outerRings.get(0).getPixelIsland());
			pixels.add(pixel);
			for (int i = 0; i < outerRings.size(); i++) {
				PixelList neighbours = pixel.getOrthogonalNeighbours(outerRings.get(i).getPixelIsland());
				if (neighbours.size() == 2) {
					if (neighbours.get(0).isNeighbour(neighbours.get(1))) {
						Int2 dir1 = neighbours.get(0).getInt2().subtract(pixel.getInt2());
						Int2 dir2 = neighbours.get(1).getInt2().subtract(pixel.getInt2());
						direction1 = new Int2(-(dir1.getX() == 0 ? dir2.getX() : dir1.getX()), -(dir1.getY() == 0 ? dir2.getY() : dir1.getY()));
						direction2 = direction1;
					} else {
						Int2 dir = neighbours.get(0).getInt2().subtract(pixel.getInt2());
						direction1 = new Int2((dir.getX() != 0 ? 0 : 1), (dir.getY() != 0 ? 0 : 1));
						direction2 = new Int2((dir.getX() != 0 ? 0 : -1), (dir.getY() != 0 ? 0 : -1));
					}
					if (i < outerRings.size() - 1) {
						Pixel candidate1 = outerRings.get(i + 1).getPixelByCoordinate(pixel.getInt2().plus(direction1));
						Pixel candidate2 = outerRings.get(i + 1).getPixelByCoordinate(pixel.getInt2().plus(direction2));
						pixel = (candidate1 == null ? candidate2 : candidate1);
						if (pixel == null) {
							pixels.clear();
							continue innermost;
						}
						pixels.add(pixel);
					} else {
						Pixel candidate1 = outerRings.get(i - 1).getPixelByCoordinate(pixel.getInt2().plus(direction1));
						Pixel candidate2 = outerRings.get(i - 1).getPixelByCoordinate(pixel.getInt2().plus(direction2));
						if (candidate1 != null && candidate2 != null) {
							pixels.clear();
							continue innermost;
						}
					}
					/*PixelList neighboursInThisRing = pixelInThisRing.getOrthogonalNeighbours(allRings.get(i).getPixelIsland());
					if (neighboursInThisRing.size() != 2) {
						continue innermost;
					}*/
				} else {
					pixels.clear();
					continue innermost;
				}
			}
			break;
		}
		return pixels;
	}
	
	public void addCap(PixelList output, Int2 start, Int2 end) {
		int x0 = start.getX();
		int x1 = end.getX();
		int y0 = start.getY();
		int y1 = end.getY();
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;
		int err = dx - dy;
		int e2;
		
		while (true) {
			output.add(new Pixel(x0, y0));
			if (x0 == x1 && y0 == y1) {
				break;
			}
			e2 = 2 * err;
			if (e2 > -dy) {
				err = err - dy;
				x0 = x0 + sx;
			}
			if (e2 < dx) {
				err = err + dx;
				y0 = y0 + sy;
			}
		}
	}
	
	public PixelGraphList getOrCreateGraphList(File inputFile) {
		this.setInputFile(inputFile);
		return this.getOrCreateGraphList();
	}

	public void writeBinarizedFile(File outputFile) {
		BufferedImage binarizedImage = getImageProcessor().getBinarizedImage();
		ImageIOUtil.writeImageQuietly(binarizedImage, outputFile);
	}

	public void scanThresholds(int[] thresholds) {
		if (getBase() == null) {
			throw new RuntimeException("null base");
		}
		if (getOutputDir() == null || !getOutputDir().exists()) {
			throw new RuntimeException("null or non-existent outputDir: "+getOutputDir());
		}
		for (int threshold : thresholds) {
			getImageProcessor().setThreshold(threshold);
			setThinning(null);
			File file = ensureInputFile();
			readAndProcessInputFile(file);
			writeBinarizedFile(new File(getOutputDir(), "binarized"+threshold+".png"));
		}
	}

	private File ensureInputFile() {
		File file = new File(inputDir, getBase()+".png");
		return file;
	}

	public static void main(String[] args) throws Exception {
		DiagramAnalyzer analyzer = new DiagramAnalyzer();
		analyzer.setInputFile(new File(args[0]));
		SVGUtil.debug(analyzer.convertPixelsToSVG(), new FileOutputStream(new File(args[1])), 0);
	}

	public DiagramAnalyzer setInputDir(File inputDir) {
		getImageProcessor().setInputDir(inputDir);
		return this;
	}
	
	public File getInputDir() {
		return getImageProcessor().getInputDir();
	}

	public DiagramAnalyzer setThreshold(int threshold) {
		getImageProcessor().setThreshold(threshold);
		return this;
	}

	public void readAndProcessInputFile() {
		ensureImageProcessor();
		File inputFile = imageProcessor.getInputFile();
		if (inputFile == null) {
			if (imageProcessor.getInputDir() != null &&
				imageProcessor.getBase() != null &&
				imageProcessor.getInputSuffix() != null) {
					inputFile = new File(imageProcessor.getInputDir(), imageProcessor.getBase() +
					imageProcessor.getInputSuffix());
			} else {
				throw new RuntimeException("no image file read; Cannot create default input file");
			}
		}
		readAndProcessInputFile(inputFile);
	}

	public PixelIslandList writeLargestPixelIsland() {
		PixelIslandList pixelIslandList = getOrCreateSortedPixelIslandList();
		SVGSVG.wrapAndWriteAsSVG(getLargestIsland(pixelIslandList).getOrCreateSVGG(), new File(getOutputDir(), "largestIsland.svg"));
		return pixelIslandList;
	}

	private PixelIsland getLargestIsland(PixelIslandList pixelIslandList) {
		return pixelIslandList.get(0);
	}

	public PixelIsland getLargestIsland() {
		PixelIslandList islandList = getOrCreateSortedPixelIslandList();
		return islandList == null ? null : islandList.get(0);
	}

	public void scanThresholdsAndWriteBinarizedFiles(File targetDir, int[] thresholds, File chemFile) {
		for (int threshold : thresholds) {
			setThreshold(threshold);
			readAndProcessInputFile(chemFile);
			File binarizedFile = new File(targetDir, "binarized" + threshold + ".png");
			writeBinarizedFile(binarizedFile);
		}
	}

	/** convenience routine for creating binary pixelList.
	 * 
	 * @param imageFile
	 * @return
	 */
	public PixelIslandList createDefaultPixelIslandList(File imageFile) {
		ImageProcessor imageProcessor = getImageProcessor();
		imageProcessor.setBinarize(true);
		imageProcessor.setThreshold(180); // turns blue to black
		imageProcessor.setThinning(new ZhangSuenThinning());
		imageProcessor.setDebug(true);
		imageProcessor.readAndProcessFile(imageFile);
		getOrCreateGraphList(imageFile);
		PixelIslandList pixelIslandList = getOrCreatePixelIslandList();
		return pixelIslandList;
	}

	/** convenience routine for creating binary pixel rings.
	 * aggrgates pixel rings - may be poor idea
	 * @param imageFile
	 * @return
	 */
	public PixelRingList createDefaultPixelRings() {
		PixelIslandList pixelIslandList = createDefaultPixelIslandList1();
		PixelRingList pixelRingList = null;
		pixelRingList = pixelIslandList.createAggregatedInternalPixelRingList();
// alternative method		
		PixelIsland pixelIsland0 = pixelIslandList.get(0);
//		pixelRingList = pixelIsland0 == null ? new PixelRingList() : pixelIsland0.getOrCreateInternalPixelRings();
		return pixelRingList;
	}

	/** convenience routine for creating binary pixel rings.
	 * 
	 * @param imageFile
	 * @return
	 */
	public LocalSummitList createDefaultPixelRingListList() {
		PixelIslandList pixelIslandList = createDefaultPixelIslandList1();
		LocalSummitList pixelRingListList = null;
		pixelRingListList = pixelIslandList.createInternalPixelRingListList();
		return pixelRingListList;
	}

	/** default image processing .
	 * threshold 180 and removal of all islands < 10*10 
	 * mainly for testing
	 * 
	 * @param imageFile
	 * @return
	 */
	public PixelIslandList createDefaultPixelIslandList1() {
		ImageProcessor imageProcessor = getImageProcessor();
		imageProcessor.setBinarize(true);
		imageProcessor.setThreshold(180); // turns blue to black
		imageProcessor.setThinning(null);
		imageProcessor.setDebug(false);
		imageProcessor.readAndProcessFile();
		PixelIslandList pixelIslandList = getOrCreatePixelIslandList();
		pixelIslandList.removeIslandsWithBBoxesLessThan(new Real2Range(new RealRange(0, 10), new RealRange(0, 10)));
		pixelIslandList.sortBySizeDescending();
		return pixelIslandList;
	}
	
	/** convenience routine for creating binary pixel rings.
	 * 
	 * @param imageFile
	 * @return
	 */
	public PixelRingList createDefaultPixelRings(BufferedImage image) {
		ImageProcessor imageProcessor = getImageProcessor();
		imageProcessor.setBinarize(true);
		imageProcessor.setThreshold(180); // turns blue to black
		imageProcessor.setThinning(null);
		imageProcessor.setDebug(true);
		imageProcessor.processImage(image);
		PixelIslandList pixelIslandList = getOrCreatePixelIslandList();
		pixelIslandList.removeIslandsWithBBoxesLessThan(new Real2Range(new RealRange(0, 10), new RealRange(0, 10)));
		pixelIslandList.sortBySizeDescending();
		PixelIsland island0 = pixelIslandList.get(0);
		PixelRingList pixelRingList = island0.getOrCreateInternalPixelRings();
		return pixelRingList;
	}

	public void createAxialPixelFrequencies() {
		AxialPixelFrequencies frequencies = getImageProcessor().getMainProcessor().getOrCreateAxialPixelFrequencies();
		
	}

	public AxialPixelFrequencies getAxialPixelFrequencies() {
		return getImageProcessor().getMainProcessor().getOrCreateAxialPixelFrequencies();
	}

	public void writeImage(File outfile) {
		ImageIOUtil.writeImageQuietly(getImageProcessor().getImage(), outfile);
	}

	/** create default monochrome DiagramAnalyzer.
	 * 
	 * @param pixelList
	 * @return
	 */
	public static DiagramAnalyzer createDiagramAnalyzer(PixelList pixelList) {
		DiagramAnalyzer diagramAnalyzer = null;
		if (pixelList != null && pixelList.size() > 0) {
			Int2Range bbox = pixelList.getIntBoundingBox();
			diagramAnalyzer = DiagramAnalyzer.createDiagramAnalyzer(
				bbox.getXRange().getMax() + 1, // the +1 are because range is inclusive
				bbox.getYRange().getMax() + 1,
				pixelList);
		}
		return diagramAnalyzer;
	}

	/** creates binary image from pixels and wraps in new diagramAnalyzer.
	 * 
	 * @param width
	 * @param height
	 * @param pixelList
	 * @return
	 */
	public static DiagramAnalyzer createDiagramAnalyzer(int width, int height, PixelList pixelList) {
		BufferedImage image = createDefaultImage(width, height, pixelList);
		LOG.debug("b on w; "+ImageUtil.createString(image));
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.setDebug(true);
		diagramAnalyzer.setThinning(null);
		// perhaps do NOT binarize an already binarized diagram Or maybe?
		diagramAnalyzer.setBinarize(false);
//		diagramAnalyzer.setBinarize(true);
		diagramAnalyzer.getImageProcessor().processImage(image);
		LOG.debug("image "+ImageUtil.createString(diagramAnalyzer.getImage()));
		return diagramAnalyzer;
	}

	public static BufferedImage createDefaultImage(int width, int height, PixelList pixelList) {
		BufferedImage image = ImageUtil.createARGBBufferedImage(width, height);
		populateImage(pixelList, image);
		return image;
	}

	public BufferedImage createDefaultImage(PixelList pixelList) {
		Int2Range bbox = pixelList.getIntBoundingBox();
		BufferedImage image = ImageUtil.createARGBBufferedImage(
				bbox.getXRange().getMax(),
				bbox.getYRange().getMax());
		populateImage(pixelList, image);
		getImageProcessor().setImage(image);
		return image;
	}

	private static void populateImage(PixelList pixelList, BufferedImage image) {
		ImageUtil.setImageWhite(image);
		for (Pixel pixel : pixelList) {
			Int2 xy = pixel.getInt2();
			int x = xy.getX();
			int y = xy.getY();
			if (x < image.getWidth() && y < image.getHeight()) {
				image.setRGB(x, y, ColorUtilities.RGB_BLACK);
			}
		}
	}

	public DiagramAnalyzer setDebug(boolean b) {
		ensureImageProcessor().setDebug(b);
		return this;
	}

	/** requires an image.
	 * 
	 */
	public PixelIslandList createDefaultPixelIslandList() {
		BufferedImage image = getImage();
		if (image == null) {
			throw new RuntimeException("must have created image");
		}
		return imageProcessor.getOrCreatePixelIslandList();
	}

	public PixelList getPixelList() {
		return imageProcessor.getOrCreatePixelList();
	}

	public static DiagramAnalyzer processImage(BufferedImage image) {
		DiagramAnalyzer diagramAnalyzer = null;
		if (image != null) {
			diagramAnalyzer = new DiagramAnalyzer();
			diagramAnalyzer.setImage(image);
			diagramAnalyzer.getImageProcessor().processImage();
		}
		return diagramAnalyzer;
	}

	public static DiagramAnalyzer processUnthinnedImage(BufferedImage image) {
		DiagramAnalyzer diagramAnalyzer = null;
		if (image != null) {
			diagramAnalyzer = new DiagramAnalyzer();
			diagramAnalyzer.setBinarize(true);
			diagramAnalyzer.setThinning(null);
			diagramAnalyzer.setImage(image);
			diagramAnalyzer.getImageProcessor().processImage();
		}
		return diagramAnalyzer;
	}

	public IntRangeArray getYslices() {
		IntRangeArray intRangeArray = null;
		BufferedImage image = getImage();
		if (image != null) {
			int height = image.getHeight();
			intRangeArray = new IntRangeArray();
			for (int y = 0; y < height; y++) {
				Integer xmin = null;
				Integer xmax = null;
				for (int x = 0; x < image.getWidth(); x++) {
					int color = image.getRGB(x, y);
					if ((color & 0x00ffffff) == 0) {
						if (xmin == null) {
							xmin = x;
							xmax = x;
						} else {
							xmax = x;
						}
					}
				}
				if (xmin != null && xmax != null) {
					IntRange intRange = new IntRange(xmin, xmax);
					intRangeArray.add(intRange);
				} 
			}
		}
		return intRangeArray;
	}

	public BufferedImage getBinarizedImage() {
		return imageProcessor == null ? null : imageProcessor.getBinarizedImage();
	}

	public Real2Array extractLocalSummitCoordinates(int minNestedRings, int axis) {
		setThinning(null);
		readAndProcessInputFile();
		// list of pixelRings by island
		LocalSummitList localSummitList = createDefaultPixelRingListList();
		/*PixelRingList summitList = */ localSummitList.extractLocalSummits(minNestedRings);
		Real2Array centreArray = localSummitList.getCentreArray();
		centreArray.sortAscending(axis);
		return centreArray;
	}

	public SVGLineList extractHorizontalLines() {
		setThinning(new ZhangSuenThinning()).readAndProcessInputFile();
		PixelIslandList pixelIslandList = getImageProcessor().getOrCreatePixelIslandList();
		pixelIslandList.removeIslandsWithBBoxesLessThan(new Int2(10,10)); //<<<
		PixelGraphList graphList = getOrCreateGraphList();
		graphList.mergeNodesCloserThan(3.0);                            
		
		LineCache lineCache = new LineCache(new ComponentCache()).setSegmentTolerance(1.0);
		lineCache.addGraphList(graphList);
		
		IntArray yArray = lineCache.getGridYCoordinates();
		graphList.snapNodesToArray(yArray, Axis2.Y, 1);
		/** recreate cache to clear old values */
		
		lineCache = new LineCache(new ComponentCache());
		SVGLineList edgeLines = graphList.createLinesFromEdges();
		lineCache.addLines(edgeLines.getLineList());
		SVGLineList horSVGLineList = lineCache.getOrCreateHorizontalSVGLineList();
		horSVGLineList.mergeLines(1.0, MergeMethod.OVERLAP);
		return horSVGLineList;
		
	}

	public PixelRingList extractLocalSummits(int minNestedRings) {
		return createDefaultPixelRingListList().extractLocalSummits(minNestedRings);
	}

	public Real2Array getCentreArray(int minNestedRings) {
		LocalSummitList summitList = createDefaultPixelRingListList();
		summitList.extractLocalSummits(minNestedRings);
		return summitList.getCentreArray();
	}

	public SVGLineList extractHorizontalLines(File imageFile, File pngDir) {
		setThinning(new ZhangSuenThinning());
		readAndProcessInputFile(imageFile);
		ImageProcessor imageProcessor = getImageProcessor();
		PixelIslandList pixelIslandList = imageProcessor.getOrCreatePixelIslandList();
		pixelIslandList.removeIslandsWithBBoxesLessThan(new Int2(10,10)); 
		
		PixelGraphList graphList = getOrCreateGraphList();
		graphList.mergeNodesCloserThan(3.0);                            
	
		LineCache lineCache = new LineCache().setSegmentTolerance(1.0).addGraphList(graphList);
		
		IntArray xArray = lineCache.getGridXCoordinates();
		graphList.snapNodesToArray(xArray, Axis2.X, 2);
		IntArray yArray = lineCache.getGridYCoordinates();
		graphList.snapNodesToArray(yArray, Axis2.Y, 1);
		lineCache = new LineCache(new ComponentCache());
		
		lineCache.addLines(graphList.createLinesFromEdges());
		SVGLineList horSVGLineList = lineCache.getOrCreateHorizontalSVGLineList();
		
		horSVGLineList.mergeLines(1.0, MergeMethod.OVERLAP);
		return horSVGLineList;
	}

	public void extractLocalSummits(int minNestedRings, File imageFile, File pngDir) {
		setThinning(null);
		readAndProcessInputFile(imageFile);
		BufferedImage image = getImage();
		if (image != null && image.getWidth() * image.getHeight() < 1000000) {
			PixelRingList localSummits = extractLocalSummits(minNestedRings);
			Real2Array centreArray = getCentreArray(minNestedRings);
		}
	}

}

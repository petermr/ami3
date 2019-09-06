package org.contentmine.graphics.svg.plot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RangeScaler;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
import org.contentmine.graphics.svg.SVGPoly;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.cache.LineCache;
import org.contentmine.graphics.svg.cache.PolygonCache;
import org.contentmine.graphics.svg.cache.ShapeCache;
import org.contentmine.graphics.svg.objects.SVGRhomb;

/** superclass of all plotBox types.
 * current types include XY, X, Y Plots
 * 
 * @author pm286
 *
 */
public abstract class AbstractPlotBox {
	protected static final Logger LOG = Logger.getLogger(AbstractPlotBox.class);
	public static int FORMAT_NDEC = 3;
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum AxisType {
		BOTTOM(0, LineDirection.HORIZONTAL, 1),
		LEFT(1, LineDirection.VERTICAL, -1),
		TOP(2, LineDirection.HORIZONTAL, -1),
		RIGHT(3, LineDirection.VERTICAL, 1);
		private int serial;
		private LineDirection direction;
		/** if 1 adds outsideWidth to maxBox, else if -1 adds insideWidth */
		private int outsidePositive;
		private AxisType(int serial, LineDirection direction, int outsidePositive) {
			this.serial = serial;
			this.direction = direction;
			this.outsidePositive = outsidePositive;
		}
		
		public int getSerial() {
			return serial;
		}
		public static int getSerial(AxisType axisType) {
			for (int i = 0; i < values().length; i++) {
				if (values()[i].equals(axisType)) {
					return i;
				}
			}
			return -1;
		}
		public static final int BOTTOM_AXIS = AxisType.getSerial(AxisType.BOTTOM);
		public static final int LEFT_AXIS   = AxisType.getSerial(AxisType.LEFT);
		public static final int TOP_AXIS    = AxisType.getSerial(AxisType.TOP);
		public static final int RIGHT_AXIS  = AxisType.getSerial(AxisType.RIGHT);
		public LineDirection getLineDirection() {
			return direction;
		}
		/** 
		 * 
		 * @return if 1 adds outsideWidth to max dimension of initial box and
		 *                   insideWidth min dimension
		 *         if 0 adds outsideWidth to min dimension of initial box and
		 *                   insideWidth max dimension
		 *                   
		 *   
		 */
		public int getOutsidePositive() {
			return outsidePositive;
		}
	}

	public enum BoxType {
		HLINE("bottom x-axis only"), 
		UBOX("bottom x-axis L-y-axis R-y-axis"),
		PIBOX("top x-axis L-y-axis R-y-axis"),
		LBOX("bottom x-axis L-y-axis"),
		RBOX("bottom x-axis R-y-axis"),
		FULLBOX("bottom x-axis top x-axis L-y-axis R-y-axis"),
		;
		private String title;
		private BoxType(String title) {
			this.title = title;
		}
	}

	// ==================================
	
	protected AnnotatedAxis[] axisArray;
	protected BoxType boxType;
	protected int ndecimal = FORMAT_NDEC;
	protected File svgOutFile;
	protected String csvContent;
	protected File csvOutFile;
	
	protected Real2Array screenXYs;
	protected Real2Array scaledXYs;
	protected RealArray screenYs;
	protected RealArray scaledYs;
	protected RealArray screenXs;
	protected RealArray scaledXs;
	protected List<String> xTitles;
	protected List<String> yTitles;
	
	/** from Forest plot - may need revising */
	public static final double PERCENT_100 = 100.;
	public static final String NO_SCALE_FILL = "red";
	public static final String SCALE_FILL = "purple";
	
	/** for file names */
	public static final String SVG = ".svg";
	public static final String HORIZONTAL = "horizontal";
	public static final String VERTICAL = "vertical";
	public static final String NONAXIAL = "nonaxial";
	public static final String POLYLIST = "polylist";
	public static final String LINES = "lines";
	public static final String RECTS = "rects";
	public static final String CACHE = "cache";
	public static final String POLYGONS = "polygons";
	protected static final String DEFAULT_LINE_FILL = "fill:blue;";
	
	public static final String DEFAULT_WEIGHT_2_STYLE = "font-size:5.0;fill:blue;font-weight:bold;font-family:helvetica;";
	public static final Real2 WEIGHT_OFFSET_UP = new Real2(0.0, -2.0);
	public static final String DEFAULT_WEIGHT_1_STYLE = "font-size:5.0;fill:green;font-weight:bold;font-family:helvetica;";

	protected RangeScaler rangeScaler;
//	protected double totalRectArea;
	protected boolean applyScale = true;
	protected boolean logarithmicScale;
	protected File outputDir;
	
	protected double diamondEps;
	protected double lineMergeEps;
	protected double nonAxialEps;

	protected String fileRoot;
	protected List<SVGLine> allLines;
	protected List<SVGLine> horizontalLines;
	protected List<SVGLine> nonAxialLineList;
	private List<SVGPolygon> polygonList;
	private List<SVGRect> rectList;
	// is this a good place or should it be in polygon??
//	private List<SVGRhomb> rhombList;
	private List<SVGShape> shapeList;
	protected List<SVGLine> verticalLines;
	
	protected ComponentCache componentCache;
	protected LineCache lineCache;
	protected ShapeCache shapeCache;
	protected PolygonCache polygonCache;


	protected void setDefaults() {
		axisArray = new AnnotatedAxis[AxisType.values().length];
		for (AxisType axisType : AxisType.values()) {
			AnnotatedAxis axis = createAxis(axisType);
			axisArray[axisType.getSerial()] = axis;
		}
		ndecimal = FORMAT_NDEC;
		lineMergeEps = 0.1; // axial merge
	}

	AnnotatedAxis createAxis(AxisType axisType) {
		AnnotatedAxis axis = new AnnotatedAxis(this, axisType);
		return axis;
	}

	public ComponentCache getComponentCache() {
		return componentCache;
	}

	/** MAIN ENTRY METHOD for processing plots.
	 * 
	 * @param originalSvgElement
	 * @throws FileNotFoundException 
	 */
	public void readAndCreateCSVPlot(File file) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(file);
		this.fileRoot = FilenameUtils.getName(file.toString());
		readAndCreateCSVPlot(inputStream);
	}

	/** MAIN ENTRY METHOD for processing plots.
	 * 
	 * @param inputStream
	 */
	private void readAndCreateCSVPlot(InputStream inputStream) {
		if (inputStream == null) {
			throw new RuntimeException("Null input stream");
		}
		AbstractCMElement svgElement = SVGUtil.parseToSVGElement(inputStream);
		if (svgElement == null) {
			throw new RuntimeException("Null svgElement");
		}
		readAndCreateCSVPlot(svgElement);
	}

	/** ENTRY METHOD for processing figures.
	 * 
	 * @param originalSvgElement
	 */
	public void readGraphicsComponents(File inputFile) {
		if (inputFile == null) {
			throw new RuntimeException("Null input file");
		}
		if (!inputFile.exists() || inputFile.isDirectory()) {
			throw new RuntimeException("nonexistent file or isDirectory "+inputFile);
		}
		fileRoot = inputFile.getName();
		componentCache = new ComponentCache(this);
		try {
			componentCache.readGraphicsComponentsAndMakeCaches(new FileInputStream(inputFile));
		} catch (IOException e) {
			throw new RuntimeException("Cannot read inputFile", e);
		}
	}

	protected abstract void readAndCreateCSVPlot(AbstractCMElement svgElement);

	protected void extractDataScreenPoints() {
		// TODO Auto-generated method stub
		
	}

	protected String createCSVContent() {
		return csvContent;
	}

	protected void makeAxialTickBoxesAndPopulateContents() {
		LineCache lineCache = componentCache.getOrCreateLineCache();
		for (AnnotatedAxis axis : axisArray) {
			axis.getOrCreateSingleLine();		
			axis.createAndFillTickBox(lineCache.getOrCreateHorizontalLineList(), lineCache.getOrCreateVerticalLineList());
		}
	}

	protected void extractScaleTextsAndMakeScales() {
		for (AnnotatedAxis axis : this.axisArray) {
			axis.extractScaleTextsAndMakeScales();
		}
	}

	protected void extractTitleTextsAndMakeTitles() {
		for (AnnotatedAxis axis : this.axisArray) {
			axis.extractTitleTextsAndMakeTitles();
		}
	}

	protected void makeRangesForAxes() {
		for (AnnotatedAxis axis : this.axisArray) {
			axis.createAxisRanges();
		}
	}

	public void writeCSV(File file) {
		if (file != null) {
			try {
				IOUtils.write(csvContent, new FileOutputStream(file));
			} catch (IOException e) {
				throw new RuntimeException("cannot write CSV: ", e);
			}
		}
	}

	private AbstractCMElement copyAnnotatedAxes() {
		SVGG g = new SVGG();
		g.setSVGClassName("plotBox");
		for (AnnotatedAxis axis : axisArray) {
			g.appendChild(axis.getSVGElement().copy());
		}
		return g;
	}

	public BoxType getBoxType() {
		return boxType;
	}

	public void setBoxType(BoxType boxType) {
		this.boxType = boxType;
	}

	public AnnotatedAxis[] getAxisArray() {
		return axisArray;
	}

	public int getNdecimal() {
		return ndecimal;
	}

	public void setNdecimal(int ndecimal) {
		this.ndecimal = ndecimal;
	}

	public List<SVGText> getHorizontalTexts() {
		return componentCache.getOrCreateTextCache().getOrCreateHorizontalTexts();
	}

	public List<SVGText> getVerticalTexts() {
		return componentCache.getOrCreateTextCache().getOrCreateVerticalTexts();
	}

	public void writeProcessedSVG(File file) {
		if (file != null) {
			SVGElement processedSVGElement = componentCache.createSVGElement();
			processedSVGElement.appendChild(copyAnnotatedAxes());
			SVGSVG.wrapAndWriteAsSVG(processedSVGElement, file);
		}
	}

	public String getCSV() {
		return csvContent;
	}

	public File getSvgOutFile() {
		return svgOutFile;
	}

	public void setSvgOutFile(File svgOutFile) {
		this.svgOutFile = svgOutFile;
	}

	public File getCsvOutFile() {
		return csvOutFile;
	}

	public void setCsvOutFile(File csvOutFile) {
		this.csvOutFile = csvOutFile;
	}

	public void readAndStructureFigures(AbstractCMElement svgElement) {
		List<SVGElement> textElements = SVGUtil.getQuerySVGElements(svgElement, ".//*[local-name()='text']");
		List<SVGText> texts = SVGText.extractTexts(textElements);
		LOG.debug("texts "+texts.size());
	}

	protected ComponentCache getOrCreateComponentCache() {
		if (componentCache == null) {
			componentCache = new ComponentCache(this);
		}
		return componentCache;
	}

	public List<SVGLine> getOrCreateHorizontalLineList() {
		if (horizontalLines == null) {
			horizontalLines = getOrCreateLineCache().getOrCreateHorizontalLineList();
		}
		return horizontalLines;
	}

	protected LineCache getOrCreateLineCache() {
		if (lineCache == null) {
			lineCache = getOrCreateComponentCache().getOrCreateLineCache();
		}
		return lineCache;
	}

	protected ShapeCache getOrCreateShapeCache() {
		if (shapeCache == null) {
			shapeCache = getOrCreateComponentCache().getOrCreateShapeCache();
		}
		return shapeCache;
	}

	protected PolygonCache getOrCreatePolygonCache() {
		if (polygonCache == null) {
			polygonCache = getOrCreateComponentCache().getOrCreatePolygonCache();
		}
		return polygonCache;
	}

	public List<SVGLine> getOrCreateVerticalLineList() {
		if (verticalLines == null) {
			verticalLines = getOrCreateLineCache().getOrCreateVerticalLineList();
		}
		return verticalLines;
	}

	public List<SVGRhomb> getOrCreateRhombList() {
		return getOrCreateShapeCache().getOrCreateRhombList();
	}

	private List<SVGLine> getOrCreateNonAxialLineList() {
		if (nonAxialLineList == null) {
			nonAxialLineList = new ArrayList<SVGLine>(getOrCreateAllLineList());
			nonAxialLineList.removeAll(getOrCreateHorizontalLineList());
			nonAxialLineList.removeAll(getOrCreateVerticalLineList());
		}
		return nonAxialLineList;
	}

	private List<SVGLine> getOrCreateAllLineList() {
		if (allLines == null) {
			allLines = getOrCreateLineCache().getOrCreateLineList().getLineList();
		}
		return allLines;
	}

	public void removeNearlyAxialLines() {
		for (int i = nonAxialLineList.size() - 1; i >= 0; i--) {
			SVGLine svgLine = nonAxialLineList.get(i);
			if (nonAxialLineList.get(i).isHorizontal(nonAxialEps ) || svgLine.isVertical(nonAxialEps)) {
				nonAxialLineList.remove(i);
			}
		}
	}

	public File getCacheFile() {
		return (outputDir == null || fileRoot == null) ? null : new File(outputDir, fileRoot+"/"+CACHE+SVG);
	}

	public File getHorizontalLinesFile() {
		return (outputDir == null || fileRoot == null) ? null : new File(outputDir, fileRoot+"/"+HORIZONTAL+SVG);
	}
	
	private File getLinesFile() {
		return (outputDir == null || fileRoot == null) ? null :  new File(outputDir, fileRoot+"/"+LINES+SVG);
	}

	public File getPolyListFile() {
		return (outputDir == null || fileRoot == null) ? null : new File(outputDir, fileRoot+"/"+POLYLIST+SVG);
	}

	protected File getPolygonsFile() {
		return (outputDir == null || fileRoot == null) ? null : new File(outputDir, fileRoot+"/"+POLYGONS+SVG);
	}

	public File getVerticalLinesFile() {
		return (outputDir == null || fileRoot == null) ? null : new File(outputDir, fileRoot+"/"+VERTICAL+SVG);
	}

	public double getDiamondEps() {
		return diamondEps;
	}

	public String getFileRoot() {
		return fileRoot;
	}

	protected File createRectsFile() {
		return (outputDir == null || fileRoot == null) ? null : new File(outputDir, fileRoot+"/"+RECTS+SVG);
	}

	protected List<SVGPolygon> createPolygons(List<SVGLine> lineList) {
		List<SVGPolygon> polygonList = SVGPolygon.createPolygonsFromLines(lineList, diamondEps);
		return polygonList;
	}

	protected SVGG createPolygonsElement(List<SVGPolygon> polygonList) {
		SVGG g = new SVGG();
		for (SVGPolygon polygon : polygonList) {
			if (polygon.size() == 4) {
				String style = "fill:red;stroke:green;stroke-width:3.0;";
				polygon.setCSSStyle(style);
				g.appendChild(polygon.copy());
			}
		}
		return g;
	}

	
	protected List<SVGPolygon> getOrCreateNonRectPolygonList(List<SVGLine> nonAxialLineList) {
		List<SVGPolygon> polygonList = componentCache.getOrCreatePolygonCache().getOrCreatePolygonList();
		polygonList.addAll(createPolygons(nonAxialLineList));
		return polygonList;
	}

	protected List<SVGLine> getOrCreateLineList() {
		if (allLines == null) {
			getOrCreateLineCache();
			allLines = lineCache.getOrCreateLineList().getLineList();
		}
		return allLines;
	}

	protected SVGG createHorizontalLinesElement() {
		SVGG gg = new SVGG();
		for (SVGLine line : horizontalLines) {
			String lineStyle = DEFAULT_LINE_FILL;
			line.setCSSStyle(lineStyle);
			gg.appendChild(line);
			gg.appendChild(plotLineEnd(line, 0));
			gg.appendChild(plotLineEnd(line, 1));
			Real2Range bbox0 = line.getBoundingBox();
		}
		return gg;
	}

	protected SVGG createShapeListElement(List<? extends SVGShape> shapeList) {
		getOrCreateRectList();
		SVGG gg = new SVGG();
		double totalArea = 0.0;
		for (SVGShape shape : shapeList) {
			totalArea += shape.getBoundingBox().calculateArea();
		}
		for (SVGShape shape : shapeList) {
			double area = PERCENT_100 * shape.getBoundingBox().calculateArea() / totalArea;
//			area *= 2; // because there are two rects for each one displayed???
			shape.setFill("blue");
			gg.appendChild(shape.copy());
			gg.appendChild(plotShapeArea(shape, area, 5.0));
		}
		return gg;
	}

	protected SVGG createRhombElement() {
		getOrCreateRhombList();
		return createShapeListElement(getOrCreateRhombList());
	}

	protected SVGG createPolygonElement() {
		getOrCreatePolygonList();
		SVGG g = new SVGG();
		for (SVGPoly polygon : polygonList) {
			g.appendChild(polygon.copy());
		}
		return g;
	}

	public List<SVGPolygon> getOrCreatePolygonList() {
		polygonList = componentCache.getOrCreatePolygonCache().getOrCreatePolygonList();
		return polygonList;
	}

	protected SVGG createRectElement() {
		getOrCreateRectList();
		SVGG g = new SVGG();
		for (SVGRect rect : rectList) {
			g.appendChild(rect.copy());
		}
		return g;
	}

	public List<SVGRect> getOrCreateRectList() {
		rectList = componentCache.getOrCreateRectCache().getOrCreateRectList();
		return rectList;
	}

	protected SVGG createShapeElement() {
		getOrCreateShapeList();
		SVGG g = new SVGG();
		for (SVGShape shape : shapeList) {
			g.appendChild(shape.copy());
		}
		return g;
	}

	public List<SVGShape> getOrCreateShapeList() {
		shapeList = componentCache.getOrCreateShapeCache().getOrCreateAllShapeList();
		return shapeList;
	}


	public SVGG plotLineEnd(SVGLine line, int end) {
		SVGG g = new SVGG();
		Real2 xy = line.getXY(end);
		double x = extractTransformX(xy);
		Real2 offset = applyScale ? WEIGHT_OFFSET_UP : new Real2(0.0, 5.0);
		SVGText text = SVGText.createDefaultText(xy.plus(offset), String.valueOf(Util.format(x, 2)));
		text.setFontSize(5.0);
		text.setFill(applyScale ? SCALE_FILL : NO_SCALE_FILL);
		text.setFontWeight("bold");
		text.setFontFamily("helvetica");
		
		g.appendChild(text);
		
		return g;
	}

	public double extractTransformX(Real2 xy) {
		double x = xy.getX();
		if (applyScale) {
			x = rangeScaler.transformInputToOutput(x);
			x = Math.pow(10.0, x);
		} else {
			x = (int) x;
		}
		return x;
	}

	/** should move elsewhere
	 * 
	 * @param shape
	 * @return
	 */
	public SVGG plotShapeArea(SVGShape shape, double area, double fontSize) {
		SVGG g = new SVGG();
		Real2Range bbox = shape.getBoundingBox();
		Real2 xy = bbox.getCentroid();
		double x = Util.format(extractTransformX(xy), 2);
		SVGText text = SVGText.createDefaultText(xy.plus(WEIGHT_OFFSET_UP), String.valueOf(x));
		String style = DEFAULT_WEIGHT_2_STYLE;
		text.setCSSStyle(style);
		g.appendChild(text);
		SVGText text1 = SVGText.createDefaultText(xy.plus(new Real2(5.0, 5.0)), ""+Util.format(area, 2));
		text1.setFontSize(fontSize);
		String weightStyle  = DEFAULT_WEIGHT_1_STYLE;
		text1.setCSSStyle(weightStyle);
		g.appendChild(text1);
		return g;
	}

	public ComponentCache createCaches(AbstractCMElement svgElement) {
		ComponentCache componentCache = new ComponentCache(this); 
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		return componentCache;
	}

	protected void setNonAxialEps(double eps) {
		nonAxialEps = eps;
	}

	public void setOutputDir(File file) {
		this.outputDir = file;
	}

	public boolean isLogarithmicScale() {
		return logarithmicScale;
	}

	public void setLogarithmicScale(boolean logarithmicScale) {
		this.logarithmicScale = logarithmicScale;
	}

	public boolean isApplyScale() {
		return applyScale;
	}

	public void setDiamondEps(double diamondEps) {
		this.diamondEps = diamondEps;
	}

	public void setFileRoot(String fileRoot) {
		this.fileRoot = fileRoot;
		
	}

}

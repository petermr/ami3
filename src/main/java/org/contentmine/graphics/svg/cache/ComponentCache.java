package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGDefs;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGImage;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGTitle;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.StyleAttributeFactory;
import org.contentmine.graphics.svg.plot.AbstractPlotBox;
import org.contentmine.graphics.svg.text.build.TextChunkList;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.graphics.svg.util.SuperPixelArray;

/** stores SVG primitives for access by analysis programs
 * 
 * @author pm286
 *
 */
public class ComponentCache extends AbstractCache {
	public static final Logger LOG = Logger.getLogger(ComponentCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum Feature {
		// texts
		HORIZONTAL_TEXT_COUNT("htxt"),
		HORIZONTAL_TEXT_STYLE_COUNT("htsty"),
		VERTICAL_TEXT_COUNT("vtxt"),
		VERTICAL_TEXT_STYLE_COUNT("vtsty"),
		// shapes
		LINE_COUNT("lines"),
		RECT_COUNT("rects"),
		PATH_COUNT("paths"),
		CIRCLE_COUNT("circs"),
		ELLIPSE_COUNT("ellips"),
		POLYGONS_COUNT("pgons"),
		POLYLINE_COUNT("plines"),
		SHAPE_COUNT("shapes"),
		// rules
		LONG_HORIZONTAL_RULE_COUNT("lnghr"),
		SHORT_HORIZONTAL_RULE_COUNT("shthr"),
		TOP_HORIZONTAL_RULE_COUNT("tophr"),
		BOTTOM_HORIZONTAL_RULE_COUNT("bothr"),
		LONG_HORIZONTAL_RULE_THICKNESS_COUNT("hrthick"),
		// panels
		HORIZONTAL_PANEL_COUNT("hpanel"),
		;
		
		public String abbrev;

		private Feature(String abbrev) {
			this.abbrev = abbrev;
		}
		public static List<String> getAbbreviations() {
			List<String> abbrevs = new ArrayList<String>();
			for (Feature feature : values()) {
				abbrevs.add(feature.abbrev);
			}
			return abbrevs;
		}
		
		public static Feature getFeatureFromAbbreviation(String abbrev) {
			for (Feature feature : values()) {
				if (feature.abbrev.equals(abbrev)) {
					return feature;
				}
			}
			return null;
		}
		public static List<String> getAbbreviations(List<Feature> features) {
			List<String> abbreviations = new ArrayList<String>();
			for (Feature feature : features) {
				abbreviations.add(feature.abbrev);
			}
			return abbreviations;
		}
		
		public final static List<Feature> TEXT_SHAPE_FEATURES = Arrays.asList(new Feature[] {
				Feature.HORIZONTAL_TEXT_COUNT,
				Feature.HORIZONTAL_TEXT_STYLE_COUNT,
				Feature.VERTICAL_TEXT_COUNT,
				Feature.VERTICAL_TEXT_STYLE_COUNT,
				
				Feature.LINE_COUNT,
				Feature.RECT_COUNT,
				Feature.PATH_COUNT,
				Feature.CIRCLE_COUNT,
				Feature.ELLIPSE_COUNT,
				Feature.POLYGONS_COUNT,
				Feature.POLYLINE_COUNT,
				Feature.SHAPE_COUNT,
			}
			);

		public final static List<Feature> RECT_LINE_FEATURES = Arrays.asList(new Feature[] {
				Feature.LONG_HORIZONTAL_RULE_COUNT,
				Feature.SHORT_HORIZONTAL_RULE_COUNT,
				Feature.TOP_HORIZONTAL_RULE_COUNT,
				Feature.BOTTOM_HORIZONTAL_RULE_COUNT,
				Feature.LONG_HORIZONTAL_RULE_THICKNESS_COUNT,
				Feature.HORIZONTAL_PANEL_COUNT,
			}
			);

	}

	public static final String FILE = "file";

	public final static String MAJOR_COLORS[] = {
			"red",
			"green",
			"blue",
			"cyan",
			"magenta",
			"yellow",
			"pink",
			"gray",
			"purple",
		};



	// =======================================
	
	public static int ZERO_PLACES = 0;
	

	private ImageCache imageCache;
	private PathCache pathCache;
	TextCache textCache;
	private PolylineCache polylineCache;
	private PolygonCache polygonCache;
	private GlyphCache glyphCache;
	private LineCache lineCache;
	private MathCache mathCache;
	private RectCache rectCache;
	ShapeCache shapeCache; // can be accessed by siblings
	private ContentBoxCache contentBoxCache;
	private TextChunkCache textChunkCache;
	
	// other caches as they are developed
	private List<AbstractCache> abstractCacheList;
	
	private Real2Range positiveXBox;

	public String fileRoot;
	public String debugRoot = "target/debug/";
	private String glyphDebug = "target/glyphs/";
	private String imageDebug = "target/images/";
	private String pathDebug = "target/paths/";
	private String shapeDebug = "target/shapes/";
	private String textDebug = "target/texts/";
	private File plotDebug = new File("target/plots/");

	private boolean removeWhitespace = false;
	private boolean splitAtMove = true;
	
	protected List<SVGElement> allElementList;
	List<Real2Range> boundingBoxList;

	private List<Real2> whitespaceSpixels;
	private double outerBoxEps = 3.0; // outer bbox error
	private TextStructurer textStructurer;
	private List<AbstractCache> cascadingCacheList;



	/** this may change as we decide what types of object interact with store
	 * may need to move to AbstractCache
	 * 
	 * @param plotBox
	 */
	public ComponentCache(AbstractPlotBox plotBox) {
		super(plotBox);
	}
	
	public ComponentCache() {
	}

	/** 
	 * 
	 * @param file
	 * @throws RuntimeException if file not found
	 */
	public void readGraphicsComponentsAndMakeCaches(File file) throws RuntimeException {
		this.fileRoot = FilenameUtils.getBaseName(file.getName());
		try {
			readGraphicsComponentsAndMakeCaches(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void readGraphicsComponentsAndMakeCaches(InputStream inputStream) {
		if (inputStream == null) {
			throw new RuntimeException("Null input stream: "+inputStream);
		}
		AbstractCMElement svgElement = SVGUtil.parseToSVGElement(inputStream);
		readGraphicsComponentsAndMakeCaches(svgElement);
	}

	public void readGraphicsComponentsAndMakeCaches(AbstractCMElement svgElement) {
		if (svgElement != null) {
			long time = System.currentTimeMillis();
			this.inputSVGElement = (SVGElement) svgElement.copy();
			LOG.trace("after copy "+(System.currentTimeMillis() - time));
			this.convertedSVGElement = new SVGG();
			
			 // is this a good idea? These are clipping boxes. 
			SVGDefs.removeDefs(this.inputSVGElement);
			LOG.trace("atts "+(System.currentTimeMillis() - time));
			StyleAttributeFactory.convertElementAndChildrenFromOldStyleAttributesToCSS(this.inputSVGElement);
			LOG.trace("after atts "+(System.currentTimeMillis() - time));
			
			this.positiveXBox = new Real2Range(new RealRange(-100., 10000), new RealRange(-10., 10000));
			this.removeEmptyTextElements();
			LOG.trace("after empty "+(System.currentTimeMillis() - time));
			this.removeNegativeXorYElements();
			LOG.trace("casc "+(System.currentTimeMillis() - time));
			
			this.getOrCreateCascadingCaches();
			LOG.trace("lines "+(System.currentTimeMillis() - time));
			this.lineCache.createSpecializedLines();
			LOG.trace("end "+(System.currentTimeMillis() - time));
			LOG.trace("lines: "+lineCache);
			LOG.trace("text: "+textCache);
			
//			this.debugComponentsToSVGFiles();
		} else {
			throw new RuntimeException("Null svgElement");
		}
	}

	private void debugComponentsToSVGFiles() {
		AbstractCMElement g;
		SVGG gg = new SVGG();
		g = this.getOrCreatePathCache().debugToSVG(pathDebug+this.fileRoot+".debug.svg");
		g.appendChild(new SVGTitle("path"));
		
		
		g = this.imageCache.debugToSVG(imageDebug+this.fileRoot+".debug.svg");
		g.appendChild(new SVGTitle("image"));
		
		g = this.getOrCreateShapeCache().debugToSVG(shapeDebug + fileRoot+".debug.svg");
		g.appendChild(new SVGTitle("shape"));
		gg.appendChild(g.copy());

		g = this.textCache.debug(textDebug + this.fileRoot+".debug.svg");
		g.appendChild(new SVGTitle("text"));
		gg.appendChild(g.copy());
		
		SVGSVG.wrapAndWriteAsSVG(gg, new File(plotDebug, fileRoot+".debug.svg"));
	}

	public PathCache getOrCreatePathCache() {
		if (pathCache == null) {
			this.pathCache = new PathCache(this);
			this.pathCache.extractPaths(this.inputSVGElement);
		}
		return pathCache;
	}

	public ImageCache getOrCreateImageCache() {
		if (imageCache == null) {
			this.imageCache = new ImageCache(this);
			this.imageCache.getOrCreateImageList();
		}
		return imageCache;
	}

	public TextCache getOrCreateTextCache() {
		int ndecimal = -1;
		return getOrCreateTextCache(ndecimal);
	}

	/**
	 * 
	 * @param ndecimal format the coordinates ; if -1 then uses default
	 * @return
	 */
	public TextCache getOrCreateTextCache(int ndecimal) {
		if (textCache == null) {
			this.textCache = new TextCache(this);
			if (ndecimal >= 0) {
				textCache.setCoordinateDecimalPlaces(ndecimal);
			}
			if (this.inputSVGElement != null) {
				this.textCache.extractTexts(this.inputSVGElement);
			}
		}
		return textCache;
	}

	public ShapeCache getOrCreateShapeCache() {
		if (shapeCache == null) {
			long millis = System.currentTimeMillis();
			shapeCache = new ShapeCache(this);
			pathCache = getOrCreatePathCache();
			List<SVGPath> currentPathList = this.pathCache.getCurrentPathList();
			this.getOrCreateShapeCache().extractShapes(currentPathList, inputSVGElement);
			List<SVGShape> shapeList = shapeCache.getOrCreateConvertedShapeList();
			addElementsToExtractedElement(shapeList);
			LOG.trace("shapes: "+(System.currentTimeMillis() - millis)/1000);
		}
		return shapeCache;
	}

	public GlyphCache getOrCreateGlyphCache() {
		if (glyphCache == null) {
			this.glyphCache = new GlyphCache(this);
			
			List<SVGPath> pathList = getOrCreatePathCache().getCurrentPathList();
			for (SVGPath path : pathList) {
				
			}
		}
		return glyphCache;
	}

	public LineCache getOrCreateLineCache() {
		if (lineCache == null) {
			this.lineCache = new LineCache(this);
		}
		return lineCache;
	}

	public RectCache getOrCreateRectCache() {
		if (rectCache == null) {
			this.rectCache = new RectCache(this);
		}
		return rectCache;
	}

	public PolylineCache getOrCreatePolylineCache() {
		if (polylineCache == null) {
			this.polylineCache = new PolylineCache(this);
//			polylineCache.setSiblingShapeCache(shapeCache);
		}
		return polylineCache;
	}

	public PolygonCache getOrCreatePolygonCache() {
		if (polygonCache == null) {
			this.polygonCache = new PolygonCache(this);
//			polygonCache.setSiblingShapeCache(shapeCache);
		}
		return polygonCache;
	}

	public TextChunkCache getOrCreateTextChunkCache() {
		if (textChunkCache == null) {
//			LOG.debug("t0");
			this.textChunkCache = new TextChunkCache(this);
//			LOG.debug("t1");
			textStructurer = textChunkCache.getOrCreateTextStructurer();
			TextChunkList textChunkList = textChunkCache.getOrCreateTextChunkList();
			// should probably move TextStructure to TextChunkCache
//			LOG.debug("t2");
			textStructurer = TextStructurer.createTextStructurerWithSortedLines(convertedSVGElement);
//			LOG.debug("t3");
			AbstractCMElement inputSVGChunk = textStructurer.getSVGChunk();
//			LOG.debug("t4");
			textChunkCache.cleanChunk(inputSVGChunk);
//			LOG.debug("t5");
			AbstractCMElement textChunk = textStructurer.getTextChunkList().getLastTextChunk();
			textStructurer.condenseSuscripts();
//			LOG.debug("t6");
		}
		return textChunkCache;
	}

	public ContentBoxCache getOrCreateContentBoxCache() {
		if (contentBoxCache == null) {
//			this.contentBoxCache = new ContentBoxCache(this);
			contentBoxCache = ContentBoxCache.createCache(rectCache, textChunkCache);
			if (contentBoxCache != null) {
				contentBoxCache.getOrCreateConvertedSVGElement();
				LOG.trace("poly1 "+contentBoxCache.ownerComponentCache.shapeCache.getPolylineList());
				contentBoxCache.getOrCreateContentBoxGrid();
			}
		}
		return contentBoxCache;
	}

	void addElementsToExtractedElement(List<? extends SVGElement> elementList) {
		if (convertedSVGElement == null) {
			convertedSVGElement = new SVGG();
		}
		for (AbstractCMElement element : elementList) {
			SVGElement elementCopy = (SVGElement) element.copy();
			
			StyleAttributeFactory.convertElementAndChildrenFromOldStyleAttributesToCSS(elementCopy);
//			StyleAttributeFactory.createUpdatedStyleAttribute(elementCopy, AttributeStrategy.MERGE);
			convertedSVGElement.appendChild(elementCopy);
		}
	}

	/** some plots have publisher cruft outside the limits, especially negative Y.
	 * remove these elements from svgElement
	 * @param plotBox TODO
	 * 
	 */
	public void removeNegativeXorYElements() {
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(inputSVGElement);
		for (int i = texts.size() - 1; i >= 0; i--) {
			SVGText text = texts.get(i);
			Real2 xy = text.getXY();
			if (xy != null && (xy.getX() < 0.0 || xy.getY() < 0.0)) {
				texts.remove(i);
				text.detach();
			}
		}
	}

	public void removeEmptyTextElements() {
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(this.inputSVGElement);
		for (int i = texts.size() - 1; i >= 0; i--) {
			SVGText text = texts.get(i);
			String s = text.getValue();
			if (s == null || "".equals(s.trim())) {
				texts.remove(i);
				text.detach();
			}
		}
	}



	public SVGElement createSVGElement() {
		SVGG g = new SVGG();
		g.appendChild(copyOriginalElements());
		g.appendChild(getOrCreateShapeCache().createSVGAnnotations());
		g.appendChild(pathCache.createSVGAnnotation().copy());
		return g;
	}
	
	private AbstractCMElement copyOriginalElements() {
		SVGG g = new SVGG();
		ShapeCache.addList(g, new ArrayList<SVGPath>(pathCache.getOriginalPathList()));
		ShapeCache.addList(g, new ArrayList<SVGText>(textCache.getOrCreateOriginalTextList()));
		g.setStroke("pink");
		return g;
	}

	public Real2Range getPositiveXBox() {
		return positiveXBox;
	}

	public boolean isRemoveWhitespace() {
		return removeWhitespace;
	}

	public void setFileRoot(String fileRoot) {
		this.fileRoot = fileRoot;
	}

	public String getFileRoot() {
		return fileRoot;
	}


	public File getPlotDebug() {
		return plotDebug;
	}

	public void setPlotDebug(File plotDebug) {
		this.plotDebug = plotDebug;
	}

	public AbstractCMElement getExtractedSVGElement() {
		return convertedSVGElement;
	}


	public List<Real2Range> getImageBoxes() {
		List<Real2Range> boxes = new ArrayList<Real2Range>();
		for (SVGImage image : imageCache.getImageList()) {
			Real2Range box = image.getBoundingBox();
			boxes.add(box);
		}
		return boxes;
	}
	
	/** expand bounding boxes and merge
	 * 
	 * @param d
	 */
	public List<Real2Range> getMergedBoundingBoxes(double d) {
		List<Real2Range> boundingBoxes = new ArrayList<Real2Range>();
		for (SVGShape shape : getOrCreateShapeCache().getOrCreateAllShapeList()) {
			Real2Range bbox = shape.getBoundingBox();
			if (bbox != null) {
				bbox.extendBothEndsBy(Direction.HORIZONTAL, d, d);
				bbox.extendBothEndsBy(Direction.VERTICAL, d, d);
				boundingBoxes.add(bbox);
			}
		}
		mergeBoundingBoxes(boundingBoxes);
		return boundingBoxes;
	}

	private void mergeBoundingBoxes(List<Real2Range> boundingBoxes) {
		boolean change = true;
		while (change) {
			Real2Range bbox0 = null;
			Real2Range bbox1 = null;
			Real2Range bbox2 = null;
			change = false;
			for (int i = 0; i < boundingBoxes.size(); i++) {
				bbox0 = boundingBoxes.get(i);
				for (int j = i + 1; j < boundingBoxes.size(); j++) {
					bbox1 = boundingBoxes.get(j);
					Real2Range bbox3 = bbox0.intersectionWith(bbox1);
					if (bbox3 != null && bbox3.isValid()) {
						bbox2 = bbox0.plus(bbox1);
						change = true;
						break;
					}
				}
				if (change) break;
			}
			if (change) {
				boundingBoxes.remove(bbox0);
				boundingBoxes.remove(bbox1);
				boundingBoxes.add(bbox2);
			}
		}
	}

	public void setSplitAtMove(boolean b) {
		this.splitAtMove = b;
	}

	public boolean getSplitAtMove() {
		return this.splitAtMove;
	}

	/** gets feature values.
	 * Mainly counts of occurrences
	 * null ot zero is replaced by ""
	 * 
	 * @param features
	 * @return
	 */
	public List<String> getFeatureValues(List<Feature> features) {
		List<String> featureValues = new ArrayList<String>();
		for (Feature feature : features) {
			String featureValue = getFeatureValue(feature);
			featureValues.add(featureValue == null || featureValue.equals("0") ? "" : featureValue);
		}
		return featureValues;
	}

	public String getFeatureValue(String abbrev) {
		Feature feature = Feature.getFeatureFromAbbreviation(abbrev);
		return (feature == null) ? null : getFeatureValue(feature);
	}
	public String getFeatureValue(Feature feature) {
		String value = null;
		getOrCreateCascadingCaches();
		if (Feature.HORIZONTAL_TEXT_COUNT.equals(feature)) {
			value = String.valueOf(textCache.getOrCreateHorizontalTexts().size());
		} else if (Feature.HORIZONTAL_TEXT_STYLE_COUNT.equals(feature)) {
			value = String.valueOf(textCache.getOrCreateHorizontalTextStyleMultiset().entrySet().size());
		} else if (Feature.VERTICAL_TEXT_COUNT.equals(feature)) {
			value = String.valueOf(textCache.getOrCreateVerticalTexts().size());
		} else if (Feature.VERTICAL_TEXT_STYLE_COUNT.equals(feature)) {
			value = String.valueOf(textCache.getOrCreateVerticalTextStyleMultiset().entrySet().size());
			
		} else if(Feature.PATH_COUNT.equals(feature)) {
			value = String.valueOf(getOrCreateShapeCache().getPathList().size());
		} else if(Feature.CIRCLE_COUNT.equals(feature)) {
			value = String.valueOf(getOrCreateShapeCache().getCircleList().size());
		} else if(Feature.ELLIPSE_COUNT.equals(feature)) {
			value = String.valueOf(getOrCreateShapeCache().getEllipseList().size());
		} else if(Feature.LINE_COUNT.equals(feature)) {
			value = String.valueOf(getOrCreateShapeCache().getLineList().size());
		} else if(Feature.POLYGONS_COUNT.equals(feature)) {
			value = String.valueOf(getOrCreateShapeCache().getPolygonList().size());
		} else if(Feature.POLYLINE_COUNT.equals(feature)) {
			value = String.valueOf(getOrCreateShapeCache().getPolylineList().size());
		} else if(Feature.RECT_COUNT.equals(feature)) {
			value = String.valueOf(getOrCreateShapeCache().getRectList().size());
		} else if(Feature.SHAPE_COUNT.equals(feature)) {
			value = String.valueOf(getOrCreateShapeCache().getShapeList().size());
			
		} else if(Feature.LONG_HORIZONTAL_RULE_COUNT.equals(feature)) {
			value = String.valueOf(lineCache.getOrCreateLongHorizontalLineList().size());
		} else if(Feature.SHORT_HORIZONTAL_RULE_COUNT.equals(feature)) {
			value = String.valueOf(lineCache.getOrCreateShortHorizontalLineList().size());
		} else if(Feature.TOP_HORIZONTAL_RULE_COUNT.equals(feature)) {
			value = String.valueOf(lineCache.getTopHorizontalLineList().size());
		} else if(Feature.BOTTOM_HORIZONTAL_RULE_COUNT.equals(feature)) {
			value = String.valueOf(lineCache.getBottomHorizontalLineList().size());
		} else if(Feature.LONG_HORIZONTAL_RULE_THICKNESS_COUNT.equals(feature)) {
			value = String.valueOf(lineCache.getHorizontalLineStrokeWidthSet().entrySet().size());
			
		} else if(Feature.HORIZONTAL_PANEL_COUNT.equals(feature)) {
			value = String.valueOf(rectCache.getOrCreateHorizontalPanelList().size());
			
		} else {
			LOG.warn("No cache for "+feature);
		}
		return value;
	}

	/* cached boundingBox.
	 * The bbox may be reset 
	 * 
	 */
	public Real2Range getBoundingBox() {
		if (boundingBox == null) {
			getOrCreateCascadingCaches();
			for (AbstractCache cache : cascadingCacheList) {
				if (cache != null) {
					addBoxToTotalBox(cache.getBoundingBox());
				}
			}
			if (boundingBox == null) {
				LOG.trace("null BBox (maybe no primitives) "+(inputSVGElement == null ? "NULL" : inputSVGElement.toXML()));
			}
		}
		return boundingBox;
	}

	private void addBoxToTotalBox(Real2Range box) {
		if (boundingBox == null && box != null) {
			boundingBox = new Real2Range(box);
		} else if (box != null) {
			boundingBox = boundingBox.plus(box);
		} else {
			// leave bounding box as is
		}
	}

	/** aggregates all elements, include derived ones.
	 * 
	 * @return single list of all raw and derived SVGElements
	 */
	@Override
	public List<? extends SVGElement> getOrCreateElementList() {
		if (allElementList == null) {
			allElementList = new ArrayList<SVGElement>();
			getOrCreateCascadingCaches();
			// don't add paths as we have already converted to shapes
//			allElementList.addAll(pathCache.getOrCreateElementList());
			allElementList.addAll(imageCache.getOrCreateElementList());
			List<? extends SVGElement> shapes = getOrCreateShapeCache().getOrCreateElementList();
			for (SVGElement shape : shapes) {
				if (shape instanceof SVGRect || shape instanceof SVGLine || shape instanceof SVGText) {
					LOG.trace("skipped: "+shape);
					// skip
				} else {
					LOG.trace("added: "+shape);
					allElementList.add(shape);
				}
			}
			LOG.trace("rect: "+allElementList.size());
			allElementList.addAll(rectCache.getOrCreateElementList());
			LOG.trace("lines: "+allElementList.size());
			allElementList.addAll(lineCache.getOrCreateElementList()); 
			// this goes last in case it would be hidden
			LOG.trace("text: "+allElementList.size());
			List<? extends SVGElement> elementList = textCache.getOrCreateElementList();
			allElementList.addAll(elementList);
		}
		return allElementList;
	}

	/** creates caches in order
	 * path, text , image are primitives
	 * then path->shape->line->rect
	 * 
	 * each subsequent cache removes the elemnts from any earlier one it uses.
	 * the original SVG element is NOT altered.
	 * 
	 * There is some backtracking required. For example later elements (e.g. borderingRect)
	 * may be removed and this will affect not only the rect list but also the bounding boxes.
	 * These in turn affect the judgment of axial lines and long lines.
	 * 
	 * 
	 */
	public void getOrCreateCascadingCaches() {
		if (cascadingCacheList == null) {
			cascadingCacheList = new ArrayList<AbstractCache>();
			LOG.trace("path");
			cascadingCacheList.add(getOrCreatePathCache());
			LOG.trace("text");
			cascadingCacheList.add(getOrCreateTextCache());
			LOG.trace("image");
			cascadingCacheList.add(getOrCreateImageCache());
			
			// first pass creates raw caches which may be elaborated later
			// GEOMETRY
			LOG.trace("shape");
			cascadingCacheList.add(getOrCreateShapeCache());
			LOG.trace("glyph");
			cascadingCacheList.add(getOrCreateGlyphCache());
			LOG.trace("line");
			cascadingCacheList.add(getOrCreateLineCache());
			LOG.trace("rect");
			cascadingCacheList.add(getOrCreateRectCache());
			LOG.trace("polyline");
			cascadingCacheList.add(getOrCreatePolylineCache());
			// TEXT
			LOG.trace("text");
			// this is slow and may not be required
			if (!ignoreClassList.contains(TextChunkCache.class)) {
				LOG.debug("ignored "+TextChunkCache.class);
				cascadingCacheList.add(getOrCreateTextChunkCache());
			}
			// COMBINED OBJECTS
			LOG.trace("contentbox");
			cascadingCacheList.add(getOrCreateContentBoxCache());
			// tidying heuristics
			LOG.trace("border");
			this.removeBorderingRects();
		}
	}

	public List<Real2Range> getBoundingBoxList() {
		if (boundingBoxList == null) {
			boundingBoxList = new ArrayList<Real2Range>();
			getOrCreateElementList();
			
			for (SVGElement element : allElementList) {
				Real2Range bbox = element.getBoundingBox();
				boundingBoxList.add(bbox);
			}
		}
		return boundingBoxList;
	}

	public List<Real2> getWhitespaces(double dx, double dy) {
		List<Real2Range> boundingBoxList = this.getBoundingBoxList();
		Real2Range box = getBoundingBox()
				.getReal2RangeExtendedInX(dx, dy).getReal2RangeExtendedInY(dx, dy);
		RealRange xRange = box.getRealRange(Direction.HORIZONTAL);
		RealRange yRange = box.getRealRange(Direction.VERTICAL);
		List<Real2> whitespaces = new ArrayList<Real2>();
		for (double xx = xRange.getMin(); xx < xRange.getMax(); xx+=dx) {
			for (double yy = yRange.getMin(); yy < yRange.getMax(); yy+=dy) {
				boolean inside = false;
				Real2[] xy = new Real2[]{
						new Real2(xx+dx/2, yy+dx/2),
						new Real2(xx+dx/2, yy-dx/2),
						new Real2(xx-dx/2, yy+dx/2),
						new Real2(xx-dx/2, yy-dx/2),
				};
				for (int k = 0; k < boundingBoxList.size(); k++) {
					for (Real2 xy0 : xy) {
						Real2Range bbox = boundingBoxList.get(k);
						if (bbox != null && bbox.includes(xy0)) {
							inside = true;
							break;
						}
					}
				}
				if (!inside) {
					whitespaces.add(new Real2(xx, yy));
				}
			}
		}
		return whitespaces;
	}

	public SuperPixelArray getWhitespaceSuperPixelArray(double dx, double dy) {
		List<Real2> whitespaces = getWhitespaces(dx, dy);
		SuperPixelArray superPixelArray = new SuperPixelArray(new Int2Range(this.getBoundingBox()));
		for (Real2 whitespace : whitespaces) {
			superPixelArray.setPixel(1, Int2.getInt2(whitespace));
		}
		return superPixelArray;
	}

	public SuperPixelArray getWhitespaceSuperPixelArray(List<Real2Range> boundingBoxLists) {
		SuperPixelArray superPixelArray = new SuperPixelArray(new Int2Range(this.getBoundingBox()));
		for (Real2Range boundingBox : boundingBoxLists) {
			superPixelArray.setPixels(1, new Int2Range(boundingBox));
		}
		return superPixelArray;
	}

	public SVGG createWhitespaceG(double dx, double dy) {
		whitespaceSpixels = getWhitespaces(dx, dy);
		SVGG gg = new SVGG();
		for (Real2 xy : whitespaceSpixels) {
			gg.appendChild(new SVGCircle(xy, dx/2.));
		}
		return gg;
	}

	@Override
	public String toString() {
		String s = ""
		+"image: "+String.valueOf(imageCache)+"\n"
		+"path: "+String.valueOf(pathCache)+"\n"
		+"text: "+String.valueOf(textCache)+"\n"
		+"line: "+String.valueOf(lineCache)+"\n"
		+"rect: "+String.valueOf(rectCache)+"\n"
		+"shape: "+String.valueOf(getOrCreateShapeCache()+"\n");
		if (abstractCacheList != null) {
			for (AbstractCache abstractCache : abstractCacheList) {
				s += abstractCache.getClass().getSimpleName()+": "+String.valueOf(abstractCache)+"\n";
			}
		}
		return s;
	}

	public void setContentBoxCache(ContentBoxCache contentBoxCache) {
		
	}
	
	public void addCache(AbstractCache abstractCache) {
		if (abstractCacheList == null) {
			abstractCacheList = new ArrayList<AbstractCache>();
		}
		if (!abstractCacheList.contains(abstractCache)) {
			abstractCacheList.add(abstractCache);
		}
		
	}

	public void removeBorderingRects() {
		Real2Range outerBbox = this.getBoundingBox();
		List<SVGRect> rectList = rectCache.getOrCreateRectList();
		LOG.trace("pre removal rect bbox "+outerBbox + "; "+rectCache.getOrCreateRectList().size());
		boolean removed = false;
		for (int i = rectList.size() - 1; i >= 0; i--) {
			SVGRect rect = rectList.get(i);
			Real2Range bbox = rect.getBoundingBox();
			if (outerBbox.isEqualTo(bbox, outerBoxEps)) {
				removed = rectCache.remove(rect);
				LOG.info("removed outer rect "+rect.toXML());
			}
		}
		if (removed) {
			rectCache.clearAll();
			outerBbox = rectCache.getBoundingBox();
			LOG.trace("post removal rect bbox "+outerBbox + "; "+rectCache.getOrCreateRectList().size());
		}
		
	}
	
	@Override
	public void clearAll() {
		superClearAll();
		imageCache = null;
		pathCache = null;
		textCache = null;
		lineCache = null;
		rectCache = null;
		shapeCache = null;
		contentBoxCache = null;
		abstractCacheList = null;
		
		positiveXBox = null;

		fileRoot = null;
		inputSVGElement = null;

		convertedSVGElement = null;
		allElementList = null;
		boundingBoxList = null;

		whitespaceSpixels = null;

	}

	public void addContentBoxCache(ContentBoxCache contentBoxCache) {
		throw new RuntimeException(" addContentBoxCache NYI");
		
	}

	/** creates a pseudo original containing element.
	 * COPIES elements so elements not affected.
	 * typical usage is that elementList are extracted from some other object
	 * 
	 * @param elementList list of elements
	 * @return
	 */
	public static AbstractCMElement createContainingElement(List<SVGElement> elementList) {
		AbstractCMElement containingElement = new SVGG();
		for (AbstractCMElement element : elementList) {
			containingElement.appendChild(element.copy());
		}
		return containingElement;
	}

	/** reads from a list of elements.
	 * COPIED so can be used multiple times.
	 * @param elementList
	 */
	public void readGraphicsComponentsAndMakeCaches(List<SVGElement> elementList) {
		AbstractCMElement element = ComponentCache.createContainingElement(elementList);
		readGraphicsComponentsAndMakeCaches(element);
	}

	public static ComponentCache readAndCreateComponentCache(File svgFile) {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(svgFile);
		ComponentCache componentCache = new ComponentCache(); 
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		return componentCache;
	}

	public MathCache getOrCreateMathCache() {
		if (mathCache == null) {
			this.mathCache = new MathCache(this);
		}
		return mathCache;
	}

	List<? extends SVGElement> extractAndDisplayComponents(File infile, File outfile) {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(infile);
		readGraphicsComponentsAndMakeCaches(svgElement);
		SVGSVG.wrapAndWriteAsSVG(getOrCreateConvertedSVGElement(), outfile);
		List<? extends SVGElement> componentList = getOrCreateElementList();
		return componentList;
	}

	public static ComponentCache createComponentCache(File file) {
		ComponentCache componentCache = null;
		if (file != null) {
			SVGElement svgElement = SVGElement.readAndCreateSVG(file);
			if (svgElement != null) {
				componentCache = new ComponentCache();
				componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
			}
		}
		return componentCache;
	}

}

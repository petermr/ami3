package org.contentmine.graphics.svg.layout;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.cache.PageCache;
import org.contentmine.graphics.svg.cache.ShapeCache;
import org.contentmine.graphics.svg.cache.TextCache;

import nu.xom.Attribute;

/** a footer (can be on any/every page
 * 
 * @author pm286
 *
 */
public class SVGPubstyleColumn extends AbstractPubstyle {

	private static final Logger LOG = Logger.getLogger(SVGPubstyleColumn.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public enum ColumnPosition{
		ANY,
		LEFT,
		MIDDLE,
		RIGHT,
		WIDE;
	}
	
	private static final String XPATH = "xpath";
	public final static String SVG_CLASSNAME = "column";

	private ColumnPosition columnPosition;
	
	public SVGPubstyleColumn() {
		super();
		init();
	}
	
	public SVGPubstyleColumn(SVGElement element) {
		super(element);
	}

	public SVGPubstyleColumn(SVGElement element, ColumnPosition columnPosition) {
		this(element);
		this.columnPosition = columnPosition;
	}

	protected void init() {
		super.init();
		columnPosition = ColumnPosition.ANY;
	}
	
	@Override
	protected String getPubstyleClassName() {
		return SVG_CLASSNAME;
	}

	public void setXPath(String columnXpath) {
		this.addAttribute(new Attribute(XPATH, columnXpath));
	}

	/** 
	 * 
	 * @param svgElement TODO
	 * @return
	 */
	public List<DocumentChunk> extractDocumentChunksInBox(PageCache pageCache) {
		List<DocumentChunk> documentChunks1;
		if (pageCache == null) {
			throw new RuntimeException("NULL pageCache");
		}
		
		TextCache textCache = pageCache.getOrCreateTextCache(1);
		List<SVGText> texts = extractTextsContainedInBox(textCache.getOrCreateCurrentTextList());
		containingPubstyle.setExtractedTexts(texts);
		ShapeCache shapeCache = pageCache.getOrCreateShapeCache();
		List<SVGShape> shapes = extractShapesContainedInBox(shapeCache.getShapeList());
		LOG.debug("PATHS: "+shapes);
		containingPubstyle.setExtractedShapes(shapes);
		documentChunks1 = matchDocumentChunks();
		// this is just debug
//		int ipage = containingPubstyle.currentPage;
//		String dirRoot = containingPubstyle.dirRoot;
//		SVGSVG.wrapAndWriteAsSVG(texts, new File("target/pubstyle/"+dirRoot+"/page"+ipage+".texts.svg"));
//		SVGSVG.wrapAndWriteAsSVG(paths, new File("target/pubstyle/"+dirRoot+"/page"+ipage+".paths.svg"));
		return documentChunks1;
	}

	private List<SVGText> extractTextsContainedInBox(List<SVGText> textList0) {
		List<SVGElement> elementsInBox = SVGElement.extractElementsContainedInBox(textList0, this.getBoundingBox());
		extractedTexts = SVGText.extractTexts(elementsInBox);
		return extractedTexts;
	}

	private List<SVGShape> extractShapesContainedInBox(List<SVGShape> shapeList0) {
		List<SVGElement> elementsInBox = SVGElement.extractElementsContainedInBox(shapeList0, this.getBoundingBox());
		extractedShapes = SVGShape.extractShapes(elementsInBox);
		return extractedShapes;
	}


	/**  
	 * 	 * 
	 * @param svgElement TODO
	 * @return
	 */
	public List<DocumentChunk> extractDocumentChunksInBox(SVGElement svgElement) {
		List<DocumentChunk> documentChunks1;
		if (svgElement == null) {
			LOG.error("Null SVGElement");
			throw new RuntimeException("NULL svgElement");
		}
		
		List<SVGText> texts = extractTextsContainedInBox(svgElement);
		containingPubstyle.setExtractedTexts(texts);
		List<SVGPath> paths = extractPathsContainedInBox(svgElement);
		LOG.debug("PATHS: "+paths);
		containingPubstyle.setExtractedPaths(paths);
		documentChunks1 = matchDocumentChunks();
		// this is just debug
//		int ipage = containingPubstyle.currentPage;
//		String dirRoot = containingPubstyle.dirRoot;
//		SVGSVG.wrapAndWriteAsSVG(texts, new File("target/pubstyle/"+dirRoot+"/page"+ipage+".texts.svg"));
//		SVGSVG.wrapAndWriteAsSVG(paths, new File("target/pubstyle/"+dirRoot+"/page"+ipage+".paths.svg"));
		return documentChunks1;
	}

//	public void setContainingPubstyle(SVGPubstyle svgPubstyle) {
//		this.containingPubstyle = svgPubstyle;
//	}


}

package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.fonts.StyleRecordFactory;
import org.contentmine.graphics.svg.fonts.StyleRecordSet;
import org.contentmine.graphics.svg.layout.DocumentChunk;
import org.contentmine.graphics.svg.layout.SVGPubstyle;
import org.contentmine.graphics.svg.util.SuperPixelArray;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** cache of components relevant to a single page.
 * 
 * 
 * @author pm286
 *
 */
public class PageCache extends ComponentCache {
	private static final Logger LOG = Logger.getLogger(PageCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static Double DEFAULT_XMAX = 600.0;
	public final static Double DEFAULT_YMAX = 800.0;

	private File inputSvgFile;
	private Integer serialNumber;
	private DocumentCache documentCache;
	private PageHeaderCache headerCache;
	private PageFooterCache footerCache;
	private PageLeftSidebarCache leftSidebarCache;
	private PageRightSidebarCache rightSidebarCache;
	private PageComponentCache bodyCache;
	private String basename;
	private List<SVGRect> rectsList;
	private PageLayout pageLayout;
	private double paraSepRatio;

	public PageCache() {
		setDefaults();
	}
	
	public PageCache(DocumentCache documentCache) {
		this();
		this.setDocumentCache(documentCache);
	}
	
	private void setDefaults() {
		paraSepRatio = 1.7; 
	}

	@Override
	public void readGraphicsComponentsAndMakeCaches(AbstractCMElement svgElement) {
		super.readGraphicsComponentsAndMakeCaches(svgElement);
		ensurePageComponentCaches();
	}
	
	public void readGraphicsComponentsAndMakeCaches(File inputSvgFile) {
		this.setSVGFile(inputSvgFile);
		inputSVGElement = SVGElement.readAndCreateSVG(inputSvgFile);
		super.readGraphicsComponentsAndMakeCaches(inputSVGElement);
		ensurePageComponentCaches();
	}
	

	private void ensurePageComponentCaches() {
		// done in this order so the margins can inform the body size
		ensureTopBottomLeftRightMarginCaches();
		// this must be the order at present as body is defined 
		// by the others
		getOrCreateBodyCache();
	}

	void ensureTopBottomLeftRightMarginCaches() {
		getOrCreateHeaderCache();
		getOrCreateFooterCache();
		getOrCreateLeftSidebarCache();
		getOrCreateRightSidebarCache();
	}

	public void getOrCreateBodyCache() {
		if (bodyCache == null) {
			bodyCache = new PageBodyCache(this);
		}
	}

	public PageHeaderCache getOrCreateHeaderCache() {
		if (headerCache == null) {
			headerCache = new PageHeaderCache(this);
		}
		return headerCache;
	}

	public PageFooterCache getOrCreateFooterCache() {
		if (footerCache == null) {
			footerCache = new PageFooterCache(this);
		}
		return footerCache;
	}

	public PageLeftSidebarCache getOrCreateLeftSidebarCache() {
		if (leftSidebarCache == null) {
			leftSidebarCache = new PageLeftSidebarCache(this);
		}
		return leftSidebarCache;
	}

	public PageRightSidebarCache getOrCreateRightSidebarCache() {
		if (rightSidebarCache == null) {
			rightSidebarCache = new PageRightSidebarCache(this);
		}
		return rightSidebarCache;
	}

	AbstractCMElement createSummaryBoxes(File svgFile) {
		LOG.trace("CREATE SUMMARY BOXES");
		this.inputSvgFile = svgFile;
		Multiset<Int2Range> intBoxes1 = HashMultiset.create();
		AbstractCMElement boxg = this.getStyledBoxes(intBoxes1);
		getOrCreateExtractedSVGElement().appendChild(boxg);
		Multiset<Int2Range> intBoxes = intBoxes1;
		List<Multiset.Entry<Int2Range>> sortedIntBoxes1 = MultisetUtil.createListSortedByCount(intBoxes);
		for (Multiset.Entry<Int2Range> box : sortedIntBoxes1) {
			int count = box.getCount();
			if (count > 1) {
				SVGRect rect = SVGRect.createFromReal2Range(Real2Range.createReal2Range(box.getElement()));
				rect.setStrokeWidth((double) count / 3.);
				rect.setFill("none");
				rect.setStroke("red");
				getOrCreateExtractedSVGElement().appendChild(rect);
			}
		}
		return boxg;
	}

	private AbstractCMElement getStyledBoxes(Multiset<Int2Range> intBoxes) {
		AbstractCMElement g = new SVGG();
		if (inputSVGElement != null) {
			List<SVGText> svgTexts = SVGText.extractSelfAndDescendantTexts(inputSVGElement); 
			StyleRecordFactory styleRecordFactory = new StyleRecordFactory();
			StyleRecordSet styleRecordSet = styleRecordFactory.createStyleRecordSet(svgTexts);
			g = styleRecordSet.createStyledTextBBoxes(svgTexts);
			List<SVGRect> boxes = SVGRect.extractSelfAndDescendantRects(g);
			for (SVGRect box : boxes) {
				Int2Range intBox = new Int2Range(box.getBoundingBox());
				intBoxes.add(intBox);
			}
		}
		return g;
	}

	public void setSerialNumber(int serial) {
		this.serialNumber = serial;
	}
	
	public Integer getSerialNumber() {
		if (serialNumber == null) {
			// ?
		}
		return serialNumber;
	}
	
	public File getSVGFile() {
		return inputSvgFile;
	}

	public void setSVGFile(File svgFile) {
		this.inputSvgFile = svgFile;
	}

	public DocumentCache getDocumentCache() {
		return documentCache;
	}

	public void setDocumentCache(DocumentCache documentCache) {
		this.documentCache = documentCache;
	}

	public AbstractCMElement getOrCreateExtractedSVGElement() {
		if (convertedSVGElement == null) {
			convertedSVGElement = new SVGG();
		}
		return convertedSVGElement;
	}

	/** superPixelArray is the blocks taken up by stuff.
	 * 
	 * @param outDir
	 * @param svgFile
	 * @return
	 */
	public SuperPixelArray createSuperpixelArray() {
		createTextCache();
		Real2Range bbox = Real2Range.createTotalBox(getBoundingBoxList());
		LOG.trace(">> "+bbox+" "+getBoundingBoxList().size());
		LOG.debug("t0 "+System.currentTimeMillis());
		SuperPixelArray superPixelArray = new SuperPixelArray(new Int2Range(bbox));
		superPixelArray.setPixels(1, getBoundingBoxList());
		LOG.debug("t1 "+System.currentTimeMillis());
		return superPixelArray;
	}

	private void createTextCache() {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(inputSvgFile);
		LOG.trace("t00 "+System.currentTimeMillis()/1000);
		readGraphicsComponentsAndMakeCaches(svgElement);
		LOG.trace("t01 "+System.currentTimeMillis()/1000);
		TextCache textCache = getOrCreateTextCache();
	}

	private String getBaseName(File svgFile) {
		return svgFile == null ? null : FilenameUtils.getBaseName(svgFile.toString());
	}

	public void setSvgFile(File svgFile) {
		this.inputSvgFile = svgFile;
		basename = getBaseName(svgFile);
	}

	public File getSvgFile() {
		return inputSvgFile;
	}

	public AbstractCMElement getExtractedSVGElement() {
		return convertedSVGElement;
	}

	void readPageLayoutAndMakeBBoxesAndMargins(PageLayout pageLayout) {
		ensurePageComponentCaches();
		bodyCache.boundingBox = pageLayout.getBodyLimits();
		LOG.trace("body "+bodyCache.boundingBox);
		this.headerCache.setYMax(bodyCache.boundingBox.getYMin());
		this.footerCache.setYMin(bodyCache.boundingBox.getYMax());
		this.leftSidebarCache.setXMax(bodyCache.boundingBox.getXMin());
		this.rightSidebarCache.setXMin(bodyCache.boundingBox.getXMax());
		rectsList = pageLayout.getRectList(PageLayout.BODY);
		LOG.trace("made rects: "+rectsList.size());
	}

	public String getBasename() {
		return basename;
	}

	public void setBasename(String basename) {
		this.basename = basename;
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("body: "+bodyCache.toString()+"\n");
		sb.append("header: "+headerCache.toString()+"\n");
		sb.append("footer: "+footerCache.toString()+"\n");
		sb.append("left: "+leftSidebarCache.toString()+"\n");
		sb.append("right: "+rightSidebarCache.toString()+"\n");
		return sb.toString();
	}

	public SVGElement createSVGElementFromComponents() {
		SVGElement g = new SVGG();
		addElementAndChildren(g, bodyCache);
		addElementAndChildren(g,headerCache);
		addElementAndChildren(g,footerCache);
		addElementAndChildren(g,leftSidebarCache);
		addElementAndChildren(g,rightSidebarCache);
		g.appendChild(inputSVGElement.copy());
		if (rectsList !=  null) {
			for (SVGRect rect : rectsList) {
				g.appendChild(rect.copy());
			}
		}
		return g;
	}

	private void addElementAndChildren(AbstractCMElement g, PageComponentCache cache) {
		g.appendChild(cache.getOrCreateConvertedSVGElement().copy());
		for (AbstractCMElement element : cache.getOrCreateAllElementList()) {
			g.appendChild(element.copy());
		}
	}

	public void setPageLayout(PageLayout pageLayout) {
		if (pageLayout == null) {
			throw new RuntimeException("null pageLayout");
		}
		this.pageLayout = pageLayout;
	}

	/** this is a default two-column layout.
	 * 
	 * @return
	 */
	public List<Real2Range> getDefault2ColumnClipBoxes() {
		return Arrays.asList(
			new Real2Range[] {
				new Real2Range(new RealRange(13., 255.), new RealRange(0,999)),
				new Real2Range(new RealRange(260., 999.), new RealRange(0,999)),
			});
	}

	/** general Lyout (NYI)
	 * 
	 * @return
	 */
	public List<Real2Range> getSpecificClipBoxes() {
		List<Real2Range> clipBoxes = new ArrayList<Real2Range>();
		if (pageLayout != null) {
			clipBoxes = pageLayout.getClipBoxes();
//			clipBoxes = Arrays.asList(
//				new Real2Range[] {
//					new Real2Range(new RealRange(13., 255.), new RealRange(0,999)),
//					new Real2Range(new RealRange(260., 999.), new RealRange(0,999)),
//				});
		}
		return clipBoxes;
	}
	
	public File getInputSVGFile() {
		return inputSvgFile;
	}

	public HtmlElement createHtmlElement() {
		HtmlElement html = getOrCreateTextCache().createHtmlElementNew();
		return html;
	}

	public File debugSvgElementToSVGFile(String topDirName, String dirRoot, int page) {
		SVGElement svgElement = createSVGElementFromComponents();
		File file = new File(topDirName + dirRoot + "/pageCache"+page+".svg");
		SVGSVG.wrapAndWriteAsSVG(svgElement, file);
		return file;
	}

	public File debugChunksToSVGFile(SVGPubstyle pubstyle, String topDirName, String dirRoot, int page) {
		List<DocumentChunk> documentChunks = pubstyle.createDocumentChunks(this);
		File file = new File(topDirName + dirRoot + "/page"+page+".svg");
		SVGSVG.wrapAndWriteAsSVG(documentChunks, file);
		return file;
	}

	public File debugShapesToSVGFile(String topDirName, String dirRoot, int page) {
		List<SVGRect> rectList = getOrCreateRectCache().getOrCreateRectList();
		SVGLineList lineList = getOrCreateLineCache().getOrCreateLongHorizontalLineList();
		List<SVGShape> shapeList = getOrCreateShapeCache().getShapeList();
		List<SVGElement> shapes = new ArrayList<SVGElement>();
		shapes.addAll(rectList);
		shapes.addAll(lineList.getLineList());
		shapes.addAll(shapeList);
		File file = new File(topDirName + dirRoot + "/shapes"+page+".svg");
		SVGSVG.wrapAndWriteAsSVG(shapes, file);
		return file;
	}
	
	/** needs integrating with current textList.
	 * 
	 * @param textList
	 * @return
	 */
	public HtmlDiv createHtmlFromPage(List<SVGText> textList) {
	
//		HtmlHtml html = new HtmlHtml();
		HtmlDiv div = new HtmlDiv();
//		html.getOrCreateBody().appendChild(div);
		SVGText lastText = null;
		HtmlP p = null;
		for (SVGText text : textList) {
			if (lastText == null) {
				p = addNewPara(div);
			} else {
				
				p = possiblyCreatePara(div, lastText, p, text);
			}
			lastText = text;
			addTextToSpanToP(p, " "); // interword space
			addTextToSpanToP(p, text.getText());
		}
		return div;
	}

	private HtmlP possiblyCreatePara(HtmlDiv div, SVGText lastText, HtmlP p, SVGText text) {
		double dy = text.getY() - lastText.getY();
		double fontSize = lastText.getFontSize();
		if (dy > fontSize * paraSepRatio) {
			p = addNewPara(div);
		}
		return p;
	}

	
	private static void addTextToSpanToP(HtmlP p, String text2) {
		HtmlSpan span = new HtmlSpan();
		span.appendChild(text2);
		p.appendChild(span);
	}

	private static HtmlP addNewPara(HtmlDiv div) {
		HtmlP p = new HtmlP();
		div.appendChild(p);
		return p;
	}

	public double getParaSepRatio() {
		return paraSepRatio;
	}

	public void setParaSepRatio(double paraSepRatio) {
		this.paraSepRatio = paraSepRatio;
	}

	public HtmlDiv createHTMLFromTextList() {
		TextCache textCache = getOrCreateTextCache();
		List<SVGText> textList = textCache.getOrCreateCurrentTextList();
		HtmlDiv div = createHtmlFromPage(textList);
		return div;
	}



}

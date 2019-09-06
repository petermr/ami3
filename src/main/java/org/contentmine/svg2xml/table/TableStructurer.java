package org.contentmine.svg2xml.table;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntRangeArray;
import org.contentmine.eucl.euclid.Line2;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlBr;
import org.contentmine.graphics.html.HtmlCaption;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHr;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTfoot;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.html.HtmlUl;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.linestuff.BoundingBoxManager;
import org.contentmine.graphics.svg.rule.Rule;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalElement;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalRule;
import org.contentmine.graphics.svg.rule.vertical.VerticalRule;
import org.contentmine.graphics.svg.text.build.Phrase;
import org.contentmine.graphics.svg.text.build.PhraseChunk;
import org.contentmine.graphics.svg.text.build.TextChunk;
import org.contentmine.graphics.svg.text.build.TextChunkList;
import org.contentmine.graphics.svg.text.line.ScriptLine;
import org.contentmine.graphics.svg.text.line.TextLine;
import org.contentmine.graphics.svg.text.structure.TextStructurer;

import nu.xom.Attribute;

public class TableStructurer {
	private static final String XY = "xy";
	private static final Logger LOG = Logger.getLogger(TableStructurer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final double PIXEL_GAP = 1.5;
	private static final String EMPTY_CHILD = "";
	private static final String WIDE = "w";
	private static final String LONG = "b";
	private static final String FONT = "f";
	private static final Double LINE_MAX_THICK = 10.0; // thick line
	// the fontSize-independent with of an indented space 
	public static final double SPACE_WIDTH = 1.0;
	public static final String LEADING_SPACE = "~";
	

	private TextChunkList totalTextChunkList;
	private int maxColumns;
	private ColumnManagerList columnManagerList;
	private String title;
	private HtmlTable htmlTable;
	private HtmlHtml html;
	private HtmlTbody tableBody;
	private TextStructurer textStructurer;
	private List<HorizontalRule> horizontalRulerList;
	private List<SVGElement> horizontalElementList;
	private List<VerticalRule> verticalRulerList;
	private Real2Range bboxRuler;
	private Map<String, SVGElement> horizontalElementByCode;
	private String rowCodes;
	private double minRulerSpacingY = 5.; // below this counts as wide line
	private List<TableSection> tableSectionList;
	private TableTitle tableTitle;
	private BoundingBoxManager headerBBoxManager;
	private BoundingBoxManager titleBBoxManager;
	private BoundingBoxManager bodyBBoxManager;
	private BoundingBoxManager footerBBoxManager;
	private double yTolerance = 0.2;
	private List<SVGShape> shapeList;
	private boolean hasZeroDimensionalShapes = false;
	private TableGrid tableGrid;
	private double epsilon = 0.01;
	private List<SVGRect> spanningRects;
	private SVGRect outerRect;
	private List<SVGRect> rectList;
	private HtmlThead thead;
	private HtmlUl titleUl;

	
	public TableStructurer(TextChunk textChunk) {
		this.totalTextChunkList = new TextChunkList(textChunk);
		maxColumns = textChunk.getMaxColumns();
	}

	public Real2Range getTitleBBox() {return titleBBoxManager.getTotalBox();}
	public Real2Range getHeaderBBox() {return headerBBoxManager.getTotalBox();}
	public Real2Range getBodyBBox() {return bodyBBoxManager.getTotalBox();}
	public Real2Range getFooterBBox() {return footerBBoxManager.getTotalBox();}
	
	public String createTitle() {
		StringBuilder titleSB = new StringBuilder();
		titleBBoxManager = new BoundingBoxManager();
		if (tableSectionList != null && tableSectionList.size() > 0) {
			TableSection titleSection = tableSectionList.get(0);
			TextChunk sectionPhraseListList = titleSection.getOrCreatePhraseListList();
			titleUl = sectionPhraseListList.getPhraseListUl();
			try {
				XMLUtil.debug(titleUl, new FileOutputStream("target/table/debug/title.html"), 1);
			} catch (Exception e) {
				// 
			}
			
			List<HorizontalElement> horizontalList = titleSection.getHorizontalElementList();
			if (tableTitle != null) {
				titleSB.append(tableTitle.getTitle());
			}
			for (int i = 0; i < horizontalList.size(); i++) {
				SVGElement horizontal = (SVGElement) horizontalList.get(i);
				titleSB.append(horizontal);
				Real2Range bbox = horizontal.getBoundingBox();
				titleBBoxManager.add(bbox);
			}
		} else {
			LOG.trace("NO table sections");
			return "Null title";
		}
		
		title = titleSB.toString();
		return title;
	}

	private ColumnManagerList ensureColumnManagerList() {
		if (columnManagerList == null) {
			this.columnManagerList = new ColumnManagerList();
			for (int i = 0; i < maxColumns; i++) {
				ColumnManager columnManager = new ColumnManager();
				columnManagerList.add(columnManager);
			}
		}
		return columnManagerList;
	}
	
	private ColumnManager getColumnManager(int iCol) {
		ensureColumnManagerList();
		return columnManagerList.get(iCol);
	}

	public HtmlTable getHtmlTable() {
		return htmlTable;
	}

	private HtmlHtml createHtmlWithTable() {
		html = new HtmlHtml();
		createHtmlHead();
		createHtmlTable();
		html.appendChild(htmlTable);
		html.appendChild(createFirefoxWarning());		
		return html;
	}

	private void createHtmlHead() {
		HtmlHead head = new HtmlHead();
		html.appendChild(head);
		addStyle(head);
	}

	private void addStyle(HtmlHead head) {
		HtmlStyle style = new HtmlStyle();
		style.addCss("table {border : solid 1pt;}");
		style.addCss("caption {background : #bbffff;}");
		style.addCss("th {background : #ffdddd; border : solid blue 2pt;}");
		style.addCss("tr {border : solid 1pt;}");
		style.addCss("td {border : solid 1pt;}");
		style.addCss("tfoot {border : solid 2pt; background : #ffddff;}");
		style.addCss(".firefox {font-size : 6pt; font-style : italic;}");
		head.appendChild(style);
	}

	public HtmlTable createHtmlTable() {
		htmlTable = new HtmlTable();
		addTitle();
		addEmptyHtmlHead();
		
		addFooter();
		addHeader();
		createBody0();
		createBody();
		html.appendChild(createFirefoxWarning());
		
		return htmlTable;
	}

	private HtmlDiv createFirefoxWarning() {
		HtmlDiv div = new HtmlDiv();
		HtmlP p = new HtmlP("Note: If strange characters appear in Firefox, configure it for UTF-8 or use another browser");
		div.appendChild(p);
		div.addAttribute(new Attribute("class", "firefox"));
		return div;
	}

	private void createBody0() {
		if (tableSectionList == null || tableSectionList.size() < 3) {
			LOG.trace("ERROR: no Body section");
			return;
		}
		TableSection bodySection = tableSectionList.get(2);
		AbstractCMElement bodyPhraseListList= bodySection.getOrCreatePhraseListList();
	}

	private void createBody() {
		tableBody = new HtmlTbody();
		addBody(tableBody);
	}

	private void addBody(HtmlTbody tableBody) {
		List<HtmlTr> rows = null;
		bodyBBoxManager = new BoundingBoxManager();
		TextChunk bodyPhraseListList= new TextChunk();
		if (tableSectionList == null || tableSectionList.size() < 3) {
			LOG.error("ERROR: no Body section");
		} else {
			TableSection bodySection = tableSectionList.get(2);
			for (HorizontalElement element : bodySection.getHorizontalElementList()) {
				bodyBBoxManager.add(((SVGElement)element).getBoundingBox());
				if (element instanceof PhraseChunk) {
					bodyPhraseListList.add(new PhraseChunk((PhraseChunk) element));
				} else {
					LOG.trace("Omitted ruler: "+element);
				}
			}
		}
		rows = createBodyTableRows(bodyPhraseListList);
		HtmlTr row1 = new HtmlTr();
		
//		addNOTE(tableBody, row1);
		
		for (HtmlTr row : rows) {
			tableBody.appendChild(row);
		}
		htmlTable.appendChild(tableBody);

	}

	private void addNOTE(HtmlTbody tableBody, HtmlTr row1) {
		HtmlTd td = new HtmlTd();
		row1.appendChild(td);
		td.appendChild("BODY FOLLOWS");
		tableBody.appendChild(row1);
	}

	private void addEmptyHtmlHead() {
		thead = new HtmlThead();
		htmlTable.appendChild(thead);
	}

	private void addTitle() {
		createTitle();
		HtmlCaption caption = new HtmlCaption();
		caption.appendChild(title);
		htmlTable.appendChild(caption);
	}

	private void addHeader() {
		headerBBoxManager = new BoundingBoxManager();
		TableSection tableHeaderSection = null;
		if (tableSectionList != null && tableSectionList.size() > 1) {
			tableHeaderSection =tableSectionList.get(1);
			for (HorizontalElement element : tableHeaderSection.getHorizontalElementList()) {
				headerBBoxManager.add(((SVGElement)element).getBoundingBox());
				if (element instanceof HorizontalRule) {
					addRulerToHead((HorizontalRule) element);
				} else {
					addPhraseList((PhraseChunk) element);
				}
			}
		}
		List<HtmlTr> trList = createHeaderRows(tableHeaderSection);

	}
	
	private List<HtmlTr> createHeaderRows(TableSection tableHeaderSection) {
		List<HtmlTr> rows = new ArrayList<HtmlTr>();
		if (tableHeaderSection == null) return rows;
		TextChunk headerPhraseListList = new TextChunk();
		for (HorizontalElement element : tableHeaderSection.getHorizontalElementList()) {
			if (element instanceof PhraseChunk) {
				PhraseChunk phraseList = (PhraseChunk) element;
				headerPhraseListList.add(phraseList);
				List<HtmlTh> thList = phraseList.getThList();
			} else {
//				LOG.error("Omitted ruler: "+element);
			}
		}

		
		// find margins
		IntRangeArray bestWhitespaces = headerPhraseListList.getBestWhitespaceRanges();
		LOG.trace("BestWhite (R margin) "+bestWhitespaces);
		IntRangeArray bestColumnRanges = headerPhraseListList.getBestColumnRanges();
		LOG.trace("BestColumn () "+bestColumnRanges);
		
		for (PhraseChunk phraseList : headerPhraseListList) {
			HtmlTr row = createTableRow(phraseList);
			rows.add(row);
		}
		return rows;

	}


	private void addPhraseList(PhraseChunk phraseList) {
		List<HtmlTh> thList = phraseList.getThList();
		HtmlTr tr = new HtmlTr();
		for (HtmlTh th : thList) {
			tr.appendChild(th);
		}
		thead.appendChild(tr);
		LOG.trace(">TH>"+phraseList.getStringValue());
	}

	private void addRulerToHead(HorizontalRule ruler) {
		Real2 xy = ruler.getXY();
		HtmlTr tr = new HtmlTr();
		HtmlTh th = new HtmlTh();
		tr.appendChild(th);
		HtmlTd td = new HtmlTd();
		th.appendChild(td);
		HtmlHr hr = new HtmlHr();
		td.appendChild(hr);
		hr.addAttribute(new Attribute(XY, xy.toString()));
		hr.setStyle("color:#00ff77;height:2px");
		thead.appendChild(tr);
	}

	private void addFooter() {
		HtmlTfoot foot = new HtmlTfoot();
		htmlTable.appendChild(foot);
		
		HtmlTr footRow = new HtmlTr();
		foot.appendChild(footRow);
		HtmlTd footTd = new HtmlTd();
		footRow.appendChild(footTd);
		footTd.addAttribute(new Attribute("colspan", String.valueOf(maxColumns)));
		footerBBoxManager = new BoundingBoxManager();
		if (tableSectionList != null && tableSectionList.size() > 3) {
			TableSection footerSection =tableSectionList.get(3);
			for (HorizontalElement element : footerSection.getHorizontalElementList()) {
				footerBBoxManager.add(((SVGElement)element).getBoundingBox());
				if (element instanceof HorizontalRule) {
					LOG.trace("HRULE in footer");
				} else {
					PhraseChunk phraseList = (PhraseChunk) element;
					footTd.appendChild(phraseList.toString());
					footTd.appendChild(new HtmlBr());
				}
			}
		}
	}
	
	public void analyzeShapeList() {
		getOrCreateShapeList();
		removeOuterBox();
		debugShapesGraphically();
		createPotentialHorizontalSeparators();
		getOrCreateVerticalRulerList();
	}

	private void debugShapesGraphically() {
		SVGG g = new SVGG();
		int i = 0; 
		String[] color = {"red", "green", "blue", "yellow", "cyan", "magenta", "black"};
		for (SVGElement shape : shapeList) {
			SVGElement shapeNew = (SVGElement) shape.copy();
			shapeNew.setFill(color[i++ % color.length]);
			shapeNew.setFill("none");
			shapeNew.setStroke("black");
			shapeNew.setStrokeWidth(2.);
			shapeNew.setOpacity(0.1);
			g.appendChild(shapeNew);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/debug/shapes"+(int)(100*Math.random())+".svg"));
	}

	private void removeOuterBox() {
		Real2Range bbox = SVGElement.createBoundingBox(shapeList);
		if (bbox != null) {
			RealRange xRange = bbox.getXRange();
			RealRange yRange = bbox.getYRange();
			List<SVGRect> horizontalSpanRects = new ArrayList<SVGRect>();
			List<SVGRect> verticalSpanRects = new ArrayList<SVGRect>();
			for (SVGElement shape : shapeList) {
				if (shape instanceof SVGRect) {
					SVGRect rect = (SVGRect) shape;
					RealRange xRange1 = rect.getRealRange(Direction.HORIZONTAL);
					RealRange yRange1 = rect.getRealRange(Direction.VERTICAL);
					if (xRange.isEqualTo(xRange1, epsilon)) {
						horizontalSpanRects.add(rect);
					} else {
						if (yRange.isEqualTo(yRange1, epsilon)) {
							verticalSpanRects.add(rect);
						}
					}				
				}
			}
		
			spanningRects = findAllSpanningRects(horizontalSpanRects, verticalSpanRects, epsilon);
			if (spanningRects.size() == 1) {
				outerRect = spanningRects.get(0);
				Real2Range bbox1 = outerRect.getBoundingBox();
				if (bbox1.isEqualTo(bbox, epsilon)) {
					if (shapeList.remove(outerRect)) {
						LOG.debug("removed outerRect from shapeList: "+outerRect.toXML());
					} else {
						LOG.trace("failed to remove outerRect "+outerRect.hashCode());
					}
				}
			} 
		}
	}
	
	private static List<SVGRect> findAllSpanningRects(List<SVGRect> horizontalSpanRects, List<SVGRect> verticalSpanRects, double epsilon) {
		List<SVGRect> allSpanRects = new ArrayList<SVGRect>();
		allSpanRects.addAll(verticalSpanRects);
		allSpanRects.addAll(horizontalSpanRects);
		SVGShape.eliminateGeometricalDuplicates(allSpanRects, epsilon);
		return allSpanRects;
	}

	public void createPotentialHorizontalSeparators() {
		getOrCreateShapeList();
		rectList = extractRects(shapeList);
		getOrCreateHorizontalRulerList();
		
		
	}

	public List<VerticalRule> getOrCreateVerticalRulerList() {
		if (verticalRulerList == null) {
			getOrCreateShapeList();
			List<SVGLine> lineList = extractLines(shapeList, Line2.YAXIS);
			lineList = removeShortLines(lineList, 1.0);
			verticalRulerList = VerticalRule.createSortedRulersFromSVGList(lineList);
			Rule.formatStrokeWidth(verticalRulerList, 1);
		}
		return verticalRulerList;
	}

	public List<HorizontalRule> getOrCreateHorizontalRulerList() {
		if (horizontalRulerList == null) {
			shapeList = getOrCreateShapeList();
			List<SVGLine> lineList = extractLines(shapeList, Line2.XAXIS);
			lineList = removeShortLines(lineList, 1.0);
			horizontalRulerList = HorizontalRule.createSortedRulersFromSVGList(lineList);
			Rule.formatStrokeWidth(horizontalRulerList, 1);
		}
		return horizontalRulerList;
	}

	public List<SVGShape> getOrCreateShapeList() {
		if (shapeList == null) {
			SVGElement svgChunk = textStructurer.getSVGChunk();
			shapeList = SVGUtil.makeShapes(svgChunk);
			SVGElement.format(shapeList, 3);
			addMarkersToZeroDimensionalShapes();
			if (hasZeroDimensionalShapes) {
				SVGSVG.wrapAndWriteAsSVG(shapeList, new File("target/shapes/zero.svg"));
			}
			SVGShape.eliminateGeometricalDuplicates(shapeList, epsilon);
		}
		return shapeList;
	}

	private void addMarkersToZeroDimensionalShapes() {
		for (SVGShape shape : shapeList) {
			if (shape.isZeroDimensional()) {
//				shape.setMarkerEndRef(SVGMarker.ZEROLINE);
				this.hasZeroDimensionalShapes = true;
			}
		}
	}


	public static List<SVGRect> extractRects(List<SVGShape> shapeList) {
		List<SVGRect> rectList = new ArrayList<SVGRect>();
		for (SVGElement shape : shapeList) {
			if (shape instanceof SVGRect) {
				SVGRect rect = (SVGRect) shape;
				rectList.add(rect);
			}
		}
		return rectList;
	}
	
	public static List<SVGLine> extractLines(List<SVGShape> shapeList, Line2 axis) {
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		for (SVGElement shape : shapeList) {
			if (shape instanceof SVGLine) {
				SVGLine line = (SVGLine) shape;
				addAxiallyAlignedLineToList(axis, lineList, line);
			} else if (shape instanceof SVGPolyline) {
				SVGPolyline polyline = (SVGPolyline) shape;
				List<SVGLine> lineList1 = polyline.createLineList();
				for (SVGLine line1 : lineList1) {
					// we have a bug in zigzag polylines MLMLML and this is a temporary fix
					addAxiallyAlignedLineToList(axis, lineList, line1);
				}
			}
		}
		return lineList;
	}

	public static List<SVGRect> extractRects(List<SVGShape> shapeList, Line2 axis) {
		List<SVGRect> rectList = new ArrayList<SVGRect>();
		for (SVGElement shape : shapeList) {
			if (shape instanceof SVGRect) {
				SVGRect rect = (SVGRect) shape;
				rectList.add(rect);
			}
		}
		return rectList;
	}

	private static void addAxiallyAlignedLineToList(Line2 axis, List<SVGLine> lineList, SVGLine line) {
		if (axis == null || 
				(line.isHorizontal(SVGLine.EPS) && axis.equals(Line2.XAXIS)) ||
				(line.isVertical(SVGLine.EPS) && axis.equals(Line2.YAXIS))) {
			lineList.add(line);
		}
	}

	public static List<SVGLine> removeShortLines(List<SVGLine> lineList, double length) {
		List<SVGLine> newLines = new ArrayList<SVGLine>();
		for (SVGLine line : lineList) {
			Double rLength = line.getLength();
			if (rLength != null && rLength > length) {
				newLines.add(line);
			}
		}
		return newLines;
	}

	public void setTextStructurer(TextStructurer textStructurer) {
		this.textStructurer = textStructurer;
	}

	public List<HorizontalRule> getHorizontalRulerList() {
		return getHorizontalRulerList(false, 0.0);
	}

	/**
	 * assumes lines sorted in increasing Y
	 * 
	 * @param merge touching lines at same y-value
	 * @param create tramline from 
	 * 
	 * @return
	 */
	public List<HorizontalRule> getHorizontalRulerList(boolean merge, double eps) {
		LOG.trace("====HRuler===");
		if (horizontalRulerList != null && merge) {
			horizontalRulerList = addRulerOrCombineVerticalOverlaps();
			horizontalRulerList = joinHorizontallyTouchingRulers1();
		}
		return horizontalRulerList;
	}

	private List<HorizontalRule> addRulerOrCombineVerticalOverlaps() {
		List<HorizontalRule> newRulerList = new ArrayList<HorizontalRule>();
		for (int i = 0; i < horizontalRulerList.size(); i++) {
			HorizontalRule horizontalRuler = horizontalRulerList.get(i);
			if (horizontalRuler.getSVGLine() != null) {
				addRulerOrCombineVerticalOverlaps(newRulerList, horizontalRuler);
			}
		}
		return newRulerList;
	}

	/** if ruler is far from previous one, add it to list.
	 * if ruler is close to last one, integrate it into last ruler as a "tramline"
	 * @param newRulerList
	 * @param horizontalRuler
	 * @return
	 */
	private void addRulerOrCombineVerticalOverlaps(List<HorizontalRule> newRulerList, HorizontalRule horizontalRuler) {
		boolean multipleRuler = true;
		if (newRulerList.size() > 0) {
			HorizontalRule lastRuler = newRulerList.get(newRulerList.size() -1);
			IntRange thisXRange = new IntRange(horizontalRuler.getBoundingBox().getXRange());
			IntRange lastXRange = new IntRange(lastRuler.getBoundingBox().getXRange());
			double deltaY = horizontalRuler.getY() - lastRuler.getY();
			// if tramlines, record width and skip addition
			if (deltaY < minRulerSpacingY // close together
					&& lastXRange.compareTo(thisXRange) == 0) { // equal spans
				double width = lastRuler == null || lastRuler.getWidth() == null ? deltaY : Math.max(deltaY, lastRuler.getWidth());
				width = horizontalRuler == null || horizontalRuler.getWidth() == null ? width : Math.max(width, horizontalRuler.getWidth());
				lastRuler.setWidth(width);
				multipleRuler = false;
			} 
		}
		if (multipleRuler) {
			newRulerList.add(horizontalRuler);
		}
	}

	/** join all overlapping rulers on same line
	 * 
	 * @param startRow
	 * @return
	 */
	private List<HorizontalRule> joinHorizontallyTouchingRulers1() {
		List<HorizontalRule> rulerList = new ArrayList<HorizontalRule>();
		IntRange previousRange = null;
		double previousY = Double.NaN;
		SVGLine line = null;
		for (int i = 0; i < horizontalRulerList.size(); i++) {
			HorizontalRule thisRuler = (HorizontalRule) horizontalRulerList.get(i);
			line = thisRuler.getSVGLine();
			double thisY = line.getXY(0).getY();
			IntRange thisRange = new IntRange(thisRuler.getBoundingBox().getXRange().getRangeExtendedBy(PIXEL_GAP, PIXEL_GAP));
			if (previousRange != null &&
				Real.isEqual(thisY, previousY, yTolerance) && previousRange.intersectsWith(thisRange)) {
					previousRange = previousRange.plus(thisRange);
					LOG.trace("Joint touching horizontal rulers");
			} else if (previousRange != null) {
				HorizontalRule newRuler = createRuler(previousRange, line, previousY);
				rulerList.add(newRuler);
				previousRange = thisRange;
			} else {
				previousRange = thisRange;					
			}
			previousY = thisY;
		}
		if (previousRange != null) {
			HorizontalRule newRuler = createRuler(previousRange, line, previousY);
			rulerList.add(newRuler);
		}
//		for (HorizontalElementNew ruler : rulerList) {
//			LOG.trace("RULER: "+ruler);
//		}
		horizontalRulerList = rulerList;
		return horizontalRulerList;
	}
	
	private HorizontalRule createRuler(IntRange previousRange, SVGLine line, double y) {
		SVGLine newLine = new SVGLine(line);
		newLine.setXY(new Real2(previousRange.getMin(), y), 0);
		newLine.setXY(new Real2(previousRange.getMax(), y), 1);
		HorizontalRule ruler = new HorizontalRule(newLine);
		return ruler;
	}


	public void mergeRulersAndTextIntoShapeList() {
		totalTextChunkList = textStructurer.getTextChunkList();
		getOrCreateHorizontalRulerList();
		int iPhrase = 0; 
		int iRuler = 0;
		horizontalElementList = new ArrayList<SVGElement>();
		while (true) {
			if (iPhrase < totalTextChunkList.size() && iRuler < horizontalRulerList.size()) {
				PhraseChunk phraseList = totalTextChunkList.get(iPhrase).getLastPhraseChunk();
				Rule ruler = horizontalRulerList.get(iRuler);
				double yPhrase = phraseList.getBoundingBox().getYMin();
				double yRuler = ruler.getBoundingBox().getYMin();
				if (yPhrase <= yRuler) {
					horizontalElementList.add(totalTextChunkList.get(iPhrase++));
				} else {
					horizontalElementList.add(horizontalRulerList.get(iRuler++));
				}
			} else if (iPhrase < totalTextChunkList.size()) {
				horizontalElementList.add(totalTextChunkList.get(iPhrase++));
			} else if (iRuler < horizontalRulerList.size()) {
				horizontalElementList.add(horizontalRulerList.get(iRuler++));
			} else {
				break;
			}
		}
		addIndexes();
	}

	private void addIndexes() {
		Real2Range bboxPhrase = totalTextChunkList.getBoundingBox();
		bboxRuler = SVGUtil.createBoundingBox(horizontalRulerList);
		horizontalElementByCode = new HashMap<String, SVGElement>();
		// there may be no lines
		Integer maxLength = (bboxRuler == null) ? null : (int) (double) bboxRuler.getXRange().getRange();
		Double maxFont = getMaxFont(totalTextChunkList);
		int iPhrase = 0;
		int iRuler = 0;
		StringBuilder total = new StringBuilder();
		for (int i = 0; i < horizontalElementList.size(); i++) {
			SVGElement horizontalElement = horizontalElementList.get(i);
			String index = "";
			if (horizontalElement instanceof PhraseChunk) {
				index = indexLineChunk(maxFont, iPhrase, horizontalElement);
				iPhrase++;
			} else if (horizontalElement instanceof SVGLine) {
				index = indexSVGLine(maxLength, iRuler, horizontalElement);
				iRuler++;
			}
			total.append(index);
			horizontalElementByCode.put(index, horizontalElement);
			horizontalElement.addAttribute(new Attribute("code", String.valueOf(index)));
		}
		rowCodes = total.toString().trim();
	}

	private String indexSVGLine(Integer maxLength, int iRuler, AbstractCMElement horizontalElement) {
		String index;
		index = " L"+iRuler+"";
		SVGLine line = (SVGLine) horizontalElement;
		double width = line.getStrokeWidth();
		String w = "";
		if (width > 0.6) {
			w += WIDE;
		}
		if (width > 1.5) {
			w += WIDE;
		}
		if (width > 4) {
			w += WIDE;
		}
		index += w+"";
		double l = line.getLength();
		String s = "";
		if (maxLength != null) {
			if (l / (double) maxLength > 0.1) {
				s += LONG;
			}
			if (l / (double) maxLength > 0.2) {
				s += LONG;
			}
			if (l / (double) maxLength > 0.4) {
				s += LONG;
			}
			if (l / (double) maxLength > 0.8) {
				s += LONG;
			}
			if (l / (double) maxLength > 0.99) {
				s += LONG;
			}
		}
		index += s+"";
		return index;
	}

	private String indexLineChunk(Double maxFont, int iPhrase, AbstractCMElement horizontalElement) {
		String index;
		index = " P"+iPhrase;
		PhraseChunk lineChunk = (PhraseChunk) horizontalElement;
		String f = "";
		Double ff = lineChunk.getFontSize();
		if (ff != null && maxFont != null) {
			int fs = (int) (double) lineChunk.getFontSize();
			if (fs / maxFont > 0.6) {
				f += FONT;
			}
			if (fs / maxFont > 0.8) {
				f += FONT;
			}
			if (fs / maxFont > 0.99) {
				f += FONT;
			}
			index += f;
		}
		return index;
	}

	private Double getMaxFont(AbstractCMElement phraseListList2) {
		Double maxFont = null;
		if (totalTextChunkList.size() > 0) {
			maxFont = totalTextChunkList.get(0).getFontSize();
			for (int i = 1; i < totalTextChunkList.size(); i++) {
				Double f = totalTextChunkList.get(i).getFontSize();
				maxFont =  Math.max(maxFont,  f);
			}
		}
		return maxFont;
	}

	public String getRowCodes() {
		return rowCodes;
	}

	public List<SVGElement> getHorizontalElementList() {
		return horizontalElementList;
	}

	public static HtmlHtml createHtmlWithTable(File inputFile, IntRangeArray rangeArray) {
		IntRange[] intRanges = new IntRange[5];
		if (rangeArray == null) {
			throw new RuntimeException("null RangeArray");
		}
		if (rangeArray.size() != 5) {
			LOG.warn("table too complex: "+rangeArray.size());
			return null;
		}
		for (int i = 0; i < rangeArray.size(); i++) {
			intRanges[i] = rangeArray.get(i);
		}
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		
		List<ScriptLine> scriptedLineList = textStructurer.getScriptedLineListForCommonestFont();
		for (ScriptLine scriptLine : scriptedLineList) {
			TextLine textLine0 = scriptLine.getTextLineList().get(0);
		}
		
//		TableStructurer tableStructurer = textStructurer.createTableStructurer();
		TableStructurer tableStructurer = TableStructurer.createTableStructurer(textStructurer);
		HtmlHtml html = tableStructurer.createHtmlWithTable();
		return html;
	}

	/** dummy due to refactoring
	 * 
	 * @param textStructurer
	 * @return
	 * @throws RuntimeException
	 */
	public static TableStructurer createTableStructurer(TextStructurer textStructurer) {
		TextChunkList textChunkList = textStructurer.getOrCreateTextChunkListFromWords();
		TableStructurer tableStructurer = new TableStructurer(textChunkList.getLastTextChunk());
		tableStructurer.setTextStructurer(textStructurer);
//		if (omitShapeList ) {
//			LOG.info("Skipped tableStructurer shapeList");
//		} else {
			tableStructurer.analyzeShapeList();
//		}
		return tableStructurer;
	}

	/** this is messy code.
	 * 
	 * @param inputFile
	 * @param tableSectionList
	 * @param tableTitle
	 * @return
	 */
	private HtmlHtml createHtmlWithTable(File inputFile, List<TableSection> tableSectionList, TableTitle tableTitle) {
		textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		
		List<ScriptLine> scriptedLineList = textStructurer.getScriptedLineListForCommonestFont();
		for (ScriptLine scriptLine : scriptedLineList) {
			TextLine textLine0 = scriptLine.getTextLineList().get(0);
		}
		
//		TableStructurer tableStructurer = textStructurer.createTableStructurer();
		this.setTableTitle(tableTitle);
		this.setSections(tableSectionList);
		HtmlHtml html = this.createHtmlWithTable();
		return html;
	}
	
	/** this is messy code.
	 * 
	 * @param tableSectionList
	 * @param tableTitle
	 * @return
	 */
	public HtmlHtml createHtmlWithTable(List<TableSection> tableSectionList, TableTitle tableTitle) {
		this.setTableTitle(tableTitle);
		this.setSections(tableSectionList);
		HtmlHtml html = this.createHtmlWithTable();
		return html;
	}


	public void setTableTitle(TableTitle tableTitle) {
		this.tableTitle = tableTitle;
	}

	private void setSections(List<TableSection> tableSectionList) {
		this.tableSectionList = tableSectionList;
	}

	private void analyzeTableRow(PhraseChunk phraseList, int iRow) {
		TextChunk lastTextChunk = totalTextChunkList.getLastTextChunk();
		lastTextChunk.getOrCreateChildPhraseChunkList();
		ensureColumnManagerList();
		IntRangeArray bestWhitespaces = lastTextChunk.getBestWhitespaceRanges();
		if (bestWhitespaces.size() != maxColumns) {
			LOG.warn("maxWhitespace ("+bestWhitespaces.size()+") != maxColumns ("+maxColumns+")");
		}
		int iPhrase = 0;
		LOG.trace("-----------------------");
		for (int icol = 0; icol < maxColumns; icol++) {
			Phrase phrase = phraseList.get(iPhrase);
			IntRange enclosingRange = bestWhitespaces.get(icol);
			IntRange phraseRange = phrase.getIntRange();
			ColumnManager columnManager = getColumnManager(icol);
			columnManager.setEnclosingRange(enclosingRange);
			if (!enclosingRange.includes(phraseRange.getMin())) {
				columnManager.addPhrase(null);
				continue;
			}
			columnManager.addPhrase(phrase);
			columnManager.setStartX(phrase.getStartX());
			columnManager.setEndX(phrase.getEndX());
			double delta = phrase.getStartX() - enclosingRange.getMin();
			if (iPhrase < phraseList.size() - 1) {
				iPhrase++;
			};
		}
	}

	public HtmlTr createTableRow(PhraseChunk phraseList, int iRow, Class<?> clazz) {
		totalTextChunkList.getLastTextChunk().getOrCreateChildPhraseChunkList();
		ensureColumnManagerList();
		HtmlTr row = new HtmlTr();
		for (int icol = 0; icol < maxColumns; icol++) {
			HtmlElement cell = (clazz.equals(HtmlTh.class)) ? new HtmlTh() : new HtmlTd();
			row.appendChild(cell);
			Phrase phrase = columnManagerList.get(icol).getPhrase(iRow);
			HtmlElement span = phrase == null ? new HtmlBr() : phrase.getSpanValue();
			cell.appendChild(span);
		}
		return row;
	}

	private List<HtmlTr> createBodyTableRows(int startRow, int endRow, Class<?> clazz) {
		TextChunk lastTextChunk = totalTextChunkList.getLastTextChunk();
		lastTextChunk.getOrCreateChildPhraseChunkList();
		List<HtmlTr> rows = new ArrayList<HtmlTr>();
		
		for (int iRow = startRow; iRow <= endRow; iRow++) {
			PhraseChunk phraseList = lastTextChunk.get(iRow);
			analyzeTableRow(phraseList, iRow);
		}
		for (int iRow = startRow; iRow <= endRow; iRow++) {
			PhraseChunk phraseList = lastTextChunk.get(iRow);
			HtmlTr row = createTableRow(phraseList, iRow, clazz);
			rows.add(row);
		}
		return rows;
	}

	public List<HtmlTr> createBodyTableRows(TextChunk bodyPhraseListList) {
		List<HtmlTr> rows = new ArrayList<HtmlTr>();
		// find margins
		IntRangeArray bestWhitespaces = bodyPhraseListList.getBestWhitespaceRanges();
		IntRangeArray bestColumnRanges = bodyPhraseListList.getBestColumnRanges();
		
		for (PhraseChunk phraseList : bodyPhraseListList) {
			HtmlTr row = createTableRow(phraseList);
			rows.add(row);
		}
		return rows;
	}

	private HtmlTr createTableRow(PhraseChunk phraseList) {
		HtmlTr tr = new HtmlTr();
		for (Phrase phrase : phraseList) {
			HtmlTd td = new HtmlTd();
			tr.appendChild(td);
			AbstractCMElement htmlElement = phrase.toHtml();
			td.appendChild(htmlElement.copy());
		}
		return tr;
	}

	public BoundingBoxManager getHeaderBBoxManager() {
		return headerBBoxManager;
	}

	public BoundingBoxManager getTitleBBoxManager() {
		return titleBBoxManager;
	}

	public BoundingBoxManager getBodyBBoxManager() {
		return bodyBBoxManager;
	}

	public BoundingBoxManager getFooterBBoxManager() {
		return footerBBoxManager;
	}

	public List<TableSection> getTableSectionList() {
		return tableSectionList;
	}

	public TableGrid createGrid() {
		if (tableGrid == null) {
			getOrCreateHorizontalRulerList();
			getOrCreateVerticalRulerList();
			if (verticalRulerList.size() > 0 && horizontalRulerList.size() > 0) {
				TableGridFactory tableGridFactory = new TableGridFactory(horizontalRulerList, verticalRulerList);
				tableGrid = tableGridFactory.getOrCreateTableGrid();
			}
		}
		return tableGrid;
	}



}

package org.contentmine.svg2xml.table;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.eucl.euclid.RealRangeArray;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlCaption;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;

import nu.xom.Element;
import nu.xom.Nodes;

/** holds temporary table as list of chunks.
 * might disappear into TableAnalyzer later?
 * @author pm286
 *
 */
public class TableTable extends TableChunk {

	private final static Logger LOG = Logger.getLogger(TableTable.class);

	public static final double HALF_SPACE = 2.0;
	
	private List<TableChunk> chunkList;
	private int maxHorizontalChunks;
	private List<TableChunk> noColumnChunkList;
	private List<TableChunk> maxColumnChunkList;
	private List<TableChunk> otherChunkList;
	private TableChunk headerChunk;
	private TableChunk bodyChunk;
	private TableChunk captionChunk;
	private TableChunk footerChunk;
	private HtmlTable htmlTable;

	private List<SVGShape> shapeList;
	private List<SVGText> textList;
	private Real2Range pathBox;
	private Real2Range textBox;
	private Real2Range totalBox;

	private List<TableChunk> tableChunkList;

	private Integer tableNumber;

	public TableTable() {
		init();
	}

	public static TableTable createTableTable(Element element) {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(element);
		return createTableTable(svgElement);
	}

	private static TableTable createTableTable(AbstractCMElement svgElement) {
		List<SVGShape> shapeList = SVGShape.extractSelfAndDescendantShapes(svgElement);
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(svgElement);
		TableTable table = new TableTable(shapeList, textList);
		return table;
	}

	public TableTable(List<SVGShape> pathList, List<SVGText> textList) {
		this();
		this.shapeList = pathList;
		this.textList = textList;
	}

	private void init() {
		chunkList = new ArrayList<TableChunk>();
	}
	
	public void add(TableChunk tableChunk) {
		chunkList.add(tableChunk);
	}

	/** simple at present (does not do paths in table cells
	 * 
	 */
	public RealRangeArray createCoarseVerticalMask() {
		textBox = SVGUtil.createBoundingBox(textList);
		totalBox = textBox;
		if (shapeList != null && shapeList.size() > 0) {
			verticalMask = createVerticalMaskFromPaths();
			verticalMask.addTerminatingCaps(totalBox.getYMin(), totalBox.getYMax());
		} else {
			verticalMask = new RealRangeArray(textBox, RealRange.Direction.VERTICAL);
		}
		verticalMask = verticalMask.inverse();
		LOG.trace("YMask: "+verticalMask);
		return verticalMask;
		
	}
		
	private RealRangeArray createVerticalMaskFromPaths() {
	
		this.shapeList = SVGUtil.removeDuplicateShapes(shapeList);
		this.pathBox = SVGUtil.createBoundingBox(shapeList);
		totalBox = totalBox.plus(pathBox);
		// because some "lines" (e.g. in BMC) are multiple paths. This is a mess and needs more 
		// heuristics
		List<Real2Range> pathBboxList = SVGUtil.createNonOverlappingBoundingBoxList(shapeList);
		verticalMask = new RealRangeArray(pathBboxList, RealRange.Direction.VERTICAL);
		return verticalMask;
	}

	public void analyze() {
		getMaxHorizontalChunks();
		createChunkLists();
		if (maxColumnChunkList.size() == 2) {
			headerChunk = maxColumnChunkList.get(0);
			bodyChunk = maxColumnChunkList.get(1);
		}
		if (noColumnChunkList.size() > 0) {
			captionChunk = noColumnChunkList.get(0);
			captionChunk.debug();
		}
		if (noColumnChunkList.size() > 1) {
			footerChunk = maxColumnChunkList.get(1);
		}
		if (otherChunkList.size() > 0) {
			LOG.trace("OTHER");
			for (TableChunk chunk : otherChunkList) { 
				LOG.trace(">> "+chunk);
			}
		}
		LOG.trace("maxCol "+maxColumnChunkList.size());
		LOG.trace("noCol "+noColumnChunkList.size());
		LOG.trace("otherCol "+otherChunkList.size());
		LOG.trace("caption "+captionChunk);
		LOG.trace("header "+headerChunk);
		LOG.trace("body "+bodyChunk);
		LOG.trace("footer "+footerChunk);
	}
	
	public HtmlTable createTable() {
		htmlTable = new HtmlTable();
		createAndAddCaption();
		createAndAddHeader();
		createAndAddRows();
		createAndAddFooter();
		return htmlTable;
	}

	private void createAndAddFooter() {
		if (footerChunk != null) {
//			HtmlFooter htmlFooter = new HtmlFooter();
//			HtmlP para = makePara(footerChunk);
//			htmlFooter.appendChild(para);
//			htmlTable.appendChild(htmlFooter);
		}
	}

	private void createAndAddHeader() {
		if (headerChunk != null) {
			HtmlHead htmlHead = new HtmlHead();
			htmlTable.appendChild(htmlHead);
			HtmlTh th = new HtmlTh();
			htmlHead.appendChild(th);
			for (int i = 0; i < maxHorizontalChunks; i++) {
				String text = headerChunk.getHorizontalChunk(i);
				th.appendChild(text);
			}
		}
	}

	private void createAndAddRows() {
		if (bodyChunk != null) {
			HtmlBody htmlBody = new HtmlBody();
			htmlTable.appendChild(htmlBody);
			
//			for (//)
//			HtmlTr tr = new HtmlTr();
//			htmlBody.appendChild(tr);
//			for (int i = 0; i < maxHorizontalChunks; i++) {
//				String text = headerChunk.getHorizontalChunk(i);
//				tr.appendChild(text);
//			}
		}
	}

	private void createAndAddCaption() {
		if (captionChunk != null) {
			HtmlCaption htmlCaption = new HtmlCaption();
			HtmlP para = makePara(captionChunk);
			htmlCaption.appendChild(para);
			htmlTable.appendChild(htmlCaption);
		}
	}

	private HtmlP makePara(TableChunk chunk) {
		HtmlP p = new HtmlP();
		for (Element element : chunk.getElementList()) {
			p.appendChild(element.getValue());
		}
		return p;
	}

	private void createChunkLists() {
		noColumnChunkList = new ArrayList<TableChunk>();
		maxColumnChunkList = new ArrayList<TableChunk>();
		otherChunkList = new ArrayList<TableChunk>();
		for (TableChunk tableChunk : chunkList) {
			int horizontalChunkCount = tableChunk.getHorizontalGaps().size();
			if (horizontalChunkCount == 1) {
				noColumnChunkList.add(tableChunk);
			} else if (horizontalChunkCount == maxHorizontalChunks) {
				maxColumnChunkList.add(tableChunk);
			} else {
				otherChunkList.add(tableChunk);
			}
		}
	}

	public int getMaxHorizontalChunks() {
		maxHorizontalChunks = 0;
		for (TableChunk tableChunk : chunkList) {
			int horizontalChunkCount = tableChunk.getHorizontalGaps().size();
			maxHorizontalChunks = Math.max(horizontalChunkCount, maxHorizontalChunks);
		}
		return maxHorizontalChunks;
	}

	public List<SVGShape> getShapeList() {
		return shapeList;
	}

	public List<TableChunk> createVerticalTextChunks() {
		this.createCoarseVerticalMask();
		tableChunkList = new ArrayList<TableChunk>();
		if (verticalMask != null) {
			for (RealRange realRange : verticalMask) {
				TableChunk AbstractTableChunk = new TableChunk();
				tableChunkList.add(AbstractTableChunk);
				for (SVGText text : textList) {
					RealRange textRange = text.getRealRange(Direction.VERTICAL);
					if (realRange.includes(textRange)) {
						AbstractTableChunk.add(text);
					}
				}
			}
		}
		return tableChunkList;
	}

	public List<TableChunk> getGenericTextChunkList() {
		return tableChunkList;
	}

	public List<TableChunk> analyzeVerticalTextChunks() {
		createVerticalTextChunks();
		int index = 0;
		for (TableChunk abstractTableChunk : tableChunkList) {
			abstractTableChunk.createHorizontalMaskWithTolerance(HALF_SPACE);
			int cols = abstractTableChunk.getHorizontalMask().size();
			TableChunk abstractChunk = null;
			if (cols == 1) {
				abstractChunk = new TableCaption(abstractTableChunk);
			} else {
				TableBody tableBody = new TableBody(abstractTableChunk);
				tableBody.createStructuredRows();
				abstractChunk = tableBody;
			}
			LOG.trace(">> "+abstractTableChunk.getHorizontalMask());
			// replace with new class
			tableChunkList.set(index, abstractChunk);
			index++;
		}
		return tableChunkList;
	}
	
	public HtmlTable createHtmlElement() {
		HtmlTable table = new HtmlTable();
		table.setBorder(1);
		HtmlHead head = new HtmlHead();
		table.appendChild(head);
		HtmlBody body = new HtmlBody();
		table.appendChild(body);
		// chunk can be caption or table
		for (TableChunk tableChunk : tableChunkList) {
			LOG.trace(tableChunk.getClass());
			HtmlElement htmlElement = tableChunk.createHtmlElement();
			if (htmlElement instanceof HtmlTable) {
				if (hasOnlyOneRow(htmlElement)) {
					htmlElement = TableRow.convertBodyHeader(htmlElement);
				} else {
					XMLUtil.transferChildren(htmlElement, body);
				}
				body.appendChild(htmlElement);
			} else if (htmlElement instanceof HtmlCaption) {
				LOG.trace("Created caption: "+htmlElement.toXML());
				HtmlCaption caption = (HtmlCaption)htmlElement;
				TableCaption.addCaptionTo(table, caption);
				// might be more than one caption (e.g. subtables
				this.tableNumber = tableNumber != null ? tableNumber : TableCaption.getNumber(caption);
				LOG.trace("table number: "+tableNumber);
			} else {
				LOG.trace("HTML: "+htmlElement);
			}
		}
		removeEmptyTables(body);
		// size of growing table children
		int nn = table.query(".//*").size();
		try {
			File file = new File("target/");
			file.mkdirs();
			SVGUtil.debug(table, new FileOutputStream("target/table"+nn+".html"), 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return table;
	}

	private boolean hasOnlyOneRow(AbstractCMElement htmlElement) {
		return htmlElement.query("*[local-name()='tr']").size() == 1;
	}

	private void removeEmptyTables(HtmlBody body) {
		Nodes tables = body.query(".//*[local-name()='table' and count(*)=0]");
		for (int i = 0; i < tables.size(); i++) {
			tables.get(i).detach();
		}
	}

	public Integer getTableNumber() {
		return tableNumber;
	}

}

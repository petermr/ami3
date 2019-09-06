package org.contentmine.svg2xml.table;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntRangeArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlCaption;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTfoot;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.SVGTitle;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalElement;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalRule;
import org.contentmine.graphics.svg.text.build.PhraseChunk;
import org.contentmine.graphics.svg.text.build.TextChunkList;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.page.PageLayoutAnalyzer;
import org.contentmine.svg2xml.table.TableSection.TableSectionType;
import org.contentmine.svg2xml.util.GraphPlot;

import nu.xom.Attribute;

/** IMPORTANT2017 creates structured content from SVGElement
 * uses caches to extract high level obecjts and then create tables
 * 
 * Probably will have siblings to do the same for Plots.
 * 
 * 2017-10 integrating TextChunkCache into previous
 * 
 * @author pm286
 *
 */
public class TableContentCreator extends PageLayoutAnalyzer {

	private static final Pattern TABLE_PATTERN = Pattern.compile("T[Aa][Bb][Ll][Ee]");
	private static final String TITLE_SECT_FILL = "yellow";
	private static final String HEADER_SECT_FILL = "red";
	private static final String BODY_SECT_FILL = "cyan";
	private static final String FOOTER_SECT_FILL = "blue";

	private static final Logger LOG = Logger.getLogger(TableContentCreator.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static Pattern TABLE_N = Pattern.compile("(T[Aa][Bb][Ll][Ee]\\s+\\d+\\.?\\s+(?:\\(cont(inued)?\\.?\\))?\\s*)");
	private static final Pattern HEADERBOX = Pattern.compile("HEADERBOX: (\\d+)");
        /// TEST
    private static final String MINUS_EQUIVALENTS_STRING = "\u2212\\-";
    private static final Pattern COMPOUND_COL_NUMS = Pattern.compile("(["+MINUS_EQUIVALENTS_STRING+"\\+]?\\d*\\.?\\d+"+"|"+
                                                                          "["+MINUS_EQUIVALENTS_STRING+"\\+]?\\d+)");
    public final static Pattern TABLE_PATTERN_CONTINUED = Pattern.compile("(.*\\(?[Cc]ont(inued)?\\.?\\)?)");
	public final static Pattern TABLE_TITLE_PATTERN_SIMPLE = Pattern.compile("(T[Aa][Bb][Ll][Ee]\\s+\\d+\\.?\\s+\\s*)");
	public final static Pattern TABLE_TITLE_PATTERN_CONTINUED = Pattern.compile("(T[Aa][Bb][Ll][Ee]\\s+\\d+\\.?\\s+(\\(?[Cc]ont(inued)?\\.?\\)?)\\s*)");
	public final static Pattern TABLE_TITLE_PATTERN_ANY = Pattern.compile("(.*T[Aa][Bb][Ll][Ee]\\s+\\d+\\.?\\s+(?:\\([Cc]ont(inued)?\\.?\\))?\\s*.*)");        

    private static final String TABLE_FOOTER = "table.footer";
	private static final String TABLE_BODY = "table.body";
	private static final String TABLE_HEADER = "table.header";
	private static final String TABLE_TITLE = "table.title";
	public static final String DOT_ANNOT_SVG = ".annot.svg";
	private static final String DOT_PNG = ".png";
	private static final String CELL_FULL = "cell";
	private static final String CELL_EMPTY = "empty";
    private static final String DATA_CELL_MIN_X = "data-cellminx";
    private static final String DATA_CELL_MAX_X = "data-cellmaxx";
    private static final String DATA_CELL_MIN_Y = "data-cellminy";
    private static final String DATA_CELL_MAX_Y = "data-cellmaxy";
    private static final String DATA_ROW_MIN_X = "data-rowminx";
    private static final String DATA_ROW_MIN_Y = "data-rowminy";
    
    private static final String DATA_ATTR_ROLE = "data-role";
    private static final String ROLE_OBSERVATION_LABEL = "obslabel";
    private static final String ROLE_OBSERVATION = "obs";
    private static final String DATA_ATTR_TBL_SEG = "data-tblseg";
    private static final String TBL_SEG_SUBTABLE_TITLE = "subtabletitle";
    private static final String COLSPAN = "colspan";
    private static final String SUBTABLE = "subtable";
    private static final String START_HDR_COL = "data-startheadercol";
    private static final String END_HDR_COL = "data-endheadercol";
    private static final String SUPPLEMENTARY_OBS = "supp-obs";
    private static final String SUPPLEMENTARY_HEADER = "supp-header";
                 
	private List<HorizontalRule> rulerList;
	private List<TableSection> tableSectionList;
	private IntRangeArray rangesArray;
	private TableTitle tableTitle;
	private boolean addIndents;
    private boolean treatIndentsAsSubtables = true;
	private TableTitleSection tableTitleSection;
	private TableHeaderSection tableHeaderSection;
	private TableBodySection tableBodySection;
	private TableFooterSection tableFooterSection;
    private HtmlThead tableHtmlThead;
    private HtmlTfoot tableHtmlTfoot;
	private SVGElement annotatedSvgChunk;
    private boolean tableHasCompoundColumns = false;
	private double rowDelta = 2.5; //large to manage suscripts
    private final double xEpsilon = 0.1;
    private final double colHeaderGroupXEpsilon = 10; // No smaller value works, especially for finding the end of the spanning header
    private final DecimalFormat decFormat = new DecimalFormat("0.000"); 
    private int headerOffset = 0; // Number of empty cells at start of header rows

	
	public TableContentCreator() {
	}

	/** scans whole file for all tableTitles.
	 * 
	 * @param svgChunkFiles
	 * @return list of titles;
	 */
	public List<TableTitle> findTableTitles(List<File> svgChunkFiles) {
		List<TableTitle> tableTitleList = new ArrayList<TableTitle>();
		for (File svgChunkFile : svgChunkFiles) {
			findTableTitle(tableTitleList, svgChunkFile);
		}
		return tableTitleList;
	}

	private void findTableTitle(List<TableTitle> tableTitleList, File svgChunkFile) {
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(svgChunkFile);
		TextChunkList phraseListList = textStructurer.getTextChunkList();
		phraseListList.format(3);
		String value = phraseListList.getStringValue();
		List<String> titleList = findTitlesWithPattern(value);
		for (int i = 0; i < titleList.size(); i++) {
			TableTitle tableTitle = new TableTitle(titleList.get(i), svgChunkFile.getName());
			tableTitleList.add(tableTitle);
		}
	}

	private List<String> findTitlesWithPattern(String value) {
		Matcher matcher = TABLE_N.matcher(value);
		List<String> titleList = new ArrayList<String>();
		int start = 0;
		while (matcher.find(start)) {
			start = matcher.end();
			String title = matcher.group(0);
			if (titleList.contains(title)) {
				LOG.warn("Duplicate title: "+title);
				title += "*";
			}
			titleList.add(title);
		}
		return titleList;
	}

	public int search(String title) {
		int titleIndex = -1;
		for (int i = 0; i < horizontalList.size(); i++) {
			HorizontalElement horizontalElement = horizontalList.get(i);
			if (horizontalElement instanceof PhraseChunk) {
				String value = ((PhraseChunk)horizontalElement).getStringValue().trim();
				if (value.startsWith(title)) {
					titleIndex = i;
					LOG.trace("title["+value+"]");
					break;
				}
			}
		}
		LOG.trace(title+"/"+titleIndex+"/");
		return titleIndex;
	}

	public HorizontalRule getNextRuler(int irow) {
		HorizontalRule nextRuler = null;
		getFullRulers(irow);
		return nextRuler;
	}

	/** assume following ruler is representative of this table and find all subsequent full rulers.
	 * 
	 * @param startRow
	 * @return
	 */
	public List<HorizontalRule> getFullRulers(int startRow) {
		HorizontalRule firstRuler = null;
		IntRange firstRange = null;
		List<HorizontalRule> followingRulerList = new ArrayList<HorizontalRule>();
		IntRange previousRange = null;
		for (int i = startRow; i < horizontalList.size(); i++) {
			HorizontalElement horizontalElement = horizontalList.get(i);
			if (horizontalElement instanceof HorizontalRule) {
				HorizontalRule thisRuler = (HorizontalRule) horizontalElement;
				IntRange thisRange = new IntRange(thisRuler.getBoundingBox().getXRange());
				if (firstRuler == null) {
					firstRuler = thisRuler;
					firstRange = thisRange;
				} else if (!thisRange.isEqualTo(firstRange)) {
					LOG.trace("skipped range: "+thisRange+" vs "+firstRange);
					continue;
				}
				followingRulerList.add(thisRuler);
			}
		}
		return followingRulerList;
	}

	public void createSectionsAndRangesArray() {
		List<HorizontalElement> horizontalList = getHorizontalList();
		int iRow = tableTitle == null ? 0 : search(tableTitle.getTitle());
//		FIXME
//		mergeOverlappingRulersWithSameYInHorizontalElements(iRow);
		if (iRow == -1) {
			LOG.error("Cannot find title: "+tableTitle);
		} else {
			List<HorizontalRule> fullRulerList = getFullRulers(iRow);
			tableSectionList = new ArrayList<TableSection>();
			IntRange tableSpan = fullRulerList.size() == 0 ? null : fullRulerList.get(0).getIntRange().getRangeExtendedBy(20, 20);
			if (tableSpan != null) {
				this.createSections(horizontalList, iRow, fullRulerList, tableSpan);
				this.createPhraseRangesArray();
				analyzeRangesAndSections();

			}
		}
	}
	
	private IntRangeArray createPhraseRangesArray() {
		rangesArray = new IntRangeArray();
		int length = 0;
		for (TableSection tableSectionX : tableSectionList) {
			int phraseListCount = tableSectionX.getPhraseListCount();
			IntRange intRange = new IntRange(length, length + phraseListCount);
			length += phraseListCount;
			rangesArray.add(intRange);
		}
		return rangesArray;
	}

	private void analyzeRangesAndSections() {
		
		String firstSectionString = tableSectionList.get(0).getStringValue().trim();
		firstSectionString = firstSectionString.substring(0,  Math.min(firstSectionString.length(), 50)).trim();
		String lastSectionString = tableSectionList.get(tableSectionList.size() - 1).getStringValue().trim();
		lastSectionString = lastSectionString.substring(0,  Math.min(lastSectionString.length(), 50)).trim();
		if (firstSectionString.startsWith("Table")) {
			LOG.trace("title 0 "+firstSectionString);
		} else if (lastSectionString.startsWith("Table")) {
			LOG.trace("title last "+lastSectionString);
		} else {
			LOG.debug("***** NO TITLE SECTION ****");//\n"+firstSectionString+"\n"+lastSectionString);
		}
		if (rangesArray.size() == 4) {
			// the commonest
			if (rangesArray.get(0).getMax() > 2) {
				LOG.trace("large title: "+firstSectionString+"\n"+rangesArray);
			} else if (rangesArray.get(1).getRange() > 4) {
				LOG.trace("large header: "+rangesArray);
			} else if (rangesArray.get(2).getRange() < 4) {
				LOG.trace("small body: "+rangesArray);
			}
		} else {
//			LOG.debug("Ranges: "+rangesArray.size()+"; "+rangesArray);
		}

	}

	private void createSections(List<HorizontalElement> horizontalList, int iRow, List<HorizontalRule> fullRulerList,
			IntRange tableSpan) {
		TableSection tableSection = null;
		LOG.trace("start at row: "+iRow+"; "+horizontalList.get(0));
		for (int j = iRow; j < horizontalList.size(); j++) {
			HorizontalElement element = horizontalList.get(j);
			HorizontalRule ruler = (element instanceof HorizontalRule) ? 
					(HorizontalRule) element : null;
			if (tableSection == null || fullRulerList.contains(ruler)) {
				tableSection = new TableSection(TableSectionType.OTHER);
				tableSectionList.add(tableSection);
			}
			if (element instanceof PhraseChunk) {
				PhraseChunk newPhraseChunk = (PhraseChunk) element;
				if (newPhraseChunk.size() > 0) {
					tableSection.add(newPhraseChunk);
				}
				
			} else if (element instanceof HorizontalRule) {
				// dont add Ruler if first element (e.g sectioning ruler)
				if (tableSection.getHorizontalElementList().size() > 0) {
					tableSection.add(element);
				}
			}
		}
	}

	public IntRangeArray getRangesArray() {
		return rangesArray;
	}

	public List<TableSection> getTableSectionList() {
		return tableSectionList;
	}

	public HtmlHtml createHTMLFromSVG(File inputFile) {
		createContent(inputFile);
		createSectionsAndRangesArray();
		HtmlHtml html = tableStructurer.createHtmlWithTable(tableSectionList, tableTitle);
		try {
			XMLUtil.debug(html, new File("target/table/debug/sections.html"), 1);
		} catch (IOException e) {
		}
		return html;
	}

	public void setTableTitle(TableTitle tableTitle) {
		this.tableTitle = tableTitle;
	}

	public void setAddIndents(boolean add) {
		this.addIndents = add;
	}

	/** FIXME.
	 * works on getTextStructurer().getSVGChunk(). needs refactoring to textStructurer
	 * 
	 * returns original file with overlaid boxes
	 * 
	 * @param colors
	 * @param opacity
	 * @return
	 */
	public SVGElement createMarkedSections(/*SVGElement markedChunk,*/
			String[] colors,
			double[] opacity) {
		// write SVG
		SVGElement markedChunk = tableTextStructurer.getSVGChunk();
		
//		LOG.warn("Check SVGChunk after refactor");
//		SVGElement markedChunk = this.annotatedSvgChunk;
		if (markedChunk == null) {
			throw new RuntimeException("Missing markedChunk - change logic");
		}
		SVGG g = new SVGG();
		g.setSVGClassName("sections");
		markedChunk.appendChild(g);
		TableStructurer tableStructurer = getTableStructurer();
		SVGRect plotBox;
		plotBox = GraphPlot.createBoxWithFillOpacity(tableStructurer.getTitleBBox(), colors[0], opacity[0]);
		plotBox.setSVGClassName(TABLE_TITLE);
		plotBox.appendChild(new SVGTitle(TABLE_TITLE));
		g.appendChild(plotBox);
		plotBox = GraphPlot.createBoxWithFillOpacity(tableStructurer.getHeaderBBox(), colors[1], opacity[1]);
		plotBox.setSVGClassName(TABLE_HEADER);
		plotBox.appendChild(new SVGTitle(TABLE_HEADER));
		g.appendChild(plotBox);
		plotBox = GraphPlot.createBoxWithFillOpacity(tableStructurer.getBodyBBox(), colors[2], opacity[2]);
		plotBox.setSVGClassName(TABLE_BODY);
		plotBox.appendChild(new SVGTitle(TABLE_BODY));
		g.appendChild(plotBox);
		plotBox = GraphPlot.createBoxWithFillOpacity(tableStructurer.getFooterBBox(), colors[3], opacity[3]);
		plotBox.setSVGClassName(TABLE_FOOTER);
		plotBox.appendChild(new SVGTitle(TABLE_FOOTER));
		g.appendChild(plotBox);
		TableContentCreator.shiftToOrigin(markedChunk, g);
		return markedChunk;
	}

	public static void shiftToOrigin(SVGElement markedChunk, SVGG g) {
		SVGG gg = null;
		AbstractCMElement svgElement =  (AbstractCMElement) markedChunk.getChildElements().get(0);
		if (svgElement instanceof SVGG) {
			SVGG firstG = (SVGG) markedChunk.getChildElements().get(0);
			Transform2 t2 = firstG.getTransform();
			g.setTransform(t2);
		}
	}

	public TableTitleSection getOrCreateTableTitleSection() {
		if (tableTitleSection == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() >= 1) {
				tableTitleSection = new TableTitleSection(tableSectionList.get(0));
			}
		}
		return tableTitleSection;
	}

	public TableHeaderSection getOrCreateTableHeaderSection() {
		if (tableHeaderSection == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() >= 2) {
				tableHeaderSection = new TableHeaderSection(tableSectionList.get(1));
			}
		}
		return tableHeaderSection;
	}

	public TableBodySection getOrCreateTableBodySection() {
		if (tableBodySection == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() >= 3) {
				tableBodySection = new TableBodySection(tableSectionList.get(2));
			}
		}
		return tableBodySection;
	}

	public TableFooterSection getOrCreateTableFooterSection() {
		if (tableFooterSection == null) {
			List<TableSection> tableSectionList = getTableStructurer().getTableSectionList();
			if (tableSectionList.size() >= 4) {
				tableFooterSection = new TableFooterSection(tableSectionList.get(3));
			}
		}
		return tableFooterSection;
	}


	public AbstractCMElement getSVGChunk() {
		return tableTextStructurer.getSVGChunk();
	}

	public SVGElement annotateAreas(File inputFile) {
		createHTMLFromSVG(inputFile);
		return annotateAreasInSVGChunk();
	}

	public SVGElement annotateAreasInSVGChunk() {
		SVGElement svgChunk = createMarkedSections(
				new String[] {"yellow", "red", "cyan", "blue"},
				new double[] {0.2, 0.2, 0.2, 0.2}
			);
		TableTitleSection tableTitle = getOrCreateTableTitleSection();
		if (tableTitle == null) {
//			LOG.warn("no table title");
		} else {
			svgChunk = tableTitle.createMarkedContent(
					(SVGElement) svgChunk.copy(),
					new String[] {"yellow", "yellow"}, 
					new double[] {0.2, 0.2}
					);
		}
		TableHeaderSection tableHeader = getOrCreateTableHeaderSection();
		if (tableHeader == null) {
//			LOG.warn("no table header");
		} else {
			tableHeader.createHeaderRowsAndColumnGroups();
			svgChunk = tableHeader.createMarkedSections(
					(SVGElement) svgChunk.copy(),
					new String[] {"blue", "green"}, 
					new double[] {0.2, 0.2}
					);
		}
		TableBodySection tableBody = getOrCreateTableBodySection();
		if (tableBody == null) {
			LOG.trace("no table body");
		} else {
			tableBody.createHeaderRowsAndColumnGroups();
			svgChunk = tableBody.createMarkedSections(
					(SVGElement) svgChunk.copy(),
					new String[] {"yellow", "red"}, 
					new double[] {0.2, 0.2}
					);
		}
		TableFooterSection tableFooter = getOrCreateTableFooterSection();
		if (tableFooter != null) {
			svgChunk = tableFooter.createMarkedContent(
					(SVGElement) svgChunk.copy(),
					new String[] {"blue", "blue"}, 
					new double[] {0.2, 0.2}
					);
		}
		return svgChunk;
	}

	public void markupAndOutputTable(File inputFile, File outDir) {
		String outRoot = inputFile.getName();
		outRoot = outRoot.substring(0, outRoot.length() - DOT_PNG.length());
		LOG.trace("reading SVG "+inputFile);
		annotatedSvgChunk = annotateAreas(inputFile);
		File outputFile = new File(outDir, outRoot+DOT_ANNOT_SVG);
		LOG.trace("writing annotated SVG "+outputFile);
		SVGSVG.wrapAndWriteAsSVG(annotatedSvgChunk, outputFile);
	}

	/** create HTML from annot.svg
	 * 
	 * @param annotSvgFile
	 * @param outDir
	 * @throws IOException 
	 */
	public void createHTML(File annotSvgFile, File outDir) throws IOException {
//		LOG.debug("reading SVG from "+annotSvgFile);
		HtmlHtml html = createHtmlFromSVG();
		File outfile = new File(outDir, annotSvgFile.getName()+".html");
//		LOG.debug("writing HTML to : "+outfile);
		XMLUtil.debug(html, outfile, 1);
		
		
	}

	public HtmlHtml createHtmlFromSVG() {
		HtmlHtml html = new HtmlHtml();
                // Use this API to ensure top-level structures are added
                // to the output XML tree / HTML DOM
		HtmlBody body = html.getOrCreateBody();      
                HtmlHead htmlHead = html.getOrCreateHead();
                htmlHead.addUTF8Charset();  
		HtmlTable table = new HtmlTable();
		table.setClassAttribute("table");
		body.appendChild(table);
		
		
		int bodyCols = getGElements(annotatedSvgChunk).size();
                
                if (bodyCols > 0) {
                    addCaption(annotatedSvgChunk, table);
                    addHeader(annotatedSvgChunk, table, bodyCols);
                    addBody(annotatedSvgChunk, table);
                    addFooter(annotatedSvgChunk, table, bodyCols);
                }
               
		return html;
	}

	private void addHeader(AbstractCMElement svgElement, HtmlTable table, int bodyCols) {
                int cols = 0;
		HtmlTr tr = new HtmlTr();
                tr.addAttribute(new Attribute("data-tblrole", "columnheaderrow"));

		AbstractCMElement g = svgElement == null ? null : (SVGElement) XMLUtil.getSingleElement(svgElement, 
				".//*[local-name()='g' and @class='"+TableHeaderSection.HEADER_COLUMN_BOXES+"']");
		if (g != null) {
			cols = addHeaderBoxes(tr, g, bodyCols);
		}
                
                HtmlThead htmlThead = new HtmlThead();
                AbstractCMElement gColGroups = svgElement == null ? null : (SVGElement) XMLUtil.getSingleElement(svgElement, 
				".//*[local-name()='g' and @class='"+TableHeaderSection.HEADER_BOXES+"']");
		if (gColGroups != null) {
                    addColumnGroups(htmlThead, gColGroups, g, bodyCols - cols);
		}
               
                htmlThead.appendChild(tr);
                
                this.tableHtmlThead = htmlThead;
                table.appendChild(htmlThead);
	}
        
        /**
         * Take a flat list of column group headers and use the annotated index to 
         * separate them into the correct set of rows.
         * @param rects
         * @return 
         */
        private List<List<SVGRect>> createColumnGroupRows(List<SVGRect> headerBoxRects) {
            List<List<SVGRect>> colGroupHeaderRows = new ArrayList<List<SVGRect>>();
            
            if (headerBoxRects != null && headerBoxRects.size() > 0) {
                for (SVGRect rect : headerBoxRects) {
                    // Allocate rects to rows according to the numbering in the annot
                    String title = rect.getValue();
                    Matcher matcher = HEADERBOX.matcher(title);
                    if (matcher.find())
                    {
                        String find = matcher.group(1);
                        Integer colGroupRow = Integer.parseInt(find);
                        // Add to the array of rects
                        if (colGroupRow > colGroupHeaderRows.size() - 1) {
                            List<SVGRect> colGroupHeaderRow = new ArrayList<SVGRect>();
                            colGroupHeaderRows.add(colGroupHeaderRow);
                        }
                        
                        colGroupHeaderRows.get(colGroupRow).add(rect);
                    }
                }
            }   
            
            return colGroupHeaderRows;
        } 
        
        private void addColumnGroups(HtmlThead htmlHead, AbstractCMElement g, AbstractCMElement hdrG, int bodyDelta) { 
            List<SVGRect> columnGroupRects = SVGRect.extractSelfAndDescendantRects(g);
            List<List<SVGRect>> cgRows = createColumnGroupRows(columnGroupRects);
            HtmlTr tr;
            
            List<SVGRect> hdrRects = SVGRect.extractSelfAndDescendantRects(hdrG);

            // Iterate over header columns
            // and match left-hand of colgroup to underlying
            // grid, filling with empty th as needed
            HtmlTh th = new HtmlTh();
            
            for (int cgr = 0; cgr < cgRows.size(); cgr++) {
                List<SVGRect> rects = cgRows.get(cgr);
                tr = new HtmlTr();
                int cgIndex = 0;
                int colspan = 0;
                int colSpanStartCol = 0;
                int colSpanEndCol = 0;
                boolean getNextColGroupDetails = false;
                
                SVGRect rect = rects.get(cgIndex);
                String title = rect.getValue();   // messy but has to be rewritten
                title = title.replace(" //", "");
                double colGroupMinX = rect.getBoundingBox().getXMin();
                double colGroupMaxX = rect.getBoundingBox().getXMax();
                RealRange colGroupRange = rect.getRealRange(Direction.HORIZONTAL);
                 
                for (int i = 0; i < bodyDelta; i++) {
                    th.setClassAttribute(CELL_EMPTY);
                    HtmlTh thDeepCopy = (HtmlTh) (HtmlTh.create(th));
                    tr.appendChild(thDeepCopy);
                }
                
                int i = 0;
                while (i < hdrRects.size()) {
                    if (cgIndex > rects.size() - 1) {
                        // No more col groups -- fill with empty cells
                        th.setClassAttribute(CELL_EMPTY);
                        HtmlTh thDeepCopy = (HtmlTh) (HtmlTh.create(th));
                        tr.appendChild(thDeepCopy);
                        th = new HtmlTh();
                        i++;
                        continue;
                    }
                                             
                    // Get details of next spanning column group
                    if (getNextColGroupDetails) {
                        rect = rects.get(cgIndex);
                        title = rect.getValue();   // messy but has to be rewritten
                        title.replace(" //", "");
                        colGroupMinX = rect.getBoundingBox().getXMin();
                        colGroupMaxX = rect.getBoundingBox().getXMax();
                        colGroupRange = rect.getRealRange(Direction.HORIZONTAL);
                        getNextColGroupDetails = false;
                    }
                    
                    SVGRect headerCol = hdrRects.get(i);
                    double headerColMinX = headerCol.getBoundingBox().getXMin();
                    double headerColMaxX = headerCol.getBoundingBox().getXMax();
                    RealRange headerColRange = headerCol.getRealRange(Direction.HORIZONTAL);

                    if (colGroupRange.intersects(headerColRange)) {
                        th.setClassAttribute(CELL_FULL);
                        if (isEqualTo(colGroupMinX, headerColMinX, colHeaderGroupXEpsilon)) {
                            // This is the start of the span
                            th.appendChild(title.substring(title.indexOf("/") + 1));
                            colSpanStartCol = i + this.headerOffset;
                            colspan++;
                        } else if (isEqualTo(colGroupMaxX, headerColMaxX, colHeaderGroupXEpsilon)) {
                            // This is the final part of the span
                            if (++colspan > 1) {
                                th.setAttribute(COLSPAN, Integer.toString(colspan));
                            }
                            cgIndex++;
                            getNextColGroupDetails = true;
                            colSpanEndCol = i + this.headerOffset;
                            LOG.trace("SPAN complete:"+title);
                            LOG.trace("START_HDR_COL:"+colSpanStartCol);
                            LOG.trace("END_HDR_COL:"+colSpanEndCol);
                            LOG.trace("colspan:"+colspan);
                            colspan = 0;
                            
                            // Record start and end header columns spanned
                            th.addAttribute(new Attribute(START_HDR_COL, Integer.toString(colSpanStartCol)));
                            th.addAttribute(new Attribute(END_HDR_COL, Integer.toString(colSpanEndCol)));

                            HtmlTh thDeepCopy = (HtmlTh) (HtmlTh.create(th));
                            tr.appendChild(thDeepCopy);
                            th = new HtmlTh();
                        } else if (isGreaterThan(colGroupMaxX, headerColMaxX, colHeaderGroupXEpsilon)) {
                            // span continues beyond this header col
                            colspan++;
                        } 
                        
                        // Column header / column superheader has been found
                        i++;
                    } else if (colspan > 0) {
                        // There is a span which must be closed
                        // Close it but do not advance index as
                        // current header has not been matched to an intersecting heading
                        if (colspan > 1) {
                            th.setAttribute(COLSPAN, Integer.toString(colspan));
                        }
                        cgIndex++;
                        getNextColGroupDetails = true;
                        colSpanEndCol = i;
                        LOG.trace("SPAN complete:" + title);
                        LOG.trace("START_HDR_COL:" + colSpanStartCol);
                        LOG.trace("END_HDR_COL:" + colSpanEndCol);
                        LOG.trace("colspan:" + colspan);
                        colspan = 0;
                            
                        // Record start and end header columns spanned
                        th.addAttribute(new Attribute(START_HDR_COL, Integer.toString(colSpanStartCol)));
                        th.addAttribute(new Attribute(END_HDR_COL, Integer.toString(colSpanEndCol)));

                        HtmlTh thDeepCopy = (HtmlTh) (HtmlTh.create(th));
                        tr.appendChild(thDeepCopy);
                        th = new HtmlTh();
                    } else {
                        th.setClassAttribute(CELL_EMPTY);
                        HtmlTh thDeepCopy = (HtmlTh) (HtmlTh.create(th));
                        tr.appendChild(thDeepCopy);
                        th = new HtmlTh();
                        i++;
                    }
                }
                
                // Allocate any colspan which is unrecorded at the end of the headers
                // This is the final part of the span
                if (colspan > 1) {
                    colSpanEndCol = hdrRects.size() - 1;
                    colSpanStartCol = colSpanEndCol - colspan + 1;
                    th.setAttribute(COLSPAN, Integer.toString(colspan));
                    // Record start and end header columns spanned
                    th.addAttribute(new Attribute(START_HDR_COL, Integer.toString(colSpanStartCol)));
                    th.addAttribute(new Attribute(END_HDR_COL, Integer.toString(colSpanEndCol)));
                    
                    HtmlTh thDeepCopy = (HtmlTh) (HtmlTh.create(th));
                    tr.appendChild(thDeepCopy);
                }
                
                HtmlTr trDeepCopy = (HtmlTr)HtmlTr.create(tr);
                htmlHead.appendChild(trDeepCopy);
            }
        }
        
	private int addHeaderBoxes(HtmlTr tr, AbstractCMElement g, int bodyCols) {
		List<SVGRect> rects = SVGRect.extractSelfAndDescendantRects(g);
		int headerCols = rects.size();
                this.headerOffset = bodyCols - headerCols;
		LOG.trace("Header boxes: "+headerCols+"; delta: "+this.headerOffset);
		for (int i = 0; i < this.headerOffset; i++) {
			HtmlTh th = new HtmlTh();
			tr.appendChild(th);
		}
		for (int i = 0; i < headerCols; i++) {
			SVGRect rect = rects.get(i);   // messy but has to be rewritten
			String title = rect.getValue();   // messy but has to be rewritten
			title = title.replace(" //", "");
                        title = ValueNormaliser.removeUnusualCharacterTooltip(title);
			HtmlTh th = new HtmlTh();
			th.setClassAttribute(CELL_FULL);
                        double cellMinX = rect.getBoundingBox().getXMin();
                        double cellMaxX = rect.getBoundingBox().getXMax();
                        th.setAttribute(DATA_CELL_MIN_X, decFormat.format(cellMinX));
                        th.setAttribute(DATA_CELL_MAX_X, decFormat.format(cellMaxX));
			th.appendChild(title.substring(title.indexOf("/")+1));
			tr.appendChild(th);
		}
		return headerCols;
	}

	private void addBody(AbstractCMElement svgElement, HtmlTable table) {
		List<SVGG> gs = getGElements(svgElement);
		if (gs.size() == 0) {
//			LOG.warn("No annotated body");
			return;
		}
		List<List<SVGRect>> columnList = new ArrayList<List<SVGRect>>();
		for (int i = 0; i < gs.size(); i++) {
			List<SVGRect> rects = SVGRect.extractSelfAndDescendantRects(gs.get(i));
			columnList.add(rects);
		}
		LOG.trace("Body columns: "+columnList.size());

		if (columnList.size() == 0) {
			return;
		}

		List<RealRange> allRanges = createRowRanges(columnList);
		for (int jcol = 0; jcol < columnList.size(); jcol++) {
			List<SVGRect> column = columnList.get(jcol);
			padColumn(column, allRanges);
		}
                
                HtmlTbody mainTableTbody = new HtmlTbody();
                
                // FIXME No need to add the cells to a DOM at this stage
                // as we immediately restructure -- just need a List<List<HtmlElement>>
                for (int irow = 0; irow < allRanges.size(); irow++) {
                    createRowsAndAddToTbody(mainTableTbody, columnList, irow);
                }
                
                if (this.headerOffset >= 0) {
                    // Split columns with compound content
                    splitCompoundColumnContent(mainTableTbody, columnList.size());
                }
                 
                // Content enhancements applied after grid-resolution -- factor out?
                // Re-structure into subtables
                HtmlTbody restructuredTbody = mainTableTbody;
                
                
                if (treatIndentsAsSubtables) {
                    restructuredTbody = createSubtablesFromIndents(mainTableTbody, columnList.size());
                }
                
                mergeUnwrappedObservationLabels(restructuredTbody);
                
                // If transformed table exists and is basically well formed
                // then add it as the top-level tbody of the table
                if (restructuredTbody != null && restructuredTbody.getChildCount() > 0) {
                    table.appendChild(restructuredTbody);
                }
	}

	private List<SVGG> getGElements(AbstractCMElement svgElement) {
		AbstractCMElement g = svgElement == null ? null : (SVGElement) XMLUtil.getSingleElement(svgElement, 
				".//*[local-name()='g' and @class='"+TableBodySection.BODY_CELL_BOXES+"']");
		List<SVGG> gs = (g == null) ? new ArrayList<SVGG>() : SVGG.extractSelfAndDescendantGs(g);
		return gs;
	}

	private void createRowsAndAddToTbody(HtmlTbody tbody, List<List<SVGRect>> columnList, int irow) {
		HtmlTr tr = new HtmlTr();
               
		tbody.appendChild(tr);
               
		for (int jcol = 0; jcol < columnList.size(); jcol++) {
			List<SVGRect> rectjList = columnList.get(jcol);
			if (irow >= rectjList.size()) {
				LOG.trace("row index out of range "+irow);;
			} else {
				SVGShape rectij = rectjList.get(irow);
				HtmlTd td = new HtmlTd();
				tr.appendChild(td);
				String value = rectij == null ? "/" : rectij.getValue();
				String value1 = value.substring(value.indexOf("/")+1);
                                // Normalise number prefixes which are semanticly minus signs
                                String normalisedValue = ValueNormaliser.normaliseNumericalValueString(value1);
                                normalisedValue = ValueNormaliser.removeUnusualCharacterTooltip(normalisedValue);
				td.appendChild(normalisedValue);
				td.setClassAttribute((normalisedValue.trim().length() == 0) ? CELL_EMPTY : CELL_FULL);
                                addLayoutDataAttributes(tr, td, rectij, rectjList, irow);
			}
		}
	}

	private List<RealRange> createRowRanges(List<List<SVGRect>> columnList) {
		// populate allRanges with column0
		List<RealRange> allRanges = new ArrayList<RealRange>();
		List<SVGRect> column0 = columnList.get(0);
		if (column0.size() == 0) {
			return allRanges; // no rows
		}
		for (int irow = 0; irow < column0.size(); irow++) {
			SVGRect rowi = column0.get(irow);
			RealRange rowRange = rowi.getBoundingBox().getYRange().format(3);
			allRanges.add(rowRange);
		}
		
		// iterate over other columns, filling in holes if necessary
		for (int jcol = 0; jcol < columnList.size(); jcol++) {
			List<SVGRect> columnj = columnList.get(jcol);
			int allPtr = allRanges.size() - 1;
			int colPtr = columnj.size() - 1;
			if (colPtr > allPtr) {
				LOG.error("Column ("+jcol+"; "+(colPtr+1)+") larger than allRanges ("+(allPtr+1)+") \n"+columnj+"; \n"+allRanges);
			}
			while (colPtr >= 0) {
				SVGRect rowi = columnj.get(colPtr);
				RealRange colRange = rowi.getBoundingBox().getYRange();
				RealRange allRange = allRanges.get(allPtr);
				if (colRange.intersects(allRange)) {
					RealRange newRange = colRange.plus(allRange);
					allRanges.set(allPtr, newRange);
					LOG.trace("equal: "+allPtr+"; "+colPtr);
					allPtr--;
					colPtr--;
				} else if (colRange.getMax() < allRange.getMin()) {
					LOG.trace("less: "+allPtr+"; "+colPtr);
					allPtr--;
				} else if (colRange.getMin() > allRange.getMax()) {
					LOG.trace("more: "+allPtr+"; "+colPtr);
					allRanges.add(allPtr + 1, colRange);
					colPtr--;
				} else {
					throw new RuntimeException("cannot add to allRanges "+allRange+"; "+colRange);
				}
				if (allPtr < 0 && colPtr >= 0) {
					LOG.error("Cannot match col=>all "+colPtr+" => "+allPtr+"; "+columnj.size()+" => "+allRanges.size()+" => "+columnj+" => "+allRanges);
					break;
				}
			}
		}
		return allRanges;
	}

	private void padColumn(List<SVGRect> column, List<RealRange> allRanges) {
		int allPtr = allRanges.size() - 1;
		int colPtr = column.size() - 1;
		while (allPtr >= 0) {
			if (colPtr < 0) {
				// empty space at start of column
				column.add(0, (SVGRect) null);
				allPtr--;
			} else {
				RealRange allRange = allRanges.get(allPtr);
				RealRange colRange = column.get(colPtr).getBoundingBox().getYRange();
				if (colRange.intersects(allRange)) {
					// ranges match
					colPtr--;
					allPtr--;
				} else if (colRange.getMin() > allRange.getMax()) {
					throw new RuntimeException("IMPOSSIBLE "+allRange+"; "+colRange);
				} else if (colRange.getMax() < allRange.getMin()) {
					// empty cell in column
					column.add(colPtr + 1, (SVGRect) null);
					allPtr--;
				} else {
					throw new RuntimeException("cannot map to allRanges "+allRange+"; "+colRange);
				}
			}
		}
	}
        
        /**
         * Record page layout data as HTML data attributes
         */
        private void addLayoutDataAttributes(HtmlTr tr, HtmlTd td, SVGShape rectij, List<SVGRect> rectjList, int irow) {
            DecimalFormat decFormat = new DecimalFormat("0.000");
     
            // Record position data in each cell
            if (rectij != null) {
                double cellMinX = rectij.getBoundingBox().getXMin();
                double cellMinY = rectjList.get(irow).getBoundingBox().getYMin();
                td.setAttribute(DATA_CELL_MIN_X, decFormat.format(cellMinX));
                td.setAttribute(DATA_CELL_MIN_Y, decFormat.format(cellMinY));

                // As soon as we get a non-empty cell set the row minimum x, y once  
                if (tr.getAttributeValue(DATA_ROW_MIN_Y) == null) {
                    tr.setAttribute(DATA_ROW_MIN_X, decFormat.format(cellMinX));
                    tr.setAttribute(DATA_ROW_MIN_Y, decFormat.format(cellMinY));
                }                
            }
        }
        
        private boolean rowIsRightClear(HtmlTr tr) {
            boolean isRightClear = false;
            
            List<HtmlTd> tds = tr.getTdChildren();
            
            if (!tds.isEmpty()) {
                // First cell has content
                isRightClear = (tds.get(0).getClassAttribute().equals("cell"));
                
                if (isRightClear) {
                    // All other cells are empty
                    int jcol = 1;
                    
                    while (isRightClear && jcol < tds.size()) {
                        isRightClear = (isRightClear && tds.get(jcol).getClassAttribute().equals("empty"));
                        jcol++;
                    }
                }
            }
            
            return isRightClear;
        }
        
        private boolean obsRowIsRightClear(HtmlTr tr) {
            boolean isRightClear = false;
            
            List<HtmlTd> tds = tr.getTdChildren();
           
            if (!tds.isEmpty()) {
                int columnCount = tr.getChildCount();
                if (tds.size() == columnCount - 1) {
                    // All value cells (tds) are empty
                    int jcol = 1;
                    isRightClear = true;
                    
                    while (isRightClear && jcol < tds.size()) {
                        isRightClear = (isRightClear && tds.get(jcol).getClassAttribute().equals("empty"));
                        jcol++;
                    }
                }
            }
            
            return isRightClear;
        }
        
        /**
         * Use layout information in HTML to find subtables using relative x positions
         * across rows.
         * @param table The table as a flat list of rows with cells marked up with raw x,y layout values 
         * @return A transformed copy of the table with subtables as nested tables
         */
        private HtmlTbody createSubtablesFromIndents(HtmlTbody table, int columnCount) {
            HtmlTbody restructTable = null;
            
            if (table != null) {
                double curMinX = 0.0;
                double prevMinX = 0.0;
                
                double curRowMinX;
                HtmlTr prevRow = null;           
                           
                List<HtmlTr> rows = table.getOrCreateChildTrs();
                
                HtmlTbody currentSubtable = null;
                restructTable = new HtmlTbody();
                
                LOG.trace("---");
                                    
                for (int irow = 0; irow < rows.size(); irow++) {
                    HtmlTr tr = rows.get(irow);
                    
                    // Set prevRow every time.
                    // Defer processing until next line when indent relationship is known
                    // Logic from indents determines where prevRow is put into restruct table
                    
                    // Skip the header row
                    ArrayList<HtmlTh> ths = (ArrayList<HtmlTh>)tr.getThChildren();
                    
                    if (ths != null && ths.size() > 0) {
                        // A header row cannot be part of a subtable
                        tr.addAttribute(new Attribute("data-tblrole","columnheaderrow"));
                        continue;
                    }
                    
                    // Get the min X of the current row
                    Attribute attr = tr.getAttribute("data-rowminx");
                    curRowMinX = Double.parseDouble(attr == null ? "0.0" : attr.getValue());
                    
                    // Find left edge of current table or subtable
                    if (curRowMinX > 0.0) {
                        curMinX = curRowMinX;
                    }
                    
                    LOG.trace("R:"+irow+"\t"+extractRowLabel(tr));
                    
                    if (curMinX != 0.0) {
                        boolean isOutDent = isGreaterThan(prevMinX, curMinX, xEpsilon);
                        LOG.trace("R:"+irow+"\t"+prevMinX+"->"+curMinX+":"+(isOutDent ? "OUTDENT" : "NOT OUTDENT"));
                        if (isGreaterThan(curMinX, prevMinX, xEpsilon)) {
                            // INDENT or FIRST
                            if (prevMinX > 0.0) {
                                // This is an indent -- a new subtable has started
                                LOG.trace("ST:Indent: ("+prevMinX+"->"+curMinX+")");
                                LOG.trace("R:"+irow+"\t"+"[IN]\t\t\t"+"ST?:"+(currentSubtable != null ? "Y" : "N"));
                                
                                currentSubtable = new HtmlTbody();
                                currentSubtable.addAttribute(new Attribute("class", SUBTABLE));

                                // The previous line was subtable heading line
                                if (irow > 0) {      
                                    LOG.trace("Add ST header:"+irow+"\t"+extractRowLabel(prevRow));
                                    this.addSubtableRow(currentSubtable, prevRow, true);
                                }
                            } 
                        } else if (isEqualTo(curMinX, prevMinX, xEpsilon)) {
                            // ALIGNED
                            LOG.trace("Obs row: (" + prevMinX + "==" + curMinX + ")");
                            LOG.trace("R:"+irow+"\t"+"[ALIGN]\t\t\t"+"ST?:"+(currentSubtable != null ? "Y" : "N"));
                            if (curMinX != 0.0) {  
                                if (currentSubtable != null) {
                                    addSubtableRow(currentSubtable, prevRow, false);
                                } else {
                                    addTopLevelRow(restructTable, prevRow);
                                }
                            } 
                        } else { 
                            // curMinX < prevMinX, within tolerance
                            // OUTDENT
                            LOG.trace("R:"+irow+"\t"+"[OUT]\t\t\t"+"ST?:"+(currentSubtable != null ? "Y" : "N"));
                            
                            // Handle case where grid is not well formed upstream and 
                            // currentSubtable is null  
                            if (currentSubtable != null) {
                                // This is the start of a new section
                                // The previous line is the end of the subtable
                                this.addSubtableRow(currentSubtable, prevRow, false);
                                List<HtmlTr> stRows = currentSubtable.getOrCreateChildTrs();
                                LOG.trace("ST:Complete: (" + prevMinX + "->" + curMinX + "):total rows:" + (stRows != null ? stRows.size() : 0));

                                // Add the completed subtable to the restructured table
                                HtmlTbody subtableDeepCopy = (HtmlTbody) (HtmlTbody.create(currentSubtable));
                                restructTable.appendChild(subtableDeepCopy);
                            }
                            
                            currentSubtable = null; 
                            LOG.trace("------");
                        }
                        
                        // Make a copy of the current row reference and 
                        // store it until its role is determined at the next row
                        prevRow = tr;
                    
                        prevMinX = curRowMinX;
                    }
                }
                
                // At the end of main table processing: 
                // allocate final row and
                // close any final incomplete subtable
                if (currentSubtable != null) {
                    addSubtableRow(currentSubtable, prevRow, false);
                    
                    LOG.trace("ST:Complete At Table end: (" + prevMinX + "->" + curMinX + "):total rows:" + currentSubtable.getChildCount());
                    HtmlTbody subtableDeepCopy = (HtmlTbody)(HtmlTbody.create(currentSubtable));
                    restructTable.appendChild(subtableDeepCopy);
                } else {
                    addTopLevelRow(restructTable, prevRow);
                }
            }
            
            return restructTable;
        }
        
        /**
         * Helper method.
         * Add row to tbody (for observation rows at top-level or within subtable) 
         */
        private void addObservationRow(HtmlTbody tbody, HtmlTr observationRow) {
            if (observationRow == null || tbody == null) {
                return;
            }
   
            HtmlTd td = observationRow.getTd(0);
            
            if (td != null) {
                HtmlTh th = new HtmlTh();
                th.setValue(td.getValue());
            
                if (!rowIsRightClear(observationRow)) {
                        th.setAttribute(DATA_ATTR_ROLE, ROLE_OBSERVATION_LABEL);  
                }         
                observationRow.replaceChild(td, th);
            }

            // Add a deep-copy of the row to mainTable
            HtmlTr trDeepCopy = (HtmlTr) (HtmlTr.create(observationRow));
            tbody.addRow(trDeepCopy);
        }
        
        private void addSubtableRow(HtmlTbody tbody, HtmlTr subtableRow, boolean isSubtableHeaderRow) {
            String rowLabel = extractRowLabel(subtableRow);
            LOG.trace("addSubtableRow: Add ST "+(isSubtableHeaderRow ? "header" : "row" )+":"+rowLabel);
            
            if (isSubtableHeaderRow) {
                subtableRow.setAttribute(DATA_ATTR_TBL_SEG, TBL_SEG_SUBTABLE_TITLE);   
            } else {
                subtableRow.setAttribute(DATA_ATTR_ROLE, ROLE_OBSERVATION);
            }
            
            addObservationRow(tbody, subtableRow);
        }

        /**
         * Helper method.  
         * Add top-level row to table.
         * Top-level rows are observation labels and observation data.
         */
        private void addTopLevelRow(HtmlTbody mainTableTbody, HtmlTr topLevelRow) {
            this.addObservationRow(mainTableTbody, topLevelRow);
        }

        /**
         * Helper method.
         * @return Label 
         */
        String extractRowLabel(HtmlTr row) {
            String rowLabel = "";
            
            if (row != null) {
                if (row.getTd(0) != null) {
                    rowLabel = row.getTd(0).getValue();
                }
            } 
            
            return rowLabel;
        }
              
        /**
         * Compare coordinates with explicit tolerance
         * 
         */
        private boolean isEqualTo(double d1, double d2, double tolerance) {
            return (Math.abs(d1 - d2) <= tolerance);
        }
        
        /**
         * Compare coordinates with explicit tolerance
         * 
         */
        private boolean isGreaterThan(double d1, double d2, double tolerance) {
            return (d1 > d2 && (Math.abs(d1 - d2) > tolerance));
        }
        
        /**
         * Compare coordinates with explicit tolerance
         * 
         */
        private boolean isLessThan(double d1, double d2, double tolerance) {
            return (d2 > d1 && (Math.abs(d2 - d1) > tolerance));
        }
        
        /**
         * Merge unallocated right clears (after subtable identification).
         *
         * @param tbody The top-level tbody for the table
         */
        private void mergeUnwrappedObservationLabels(HtmlTbody tbody) {
            if (tbody == null) {
                return;
            }
            
            // Conceptually a table consists of a list of rowgroups.
            // These may be true rowgroups (i.e., subtables) (HTML tbody)
            // or the degenerate case of an individual top-level observation row (HTML tr) 
            List<HtmlElement> tbodyElts = tbody.getChildElementsList();
            
            HtmlElement prevRowGroup = null;
            double prevRowMinX = 0.0;
            double curRowMinX = 0.0;
           
            for (int i = tbodyElts.size() - 1; i >= 0; i--) {
                // tr: If n is unlabelled right clear then merge th with n+1 and discard line
                // tbody: recurse into subtable 
            	HtmlElement curRowGroup = tbodyElts.get(i);
                
                if (curRowGroup instanceof HtmlTbody) {
                    // Handle subtable case
                    mergeUnwrappedObservationLabels((HtmlTbody)curRowGroup);
                } else if (curRowGroup instanceof HtmlTr) {
                    if (i < tbodyElts.size() - 1) {
                        if (prevRowGroup instanceof HtmlTr) {
                            if (obsRowIsRightClear((HtmlTr) prevRowGroup)) {
                                // Only merge lines with the same level of indent
                                Attribute attr = curRowGroup.getAttribute("data-rowminx");
                                curRowMinX = Double.parseDouble(attr == null ? "-1.0" : attr.getValue());
                                if (isEqualTo(curRowMinX, prevRowMinX, xEpsilon)) {
                                    // Append the text from this row's header onto the 
                                    // header from the previous row and delete this row
                                    HtmlTh prevRowHeader = (HtmlTh) prevRowGroup.getChild(0);
                                    HtmlTh curRowHeader = (HtmlTh) curRowGroup.getChild(0);
                                    String currentHeaderText = curRowHeader.getValue();
                                    String mergedHeaderText = currentHeaderText + " " + prevRowHeader.getValue();
                                    curRowHeader.setValue(mergedHeaderText);
                                    tbody.removeChild(i + 1);
                                    LOG.trace("Merge dangling row-header text:row:" + i + "-" + (i + 1) + ":" + mergedHeaderText);
                                }
                            }
                        }
                    }
                } 
                
                prevRowGroup = curRowGroup;
                prevRowMinX = curRowMinX;
            }
        }
        
        /**
         * Determine the number of derived columns for each raw column
         * resulting from splitting columns to separate all floating point values.
         * @param tbody The body of the table
         * @param columnCount The number of columns in the original table grid by layout
         * @return 
         */
        private int[] determineSplitColumnDimensions(HtmlTbody tbody, int columnCount) {
            // FIXME Record whether table has any compound columns -- avoid unnecessary generation of the supplemental table
            int[] compoundDimensions = new int[columnCount];
            Arrays.fill(compoundDimensions, 1);
            
            if (tbody != null) {
                List<HtmlTr> rows = tbody.getOrCreateChildTrs();

                if (rows != null) {
                    for (int i = 0; i < rows.size(); i++) {
                        List<String> cellValues = rows.get(i).getTdCellValues();

                        for (int j = 1; j < cellValues.size(); j++) {
                            String cellValue = cellValues.get(j);
                                                                
                            Matcher m = COMPOUND_COL_NUMS.matcher(cellValue);
                            List<String> tokens = new LinkedList<String>();
                            
                            while (m.find()) {
                                String token = m.group(1);
                                tokens.add(token);
                            }
                                
                            // Find the maximum dimension of the new column set
                            if (tokens.size() > compoundDimensions[j]) {
                                tableHasCompoundColumns = true;
                                compoundDimensions[j] = tokens.size();
                            }
                        }
                    }
                }
            }
            
            return compoundDimensions;
        }
        
        /**
         * Find columns containing semantically distinct numerical values combined with standard punctuation.
         */
        private void splitCompoundColumnContent(HtmlTbody tbody, int columnCount) {
            // Search table for columns with multiple numerical content values
            if (tbody != null) {
                List<HtmlTr> rows = tbody.getOrCreateChildTrs();

                if (rows != null) {
                    int[] compoundDimensions = determineSplitColumnDimensions(tbody, columnCount);
                    
                    if (tableHasCompoundColumns) {
                        // For each cell in the original grid, there is a list of separate values
                        List<String>[][] newStructure = new ArrayList[columnCount][rows.size()];

                        for (int i = 0; i < rows.size(); i++) {
                            HtmlTr tr = rows.get(i);
                            List<String> cellValues = tr.getTdCellValues();

                            for (int j = 1; j < cellValues.size(); j++) {
                                String cellValue = cellValues.get(j);

                                Matcher m = COMPOUND_COL_NUMS.matcher(cellValue);
                                List<String> tokens = new ArrayList<String>(compoundDimensions[j]);

                                while (m.find()) {
                                    String token = m.group(1);
                                    tokens.add(token);
                                }

                                if (tokens.size() > 0) {
                                    newStructure[j][i] = tokens;
                                } else {
                                    newStructure[j][i] = new ArrayList<String>(
                                            Collections.nCopies(compoundDimensions[j], cellValue));
                                }

                                // If original column is split, add multiple cells to end of row
                                if (compoundDimensions[j] > 1) {
                                   for (int k = 0; k < compoundDimensions[j]; k++) {
                                        String token = "";
                                        
                                        // Handle case where there are fewer values in this column row 
                                        // than the maximum detected in the column (due to use of column
                                        // to hold values in different formats, or publisher error/inconsistency)
                                        if (k > newStructure[j][i].size() - 1) {
                                            // If there are no more extracted values for this row
                                            // duplicate first/only value
                                            token = newStructure[j][i].get(0);
                                        } else {
                                            token = newStructure[j][i].get(k);
                                        }
                                    
                                        HtmlElement td = HtmlTd.createAndWrapText(token);
                                        td.addAttribute(new Attribute("data-role", SUPPLEMENTARY_OBS));
                                        if (token == null || token.equals("")) {
                                            td.setClassAttribute("empty");
                                        } else {
                                            td.setClassAttribute("cell");
                                        }
                                        tr.appendChild(td);
                                    }
                                }
                            }
                        }

                        // Create new header structures
                        createSupplementalSplitHeaders(compoundDimensions);
                    }
                }
            }
        }
        
        /**
         * Create header structure for supplemental split-column table
         */
        private void createSupplementalSplitHeaders(int[] compoundDimensions) {
            HtmlTr directColumnHeaders = (HtmlTr)this.tableHtmlThead.getChild(this.tableHtmlThead.getChildCount() - 1);
            List<HtmlTh> ths = directColumnHeaders.getThChildren();
            for (int j = 1; j < compoundDimensions.length; j++) {
                if (compoundDimensions[j] > 1) {
                    String headerName = ths.get(j).getValue();
                    for (int k = 0; k < compoundDimensions[j]; k++) {
                        // Append additional header cells corresponding to split columns
                        HtmlTh suppTh = HtmlTh.createAndWrapText(headerName+":"+k);
                        suppTh.addAttribute(new Attribute("data-role", SUPPLEMENTARY_HEADER));
                        directColumnHeaders.appendChild(suppTh);
                    }
                }
            }
            
            createSupplementalColumnTreeHeaders(compoundDimensions);
        }
        
        private void createSupplementalColumnTreeHeaders(int[] compoundDimensions) {
            List<HtmlTr> allColumnHeaderRows = this.tableHtmlThead.getChildTrs();
            
            if (allColumnHeaderRows == null || allColumnHeaderRows.size() < 2) {
                return;
            }
            
            int totalCellsSpanned = 0;
            
            for (int i = 0; i < allColumnHeaderRows.size() - 1; i++) {
                HtmlTr tr = allColumnHeaderRows.get(i);
                List<HtmlTh> ths = tr.getThChildren();
                Integer startSpanCol = 0;
                Integer endSpanCol= 0;
                Integer endSpanColPrev = 0;
                
                for (int hj = this.headerOffset; hj < ths.size(); hj++) {
                    HtmlTh th = ths.get(hj);
                    String superHeaderName = th.getValue();
                    int splitColSpan = 0;
                    
                    // Get range of grid columns spanned by this super-header
                    Attribute startSpanColAttr = th.getAttribute(START_HDR_COL);
                    Attribute endSpanColAttr = th.getAttribute(END_HDR_COL);
                    
                    if (startSpanColAttr != null && endSpanColAttr != null) {
                        endSpanColPrev = endSpanCol;
                        startSpanCol = Integer.parseUnsignedInt(startSpanColAttr.getValue());
                        endSpanCol = Integer.parseUnsignedInt(endSpanColAttr.getValue());
                
                        LOG.trace("Super header col:"+hj+":"+"start:"+startSpanCol+":end:"+endSpanCol);
                        
                        // New colspan
                        boolean spansCompoundColumns = false;
                        for (int s = startSpanCol; s < endSpanCol + 1; s++) {
                            LOG.trace("compDim:col:"+s+"="+compoundDimensions[s]);
                            splitColSpan += (compoundDimensions[s] > 1 ? compoundDimensions[s] : 0);
                            spansCompoundColumns = spansCompoundColumns || compoundDimensions[s] > 1; 
                        }
                        
                        LOG.trace("New colspan:"+splitColSpan);
                        if (spansCompoundColumns) {
                            // Append additional header cells corresponding to split columns
                            HtmlTh suppTh = HtmlTh.createAndWrapText(superHeaderName);
                            suppTh.setAttribute(COLSPAN, Integer.toString(splitColSpan));
                            suppTh.addAttribute(new Attribute("data-role", SUPPLEMENTARY_HEADER));
                            tr.appendChild(suppTh);
                            totalCellsSpanned += splitColSpan;
                        } 
                    } else {
                        LOG.trace("Super-column:"+hj+":grid-col range:["+endSpanColPrev+","+startSpanCol+"]");
                        // Interpolate any empty superheaders needed 
                        int startPadColHeader = this.headerOffset;
                        int endPadColHeader = startPadColHeader + 1;
                        if (endSpanColPrev > this.headerOffset && startSpanCol > endSpanColPrev) {
                            // This is a gap between supercolumn header spans
                            startPadColHeader = endSpanColPrev;
                            endPadColHeader = startSpanCol - 1;
                        }
                        for (int e = startPadColHeader; e < endPadColHeader; e++) {
                            if (compoundDimensions[e] > 1) {
                                for (int e1 = 0; e1 < compoundDimensions[e]; e1++) {
                                    HtmlTh thDeepCopy = (HtmlTh) (HtmlTh.create(th));
                                    tr.appendChild(thDeepCopy);
                                    totalCellsSpanned++;
                                }
                            } 
                        }
                    }
                }
                           
                // Pad the superheader row if necessary
                if (totalCellsSpanned < compoundDimensions.length) {
                    int c = endSpanColPrev + 1;
                    
                    // Find all remaining compound columns not under superheaders
                    while (c < compoundDimensions.length) {
                        if (compoundDimensions[c] > 1) {
                           for (int pp = 0; pp < compoundDimensions[c]; pp++) {
                                HtmlTh thEmpty = new HtmlTh();
                                thEmpty.setClassAttribute(CELL_EMPTY);
                                thEmpty.addAttribute(new Attribute("data-role", SUPPLEMENTARY_HEADER));
                                tr.appendChild(thEmpty);
                           }
                        } 
                        c++;
                    }
                }
            }
        }

	/**
         * Add the table title as the HTML caption element
         * @param svgElement
         * @param table 
         */
	private void addCaption(AbstractCMElement svgElement, HtmlTable table) {
		HtmlCaption caption = new HtmlCaption();
		String captionString = svgElement == null ? null : XMLUtil.getSingleValue(svgElement, ".//*[local-name()='g' and @class='"+TableTitleSection.TITLE_TITLE+"']");
                if (captionString != null) {
                       int idx = captionString.indexOf("//");
                       captionString = idx == -1 ? captionString : captionString.substring(idx + 2);
                       caption.appendChild(captionString);
                       table.appendChild(caption);
                }
	}
        
        /**
         * Add the table footer as the HTML tfoot element
         * @param svgElement
         * @param table 
         */
	private void addFooter(AbstractCMElement svgElement, HtmlTable table, int bodyCols) {
		HtmlTfoot htmlTfoot = new HtmlTfoot();
		String tableFooterString = svgElement == null ? null : XMLUtil.getSingleValue(svgElement, ".//*[local-name()='g' and @class='"+TableFooterSection.FOOTER_TITLE +"']");
                if (tableFooterString != null) {
                       int idx = tableFooterString.indexOf("//");
                       tableFooterString = idx == -1 ? tableFooterString : tableFooterString.substring(idx + 2);
                       tableFooterString = ValueNormaliser.removeUnusualCharacterTooltip(tableFooterString);                       
                       // tfoot is a row group so the content must be wrapped
                       // as at least one row
                       HtmlTr tr = new HtmlTr();
                       HtmlTd td = new HtmlTd();
                       td.setAttribute(COLSPAN, Integer.toString(bodyCols));
                       tr.appendChild(td);
		       td.appendChild(tableFooterString);
                       htmlTfoot.appendChild(tr);
                       
                       this.tableHtmlTfoot = htmlTfoot;
                       if (this.tableHtmlTfoot != null) {
                          table.appendChild(this.tableHtmlTfoot);
                       }
                       LOG.debug("Adding footer:"+tableFooterString);
                }
	}

	public AbstractCMElement getAnnotatedSvgChunk() {
		return annotatedSvgChunk;
	}

}

package org.contentmine.cproject.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.DataTableLookup;
import org.contentmine.cproject.util.ResultsAnalysis.SummaryType;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlButton;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlElement.Target;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlScript;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTfoot;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTr;

import nu.xom.Element;

public class DataTablesTool {

	private static final Logger LOG = Logger.getLogger(DataTablesTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String ASPNET_AJAX = "http://ajax.aspnetcdn.com/ajax/";
	public static final String JQUERY_DATA_TABLES_MIN_JS = ASPNET_AJAX+"jquery.dataTables/1.9.4/jquery.dataTables.min.js";
	public static final String JQUERY_1_8_2_MIN_JS = ASPNET_AJAX+"jQuery/jquery-1.8.2.min.js";
	public static final String JQUERY_DATA_TABLES_CSS = ASPNET_AJAX+"jquery.dataTables/1.9.4/css/jquery.dataTables.css";
	public final static String DATA_TABLE_FUNCTION0 = ""
	+ "$(function()  {\n"
	+ "$(\"#";
	public final static String DATA_TABLE_FUNCTION1 = ""
	+ "\").dataTable();\n"
	+ "})\n";
//	+ "	var node = document.getElementById('node-id');"
//	+ " node.innerHTML('<p>some dynamic html</p>');";	
	public static final String TABLE = "table";
	public static final String TABLE_STRIPED = "table-striped";
	public static final String TABLE_BORDERED = "table-bordered";
	public static final String TABLE_HOVER = "table-hover";
	
	public static final String TARGET = "target";
	
	private static final String RESULTS = "results";
	public static final String ARTICLES = "articles";
	public static final String BIBLIOGRAPHY = "bibliography";
	
	private static final String DEFAULTS = 
			    DataTablesTool.TABLE+
			" "+DataTablesTool.TABLE_STRIPED+
			" "+DataTablesTool.TABLE_BORDERED+
			" "+DataTablesTool.TABLE_HOVER;
	private static final String BS_EXAMPLE_TABLE_RESPONSIVE = "bs-example table-responsive";

	private String title;
	private String tableId; // HTML ID of table element

	public List<CellRenderer> cellRendererList;
	private List<String> rowHeadingList;
	private String rowHeadingName;
	private CellCalculator cellCalculator;
	private String remoteLink0;
	private String remoteLink1;
	private String localLink0;
	private String localLink1;
	private List<HtmlTd> footerCells;
	private HtmlTd footerCaption;
	private String rowLabelId;
	private File projectDir;
	private ResultsAnalysis resultsAnalysis;
	private String bibliographyId;
	private Map<String, AbstractMetadata> metadataByCTreename;
	private DataTableLookup dataTableLookup;
	private boolean addWikidataBiblio;

	public DataTablesTool() {
		this.setTableId(RESULTS);
		setDefaults();
	}

	public DataTablesTool(String rowLabelId, String bibliographyId) {
		this();
		this.setRowLabelId(rowLabelId);
		this.setBibliographyId(bibliographyId);
	}

	private void setDefaults() {
		remoteLink0 = "http://epmc.org/";
		remoteLink1 = "";
		localLink0 = "";
		localLink1 = "/scholarly.html";
		addWikidataBiblio = false;
	}

	public DataTablesTool(CellCalculator cellCalculator) {
		this();
		this.setCellCalculator(cellCalculator);
	}
	
	public void setCellCalculator(CellCalculator cellCalculator) {
		this.cellCalculator = cellCalculator;
	}

	public void makeDataTableHead(HtmlHtml html) {
		HtmlHead head = html.getOrCreateHead();
		head.addTitle(title);
		head.addCSSStylesheetLink(JQUERY_DATA_TABLES_CSS);
		head.addJavascriptLink(JQUERY_1_8_2_MIN_JS);
		head.addJavascriptLink(JQUERY_DATA_TABLES_MIN_JS);
		String script = DATA_TABLE_FUNCTION0 + tableId + DATA_TABLE_FUNCTION1;
		head.addJavascript(script);
	}

	private void createA(String href, String aValue, HtmlTd htmlTd, String title) {
		if (href != null && href.trim().length() > 0) {
			HtmlA htmlA = new HtmlA();
			htmlA.appendChild(aValue);
			htmlA.setHref(href);
			htmlA.setTarget(Target.separate);
			if (title != null) htmlA.setTitle(title);
			htmlTd.appendChild(htmlA);
		}
	}

	public HtmlThead createHtmlHead() {
		HtmlThead htmlThead = new HtmlThead();
		HtmlTr htmlTr = new HtmlTr();
		htmlThead.appendChild(htmlTr);
		// normally "articles"
		htmlTr.appendChild(createColumnHeading(this.getRowLabelId()));
		htmlTr.appendChild(createColumnHeading(this.getBibliographyId()));
		addRemainingColumnHeadings(htmlTr);
		return htmlThead;
	}

	private HtmlTh createColumnHeading(String id) {
		HtmlTh htmlTh = new HtmlTh();
		htmlTh.appendChild(id);
		return htmlTh;
	}

	private void addRemainingColumnHeadings(HtmlTr htmlTr) {
		
		for (CellRenderer renderer : cellRendererList) {
			if (renderer.isVisible()) {
				HtmlTh htmlTh = new HtmlTh();
				htmlTr.appendChild(htmlTh);
				htmlTh.appendChild(renderer.getFlag());
			}
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTableId(String id) {
		this.tableId = id;
	}
	
	public String getId() {
		return tableId;
	}

	public void setRowHeadingList(List<String> rowHeadingList) {
		this.rowHeadingList = rowHeadingList;
	}

	public List<String> getOrCreateRowHeadingList() {
		if (rowHeadingList == null) {
			rowHeadingList = new ArrayList<String>();
		}
		return rowHeadingList;
	}

	public void setCellRendererList(List<CellRenderer> cellRendererList) {
		this.cellRendererList = cellRendererList;
	}

	public List<CellRenderer> getOrCreateColumnHeadingList() {
		if (cellRendererList == null) {
			cellRendererList = new ArrayList<CellRenderer>();
		}
		return cellRendererList;
	}

	/** this calls addCellValues(htmlTr, rowHeading) which includes ResultsAnalysis logic.
	 * 
	 * @param cellCalculator TODO
	 * @param remoteLink0
	 * @param remoteLink1
	 * @param htmlTbody
	 */
	public void addRows(HtmlTbody htmlTbody) {
		for (int iRow = 0; iRow < rowHeadingList.size(); iRow++) {
			if (iRow % 10 == 0) System.out.print("r");
			String rowHeading = rowHeadingList.get(iRow);
			HtmlTr htmlTr = new HtmlTr();
			htmlTbody.appendChild(htmlTr);
			String title = "fixme";
			
			HtmlTd htmlTd = createBibliographicRefCell(rowHeading, "remote PDF on server", title, "HTML on local server");
			htmlTr.appendChild(htmlTd);
			htmlTd = createBibliographicDataCell(rowHeading);
			htmlTr.appendChild(htmlTd);
			
			cellCalculator.addCellValues(cellRendererList, htmlTr, iRow);
		}
	}

	private HtmlTd createBibliographicRefCell(String rowId, String rowHeadingTitle, String title, String titleTitle) {
		String remoteHref = createHref(remoteLink0, rowId, remoteLink1);
		String localHref = createHref(localLink0, rowId, localLink1);
		
		HtmlTd htmlTd1 = new HtmlTd();
		createA(remoteHref, rowId, htmlTd1, rowHeadingTitle);
		createA(localHref, "local", htmlTd1, titleTitle);
		if (addWikidataBiblio) {
			String qid = getQIDForPMC(rowId);
			if (qid != null) {
				createA("https://www.wikidata.org/wiki/"+qid, "wd:"+qid, htmlTd1, "wikidata");
			}
		}
		HtmlTd htmlTd = htmlTd1;
		htmlTd.setTitle(title);
		
		return htmlTd;
	}

	private String getQIDForPMC(String rowId) {
		String PMCID = "P932";
		// remove leading PMC
		rowId = rowId.replace("PMC", "");
		String query =
				"SELECT ?item " + "WHERE {" +
                "  ?item wdt:" + PMCID + " " + "\"" + rowId + "\"" + " ." +
                "}";
		Element element = null;
		try {
			element = dataTableLookup.createSparqlLookup(query);
		} catch (IOException e) {
			throw new RuntimeException("Cannot query for QID", e);
		}
		/**
		<result>
			<binding name="item">
				<uri>http://www.wikidata.org/entity/Q25257418</uri>
			</binding>
		</result>
		 */
//		LOG.debug("el "+element.toXML());
		String qidString = XMLUtil.getSingleValue(element, ".//*[local-name()='binding' and @name='item']/*[local-name()='uri']");
		String[] fields = qidString == null ? null : qidString.split("/");
		String qid = fields == null ? null : fields[fields.length - 1];
//		LOG.debug("qid "+qid);
		return qid;
	}

	private HtmlTd createBibliographicDataCell(String rowHeading) {
		HtmlTd td = new HtmlTd();
		HtmlDiv div = null;
		if (metadataByCTreename != null) {
			AbstractMetadata abstractMetadata = metadataByCTreename.get(rowHeading);
			if (abstractMetadata != null) {
				div = abstractMetadata.createSimpleHtml();
			}
		} 
		if (div == null) {
			div = new HtmlDiv();
			div.appendChild(HtmlSpan.createSpanWithContent("no bibliographic metadata supplied"));
		}
		td.appendChild(div);
		return td;
	}

	private String createHref(String link0, String rowHeading, String link1) {
		String href = ((link0 == null) ? "" : link0) 
				+ rowHeading + ((link1 == null) ? "" : link1);
		return href;
	}

	public void addCellValuesToRow(HtmlTr htmlTr, int iRow) {
		for (int iCol = 0; iCol < cellRendererList.size(); iCol++) {
			HtmlElement htmlTd = new HtmlTd();
			htmlTr.appendChild(htmlTd);
			HtmlElement contents = cellCalculator.createCellContents(iRow, iCol);
//			contents = contents == null ? "" : contents;
			if (contents != null) {
				htmlTd.appendChild(contents);
			}
		}
	}

	public HtmlTable createHtmlDataTable() {
		HtmlTable htmlTable = new HtmlTable();
		htmlTable.appendChild(createHtmlHead());
		HtmlTbody htmlTbody = new HtmlTbody();
		htmlTable.appendChild(htmlTbody);
		addRows(htmlTbody);
		addFooter(htmlTable);
		return htmlTable;
	}

	/** footer contains counts, I think.
	 * 
	 * @param htmlTable
	 * @return
	 */
	private HtmlTfoot addFooter(HtmlTable htmlTable) {
		HtmlTfoot htmlTfoot = new HtmlTfoot();
		if (footerCaption == null || footerCells == null) {
			LOG.trace(""
					+ ""
					+ "caption or cells");
		} else if (footerCells.size() != cellRendererList.size()) {
			LOG.error("Wrong number of footer cells: "+footerCells.size()+" != "+cellRendererList.size());
			return null;
		} else {
			HtmlTr tr = new HtmlTr();
			htmlTfoot.appendChild(tr);
			tr.appendChild(footerCaption);
			for (int i = 0; i < cellRendererList.size(); i++) {
				tr.appendChild(footerCells.get(i));
			}
		}
		htmlTable.appendChild(htmlTfoot);
		return htmlTfoot;
	}

	private void addDiv(HtmlTd td, int i) {
		HtmlDiv div = new HtmlDiv();
		div.appendChild("D"+i);
		td.appendChild(div);
	}

	public HtmlHtml createHtmlWithDataTable(HtmlTable table) {
		HtmlHtml html = HtmlHtml.createUTF8Html();
		makeDataTableHead(html);
		HtmlDiv htmlDiv = new HtmlDiv();
		htmlDiv.setClassAttribute(BS_EXAMPLE_TABLE_RESPONSIVE);
		html.getOrCreateBody().appendChild(htmlDiv);
		htmlDiv.appendChild(table);
		return html;
	}

	private void addButton(HtmlBody body) {
		HtmlButton button = new HtmlButton("Click me");
		button.setOnClick("testFunction(alert('click!!'));");
		body.appendChild(button);
	}

	private void addScript(HtmlBody body) {
		String scriptString = ""
				+ "var node = document.getElementById('footer-id');\n"
				+ "node.innerHTML('<p>some dynamic html</p>');";
		HtmlScript script = new HtmlScript();
		script.setCharset("UTF-8");
		script.setType("text/javascript");
		script.appendChild(scriptString);
		body.appendChild(script);
	}

	public void setRowHeadingName(String rowHeadingName) {
		this.rowHeadingName = rowHeadingName;
	}

	public void setRemoteLink0(String link0) {
		this.remoteLink0 = link0;
	}

	public String getRemoteLink0() {
		return remoteLink0;
	}
	
	public void setRemoteLink1(String link1) {
		this.remoteLink1 = link1;
	}

	public String getRemoteLink1() {
		return remoteLink1;
	}

	public void setLocalLink0(String link0) {
		this.localLink0 = link0;
	}

	public String getLocalLink0() {
		return localLink0;
	}
	
	public void setLocalLink1(String link1) {
		this.localLink1 = link1;
	}

	public String getLocalLink1() {
		return localLink1;
	}

	public HtmlHtml createHtml(CellCalculator cellCalculator) {
		HtmlTable htmlTable = createHtmlDataTable();
		htmlTable.setClassAttribute(DataTablesTool.DEFAULTS);
		htmlTable.setId(tableId);
		HtmlHtml html = createHtmlWithDataTable(htmlTable);
		return html;
	}
	
	public void setFooterCaption(HtmlTd caption) {
		this.footerCaption = caption;
	}
	
	public void setFooterCells(List<HtmlTd> cells) {
		this.footerCells = cells;
	}
	
	public String getRowLabelId() {
		return rowLabelId;
	}

	public String getBibliographyId() {
		return bibliographyId;
	}

	public void setRowLabelId(String rowLabelId) {
		this.rowLabelId = rowLabelId;
	}

	public void setBibliographyId(String bibliographyId) {
		this.bibliographyId = bibliographyId;
	}

	public void setProjectDir(File projectDir) {
		this.projectDir = projectDir;
	}

	public File getProjectDir() {
		return projectDir;
	}

	public void createTableComponents(ResultsAnalysis resultsAnalysis) throws IOException {
		String project = FilenameUtils.getBaseName(this.projectDir.toString());
		setTitle(project);
		this.resultsAnalysis = resultsAnalysis;

		// several different table types (count, summary, etc.)
		this.createAndWriteResultsTableForSummaryTypes();
		
		LOG.trace(cellRendererList);
		
		addTableFooter();
	}

	private void addTableFooter() {
		List<HtmlTd> footerList = this.createFooterList();
		HtmlTd caption = new HtmlTd();
		caption.appendChild("counts");
		setFooterCaption(caption);
		setFooterCells(footerList);
	}

	public List<HtmlTd> createFooterList() {
		List<HtmlTd> footerList = new ArrayList<HtmlTd>();
		for (CellRenderer cellRenderer : cellRendererList) {
			HtmlTd td = new HtmlTd();
			td.appendChild(cellRenderer.getHeading());
			footerList.add(td);
		}
		return footerList;
	}

	private void createAndWriteResultsTableForSummaryTypes() throws IOException {
		for (SummaryType summaryType : ResultsAnalysis.SUMMARY_TYPES) {
			resultsAnalysis.setSummaryType(summaryType);
			HtmlTable table = resultsAnalysis.makeHtmlDataTable();
			HtmlHtml html = createHtmlWithDataTable(table);
			File outfile = new File(projectDir, summaryType.toString()+"."+CProject.DATA_TABLES_HTML);
			XMLUtil.debug(html, outfile, 1);
		}
	}

	public static DataTablesTool createBiblioEnabledTable() {
		return new DataTablesTool(ARTICLES, BIBLIOGRAPHY);
	}

	public void setMetadataByTreename(Map<String, AbstractMetadata> metadataByCTreename) {
		if (metadataByCTreename == null) {
			LOG.warn("Null metadataByTreename");
		}
		this.metadataByCTreename = metadataByCTreename;
	}

	public void setLookup(DataTableLookup dataTableLookup) {
		this.dataTableLookup = dataTableLookup;
	}

	public boolean isAddWikidataBiblio() {
		return addWikidataBiblio;
	}

	public void setAddWikidataBiblio(boolean addWikidataBiblio) {
		this.addWikidataBiblio = addWikidataBiblio;
	}

}

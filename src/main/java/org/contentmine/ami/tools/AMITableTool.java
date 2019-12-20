package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.table.ColumnMatcher;
import org.contentmine.ami.tools.table.FileMatcher;
import org.contentmine.ami.tools.table.TQueryTool;
import org.contentmine.ami.tools.table.TTemplate;
import org.contentmine.ami.tools.table.TTemplateList;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.RectTabColumn;
import org.contentmine.cproject.util.RectangularTable;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTfoot;
import org.contentmine.graphics.html.HtmlTr;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import picocli.CommandLine.Option;
/**
 * 
 * @author pm286
 *
 */
public class AMITableTool extends AbstractAMITool {
	private static final Logger LOG = Logger.getLogger(AMITableTool.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final int INCONSISTENT_FOOTER = -2;
	public static final int NO_FOOTER = -1;
	public static final int NOT_FOUND = -1;
	// use "1" to mark a match
	public static final int FIRST_MATCH = 1;
	private static final String SUBTABLE = "subtable";

	/** for reading in
	 * 
	 * @author pm286
	 *
	 */
	public enum TableType {
		apa,
		grid,
	}
	
	public enum TableFormat {
		THBF,
		HBTF,
	}
	  @Option(names = {"--columntypes"},
		arity = "0",
	    description = "try to determine column types"
	    		+ "")
	private Boolean columnTypes = false;
	
	@Option(names = {"--multiset"},
		arity = "1..*",
	    description = "%n"
	    		+ "")
	private List<String> multisetList;
	  
	@Option(names = {"--summarytable"},
		arity = "1",
	    description = "summary table in CProject space which maps columns on per-table basis%n"
	    		+ "")
	private String summaryTableName;

	  @Option(names = {"--tabledir"},
		arity = "1",
	    description = "table directoryName (relative to CTree)")
	private String tableDirName = null;

	  @Option(names = {"--template"},
		arity = "1",
	    description = "extraction template to use ; if cannot be found , lists the others")
	private String templateName = null;

	  @Option(names = {"--templatefile"},
		arity = "1",
	    description = "file with (XML) extraction templates; if name is not absolute then relative to CProject ")
	private File templateFile = null;

	private TTemplateList templateListElement;
	private FileMatcher fileMatcher = null; // 
	private File tableDir;
	private TTemplate currentTemplate;

	private Map<String, Multiset<String>> multisetByName;

	private HtmlHtml templateSummaryHtml;
	private HtmlTable templateSummaryTable;

	private String currentCaption;
	private int columnSerial;


    /**
     * @param cProject
     */

	public AMITableTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMITableTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMITableTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
		System.out.println("multisetList         " + multisetList);
		System.out.println("summaryTable         " + summaryTableName);
		System.out.println("tableDirName         " + tableDirName);
		System.out.println("templateFile         " + templateFile);
		System.out.println("templateName         " + templateName);
		System.out.println();
		if (tableDirName == null) {
			System.err.println("must have tableDirname");
		}

	} 

	@Override
    protected void runSpecifics() {
		LOG.debug("runSpecifics");
		if (templateName != null) {
			getTemplates();
		}
		if (multisetList != null && multisetList.size() > 0) {
			multisetByName = new HashMap<>();
			for (String multisetName : multisetList) {
				multisetByName.put(multisetName, HashMultiset.create());
			}
		}
		File summaryTableFile = (cProject == null || summaryTableName == null) ? null :
			new File(cProject.getDirectory(), summaryTableName);

    	if (false) {
		} else if (processTrees()) {
			summarize();
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
	    }
    	
    }

	private void getTemplates() {
		templateListElement = TTemplateList.getOrCreateTemplateListElement(templateFile);
		if (templateListElement == null) {
			LOG.warn("Cannot find templateListElement for "+templateFile);
		}
		currentTemplate = (templateListElement == null) ? null : 
			templateListElement.getTemplateFor(templateName);
		if (currentTemplate == null) {
			LOG.warn("Cannot find template for "+templateName);
			return;
		} else {
			debugPrint(Verbosity.DEBUG, "found template for : "+templateName);
			templateSummaryHtml = HtmlHtml.createUTF8Html();
			templateSummaryTable = new HtmlTable();
			templateSummaryHtml.getOrCreateBody().appendChild(templateSummaryTable);

		}
		fileMatcher = currentTemplate.getTemplateList().getOrCreateFileMatcher();
		return;
	}


	public void processTree() {
		boolean debug = false;
//		if (!CMFileUtil.shouldMake(forceMake, sectionsDir, debug, sectionsDir)) {
//			if (debug) LOG.debug("skipped: "+sectionsDir);
//			return;
//		}
		tableDir = new File(cTree.getDirectory(), tableDirName);
		if (!tableDir.exists()) {
			System.err.println("table dir does not exist: "+tableDir);
			return;
		}
		if (columnTypes) {
			writeSubTablesForTreeAndUpdateMultisets();
		}
	}

	private void writeSubTablesForTreeAndUpdateMultisets() {
		if (fileMatcher != null) {
			String tableFileRegex = fileMatcher.getOrCreateQueryTool().getSingleRegex();
			List<File> listFilesFromPaths = Util.listFilesFromPaths(tableDir, tableFileRegex);
			debugPrint(Verbosity.DEBUG, "files to analyze: "+listFilesFromPaths.size()+"; "+tableFileRegex+" / "+listFilesFromPaths);
			List<HtmlTable> rawXhtmlTableList = makeRawXhtmlTables(listFilesFromPaths);
			debugPrint(Verbosity.DEBUG, "raw tables: "+rawXhtmlTableList.size());
			for (HtmlTable rawXhtmlTable : rawXhtmlTableList) {
				createSubTable(rawXhtmlTable);
			}
		}
	}

	private void createSubTable(HtmlTable rawXhtmlTable) {
		rawXhtmlTable.normalizeWhitespace();
		rawXhtmlTable.ensureTidy();
		HtmlTable subTable = createMatchedSubtable(rawXhtmlTable);
		if (subTable != null) {
			String id = rawXhtmlTable.getId();
			if (id != null) subTable.setId(id);
			HtmlHtml subTableHtml = HtmlHtml.createUTF8Html();
			subTableHtml.setId(id);
			HtmlBody body = subTableHtml.getOrCreateBody();
			body.appendChild(subTable);
			subTable.addCaption(currentCaption);
			addTableStyle(subTableHtml);
			if (subTable.getRows().size() > 0) {
				makeSummaryTableAndMultiset(subTable, subTableHtml);
			}
		}
	}

	private void makeSummaryTableAndMultiset(HtmlTable subTable, HtmlHtml subTableHtml) {
		/**
 <thead>
  <tr>
   <th>Art-id</th>
   <th>label</th>
   <th>caption</th>
  </tr>
 </thead>
 <tr>
  <td>PMC4391421</td>
  <td>
   <a href="../PMC4391421/sections/tables/table_1.html">Table 1</a>
  </td>
  <td>Chemical composition of thyme EO</td>
 </tr>
		 */
		String id = subTableHtml.getId();
		String serial = id == null ? "" : id.replaceAll(".*_", "");
		File subTableFile = new File(tableDir,  templateName+"_" + (serial) + "."+CTree.HTML);
		XMLUtil.writeQuietly(subTableHtml, subTableFile, 1);
		copyLinkToSubtableFile(serial, subTableFile);
		
		for (String multisetName : multisetList) {
			addColumnsToMultiset(subTable, multisetName);
		}
	}

	private void copyLinkToSubtableFile(String serial, File subTableFile) {
		String href = Util.getRelativeFilename(new File(cProject.getDirectory(), AMISectionTool.TABLE_SUMMARY_DIRNAME), subTableFile, "/");
		HtmlTr tr = new HtmlTr();
		tr.appendChild(new HtmlTd(cTree.getName()));
		HtmlTd td = new HtmlTd();
		td.appendChild(HtmlA.createFromHrefAndContent(href, SUBTABLE+"_"+serial));
		tr.appendChild(td);
		tr.appendChild(new HtmlTd(currentCaption));
		templateSummaryTable.appendChild(tr);
	}

	private void addColumnsToMultiset(HtmlTable subTable, String columnName) {
		RectangularTable rectTable = RectangularTable.createRectangularTable(subTable);
		int colIndex = rectTable.getColumnIndexFromRegex("^"+columnName+"=.*");
		if (colIndex == -1) {
			LOG.warn("Cannot find column: "+columnName);
		} else {
		
			HtmlTfoot tfoot = (HtmlTfoot) XMLUtil.getSingleElement(subTable, "./*[local-name()='"+HtmlTfoot.TAG+"']");
			HtmlTbody tbody = (HtmlTbody) XMLUtil.getSingleElement(subTable, "./*[local-name()='"+HtmlTbody.TAG+"']");
			if (tfoot != null && tbody != null) {
				List<HtmlTr> rows = tbody.getRowList();
				List<String> colValues = new ArrayList<>();
				for (HtmlTr tr : rows) {
					String value = tr.getTCellChildren().get(colIndex).getValue();
					colValues.add(value);
				}
			}
		}
		
//		subTable.addFooterSummary();
		RectTabColumn column = rectTable.getColumn(colIndex);
		if (column != null) {
			Multiset<String> multiset = multisetByName.get(columnName);
			if (multiset != null) {
				multiset.addAll(column.getValues());
			}
		}
	}

	private HtmlTable createMatchedSubtable(HtmlTable rawXhtmlTable) {
		currentCaption = rawXhtmlTable.getCaptionValue();
		TQueryTool queryTool = currentTemplate.getTitleMatcher().getOrCreateQueryTool();
		HtmlTable htmlSubTable = null;
		debugPrint(Verbosity.DEBUG, "queryTool "+queryTool.getSingleRegex());
		if (queryTool.matches(currentCaption)) {
			debugPrint(Verbosity.DEBUG, "matched table caption "+currentCaption);
			htmlSubTable = createHtmlSubTable(rawXhtmlTable);
		} else {
			debugPrint(Verbosity.DEBUG, "failed match caption "+currentCaption);
		}
		return htmlSubTable;
	}

	private HtmlTable createHtmlSubTable(HtmlTable rawXhtmlTable) {
		RectangularTable rectSubTable = new RectangularTable();
		RectangularTable rectTable = RectangularTable.createRectangularTable(rawXhtmlTable);
		HtmlTable denormalizedheader = rawXhtmlTable.getDenormalizedHeader();
		LOG.debug("DH "+denormalizedheader.toXML());
		LOG.debug("COLS "+rectTable.getColumnCount());
		List<RectTabColumn> columnList = rectTable.getCompleteColumnList();
		List<ColumnMatcher> columnMatcherList = currentTemplate.getOrCreateColumnMatcherList();
		int footerStart = NO_FOOTER;
		for (ColumnMatcher columnMatcher : columnMatcherList) {
			footerStart = columnMatcher.createColumnsAndAddToSubTable(rectSubTable, columnList, footerStart);
		}
		int consistentFooterStart = getConsistentFooterStart(footerStart);
		return rectSubTable.createHtmlTable(consistentFooterStart);
	}

	private int getConsistentFooterStart(int footerStart) {
		int consistentFooterStart = NO_FOOTER;
		if (footerStart == NO_FOOTER) {
//			LOG.debug("Cannot find footer");
		} else if (footerStart == INCONSISTENT_FOOTER) {
			LOG.warn("Inconsistent footer");
		} else {
			LOG.debug("SPLIT footer "+footerStart);
			consistentFooterStart = footerStart;
		}
		return consistentFooterStart;
	}

	private void summarize() {
		if (multisetList.size() > 0) {
			createAndOutputMultisets();
		}
		XMLUtil.writeQuietly(templateSummaryHtml, new File(cProjectDirectory, "__tables/"+templateName+"."+CTree.HTML), 1);
	}

	private void createAndOutputMultisets() {
		for (String colName : multisetByName.keySet()) {
			Multiset<String> words = multisetByName.get(colName);
			List<Entry<String>> listByCount = MultisetUtil.createListSortedByCount(words);
			try {
				File summaryTableDir = new File(cProject.getDirectory(), 
						AMISectionTool.SummaryType.table.getSummaryPath());
				File file = new File(summaryTableDir, colName+"_"+"multiset"+"."+CTree.TXT);
//					LOG.debug("writing to "+file);
				OutputStream fos = new FileOutputStream(file);
				StringBuilder sb = new StringBuilder();
				for (Entry<String> entry : listByCount) {
					sb.append(entry.toString()+"\n");
				}
				IOUtils.write(sb.toString(), fos);
				fos.close();
			} catch (Exception e) {
				System.err.println("cannot write multiset: "+e);
			}
		}
	}


	private void addTableStyle(HtmlHtml html) {
		HtmlStyle style = html.getOrCreateHead().getOrCreateHtmlStyle();
		String css = ""+
		"		  table {"
		+ "         background : red;"
		+ "         margin : 2px;"
		+ "       }\n"
		+ "       tr {"
		+ "         margin : 2px; "  
		+ "         background : #aaffff;"
		+ "       }\n"
		+ "		  td {"
		+ "         margin : 2px; "
		+ "         background : #ccffff;"
		+ "         border-left: 1px solid blue;"
		+ "       }\n"
		+ "       td[colspan='2'] {background: #ddddff} "
		+ "       td[rowspan='2'] {background: #ffdddd} "
		+ "       tfoot tr td {background-color : #ffb; font-weight : bold;} "
		+ ""
		+ ""
		+ ""
		;
		style.addCss(css);
	}


	private List<HtmlTable> makeRawXhtmlTables(List<File> tableFileList) {
		List<HtmlTable> htmlTableList = new ArrayList<>();
		for (File tableFile : tableFileList) {
			LOG.debug("file "+tableFile);
			String cTreeName = FilenameUtils.getBaseName(tableFile.getParentFile().getParentFile().getParentFile().toString());
			String tableName = FilenameUtils.getBaseName(tableFile.toString());
			List<HtmlTable> tableList = HtmlTable.extractTablesIgnoreNamespace(tableFile);
			if (tableList.size() > 1) {
				throw new RuntimeException("Table file should have one table: "+tableFile);
			} else if (tableList.size() == 0) {
				LOG.warn("Table file has no table: "+tableFile);
			} else {
				HtmlTable table = tableList.get(0);
				table.setId(cTreeName+"/"+tableName);
				htmlTableList.addAll(tableList);
			}
		}
		for (HtmlTable htmlTable : htmlTableList) {
			XMLUtil.replaceStyleMarkup(htmlTable);
		}
		return htmlTableList;
	}


}

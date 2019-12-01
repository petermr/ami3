package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public static final Logger LOG = Logger.getLogger(AMITableTool.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final int INCONSISTENT_FOOTER = -2;
	public static final int NO_FOOTER = -1;
	public static final int NOT_FOUND = -1;
	// use "1" to mark a matche
	public static final int FIRST_MATCH = 1;

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

//	private Element templateElement;
	private TTemplateList templateListElement;
	private FileMatcher fileMatcher = null; // 
	private File tableDir;
	private TTemplate currentTemplate;

	private Map<String, Multiset<String>> multisetByName;

	private HtmlHtml templateSummaryHtml;
	private HtmlTable templateSummaryTable;

	private String currentCaption;


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
		} else {
			LOG.debug("found template for : "+templateName);
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
		String tableFileRegex = fileMatcher.getOrCreateQueryTool().getSingleRegex();
		List<File> listFilesFromPaths = Util.listFilesFromPaths(tableDir, tableFileRegex);
//		LOG.debug("files to analyze: "+listFilesFromPaths.size()+"; "+tableFileRegex);
		List<HtmlTable> rawXhtmlTableList = makeRawXhtmlTables(listFilesFromPaths);
//		LOG.debug("raw tables: "+rawXhtmlTableList.size());
		int serial = 0;
		for (HtmlTable rawXhtmlTable : rawXhtmlTableList) {
			serial = createSubTable(++serial, rawXhtmlTable);
		}
	}

	private int createSubTable(int serial, HtmlTable rawXhtmlTable) {
		rawXhtmlTable.normalizeWhitespace();
		HtmlTable subTable = createMatchedSubtable(rawXhtmlTable);
		if (subTable != null) {
			HtmlHtml subTableHtml = HtmlHtml.createUTF8Html();
			subTableHtml.getOrCreateBody().appendChild(currentCaption);
			subTableHtml.getOrCreateBody().appendChild(subTable);
			addTableStyle(subTableHtml);
			if (subTable.getRows().size() > 0) {
				makeSummaryTableAndMultiset(serial, subTable, subTableHtml);
			}
		}
		return serial;
	}

	private void makeSummaryTableAndMultiset(int serial, HtmlTable subTable, HtmlHtml subTableHtml) {
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
		File subTableFile = new File(tableDir,  templateName+"_" + (serial) + "."+CTree.HTML);
		XMLUtil.writeQuietly(subTableHtml, subTableFile, 1);
		copyLinkToSubtableFile(serial, subTableFile);
		
		for (String multisetName : multisetList) {
			addColumnsToMultiset(subTable, multisetName);
		}
	}

	private void copyLinkToSubtableFile(int serial, File subTableFile) {
		String href = Util.getRelativeFilename(new File(cProject.getDirectory(), AMISectionTool.TABLE_SUMMARY_DIRNAME), subTableFile, "/");
		HtmlTr tr = new HtmlTr();
		tr.appendChild(new HtmlTd(cTree.getName()));
		HtmlTd td = new HtmlTd();
		td.appendChild(HtmlA.createFromHrefAndContent(href, "subtable_"+serial));
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
					String value = tr.getTdOrThChildren().get(colIndex).getValue();
					colValues.add(value);
				}
//				LOG.debug("VAL "+colValues);
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
		currentCaption = rawXhtmlTable.getCaption();
		TQueryTool queryTool = currentTemplate.getTitleMatcher().getOrCreateQueryTool();
		HtmlTable htmlSubTable = null;
		if (queryTool.matches(currentCaption)) {
			htmlSubTable = createHtmlSubTable(rawXhtmlTable);
		}
		return htmlSubTable;
	}

	private HtmlTable createHtmlSubTable(HtmlTable rawXhtmlTable) {
		RectangularTable rectSubTable = new RectangularTable();
		RectangularTable rectTable = RectangularTable.createRectangularTable(rawXhtmlTable);
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
			LOG.debug("Cannot find footer");
		} else if (footerStart == INCONSISTENT_FOOTER) {
			LOG.debug("Inconsistent footer");
		} else {
			AMITableTool.LOG.debug("SPLIT footer "+footerStart);
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
			List<HtmlTable> tableList = HtmlTable.extractTablesIgnoreNamespace(tableFile);
			htmlTableList.addAll(tableList);
		}
		return htmlTableList;
	}


}

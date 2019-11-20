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
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlTable;

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

//		if (tableRegex != null) {
//			extractAndWriteSubtables();
//		}
		if (columnTypes) {
			writeSubTablesAndMultisets();
		}
	}

	private void writeSubTablesAndMultisets() {
		String tableFileRegex = fileMatcher.getOrCreateQueryTool().getSingleRegex();
//		LOG.debug("TFR "+tableFileRegex);
		List<File> listFilesFromPaths = Util.listFilesFromPaths(tableDir, tableFileRegex);
		List<HtmlTable> rawXhtmlTableList = makeRawXhtmlTables(
				listFilesFromPaths);
//		LOG.debug("LF "+listFilesFromPaths.size());
//		LOG.debug("Tables "+rawXhtmlTableList.size()+"; "+tableFileRegex);
		int serial = 0;
		for (HtmlTable rawXhtmlTable : rawXhtmlTableList) {
			rawXhtmlTable.normalizeWhitespace();
			HtmlTable subTable = createMatchedSubtable(rawXhtmlTable);
			HtmlHtml html = new HtmlHtml();
			html.getOrCreateBody().appendChild(subTable);
			addTableStyle(html);
			if (subTable.getRows().size() > 0) {
				XMLUtil.writeQuietly(html, new File(tableDir,  templateName+/*"_"+"extracted"+*/"_"+(++serial)+"."+CTree.HTML), 1);
				for (String multisetName : multisetList) {
					addColumnsToMultiset(subTable, multisetName);
				}
			}
		}
	}

	private void addColumnsToMultiset(HtmlTable subTable, String columnName) {
		RectangularTable rectTable = RectangularTable.createRectangularTable(subTable);
		subTable.addFooterSummary();
		RectTabColumn column = rectTable.getColumn(columnName);
		if (column != null) {
			Multiset<String> multiset = multisetByName.get(columnName);
			if (multiset != null) {
				multiset.addAll(column.getValues());
			}
		}
	}

	private HtmlTable createMatchedSubtable(HtmlTable rawXhtmlTable) {
		HtmlTable subHtmlTable = new HtmlTable();
		RectangularTable rectSubTable = null;
		String caption = rawXhtmlTable.getCaption();
//		LOG.debug("current Template: "+currentTemplate.toXML());
		TQueryTool queryTool = currentTemplate.getTitleMatcher().getOrCreateQueryTool();
//		System.out.println("capt "+caption);
		if (queryTool.matches(caption)) {
			rectSubTable = new RectangularTable();
			System.out.println("table: "+caption);
			RectangularTable rectTable = RectangularTable.createRectangularTable(rawXhtmlTable);
			List<ColumnMatcher> columnMatcherList = currentTemplate.getOrCreateColumnMatcherList();
			for (ColumnMatcher columnMatcher : columnMatcherList) {
				List<RectTabColumn> columnList = rectTable.getCompleteColumnList();
				if (columnList !=  null) {
					createColumnsAndAddToSubTable(rectSubTable, columnMatcher, columnList);
				}
			}
			subHtmlTable = rectSubTable.createHtmlTable();
		}
		return subHtmlTable;
	}

	private void createColumnsAndAddToSubTable(RectangularTable subRectTable, ColumnMatcher columnMatcher, List<RectTabColumn> columnList) {
		for (int jcol = 0; jcol < columnList.size(); jcol++) {
			String colHeader = columnList.get(jcol).getHeader();
			TQueryTool queryTool = columnMatcher.getOrCreateTitleMatcher().getOrCreateQueryTool();
			LOG.debug(colHeader+"; "+queryTool.getOrPatternList()+"; "+queryTool.getOrPatternList().get(0).flags());
			if (queryTool.matches(colHeader)) {
				String colName = columnMatcher.getName();
				System.out.println("column: "+colName+" => "+colHeader);
				RectTabColumn rectangularCol = RectTabColumn.createColumn(
						columnList.get(jcol).getValues(), colName+"=("+colHeader+")");
				subRectTable.addColumn(rectangularCol);
			}
		}
	}

	private void summarize() {
		if (multisetList.size() > 0) {
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
		}
		fileMatcher = currentTemplate.getTemplateList().getOrCreateFileMatcher();
		return;
	}


	private void addTableStyle(HtmlHtml html) {
		HtmlStyle style = html.getOrCreateHead().getOrCreateHtmlStyle();
		String css = ""+
		"		  table {background : red;\n" + 
		"			  margin : 2px;}\n" + 
		"			  \n" + 
		"			  tr {margin : 2px; background : #aaffff;}";
		style.addCss(css);
	}


	private List<HtmlTable> makeRawXhtmlTables(List<File> tableFileList) {
		List<HtmlTable> htmlTableList = new ArrayList<>();
		for (File tableFile : tableFileList) {
			List<HtmlTable> tableList = HtmlTable.extractTables(tableFile);
			htmlTableList.addAll(tableList);
		}
		return htmlTableList;
	}


}

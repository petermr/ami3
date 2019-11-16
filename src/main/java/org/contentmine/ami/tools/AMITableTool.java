package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.table.TTColumn;
import org.contentmine.ami.tools.table.TTemplate;
import org.contentmine.ami.tools.table.TTemplateList;
import org.contentmine.ami.tools.table.TTitle;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.cproject.util.RectTabColumn;
import org.contentmine.cproject.util.RectangularTable;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTr;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import nu.xom.Element;
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

	private Element templateElement;
    private Map<String, List<Pattern>> columnFindPatternListMap;
	private TTemplateList templateListElement;
	private String tableRegex = null; // 
	private File tableDir;
	private TTemplate tableTemplateElement;

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
		
		if (templateName != null) {
			getTemplates();
			System.err.println("debug exit");
			return;
		}
		if (multisetList.size() > 0) {
			multisetByName = new HashMap<>();
			for (String multisetName : multisetList) {
				multisetByName.put(multisetName, HashMultiset.create());
			}
//			LOG.debug("MULTI "+multisetByName);
		}
		File summaryTableFile = (cProject == null || summaryTableName == null) ? null :
			new File(cProject.getDirectory(), summaryTableName);
//		WordCollectionFactory wordFactory = new WordCollectionFactory(null);
//		wordFactory.

    	if (false) {
		} else if (processTrees()) {
			summarize();
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
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
		tableTemplateElement = (templateListElement == null) ? null : 
			templateListElement.getTemplateFor(templateName);
		if (tableTemplateElement == null) {
			LOG.warn("Cannot find template for "+templateName);
		}
		tableRegex = tableTemplateElement.getTableRegex();
		columnFindPatternListMap = tableTemplateElement.getColumnPatternListMap();
		LOG.debug("vars "+templateListElement.getOrCreateVariableMap());
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

		if (tableRegex != null) {
//			extractAndWriteSubtables();
		}
		if (columnTypes) {
			writeSubTablesAndMultisets();
		}
	}

	private void writeSubTablesAndMultisets() {
		List<HtmlTable> rawXhtmlTableList = makeRawXhtmlTables(Util.listFilesFromPaths(tableDir, tableRegex));
		int serial = 0;
		for (HtmlTable rawXhtmlTable : rawXhtmlTableList) {
			rawXhtmlTable.normalize();
			HtmlTable subTable = createMatchedSubtable(rawXhtmlTable);
			HtmlHtml html = new HtmlHtml();
			html.getOrCreateBody().appendChild(subTable);
			addTableStyle(html);
			if (subTable.getRows().size() > 0) {
				XMLUtil.writeQuietly(html, new File(tableDir,  templateName+"_"+"extracted"+"_"+(++serial)+"."+CTree.HTML), 1);
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
		RectangularTable subRectTable = null;
		String caption = rawXhtmlTable.getCaption();
		TTitle titleElement = tableTemplateElement.getOrCreateTitleElement();
		if (titleElement.find(caption)) {
			subRectTable = new RectangularTable();
			System.out.println("table: "+caption);
			RectangularTable rectTable = RectangularTable.createRectangularTable(rawXhtmlTable);
			List<TTColumn> columnElementList = tableTemplateElement.getOrCreateColumnElementList();
			for (TTColumn columnElement : columnElementList) {
				List<RectTabColumn> columnList = rectTable.getCompleteColumnList();
				if (columnList !=  null) {
					
					for (int jcol = 0; jcol < columnList.size(); jcol++) {
						String colHeader = columnList.get(jcol).getHeader();
						if (columnElement.find(colHeader)) {
//							HtmlTr tr = rawXhtmlTable.getHeaderRow();
//							HtmlElement elem = tr.getTdOrThChildren().get(jcol);
//							String rawColName = elem.getValue();
							String colName = columnElement.getName();
							System.out.println("column: "+colName+" => "+colHeader);
							RectTabColumn rectangularCol = RectTabColumn.createColumn(
									columnList.get(jcol).getValues(), colName+"=("+colHeader+")");
							subRectTable.addColumn(rectangularCol);
						}
					}
				}
			}
			subHtmlTable = subRectTable.createHtmlTable();
		}
		return subHtmlTable;
	}

//	List<String> flattenStrings = column.flatten();
//	System.err.println("FLATTEN "+flattenStrings);

//	private void extractAndWriteSubtables() {
//		List<File> listFilesFromPaths = Util.listFilesFromPaths(tableDir, tableRegex);
//		List<HtmlTable> rawXhtmlTableList = makeRawXhtmlTables(listFilesFromPaths);
//		int serial = 1;
//		for (HtmlTable rawXhtmlTable : rawXhtmlTableList) {
//			createAndWriteSubtables(tableDir, rawXhtmlTable, serial++);
//		}
//	}

//	private void createAndWriteSubtables(File tableDir, HtmlTable rawXhtmlTable, int serial) {
//		IntArray columnNumbers = extractColumnsFromTable(rawXhtmlTable);
//		if (columnNumbers.size() > 0) {
//			HtmlTable subTable = createSubTable(rawXhtmlTable, columnNumbers);
//			HtmlHtml html = new HtmlHtml();
//			html.getOrCreateBody().appendChild(subTable);
//			addTableStyle(html);
//			File xmlFile = new File(tableDir, templateName + (serial + 1) + ".html");
//			XMLUtil.writeQuietly(html, xmlFile, 1);
//		}
//	}

	private void addTableStyle(HtmlHtml html) {
		HtmlStyle style = html.getOrCreateHead().getOrCreateHtmlStyle();
		String css = ""+
		"		  table {background : red;\n" + 
		"			  margin : 2px;}\n" + 
		"			  \n" + 
		"			  tr {margin : 2px; background : #aaffff;}";
		style.addCss(css);
	}

	/** MESSY */
//	private HtmlTable createSubTable(HtmlTable htmlTable, IntArray columnNumbers) {
//		RectangularTable rectangularTable = RectangularTable.createRectangularTable(htmlTable);
//		int rows = rectangularTable.getRows().size();
//		List<String> colnames = rectangularTable.getHeader();
//		HtmlTable subTable = new HtmlTable();
//		createAndPopulateThead(columnNumbers, colnames, subTable);
//
//		HtmlTbody tbody = subTable.getOrCreateTbody();
//		for (int i = 0; i < rows; i++) {
//			tbody.addRow(new HtmlTr());
//		}
//		List<HtmlTr> rowList = tbody.getRowList();
//		
//		convertColumnsToRows(columnNumbers, rectangularTable, rowList);
//		return subTable;
//	}

//	private void createAndPopulateThead(IntArray columnNumbers, List<String> colnames, HtmlTable subTable) {
//		HtmlThead thead = subTable.getOrCreateThead();
//		HtmlTr tr = thead.getOrCreateTr();
//		for (int jcol = 0; jcol < columnNumbers.size(); jcol++) {
//			String colHeader = colnames.get(columnNumbers.elementAt(jcol));
//			tr.appendChild(new HtmlTh(colHeader));
//		}
//	}

//	private void convertColumnsToRows(IntArray columnNumbers, RectangularTable rectangularTable,  
//			List<HtmlTr> rowList) {
//		int rows = rectangularTable.getRows().size();
//
//		for (int ii = 0; ii < columnNumbers.size(); ii++) {
//			int jcol = columnNumbers.elementAt(ii);
//			RectTabColumn column = rectangularTable.getColumn(jcol);
//			for (int irow = 0; irow < rows; irow++) {
//				String value  = column.get(irow);
//				rowList.get(irow).appendChild(new HtmlTd(value == null ? "" : value.trim()));
//			}
//		}
//	}

//	private IntArray extractColumnsFromTable(HtmlTable htmlTable) {
////		LOG.debug(">>"+htmlTable.getAttributeValue("id")); ID is not in table, but table-wrap
//		RectangularTable rectangularTable = RectangularTable.createRectangularTable(htmlTable);
//		List<String> columns = rectangularTable.getHeader();
//		IntArray columnNumbers = new IntArray();
//		if (columns == null) {
//			LOG.debug("no columns");
//		} else {
//			for (int icol = 0; icol < columns.size(); icol++) {
//				String column = Util.replaceUnicodeWhitespaces(columns.get(icol), " ").trim();
//				if (column.length() != 0) {
//					int colFound = -1;
//					for (List<Pattern> findPatternList : columnFindPatternListMap.values()) {
//						for (Pattern findPattern : findPatternList) {
//							Matcher matcher = findPattern.matcher(column);
//							if (matcher.find()) {
//								colFound = icol;
//								break;
//							}
//						}
//					}
//					if (colFound >= 0) {
//						columnNumbers.addElement(colFound);
//					}
//				}
//			}
//		}
//		return columnNumbers;
//	}

	private List<HtmlTable> makeRawXhtmlTables(List<File> tableFileList) {
		List<HtmlTable> htmlTableList = new ArrayList<>();
		for (File tableFile : tableFileList) {
			if (tableFile.toString().endsWith(".xml")) {
				List<HtmlTable> tableList = HtmlTable.extractTables(tableFile);
				htmlTableList.addAll(tableList);
			}
		}
		return htmlTableList;
	}


}

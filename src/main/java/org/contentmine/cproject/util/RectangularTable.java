package org.contentmine.cproject.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTr;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

public class RectangularTable {

	private static final Logger LOG = Logger.getLogger(RectangularTable.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String NEW_LINE_SEPARATOR = "\n";
	
	private List<String> header;
	private List<List<String>> rows;
	private List<String> currentRow;
	private int truncateChars = -1;
	private Map<Integer, RectTabColumn> columnByIndexMap;
	private HashMap<Integer, Map<String, Integer>> rowIndexByCellValueMapByColumnIndexMap;
	private String caption;

	private List<RectTabColumn> completeColumnList;
	
	public RectangularTable() {
	}
	
	public RectangularTable(List<String> header) {
		this.header = header;
	}
	
    public RectangularTable(RectangularTable csvTable) {
    	this.header = new ArrayList<String>(csvTable.header);
    	this.rows = new ArrayList<List<String>>();
    	for (int i = 0; i < csvTable.rows.size(); i++) {
    		this.rows.add(new ArrayList<String>(csvTable.rows.get(i)));
    	}
//    	this.currentRow = new ArrayList<String>(csvTable.currentRow);
    	this.truncateChars = csvTable.truncateChars;
    	this.clearMaps();
	}

 
    /** use default CSV format.
     * 
     * @param file
     * @param useHeader
     * @return
     * @throws IOException
     */
    public final static RectangularTable readCSVTable(File file, boolean useHeader) throws IOException {
    	return readCSVTable(new FileInputStream(file), useHeader, CSVFormat.DEFAULT);
    }

    /** use default CSV format.
     * 
     * @param file
     * @param useHeader
     * @return
     * @throws IOException
     */
    public final static RectangularTable readCSVTable(InputStream inputStream, boolean useHeader) throws IOException {
    	return readCSVTable(inputStream, useHeader, CSVFormat.DEFAULT);
    }

    public final static RectangularTable readCSVTable(InputStream inputStream, boolean useHeader, CSVFormat csvFormat) throws IOException {
    	if (inputStream != null) {
        	String s = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
        	if (s != null) {
        		StringReader reader = new StringReader(s);
        		return readCSVTable(reader, useHeader, csvFormat);
        	}
    	}
    	return null;
    }
    
	public static RectangularTable readCSVTable(Reader reader, boolean useHeader) throws IOException {
		return readCSVTable(reader, useHeader, CSVFormat.DEFAULT);
	}
	
    /**
     * 
     * @param reader
     * @param useHeader assume first record is header
     * @return
     * @throws IOException
     */
	public static RectangularTable readCSVTable(Reader reader, boolean useHeader, CSVFormat csvFormat) throws IOException {
		RectangularTable table = null;
		int maxRowLength = 0;
		if (reader != null) {
			table = new RectangularTable();
			Iterable<CSVRecord> records = csvFormat.parse(reader);
			table.rows = new ArrayList<List<String>>();
			for (CSVRecord record : records) {
				List<String> values = RectangularTable.getValues(record);
				if (maxRowLength == 0) {
					maxRowLength = values.size();
				} else if (values.size() != maxRowLength) {
					throw new RuntimeException("Bad row (was "+maxRowLength+", found: "+values.size()+"; "+record);
				}
				table.rows.add(values);
			}
			if (useHeader) {
				table.header = table.rows.get(0);
				table.rows.remove(0);
			}
		}
		return table;
	}

	public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = new ArrayList<String>(header);
	}

	public List<List<String>> getRows() {
		return rows;
	}

	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}

	public void writeCsvFile(File file) throws IOException {
		writeCsvFile(file.toString());
	}

	public void writeCsvFile(String filename) throws IOException {
		if (filename != null) {
			try {
			    File file = new File(filename);
				String s = writeCSVString();
			    FileUtils.writeStringToFile(file, s);
		    } catch (IOException ee) {
		    	LOG.debug("Could not write to: "+filename, ee);
		    }
		}
	}

	public String writeCSVString() {
		StringWriter sw = new StringWriter();
		try {
			this.write(sw);
		} catch (IOException e) {
			throw new RuntimeException("cannot write", e);
		}
		return sw.toString();
	}
    

	public CSVPrinter write(Writer writer) throws IOException {
		CSVPrinter csvFilePrinter = null;
		try {
			csvFilePrinter = 
					new CSVPrinter(writer, CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR).withQuote('\"'));
			if (header != null) {
				csvFilePrinter.printRecord(header);
			}
			if (rows != null) {
			    for (int i = 0; i < rows.size(); i++) {
			        List<String> row = rows.get(i);
			        if (row == null) {
			        	row = new ArrayList<String>();
			        }
			        csvFilePrinter.printRecord(row);
			    }
			}
          writer.flush();
          writer.close();
        } catch (Exception e) {
            throw new RuntimeException("failed to write CSV", e);
        } finally {
//            try {
//                writer.flush();
//                writer.close();
//                if (csvFilePrinter != null) csvFilePrinter.close();
//            } catch (IOException e) {
//                throw new RuntimeException("failed to close/flush CSV", e);
//            }
        }
		return csvFilePrinter;
    }


	public CSVPrinter write(Appendable writer) throws IOException {
		CSVPrinter csvFilePrinter = null;
		try {
			csvFilePrinter = 
					new CSVPrinter(writer, CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR));
			if (header != null) {
				csvFilePrinter.printRecord(header);
			}
			if (rows != null) {
			    for (int i = 0; i < rows.size(); i++) {
			        List<String> row = rows.get(i);
			        if (row == null) {
			        	row = new ArrayList<String>();
			        }
			        csvFilePrinter.printRecord(row);
			    }
			}
        } catch (Exception e) {
            throw new RuntimeException("failed to write CSV", e);
        } finally {
            try {
//                writer.flush();
//                writer.close();
                if (csvFilePrinter != null) csvFilePrinter.close();
            } catch (IOException e) {
                throw new RuntimeException("failed to close/flush CSV", e);
            }
        }
		return csvFilePrinter;
    }

	public void createMultisetAndOutputRowsWithCounts(List<String> values, String filename) throws IOException {
		Multiset<String> set = HashMultiset.create();
		set.addAll(values);
		List<Entry<String>> sortedEntryList = CMineUtil.getEntryListSortedByCount(set);
		List<List<String>> rows = new ArrayList<List<String>>();
		for (Entry<String> entry : sortedEntryList) {
			List<String> row = new ArrayList<String>();
			row.add(String.valueOf(entry.getElement()));
			row.add(String.valueOf(entry.getCount()));
			rows.add(row);
		}
		setRows(rows);
		writeCsvFile(filename);
	}

	public void addRow(List<String> row) {
		if (rows == null) {
			rows = new ArrayList<List<String>>();
		}
		rows.add(row);
		clearMaps();
	}

	public void clearRow() {
		currentRow = new ArrayList<String>();
	}

	public void addCurrentRow() {
		addRow(currentRow);
		currentRow = null;
	}

	public void addCell(Object value) {
		if (currentRow != null) {
			String s = value == null ? "" : String.valueOf(value);
			if (truncateChars > 0 && truncateChars < s.length()) {
				s = s.substring(0, truncateChars);
			}
			currentRow.add(s);
		}
	}

	public void setTruncate(int nchars) {
		this.truncateChars  = nchars;
	}
	
	public int getIndexOfColumn(String columnHeading) {
		if (header != null && columnHeading != null) {
			for (int i = 0; i < header.size(); i++) {
				if (columnHeading.equals(header.get(i))) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public int getRowWithCellValue(int jcol, String value) {
		if (value != null && jcol >= 0 && rows != null) {
			for (int irow = 0; irow < rows.size(); irow++) {
				List<String> row = rows.get(irow);
				if (value.equals(row.get(jcol))) {
					return irow;
				}
			}
		}
		return - 1;
	}
	
	/** caches column in index assuming won't be updated. 
	 * Use carefully
	 * 
	 * @param jcol
	 * @return
	 */
	public RectTabColumn getColumn(Integer jcol) {
		getOrCreateColumnByIndexMap();
		RectTabColumn column = columnByIndexMap.get(jcol);
		if (column == null) {
			column = new RectTabColumn();
			for (int irow = 0; irow < rows.size(); irow++) {
				List<String> row = rows.get(irow);
				column.add(jcol >= row.size() ? null : row.get(jcol));
			}
			if (header != null) {
				column.setHeader(header.get(jcol));
			}
			columnByIndexMap.put(jcol, column);
		}
		return column;
	}

	public int getRowIndex(String colHeading, String value) {
		Integer jcol = getIndexOfColumn(colHeading);
		if (jcol >= 0) {
			getOrCreateRowIndexByCellValueByColumnMapByColumnIndexMap();
			Map<String, Integer> rowIndexByCellValueMap = getOrCreateRowIndexByCellValue(jcol);
			Integer irow = rowIndexByCellValueMap.get(value);
			return irow == null ? -1 : irow;
		}
		return -1;
	}

	/** adds csvTable to bottom of this.
	 * requires that both tables have the same non-null headers
	 * 
	 * @param csvTable
	 * @return
	 */
	public boolean addAll(RectangularTable csvTable) {
		boolean added = false;
		if (csvTable == null || csvTable == null) {
		} else if (csvTable.rows == null || csvTable.rows.size() == 0 || this.rows == null || this.rows.size() == 0) {
		} else if (this.header == null || csvTable.header == null) {
		} else if (this.header.toString().equals(csvTable.header.toString())) {
			this.rows.addAll(csvTable.rows);
			added = true;
		}
		this.clearMaps();
		return added;
	}

	public int size() {
		return rows == null ? 0 : rows.size();
	}

	/** adds column with specific header.
	 * 
	 * @param col
	 * @param head
	 * @return
	 */
	@Deprecated // use addColumn with header already set
	
	public boolean addColumn(RectTabColumn col, String head) {
		String colHead = col.getHeader();
		if (colHead == null) {
			col.setHeader(head);
		} else if (colHead.equals(head)) {
			throw new RuntimeException("Changing column heade not allowed");
		}
		return addColumn(col);
	}

	/** add column
	 * must have header set
	 * @param col
	 * @return
	 * throws RuntimeException if header is null
	 */
	public boolean addColumn(RectTabColumn col) {
		if (col == null) {
			throw new RuntimeException("column cannot be null");
		}
		
		if (!isMergeable(col)) {
			LOG.debug("cannot addColumn");
			return false;
		}
		if (col.getHeader() == null) {
			throw new RuntimeException("column must have header");
		}
		ensureHeader();
		// first add?
		if (rows == null || rows.size() == 0) {
			int size = col.getValues().size();
			rows = new ArrayList<>(size);
			for (int i = 0; i < size;  i++) {
				rows.add(new ArrayList<>());
			}
		}

		this.header.add(col.getHeader());
		for (int i = 0; i < rows.size(); i++) {
			String value = col.get(i);
			if (rows.get(i) == null) {
				rows.set(i, new ArrayList<String>());
			}
			this.rows.get(i).add(value);
		}
		return true;
	}
	
	public RectangularTable merge(RectangularTable csvTable1) {
		if (!isMergeable(csvTable1)) return null;
		RectangularTable table = new RectangularTable(this);
		for (int i = 0; i < this.size(); i++) {
			table.rows.get(i).addAll(csvTable1.rows.get(i));
		}
		table.header.addAll(csvTable1.header);
		return table;
	}

	/** merges table into this.
	 * requires a common column which must be sorted in both
	 * 
	 * @param csvTable1
	 * @param col
	 */
	public RectangularTable mergeOnCommonColumn(RectangularTable csvTable1, String col) {
		if (!isMergeable(csvTable1)) return null;
		if (!haveSortedCommonColumn(csvTable1, col)) return null;
		List<String> mergeCols = new ArrayList<String>(csvTable1.header);
		mergeCols.remove(col);
		RectangularTable table = mergeTables(csvTable1, mergeCols);
		return table;
	}
	
	/** merges table into this.
	 * requires a common column which must be sorted in both
	 * 
	 * @param table1
	 * @param colHead
	 */
	public RectangularTable mergeOnUnsortedColumn(RectangularTable table1, String colHead) {
		if (rows == null || table1 == null || table1.rows == null ||
				header == null || table1.header == null || colHead == null) return null;
		List<Integer> index = getMapping0to1(this, table1, colHead);
		RectangularTable table2 = new RectangularTable();
		List<String> header2 = new ArrayList<String>(table1.header);
		int colIdx = header2.indexOf(colHead);
		header2.remove(colIdx);
		table2.setHeader(header2);
		for (Integer i : index) {
			List<String> row1;
			if (i != null) {
				row1 = new ArrayList<String>(table1.rows.get(i));
			} else {
				row1 = createNullList(table1.header.size());
			}
			row1.remove(colIdx);
			table2.addRow(row1);
		}
		RectangularTable table3 = mergeOnCommonColumn(table2, colHead);
		return table3;
	}

	/**
	 * 		
	 *  List<String> col0 = new ArrayList<String>(Arrays.asList(new String[] {"a","b","c","d","e","f","g","h","i","j"}));
		List<String> col1 = new ArrayList<String>(Arrays.asList(new String[] {"b","h","j","c","f","g","a","d","i","e"}));
		List<Integer> mapping0to1 = CSVTable.getMapping0to1(col0, col1);
		// col1[mapping0to1[idx]] == col0[idx]
		Assert.assertEquals("[6, 0, 3, 7, 9, 4, 5, 1, 8, 2]", mapping0to1.toString());
		List<Integer> mapping1to0 = CSVTable.getMapping0to1(col1, col0);
		// col0[mapping1to0[idx]] == col1[idx]
		Assert.assertEquals("[1, 7, 9, 2, 5, 6, 0, 3, 8, 4]", mapping1to0.toString());

	 * 
	 * @param table0
	 * @param table1
	 * @param colHead
	 * @return
	 */
	public static List<Integer> getMapping0to1(RectangularTable table0, RectangularTable table1, String colHead) {
		RectTabColumn col0 = table0.getColumn(colHead);
		RectTabColumn col1 = table1.getColumn(colHead);
		return getMapping0to1(col0, col1);
	}

	public static List<Integer> getMapping0to1(RectTabColumn col0, RectTabColumn col1) {
		Map<String, Integer> index0 = createIndex(col0);
		Map<String, Integer> index1 = createIndex(col1);
		List<Integer> map0to1 = new ArrayList<Integer>();
		for (int i = 0; i < col0.size(); i++) {
			String value0 = col0.get(i);
			Integer i1 = index1.get(value0);
			map0to1.add(i1);
		}
		return map0to1;
	}
	
	public boolean renameHeader(List<String> oldNames, List<String> newNames) {
		boolean renamed = false;
		if (oldNames != null && newNames != null && oldNames.size() == newNames.size()) {
			List<String> newHeader = new ArrayList<String>();
			for (int i = 0; i < header.size(); i++) {
				String header_i = header.get(i);
				int idx = oldNames.indexOf(header_i);
				if (idx != -1) {
					header_i = newNames.get(idx);
					renamed = true;
				}
				newHeader.add(header_i);
			}
			header = newHeader;
		}
		return renamed;
	}
	
	public RectTabColumn getColumn(String col) {
		int idx = this.getIndexOfColumn(col);
		return (idx == -1) ? null : this.getColumn(idx);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(header+"\n");
		if (rows != null) {
			for (List<String> row : rows) {
				sb.append(row+"\n");
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param colHead
	 * @return if no column returns empty List
	 */
	public List<Entry<String>> extractSortedMultisetListForColumn(String colHead) {
		RectTabColumn col = this.getColumn(colHead);
		return col == null ? new ArrayList<Entry<String>>() : col.extractSortedMultisetListForColumn();
	}

	public static List<String> getValues(CSVRecord record) {
		List<String> values = new ArrayList<String>();
		if (values != null) {
			for (int i = 0; i < record.size(); i++) {
				values.add(record.get(i));
			}
		}
		return values;
	}

	public List<Entry<String>> extractDuplicateMultisetList(String colHead) {
		List<Entry<String>> multisetList = extractSortedMultisetListForColumn(colHead);
		List<Entry<String>> duplicateList = new ArrayList<Entry<String>>();
		for (Entry<String> entry : multisetList) {
			if (entry.getCount() > 1) {
				duplicateList.add(entry);
			} else {
				break;
			}
		}
		return duplicateList;
				
	}

	public List<Entry<String>> extractUniqueMultisetList(String colHead) {
		List<Entry<String>> multisetList = extractSortedMultisetListForColumn(colHead);
		List<Entry<String>> uniqueList = new ArrayList<Entry<String>>();
		for (Entry<String> entry : multisetList) {
			if (entry.getCount() == 1) {
				uniqueList.add(entry);
			} else {
//				break;
			}
		}
		return uniqueList;
	}

	public RectangularTable extractTable(List<String> columnHeadings) {
		RectangularTable newTable = new RectangularTable();
		newTable.ensureRows(rows.size());
		List<Integer> columnIndexes = extractColumnIndexes(columnHeadings);
		for (int i = 0; i < columnIndexes.size(); i++) {
			int jcol = columnIndexes.get(i);
			RectTabColumn column = this.getColumn(jcol);
			String head = header.get(jcol);
			newTable.addColumn(column, head);
		}
		return newTable;
	}
	
	/** writes subtable of given headings.
	 * 
	 * @param file
	 * @param columnHeadings
	 * @throws IOException
	 */
	public void writeCsvFile(File file, String ... columnHeadings) throws IOException {
		writeCsvFile(file, Arrays.asList(columnHeadings));
	}

	/** writes subtable of given headings.
	 * 
	 * @param file
	 * @param columnHeadings
	 * @throws IOException
	 */

	public void writeCsvFile(File file, List<String> columnHeadings) throws IOException {
		RectangularTable table = this.extractTable(columnHeadings);
		table.writeCsvFile(file);
	}

	// ===============================
		
	private void clearMaps() {
		rowIndexByCellValueMapByColumnIndexMap = null;
		columnByIndexMap = null;
	}

	private static Map<String, Integer> createIndex(RectTabColumn col) {
		Map<String, Integer> index = new HashMap<String, Integer>();
		for (int i = 0; i < col.size(); i++) {
			index.put(col.get(i), (Integer) i);
		}
		return index;
	}

	private List<String> createNullList(int size) {
		List<String> nullList = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			nullList.add((String) null);
		}
		return nullList;
	}

	private List<Integer> extractColumnIndexes(List<String> columnHeadings) {
		List<Integer> columns = new ArrayList<Integer>();
		for (String columnHeading : columnHeadings) {
			int idx = header.indexOf(columnHeading);
			if (idx == -1) {
				LOG.warn("column heading not found: "+columnHeading);
			} else {
				columns.add(idx);
			}
		}
		return columns;
	}

	private void ensureHeader() {
		if (header == null) {
			header = new ArrayList<String>();
		}
	}

	private void ensureRows(int size) {
		if (rows == null) {
			rows = new ArrayList<List<String>>();
		}
		if (rows.size() == 0) {
			for (int i = 0; i < size; i++) {
				rows.add(new ArrayList<String>());
			}
		}
	}

	private Map<Integer, RectTabColumn> getOrCreateColumnByIndexMap() {
		if (columnByIndexMap == null) {
			columnByIndexMap = new HashMap<Integer, RectTabColumn>();
		}
		return columnByIndexMap;
	}

	private Map<String, Integer> getOrCreateRowIndexByCellValue(Integer jcol) {
		Map<String, Integer> rowIndexByCellValueMap = rowIndexByCellValueMapByColumnIndexMap.get(jcol);
		if (rowIndexByCellValueMap == null) {
			rowIndexByCellValueMap = new HashMap<String, Integer>();
			rowIndexByCellValueMapByColumnIndexMap.put(jcol, rowIndexByCellValueMap);
			RectTabColumn column = getColumn(jcol);
			for (int irow = 0; irow < column.size(); irow++) {
				String valuec = column.get(irow);
				rowIndexByCellValueMap.put(valuec, new Integer(irow));
			}
		}
		return rowIndexByCellValueMap;
	}

	private void getOrCreateRowIndexByCellValueByColumnMapByColumnIndexMap() {
		if (rowIndexByCellValueMapByColumnIndexMap == null) {
			rowIndexByCellValueMapByColumnIndexMap = new HashMap<Integer, Map<String, Integer>>();
		}
	}

	private boolean haveSortedCommonColumn(RectangularTable csvTable1, String col) {
		if (!isMergeable(csvTable1)) return false;
		RectTabColumn col0 = this.getColumn(col);
		RectTabColumn col1 = csvTable1.getColumn(col);
		if (col0 != null && col1 != null) {
			for (int irow = 0; irow < col0.size(); irow++) {
				String value0 = col0.get(irow);
				String value1 = col1.get(irow);
				if (!value0.equals(value1)) {
					LOG.warn("merge values are different: "+irow+"; "+value0+" != "+value1);
					return false;
				}
			}
		}
		return true;
	}

	private boolean isMergeable(RectTabColumn col) {
		if (col == null) return false;
		if (this.size() > 0 && this.size() != col.size()) {
			LOG.warn("Cannot add column of size ("+col.size()+") to table ("+this.size()+")");
			return false;
		}
		return true;
	}

	private boolean isMergeable(RectangularTable csvTable1) {
		boolean merge = false;
		if (csvTable1 == null) {
			LOG.debug("null table");
		} else if (csvTable1.rows == null) {
			LOG.debug("null rows");
		} else if (this.rows == null || this.rows.size() > 0 && this.rows.size() != csvTable1.rows.size()) {
			LOG.debug("unequal rows");
		} else {
			merge = true;
		}
		return merge;
	}

	private RectangularTable mergeTables(RectangularTable csvTable, List<String> mergeCols) {
		RectangularTable table = new RectangularTable(this);
		for (String col : mergeCols) {
			RectTabColumn column = csvTable.getColumn(col);
			table.addColumn(column, col);
		}
		return table;
	}

	public List<String> getValuesNotIn(RectangularTable notTable, String colHead) {
		RectTabColumn notList = notTable.getColumn(colHead);
		Set<String> notSet = new HashSet<String>(notList.getValues());
		RectTabColumn thisColumn = this.getColumn(colHead);
		List<String> newColumn = new ArrayList<String>();
		for (String value : thisColumn) {
			if (!notSet.contains(value)) {
				newColumn.add(value);
			}
		}
		return newColumn;
	}

	public void writeColumn(File outputCsvFile, String columnHeading) throws IOException {
		RectTabColumn lines = getColumn(columnHeading);
		FileUtils.writeLines(outputCsvFile, lines.getValues(), "\n");
	}
	
	public HtmlTable createHtmlTable() {
		HtmlTable htmlTable = new HtmlTable();
		List<String> header = getHeader();
		if (header !=  null) {
			HtmlThead thead = htmlTable.getOrCreateThead().addHeader(header);
		}
		HtmlTbody body = htmlTable.getOrCreateTbody();
		List<List<String>> rows = this.getRows();
		if (rows != null) {
			
			for (List<String> row : rows) {
				HtmlTr tr = new HtmlTr(row);
				body.appendChild(tr);
			}
		}
		return htmlTable;
			
			
	}
		
	public static RectangularTable createRectangularTable(HtmlTable table) {
		HtmlTr tr = table.getHeaderRow();
		List<String> header = tr == null ? null : tr.getThCellValues();
		RectangularTable rectangularTable = new RectangularTable(header);
		List<HtmlTr> trTdList = table.getTrTdRows();
		for (HtmlTr trTd : trTdList) {
			List<String> strings = trTd.getTdCellValues();
			rectangularTable.addRow(strings);
		}
		return rectangularTable;
	}

	public static RectangularTable createRectangularTable(HtmlTable table, List<String> headers) {
		RectangularTable rectangularTable = new RectangularTable(headers);
		List<HtmlTr> trTdList = table.getTrTdRows();
		for (HtmlTr trTd : trTdList) {
			List<String> strings = trTd.getTdCellValues();
			rectangularTable.addRow(strings);
		}
		return rectangularTable;
	}

	public List<RectTabColumn> getColumnList(String[] colNames) {
		List<RectTabColumn> columnList = new ArrayList<>();
		for (String colName : colNames) {
			RectTabColumn column = this.getColumn(colName);
			columnList.add(column);
		}
		return columnList;
	}
	
	public Integer getColumnCount() {
		return header == null ? null : header.size();
	}
	
	public List<RectTabColumn> getCompleteColumnList() {
		completeColumnList = null;
		if (completeColumnList == null && getColumnCount() != null) {
			completeColumnList = new ArrayList<>();
			for (int jcol = 0; jcol < getColumnCount(); jcol++) {
				RectTabColumn col = this.getColumn(jcol);
				completeColumnList.add(col);
			}
		}
		return completeColumnList;
	}

	public List<String> getColumnValues(String doi) {
		RectTabColumn column = this.getColumn(doi);
		return column == null ? null : column.getValues();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	


}

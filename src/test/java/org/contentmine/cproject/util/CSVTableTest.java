package org.contentmine.cproject.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multiset.Entry;

/** tests CSVTable
 * 
 * @author pm286
 *
 */
public class CSVTableTest {

	private static final Logger LOG = Logger.getLogger(CSVTableTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private final static List<String> HEADERS1 = new ArrayList<String>(Arrays.asList(new String[] {"A", "B", "C"})); 
	private final static List<String> HEADERS2 = new ArrayList<String>(Arrays.asList(new String[] {"A", "D", "E"})); 
	private final static List<String> HEADERS3 = new ArrayList<String>(Arrays.asList(new String[] {"D", "E"})); 

	@Test
	public void testCSVTableColumn() {
		RectangularTable csvTable = new RectangularTable();
		csvTable.setHeader(HEADERS1);
		Assert.assertEquals(0, csvTable.getIndexOfColumn("A"));
		Assert.assertEquals(2, csvTable.getIndexOfColumn("C"));
		Assert.assertEquals(-1, csvTable.getIndexOfColumn("D"));
	}
	

	@Test
	public void testCSVTable() throws IOException {
		RectangularTable csvTable = new RectangularTable();
		csvTable.setHeader(HEADERS1);
		Assert.assertEquals("A,B,C\n", csvTable.writeCSVString());
	}
	
	@Test
	public void testWriteRows() throws IOException {
		RectangularTable csvTable = new RectangularTable();
		csvTable.setHeader(HEADERS1);
		csvTable.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","b0","c0"})));
		csvTable.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","b1","c1"})));
		csvTable.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","b2","c2"})));
		Assert.assertEquals("A,B,C\n"
				+ "a0,b0,c0\n"
				+ "a1,b1,c1\n"
				+ "a2,b2,c2\n", csvTable.writeCSVString());
	}

	@Test
	public void testGetColumn() throws IOException {
		RectangularTable csvTable = new RectangularTable();
		csvTable.setHeader(HEADERS1);
		csvTable.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","b0","c0"})));
		csvTable.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","b1","c1"})));
		csvTable.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","b2","c2"})));
		int jcol = csvTable.getIndexOfColumn("B");
		List<String> col = csvTable.getColumn(jcol);
		Assert.assertEquals("[b0, b1, b2]", col.toString());
	}

	@Test
	public void testGetRowIndex() throws IOException {
		RectangularTable csvTable = new RectangularTable();
		csvTable.setHeader(HEADERS1);
		csvTable.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","b0","c0"})));
		csvTable.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","b1","c1"})));
		csvTable.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","b2","c2"})));
		Assert.assertEquals(1, csvTable.getRowIndex("B", "b1"));
		Assert.assertEquals(2, csvTable.getRowIndex("C", "c2"));
		Assert.assertEquals(-1, csvTable.getRowIndex("C", "c3"));
		Assert.assertEquals(-1, csvTable.getRowIndex("D", "c2"));
		csvTable.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a3","b3","c3"})));
		Assert.assertEquals(3, csvTable.getRowIndex("C", "c3"));
	}
	
	@Test
	public void testMerge() throws IOException {
		RectangularTable csvTable0 = new RectangularTable();
		csvTable0.setHeader(HEADERS1);
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","b0","c0"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","b1","c1"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","b2","c2"})));
		RectangularTable csvTable1 = new RectangularTable();
		csvTable1.setHeader(HEADERS1);
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a3","b3","c3"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a4","b4","c4"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a5","b5","c5"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a6","b6","c6"})));
		csvTable0.addAll(csvTable1);
		Assert.assertEquals("A,B,C\n"
				+ "a0,b0,c0\n"
				+ "a1,b1,c1\n"
				+ "a2,b2,c2\n"
				+ "a3,b3,c3\n"
				+ "a4,b4,c4\n"
				+ "a5,b5,c5\n"
				+ "a6,b6,c6\n"
				+ "", csvTable0.writeCSVString());

		Assert.assertEquals(7, csvTable0.size());
		Assert.assertEquals(4, csvTable1.size());
		Assert.assertEquals(1, csvTable0.getRowIndex("B", "b1"));
		Assert.assertEquals(2, csvTable0.getRowIndex("C", "c2"));
		Assert.assertEquals(3, csvTable0.getRowIndex("C", "c3"));
		Assert.assertEquals(6, csvTable0.getRowIndex("C", "c6"));
		Assert.assertEquals(5, csvTable0.getRowIndex("C", "c5"));
	}

	@Test
	public void testMergeCSV() throws IOException {
		RectangularTable csvTable0 = new RectangularTable();
		csvTable0.setHeader(HEADERS1);
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","b0","c0"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","b1","c1"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","b2","c2"})));
		RectangularTable csvTable1 = new RectangularTable();
		csvTable1.setHeader(HEADERS3);
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"d0","e0"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"d1","e1"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"d2","e2"})));
		RectangularTable newTable = csvTable0.merge(csvTable1);
		Assert.assertEquals(""+
				"A,B,C,D,E\n"+
				"a0,b0,c0,d0,e0\n"+
				"a1,b1,c1,d1,e1\n"+
				"a2,b2,c2,d2,e2\n"+
				"", newTable.writeCSVString());


	}
	
	@Test
	public void testAddColumn() throws IOException {
		RectangularTable csvTable0 = new RectangularTable();
		csvTable0.setHeader(HEADERS1);
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","b0","c0"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","b1","c1"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","b2","c2"})));
		List<String> col = new ArrayList<String>(Arrays.asList(new String[]{"d0", "d1", "d2"}));
		boolean ok = csvTable0.addColumn(col, "D");
		Assert.assertTrue(ok);
		Assert.assertEquals(""+
				"A,B,C,D\n"+
				"a0,b0,c0,d0\n"+
				"a1,b1,c1,d1\n"+
				"a2,b2,c2,d2\n"+
				"", csvTable0.writeCSVString());

	}
	
	@Test
	public void testMergeCSVOnCommonColumn() throws IOException {
		RectangularTable csvTable0 = new RectangularTable();
		csvTable0.setHeader(HEADERS1);
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","b0","c0"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","b1","c1"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","b2","c2"})));
		RectangularTable csvTable1 = new RectangularTable();
		csvTable1.setHeader(HEADERS2);
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","d0","e0"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","d1","e1"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","d2","e2"})));
		RectangularTable newTable = csvTable0.mergeOnCommonColumn(csvTable1, "A");
		Assert.assertEquals(""+
				"A,B,C,D,E\n"+
				"a0,b0,c0,d0,e0\n"+
				"a1,b1,c1,d1,e1\n"+
				"a2,b2,c2,d2,e2\n"+
				"", newTable.writeCSVString());
	}
	
	@Test
	public void testIndex() {
		List<String> col0 = new ArrayList<String>(Arrays.asList(new String[] {"a","b","c","d","e","f","g","h","i","j"}));
		List<String> col1 = new ArrayList<String>(Arrays.asList(new String[] {"b","h","j","c","f","g","a","d","i","e"}));
		List<Integer> mapping0to1 = RectangularTable.getMapping0to1(col0, col1);
		// col1[mapping0to1[idx]] == col0[idx]
		Assert.assertEquals("[6, 0, 3, 7, 9, 4, 5, 1, 8, 2]", mapping0to1.toString());
		List<Integer> mapping1to0 = RectangularTable.getMapping0to1(col1, col0);
		// col0[mapping1to0[idx]] == col1[idx]
		Assert.assertEquals("[1, 7, 9, 2, 5, 6, 0, 3, 8, 4]", mapping1to0.toString());
	}
	
	@Test
	public void testIndexWithMissing() {
		List<String> col0 = new ArrayList<String>(Arrays.asList(new String[] {"a","b","c","d","e","f","g","h","i","j"}));
		List<String> col2 = new ArrayList<String>(Arrays.asList(new String[] {"b","h","c","y","f","g","x","a","e"}));
		List<Integer> mapping0to2 = RectangularTable.getMapping0to1(col0, col2);
		// col2[mapping0to2[idx]] == col0[idx]
		Assert.assertEquals("[7, 0, 2, null, 8, 4, 5, 1, null, null]", mapping0to2.toString());
		List<Integer> mapping2to0 = RectangularTable.getMapping0to1(col2, col0);
		// col0[mapping2to0[idx]] == col2[idx]
		Assert.assertEquals("[1, 7, 2, null, 5, 6, null, 0, 4]", mapping2to0.toString());
	}

	@Test
	public void testMergeWithUnsortedColumn() throws IOException {
		RectangularTable csvTable0 = new RectangularTable();
		csvTable0.setHeader(HEADERS1);
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","b0","c0"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","b1","c1"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","b2","c2"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a3","b3","c3"})));
		RectangularTable csvTable1 = new RectangularTable();
		csvTable1.setHeader(HEADERS2);
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","d2","e2"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","d0","e0"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a3","d3","e3"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","d1","e1"})));
		RectangularTable newTable = csvTable0.mergeOnUnsortedColumn(csvTable1, "A");
		Assert.assertEquals(""+
				"A,B,C,D,E\n"+
				"a0,b0,c0,d0,e0\n"+
				"a1,b1,c1,d1,e1\n"+
				"a2,b2,c2,d2,e2\n"+
				"a3,b3,c3,d3,e3\n"+
				"", newTable.writeCSVString());
	}
	
	@Test
	public void testMergeWithUnsortedColumnWithMissing() throws IOException {
		RectangularTable csvTable0 = new RectangularTable();
		csvTable0.setHeader(HEADERS1);
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","b0","c0"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","b1","c1"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a8","b8","c8"})));
		csvTable0.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a3","b3","c3"})));
		RectangularTable csvTable1 = new RectangularTable();
		csvTable1.setHeader(HEADERS2);
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","d2","e2"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","d0","e0"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a3","d3","e3"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a5","d5","e5"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","d1","e1"})));
		RectangularTable newTable;
		newTable = csvTable0.mergeOnUnsortedColumn(csvTable1, "A");
		Assert.assertEquals(""+
				"A,B,C,D,E\n"+
				"a0,b0,c0,d0,e0\n"+
				"a1,b1,c1,d1,e1\n"+
				"a8,b8,c8,,\n"+
				"a3,b3,c3,d3,e3\n"+
				"", newTable.writeCSVString());
		newTable = csvTable1.mergeOnUnsortedColumn(csvTable0, "A");
		Assert.assertEquals(""+
				"A,D,E,B,C\n"+
				"a2,d2,e2,,\n"+
				"a0,d0,e0,b0,c0\n"+
				"a3,d3,e3,b3,c3\n"+
				"a5,d5,e5,,\n"+
				"a1,d1,e1,b1,c1\n"+
				"", newTable.writeCSVString());
		
	}
	
	@Test
	@Ignore //fails on test ordering
	public void testColumnForMultiset() {
		RectangularTable csvTable1 = new RectangularTable();
		csvTable1.setHeader(HEADERS2);
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","d2","e2"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a0","d0","e0"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a3","d3","e3"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a5","d5",""})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a2","d1","e1"})));
		csvTable1.addRow(new ArrayList<String>(Arrays.asList(new String[]{"a1","d8",""})));
		Iterable<Entry<String>> entriesA = csvTable1.extractSortedMultisetList("A");
		Assert.assertEquals("[a2 x 2, a1, a3, a5, a0]", entriesA.toString());
		Iterable<Entry<String>> entriesD = csvTable1.extractSortedMultisetList("D");
		Assert.assertEquals("[d5, d8, d0, d1, d2, d3]", entriesD.toString());
		Iterable<Entry<String>> entriesE = csvTable1.extractSortedMultisetList("E");
		Assert.assertEquals("[ x 2, e0, e1, e2, e3]", entriesE.toString());
	}

	@Test
	public void testReadCSV() throws IOException {
		String s = "A,D,E,B,C\n"+
				"a2,d2,e2,,\n"+
				"a0,d0,e0,b0,c0\n"+
				"a3,d3,e3,b3,c3\n"+
				"a5,d5,e5,,\n"+
				"a1,d1,e1,b1,c1\n";
		boolean useHeader = true;
		RectangularTable table = RectangularTable.readCSVTable(new StringReader(s), useHeader);
		Assert.assertEquals(5, table.size());
		Assert.assertEquals("[A, D, E, B, C]", table.getHeader().toString());
		useHeader = false;
		table = RectangularTable.readCSVTable(new StringReader(s), useHeader);
		Assert.assertEquals(6, table.size());
		Assert.assertNull(table.getHeader());

	}
	
	@Test
	public void testRenameHeader() throws Exception {
		String s = "A,D,E,B,C\n"+
				"a2,d2,e2,,\n"+
				"a0,d0,e0,b0,c0\n"+
				"a3,d3,e3,b3,c3\n"+
				"a5,d5,e5,,\n"+
				"a1,d1,e1,b1,c1\n";
		boolean useHeader = true;
		RectangularTable table = RectangularTable.readCSVTable(new StringReader(s), useHeader);
		List<String> oldNames = new ArrayList<String>(Arrays.asList(new String[]{"C", "A", "E"}));
		List<String> newNames = new ArrayList<String>(Arrays.asList(new String[]{"c1", "a1", "e1"}));
		boolean renamed = table.renameHeader(oldNames, newNames);
		Assert.assertTrue("renamed", renamed);
//		LOG.debug(table);
	}
	
	@Test
	public void testExtractTable() throws Exception {
		String s = "A,D,E,B,C\n"+
				"a2,d2,e2,,\n"+
				"a0,d0,e0,b0,c0\n"+
				"a3,d3,e3,b3,c3\n"+
				"a5,d5,e5,,\n"+
				"a1,d1,e1,b1,c1\n";
		boolean useHeader = true;
		RectangularTable table = RectangularTable.readCSVTable(new StringReader(s), useHeader);
		RectangularTable newTable = table.extractTable(new ArrayList<String>(Arrays.asList(new String[]{"D", "C", "A"})));
		Assert.assertEquals("extracted", ""
				+ "[D, C, A]\n"
				+ "[d2, , a2]\n"
				+ "[d0, c0, a0]\n"
				+ "[d3, c3, a3]\n"
				+ "[d5, , a5]\n"
				+ "[d1, c1, a1]\n",
		  newTable.toString());

	}
	

//	/** guava tables
//	 * may be useful later
//	 */
//	@Test
//	public void testCreateHashBasedTable() {
//		Table<String,String,Integer> table = HashBasedTable.create();
//		table.put("r1","c1",20);
//		System.out.println(table.get("r1","c1"));	
//	}


}

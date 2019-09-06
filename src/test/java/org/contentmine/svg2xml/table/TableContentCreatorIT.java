package org.contentmine.svg2xml.table;

import java.io.File;

import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Test;

public class TableContentCreatorIT {

	@Test
		public void testSimple1() {
			File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, "grid/simple1.svg");
			File outDir = new File("target/table/grid/");
			// refactor this stack
			TableContentCreator tableContentCreator = new TableContentCreator(); 
			tableContentCreator.markupAndOutputTable(inputFile, outDir);
	//		Assert.assertEquals("cols",  5, tableContentCreator.getColumnCount());
	//		Assert.assertEquals("rows",  7, tableContentCreator.getRowCount());
		}

	@Test
	public void testSimple2() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, "grid/simple2.svg");
		File outDir = new File("target/table/grid/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
	}

	// box	10.1016_S0140-6736(16)31461-1/
	// not yet solved
	@Test
	public void testBox1() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, "box/table1.svg");
		File outDir = new File("target/table/box/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
	}

	@Test
	public void testTable1() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, "grid/table1.svg");
		File outDir = new File("target/table/grid/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
	}

	@Test
		public void testSimple3() {
			File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, "grid/simple3.svg");
			File outDir = new File("target/table/grid/");
			TableContentCreator tableContentCreator = new TableContentCreator(); 
			tableContentCreator.markupAndOutputTable(inputFile, outDir);
	//		int rows = tableContentCreator.getRowCount();
	//		Assert.assertEquals("rows",  8, rows);
	//		int cols = tableContentCreator.getColumnCount();
	//		Assert.assertEquals("cols",  5, cols);
		}

	@Test
	public void testSimple4() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, "grid/simple4.svg");
		File outDir = new File("target/table/grid/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
	}

	@Test
	// 	10.1016_j.pain.2014.09.020
	public void testGlueTables() {
		File inputFile1 = new File(SVG2XMLFixtures.TABLE_DIR, "glue/table3.svg");
		File inputFile1cont = new File(SVG2XMLFixtures.TABLE_DIR, "glue/table3cont.svg");
		File inputFile1annot = new File(SVG2XMLFixtures.TABLE_DIR, "glue/table3.svg");
		File inputFile1annotcont = new File(SVG2XMLFixtures.TABLE_DIR, "glue/table3cont.svg");
		File outDir = new File("target/table/glue/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile1, outDir);
	}

}

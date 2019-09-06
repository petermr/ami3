package org.contentmine.svg2xml.table;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/** 
 * test for TableBodyChunk
 * @author pm286
 *
 */
public class TableBodyTest {

	private final static Logger LOG = Logger.getLogger(TableBodyTest.class);
	
	private final static String TABLE = "target/table";

	@Test
	public void testTDBlockValue() {
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		String value = genericChunk.getValue();
		Assert.assertEquals("value", "IN6127445.72.92IN56(WT)23065.13.24IN1604729.53.28IN6213654.33.42IN705254.53.86IN575347.04.25IN6911945.04.38IN6320941.24.55IN646348.44.60IN6815354.15.14IN6618982.25.87IN6721257.66.71IN653383.86.95IN714968.87.67", value);
	}
	
	@Test
	public void testCreateRows() {
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		TableBody tableBody = new TableBody(genericChunk.getElementList());
		List<TableRow> tableRowList = tableBody.createUnstructuredRows();
		Assert.assertEquals("rows", 14, tableRowList.size());
		String[] values = {
			"IN6127445.72.92",
			"IN56(WT)23065.13.24",
			"IN1604729.53.28",
			"IN6213654.33.42",
			"IN705254.53.86",
			"IN575347.04.25",
			"IN6911945.04.38",
			"IN6320941.24.55",
			"IN646348.44.60",
			"IN6815354.15.14",
			"IN6618982.25.87",
			"IN6721257.66.71",
			"IN653383.86.95",
			"IN714968.87.67",
		};
		for (int i = 0; i < tableRowList.size(); i++) {
			TableRow row = tableRowList.get(i);
			Assert.assertEquals("val"+1, values[i], row.getValue());
		}
	}
	
	@Test
	public void testCreateStructuredRows() {
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		TableBody tableBody = new TableBody(genericChunk.getElementList());
		List<TableRow> rowList = tableBody.createStructuredRows();
		Assert.assertEquals("rows", 14, rowList.size());
		String[] rows = {
			"{{IN61}{274}{45.7}{2.92}}",
			"{{IN56(WT)}{230}{65.1}{3.24}}",
			"{{IN160}{47}{29.5}{3.28}}",
			"{{IN62}{136}{54.3}{3.42}}",
			"{{IN70}{52}{54.5}{3.86}}",
			"{{IN57}{53}{47.0}{4.25}}",
			"{{IN69}{119}{45.0}{4.38}}",
			"{{IN63}{209}{41.2}{4.55}}",
			"{{IN64}{63}{48.4}{4.60}}",
			"{{IN68}{153}{54.1}{5.14}}",
			"{{IN66}{189}{82.2}{5.87}}",
			"{{IN67}{212}{57.6}{6.71}}",
			"{{IN65}{33}{83.8}{6.95}}",
			"{{IN71}{49}{68.8}{7.67}}",
		};
		for (int i = 0; i < rowList.size(); i++) {
			TableRow row = rowList.get(i);
			Assert.assertEquals("row"+i, rows[i], row.toString());
		}
	}
	
	
}

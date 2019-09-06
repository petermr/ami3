package org.contentmine.cproject.testutil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlTr;
import org.junit.Assert;

import nu.xom.Element;


/** utilities to analyze dataTables created by AMI commands.
 * 
 * @author pm286
 *
 */
public class DataTablesToolAnalyzer {

	private HtmlTable table;

	public DataTablesToolAnalyzer(File tableFile) {
		Element tableElement = null;
		try {
			tableElement = XMLUtil.parseQuietlyToDocument(tableFile).getRootElement();
		} catch(Exception e) {
			Assert.fail("Cannot parse "+tableFile+"; "+e);
		}
		table = (HtmlTable) HtmlElement.create(XMLUtil.getSingleElement(tableElement, ".//*[local-name()='table']"));
	}

	public DataTablesToolAnalyzer assertRowCount(int nRows) {
		Assert.assertEquals("rowCount", nRows, table.getRows().size());
		return this;
	}
	
	public DataTablesToolAnalyzer assertColumnCount(int nCols) {
		Assert.assertEquals("columnCount", nCols, table.getSingleLeadingTrThChild().getThChildren().size());
		return this;
	}

	public DataTablesToolAnalyzer assertColumnHeadings(String listAsString) {
		List<HtmlTh> ths = table.getSingleLeadingTrThChild().getThChildren();
		List<String> thStrings = new ArrayList<String>();
		for (HtmlTh th : ths) {
			thStrings.add(th.getValue().toString());
		}
		Assert.assertEquals("columns", listAsString, thStrings.toString());
		return this;
	}

	public void assertCellValue(int iRow, int jCol, String value) {
		List<HtmlTr> rows = table.getRows();
		Assert.assertTrue("0 <= row ("+iRow+") < "+rows.size(), 0 <= iRow && iRow < rows.size());
		HtmlTr row = rows.get(iRow);
		Assert.assertTrue("0 <= col ("+jCol+") < "+row.getTdChildren().size(), 0 <= jCol && jCol < row.getTdChildren().size());
		Assert.assertEquals("value("+iRow+", "+jCol+")", value, row.getTdChildren().get(jCol).getValue().trim());
		
	}
	
	

}

package org.contentmine.svg2xml.table;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore // old style
public class TableMarkupTest {

	public static final Logger LOG = Logger.getLogger(TableMarkupTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testHeaderRowList() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, "LWW61463_TABLE1..image.g.2.9.svg");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		HtmlHtml html = tableContentCreator.createHTMLFromSVG(inputFile);
		TableHeaderSection headerSection = tableContentCreator.getOrCreateTableHeaderSection(); 
		headerSection.createHeaderRowsAndColumnGroups();
		
		List<HeaderRow> headerRowList = headerSection.getOrCreateHeaderRowList();
		Assert.assertEquals(1,  headerRowList.size());
		HeaderRow headerRow = headerRowList.get(0);
		
		List<ColumnGroup> columnGroupList = headerRow.getOrCreateColumnGroupList();
		Assert.assertEquals(2,  columnGroupList.size());
		ColumnGroup columnGroup = columnGroupList.get(0);
		Assert.assertEquals("colgs", 1, columnGroup.getPhrases().size());
		Assert.assertEquals("colg 0", "5 h Preservation", columnGroup.getPhrases().get(0).getStringValue());
		Assert.assertEquals("colg 0", "line: from((182.0,661.875)) to((293.0,661.875)) v((111.0,0.0))", columnGroup.getRuler().toString());
		columnGroup = columnGroupList.get(1);
		Assert.assertEquals("colg 1", "20 h Preservation", columnGroup.getPhrases().get(0).getStringValue());
		Assert.assertEquals("colg 1", "line: from((314.0,661.875)) to((461.0,661.875)) v((147.0,0.0))", columnGroup.getRuler().toString());
	}
	
	@Test
	public void testColumnManagerList() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, "LWW61463_TABLE1..image.g.2.9.svg");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		HtmlHtml html = tableContentCreator.createHTMLFromSVG(inputFile);
		TableHeaderSection tableHeader = tableContentCreator.getOrCreateTableHeaderSection();
		tableHeader.createHeaderRowsAndColumnGroups();
				
		List<ColumnManager> columnManagerList = tableHeader.getOrCreateColumnManagerList();
		Assert.assertEquals(5,  columnManagerList.size());
		String[] headings = {
				"Warm Ischaemia",
				"Cold (n)",
				"Warm (n)",
				"Cold (n)",
				"Warm (n)",
				};
		IntRange[] ranges = new IntRange[] {
				new IntRange(99,159),
				new IntRange(189,218),
				new IntRange(252,287),
				new IntRange(330,359),
				new IntRange(411,446),
		};
		for (int i = 0; i < columnManagerList.size(); i++) {
			ColumnManager columnManager = columnManagerList.get(i);
			Assert.assertEquals(ranges[i], columnManager.getEnclosingRange());
			Assert.assertEquals(headings[i], columnManager.getPhrase(0).getStringValue());
		}
	}
	
	@Test
	public void testHeaderAreas() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, "LWW61463_TABLE1..image.g.2.9.svg");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		System.out.println();
		tableContentCreator.createHTMLFromSVG(inputFile);
		TableHeaderSection tableHeader = tableContentCreator.getOrCreateTableHeaderSection();
		tableHeader.createHeaderRowsAndColumnGroups();
		SVGElement svgChunk = (SVGElement)tableContentCreator.getSVGChunk().copy();
		svgChunk = tableHeader.createMarkedSections(
				(SVGElement)tableContentCreator.getSVGChunk().copy(),
				new String[] {"blue", "green"}, 
				new double[] {0.2, 0.2}
				);
		SVGSVG.wrapAndWriteAsSVG(svgChunk, new File("target/table/LWW61643/TABLE1..image.g.2.9.svg"));


	}

	@Test
//	@Ignore
	public void testHeaderAreasAA_Kranke() {
		String root = "AA_Kranke";
		String inputFilename = root+".g.2.3.svg";
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, inputFilename);
		File outputFile = new File("target/table/"+root+"/table.g.2.3.svg");
		SVGElement svgChunk = new TableContentCreator().annotateAreas(inputFile);
		SVGSVG.wrapAndWriteAsSVG(svgChunk, outputFile);
	}
	
	@Test
	public void testHeaderAreasACR() {
		String root = "ACR";
		String filename = ".g.7.2.svg";
		annotateAndOutput(root, filename, 4, 4);
	}

	
	@Test
	// FIXME - fails to read header
	public void testHeaderAreasADA1() {
		String root = "ADA1";
		String filename = ".g.4.0.svg";
		annotateAndOutput(root, filename, 10, 10);
	}

	@Test
	// FIXME - fails to read header
	public void testHeaderAreasADA2() {
		String root = "ADA2";
		String filename = ".g.4.3.svg";
		annotateAndOutput(root, filename, 10, 10);
	}

	@Test
	// FIXME - header not in table
	public void testHeaderAreasAHABody() {
		String root = "AHABody";
		String filename = ".g.6.4.svg";
		annotateAndOutput(root, filename, 7, 2);
	}

	@Test
	// FIXME rulers in body
	public void testHeaderAreasAMA_Dobson() {
		String root = "AMA_Dobson";
		String filename = ".g.6.4.svg";
		annotateAndOutput(root, filename, 10, 10);
	}

	@Test
	// FIXME runs over header into body
	public void testHeaderAreasAPA_Nuitjen() {
		String root = "APA_Nuitjen";
		String filename = ".svg";
		annotateAndOutput(root, filename, 15, 3);
	}

	@Test
	// FIXME runs over header into body
	public void testHeaderAreasBMJ() {
		String root = "BMJ312529";
		String filename = ".g.4.1.svg";
		annotateAndOutput(root, filename, 11, 11);
	}

	@Test
	// FIXME different PubStyle
	public void testHeaderAreasLANCET() {
		String root = "LANCET";
		String filename = ".g.6.3.svg";
		annotateAndOutput(root, filename, 5, -1);
	}

	@Test
	// FIXME too much whitespace in phraseList
	public void testHeaderAreasLPW() {
		String root = "LPW_Reisinger";
		String filename = ".g.4.5.svg";
		annotateAndOutput(root, filename, 10, 7);
	}

	@Test
	// OK!
	public void testHeaderAreasLWW61463() {
		String root = "LWW61463";
		String filename = "_TABLE.g.2.9.svg";
		annotateAndOutput(root, filename, 5, 5);
	}
	
	@Test
	// FIXME different PubStyle
	public void testHeaderAreasNEJMOA() {
		String root = "NEJMOA";
		String filename = ".g.4.1.svg";
		annotateAndOutput(root, filename, 6, 1);
	}
	
	@Test
	// FIXME different PubStyle
	public void testHeaderAreasNATURE6() {
		String root = "NATURE";
		String filename = ".g.6.0.svg";
		annotateAndOutput(root, filename, 5, 1);
	}
	
	@Test
	// FIXME runs over header into body
	public void testHeaderAreasPLOS() {
		String root = "PLOS57170";
		String filename = ".g.2.8.svg";
		annotateAndOutput(root, filename, 0, 6);
	}
	
	@Test
	// FIXME different PubStyle
	public void testHeaderAreasSpringer68755() {
		String root = "Springer68755";
		String filename = ".g.7.0.svg";
		annotateAndOutput(root, filename, 8, 8);
	}
	
	@Test
	// FIXME different PubStyle with grids
	public void testHeaderAreasTEX_Ausloos2016() {
		String root = "TEX_Ausloos2016";
		String filename = ".g.5.0.svg";
		annotateAndOutput(root, filename, 10, 10);
	}
	
	@Test
	public void testHeaderAreasWiley44386() {
		String root = "Wiley44386";
		String filename = ".g.4.1.svg";
		annotateAndOutput(root, filename, 6, 6);
	}
	
	
	// ======================================
	
	private void annotateAndOutput(String root, String filename, int nHeaderCols, int nBodyCols) {
		String inputFilename = root+filename;
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, inputFilename);
		File outputFile = new File("target/table/marked/"+root+"/table"+filename);
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		LOG.trace("reading "+inputFilename);
		SVGElement svgChunk = tableContentCreator.annotateAreas(inputFile);
		SVGSVG.wrapAndWriteAsSVG(svgChunk, outputFile);
		TableHeaderSection tableHeader = tableContentCreator.getOrCreateTableHeaderSection();
		int headerCols = tableHeader == null ? -1 :
			tableHeader.getOrCreateColumnManagerList().size();
		Assert.assertEquals(nHeaderCols, headerCols);
		TableBodySection tableBody = tableContentCreator.getOrCreateTableBodySection();
		int bodyCols = tableBody == null ? -1 :
			tableBody.getOrCreateColumnManagerList().size();
		Assert.assertEquals(nBodyCols, bodyCols);
	}

}

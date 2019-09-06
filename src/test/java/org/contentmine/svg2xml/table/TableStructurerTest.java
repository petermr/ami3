package org.contentmine.svg2xml.table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntRangeArray;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalRule;
import org.contentmine.graphics.svg.text.line.TextLine;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

public class TableStructurerTest {
	private static final Logger LOG = Logger.getLogger(TableStructurerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String AA_KRANKE_G_2_3_SVG = "AA_Kranke.g.2.3.svg";
	private static final String ACR_G_7_2_SVG = "ACR.g.7.2.svg";
	private static final String ADA1_G_4_0_SVG = "ADA1.g.4.0.svg";
	private static final String ADA2_G_4_3_SVG = "ADA2.g.4.3.svg";
	private static final String AHA_BODY_G_6_4_SVG = "AHABody.g.6.4.svg";
	private static final String AMA_DOBSON_G_6_4_SVG = "AMA_Dobson.g.6.4.svg";
	private static final String APA_NUITJEN_SVG = "APA_Nuitjen.svg";
	private static final String BLK_JPR_PARTIAL_G_2_8_SVG = "BLK_JPRPartial.g.2.8.svg";
	private static final String BLK_SAM_PARTIAL_G_6_9_SVG = "BLK_SAMPartial.g.6.9.svg";
	private static final String ELS_PETAJA_G_4_3_SVG = "ELSPetaja.g.4.3.svg";
	private static final String ELS2_G_4_17_SVG = "ELS2.g.4.17.svg";
	private static final String LPW_REISINGER_G_4_5_SVG = "LPW_Reisinger.g.4.5.svg";
	private static final String OUP_PARTIAL_G_2_4_SVG = "OUPPartial.g.2.4.svg";
	private static final String PLOS57170_G_2_8_SVG = "PLOS57170.g.2.8.svg";
	private static final String SPRINGER68755_G_7_0_SVG = "Springer68755.g.7.0.svg";
	private static final String TEX_AUSLOOS2016_G_5_0_SVG = "TEX_Ausloos2016.g.5.0.svg";
	private static final String TEX_AUSLOOS2016_G_5_1_SVG = "TEX_Ausloos2016.g.5.1.svg";

	private static final String WILEY44386_G_4_1_SVG = "Wiley44386.g.4.1.svg";
	private static final File TABLE_OUT_DIR = new File("target/table");

	/** OBSOLETE
	@Test
	@Ignore // rotated table
	public void testAATable() throws IOException {
		createDualTables(
			new File(Fixtures.TABLE_DIR, AA_KRANKE_G_2_3_SVG),
			"{0, 1}/{1, 2}/{2, 3}/{4, 20}/{21, 31}",
			1.0,
			"aa"
		);
	}

	@Test
	public void testACRTable() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, ACR_G_7_2_SVG);
		String outRoot = "acr19565481";
		createDualTables(
			inputFile,
			"{0, 2}/{2, 6}/{6, 7}/{7, 30}/{31, 45}",
			2.0,
			outRoot
		);
		int horizontalRulerCount = 4; 
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}

	@Test
	@Ignore // obsolete
	public void testADA1Table() throws IOException {
		String outRoot = "ada1PH1";
		File inputFile = new File(Fixtures.TABLE_DIR, ADA1_G_4_0_SVG);
		createDualTables(
			inputFile,
			"{0, 1}/{1, 4}/{4, 5}/{5, 9}/{10, 13}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 4; 
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}
	
	@Test
	@Ignore // obsolete
	public void testADA2Table() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, ADA2_G_4_3_SVG);
		String outRoot = "ada2PH1";
		createDualTables(
			inputFile,
			"{0, 1}/{1, 3}/{3, 4}/{4, 17}/{18, 20}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 4; // MULTILINE 
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}
	
	@Test
	@Ignore // obsolete
	public void testAHABodyTable() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, AHA_BODY_G_6_4_SVG);
		String outRoot = "ahaPH2";
		createDualTables(
			inputFile,
			"{0, 0}/{0, 2}/{2, 3}/{3, 20}/{21, 24}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 4; // MULTILINE
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);

	}
	
	@Test
	@Ignore // obsolete
	public void testAMADobsonTable() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, AMA_DOBSON_G_6_4_SVG);
		String outRoot = "amaDobson2013_1";
		createDualTables(
			inputFile,
			"{0, 3}/{3, 8}/{8, 9}/{9, 30}/{31, 36}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 22;
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);

	}
	
	@Test
	@Ignore // obsolete
	public void testAPATable() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, APA_NUITJEN_SVG);
		String outRoot = "apa";
		createDualTables(
			inputFile,
			"{0, 2}/{2, 10}/{10, 12}/{12, 19}/{20, 25}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 5; // MULTILINE
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}
	
	@Test
	public void testBLKJPRTable() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, BLK_JPR_PARTIAL_G_2_8_SVG);
		String outRoot = "blkjpr19552758";
		createDualTables(
			inputFile,
			"{0, 0}/{0, 0}/{0, 1}/{1, 16}/{17, 18}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 3;
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}
	
	@Test
	public void testBLKSAMTable() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, BLK_SAM_PARTIAL_G_6_9_SVG);
		String outRoot = "blksam19555371";
		createDualTables(
			inputFile,
			"{0, 0}/{0, 0}/{0, 1}/{1, 11}/{12, 13}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 3; // MULTILINE
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}
	
	@Test
	@Ignore // obsolete
	public void testELSTable() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, ELS_PETAJA_G_4_3_SVG);
		String outRoot = "els_Petaja2009";
		createDualTables(
			inputFile,
			"{0, 3}/{3, 3}/{3, 4}/{4, 10}/{11, 13}",
			2.5,
			outRoot
		);
		int horizontalRulerCount = 3;
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}

	@Test
	@Ignore // obsolete
	public void testELS2Table() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, ELS2_G_4_17_SVG);
		String outRoot = "els_2";
		createDualTables(
			inputFile,
			"{0, 2}/{2, 2}/{2, 3}/{3, 14}/{15, 15}",
			2.5,
			outRoot
		);
		int horizontalRulerCount = 3;
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}

	@Test
	@Ignore // obsolete
	public void testLPWTable() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, LPW_REISINGER_G_4_5_SVG);
		String outRoot = "lww_Reisinger2007";
		createDualTables(
			inputFile,
			"{0, 1}/{1, 4}/{4, 5}/{5, 19}/{20, 21}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 6; // MULTILINE
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}

	@Test
	public void testOUPPartialTable() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, OUP_PARTIAL_G_2_4_SVG);
		String outRoot = "oupPH3";
		createDualTables(
			inputFile,
			"{0, 0}/{0, 2}/{2, 3}/{3, 18}/{19, 19}",
			1.6,
			outRoot
		);
		int horizontalRulerCount = 2;
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}
	
	@Test
	@Ignore // obsolete
	public void testPLOSTable() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, PLOS57170_G_2_8_SVG);
		String outRoot = "plos19557170";
		createDualTables(
			inputFile,
			"{0, 2}/{2, 4}/{4, 5}/{5, 31}/{32, 39}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 5; // OK
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}
	
	@Test
	@Ignore // FIXME
	/ * * this one has a side box
	 * 
	 * @throws IOException
	 * /
	public void testSpringerTable() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, SPRINGER68755_G_7_0_SVG);
		String outRoot = "springer19568755";
		createDualTables(
			inputFile,
			"{0, 1}/{1, 1}/{1, 2}/{2, 22}/{23, 27}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 4;
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}

	/ * * note these two tables have been maually split
	 * 
	 * @throws IOException
	 * /
	@Test
	public void testTEX1Table() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, TEX_AUSLOOS2016_G_5_0_SVG);
		String outRoot = "tex1";
		createDualTables(
			inputFile,
			"{0, 0}/{0, 1}/{1, 2}/{2, 11}/{12, 23}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 13; // OK (one is an underline)
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}

	@Test
	public void testTEX2Table() throws IOException {
		File inputFile = new File(Fixtures.TABLE_DIR, TEX_AUSLOOS2016_G_5_1_SVG);
		String outRoot = "tex2";
		createDualTables(
			inputFile,
			"{0, 0}/{0, 1}/{1, 2}/{2, 9}/{10, 15}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 5; // OK  ones is an underline
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}

	@Test
	public void testWileyTable() throws IOException {
		String outRoot = "wiley19544386";
		File inputFile = new File(Fixtures.TABLE_DIR, WILEY44386_G_4_1_SVG);
		createDualTables(
			inputFile,
			"{0, 1}/{1, 1}/{1, 2}/{2, 22}/{23, 29}",
			1.3,
			outRoot
		);
		int horizontalRulerCount = 3;
		TableStructurer tableStructurer = testHorizontalRulers(inputFile, outRoot, horizontalRulerCount);
	}
*/ // end OBSOLETE
	
	@Test
	public void testAMARules() throws IOException {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, AMA_DOBSON_G_6_4_SVG);
		String outputRoot = "ama";
		int horizontalRulerCount = 26;
		TableStructurer tableStructurer = testHorizontalRules(inputFile, outputRoot, horizontalRulerCount);
		
		
		String rowCodes = tableStructurer.getRowCodes();

		//P0 P1ff P2f P3ff P4 P5f P6 P7f P8 P9f P10f P11f P12f P13 P14f P15f P16f P17f P18 P19f P20 P21f P22 P23f P24 P25f P26f P27 P28f P29 P30f P31 P32f P33 P34f P35f P36 P37f P38 P39f P40 P41f P42 P43f P44f P45 P46f P47 P48f P49 P50f P51 P52f P53f P54f P55f P56f P57
//		Assert.assertEquals(""
//				+ "L0wwbbbbb"            // top line
//				+ " P0ff P1f P2ff"       // caption
//				+ " L1bbbbb"             // caption bottom line
//				+ " P3f"                 // title
//				+ " L2bb L3b"            // vincula
//				+ " P4f"                 // title
//				+ " L4b L5b L6b L7bb"    //vincula
//				+ " P5f P6f P7f P8f L8bbbbb" // column headers
//				+ " P9f P10f P11f P12f L9bbbbb P13f L10bbbbb P14f L11bbbbb P15f L12wbbbbb"  // subchunk title / rows
//				+ " P16f P17f L13bbbbb P18f L14bbbbb P19f L15bbbbb P20f L16wbbbbb"
//				+ " P21f P22f L17bbbbb P23f L18bbbbb P24f L19bbbbb P25f L20wbbbbb"
//				+ " P26f P27f L21bbbbb P28f L22bbbbb P29f L23bbbbb P30f L24bbbbb"
//				+ " P31f P32f P33f P34f P35f L25bbbbb",  // footer
//				rowCodes);
		
		Pattern TOTAL = Pattern.compile(
				"L0w*b*\\s"
				+ "((?:P\\d+f*\\s)+)"
				+ "(?:L\\d+w*b*\\s)"
				+ "((?:"
				+ "((P\\d+f*\\s)+)"
				+ "((L\\d+w*b*\\s)*)"
				+ ")*)"
				+ ".*"
				+ "(L\\d+w*b*\\s)"
				+ "((?:P\\d+f*\\s)+L\\d+w*bbbbb*)"
				);
		Matcher matcher = TOTAL.matcher(rowCodes);
		if (matcher.matches()) {
			for (int i = 0; i <= matcher.groupCount(); i++) {
				LOG.trace(">>"+matcher.group(i));
			}
		}
		
	}

	@Test
	@Ignore
	public void testAA_Kranke() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, AA_KRANKE_G_2_3_SVG);
		int superscript = 99; 
		int subscript = 6;
		testSuscripts(inputFile, superscript, subscript);
		
	}

	@Test
	public void testACRSuscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, ACR_G_7_2_SVG);
		int superscript = 1; // this is false, it is a misaligned multiline
		int subscript = 6;
		testSuscripts(inputFile, superscript, subscript);
		
	}

	@Test
	public void testADA1Suscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, ADA1_G_4_0_SVG);
		int superscript = 1; // all 3 on same TextLine BUT the chi is missing
		int subscript = 0;
		testSuscripts(inputFile, superscript, subscript);
		
	}

	@Test
	public void testADA2Suscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, ADA2_G_4_3_SVG);
		int superscript = 0;
		int subscript = 0;
		testSuscripts(inputFile, superscript, subscript);
		
	}
	
	@Test
	/** this is badly typeset
	 * 
	 */
	public void testAHASuscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, ADA2_G_4_3_SVG);
		int superscript = 0;  // should be one - bad typesetting
		int subscript = 0;
		testSuscripts(inputFile, superscript, subscript);
		
	}

	@Test
	public void testAMADobsonSuscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, AMA_DOBSON_G_6_4_SVG);
		int superscript = 3;
		int subscript = 0;
		testSuscripts(inputFile, superscript, subscript);
		
	}

	@Test
	public void testAPANuitjenSuscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, APA_NUITJEN_SVG);
		int superscript = 5;
		int subscript = 0;
		testSuscripts(inputFile, superscript, subscript);
		
	}

	@Test
	public void testBLK1Suscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, BLK_JPR_PARTIAL_G_2_8_SVG);
		int superscript = 0;
		int subscript = 0;
		testSuscripts(inputFile, superscript, subscript);
		
	}

	@Test
	public void testBLK2Suscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, BLK_SAM_PARTIAL_G_6_9_SVG);
		int superscript = 3;
		int subscript = 0;
		testSuscripts(inputFile, superscript, subscript);
		
	}

	@Test
	public void testLPWSuscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, LPW_REISINGER_G_4_5_SVG);
		int superscript = 1;
		int subscript = 0;
		testSuscripts(inputFile, superscript, subscript);
	}

	@Test
	public void testPLOSSuscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, PLOS57170_G_2_8_SVG);
		int superscript = 9; // some superscripts on same line
		int subscript = 0;
		testSuscripts(inputFile, superscript, subscript);
	}

	@Test
	public void testSpringerSuscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, SPRINGER68755_G_7_0_SVG);
		int superscript = 1;
		int subscript = 0;
		testSuscripts(inputFile, superscript, subscript);
	}

	@Test
	public void testTEX1Suscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, TEX_AUSLOOS2016_G_5_0_SVG);
		int superscript = 2;
		int subscript = 4;
		testSuscripts(inputFile, superscript, subscript);
	}

	@Test
	public void testTEX2Suscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, TEX_AUSLOOS2016_G_5_1_SVG);
		int superscript = 0;
		int subscript = 2;
		testSuscripts(inputFile, superscript, subscript);
	}

	@Test
	public void testWileySuscripts() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, WILEY44386_G_4_1_SVG);
		int superscript = 0;
		int subscript = 0;
		testSuscripts(inputFile, superscript, subscript);
	}

	
	// ----------------------
	@Test
	@Ignore // obsolete
	public void testELSTableRangesArray() throws IOException {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, ELS_PETAJA_G_4_3_SVG);
		String outRoot = "els_Petaja2009.ranges";
//		"{0, 3}/{3, 3}/{3, 4}/{4, 10}/{11, 13}"
		IntRangeArray rangesArray = new IntRangeArray(
				Arrays.asList(
						new IntRange[]{
								new IntRange(0, 3),
								new IntRange(3, 3),
								new IntRange(3, 4),
								new IntRange(4, 10),
								new IntRange(11, 13),
				})
		);

		createDualTables(
			inputFile,
			rangesArray,
			2.5,
			outRoot
		);
		int horizontalRulerCount = 3;
		TableStructurer tableStructurer = testHorizontalRules(inputFile, outRoot, horizontalRulerCount);
	}

	@Test
	@Ignore // throws null RangeArray
	// FIXME
	public void testELSTableAuto() throws IOException {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, ELS_PETAJA_G_4_3_SVG);
		IntRangeArray rangesArray = null;
		String outRoot = "els_Petaja2009.auto";

		createDualTables(
			inputFile,
			rangesArray,
			2.5,
			outRoot
		);
		int horizontalRulerCount = 3;
		TableStructurer tableStructurer = testHorizontalRules(inputFile, outRoot, horizontalRulerCount);
	}


	
	// =======================
	
	private TableStructurer testHorizontalRules(File inputFile, String outputRoot, int horizontalRulerCount) {
		File outputSVGFile = new File("target/table/"+outputRoot+"/horizontal.svg");

		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		TableStructurer tableStructurer = TableStructurer.createTableStructurer(textStructurer);
		List<HorizontalRule> rulerList = tableStructurer.getHorizontalRulerList();
		if (rulerList == null) {
			LOG.warn("Expected RuleList");
//			Assert.assertEquals(horizontalRulerCount);		
		} else {
			Assert.assertEquals(horizontalRulerCount, rulerList.size());		
		}
		tableStructurer.mergeRulersAndTextIntoShapeList();
		List<SVGElement> horizontalElementList = tableStructurer.getHorizontalElementList();
		SVGG g = new SVGG();
		for (AbstractCMElement horizontalElement : horizontalElementList) {
//			Element element = ((HorizontalElement)horizontalElement).copyElement();
			Element element = (Element)horizontalElement.copy();
			g.appendChild(element);
		}
		SVGSVG.wrapAndWriteAsSVG(g, outputSVGFile);
		return tableStructurer;
	}
	

	private void createDualTables(File inputFile, String rangesS, double scale, String outRoot) throws IOException {
		IntRangeArray rangesArray = makeRangeArrayFromString(rangesS);
		createDualTables(inputFile, rangesArray, scale, outRoot);
	}

	private void createDualTables(File inputFile, IntRangeArray rangesArray, double scale, String outRoot)
			throws IOException, FileNotFoundException {
		HtmlHtml html = TableStructurer.createHtmlWithTable(inputFile, rangesArray);
		File outputDir = new File("target/table/"+"new/"+outRoot+"/");
		outputDir.mkdirs();
		XMLUtil.debug(html, new FileOutputStream(new File(outputDir, "table.html")), 1);
		AbstractCMElement svg = createSVGPanel(inputFile, scale);
		HtmlTable twinTable = new HtmlTable();
		HtmlTr tr = new HtmlTr();
		twinTable.appendChild(tr);
		HtmlTd ltd = new HtmlTd();
		tr.appendChild(ltd);
		ltd.appendChild(svg);
		HtmlTd rtd = new HtmlTd();
		tr.appendChild(rtd);
		rtd.appendChild(html);
		XMLUtil.debug(twinTable, new FileOutputStream(new File(outputDir, "totalTable.html")), 1);
	}

	private AbstractCMElement createSVGPanel(File inputFile, double scale) {
		AbstractCMElement svg = SVGElement.readAndCreateSVG(inputFile);		
		SVGG g = new SVGG();
		g.addAttribute(new Attribute("transform", "matrix("+scale+",0.0,0.0,"+scale+",0.0,0.0)"));
		int nchild = svg.getChildCount();
		for (int i = 0; i < nchild; i++) {
			Node child = svg.getChild(0);
			child.detach();
			g.appendChild(child);
		}
		svg.appendChild(g);
		return svg;
	}

	private IntRangeArray makeRangeArrayFromString(String rangesS) {
		String[] rr = rangesS.split("/");
		IntRangeArray rangesArray = new IntRangeArray();
		for (String token : rr) {
			IntRange ir = IntRange.parseCurlyBracketString(token);
			if (ir == null) {
				throw new RuntimeException("probably bad range in: "+rangesS);
			}
			rangesArray.add(ir);
		}
		return rangesArray;
	}
	
	private void debugLines(List<SVGLine> lineList) {
		SVGG g = new SVGG();
		for (SVGElement line : lineList) {
			g.appendChild(line);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/table/dobsonShapes.svg"));
	}

	private void testSuscripts(File inputFile, int superscript, int subscript) {
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		textStructurer.extractAndApplySuscripts();
		List<TextLine> superList = textStructurer.getSuperscriptLineList();
//		for (TextLine supersc : superList) {
//			System.out.println("^^"+supersc);
//		}
//		for (TextLine sub : textStructurer.getSubscriptLineList()) {
//			System.out.println("vv"+sub);
//		}
		Assert.assertEquals("super", superscript, textStructurer.getSuperscriptLineList().size());
		Assert.assertEquals("sub", subscript, textStructurer.getSubscriptLineList().size());
		
		
	}


}

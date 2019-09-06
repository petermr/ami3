package org.contentmine.svg2xml.analyzer;

import java.io.File;

import org.apache.log4j.Logger;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.contentmine.svg2xml.page.PageAnalyzerTest;
import org.junit.Ignore;
import org.junit.Test;

public class SampleTest {

	private final static Logger LOG = Logger.getLogger(SampleTest.class);

	public final static File AJCINDIR = new File(SVG2XMLFixtures.EXT_PDFTOP, "ajc");
	public final static File AJCSVGDIR = new File(SVG2XMLFixtures.TARGET, "ajc");
	public final static File AJCOUTDIR = new File(SVG2XMLFixtures.TARGET, "ajc");
	
	public final static String MATHS = "maths-1471-2148-11-311";
	public final static String MULTIPLE = "multiple-1471-2148-11-312";
	public final static String TREE = "tree-1471-2148-11-313";

	public final static String AJC1 = "CH01182";
	
	public void createSVGFixtures() {
//		PDFAnalyzer.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, MATHS);
//		PDFAnalyzer.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, MULTIPLE);
//		PDFAnalyzer.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, TREE);

//		createSVG(AJCINDIR, AJCOUTDIR, AJC1);    // uncomment for AJC

	}
	
	@Test
	public void testSetup() {
		
	}
	
	@Test
	@Ignore
	public void testAnalyzePDFSInBMCDirectory() {
		PageAnalyzerTest.testDirectory(SVG2XMLFixtures.BMCINDIR, SVG2XMLFixtures.BMCSVGDIR, SVG2XMLFixtures.BMCOUTDIR, false);
	}

	@Test
	@Ignore
	public void testAnalyzePDFSInElifeDirectory() {
		PageAnalyzerTest.testDirectory(SVG2XMLFixtures.ELIFEINDIR, SVG2XMLFixtures.ELIFESVGDIR, SVG2XMLFixtures.ELIFEOUTDIR, false);
	}
	
	@Test
	@Ignore
	public void testAnalyzePDFSInPeerJDirectory() {
		PageAnalyzerTest.testDirectory(SVG2XMLFixtures.PEERJINDIR, SVG2XMLFixtures.PEERJSVGDIR, SVG2XMLFixtures.PEERJOUTDIR, false);
	}
	
	@Test
	@Ignore
	public void testAny() {
		PageAnalyzerTest.testDirectory(SVG2XMLFixtures.ANYINDIR, SVG2XMLFixtures.ANYSVGDIR, SVG2XMLFixtures.ANYOUTDIR);
	}

	//====================================================================

}

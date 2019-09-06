package org.contentmine.svg2xml.page;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.text.line.ScriptLine;
import org.contentmine.graphics.svg.text.structure.AbstractContainer;
import org.contentmine.svg2xml.pdf.PDFAnalyzer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class PageAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(PageAnalyzerTest.class);
	
	public final static String BMC_GEOTABLE = "geotable-1471-2148-11-310";

	public final static String AJC1 = "CH01182";
	
	@Test
	public void test312MULT_8() {
		String[][][] values ={
				org.contentmine.svg2xml.text.TextFixtures.BMC_312MULT_8_0_HTML,
				org.contentmine.svg2xml.text.TextFixtures.BMC_312MULT_8_1_HTML,
				org.contentmine.svg2xml.text.TextFixtures.BMC_312MULT_8_2_HTML,
		};
		File[] files ={
				org.contentmine.svg2xml.text.TextFixtures.BMC_312MULT_8_0_SVG,
				org.contentmine.svg2xml.text.TextFixtures.BMC_312MULT_8_1_SVG,
				org.contentmine.svg2xml.text.TextFixtures.BMC_312MULT_8_2_SVG,
		};
		org.contentmine.svg2xml.text.TextFixtures.testSpans(values, files);
	}
	
	@Test
	public void testPageAnalyzer8() {
		PageAnalyzer pageAnalyzer = new PageAnalyzer(org.contentmine.svg2xml.text.TextFixtures.createSVG(org.contentmine.svg2xml.text.TextFixtures.BMC_312MULT_8_SVG), null);
		//PageAnalyzer pageAnalyzer = new PageAnalyzer(this, pageCounter);
		pageAnalyzer.splitChunksAndCreatePage();
		/*pageAnalyzer.analyze();
		List<AbstractContainer> containerList = pageAnalyzer.getPageAnalyzerContainerList();
		Assert.assertNotNull("containerList", containerList);
		for (AbstractContainer container : containerList) {
			LOG.trace(container.toString());
		}*/
	}


	@Before
	public void createSVGFixtures() {
		//PDFAnalyzer.createSVG(Fixtures.BMCINDIR, Fixtures.BMCSVGDIR, BMC_GEOTABLE);
	}
	
	@Test
	public void testSetup() {
		
	}
	
	//TODO update container count once a decent chunking algorithm has been written
//	@Test
//	public void testRawPage1() {
//		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE1);
//		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
//		Assert.assertNotNull("containers", containerList);
//		//Assert.assertEquals("containers", 12, containerList.size());
//	}
	
	
	
//	
//	@Test
//	public void testPage2HtmlAll() throws Exception {
//		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
//		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
//		int i = 0;
//		for (AbstractContainer container : containerList) {
//			new File("target/junk/").mkdirs();
//			SVGUtil.debug(container.createHtmlElement(), 
//				new FileOutputStream(new File("target/junk/page2."+(i++)+".html")), 1);
//		}
//	}
//	
//	@Test
//	public void testPage2Html0() {
//		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
//		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
//		String actual = containerList.get(0).createHtmlElement().toXML();
//		LOG.trace(".. "+actual);
//		Assert.assertEquals("html0", "" +
//				"<div xmlns=\"http://www.w3.org/1999/xhtml\" id=\"g.2.0\">Hiwatashi  <i>et al</i> .  <i>BMC Evolutionary Biology </i> 2011,  <b>11</b> :312 http://www.biomedcentral.com/1471-2148/11/312 </div>",
//					actual);
//	}
//	
//	/** 
//	 * Note this has wrongly elided 's'
//	 */
//	@Test
//	public void testPage2Html3_3() {
//		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.SVG_MULTIPLE_2_3_3_SVG);
//		LOG.trace(SVGElement.readAndCreateSVG(SVG2XMLFixtures.SVG_MULTIPLE_2_3_3_SVG).toXML());
//		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
//		String actual = containerList.get(0).createHtmlElement().toXML();
//		LOG.trace(".. "+actual);
//		// ids are computed so 2.0
//		Assert.assertEquals("html0", "" +
//				"<div xmlns=\"http://www.w3.org/1999/xhtml\" id=\"g.2.0\">study, we focused on gibbons (Family Hylobatidae), com-</div>",
//				actual);
//	}
//
//	/** 
//	 * Note this has wrongly elided 's'
//	 */
//	@Test
//	public void testPage2Html3() {
//		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
//		List<AbstractContainer> containerList = pageAnalyzer.getAbstractContainerList();
//		String actual = containerList.get(3).createHtmlElement().toXML();
//		LOG.trace(".. "+actual);
//		Assert.assertEquals("html3", 
//				"<div xmlns=\"http://www.w3.org/1999/xhtml\" id=\"g.2.3\">L opsin gene of two African hominoids, humans [18] and chimpanzees (primarily  <i>P. t. verus</i>" +
//				" ) [25]. In the present study, we focused on gibbons (Family Hylobatidae), com-monly known as the lesser apes, for which normal tri-chromacy is reported" +
//				" [30]. Gibbons occur in Asia and are the most diverse and speciose of all living apes [31], mak-ing them an ideal group with which to assess the range" +
//				" of L/M opsin genetic variation. We examined the nucleotide variation of both the L and M opsin genes by sequencing the 3.6~3.9-kb genomic region" +
//				" encompassing exon 3 to exon 5 from individuals in five species and three genera of gibbons. <p /></div>",
//				actual);
//	}
//
//	@Test
//	public void testPage2ScriptLineList0Content() {
//		PageAnalyzer pageAnalyzer = PageAnalyzer.createAndAnalyze(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2);
//		ScriptContainer scriptContainer = (ScriptContainer) pageAnalyzer.getAbstractContainerList().get(0);
//		List<ScriptLine> scriptLineList = scriptContainer.getScriptLineList();
//		Assert.assertEquals("scriptLines", 2, scriptLineList.size());
//		checkScriptLineListContent(
//				new String[]{
//						"Hiwatashietal.BMCEvolutionaryBiology2011,11:312  %%%%\n",
//						"http://www.biomedcentral.com/1471-2148/11/312  %%%%\n"
//				},
//				scriptLineList
//		);
//	}
	
//	@Test
//	public void testStyleSpans2_0_0() {
//		StyleSpans styleSpans = StyleSpansTest.getStyleSpans(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2, 0, 0);
//		StyleSpansTest.checkStyleSpans("0 0", 
//				"<span xmlns=\"http://www.w3.org/1999/xhtml\">Hiwatashi <i>et al</i>. <i>BMC Evolutionary Biology </i>2011, <b>11</b>:312</span>",
//				7.97, styleSpans);
//	}
//	
//	@Test
//	public void testStyleSpans2_2_0() {
//		StyleSpans styleSpans = StyleSpansTest.getStyleSpans(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2, 2, 0);
//		StyleSpansTest.checkStyleSpans("2 0", 
//				"<span xmlns=\"http://www.w3.org/1999/xhtml\">corresponding sequence of the other, this type of recombi-</span>",
//				9.763, styleSpans);
//	}
//	
//	@Test
//	public void testPage2ScriptLineList0() {
//		StyleSpans styleSpans = StyleSpansTest.getStyleSpans(SVG2XMLFixtures.RAW_MULTIPLE312_SVG_PAGE2, 0, 0);
//		StyleSpansTest.checkStyleSpans("0 0", 
//				"<span xmlns=\"http://www.w3.org/1999/xhtml\">Hiwatashi <i>et al</i>. <i>BMC Evolutionary Biology </i>2011, <b>11</b>:312</span>",
//				7.97, styleSpans);
//	}
	

	
	
	
	//================================================================
	
	//====================================================================
	
	//TODO sort out count checking once a decent chunking algorithm has been written
	private void checkAbstractContainers(Class<?>[] classes,
			List<AbstractContainer> containerList) {
		Assert.assertNotNull(containerList);
		/*try {
			Assert.assertEquals("size", classes.length, containerList.size());
		} catch (AssertionError e) {
			System.err.println("ERROR: found classes");
			for (AbstractContainer container : containerList) {
				System.err.println(container.getClass());
			}
			throw (new RuntimeException("Failed", e));
		}
		for (int i = 0; i < classes.length; i++) {
			Assert.assertEquals("container"+i, classes[i], containerList.get(i).getClass());
		}*/
	}

	private void checkContainerRawContent(String[] contents,
			List<AbstractContainer> containerList) {
		try {
			Assert.assertEquals("size", contents.length, containerList.size());
		} catch (AssertionError e) {
			for (AbstractContainer container : containerList) {
				System.err.println(container.getRawValue());
			}
			throw (new RuntimeException("Failed", e));
		}
		for (int i = 0; i < contents.length; i++) {
			Assert.assertEquals("container"+i, contents[i], containerList.get(i).getRawValue());
		}
	}

	private void checkScriptLineListContent(String[] contents,
			List<ScriptLine> scriptLineList) {
		try {
			Assert.assertEquals("size", contents.length, scriptLineList.size());
		} catch (AssertionError e) {
			for (ScriptLine scriptLine : scriptLineList) {
				System.err.println(scriptLine);
			}
			throw (new RuntimeException("Failed", e));
		}
		for (int i = 0; i < contents.length; i++) {
			Assert.assertEquals("container"+i, contents[i], scriptLineList.get(i).toString());
		}
	}


	public static void testDirectory(File inDir, File svgDir, File outDir) {
		testDirectory(inDir, svgDir, outDir, true);
	}

	public static void testDirectory(File inDir, File svgDir, File outDir, boolean skipFile) {
		File[] files = inDir.listFiles();
		if (files != null) {
			for (File file : files) {
				String path = file.getName().toLowerCase();
				if (path.endsWith(".pdf")) {
					PDFAnalyzer analyzer = new PDFAnalyzer();
					analyzer.setSVGTopDir(svgDir);
					analyzer.setOutputTopDir(outDir);
					//analyzer.setSkipFile(skipFile);
					analyzer.analyzePDFFile(file);
				}
			}
		}
	}

}

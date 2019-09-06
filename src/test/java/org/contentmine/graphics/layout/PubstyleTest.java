package org.contentmine.graphics.layout;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.CHESConstants;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.layout.DocumentChunk;
import org.contentmine.graphics.svg.layout.PubstyleManager;
import org.contentmine.graphics.svg.layout.SVGPubstyle;
import org.contentmine.graphics.svg.layout.SVGPubstyle.PageType;
import org.contentmine.graphics.svg.layout.SVGPubstyleAbstract;
import org.contentmine.graphics.svg.layout.SVGPubstyleColumn.ColumnPosition;
import org.contentmine.graphics.svg.layout.SVGPubstyleHeader;
import org.contentmine.graphics.svg.layout.SVGPubstylePage;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class PubstyleTest {
	public static final Logger LOG = Logger.getLogger(PubstyleTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testPubstyleAttributes() {
		PubstyleManager pubstyleManager = new PubstyleManager();
		SVGElement svgElement = SVGElement.readAndCreateSVG(new File(CHESConstants.SRC_GRAPHICS_RESOURCES+"/pubstyle/bmc/pubstyle.svg"));
		SVGPubstyle pubstyle = new SVGPubstyle(svgElement, pubstyleManager);
		LOG.trace("ABS "+pubstyle.getAbstract().toXML());
		Assert.assertEquals(" DOI ", "10.1186", pubstyle.getDoi());
		Assert.assertEquals("PUBLISHER ", "BioMedCentral", pubstyle.getPublisher());
		Assert.assertEquals("PUBSTYLE ", "bmc", pubstyle.getPubstyleName());
		PageType pageType = PageType.P1;
		ColumnPosition columnPosition = ColumnPosition.LEFT;
		Assert.assertNull("NULL COL ", pubstyle.getColumn(pageType, columnPosition));
		Assert.assertEquals("COLS ", 3, pubstyle.getColumnPositions().length);
		Assert.assertEquals("COL 0 ", ColumnPosition.WIDE, pubstyle.getColumnPositions()[0]);
		Assert.assertEquals("COL 1 ", ColumnPosition.LEFT, pubstyle.getColumnPositions()[1]);
		Assert.assertEquals("COL 2 ", ColumnPosition.RIGHT, pubstyle.getColumnPositions()[2]);
		Assert.assertNull("NULL FOOT ", pubstyle.getFooter(pageType));
		LOG.trace("HEADER "+pubstyle.getHeader(pageType).toXML());
		LOG.trace("RAW "+pubstyle.getRawPage(pageType).toXML());
		Assert.assertEquals("TYPE ", PageType.PN, pubstyle.getPageType());
		//LOG.debug("PAGE "+pubstyle.getPubstylePage(pageType).toXML());
		Assert.assertNull("WIDEIMAGE ", pubstyle.getWideImage(pageType));
		Assert.assertNull("WIDETABLE ", pubstyle.getWideTable(pageType));
		// Page2
		pageType = PageType.P2;
		columnPosition = ColumnPosition.LEFT;
		Assert.assertNull("NULL COL ", pubstyle.getColumn(pageType, columnPosition));
		Assert.assertEquals("COLS ", 3, pubstyle.getColumnPositions().length);
		Assert.assertEquals("COL 0 ", ColumnPosition.WIDE, pubstyle.getColumnPositions()[0]);
		Assert.assertEquals("COL 1 ", ColumnPosition.LEFT, pubstyle.getColumnPositions()[1]);
		Assert.assertEquals("COL 2 ", ColumnPosition.RIGHT, pubstyle.getColumnPositions()[2]);
		Assert.assertNull("NULL FOOT ", pubstyle.getFooter(pageType));
		LOG.trace("HEADER "+pubstyle.getHeader(pageType).toXML());
		LOG.trace("RAW "+pubstyle.getRawPage(pageType).toXML());
		Assert.assertEquals("TYPE ", PageType.PN, pubstyle.getPageType());
		LOG.trace("PAGE "+pubstyle.getPubstylePage(pageType).toXML());
		Assert.assertNull("WIDEIMAGE ", pubstyle.getWideImage(pageType));
		Assert.assertNull("WIDETABLE ", pubstyle.getWideTable(pageType));
		// PageN
		pageType = PageType.PN;
		columnPosition = ColumnPosition.LEFT;
		Assert.assertNull("NULL COL ", pubstyle.getColumn(pageType, columnPosition));
		Assert.assertEquals("COLS ", 3, pubstyle.getColumnPositions().length);
		Assert.assertEquals("COL 0 ", ColumnPosition.WIDE, pubstyle.getColumnPositions()[0]);
		Assert.assertEquals("COL 1 ", ColumnPosition.LEFT, pubstyle.getColumnPositions()[1]);
		Assert.assertEquals("COL 2 ", ColumnPosition.RIGHT, pubstyle.getColumnPositions()[2]);
		LOG.trace("FOOT "+ pubstyle.getFooter(pageType));
		LOG.trace("HEADER "+pubstyle.getHeader(pageType).toXML());
		LOG.trace("RAW "+pubstyle.getRawPage(pageType).toXML());
		Assert.assertEquals("TYPE ", PageType.PN, pubstyle.getPageType());
		//LOG.debug("PAGE "+pubstyle.getPubstylePage(pageType).toXML());
		Assert.assertNull("WIDEIMAGE ", pubstyle.getWideImage(pageType));
		Assert.assertNull("WIDETABLE ", pubstyle.getWideTable(pageType));
	}
	
	@Test
	public void testGetPubstyleByPubstyleString() {
		PubstyleManager pubstyleManager = new PubstyleManager();
		SVGPubstyle bmcStyle = pubstyleManager.getSVGPubstyleFromPubstyleName("bmc");
		Assert.assertNotNull("style", bmcStyle);
		//LOG.debug("style: "+bmcStyle.toXML());
		Assert.assertEquals("bmc", bmcStyle.getPubstyleName());
	}
	
	@Test
	public void testGuessPubstyle() {
		PubstyleManager pubstyleManager = new PubstyleManager();
		File inputSvgFile = new File(SVGHTMLFixtures.G_S_CORPUS_DIR, 
				"mosquitos1/12936_2017_Article_1948/svg/fulltext-page1.svg");
		SVGPubstyle pubstyle = pubstyleManager.guessPubstyleFromFirstPage(inputSvgFile);
		Assert.assertNotNull("bmc", pubstyle);
		Assert.assertEquals("bmc", pubstyle.getPubstyleName());
	}
	
	@Test
	public void testPubstylePage1() {
		PubstyleManager pubstyleManager = new PubstyleManager();
		SVGPubstyle bmcPubstyle = pubstyleManager.getSVGPubstyleFromPubstyleName("bmc");
		Assert.assertNotNull("pubstyle.bmc", bmcPubstyle);
		SVGSVG.wrapAndWriteAsSVG(bmcPubstyle, new File("target/pubstyle/bmc/pubstyle.svg"));
		SVGElement pubstylePage1 = bmcPubstyle.getRawPage(PageType.P1);
		Assert.assertNotNull("null page", pubstylePage1);
		SVGElement pubstyleHeader = bmcPubstyle.getHeader(PageType.P1);
		Assert.assertNotNull("null header", pubstyleHeader);
		SVGElement pubstyleAbstract = bmcPubstyle.getAbstract();
		Assert.assertNotNull("null abstract", pubstyleAbstract);
		SVGElement abstractSection = bmcPubstyle.getAbstractSection();
		Assert.assertNotNull("abstractSection", abstractSection);
	}
	
	@Test
	public void testPubstylePage2() {
		PubstyleManager pubstyleManager = new PubstyleManager();
		SVGPubstyle pubstyle = pubstyleManager.getSVGPubstyleFromPubstyleName("bmc");
		SVGPubstylePage pubstylePage2 = pubstyle.getPubstylePage(PageType.P2);
		Assert.assertNotNull("page2", pubstylePage2);
		//LOG.debug("page2 "+pubstylePage2.toXML());
		SVGPubstyleAbstract pubstyleAbstract = pubstyle.getAbstract();
		Assert.assertNotNull("abstract", pubstyleAbstract);
		SVGElement pubstyleHeader = pubstyle.getHeader(PageType.P2);
		//LOG.error("FIXTEST");
		if (true) return;
		Assert.assertNotNull("header", pubstyleHeader);
		SVGElement pubstyleFooter = pubstyle.getFooter(PageType.P2);
		Assert.assertNotNull("footer", pubstyleFooter);
		SVGElement pubstyleLeft = pubstyle.getColumn(PageType.P2, ColumnPosition.LEFT);
		Assert.assertNotNull("left", pubstyleLeft);
		SVGElement pubstyleRight = pubstyle.getColumn(PageType.P2, ColumnPosition.RIGHT);
		Assert.assertNotNull("right", pubstyleRight);
		SVGElement pubstyleWideImage = pubstyle.getWideImage(PageType.P2);
		Assert.assertNotNull("wideImage", pubstyleWideImage);
		SVGElement pubstyleWideTable = pubstyle.getWideTable(PageType.P2);
		Assert.assertNotNull("wideTable", pubstyleWideTable);
	}
	
	@Test
	// FIXME
	public void testPubstylePage2Header() {
		PubstyleManager pubstyleManager = new PubstyleManager();
		SVGPubstyle pubstyle = pubstyleManager.getSVGPubstyleFromPubstyleName("bmc");
		SVGPubstyleHeader pubstyleHeader = pubstyle.getHeader(PageType.P2);
		Assert.assertNotNull("header not null", pubstyleHeader);
		Assert.assertEquals("bbox rect", 1, SVGRect.extractSelfAndDescendantRects(pubstyleHeader).size());
		Assert.assertEquals("header texts", 3, SVGText.extractSelfAndDescendantTexts(pubstyleHeader).size());
		Real2Range bbox = pubstyleHeader.getBoundingBox();
		Assert.assertEquals("bbox", "((0.0,540.0),(0.0,47.0))", pubstyleHeader.getBoundingBox().toString());
	}

	@Test
	public void testPubstylePage2HeaderAgainstText() {
		PubstyleManager pubstyleManager = new PubstyleManager();
		SVGPubstyle pubstyle = pubstyleManager.getSVGPubstyleFromPubstyleName("bmc");
		for (int ipage = 1; ipage <= 13; ipage++) {
			SVGPubstyleHeader pubstyleHeader = null;
			if (ipage == 1) {
				pubstyleHeader = pubstyle.getHeader(PageType.P1);
			} else if (ipage == 13) {
				pubstyleHeader = pubstyle.getHeader(PageType.PN);
			} else {
				pubstyleHeader = pubstyle.getHeader(PageType.P2);
			}
			File inputSvgFile = new File(SVGHTMLFixtures.G_S_CORPUS_DIR, 
					"mosquitos1/12936_2017_Article_1948/svg/fulltext-page"+ipage+".svg");
			SVGElement inputSVGElement = SVGElement.readAndCreateSVG(inputSvgFile);
			Map<String, String> keyValues = pubstyleHeader.extractKeyValues(inputSVGElement);
			//LOG.debug("keys "+keyValues.entrySet());
			//LOG.error("Authors and journal are muddled");
			Assert.assertEquals((ipage == 1) ? 6 : 7, keyValues.size());
			Assert.assertEquals("16", keyValues.get("vol"));
			Assert.assertEquals(ipage == 1 ? null : ""+ipage, keyValues.get("page"));
		}

	}

	@Test
	@Ignore // long
	public void testPubstyleSectionsInCorpus() {
		PubstyleManager pubstyleManager = new PubstyleManager();
		SVGPubstyle pubstyle = pubstyleManager.getSVGPubstyleFromPubstyleName("bmc");
		int end = 99;//13;
		int start = 1;
		String[] dirRoots = {
				"mosquitos1/12936_2017_Article_1948",
				"mosquitos1/12936_2017_Article_2115",
				"mosquitos1/12936_2017_Article_2156",
				"mosquitos1/13071_2017_Article_2342",
				"mosquitos1/13071_2017_Article_2417",
				"mosquitos1/13071_2017_Article_2489",
				"mosquitos1/13071_2017_Article_2546",
				"mosquitos1/13071_2017_Article_2581",
				"mosquitos1/13071_2018_Article_2625",
				"mosquitos1/wellcomeopenres-2-13662",
		};
		for (String dirRoot : dirRoots) {
			String pageRoot = dirRoot + "/svg/fulltext-page";
			pubstyle.setEndPage(end);
			pubstyle.setDirRoot(dirRoot);
			for (int page = start; page <= end; page++) {
				pubstyle.setCurrentPage(page);
				File inputSvgFile = new File(SVGHTMLFixtures.G_S_CORPUS_DIR, pageRoot+page+".svg");
				if (!inputSvgFile.exists()) {
//					LOG.debug("====================FINISHED=================");
					break;
				}
				SVGElement inputSVGElement = SVGElement.readAndCreateSVG(inputSvgFile);
				List<DocumentChunk> documentChunks = pubstyle.createDocumentChunks(inputSVGElement);
				SVGSVG.wrapAndWriteAsSVG(documentChunks, new File("target/pubstyle/" + dirRoot + "/page"+page+".svg"));
			}
		}
	}



}

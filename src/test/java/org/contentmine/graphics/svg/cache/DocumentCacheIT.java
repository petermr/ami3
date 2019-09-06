package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

//import org.openjdk.jmh.annotations.*;
//import java.util.concurrent.TimeUnit;


//@Ignore("This really should be in POM or CL")

public class DocumentCacheIT {
public static final Logger LOG = Logger.getLogger(DocumentCacheIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	
	@Test
	/** 9-page article.
	 * 
	 */
	// FIXME TEST
	public void testDocument() {
		String fileroot = "varga1";
		File directory = new File(SVGHTMLFixtures.G_S_PAGE_DIR, fileroot);
//		LOG.debug("dir "+directory);
		Assert.assertTrue("dir ", directory.exists());
		DocumentCache documentCache = DocumentCache.createDocumentCache(directory);
		documentCache.setCreateSummaryDebugBoxes(true);
		documentCache.processSVGFilesToSVGElementAndHTMLElement();
		// superimposed pages
		SVGElement g = documentCache.getOrCreateConvertedSVGElement();
		Assert.assertNotNull("non-null g", g);
//		LOG.debug("gg "+g.toXML());
		Assert.assertTrue("empty g", g.getChildCount() > 0);
		File file = new File("target/document/"+fileroot+"/boxes.svg");
//		LOG.debug("wrote: "+file.getAbsolutePath());
		SVGSVG.wrapAndWriteAsSVG(g, file);
		Assert.assertTrue("file exists: "+file, file.exists());
	}
	
	@Test 
	// FIXME null pointer
	@Ignore
	public void testPageComponents() {
		
		DocumentCache documentCache = new DocumentCache();
//		LOG.warn("incomplete, pageLayout not fixed");
		documentCache.analyzePages(SVGHTMLFixtures.G_S_PAGE_DIR, PageLayout.AMSOCGENE_RESOURCE, 9, "varga1/", new File("target/cache"));
//		documentCache.analyzePages(PageLayout.BMC, 8, "bmc/1471-2148-11-329/", new File("target/cache"));
//		documentCache.analyzePages(PageLayout.PLOSONE2016, 15, "TimmermansPLOS/", new File("target/cache"));
	}
	

	@Test
	public void testCreateHTMLPageAllCrop() throws IOException {
		String fileroot = "varga1";
		File targetDir = new File("target/document/" + fileroot);
		SVGHTMLFixtures.cleanAndCopyDir(new File(SVGHTMLFixtures.G_S_PAGE_DIR, fileroot + "/"), targetDir);
		DocumentCache documentCache = DocumentCache.createDocumentCache(targetDir);
		documentCache.processSVGFilesToSVGElementAndHTMLElement();
		XMLUtil.debug(documentCache.getHtmlDiv(), new File("target/html/pages.html"), 1);

	}
	
	@Test
	public void testCropBMC() throws IOException {
		// 295
		File targetDir = new File("target/document/bmc/12936_2017_Article_1948/");
		SVGHTMLFixtures.cleanAndCopyDir(new File(SVGHTMLFixtures.G_S_CORPUS_DIR, "mosquitos1/12936_2017_Article_1948/"), targetDir);
		DocumentCache documentCache = DocumentCache.createDocumentCache(targetDir);
		documentCache.processSVGFilesToSVGElementAndHTMLElement();
		XMLUtil.debug(documentCache.getHtmlDiv(), new File(targetDir, "pages.html"), 1);
	}
	


	/** JSTOR article.
	 * 
	 */
	@Test
	@Ignore
	// FIXME TEST
	public void testJSTORDevereux() {
		String fileroot = "Devereux1950";
		CTree ctree = new CTree(new File(SVGHTMLFixtures.CLOSED_GVSU, fileroot+"/"));
		DocumentCache documentCache = new DocumentCache(ctree);
		documentCache.processSVGFilesToSVGElementAndHTMLElement();
		SVGElement g = documentCache.getOrCreateConvertedSVGElement();
		Assert.assertNotNull("non-null g", g);
		Assert.assertTrue("empty g", g.getChildCount() > 0);
		File file = new File("target/document/"+fileroot+"/boxes.svg");
//		LOG.debug("wrote: "+file.getAbsolutePath());
		SVGSVG.wrapAndWriteAsSVG(g, file);
		Assert.assertTrue("file exists: "+file, file.exists());
	}
	


}

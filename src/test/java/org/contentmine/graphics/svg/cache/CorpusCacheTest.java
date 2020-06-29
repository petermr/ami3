package org.contentmine.graphics.svg.cache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

public class CorpusCacheTest extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(CorpusCacheTest.class);
@Test
	public void testMLCorpusCache() {
		File corpusDir = new File(PDF2SVG2, "test/");
		if (!corpusDir.exists()) {
			LOG.debug("no corpus: "+corpusDir);
		}
		CProject cProject = new CProject(corpusDir);
		CorpusCache corpusCache = new CorpusCache(cProject);
		List<DocumentCache> documentCacheList = corpusCache.getOrCreateDocumentCacheList();
		Assert.assertEquals("doc cache",  4, documentCacheList.size());
		DocumentCache docCache0 = documentCacheList.get(0);
		LOG.debug(docCache0);
		LOG.trace("MADE CORPUS");
		List<HtmlElement> htmlElementList = corpusCache.getOrCreateHtmlElementList();
		Assert.assertEquals("html files ", 4, htmlElementList.size());
		HtmlHtml.wrapAndWriteAsHtml(htmlElementList, corpusDir);
	}
	
	@Test
	public void testMLDocumentCache() {
		File corpusDir = new File(PDF2SVG2, "test/");
		if (!corpusDir.exists()) {
			LOG.debug("no corpus: "+corpusDir);
		}
		CProject cProject = new CProject(corpusDir);
		CorpusCache corpusCache = new CorpusCache(cProject);
		List<DocumentCache> documentCacheList = corpusCache.getOrCreateDocumentCacheList();
		Assert.assertEquals("doc cache",  4, documentCacheList.size());
		DocumentCache docCache0 = documentCacheList.get(0);
		LOG.debug(docCache0);
		LOG.trace("MADE CORPUS");
		List<HtmlElement> htmlElementList = corpusCache.getOrCreateHtmlElementList();
		Assert.assertEquals("html files ", 4, htmlElementList.size());
		HtmlHtml.wrapAndWriteAsHtml(htmlElementList, corpusDir);
	}
	


	/** get boxes for different text styles
	 * 
	 */
	@Test
	public void testGetTextBoxes() {
		CProject cProject = new CProject(new File(PDF2SVG2, "test"));
		CorpusCache corpusCache = new CorpusCache(cProject);
		corpusCache.createTextBoxesAndWriteToSVGDirectories();
	}


}

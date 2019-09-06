package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

public class CorpusCacheTest {
	private static final Logger LOG = Logger.getLogger(CorpusCacheTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	@Ignore // toolong // FIXME
	public void testCorpusCache() {
		File corpusDir = new File(SVGHTMLFixtures.G_S_CORPUS_DIR, "mosquitos/");
		if (!corpusDir.exists()) {
			LOG.info("directory not found: "+corpusDir);
			return;
		}
		CProject cProject = new CProject(corpusDir);
		CorpusCache corpusCache = new CorpusCache(cProject);
		List<DocumentCache> documentCacheList = corpusCache.getOrCreateDocumentCacheList();
		// gets this wrong (returns 985??)
		Assert.assertEquals("doc cache",  10, documentCacheList.size());
		DocumentCache docCache0 = documentCacheList.get(0);
		LOG.trace(docCache0);
		LOG.trace("MADE CORPUS");
		List<HtmlElement> htmlElementList = corpusCache.getOrCreateHtmlElementList();
		Assert.assertEquals("html files ", 4, htmlElementList.size());
		HtmlHtml.wrapAndWriteAsHtml(htmlElementList, corpusDir);
	}
	@Test
	@Ignore // toolong // FIXME
	public void testCorpusCache1() {
		File corpusDir = new File(SVGHTMLFixtures.G_S_CORPUS_DIR, "mosquitos1/");
		if (!corpusDir.exists()) {
			LOG.info("directory not found: "+corpusDir);
			return;
		}
		CorpusCache corpusCache = new CorpusCache(new CProject(corpusDir));
		List<DocumentCache> documentCacheList = corpusCache.ensureDocumentCacheList();
		// gets this wrong (returns 985??)
		Assert.assertEquals("doc cache",  10, documentCacheList.size());
		DocumentCache docCache0 = documentCacheList.get(0);
		LOG.trace(docCache0);
		LOG.trace("MADE CORPUS");
		List<HtmlElement> htmlElementList = corpusCache.getOrCreateHtmlElementList();
		Assert.assertEquals("html files ", 4, htmlElementList.size());
		HtmlHtml.wrapAndWriteAsHtml(htmlElementList, corpusDir);
	}
	
	@Test
	/** summarizes GVSU papers without conversion.
	 * 
	 * @throws Exception
	 */
	public void testALLGVSUPapersIT() throws Exception {
		if (!TestUtil.checkForeignDirExists(SVGHTMLFixtures.CLOSED_GVSU)) return;
		SVGHTMLFixtures.cleanAndCopyDir(SVGHTMLFixtures.CLOSED_GVSU, SVGHTMLFixtures.CLOSED_GVSU_TARGET);
		CorpusCache corpusCache = CorpusCache.createCorpusCache(SVGHTMLFixtures.CLOSED_GVSU_TARGET);
		Assert.assertNotNull(corpusCache);
		CProject cProject = corpusCache.getCProject();
		Assert.assertNotNull(cProject);
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		Assert.assertEquals("ctrees", 49, cTreeList.size());
		List<DocumentCache> documentCacheList = corpusCache.getOrCreateDocumentCacheList();
		Assert.assertEquals("documentcaches", 49, documentCacheList.size());
		List<File> pdfFiles = corpusCache.getFulltextPDFFiles();
		Assert.assertEquals("fulltextPDF", 49, pdfFiles.size());
		List<File> htmlFiles = corpusCache.getFulltextHTMLFiles();
		Assert.assertEquals("fulltextPDF", 1, htmlFiles.size());
	}

	@Test
	@Ignore("long")
	public void testCreatorALLGVSUPapersIT() throws Exception {
		if (!TestUtil.checkForeignDirExists(SVGHTMLFixtures.CLOSED_GVSU)) return;
		SVGHTMLFixtures.cleanAndCopyDir(SVGHTMLFixtures.CLOSED_GVSU, SVGHTMLFixtures.CLOSED_GVSU_TARGET);
		CorpusCache corpusCache = CorpusCache.createCorpusCache(SVGHTMLFixtures.CLOSED_GVSU_TARGET);
        corpusCache.convertPDF2SVG();
	}

	@Test
//	@Ignore("long")
	public void testConvertPDF2HTMLALLGVSUPapersIT() throws Exception {
		if (!TestUtil.checkForeignDirExists(SVGHTMLFixtures.CLOSED_GVSU)) return;
		SVGHTMLFixtures.cleanAndCopyDir(SVGHTMLFixtures.CLOSED_GVSU, SVGHTMLFixtures.CLOSED_GVSU_TARGET);
		CorpusCache corpusCache = CorpusCache.createCorpusCache(SVGHTMLFixtures.CLOSED_GVSU_TARGET);
        corpusCache.convertPDF2HTML();
	}

	@Test
	// FIXME TEST A
    @Ignore("HTML output not yet fixed")
	public void testALLGVSUPapers2HTMLIT() throws Exception {
		if (!TestUtil.checkForeignDirExists(SVGHTMLFixtures.CLOSED_GVSU)) return;
		SVGHTMLFixtures.cleanAndCopyDir(SVGHTMLFixtures.CLOSED_GVSU, SVGHTMLFixtures.CLOSED_GVSU_TARGET);
        CProject cProject = new CProject(SVGHTMLFixtures.CLOSED_GVSU_TARGET);
        CorpusCache corpusCache = new CorpusCache(cProject);
        CTreeList cTreeList = cProject.getOrCreateCTreeList();
        List<File> htmlFiles = cTreeList.getFulltextHtmlFiles();
        Assert.assertEquals(1,  htmlFiles.size());
        for (File htmlFile : htmlFiles) {
        	LOG.debug("deleted: "+FileUtils.deleteQuietly(htmlFile));
        }
        htmlFiles = cTreeList.getFulltextHtmlFiles();
        Assert.assertEquals(0,  htmlFiles.size());
        corpusCache.convertPDF2SVG();
        cProject.convertSVG2HTML();
        htmlFiles = cTreeList.getFulltextHtmlFiles();
        Assert.assertEquals(49,  htmlFiles.size());
        
	}

}

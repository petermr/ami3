package org.contentmine.graphics.layout;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.cache.CorpusCache;
import org.contentmine.graphics.svg.cache.DocumentCache;
import org.contentmine.graphics.svg.cache.PageCache;
import org.contentmine.graphics.svg.cache.PageCacheList;
import org.contentmine.graphics.svg.layout.PubstyleManager;
import org.contentmine.graphics.svg.layout.SVGPubstyle;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

@Ignore("This really should be in POM or CL")
public class PubstyleIT {
	
	private static final Logger LOG = Logger.getLogger(PubstyleIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testPubstyleCache() {
		PubstyleManager pubstyleManager = new PubstyleManager();
		SVGPubstyle pubstyle = pubstyleManager.getSVGPubstyleFromPubstyleName("bmc");
		int end = 99; // break after file-not-found
		int start = 1;
		File cProjectDir = new File(SVGHTMLFixtures.G_S_CORPUS_DIR, "mosquitos");
		Assert.assertTrue(cProjectDir.exists());
		LOG.debug("CPROJECT: "+cProjectDir);
//		Assert.assertEquals(1,  cProject.get);
		String dirRoot = "12936_2017_Article_1948";
		File cTree = new File(cProjectDir, dirRoot);
		String pageRoot =  "svg/fulltext-page";
		pubstyle.setEndPage(end);
		DocumentCache documentCache = DocumentCache.createDocumentCache(cTree);
		end = documentCache.getPageCount();
		for (int page = start; page <= end; page++) {
			LOG.debug("===================== current "+page+"====================");
			pubstyle.setCurrentPage(page);
			File inputSvgFile = new File(cTree, pageRoot+page+".svg");
			if (!inputSvgFile.exists()) {
				LOG.debug("cannot find: "+inputSvgFile);
				LOG.debug("====================FINISHED=================");
				break;
			}
			SVGElement inputSVGElement = SVGElement.readAndCreateSVG(inputSvgFile);
			PageCache pageCache = new PageCache(documentCache);
			
			pageCache.readGraphicsComponentsAndMakeCaches(inputSVGElement);
			
			String topDirName = "target/pubstyle/";
			File file;
			file = pageCache.debugSvgElementToSVGFile(topDirName, dirRoot, page);
			file = pageCache.debugShapesToSVGFile(topDirName, dirRoot, page);
			file = pageCache.debugChunksToSVGFile(pubstyle, topDirName, dirRoot, page);
		}
	}

	@Test
		// FIXME TEST
		public void testPubstyleSections() {
			PubstyleManager pubstyleManager = new PubstyleManager();
			SVGPubstyle pubstyle = pubstyleManager.getSVGPubstyleFromPubstyleName("bmc");
			CorpusCache corpus = CorpusCache.createCorpusCache(new File(SVGHTMLFixtures.G_S_CORPUS_DIR, "mosquitos1"));
			Assert.assertEquals("documents", 3,  corpus.getOrCreateDocumentCacheList().size());
			
			DocumentCache documentCache = corpus.getDocumentCache("12936_2017_Article_1948");
			PageCacheList pageCacheList = documentCache.getOrCreatePageCacheList();
			Assert.assertEquals("pages",  14, pageCacheList.size());
			// we start at 1
			PageCache pageCache0 = pageCacheList.get(1);
	//		LOG.debug(pageCache0.getSerialNumber());
			pubstyle.setEndPage(pageCacheList.size());
			for (PageCache pageCache : pageCacheList) {
	//			LOG.debug("===================== current "+pageCache+"====================");
				pubstyle.setCurrentPage(pageCache.getSerialNumber());
	//			SVGElement inputSVGElement = SVGElement.readAndCreateSVG(pageCache.getSVGElement());
	//			LOG.debug("inputSVG: "+inputSVGElement.toXML().length());
	//			List<DocumentChunk> documentChunks = pubstyle.createDocumentChunks(inputSVGElement);
	//			LOG.debug("DocumentChunks: "+documentChunks.size());
	//			// some pages are null
	////			Assert.assertTrue("document chunk != 0 "+page, documentChunks.size() > 0);
	//			File file = new File("target/pubstyle/" + documentCache.getName() + "/page"+page+".svg");
	//			SVGSVG.wrapAndWriteAsSVG(documentChunks, file);
	////			Assert.assertTrue("page.svg", file.exists());
			}
		}

}

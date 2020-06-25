package org.contentmine.norma.demos.jenny;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.cache.CorpusCache;
import org.contentmine.graphics.svg.cache.DocumentCache;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class MosquitoTest {
	private static final Logger LOG = LogManager.getLogger(MosquitoTest.class);
@Test
	/** creates a corpus of 10 papers about mosquitos.
	 * mainly BMC (from Jenny Molloy)
	 * initially creates both original and compact SVG forms
	 * we have copied the compact form back to the demo directory???
	 */
	@Ignore // too long
	public void testCreateCorpus() {
		File target = new File("target/demo/mosquitos/");
		Assert.assertTrue(NormaFixtures.MOSQUITOS_DIR.exists());
		CMineTestFixtures.cleanAndCopyDir(NormaFixtures.MOSQUITOS_DIR, target);
		new Norma().convertRawPDFToProjectToCompactSVG(target);
		Assert.assertTrue(target.listFiles().length > 15);

	}

	@Test
	@Ignore // papers in cephis instead
	public void testAnalyzeCorpus() {
		File targetDir = new File("target/demo/mosquitos/");
		CMineTestFixtures.cleanAndCopyDir(NormaFixtures.MOSQUITOS_DIR, targetDir);
		CorpusCache corpusCache = CorpusCache.createCorpusCache(targetDir);
		Assert.assertNotNull(corpusCache);
		CProject cProject = corpusCache.getCProject();
		Assert.assertNotNull(cProject);
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		Assert.assertEquals("ctrees", 10, cTreeList.size());
		List<DocumentCache> documentCacheList = corpusCache.getOrCreateDocumentCacheList();
		Assert.assertEquals("documentcaches", 10, documentCacheList.size());
		List<File> pdfFiles = corpusCache.getFulltextPDFFiles();
		Assert.assertEquals("fulltextPDF", 10, documentCacheList.size());
//		AbstractCMElement documentSVG = documentCache.processSVGInCTreeDirectory(targetDir);
//		LOG.debug("DOC "+documentSVG.toXML());
//		SVGSVG.wrapAndWriteAsSVG((SVGElement)documentSVG, new File("target/demo/mosquitos/document.svg"));
	}
}

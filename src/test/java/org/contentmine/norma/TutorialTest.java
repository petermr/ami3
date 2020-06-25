package org.contentmine.norma;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.Norma;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** tests Norma.
 * from a set of commandlines
 * 
 */

/**

 * @author pm286
 *
 */
public class TutorialTest {
	
	
	private static final Logger LOG = LogManager.getLogger(TutorialTest.class);
/**
	# single XML file
	rm -rf cmdir_xml
	norma -i singleFiles/test_xml_1471-2148-14-70.xml -o cmdir_xml
	*/
	@Test
	public void testConvertSingleFileToCTree() throws Exception {
		File ctree = new File("target/cmdir_xml/test_xml_1471-2148-14-70");
		if (ctree.exists()) FileUtils.forceDelete(ctree);
		Assert.assertFalse(ctree.exists());
		String args = "-i " + NormaFixtures.REGRESSION_DIR +"/"+"singleFiles/test_xml_1471-2148-14-70.xml -o target/cmdir_xml";
		Norma norma = new Norma();
		norma.run(args);
		File output = CTree.getExistingFulltextXML(ctree);
		Assert.assertNotNull("fulltextXML", output);
		
	}
	
	/**

	rm -rf cmdirs_xml
	norma -i \
	    singleFiles/test_xml_1471-2148-14-70.xml \
	    singleFiles/plosone_0115884.xml \
		 -o cmdirs_xml
	 */
	@Test
	public void testConvertTwoFilesToCTree() throws Exception {
		File cTreeTop = new File("target/cmdirs_xml");
		if (cTreeTop.exists())FileUtils.forceDelete(cTreeTop);
		Assert.assertFalse(cTreeTop.exists());
		String args = "-i "+ NormaFixtures.REGRESSION_DIR+ "/singleFiles/test_xml_1471-2148-14-70.xml "
				+            NormaFixtures.REGRESSION_DIR+"/singleFiles/plosone_0115884.xml "
				+ "-o target/cmdirs_xml --ctree";
		Norma norma = new Norma();
		norma.run(args);
		Assert.assertTrue(cTreeTop.exists());
	}		
	
	/**

	rm -rf cmdirs_all
	norma -i \
	    	singleFiles/test_xml_1471-2148-14-70.xml \
		    singleFiles/test_pdf_1471-2148-14-70.pdf \
	    	singleFiles/plosone_0115884.xml \
		 -o cmdirs_all
	 */
	@Test
	public void testMixedFilesToCTree() throws Exception {
		File cTreeTop = new File("target/cmdirs_xml");
		if (cTreeTop.exists())FileUtils.forceDelete(cTreeTop);
		Assert.assertFalse(cTreeTop.exists());
		String args = "-i "
				+ NormaFixtures.REGRESSION_DIR+ "/singleFiles/test_xml_1471-2148-14-70.xml "
				+ NormaFixtures.REGRESSION_DIR+ "/singleFiles/test_pdf_1471-2148-14-70.pdf "
				+ NormaFixtures.REGRESSION_DIR+ "/singleFiles/plosone_0115884.xml "
				+ "-o target/cmdirs_xml --ctree"
				+ "";
		Norma norma = new Norma();
		norma.run(args);
		Assert.assertTrue(cTreeTop.exists());
		Assert.assertNotNull(CTree.getExistingFulltextXML(new File("target/cmdirs_xml/plosone_0115884_xml")));
		Assert.assertNotNull(CTree.getExistingFulltextPDF(new File("target/cmdirs_xml/test_pdf_1471_2148_14_70_pdf")));
		Assert.assertNotNull(CTree.getExistingFulltextXML(new File("target/cmdirs_xml/test_xml_1471_2148_14_70_xml")));
	}		
	
	@Test
	@Ignore // cannot reach directory
	public void testConvertXML2HTML() throws Exception {
		File targetDir = new File("target/zika");
		CMineTestFixtures.cleanAndCopyDir(new File("../advert/zika"), targetDir);
		String args = "--project "+targetDir+" -i fulltext.xml -o scholarly.html --transform nlm2html";
		Norma norma = new Norma();
		norma.run(args);
	}

	@Test
	// FIXME TEST
	@Ignore
	public void testSections() throws Exception {
		LOG.warn("FIXME - method extraction");
		File targetDir = new File("target/neuro");
		CMineTestFixtures.cleanAndCopyDir(new File("src/test/resources/org/contentmine/norma/transform/neuro"), targetDir);
		String args = "--project "+targetDir+" -i fulltext.xml --output method.xml --transform nlm2method";
		Norma norma = new Norma();
		norma.run(args);
		Assert.assertTrue(new File(targetDir, "PMC3307046/fulltext.xml").exists());
		// NYI
		Assert.assertTrue("method.xml", new File( targetDir, "PMC3307046/method.xml").exists());
		args = "--project "+targetDir+" -i method.xml -o method.html --transform nlm2html";
		norma.run(args);
	}
	/**
	# create scholarly.html for 9 publishers
	 */
	/**
	cp -R quickscrapeDirs/acp/acp-15-1013-2015 temp-acp
	rm temp-acp/scholarly.html
	norma -q temp-acp -i fulltext.xml -o scholarly.html --transform nlm2html
	ls -lt temp-acp/scholarly.html
	 */
	@Test
	@Ignore // not sure what this was meant to do
	public void testConvertACPHTML() throws Exception {
		String sourceName = NormaFixtures.QUICKSCRAPE_DIR+ "/acp/acp-15-1013-2015";
		String destName = "target/cmdirs_all/acp/acp-15-1013-2015";
		testXML2ScholarlyHTML(sourceName, destName);
	}	
	
	
	/**
	cp -R src/test/resources/org/contentmine/norma/pubstyle/bmc/1471-2148-14-70/  temp-bmc
	rm temp-bmc/scholarly.html
	norma -q temp-bmc -i fulltext.xml -o scholarly.html --transform bmc2html
	ls -lt temp-bmc/scholarly.html
	 */
	/**
	cp -R src/test/resources/org/contentmine/norma/pubstyle/elife/e04407 temp-elife
	rm temp-elife/scholarly.html
	norma -q temp-elife -i fulltext.xml -o scholarly.html --transform nlm2html
	ls -lt temp-elife/scholarly.html
	 */
	/**
	cp -R src/test/resources/org/contentmine/norma/pubstyle/f1000research/3-190 temp-f1000research
	rm temp-f1000research/scholarly.html
	norma -q temp-f1000research -i fulltext.xml -o scholarly.html --transform nlm2html
	ls -lt temp-f1000research/scholarly.html
	 */
	/**
	cp -R src/test/resources/org/contentmine/norma/pubstyle/frontiers/fpsyg-05-01582 temp-frontiers
	rm temp-frontiers/scholarly.html
	norma -q temp-frontiers -i fulltext.xml -o scholarly.html --transform nlm2html
	ls -lt temp-frontiers/scholarly.html
	 */
	/**
	cp -R src/test/resources/org/contentmine/norma/pubstyle/hindawi/247835 temp-hindawi
	rm temp-hindawi/scholarly.html
	norma -q temp-hindawi -i fulltext.xml -o scholarly.html --transform nlm2html
	ls -lt temp-hindawi/scholarly.html
	 */
	/**
	cp -R src/test/resources/org/contentmine/norma/pubstyle/mdpi/04-00932 temp-mdpi
	rm temp-mdpi/scholarly.html
	norma -q temp-mdpi -i fulltext.xml -o scholarly.html --transform nlm2html
	ls -lt temp-mdpi/scholarly.html
	 */
	/**
	cp -R src/test/resources/org/contentmine/norma/pubstyle/peerj/727 temp-peerj
	rm temp-peerj/scholarly.html
	norma -q temp-peerj -i fulltext.xml -o scholarly.html --transform nlm2html
	ls -lt temp-peerj/scholarly.html
	 */
	/**
	cp -R src/test/resources/org/contentmine/norma/pubstyle/pensoft/4478 temp-pensoft
	rm temp-pensoft/scholarly.html
	norma -q temp-pensoft -i fulltext.xml -o scholarly.html --transform nlm2html
	ls -lt temp-pensoft/scholarly.html
	 */
	/**
	cp -R src/test/resources/org/contentmine/norma/pubstyle/plosone/journal.pone.0115884/ temp-plosone
	rm temp-plosone/scholarly.html
	norma -q temp-plosone -i fulltext.xml -o scholarly.html --transform nlm2html
	ls -lt temp-plosone/scholarly.html
	 */
	
	// ============= private ===============

	private void testXML2ScholarlyHTML(String sourceName, String destName) throws IOException {
		transformNLM(sourceName, destName);
	}

	private void transformNLM(String sourceName, String destName) throws IOException {
		String dtdName = NormaTransformer.NLM2HTML;
		transformXML2ScholarlyHTML(sourceName, destName, "fulltext.xml", "scholarly.html", dtdName);
	}

	private void transformXML2ScholarlyHTML(String sourceName, String destName, String infile,
			String outfile, String dtdName) throws IOException {
		File cTreeTop = new File(destName);
		if (cTreeTop.exists())FileUtils.forceDelete(cTreeTop);
		FileUtils.copyDirectory(new File(sourceName), cTreeTop);
		Assert.assertNotNull("xml", CTree.getExistingFulltextXML(cTreeTop));
		FileUtils.forceDelete(CTree.getExistingFulltextHTML(cTreeTop));
		String args = "-q "+destName
				+ " --transform " + dtdName
				+ " --standalone true"
				+ " -i " + infile
				+ " -o " + outfile;
		Norma norma = new Norma();
		norma.run(args);
		Assert.assertNotNull("html", CTree.getExistingScholarlyHTML(new File(destName)));
	}
}



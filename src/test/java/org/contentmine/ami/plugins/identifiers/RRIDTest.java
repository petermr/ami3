package org.contentmine.ami.plugins.identifiers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.lookups.RRIDLookup;
import org.contentmine.ami.plugins.identifier.IdentifierArgProcessor;
import org.contentmine.cproject.lookup.AbstractLookup;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.NormaArgProcessor;


public class RRIDTest {

	public static final Logger LOG = Logger.getLogger(RRIDTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	@Ignore // uses lookup
	public void testAny() throws IOException {
		AbstractLookup rridLookup = new RRIDLookup();
		String response = rridLookup.lookup("AB_570435");
		FileUtils.write(new File("target/rrid/ab570435.html"), response);
	}
	
	@Test
	@Ignore // fails with http 502 
	public void testAnyXML() throws IOException {
		AbstractLookup rridLookup = new RRIDLookup();
		rridLookup.setOutputFormat(".xml");
		String response = rridLookup.lookup("AB_570435");
		FileUtils.write(new File("target/rrid/ab570435.xml"), response);
	}

	@Test
	// SHOWCASE
	// TESTED 2016-01-12
//	@Ignore // uses lookup
	public void testAmiIdentifier() throws IOException {

	    File neuro4415html = new File(AMIFixtures.TEST_RRID_DIR, "JNEUROSCI.4415-13.2014.html");
	    LOG.debug("input: "+neuro4415html);
	    File q4415 = new File(AMIFixtures.TEST_RRID_DIR, "q4415/");
	    q4415.mkdirs();
	    FileUtils.copyFile(neuro4415html, new File(q4415, "fulltext.html"));
	    String cmd = "norma -q " + q4415 + " --input fulltext.html --html htmlunit -o scholarly.html";
	    new NormaArgProcessor(cmd).runAndOutput();
	    File rridDir = new File("target/rrid/");
	    if (rridDir.exists()) rridDir.delete();
	    rridDir.mkdirs();
	    FileUtils.copyDirectory(AMIFixtures.TEST_RRID_DIR, rridDir);
	    cmd = "--id.identifier --context 35 50 --id.regex  "+NAConstants.MAIN_AMI_DIR+"/regex/identifiers.xml --id.type rrid.ab -q "+rridDir+" -i scholarly.html"; 
	    IdentifierArgProcessor argProcessor = new IdentifierArgProcessor(cmd);
	    argProcessor.runAndOutput();
		AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
				"<results title=\"rrid.ab\">"
				+ "<result pre=\" receptor α2 (catalog #600-401-D45 \" exact=\"RRID:AB_11182018\" "
				+ "post=\"; Rockland Immunochemicals), α5 (catalog #AB9678 R\" "
				+ "xpath=\"/html[1]/body[1]/div[1]/div[3]/div[3]/");
	    Assert.assertTrue("exists", rridDir.exists());
	    File resultsXml = new File(rridDir, "q4415/results/identifier/rrid.ab/results.xml");
	    LOG.debug("output: "+resultsXml);
	    Assert.assertTrue("results", resultsXml.exists());
	}
}

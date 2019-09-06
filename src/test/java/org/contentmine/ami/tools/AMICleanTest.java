package org.contentmine.ami.tools;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMICleanTool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.junit.Test;

import junit.framework.Assert;

/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMICleanTest {
	private static final Logger LOG = Logger.getLogger(AMICleanTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testHelp() {
		new AMICleanTool().runCommands(new String[]{});
	}

	@Test
	/** 
	 * 
	 */
	public void testCleanForestPlotsSmall() throws Exception {
		String args = 
				"-p /Users/pm286/workspace/uclforest/forestplotssmall"
				+ " --dir svg/ pdfimages/ --file scholarly.html"
				;
		AMICleanTool amiCleaner = new AMICleanTool();
		amiCleaner.runCommands(args);
		CProject cProject = amiCleaner.getCProject();
		Assert.assertNotNull("CProject not null", cProject);
	}

	@Test
	/**
	 * tests cleaning directories in a single CTree.
	 */
	public void testCleanSingleTree() {
		String cmd = "-t /Users/pm286/workspace/uclforest/dev/higgins --dir pdfimages";
		new AMICleanTool().runCommands(cmd);
	}

	@Test
	/**
	 * tests cleaning directories in a project for ami-search
	 */
	public void testCleanResults() {
		File targetDir = new File("target/cooccurrence/osanctum200");
		CMineTestFixtures.cleanAndCopyDir(new File("/Users/pm286/workspace/tigr2ess/osanctum200"), targetDir);

		String cmd = "-p " + targetDir + " --dir results cooccurrence";
		new AMICleanTool().runCommands(cmd);
		// delete children of ctrees
		cmd = "-p " + targetDir + ""
			+ " --file "
			+ " gene.human.count.xml"
		    + " gene.human.snippets.xml"
		    + " scholarly.html"
//		    + " search.country.count.xml"
//		    + " search.country.snippets.xml"
//		    + " search.disease.count.xml"
//		    + " search.disease.snippets.xml"
//		    + " search.diterpene.count.xml"
//		    + " search.diterpene.snippets.xml"
//		    + " search.drugs.count.xml"
//		    + " search.drugs.snippets.xml"
//		    + " search.monoterpene.count.xml"
//		    + " search.monoterpene.snippets.xml"
//		    + " search.monoterpenes.count.xml"
//		    + " search.monoterpenes.snippets.xml"
//		    + " search.plantparts.count.xml"
//		    + " search.plantparts.snippets.xml"
//		    + " search.spices.count.xml"
//		    + " search.spices.snippets.xml"
		    + " species.binomial.count.xml"
		    + " species.binomial.snippets.xml"
		    + " word.frequencies.count.xml"
		    + " word.frequencies.snippets.xml";
		new AMICleanTool().runCommands(cmd);
	}

	@Test
	/**
	 * tests cleaning directories in a project for ami-search
	 */
	public void testCleanResultsGlob() {
		File targetDir = new File("target/cooccurrence/osanctum200");
		CMineTestFixtures.cleanAndCopyDir(new File("/Users/pm286/workspace/tigr2ess/osanctum200"), targetDir);
		String cmd;
//		String cmd = "-p " + targetDir + " --dir results cooccurrence";
//		new AMICleanTool().runCommands(cmd);
		// delete children of ctrees
		cmd = "-p " + targetDir + ""
			+ " --fileglob "
			+ " gene.**.xml"
		    + " **/species.*"
		    + " search.*"
		    + " xml";
		new AMICleanTool().runCommands(cmd);
	}


}

package org.contentmine.ami.tools;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.files.CMFileUtilTest;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import nu.xom.Element;

public class AMISummaryTest extends AbstractAMITest {
	
	private static final Logger LOG = LogManager.getLogger(AMISummaryTest.class);
	private static final File TARGET_DIR = new AMISummaryTest().createAbsoluteTargetDir();
	private static final String SUMMARY_CONST = "_summary";
	
	@Test
	public void testSummarizeMethods() {
		String root = "methods";
		String project = "summarizeProject/";
		File expectedDir = new File(TEST_BATTERY10+"."+"expected", project);
		File targetDir = new File(TARGET_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(TEST_BATTERY10, targetDir);
		String cmd = "-vvv"
				+ " -p "+targetDir
				+ " --output " + "/sections/body/"+root
				+ " summary "
				+ " --glob **/PMC*/sections/*_body/*_methods/**/*_p.xml"
				+ " --flatten"
			;
		AMI.execute(cmd);
		AbstractAMITest.compareDirectories(targetDir, expectedDir);
		
		// ami -vvv -p CEVOpen --output /sections/body/manny
		//	 summary --glob **/PMC*/sections/*_body/*_methods/**/*_p.xml --flatten"
	}

	/** extracts the unflattened subtree with a globbed set of leafnodes
	 * This creates a glob'ed list of results files and then creates a subtree of
	 * the project (see target/<project>
	 * Still a prototype
	 * 
	 * */
	@Test
	public void testSummarizeSearchResults() {
		String root = "search";
		String project = "summarizeProject/";
		File expectedDir = new File(TEST_BATTERY10+"."+"expected", project);
		File targetDir = new File("target/"+project);
		CMineTestFixtures.cleanAndCopyDir(TEST_BATTERY10, targetDir);
		String cmd = "-vvv"
				+ " -p "+targetDir
				+ " --output " + "/results/"+root
				+ " summary "
				+ " --glob **/PMC*/results/search/*/results.xml"
//				+ " --merge=1"
			;
		AMI.execute(cmd);
		AbstractAMITest.compareDirectories(targetDir, expectedDir);
		
	}

	/** extracts the flattened subtree of abstracts
	 * and a summary.csv 
	 * 
	 * */
	@Test
	public void testSummarizeAbstracts() {
		String root = "abstract";
		String project = "battery10/";
		File expectedDir = new File(TEST_BATTERY10+"."+"expected", project);
		File targetDir = new File(TARGET_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(TEST_BATTERY10, targetDir);
		String cmd = "-vvv"
				+ " -p "+targetDir
				+ " --output " + "/"+root
				+ " summary "
				+ " --flatten"
				+ " --outtype tab"
				+ " --glob **/PMC*/sections/*_front/*_article-meta/*_abstract.xml"
			;
		AMI.execute(cmd);
		AbstractAMITest.compareDirectories(targetDir, expectedDir);
		
	}

	@Test
	public void testDirectoryTreeElement() {
		File root = new File(TEST_BATTERY10, "PMC3211491");
		Element tree = AMISummaryTool.createDirectoryTree(root);
		String expectedString = "<dir name='PMC3211491'><dir name='pdfimages' /><dir name='results'><dir name='search'><dir name='country' /><dir name='elements' /><dir name='funders' /></dir><dir name='word'><dir name='frequencies' /></dir></dir><dir name='sections'><dir name='0_front'><dir name='0_journal-meta' /><dir name='1_article-meta' /></dir><dir name='1_body'><dir name='0_introduction' /><dir name='1_experimental' /><dir name='2_results_and_discussions' /><dir name='3_conclusion' /><dir name='4_competing_interests' /><dir name='5_authors__contributions' /></dir><dir name='2_back'><dir name='0_acknowledgements' /><dir name='1_ref-list' /></dir><dir name='3_floats-group' /><dir name='figures' /></dir><dir name='svg' /></dir>";
		XMLUtil.assertEqualsCanonically(expectedString, tree);
	}


}

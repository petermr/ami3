package org.contentmine.ami.tools;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.tools.AMISummaryTool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.junit.Test;

public class AMISummaryTest extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(AMISummaryTest.class);
	private static final String SUMMARY_CONST = "_summary";
	private static final File TARGET_SUMMARY = new File(TARGET, "summary");
	
	@Test
	public void testSummarizeMethods() {
		String root = "methods";
		String project = "summarizeProject/";
		File expectedDir = new File(TEST_BATTERY10+"."+"expected", project);
		File targetDir = new File(TARGET_SUMMARY, project);
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
		File targetDir = new File(TARGET_SUMMARY, project);
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


}

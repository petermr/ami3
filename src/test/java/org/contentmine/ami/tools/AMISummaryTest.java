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
	private static final String SUMMARY_CONST = "_summary";
	private static final Logger LOG = LogManager.getLogger(AMISummaryTest.class);
	static File TIGR2ESS = new File("/Users/pm286/workspace/Tigr2essDistrib/tigr2ess");
	private static final File DICTIONARY_EXAMPLES = new File(TIGR2ESS, "dictionaries/examples/");
	static File OSANCTUM200 = new File(TIGR2ESS, "/osanctum200");
	static File OSANCTUM2000 = new File(TIGR2ESS, "scratch/ocimum2019027");

	@Test	
	public void testCommand() {
		File targetDir = new File("target/summary/tigr2ess");
		CMineTestFixtures.cleanAndCopyDir(OSANCTUM200, targetDir);
		String args = 
				"-p "+targetDir
				+ " --word"
				+ " --dictionary country drugs --junk "
				+ " --species binomial"
				+ " --output table"
			;
		new AMISummaryTool().runCommands(args);
	}
	
	@Test
	public void testSummarizeMethods() {
		String root = "methods";
		String project = "summarizeProject/";
		File expectedDir = new File(TEST_BATTERY10+"."+"expected", project);
		File targetDir = new File("target/"+project);
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


}

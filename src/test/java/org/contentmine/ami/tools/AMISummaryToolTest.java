package org.contentmine.ami.tools;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.tools.AMISummaryTool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.junit.Test;

public class AMISummaryToolTest {
	private static final Logger LOG = LogManager.getLogger(AMISummaryToolTest.class);
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
	


}

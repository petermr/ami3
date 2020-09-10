package org.contentmine.ami.tools;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.NAConstants;
import org.junit.jupiter.api.Test;

public class AMIWordsTest extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(AMIWordsTest.class);
	private static final File TARGET_DIR = new AMIWordsTest().createAbsoluteTargetDir();
	

	@Test
	public void testOil5() {
		File targetDir = cleanAndCopyToTarget(OIL5);
		String cmd = ""
				+ " -p " + targetDir
				+ " -vv"
				+ " words"
				+ " --stopwords pmcstop.txt stopwords.txt"
				+ " --methods frequencies "  //wordLengths"
				+ " --stripNumbers"          // works
				+ " --stemming"              // works
				+ " --wordCount 20,40"
			;
		echoAndExecute("-p target/words -vv words --stopwords pmcstop.txt stopwords.txt --methods frequencies  --stripNumbers --stemming --wordCount 20,40", cmd);
	}
	
	
	@Test
	public void testZikaWords() {
		File targetDir = new File("target/words/zika10");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
		System.out.println("target> "+targetDir);
		System.out.println(AMIFixtures.TEST_ZIKA10_DIR);
		String cmd = 
//				"-p /Users/pm286/workspace/cmdev/normami/target/cooccurrence/zika10"
				"-p " + targetDir.getAbsolutePath()
				+ " words" 
				+ " --xpath table=//table-wrap table-td=//table-wrap//td p=//p"
				+ " --methods frequencies wordLengths"
//				+ " --dictionary country disease funders"
			;
		AMI.execute(AMIWordsTool.class, cmd);
		
	}

	@Test
	public void testZikaSearch2() {
		String project = "zika2";
		File rawDir = new File(NAConstants.TEST_AMI_DIR, project);
		File targetDir = new File("target/words/"+project);
		CMineTestFixtures.cleanAndCopyDir(rawDir, targetDir);
		String args = 
				"-p "+targetDir
//				"-t "+new File(targetDir, "PMC2640145")
				+ " -vv"
				+ " words"
			;
		LOG.debug("args "+args);
		AMI.execute(args);
	}


}

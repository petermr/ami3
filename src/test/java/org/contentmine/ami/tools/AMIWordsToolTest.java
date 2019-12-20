package org.contentmine.ami.tools;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.tools.AMITransformTool;
import org.contentmine.ami.tools.AMIWordsTool;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.junit.Test;

public class AMIWordsToolTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(AMIWordsToolTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testOil5() {
//		File testFile = OIL5;
//		String name = testFile.getName();
//		File targetDir = new File("target/cooccurrence/", name);
//		CMineTestFixtures.cleanAndCopyDir(testFile, targetDir);
//		/** need HTML */
//		String args = 
//				"-p /Users/pm286/workspace/cmdev/normami/target/cooccurrence/"+name+"/";
//			;
//		new AMITransformTool().runCommands(args);
//		// transformed norma
		String args = 
				"-h"
//				+ " -p /Users/pm286/workspace/cmdev/normami/target/cooccurrence/"+name+"/"
				+ " -p " + OIL5
				+ " --stopwords pmcstop.txt stopwords.txt"
				+ " --methods frequencies "  //wordLengths"
				+ " --stripNumbers" // works
				+ " --stemming" // works
				+ " --wordCount 20,40"
				+ " -vv"
			;
		new AMIWordsTool().runCommands(args);
	}
	
	
	@Test
	public void testZikaCooccurrence() {
		File targetDir = new File("target/cooccurrence/zika10");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
		String args = 
				"-p /Users/pm286/workspace/cmdev/normami/target/cooccurrence/zika10"
				+ " --dictionary country disease funders"
			;
		new AMIWordsTool().runCommands(args);
	}

}

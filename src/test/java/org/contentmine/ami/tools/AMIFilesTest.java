package org.contentmine.ami.tools;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.cproject.files.CProject;
import org.junit.Test;



/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMIFilesTest extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(AMIFilesTest.class);
	private static final File TARGET_DIR = new AMIFilesTest().createAbsoluteTargetDir();

	@Test
	public void testCopyDir() {
		System.err.println(""+TARGET_DIR);
		String cmd = " assert --type=file " + TARGET_DIR + " foobar";
		AMI.execute(cmd);
		
		cmd = " -vvv "
				+ " -p "+TARGET_DIR
				+ " -o target/junk/summary1/"
				+ " summary "
						+ " --flatten"
//						+ " --outtype tab"
						+ " --glob "+TARGET_DIR
					;
		AMI.execute(cmd);
		System.err.println("===============aummarize Zika====================");
		cmd = " -vvv "
				+ " -p "+AMIFixtures.TEST_ZIKA10_DIR
				+ " -o " + TARGET_DIR.getAbsolutePath()+"/junk/summary1x/"
				+ " summary "
						+ " --flatten"
//						+ " --outtype tab"
						+ " --glob " + " **/PMC*/"
					;
		AMI.execute(cmd);
		System.err.println("===================================");

		CProject cProject = new CProject(AMIFixtures.TEST_ZIKA10_DIR);
		cmd = " -vvv"
				+ " -i "+cProject
				+ " -o " +TARGET_DIR.getAbsolutePath()+"/junk/summary2x/"
				+ " files"
				+ " --copydir "
				;
		AMI.execute(cmd);
	}
	
}

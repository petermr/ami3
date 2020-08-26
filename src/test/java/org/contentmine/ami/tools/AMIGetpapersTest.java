package org.contentmine.ami.tools;

import org.apache.logging.log4j.Logger;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIGetpapersTool;
import org.contentmine.ami.tools.AMIOCRTool;
import org.junit.Test;

/** test OCR.
 * 
 * @author pm286
 *
 */
public class AMIGetpapersTest extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(AMIGetpapersTest.class);
	private static final File TARGET_DIR = new AMIGetpapersTest().createAbsoluteTargetDir();

	@Test
	/** 
	 * run query
	 */
	public void testZika() throws Exception {
		String args = 
				"-p /Users/pm286/workspace/projects/zika"
				+ " --query zika"
				+ " --limit 100"
			;
		if (1 == 1) throw new RuntimeException("Not yet running");
		
		new AMIGetpapersTool().runCommands(args);
	}

}

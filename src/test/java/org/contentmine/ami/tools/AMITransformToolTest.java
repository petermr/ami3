package org.contentmine.ami.tools;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.tools.AMITransformTool;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.junit.Test;

public class AMITransformToolTest {
	private static final Logger LOG = Logger.getLogger(AMITransformToolTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** runs NormaTransformer
	 * 
	 */
	@Test
	public void testZikaScholarlyHtml() {
		File targetDir = new File("target/cooccurrence/zika10");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, targetDir);
		String args = 
				"-p /Users/pm286/workspace/cmdev/normami/target/cooccurrence/zika10/"
			;
		new AMITransformTool().runCommands(args);
	}
	
	
}

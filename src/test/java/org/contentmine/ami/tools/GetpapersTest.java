package org.contentmine.ami.tools;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIGrobidTool;
import org.contentmine.ami.tools.AMIOCRTool;
import org.junit.Test;

/** test OCR.
 * 
 * @author pm286
 *
 */
public class GetpapersTest {
	private static final Logger LOG = LogManager.getLogger(GetpapersTest.class);
@Test
	/** 
	 * smoke test
	 */
	public void testGetpapers() throws Exception {
		String args = 
				"-p /Users/pm286/workspace/uclforest/"
				+ " --out "
				+ " --exe processFullText"
			;
		new AMIGrobidTool().runCommands(args);
	}

	@Test
	/** 
	 * convert single (missing) file
	 */
	public void testGROBIDDietrichson() throws Exception {
		String args = 
				"-t /Users/pm286/workspace/uclforest/dev/dietrichson"
				+ " --basename tei/"
				+ " --exe processFullText"
			;
		new AMIGrobidTool().runCommands(args);
	}

	@Test
	/** 
	 * convert whole project
	 */
	public void testGROBIDProject() throws Exception {
		String args = 
				"-p /Users/pm286/workspace/uclforest/dev/"
				+ " --basename tei/"
				+ " --exe processFullText"
			;
		new AMIGrobidTool().runCommands(args);
	}
}

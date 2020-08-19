package org.contentmine.ami.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DirectoryDeleter;
import org.contentmine.cproject.files.Unzipper;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.junit.Assert;
import org.junit.Test;

/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMIDisplayTest extends AbstractAMITest {
	
	private static final File TARGET_DIR = new AMIDisplayTest().createTargetDir();
	static final Logger LOG = LogManager.getLogger(AMIDisplayTest.class);
	
	@Test
	public void testHelp() {
		new AMIDisplayTool().runCommands(new String[]{});
	}

	@Test
	/** this effectively runs
	 * ami -p <targetDir> search --dictionary country and then datatables again
	 */
	public void testDataTables() {
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, TARGET_DIR);
		String args = 
				" -p "+TARGET_DIR
	//			+ " -v"
				+ " search"
				+ " --dictionary "
				+ " country disease"
			;
		AMI.execute(args);

		// needs -p to root it
		args = " -vvv "
				+ " -p "+TARGET_DIR
				+ " clean"
				+ " **/full.dataTables.html"
				;
		AMI.execute(args);
		
		args = " -v -p "+TARGET_DIR 
				+ " display"
				+ " --datatables"
				;
		AMI.execute(args);
	}

	@Test
	/** this effectively runs
	 * ami -p <targetDir> search --dictionary country and then datatables again
	 */
	public void testCooccurrence() {
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_ZIKA10_DIR, TARGET_DIR);
		String args = 
				" -p "+TARGET_DIR
	//			+ " -v"
				+ " search"
				+ " --dictionary "
				+ " country disease"
			;
//		AMI.execute(args);

		// needs -p to root it
		args = " -vvv "
				+ " -p "+TARGET_DIR
				+ " clean"
				+ " **/__cooccurrence/"
				;
		AMI.execute(args);
		
		args = " -v -p "+TARGET_DIR 
				+ " display"
				+ " --cooccurrence --facets=country,disease"
				;
		AMI.execute(args);
	}

}

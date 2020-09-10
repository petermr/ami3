package org.contentmine.ami.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DirectoryDeleter;
import org.contentmine.cproject.files.Unzipper;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMIDisplayTest extends AbstractAMITest {
	
	static final Logger LOG = LogManager.getLogger(AMIDisplayTest.class);
	private static final File TARGET_DIR = new AMIDisplayTest().createAbsoluteTargetDir();
	
	@Test
	public void testHelp() {
		new AMIDisplayTool().runCommands(new String[]{});
	}

	@Test
	public void testTarget() {
		Assert.assertTrue(TARGET_DIR.toString().endsWith("ami3/target/display"));
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
		File file = new File(TARGET_DIR, "full.dataTables.html");
		Assert.assertTrue(file.exists());
		long size = FileUtils.sizeOf(file);
		Assert.assertTrue("size "+size, size == 24809);
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
		File __cooccurrence = new File(TARGET_DIR, "__cooccurrence");
		File allPlots = new File(__cooccurrence, "allPlots.svg");
		Assert.assertTrue(allPlots.exists());
		long size = FileUtils.sizeOf(allPlots);
		Assert.assertTrue("size "+size, size == 468116);
	}

}

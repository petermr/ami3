package org.contentmine.ami.tools;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class AMIAssertTest extends AbstractAMITest {

	private static final Logger LOG = LogManager.getLogger(AMIAssertTest.class);
//	private static final File TARGET_DIR = new AMIAssertTest().createAbsoluteTargetDir();

	String cmd;
@Test
	public void testAssertHelp() {
		cmd = " assert --help";
		AMI.execute(cmd);
	}

	@Test
	public void testAssertDir() {
		File dir = TEST_BATTERY10;
		cmd = " -vvv "
				+ "-t " + new File(dir, "PMC3211491")
			+ " assert "
			+ " --type=dir"
			+ " --count=15,17"
			+ " --message=number_of_Files_in_directory "
			;
		AMI.execute(cmd);
	}
	
	@Test
	public void testAssertFile() {
		File dir = TEST_BATTERY10;
		cmd = " -vvv "
				+ " -i fulltext.pdf"
				+ " -t " + new File(dir, "PMC3211491")
			+ " assert "
			+ " --type=file"
			+ " --size=8543000,8544000 "
			+ " --message=size_of_file "
			;
		AMI.execute(cmd);
	}

	@Test
	public void testAssertDirTree() {
		File dir = TEST_BATTERY10;
		File expected = new File(TEST_BATTERY10_EXPECTED, "PMC3211491/sortedtree.xml");
		cmd = " -vvv "
				+ " -t " + new File(dir, "PMC3211491")
			+ " assert "
			+ " --type=dirtree"
			+ " --dirtree=" + expected
			+ " --message=directory_tree "
			;
		AMI.execute(cmd);
	}

	@Test
	public void testAssertDirGlobTree() {
		File dir = new File(TEST_BATTERY10, "PMC3211491/");
		cmd = " -vvv "
			+ " -t " + dir
			+ " assert "
			+ " --type=dir"
			+ " --glob=**/results.xml"
			+ " --count=3"
			+ " --message=results.xml_count "
			;
		AMI.execute(cmd);
	}

	@Test
	public void testAssertDirGlobProject() {
		File dir = TEST_BATTERY10;
		cmd = " -vvv "
			+ " -p " + dir
			+ " assert "
			+ " --type=dir"
			+ " --glob=**/results.xml"
			+ " --count=99"
			+ " --message=results.xml_count "
			;
		AMI.execute(cmd);
	}

}

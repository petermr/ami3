package org.contentmine.ami.tools;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.cproject.files.CProject;
import org.junit.jupiter.api.Test;

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
	
	@Test
	/**
	 * the first command creates files of the form:
	 * cTree
	 * ..text
	 * ....introduction_d.txt
	 * ....methods_d.txt
	 * where d is number
	 */
	public void testXPathSnippetsTestEncoded() {
		CProject cProject = new CProject(AMIFixtures.TEST_ZIKA10_DIR);
		String cmd = ""
		+ " -vvv -p " + cProject.getDirectory() 
		+ " --output text"
		+ " section"
		+ " --xpath introduction=%2F%2F%2A%5Btitle%3D%27Introduction%27%5D~"
		+ "method=%2F%2F%2A%5Bcontains%28title%2C%27Methods%27%29%5D";
//		AMI.execute(cmd);
		
		cmd = ""
		+ " -vvv -p " + cProject.getDirectory() 
		+ " assert"
		+ " --type=dir"
		+ " --glob **/text/*.txt"
		+ " --filecount 99"
		;
		AMI.execute(cmd);
		
		
	}



}

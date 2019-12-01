package org.contentmine.ami.tools;

import java.io.File;
import java.io.InputStream;

import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineUtil;
import org.junit.Assert;
import org.junit.Test;

public class AbstractAMITest {
	
	public static File _HOME = new File("/Users/pm286");
	public static File CMDEV = new File(_HOME, "workspace/cmdev");
	public static File SRC_TEST_AMI = new File(CMDEV, "ami3/src/test/resources/org/contentmine/ami");
	public static File OIL5 = new File(SRC_TEST_AMI, "oil5/");
	public static File PROJECTS = new File(_HOME, "projects/");
	public static File CEV_SEARCH = new File(PROJECTS, "CEVOpen/searches/");
	public static File OIL186 = new File(CEV_SEARCH, "oil186/");
	public static File OIL1000 = new File(CEV_SEARCH, "oil1000/");
	public static CProject OIL186_PROJ = new CProject(OIL186);
	public static File CLIM_SEARCH = new File(PROJECTS, "climate/searches/");
	public static File CMIP200 = new File(CLIM_SEARCH, "cmip200/");

	
	@Test
	public void testPython() throws Exception {
//		ProcessBuilder builder = new ProcessBuilder("java", "-version");
		String[] args = {"java", "-version"};
		CMineUtil.runProcess(args, (InputStream) null);
		
		args = new String[]{"python", "--help"};
		CMineUtil.runProcess(args, (InputStream) null);
		
//		args = new String[]{"node", "--help"};
//		CMineUtil.runProcess(args, (InputStream) null);
		
		args = new String[]{"bashx", "-xx"};
		CMineUtil.runProcess(args, (InputStream) null);
		
	}
	
	@Test
	public void testRelativeFile() {
		CProject cProject = new CProject(OIL5);
		AMISearchTool tool = new AMISearchTool(cProject);
		File file = tool.getFileRelativeToProject("../zika10");
		Assert.assertNotNull("file", file);
		file = tool.getFileRelativeToProject("../oil186");
		Assert.assertNull("file", file);
		file = tool.getFileRelativeToProject("/Users/");
		Assert.assertNotNull("file", file);
	}
}

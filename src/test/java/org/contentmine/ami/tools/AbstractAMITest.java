package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineGlobber;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.Test;

import nonapi.io.github.classgraph.utils.FileUtils;

public abstract class AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(AbstractAMITest.class);

	public static File _HOME = new File("/Users/pm286");
	public static final File NEW_PROJECTS = new File(_HOME, "projects/");
	public static File CMDEV = new File(_HOME, "workspace/cmdev");
	public static final String CONTENTMINE = "ami3/src/test/resources/org/contentmine";

	public static File CMINE = new File(CMDEV, CONTENTMINE);
	public static File SRC_TEST_AMI = new File(CMINE, "ami");
	public static File SRC_TEST_GRAPHICS = new File(CMINE, "graphics");
	public static File SRC_TEST_SVG = new File(SRC_TEST_GRAPHICS, "svg");
	public static File SRC_TEST_PLOT = new File(SRC_TEST_SVG, "plot");
	public static File SRC_TEST_TOOLS = new File(SRC_TEST_AMI, "tools");
	public static File SRC_TEST_DOWNLOAD = new File(SRC_TEST_TOOLS, "download");
	public static File PDF2SVG2 = new File(SRC_TEST_AMI, "pdf2svg2");
	public static File OIL5 = new File(SRC_TEST_AMI, "oil5/");
	
	public static final File TEST_BATTERY10 = new File(SRC_TEST_AMI, "battery10");
	public static final File TEST_BATTERY10COMPUTE = new File(SRC_TEST_AMI, "battery10compute");
	public static final File TEST_DICTIONARY = new File(SRC_TEST_AMI, "dictionary");
	
	public static File WORKSPACE = new File(_HOME, "workspace/");
	public static File PROJECTS = WORKSPACE.exists() ? new File(WORKSPACE, "projects/") : NEW_PROJECTS;
	public static File CANADA = new File(PROJECTS, "canada/");
	public static File CEV_OPEN = new File(PROJECTS, "CEVOpen/").exists() ? new File(PROJECTS, "CEVOpen/") : new File(PROJECTS, "CEV/");
	public static File CEV_SEARCH = new File(CEV_OPEN, "searches/");
	public static File CLIMATE = new File(PROJECTS, "climate/");
	public static File CLIM_SEARCH = new File(CLIMATE, "searches/");
	public static File CLIMATE200 = new File(CLIMATE, "climate200");
	public static File CLIMATE200SECTIONS = new File(CLIMATE, "climate200sections");
	public static File CMIP200 = new File(CLIM_SEARCH, "cmip200/");
	
	public static File OIL186 = new File(CEV_SEARCH, "oil186/");
	public static File OIL1000 = new File(CEV_SEARCH, "oil1000/");
	public static CProject OIL186_PROJ = new CProject(OIL186);
	
	protected CProject cProject;
	protected CTree cTree;
	protected File outputFile;
	protected File svgFile;
	File globTopDir;
	List<File> globbedFiles;

	
	@Test
	public void testPython() throws Exception {
//		ProcessBuilder builder = new ProcessBuilder("java", "-version");
		String[] args = {"java", "-version"};
		CMineUtil.runProcess(args, (InputStream) null);
		
		args = new String[]{"python", "--help"};
		CMineUtil.runProcess(args, (InputStream) null);
		
//		args = new String[]{"node", "--help"};
//		CMineUtil.runProcess(args, (InputStream) null);
		
//		args = new String[]{"bashx", "-xx"};
		args = new String[]{"bash", "-xx"};
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

	protected AbstractAMITest setAMITestProjectName(String projectName) {
		File cProjectDir = new File(SRC_TEST_AMI, projectName);
		if (!cProjectDir.exists() || !cProjectDir.isDirectory()) {
			throw new RuntimeException("not a project directory: "+cProjectDir);
		}
		this.cProject = new CProject(cProjectDir);
		return this;
	}

	private void checkCProject() {
		if (cProject == null) {
			throw new RuntimeException("missing project directory: ");
		}
	}

	protected AbstractAMITest setTreeName(String treeName) {
		checkCProject();
		cTree = cProject.getCTreeByName(treeName);
		checkCTree();
		return this;
	}

	protected void checkCTree() {
		if (cTree == null) {
			throw new RuntimeException("missing cTree directory: ");
		}
	}

	protected AbstractAMITest assertTrue(String msg, boolean condition) {
		Assert.assertTrue(msg, condition);
		return this;
	}

	protected AbstractAMITest assertEquals(String msg, Object expected, Object actual) {
		Assert.assertEquals(msg, expected, actual);
		return this;
	}

	protected AbstractAMITest assertCanReadFile(String msg, File file, long minFileSize) {
		Assert.assertNotNull(msg + "; cannot read file " + file, file);
		try {
//			Files.isReadable(file.toPath()) && Files.isDirectory(path, options);
			FileUtils.checkCanReadAndIsFile(file);
			Assert.assertTrue(Files.size(file.toPath()) > minFileSize);
		} catch (IOException e) {
			Assert.assertFalse(msg + "; cannot read file " + e.getMessage(), true);
		}
		return this;
	}

	public File getSVGFile() {
		return svgFile;
	}

	protected List<File> runFileGlob(String fileGlob) {
		globTopDir = cTree != null ? cTree.getDirectory() : (cProject == null ? null : cProject.getDirectory());
		globbedFiles = CMineGlobber.listGlobbedFilesQuietly(globTopDir, fileGlob);
		LOG.warn("globbed "+globbedFiles.size()+"/"+globbedFiles);
		return globbedFiles;
	}

}

package org.contentmine.cproject.files;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.CProjectArgProcessor;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.metadata.AbstractMetadata.Type;
import org.contentmine.cproject.metadata.quickscrape.QuickscrapeMD;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Element;


public class CTreeTest {

	private static final String TEST_CREATE = "target/testcreate";
	private static final String TARGET_THESES = TEST_CREATE+"/theses/";
	
	private static final Logger LOG = Logger.getLogger(CTreeTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static File PLOS0115884_DIR = new File(CMineFixtures.TEST_FILES_DIR, "journal.pone.0115884");
	
	@Test
	public void testReadCTree() {
		CTree cTree = new CTree();
		Assert.assertTrue("exists: "+PLOS0115884_DIR, PLOS0115884_DIR.exists());
		cTree.readDirectory(PLOS0115884_DIR);
		Assert.assertEquals("fileCount", 4, cTree.getReservedFileList().size());
		Assert.assertTrue("XML", cTree.hasExistingFulltextXML());
	}
	
	@Test
	/** creates a new CTree for a PDF.
	 * 
	 */
	public void testCreateCTree() throws IOException {
		if (!TestUtil.checkForeignDirExists(CMineFixtures.TEST_MISC_DIR)) return;
		File cTreeDirectory = new File(TEST_CREATE+"/test_pdf_1471_2148_14_70_pdf");
		if (cTreeDirectory.exists()) FileUtils.forceDelete(cTreeDirectory);
		String args = "-i "+CMineFixtures.TEST_MISC_DIR+"/test_pdf_1471-2148-14-70.pdf  -o target/testcreate/ --ctree";
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		Assert.assertTrue(cTreeDirectory.exists());
		CTree cTree = new CTree(cTreeDirectory); 
		File fulltext_pdf = cTree.getExistingFulltextPDF();
		Assert.assertTrue(fulltext_pdf.exists());
	}

	@Test
	public void testCreateCTreesFromProject() throws IOException {
		File project1 = new File(CMineFixtures.TEST_PROJECTS_DIR, "project1");
		File targetProject1 = new File("target/projects/project1");
		FileUtils.copyDirectory(project1, targetProject1);
		String args = " --project "+targetProject1;
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		CTreeList cTreeList = argProcessor.getCTreeList();
		Assert.assertEquals("ctrees", 2, cTreeList.size());
		
	}

	@Test
	/** creates new CTrees for list of PDF.
	 * 
	 * SHOWCASE
	 * 
	 * takes single directory with several child PDFs and transforms them into child CTrees 
	 * with "fulltext.pdf" in each.
	 */
	public void testCreateCTreesUsingCTreeCommand() throws IOException {
		if (!TestUtil.checkForeignDirExists(CMineFixtures.TEST_MISC_DIR)) return;
		File inputDir = new File(CMineFixtures.TEST_MISC_DIR, "theses/");
		File outputDir = new File(TARGET_THESES);
		if (outputDir.exists()) FileUtils.forceDelete(outputDir);
		Assert.assertFalse(outputDir.exists());
		
		String args = "-i "+inputDir+" -e pdf -o "+outputDir+" --ctree";
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		Assert.assertTrue(outputDir.exists());
		File[] files = outputDir.listFiles();
		Arrays.sort(files);
		Assert.assertEquals(2, files.length);
		Assert.assertEquals("target:"+TARGET_THESES+"HalThesis1_pdf", 
				TARGET_THESES+"HalThesis1_pdf", files[0].getPath());
		File[] childFiles = files[0].listFiles();
		Assert.assertEquals(1, childFiles.length);
		Assert.assertEquals("fulltext.pdf", childFiles[0].getName());
		CTreeList ctreeList = argProcessor.getCTreeList();
		Assert.assertEquals("ctrees", 2, ctreeList.size());
	}
	
	@Test
	/** creates new CTrees for list of PDF.
	 * 
	 * SHOWCASE
	 * 
	 * takes several PDFs and transforms them into a project with child CTrees 
	 * with "fulltext.pdf" in each.
	 */
	public void testCreateCTreesUsingProject() throws IOException {
		if (!TestUtil.checkForeignDirExists(CMineFixtures.TEST_MISC_DIR)) return;
		File inputDir = new File(CMineFixtures.TEST_MISC_DIR, "theses/");
		File projectDir = new File(TARGET_THESES);
		if (projectDir.exists()) FileUtils.forceDelete(projectDir);
		Assert.assertFalse(projectDir.exists());
		
		String args = "-i "+inputDir+" -e pdf --project "+projectDir;
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		Assert.assertTrue(projectDir.exists());
		File[] files = projectDir.listFiles();
		Arrays.sort(files);
		Assert.assertEquals(2, files.length);
		Assert.assertEquals(projectDir+"/HalThesis1_pdf", files[0].getPath());
		File[] childFiles = files[0].listFiles();
		Assert.assertEquals(1, childFiles.length);
		Assert.assertEquals("fulltext.pdf", childFiles[0].getName());
		CTreeList ctreeList = argProcessor.getCTreeList();
		Assert.assertEquals("ctrees", 2, ctreeList.size());
	}
	
	
	
	@Test
	public void testCTreeContent1() {
		CProject cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project1"));
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
		CTree cTree1 = cTreeList.get(0);
		cTree1.getOrCreateFilesDirectoryCTreeLists();
		List<File> allChildDirectoryList = cTree1.getAllChildDirectoryList();
		Assert.assertEquals("all child dir", 0, allChildDirectoryList.size());
		List<File> allChildFileList = cTree1.getAllChildFileList();
		Assert.assertEquals("all child file", 1, allChildFileList.size());

	}
	
	@Test
	public void testGlobFileList() {
		File pmc4417228 = new File(CMineFixtures.TEST_PROJECTS_DIR, "project2/PMC4417228/");
		CTree cTree = new CTree(pmc4417228);
		// NOTE: The "**" is required
		CTreeFiles cTreeFiles = cTree.extractCTreeFiles("**/fulltext.*");
		Assert.assertEquals(2,  cTreeFiles.size());
		// sorting problem
//		Assert.assertEquals(CHESConstants.SRC_TEST_TOP + /files/projects/project2/PMC4417228/fulltext.pdf",  
//				fileList.get(0).toString());
//		Assert.assertEquals(CHESConstants.SRC_TEST_TOP + /files/projects/project2/PMC4417228/fulltext.xml",  
//				fileList.get(1).toString());
	}
	
	
	@Test
	public void testGlobFileListAndXML() {
		File pmc4417228 = new File(CMineFixtures.TEST_PROJECTS_DIR, "project2/PMC4417228/");
		CTree cTree = new CTree(pmc4417228);
		SnippetsTree xpathSnippetsTree = cTree.extractXPathSnippetsTree("**/fulltext.xml", "//kwd");
		Assert.assertEquals(1, xpathSnippetsTree.size());
		XMLSnippets snippets0 = xpathSnippetsTree.get(0);
		Assert.assertEquals(10, snippets0.size());
		Assert.assertEquals("Central Europe", snippets0.getValue(0));
		Assert.assertEquals("Habitats", snippets0.getValue(4));
		Assert.assertEquals("Sustainability", snippets0.getValue(9));
	}
	
	@Test
	/** this also creates an output file (--o)
	 * 
	 */
	public void testGlobFileAndXpathCommand() throws IOException {
		File targetDir = new File("target/glob/pmc4417228");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.TEST_PROJECTS_DIR, "project2/PMC4417228"), targetDir);
		String output = "snippets.xml";
		String args = " -q " + targetDir+" --filter file(**/fulltext.xml)xpath(//kwd) -o "+output;
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		File snippetsFile = argProcessor.getOutputFile();
		Assert.assertNotNull("snippetsFile not null", snippetsFile);
		Assert.assertTrue("snippets", snippetsFile.exists());
		Element element = XMLUtil.parseQuietlyToDocument(snippetsFile).getRootElement();
		String elementXML = element.toXML().replaceAll("\\n", "");
		Assert.assertTrue(elementXML.startsWith("<snippetsTree> <snippets file=\"target/glob/pmc4417228/fulltext.xml\">  <kwd>Central Europe</kwd>"));
		/**
<snippetsList>
 <snippets file="/Users/pm286/workspace/cproject/target/glob/pmc4417228/fulltext.xml">
  <kwd>Central Europe</kwd>
  <kwd>DPSIR framework</kwd>
  <kwd>Ecosystem functions</kwd>
  <kwd>Ecosystem regeneration</kwd>
  <kwd>Habitats</kwd>
  <kwd>Resource management</kwd>
  <kwd>Traditional ecological knowledge</kwd>
  <kwd>Village laws</kwd>
  <kwd>16-19
   <sup>th</sup> centuries
  </kwd>
  <kwd>Sustainability</kwd>
 </snippets>
</snippetsList>
*/
		Element snippets = XMLUtil.getSingleElement(element, XMLSnippets.SNIPPETS);
		String fileString = snippets.getAttributeValue(XMLSnippets.FILE);
		Assert.assertNotNull("file att", fileString);
		Assert.assertTrue("snippets file: "+fileString, 
				fileString.endsWith("target/glob/pmc4417228/fulltext.xml"));
		List<Element> kwdList = XMLUtil.getQueryElements(element, "snippets/kwd");
		Assert.assertEquals("snippets content", 10, kwdList.size());
	}

	@Test
	public void testGlobResultsAndXpathCommand() throws IOException {
		File targetDir = new File("target/glob/project3/ctree1");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.TEST_PROJECTS_DIR, "project3/ctree1"), targetDir);
		String output = "snippets.xml";
		String args = " -q " + targetDir+" --filter file(**/results.xml)xpath(//result) -o "+output;
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		CTree cTree = argProcessor.getCTree();
		Assert.assertNotNull("ctree", cTree);
		// there are two results.xml
		SnippetsTree snippetsTree = cTree.getSnippetsTree();
		Assert.assertNotNull("snippetsTree not null", snippetsTree);
		Assert.assertEquals("snippets", 2, snippetsTree.size());
	}
	
	@Test
	public void testGlobFileListMediumCommand() throws IOException {
		if (!TestUtil.checkForeignDirExists(CMineFixtures.TEST_MISC_DIR)) return;
		File targetDir = new File("target/patents/US08979/US08979000-20150317/");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.TEST_MISC_DIR, "patents/US08979/US08979000-20150317/"), targetDir);
		String args = "-i scholarly.html --filter file(**/*) --ctree "+targetDir+" -o files.xml";
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		CTree cTree = argProcessor.getCTree();
		CTreeFiles cTreeFiles = cTree.getCTreeFiles();
		Assert.assertNotNull(cTreeFiles);
		Assert.assertEquals(13,  cTreeFiles.size());
		/**
		Assert.assertEquals("treeFiles", 
				"<cTreeFiles cTree=\"target/patents/US08979/US08979000-20150317\">"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/fulltext.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/gene/hgnc/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/consort0/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/plasmid/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/synbio/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/hgnc/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/synbio/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/synbioPhrases/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/species/binomial/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/species/genus/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/word/frequencies/results.html\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/word/frequencies/results.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/scholarly.html\" />"
				+ "</cTreeFiles>",
				
				cTreeFiles.toString()
				);
				*/

		cTreeFiles.sort();
		/*
		Assert.assertEquals("treeFiles", 
				"<cTreeFiles cTree=\"target/patents/US08979/US08979000-20150317\">"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/fulltext.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/gene/hgnc/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/consort0/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/plasmid/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/regex/synbio/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/hgnc/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/synbio/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/search/synbioPhrases/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/species/binomial/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/species/genus/empty.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/word/frequencies/results.html\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/results/word/frequencies/results.xml\" />"
				+ "<file name=\"target/patents/US08979/US08979000-20150317/scholarly.html\" />"
				+ "</cTreeFiles>",
				
				cTreeFiles.toString()
				);
				*/
	}

	/** this is messy because the metadata files are the old sort.
	 * we can overcome this by using the oldVersion
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetReservedFiles() throws IOException {
		File cProjectDir = CMineFixtures.TEST_SAMPLE;
		CProject cProject = new CProject(cProjectDir);
		Assert.assertEquals(5, cProject.getOrCreateCTreeList().size());
		QuickscrapeMD quickscrapeMD = new QuickscrapeMD();
		quickscrapeMD.setVersion(QuickscrapeMD.OLD_VERSION);
		CTreeExplorer explorer = new CTreeExplorer().setFilename(quickscrapeMD.getCTreeMetadataFilename());
		CTreeList cTreeList = cProject.getCTreeList(explorer);
		Assert.assertEquals(3, cTreeList.size());
		CTreeList epmc = cProject.getCTreeList(new CTreeExplorer().setFilename(Type.EPMC.getCTreeMDFilename()));
		Assert.assertEquals(2, epmc.size());
		Assert.assertEquals(3, cProject.getCTreeList(new CTreeExplorer().setFilename(quickscrapeMD.getCTreeMetadataFilename())).size());
		Assert.assertEquals(1, cProject.getCTreeList(new CTreeExplorer().setFilename(CTree.FULLTEXT_PDF)).size());
		CTreeList xml = cProject.getCTreeList(new CTreeExplorer().setFilename(CTree.FULLTEXT_XML));
		Assert.assertEquals(2, xml.size());
		Assert.assertEquals(1, cProject.getCTreeList(new CTreeExplorer().setFilename(CTree.SCHOLARLY_HTML)).size());
		CTreeList epmcAndXml = epmc.and(xml);
		Assert.assertEquals(2,  epmcAndXml.size());
		Assert.assertEquals(2,  epmc.or(xml).size());
		
	}

	/** problem. 
	 * CTrees are normally created with child files such as fulltext.xml and these are a criterion for being a CTree.
	 * Increasingly directories are being created without child files (e.g. from crossref metadata) and we should
	 * allow them to be designated as CTrees.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCTreesWithoutReservedChildFiles() throws IOException {
		File cProjectDir = new File(CMineFixtures.TEST_DOWNLOAD_DIR, "mini");
		File targetDir = new File("target/mini");
		FileUtils.deleteDirectory(targetDir);
		FileUtils.copyDirectory(cProjectDir, targetDir);
		CProject cProject = new CProject(targetDir);
		cProject.setTreatAllChildDirectoriesAsCTrees(true);
		Assert.assertEquals(3, cProject.getOrCreateCTreeList().size());
		cProject = new CProject(targetDir);
		cProject.setTreatAllChildDirectoriesAsCTrees(false);
		// because of the child files
		Assert.assertEquals(2, cProject.getOrCreateCTreeList().size());
	}

	@Test
	public void testNormalizeDOIs() throws IOException {
		CProject cProject = createMiniTargetAndCProject();
		List<File> dirList = cProject.getOrCreateCTreeList().getCTreeDirectoryList();
		Assert.assertEquals("["
				+ "target/mini1/PMC4678086,"
				+ " target/mini1/http_dx.doi.org_10.1001_jama.2016.7992,"
				+ " target/mini1/http_dx.doi.org_10.1007_s13201-016-0429-9"
				+ "]",
				dirList.toString());
				
		cProject.setTreatAllChildDirectoriesAsCTrees(true);
		cProject.normalizeDOIBasedDirectoryCTrees();
		List<File> dirList1 = cProject.getOrCreateCTreeList().getCTreeDirectoryList();
		Assert.assertEquals("[target/mini1/10.1001_jama.2016.7992, target/mini1/10.1007_s13201-016-0429-9, target/mini1/PMC4678086]", 
				dirList1.toString());
		
	}
	
	@Test
	public void testGetDOIPrefixList() throws IOException {
		CProject cProject = CTreeTest.createMiniTargetAndCProject();
		cProject.setTreatAllChildDirectoriesAsCTrees(true);
		cProject.normalizeDOIBasedDirectoryCTrees();
		List<String> doiPrefixList = cProject.getDOIPrefixList();
		Assert.assertEquals("[10.1001, 10.1007, null]", doiPrefixList.toString());
	}
	
	

	// ====== create tidy cProject
	public static CProject createMiniTargetAndCProject() {
		File cProjectDir = new File(CMineFixtures.TEST_DOWNLOAD_DIR, "mini");
		File targetDir = new File("target/mini1");
		try {
			FileUtils.deleteDirectory(targetDir);
			FileUtils.copyDirectory(cProjectDir, targetDir);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		CProject cProject = new CProject(targetDir);
		return cProject;
	}

}

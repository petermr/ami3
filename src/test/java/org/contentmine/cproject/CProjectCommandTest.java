package org.contentmine.cproject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.metadata.quickscrape.QSRecord;
import org.contentmine.cproject.metadata.quickscrape.QuickscrapeLog;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.cproject.util.Utils;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Multimap;

import nu.xom.Element;

/** tests commands under the 'cproject' command
 * 
 * @author pm286
 *
 */
public class CProjectCommandTest {

	public static final Logger LOG = Logger.getLogger(CProjectCommandTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/** EXTRACTS URLS FROM GETPAPERS AND SHUFFLES THEM.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCrossrefUnshuffledURLS() throws IOException {
		File targetDir = new File(CMineFixtures.GETPAPERS_TARGET, "unshuffled");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.GETPAPERS_SRC, "unshuffled"), targetDir);
		String cmd = "--project "+targetDir.toString()+" --outUrls urls.txt --metadataType crossref";
		new CProject().run(cmd);
		File urls = new File(targetDir, "urls.txt");
		Assert.assertTrue(urls.exists());
		List<String> lines = FileUtils.readLines(urls);
		Assert.assertEquals(30,  lines.size());
		Assert.assertEquals("["
				+ "http://dx.doi.org/10.1002/adaw.30456,"
				+ " http://dx.doi.org/10.1007/s00332-016-9284-y,"
				+ " http://dx.doi.org/10.1007/s00300-016-1897-y,"
				+ " http://dx.doi.org/10.1007/s00294-016-0568-4,"
				+ " http://dx.doi.org/10.1002/acs.2674,"
				+ " http://dx.doi.org/10.1016/j.ijpe.2016.01.022,"
				+ " http://dx.doi.org/10.1007/s00294-016-0564-8,"
				+ " http://dx.doi.org/10.1515/tjj-2015-0056,"
				+ " http://dx.doi.org/10.1002/acs.2662,"
				+ " http://dx.doi.org/10.1007/s00291-015-0429-4,"
				+ " http://dx.doi.org/10.1016/j.ijpe.2016.01.021,"
				+ " http://dx.doi.org/10.1007/s0028",
				lines.toString().substring(0, 500));
	}

	
	/** EXTRACTS URLS FROM GETPAPERS AND SHUFFLES THEM.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCrossrefShuffledURLS() throws IOException {
		File targetDir = new File(CMineFixtures.GETPAPERS_TARGET, "unshuffled");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.GETPAPERS_SRC, "unshuffled"), targetDir);
		String cmd = "--project "+targetDir.toString()+" --outUrls urls.txt shuffle --metadataType crossref";
		CProject cproject = new CProject();
		cproject.run(cmd);
		File urls = new File(targetDir, "urls.txt");
		Assert.assertTrue(urls.exists());
		List<String> lines = FileUtils.readLines(urls);
		Assert.assertEquals(30,  lines.size());
		Assert.assertEquals("["
				+ "http://dx.doi.org/10.1002/adaw.30456,"
				+ " http://dx.doi.org/10.1007/s00332-016-9284-y,"
				+ " http://dx.doi.org/10.1007/s00300-016-1897-y,"
				+ " http://dx.doi.org/10.1007/s00294-016-0568-4,"
				+ " http://dx.doi.org/10.1002/acs.2674,"
				+ " http://dx.doi.org/10.1016/j.ijpe.2016.01.022,"
				+ " http://dx.doi.org/10.1007/s00294-016-0564-8,"
				+ " http://dx.doi.org/10.1515/tjj-2015-0056,"
				+ " http://dx.doi.org/10.1002/acs.2662,"
				+ " http://dx.doi.org/10.1007/s00291-015-0429-4,"
				+ " http://dx.doi.org/10.1016/j.ijpe.2016.01.021,"
				+ " http://dx.doi.org/10.1007/s0028",
				lines.toString().substring(0, 500));
	}

	/** EXTRACTS CSV from GETPAPERS/CROSSREF
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateCrossrefCSV() throws IOException {
		File targetDir = new File(CMineFixtures.GETPAPERS_TARGET, "20160601/csv");
		CMineTestFixtures.cleanAndCopyDir(CMineFixtures.GETPAPERS_SRC_20160601, targetDir);
		String cmd = "--project "+targetDir.toString()+" --csv crossref.csv License Title DOI";
		new CProject().run(cmd);
		File csvFile = new File(targetDir, "crossref.csv");
		Assert.assertTrue(csvFile.exists());
		List<String> lines = FileUtils.readLines(csvFile);
		Assert.assertEquals(21,  lines.size());
		Assert.assertEquals("["
				+ "License,Title,DOI,"
				+ " 0: [],Sodium Reduction—Saving Lives by Putting Choice Into Consumers’ Hands,10.1001/jama.2016.7992,"
				+ " \"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",\"The relationship between physical aggression, foreign policy and moral choices: Phenotypic and genetic findings\",10.1002/ab.21660,"
				+ " \"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",Die Hard in Notting Hill: Gender Differences in Recalling Contents from Action and Romantic Movies,10.1002/acp.3238,"
				+ " \"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",Improving Unfamiliar Face Matching by Masking the External Facial Features,10.1002/acp.3239,"
				+ " \"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",Note Taking and Note Reviewing Enhance Jurors' Recall of Trial Information,10.1002/acp.3240,"
				+ " \"2: [http://doi.wiley.com/10.1002/tdm_lic",
				lines.toString().substring(0, Math.min(lines.toString().length(), 1000)));
	}

	/** EXTRACTS CSV from GETPAPERS/CROSSREF
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateCrossrefAllHeadersCSV() throws IOException {
		File targetDir = new File(CMineFixtures.GETPAPERS_TARGET, "20160601/csv");
		CMineTestFixtures.cleanAndCopyDir(CMineFixtures.GETPAPERS_SRC_20160601, targetDir);
		String cmd = "--project "+targetDir.toString()+" --metadataType crossref --csv crossref1.csv";
		new CProject().run(cmd);
		File csvFile = new File(targetDir, "crossref1.csv");
		Assert.assertTrue("csvFile exists", csvFile.exists());
		List<String> lines = FileUtils.readLines(csvFile);
		Assert.assertEquals(132,  lines.size());
		Assert.assertEquals("["
				+ "URL,Title,Date,PDFURL,PDFFile,HTMLURL,HTMLFile,XMLURL,XMLFile,DOI,Publisher,Volume,AuthorList,Type,Issue,FirstPage,Description,Abstract,Journal,License,Links,Copyright,ISSN,Keywords,QuickscrapeMD,CrossrefMD,PublisherMD,Prefix,"
				+ " http://dx.doi.org/10.1001/jama.2016.7992,Sodium Reduction—Saving Lives by Putting Choice Into Consumers’ Hands,2016-06-01T23:24:00Z,,N,,N,,N,10.1001/jama.2016.7992,American Medical Association (AMA),,\"[Frieden Thomas R. [{\"\"name\"\":\"\"Centers for Disease Control and Prevention, Atlanta, Georgia\"\"}], ]\",journal-article,,,,,JAMA,0: [],0: [],,0098-7484,\"[[\"\"Medicine(all)\"\"]]\",N,N,N,http://id.crossref.org/prefix/10.1001,"
				+ " http://dx.doi.org/10.1002/ab.21660,\"The relationship between physical aggression, foreign policy and moral choices: Phenotypic and genetic findings\",2016-06-01T04:27:39Z,,N,,N,,N,10.1002/ab.21660,Wiley-Blackwell,,\"[McDermott Rose [{\"\"name\"\":\"\"Brown University; Providence Rhode Island\"\"}], , Hatemi Peter K. [{\"\"name\"\":\"\"Pennsylvania State University; State College Pennsylvania\"\"}], ]\",journal-article,,,,,Aggr. Behav.,"
				+ "\"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",1: [application/pdf http://api.wiley.com/onlinelibrary/tdm/v1/articles/10.1002%2Fab.21660],,0096-140X,\"[[\"\"Psychology(all)\"\"]]\",N,N,N,http://id.crossref.org/prefix/10.1002, http://dx.doi.org/10.1002/acp.3238,Die Hard in Notting Hill: Gender Differences in Recalling Contents from Action and Romantic Movies,2016-06-01T08:12:49Z,,N,,N,,N,10.1002/acp.3238,Wiley-Blackwell,,\"[Wühr Peter [{\"\"name\"\":\"\"Institut für Psychologie; Technische Universität Dortmund; Dortmund Germany\"\"}], , Schwarz Sascha [{\"\"name\"\":\"\"Institut für Psychologie; Bergische Universität Wuppertal; Wuppertal Germany\"\"}], ]\",journal-article,,,,,Applied Cognitive Psychology,"
				+ "\"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",1: [application/pdf http://api.wiley.com/onlinelibrary/tdm/v1/articles/10.1002%2Facp.3238],,0888-4080,\"[[\"\"Experimental and Cognitive Psychology\"\"]]\",N,N,N,http://id.crossref.org/prefix/10.1002, http://dx.doi.org/10.1002/acp.3239,Improving Unfamiliar Face Matching by Masking the External Facial Features,2016-06-01T08:13:03Z,,N,,N,,N,10.1002/acp.3239,Wiley-Blackwell,,\"[Kemp Richard I. [{\"\"name\"\":\"\"School of Psychology; University of New South Wales; Sydney Australia\"\"}], , Caon Alita [{\"\"name\"\":\"\"School of Psychology; University of New South Wales; Sydney Australia\"\"}], , Howard Mark [{\"\"name\"\":\"\"School of Psychology; University of New South Wales; Sydney Australia\"\"}], , Brooks Kevin R. [{\"\"name\"\":\"\"Department of Psychology; Macquarie University; Sydney Australia\"\"},{\"\"name\"\":\"\"Perception in Action Research Centre (PARC), Faculty of Human Sciences; Macquarie University; Sydney Australia\"\"}], ]\",journal-article,,,,,Applied Cognitive Psychology,"
				+ "\"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",1: [application/pdf http://api.",
				lines.toString().substring(0, Math.min(lines.toString().length(), 3000)));
	}


	/** CSV HELP
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void testCSVHelp() throws IOException {
		String cmd = "--csv"; // no csv filename so should give help
		new CProject().run(cmd);
	}

	/** MERGE PROJECTS (crossref)
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMergeProjects() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_SRC, "20160601");
		File source2Dir = new File(CMineFixtures.GETPAPERS_SRC, "20160602");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "project1");
		File target2Dir = new File(CMineFixtures.GETPAPERS_TARGET, "project2");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CMineTestFixtures.cleanAndCopyDir(source2Dir, target2Dir);
		CProject project1 = new CProject(target1Dir);
		CProject project2 = new CProject(target2Dir);
		// because git doesn't have empty directories
//		Assert.assertEquals("project1", 20, project1.getOrCreateCTreeList().size());
		Assert.assertTrue("mj", project1.getOrCreateCTreeList().size() >= 20); 
		Assert.assertEquals("project2", 35, project2.getOrCreateCTreeList().size());
		Assert.assertTrue("mj", project2.getOrCreateCTreeList().size() >= 35); 
		String cmd = "--project "+target1Dir.toString()+" --mergeProjects "+target2Dir.toString();
		new CProject().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		Assert.assertTrue("project1", project1.getOrCreateCTreeList().size() >= 55);
		Assert.assertTrue("project2", project2.getOrCreateCTreeList().size() >= 35);
		
	}
	
	/** EXTRACT URLS (temporary)
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void testGetOpenUrls() throws IOException {
		File targetDir = new File(CMineFixtures.GETPAPERS_TARGET, "unshuffled");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.GETPAPERS_SRC, "unshuffled"), targetDir);
		String cmd = "--project "+targetDir.toString()+" --outUrls urls.txt --metadataType crossref";
	}
	
	
	/** RENAME CTREE NAMES (normalize DOI names)
	 * 
	 * @throws IOException
	 */
	@Test

	public void testRenameDOIBasedNames() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "httpUrls");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "httpUrls");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CProject project1 = new CProject(target1Dir);
		CTreeList cTreeList = project1.getOrCreateCTreeList();
		Assert.assertEquals("project1", 4, cTreeList.size());
		Assert.assertTrue("urls", cTreeList.toString().contains(
				"target/getpapers/httpUrls/http_dx.doi.org_10.1063_1.4941232"));
		Assert.assertTrue("urls", cTreeList.toString().contains(
				"target/getpapers/httpUrls/http_dx.doi.org_10.1103_physrevb.93.075101"));

		String cmd = "--project "+target1Dir.toString()+" --renameCTree noHttp";
		new CProject().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		cTreeList = project1.getOrCreateCTreeList();
		LOG.trace(cTreeList);
		Assert.assertEquals("project1", 4, cTreeList.size());
		Assert.assertTrue("post normalize", cTreeList.toString().contains(
				"target/getpapers/httpUrls/10.1063_1.4941232"));
		Assert.assertTrue("urls", cTreeList.toString().contains(
				"target/getpapers/httpUrls/10.1103_physrevb.93.075101"));
	}

	
	/** RENAME CTREE NAMES and merge getpapers and quickscrape
	 * 
	 * typically getpapers and quickscrape may produce incompatible directories (we'll change this)
	 * 
	 * molecules
├── 10.3390_molecules21020174
│   └── crossref_result.json
├── 10.3390_molecules21020178
│   └── crossref_result.json
├── 10.3390_molecules21020180
│   └── crossref_result.json
...
├── 10.3390_molecules21020189
│   └── crossref_result.json
├── crossref_results.json
├── http_dx.doi.org_10.3390_molecules21020174
├── http_dx.doi.org_10.3390_molecules21020178
│   ├── fulltext.html
│   ├── fulltext.pdf
│   ├── fulltext.xml
│   └── results.json
├── http_dx.doi.org_10.3390_molecules21020180
│   ├── fulltext.html
│   ├── fulltext.pdf
│   ├── fulltext.xml
│   └── results.json
...
├── http_dx.doi.org_10.3390_molecules21020189
│   ├── fulltext.html
│   ├── fulltext.pdf
│   ├── fulltext.xml
│   └── results.json
└── urls.txt

'renameCTree noHttp' should normalize the dir names and copy files into a single place

may become unnecessary as getpapers and quickscrape are reconciled

	 * @throws IOException
	 */
	@Test
	public void testMergeGetpapersAndQuickscrapeNames() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "molecules");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "molecules");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 17, project1.getOrCreateCTreeList().size());
		String cmd = "--project "+target1Dir.toString()+" --renameCTree noHttp";
		new CProject().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		Assert.assertEquals("project1", 11, project1.getOrCreateCTreeList().size());
		Assert.assertEquals("post normalize", "["
				+ "target/getpapers/molecules/10.3390_molecules21020174,"
				+ " target/getpapers/molecules/10.3390_molecules21020178,"
				+ " target/getpapers/molecules/10.3390_molecules21020180,"
				+ " target/getpapers/molecules/10.3390_mo"
				, Utils.truncate(project1.getOrCreateCTreeList().toString(), 0, 200));
	}
	
	/** rename files in CTree
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRenameFiles() throws IOException {
		
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "httpUrls");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "httpUrls");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 4, project1.getOrCreateCTreeList().size());
		File oldFile = new File(project1.getDirectory(), "http_dx.doi.org_10.1063_1.4941232/results.json");
		Assert.assertTrue("oldFile exists", oldFile.exists());
		File newFile = new File(project1.getDirectory(), "http_dx.doi.org_10.1063_1.4941232/quickscrape_result.json");
		Assert.assertFalse("newFile not exists", newFile.exists());

		String cmd = "--project "+target1Dir.toString()+" --renameFile results.json quickscrape_result.json";
		new CProject().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		Assert.assertEquals("project1", 4, project1.getOrCreateCTreeList().size());
		oldFile = new File(project1.getDirectory(), "http_dx.doi.org_10.1063_1.4941232/results.json");
		Assert.assertFalse("oldFile not exists", oldFile.exists());
		newFile = new File(project1.getDirectory(), "http_dx.doi.org_10.1063_1.4941232/quickscrape_result.json");
		Assert.assertTrue("newFile exists", newFile.exists());
	}
	
	/** rename files in CTree
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDeleteFiles() throws IOException {
		
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "httpUrls");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "httpUrls");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 4, project1.getOrCreateCTreeList().size());
		File file1 = new File(project1.getDirectory(), "http_dx.doi.org_10.1103_physrevb.93.075101/results.json");
		Assert.assertTrue("file1 exists", file1.exists());
		File file2 = new File(project1.getDirectory(), "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.html");
		Assert.assertTrue("file2 exists", file2.exists());

		String cmd = "--project "+target1Dir.toString()+" --deleteFile results.json fulltext.html";
		new CProject().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		Assert.assertEquals("project1", 4, project1.getOrCreateCTreeList().size());
		file1 = new File(project1.getDirectory(), "http_dx.doi.org_10.1103_physrevb.93.075101/results.json");
		Assert.assertFalse("file1 not exists", file1.exists());
		file2 = new File(project1.getDirectory(), "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.html");
		Assert.assertFalse("file2 not exists", file2.exists());
	}
	

	/** INPUT AND OUTPUT URLS
	 * 
	 * @throws IOException
	 */
	@Test
	public void testInputOutputUrls() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "lic20160201truncated");
		
		File inUrls = new File(CMineFixtures.GETPAPERS_OPEN, "lic20160201/urls.txt");
		Assert.assertEquals("urls", 123, FileUtils.readLines(inUrls).size());
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "lic20160201truncated");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		File outUrls = new File(target1Dir, "outUrls.txt");
		Assert.assertFalse(outUrls.getAbsolutePath()+" exists", outUrls.exists());
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 4, project1.getOrCreateCTreeList().size());
		String cmd = "--project "+target1Dir.toString()+" --inUrls "+" urls.txt" +" markEmpty --outUrls outUrls.txt";
		new CProject().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		CTreeList cTreeList = project1.getOrCreateCTreeList();
		Assert.assertEquals("project1", 4, cTreeList.size());
		Assert.assertTrue("urls", cTreeList.toString().contains(
				"target/getpapers/lic20160201truncated/http_dx.doi.org_10.1088_1757-899x_106_1_012030"));
		Assert.assertTrue("urls", cTreeList.toString().contains(
				"target/getpapers/lic20160201truncated/http_dx.doi.org_10.1103_physrevb.93.075101"));
		Assert.assertTrue(outUrls.getAbsolutePath()+" exists", outUrls.exists());
		int nout = FileUtils.readLines(outUrls).size();
		Assert.assertTrue("urls "+nout, nout > 120);
	}
	
	/** CREATE FROM PDFS and HTMLs
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testCreateProjectFromFiles() throws IOException {
		File sourceDir = new File(CMineFixtures.GETPAPERS_OPEN, "pdfs");
		File targetDir = new File(CMineFixtures.GETPAPERS_TARGET, "pdfs");
		File projectDir = new File(CMineFixtures.GETPAPERS_TARGET, "newProject");
		if (projectDir.exists()) FileUtils.forceDelete(projectDir);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		
		String cmd = "--project "+projectDir.toString()+" --createCTreesFrom "+targetDir.toString();
		new CProject().run(cmd);

	}
	
	
	/** EXTRACT DOI PREFIXES
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testExtractDOIPrefixes() throws IOException {
		File sourceDir = new File(CMineFixtures.TEST_DOWNLOAD_DIR, "rename");
		Assert.assertTrue("rename exists", sourceDir.exists());
		File prefixOutFile = new File(CMineFixtures.GETPAPERS_TARGET, "rename/prefixes.xml");
		String cmd = "--project "+sourceDir.toString()+" --extractPrefixes "+prefixOutFile.toString();
		new CProject().run(cmd);
		Assert.assertTrue("exists", prefixOutFile.exists());
		Element element = XMLUtil.parseQuietlyToDocument(prefixOutFile).getRootElement();
//		Assert.assertEquals("element", ""
//				+ "<cTreeLists>\n"
//				+ " <cTreeList prefix=\"10.1001\">\n"
//				+ "  <cTree name=\"10.1001_jama.1234.5678\" />\n"
//				+ "  <cTree name=\"10.1001_jama.2016.7992\" />\n"
//				+ "  <cTree name=\"10.1001_jama.2345.6789\" />\n"
//				+ " </cTreeList>\n"
//				+ " <cTreeList prefix=\"10.1007\">\n"
//				+ "  <cTree name=\"10.1007_s13201-016-0429-9\" />\n"
//				+ " </cTreeList>\n"
//				+ " <cTreeList prefix=\"10.1016\"",
// 		element.toXML().substring(0,  300));
		
		Assert.assertTrue(element.getChildCount() > 0);
	}
	
	/** EXTRACT by DOI PREFIXES
	 * 
	 * creates a subproject for each prefix
	 * 
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testExtractByDOIPrefixes() throws IOException {
		File sourceDir = new File(CMineFixtures.TEST_DOWNLOAD_DIR, "rename");
		String prefixes = "10.1001 10.1007 10.4000 10.0000"; // the last has no files
		File outputFile = new File("target/prefixes");
		
		String cmd = "--project "+sourceDir.toString()+" --output "+outputFile.toString() + " --extractByPrefix "+prefixes;
		
		new CProject().run(cmd);
		Assert.assertTrue("outputFile", outputFile.exists() && outputFile.isDirectory());
		File[] files = outputFile.listFiles();
		Assert.assertEquals(4, files.length);
		CProject emptyProject = new CProject(new File(outputFile, "10.0000"));
		// FIXME Stefan fails
		Assert.assertEquals(1,  emptyProject.getOrCreateCTreeList().size());
		File projectDir1 = new File(outputFile, "10.1001");
		Assert.assertTrue("project1", projectDir1.listFiles().length > 0); 
		CProject project1 = new CProject(projectDir1);
		Assert.assertTrue(project1.getOrCreateCTreeList().size() > 0);

	}
	
	/** EXTRACT by DOI PREFIXES
	 * 
	 * creates a subproject for each prefix
	 * 
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testExtractByDOIPrefixesFromFile() throws IOException {
		File sourceDir = new File(CMineFixtures.TEST_DOWNLOAD_DIR, "rename");
		File inputFile = new File(CMineFixtures.TEST_DOWNLOAD_DIR, "prefixes1.txt");
		File outputFile = new File("target/prefixes1");
		
		// if no prefixes given, then look for a file with the input
		String cmd = "--project "+sourceDir.toString()+" --input "+inputFile.toString()+" --output "+outputFile.toString() + " --extractByPrefix ";
		
		new CProject().run(cmd);
		Assert.assertTrue("outputFile", outputFile.exists() && outputFile.isDirectory());
		File[] files = outputFile.listFiles();
		Assert.assertTrue(files.length > 5); // varies between 6 and 7 :-(
		CProject emptyProject = new CProject(new File(outputFile, "10.0000"));
		Assert.assertEquals(0,  emptyProject.getOrCreateCTreeList().size());
		File projectDir1 = new File(outputFile, "10.1001");
		// FIXME Stefan fails
		Assert.assertEquals("project1", 1, projectDir1.listFiles().length); 
		CProject project1 = new CProject(projectDir1);
		Assert.assertEquals(1,  project1.getOrCreateCTreeList().size());

	}
	
	/** ANALYZE FAILED DOWNLOADS
	 * 
	 * analyzes log file for "no 200 found and 0 captures
	 * 
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testExtractQuickscrapeLog() throws IOException {
		File projectDir = CMineFixtures.TEST_QUICKSCRAPE_DIR;
		String qsLogName = "total.log";
		String no200xmlName = "no200.xml";
		String qsNoCaptureName = "noCapture.xml";
		
		String cmd = "--project "+projectDir.toString()+" --quickscrapeLog "+qsLogName+
				" --quickscrapeNo200 "+no200xmlName + " --quickscrapeCapture "+qsNoCaptureName+" 0 0";

		new CProject().run(cmd);
		QuickscrapeLog quickscrapeLog = QuickscrapeLog.readLog(new File(CMineFixtures.TEST_QUICKSCRAPE_DIR, "total.log"));
		List<QSRecord> allRecords = quickscrapeLog.getQSURLRecords();
		Assert.assertEquals(6524, allRecords.size());
		List<QSRecord> records = quickscrapeLog.getNo200s();
		Assert.assertEquals(675, records.size());
		Multimap<String, String> doisByPrefix = quickscrapeLog.getUrlsByPrefix(records);
		List<List<String>> sortedKeys = CMineUtil.getListsSortedByCount(doisByPrefix);
		for (List<String> key : sortedKeys) {
			LOG.trace(key.size()+": "+key);
		}
	}
	
	
	/** EXTRACT SUBPROJECT
	 * 
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testExtractSubproject() throws IOException {
		File projectDir = CMineFixtures.TEST_QUICKSCRAPE_DIR;
		File outputDir = new File("target/subprojects/fulltexthtml");
		
		String cmd = "--project "+projectDir.toString()+" --extractContaining "+"fulltext.html"+
		    " --output "+outputDir.toString();

		new CProject().run(cmd);
		QuickscrapeLog quickscrapeLog = QuickscrapeLog.readLog(new File(CMineFixtures.TEST_QUICKSCRAPE_DIR, "total.log"));
		List<QSRecord> allRecords = quickscrapeLog.getQSURLRecords();
		Assert.assertEquals(6524, allRecords.size());
		List<QSRecord> records = quickscrapeLog.getNo200s();
		Assert.assertEquals(675, records.size());
		Multimap<String, String> doisByPrefix = quickscrapeLog.getUrlsByPrefix(records);
		List<List<String>> sortedKeys = CMineUtil.getListsSortedByCount(doisByPrefix);
		for (List<String> key : sortedKeys) {
			LOG.trace(key.size()+": "+key);
		}
	}
	
	/** REMOVES URLS WITH DOI_PREFIXES
	 * 
	 * @throws IOException 
	 * 
	 */
	@Test
	public void testRemoveURLsWithPrefixes() throws IOException {
		File projectDir = CMineFixtures.TEST_QUICKSCRAPE_DIR;
		File prefixFile = new File(CMineFixtures.TEST_QUICKSCRAPE_DIR, "miniExclude.xml");
		File urlsFile = new File(CMineFixtures.TEST_QUICKSCRAPE_DIR, "urls.txt");
		List<String> inUrls = FileUtils.readLines(urlsFile);
		Assert.assertEquals("out urls", 319, inUrls.size());

		File outUrlsFile = new File("target/remove/outUrls.txt");
		
		String cmd = "--project "+projectDir.toString()+" --removeUrlsWithPrefixes "+prefixFile.toString()+
				" --inUrls "+urlsFile.toString()+" --outUrls "+outUrlsFile.toString();
		
		new CProject().run(cmd);
		Assert.assertTrue("exists", outUrlsFile.exists());
		List<String> outUrls = FileUtils.readLines(outUrlsFile);
		Assert.assertEquals("out urls", 195, outUrls.size());
	}
	
	@Test
	public void testMakeProject() {
	    String cmd = " --project foo --fileFilter '.*/(.*).pdf' --makeProject '(\1)/fulltext.pdf'";
	    CProject cProject = new CProject();
	    cProject.run(cmd);
	}
	
}

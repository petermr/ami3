package org.contentmine.ami.tools;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.jupiter.api.Test;

import nu.xom.Element;

public class AMISummaryTest extends AbstractAMITest {
	
	private static final Logger LOG = LogManager.getLogger(AMISummaryTest.class);
	private static final File TARGET_DIR = new AMISummaryTest().createAbsoluteTargetDir();
	private static final String SUMMARY_CONST = "_summary";
	
	@Test
	public void testSummarizeMethods() {
		String root = "methods";
		String project = "summarizeProject/";
		File expectedDir = new File(TEST_BATTERY10+"."+"expected", project);
		File targetDir = new File(TARGET_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(TEST_BATTERY10, targetDir);
		String cmd = "-vvv"
				+ " -p "+targetDir
				+ " --output " + "/sections/body/"+root
				+ " summary "
				+ " --glob **/PMC*/sections/*_body/*_methods/**/*_p.xml"
				+ " --flatten"
			;
		AMI.execute(cmd);
		AbstractAMITest.compareDirectories(targetDir, expectedDir);
		
		// ami -vvv -p CEVOpen --output /sections/body/manny
		//	 summary --glob **/PMC*/sections/*_body/*_methods/**/*_p.xml --flatten"
	}

	/** extracts the unflattened subtree with a globbed set of leafnodes
	 * This creates a glob'ed list of results files and then creates a subtree of
	 * the project (see target/<project>
	 * Still a prototype
	 * 
	 * */
	@Test
	public void testSummarizeSearchResults() {
		String root = "search";
		String project = "summarizeProject/";
		File expectedDir = new File(TEST_BATTERY10+"."+"expected", project);
		File targetDir = new File("target/"+project);
		CMineTestFixtures.cleanAndCopyDir(TEST_BATTERY10, targetDir);
		String cmd = "-vvv"
				+ " -p "+targetDir
				+ " --output " + "/results/"+root
				+ " summary "
				+ " --glob **/PMC*/results/search/*/results.xml"
//				+ " --merge=1"
			;
		AMI.execute(cmd);
		AbstractAMITest.compareDirectories(targetDir, expectedDir);
		
	}

	/** extracts the flattened subtree of abstracts
	 * and a summary.csv 
	 * 
	 * */
	@Test
	public void testSummarizeAbstracts() {
		String root = "abstract";
		String project = "battery10/";
		File expectedDir = new File(TEST_BATTERY10+"."+"expected", project);
		File targetDir = new File(TARGET_DIR, project);
		CMineTestFixtures.cleanAndCopyDir(TEST_BATTERY10, targetDir);
		String cmd = "-vvv"
				+ " -p "+targetDir
				+ " --output " + "/"+root
				+ " summary "
				+ " --flatten"
				+ " --outtype tab"
				+ " --glob **/PMC*/sections/*_front/*_article-meta/*_abstract.xml"
			;
		AMI.execute(cmd);
		AbstractAMITest.compareDirectories(targetDir, expectedDir);
		
	}

	@Test
	public void testDirectoryTreeElement() {
		File root = new File(TEST_BATTERY10, "PMC3211491");
		Element tree = AMISummaryTool.createDirectoryTree(root);
//		System.out.println(tree.toXML());
		String expectedString = "<dir name=\"PMC3211491\"><file name=\"eupmc_result.json\" /><file name=\"fulltext.pdf\" /><file name=\"fulltext.xml\" /><dir name=\"pdfimages\"><dir name=\"image.1.1\"><dir name=\"image.1\"><dir name=\"image.1\"><dir name=\"hocr\"><file name=\"hocr.html\" /><file name=\"hocr.svg\" /></dir></dir><file name=\"image.1.png\" /></dir><file name=\"images.html\" /><dir name=\"octree\"><file name=\"binary.png\" /><file name=\"channel.1e1e1e.png\" /><file name=\"channel.fdfdfd.png\" /><file name=\"channels.html\" /><file name=\"histogram.svg\" /><file name=\"octree.png\" /></dir><dir name=\"raw\"><dir name=\"hocr\" /></dir><file name=\"raw.annot.html\" /><file name=\"raw.png\" /><file name=\"raw_o8.png\" /></dir></dir><dir name=\"results\"><dir name=\"search\"><dir name=\"country\"><file name=\"results.xml\" /></dir><dir name=\"elements\"><file name=\"empty.xml\" /></dir><dir name=\"funders\"><file name=\"results.xml\" /></dir></dir><dir name=\"word\"><dir name=\"frequencies\"><file name=\"results.html\" /><file name=\"results.xml\" /></dir></dir></dir><file name=\"scholarly.html\" /><file name=\"search.country.count.xml\" /><file name=\"search.country.snippets.xml\" /><file name=\"search.elements.count.xml\" /><file name=\"search.elements.snippets.xml\" /><file name=\"search.funders.count.xml\" /><file name=\"search.funders.snippets.xml\" /><dir name=\"sections\"><dir name=\"0_front\"><dir name=\"0_journal-meta\"><file name=\"0_journal-id.xml\" /><file name=\"1_journal-title-group.xml\" /><file name=\"2_issn.xml\" /><file name=\"3_issn.xml\" /><file name=\"4_publisher.xml\" /></dir><dir name=\"1_article-meta\"><file name=\"0_article-id.xml\" /><file name=\"10_volume.xml\" /><file name=\"11_issue.xml\" /><file name=\"12_fpage.xml\" /><file name=\"13_lpage.xml\" /><file name=\"14_history.xml\" /><file name=\"15_permissions.xml\" /><file name=\"16_self-uri.xml\" /><file name=\"17_abstract.xml\" /><file name=\"1_article-id.xml\" /><file name=\"2_article-id.xml\" /><file name=\"3_article-id.xml\" /><file name=\"4_article-categories.xml\" /><file name=\"5_title-group.xml\" /><file name=\"6_contrib-group.xml\" /><file name=\"7_aff.xml\" /><file name=\"8_pub-date.xml\" /><file name=\"9_pub-date.xml\" /></dir></dir><dir name=\"1_body\"><dir name=\"0_introduction\"><file name=\"0_title.xml\" /><file name=\"1_p.xml\" /><file name=\"2_p.xml\" /></dir><dir name=\"1_experimental\"><file name=\"0_title.xml\" /><file name=\"1_p.xml\" /><file name=\"2_p.xml\" /><file name=\"3_p.xml\" /><file name=\"4_p.xml\" /></dir><dir name=\"2_results_and_discussions\"><file name=\"0_title.xml\" /><file name=\"10_p.xml\" /><file name=\"1_p.xml\" /><file name=\"2_p.xml\" /><file name=\"3_p.xml\" /><file name=\"4_p.xml\" /><file name=\"5_p.xml\" /><file name=\"6_p.xml\" /><file name=\"7_p.xml\" /><file name=\"8_p.xml\" /><file name=\"9_p.xml\" /></dir><dir name=\"3_conclusion\"><file name=\"0_title.xml\" /><file name=\"1_p.xml\" /></dir><dir name=\"4_competing_interests\"><file name=\"0_title.xml\" /><file name=\"1_p.xml\" /></dir><dir name=\"5_authors__contributions\"><file name=\"0_title.xml\" /><file name=\"1_p.xml\" /></dir></dir><dir name=\"2_back\"><dir name=\"0_acknowledgements\"><file name=\"0_title.xml\" /><file name=\"1_p.xml\" /></dir><dir name=\"1_ref-list\"><file name=\"0_ref.xml\" /><file name=\"10_ref.xml\" /><file name=\"11_ref.xml\" /><file name=\"12_ref.xml\" /><file name=\"13_ref.xml\" /><file name=\"14_ref.xml\" /><file name=\"15_ref.xml\" /><file name=\"16_ref.xml\" /><file name=\"17_ref.xml\" /><file name=\"18_ref.xml\" /><file name=\"19_ref.xml\" /><file name=\"1_ref.xml\" /><file name=\"20_ref.xml\" /><file name=\"21_ref.xml\" /><file name=\"22_ref.xml\" /><file name=\"23_ref.xml\" /><file name=\"24_ref.xml\" /><file name=\"25_ref.xml\" /><file name=\"26_ref.xml\" /><file name=\"27_ref.xml\" /><file name=\"2_ref.xml\" /><file name=\"3_ref.xml\" /><file name=\"4_ref.xml\" /><file name=\"5_ref.xml\" /><file name=\"6_ref.xml\" /><file name=\"7_ref.xml\" /><file name=\"8_ref.xml\" /><file name=\"9_ref.xml\" /></dir></dir><dir name=\"3_floats-group\" /><dir name=\"figures\"><file name=\"figure_1.html\" /><file name=\"figure_1.xml\" /><file name=\"figure_2.html\" /><file name=\"figure_2.xml\" /><file name=\"figure_3.html\" /><file name=\"figure_3.xml\" /><file name=\"figure_4.html\" /><file name=\"figure_4.xml\" /><file name=\"figure_5.html\" /><file name=\"figure_5.xml\" /><file name=\"figure_6.html\" /><file name=\"figure_6.xml\" /><file name=\"summary.html\" /></dir></dir><dir name=\"svg\"><file name=\"fulltext-page.0.svg\" /><file name=\"fulltext-page.1.svg\" /><file name=\"fulltext-page.2.svg\" /><file name=\"fulltext-page.3.svg\" /><file name=\"fulltext-page.4.svg\" /><file name=\"fulltext-page.5.svg\" /><file name=\"fulltext-page.6.svg\" /></dir><file name=\"word.frequencies.count.xml\" /><file name=\"word.frequencies.snippets.xml\" /></dir>\n";
		XMLUtil.assertEqualsCanonically(expectedString, tree);
	}


}

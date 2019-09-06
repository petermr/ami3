package org.contentmine.ami.plugins.regex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.regex.AMIRegexTool;
import org.contentmine.ami.plugins.regex.CompoundRegex;
import org.contentmine.ami.plugins.regex.RegexArgProcessor;
import org.contentmine.ami.plugins.regex.RegexComponent;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.norma.NAConstants;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;
import nu.xom.Element;
import nu.xom.Text;

/** test AMIAMIRegexTool
 * hacked from old AMIRegexTool
 * @author pm286
 *
 */
public class AMIRegexToolTest {
	private static final Logger LOG = Logger.getLogger(AMIRegexToolTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** test generation of conformant regexes
	 * 
	 * desn't run anything
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSimpleTestRegex() throws IOException {
		String args = 
				// add context for 25 chars preceding and 40 post
				""
				+ " --regex "+NAConstants.MAIN_AMI_DIR+"/regex/common.xml"
				+ " --context 25 40";
	
		new AMIRegexTool().runCommands(args);
	}
	
	@Test
	// EMPTY??
	public void testAMIRegexTool() throws IOException {
		File target = new File("target/bmc/regex/15_1_511_test");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_BMC_15_1_511_CMDIR, target);
		String args = 
				"-t "+ target.toString()+
				" -i scholarly.html"
				+ " -o results.xml"
				+ " --context 25 40"
				+ " --regex "+NAConstants.MAIN_AMI_DIR+"/regex/common.xml";
		new AMIRegexTool().runCommands(args);
		
		AMIFixtures.checkResultsElementList(null, 1, 0, 
				"<results title=\"common\" />");
	}
	
	@Test
	public void testAMIRegexTools() throws IOException {
		File target = new File("target/bmc/regex/15_1_511_test");
		CMineTestFixtures.cleanAndCopyDir(AMIFixtures.TEST_BMC_15_1_511_CMDIR, target);
		File normaTemp = new File("target/bmc/regex/15_1_511_test");
		LOG.debug("tree "+normaTemp.getAbsolutePath());
		Assert.assertTrue("exists: "+normaTemp, normaTemp.exists());
		String args = 
				""
				+ "-t "+normaTemp.toString()
//				+ " -i scholarly.html"
//				+ " -o results.xml"
				+ " --context 25 40"
				+ " --regex "
					+ NAConstants.MAIN_AMI_DIR+"/regex/common.xml"
		    		+ " "+NAConstants.MAIN_AMI_DIR+"/regex/figure.xml"
		    		+ " "+NAConstants.MAIN_AMI_DIR+"/regex/phylotree.xml";
		new AMIRegexTool().runCommands(args);
		AMIFixtures.checkResultsElementList(null, 3, 0, 
				"<results title=\"common\" />");
		AMIFixtures.checkResultsElementList(null, 3, 1, 
				"<results title=\"figure\" />");
		AMIFixtures.checkResultsElementList(null, 3, 2, 
				"<results title=\"phylotree\" />");
	}
	
	@Test
	@Ignore
	// FAILS? logic is broken
	public void testCONSORTRegex() throws IOException {
		File target = new File("target/consort0/15_1_511_test/");
		
		AMIFixtures.runStandardTestHarness(
				AMIFixtures.TEST_BMC_15_1_511_CMDIR, 
				target, 
				/*new AMIRegexTool(),*/ null, // will deliberately fail
				"-q "+target+" -i scholarly.html --context 25 40 --regex "
						+ NAConstants.MAIN_AMI_DIR+"/regex/consort0.xml",
				"regex/consort0/");
		CTree cTree = new CTree(target);
		// this may alter it by reparsing
		HtmlElement scholarlyHtml = cTree.ensureScholarlyHtmlElement();
//		FileUtils.write(new File("target/consort0/text.html"), );
		File resultsXml = new File(target, "results/regex/consort0/results.xml");
		annotate(scholarlyHtml, resultsXml);
		File annotatedHtml = new File(target, "results/regex/consort0/annotated.html");
		FileUtils.write(annotatedHtml, scholarlyHtml.toXML());
	}
	
	private void annotate(HtmlElement htmlElement, File resultsXml) {
		Element resultsElement0 = XMLUtil.parseQuietlyToDocument(resultsXml).getRootElement();
		ResultsElement resultsElement = ResultsElement.createResultsElement(resultsElement0);
		List<ResultElement> resultElements = resultsElement.getOrCreateResultElementList();
		for (ResultElement resultElement : resultElements) {
			String xpath = resultElement.getXPath();
			String pre = resultElement.getPre();
			String post = resultElement.getPost();
			List<Element> nodes = XMLUtil.getQueryElements(htmlElement, xpath);
			if (nodes.size() == 1) {
				Element element = nodes.get(0);
				if (element instanceof HtmlP) {
					String value = element.getValue();
					int iPre = value.indexOf(pre);
					iPre = iPre + pre.length();
					String preString = value.substring(0,  iPre);
					int iPost = value.indexOf(post);
					if (iPost == -1) {
						LOG.debug("Cannot find :\n"+post+"\n in \n"+value); 
					}
					String postString = value.substring(iPost);
					String target = value.substring(iPre, iPost);
					for (int i = element.getChildCount() - 1; i >= 0; i--) {
						element.getChild(i).detach();
					}
					element.appendChild(new Text(preString));
					HtmlA aElement = new HtmlA();
					aElement.appendChild("["+target+"]");
					aElement.setHref("foo");
					element.appendChild(aElement);
					element.appendChild(new Text(postString));
				}
			}
		}
	}

	@Test
	// TESTED 2016-01-12
	@Ignore // fails to find file
	public void testCONSORTRegex1() throws IOException {
		File target = new File("target/consort0/15_1_511_test/");
		FileUtils.copyDirectory(AMIFixtures.TEST_WORD_EXAMPLES, AMIFixtures.TARGET_EXAMPLES_TEMP_16_1_1);
		String args = "-t "+target
//				+" -i scholarly.html"
				+ " --context 25 40"
				+ " --regex "+NAConstants.MAIN_AMI_DIR+"/regex/consort0.xml";
		new AMIRegexTool().runCommands(args);
		
		AMIFixtures.checkResultsElementList(null, 1, 0, 
				"<results title=\"consort0\"><result pre=\"-specific LBP (NSLBP), a \" name0=\"diagnose\" value0=\"diagnosis\" "
				+ "post=\"based on exclusion of a specific cause o\" "
				+ "xpath=\"/html[1]/body[1]/div[16]/div[2]/div[9]"
				);

	}
	
	@Test
	// TESTED 2016-01-12
	public void testSectioning() throws IOException {
		FileUtils.copyDirectory(AMIFixtures.TEST_BMC_15_1_511_CMDIR, new File("target/consort0/15_1_511_test/"));
		String cmd = ""
				+ "-t target/consort0/15_1_511_test/"
//				+ " -i scholarly.html"
				+ " --regex "+NAConstants.MAIN_AMI_DIR+"/regex/consort0.xml";
		RegexArgProcessor argProcessor = new RegexArgProcessor(cmd);
		
		AMIFixtures.checkResultsElementList(null, 1, 0, 
				"<results title=\"consort0\"><result pre=\"ety  3 . Approximately 90% of patients with LBP are labelled as having non-specific LBP (NSLBP), a \" name0=\"diagnose\" value0=\"diagnosis\" post=\"based on exclusion of a specific cause or pathology  4 . A wide range of health interventions for p\" xpath=\"/html[1]/b");
		
		File resultsFile = new File("target/consort0/15_1_511_test/results/regex/consort0/results.xml");
		Assert.assertEquals("results without xpath", 8,  
				XMLUtil.getQueryElements(XMLUtil.parseQuietlyToDocument(resultsFile).getRootElement(), 
						"//result").size());
		cmd = "-t target/consort0/15_1_511_test/"
//				+ " -i scholarly.html"
				+ " --xpath //*[@tagx='title']/*"
				+ " --regex "+ NAConstants.MAIN_AMI_DIR+"/regex/consort0.xml";
		argProcessor = new RegexArgProcessor(cmd);
		
		AMIFixtures.checkResultsElementList(null, 1, 0, 
				"<results title=\"consort0\">"
				+ "<result pre=\"r pattern of improvement following a wide range of primary care treatments: a systematic review of \" "
				+ "name0=\"random\" value0=\"randomized\" post=\"clinical trials \" "
				+ "xpath=\"/html[1]/body[1]/div[16]/"
				);
		resultsFile = new File("target/consort0/15_1_511_test/results/regex/consort0/results.xml");
		Assert.assertEquals("results with xpath", 2,  
				XMLUtil.getQueryElements(XMLUtil.parseQuietlyToDocument(resultsFile).getRootElement(), 
						"//result").size());
		AMIFixtures.checkResultsElementList(null, 1, 0, 
				"<results title=\"consort0\"><result pre=\"r pattern of improvement following a wide range "
				+ "of primary care treatments: a systematic review of \" name0=\"random\" value0=\"randomized\" "
				+ "post=\"clinical trials \" xpath=\"/html[1]/body[1]/div[16]/");


	}


	@Test
	// TESTED 2016-01-12
	public void testAMIRegexToolConsort0() throws IOException {
		String args = ""
				+ "-t target/bmc/regex/15_1_511_test"
//				+ " -i scholarly.html"
//				+ " -o results.xml"
				+ " --context 25 40 "
				+ "--regex "+NAConstants.MAIN_AMI_DIR+"/regex/consort0.xml";
		new AMIRegexTool().runCommands(args);
		
		AMIFixtures.checkResultsElementList(null, 1, 0, 
				"<results title=\"consort0\"><result pre=\"-specific LBP (NSLBP), a \" name0=\"diagnose\" value0=\"diagnosis\" post=\"based on exclusion of a specific cause o\" xpath=\"/html[1]/body[1]/div[16]/div[2]/div[9]/"
				);
	}
	
	@Test
	// TESTED 2016-01-12
	public void testNullSNPOutput() throws IOException {
		
		File regexDir = new File(NAConstants.TEST_AMI_DIR, "regex/");
		FileUtils.copyDirectory(new File(regexDir, "PMC4625707"), new File("target/regex/PMC4625707/"));
		String args = ""
				+ "-t target/regex/PMC4625707/"
//				+ " -i scholarly.html"
//				+ " -o results.xml"
				+ " --context 25 40"
				+ " --regex "+new File(regexDir, "snp.regex.xml");
		new AMIRegexTool().runCommands(args);
		
		AMIFixtures.checkResultsElementList(null, 1, 0, 
				"<results title=\"snp\" />");

	}
	
	@Test
	public void testBracketRegex0() throws IOException {
//String testXML = "<p> the study ( N = 300 ) was conducted p &lt; 0.01 and more</p>";
		String testXML = "<div>"
				+ "<p> LINE 1 Several matches with Q = 300 and  n = 123 and the study n = 250 was conducted </p>"
				+ "</div>";
		File test = new File("target/regex/brackets0/scholarly.html");
		FileUtils.write(test, testXML);
		Assert.assertTrue("test exists", test.exists());
		String args = ""
				+ "-t target/regex/brackets0/"
//				+ " -i scholarly.html"
//				+ " -o results.xml"
				+ " --regex " + NAConstants.MAIN_AMI_DIR+"/regex/statistics.xml";
		new AMIRegexTool().runCommands(args);
		
	}
	@Test
	public void testBracketRegex() throws IOException {
//String testXML = "<p> the study ( N = 300 ) was conducted p &lt; 0.01 and more</p>";
		String testXML = "<div>"
				+ "<p> LINE 1 Several matches with Q = 300 and  n = 123 and the study n = 250 was conducted </p>"
				+ "<p> LINE 2 try p &lt; 0.01 a match if we get the regex right</p>"
				+ "<p> LINE 3 another match Q = 123 complete </p>"
				+ "<p> LINE 4 this a match Q = 23 to test </p>"
				+ "<p> LINE 5 this is another nonmatching </p>"
				+ "</div>";
		File test = new File("target/regex/brackets/scholarly.html");
		FileUtils.write(test, testXML);
		Assert.assertTrue("test exists", test.exists());
		String args = ""
				+ "-t target/regex/brackets/"
//				+ " -i scholarly.html"
//				+ " -o results.xml"
				+ " --regex " + NAConstants.MAIN_AMI_DIR+"/regex/statistics.xml";
		new AMIRegexTool().runCommands(args);
		
	}
	
	@Test
	public void testExtractFields() throws IOException {
		File targetDir = new File("target/regex/brackets/");
		createTestDocument(targetDir, 				
				"<div>"
				+ "<p> PARA 1 Several matches with Q = 300 and  n = 123 and the study n = 250 was conducted </p>"
				+ "</div>"
				);
		File regexFile = createRegexFile(targetDir, "testme", "qqq", "Q");
		String args = ""
				+ "-t "+targetDir
//				+" -i scholarly.html"
//				+ " -o results.xml"
				+ "  --regex "+regexFile;
		new AMIRegexTool().runCommands(args);
		
	}

	@Test
	public void testExtractFields1() throws IOException {
		File targetDir = new File("target/regex/brackets/");
		createTestDocument(targetDir, 				
				"<div>"
				+ "<p> PARA 1 Several matches with Q = 300 and  n = 123 and the study n = 250 was conducted </p>"
				+ "</div>"
				);
		File regexFile = createRegexFile(targetDir, "testme1", "qqq", "[Qn]\\s*=\\s*[0-9]+");
		String args = ""
				+ "-t "+targetDir
//				+" -i scholarly.html"
//				+ " -o results.xml"
				+ "  --regex "+regexFile;
		new AMIRegexTool().runCommands(args);
	}

	private File createRegexFile(File targetDir, String title, String field, String rawRegex) {
		File regexFile = new File(targetDir, "regex.xml");
		CompoundRegex compoundRegex = createCompoundRegex(title, field, rawRegex);
		try {
			XMLUtil.debug(compoundRegex.getOrCreateCompoundRegexElement(), new FileOutputStream(regexFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write compoundRegex ", e);
		}
		return regexFile;
	}

	private CompoundRegex createCompoundRegex(String title, String fields, String rawRegex) {
		CompoundRegex compoundRegex = new CompoundRegex(title);
		RegexComponent regexComponent = new RegexComponent(compoundRegex);
		regexComponent.setField(fields);
		regexComponent.setValue(rawRegex);
		return compoundRegex;
	}

	private void createTestDocument(File targetDir, String testXML) throws IOException {
		File test = new File(targetDir, "scholarly.html");
		FileUtils.write(test, testXML);
		Assert.assertTrue("test exists", test.exists());
	}

}

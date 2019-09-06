package org.contentmine.norma;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.NormaArgProcessor;
import org.contentmine.norma.pubstyle.util.XMLCleaner;
import org.junit.Assert;

/** avoids horrible static test runner.
 * 
 * @author pm286
 *
 */
public class NormaFixtureRunner {


private static final Logger LOG = Logger.getLogger(NormaFixtureRunner.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public NormaFixtureRunner() {
	}
	
	private static File shtmlFile;
	
	public void copyToTargetRunTidyTransformWithStylesheetSymbolRoot(File from, File projectDir, String abb) {
		copyToTargetRunTidyTransformWithStylesheetSymbol(from, projectDir, abb+"2html");
	}

	public void copyToTargetRunTidyTransformWithStylesheetSymbol(File from, File projectDir, String symbol) {
		LOG.trace(projectDir+": tidy fulltext.html to fulltext.xhtml");
		CMineTestFixtures.cleanAndCopyDir(from, projectDir);
		String args = "--project "+projectDir+" -i fulltext.html -o fulltext.xhtml --html jsoup";
		DefaultArgProcessor argProcessor = new NormaArgProcessor(args); 
		argProcessor.runAndOutput(); 
		CProject project = new CProject(projectDir);
		CTree ctree0 = project.getOrCreateCTreeList().get(0);
		File xhtmlFile = ctree0.getExistingFulltextXHTML();
		if (xhtmlFile != null) {
			Assert.assertTrue("xhtml: ", xhtmlFile.exists());
			LOG.trace("convert xhtml to html Symbol: "+symbol);
			args = "--project "+projectDir+" -i fulltext.xhtml -o scholarly.html --transform "+symbol;
			argProcessor = new NormaArgProcessor(args); 
			argProcessor.runAndOutput(); 
			shtmlFile = ctree0.getExistingScholarlyHTML();
			Assert.assertNotNull("failed convert using: "+symbol, shtmlFile);
			Assert.assertTrue("shtml: ", shtmlFile.exists());
		}
	}
	
	public void tidyTransformAndClean(File from, File projectDir, String abb) throws IOException {
		// This passes a static variable!!! What was I thinking???
		copyToTargetRunTidyTransformWithStylesheetSymbolRoot(from, projectDir, abb);
		if (shtmlFile != null) {
			XMLCleaner cleaner = XMLCleaner.createCleaner(shtmlFile);
			cleaner.removeCommonEmptyElements();
			String cleanedXml = cleaner.getElement().toXML();
			File file = new File(projectDir, "cleaned.html");
			FileUtils.write(file, cleanedXml);
			cleaner.removeXMLNSNamespace();
			FileUtils.write(new File(projectDir, "cleaned.xml"), cleaner.getElement().toXML());
		}
	}
	
}

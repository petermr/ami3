package org.contentmine.norma.patents.uspto;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.NormaArgProcessor;
import org.contentmine.norma.NormaFixtures;
import org.contentmine.norma.util.NormaTestFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class USPTOTest {
	private static final Logger LOG = LogManager.getLogger(USPTOTest.class);
@Test
	@Ignore // something wrong with scholarly html
	public void testReadCTree() throws IOException {
		File target = new File("target/us08978/US08978162-20150317");
		FileUtils.copyDirectory(new File(NormaFixtures.TEST_USPTO08978_DIR, "US08978162-20150317/"), target);
		String args = "-i fulltext.xml --transform uspto2html -o scholarly.html --ctree "+target; 
		DefaultArgProcessor norma = new NormaArgProcessor(args);
		norma.runAndOutput();
		NormaTestFixtures.checkScholarlyHtml(target, 
//				"<?xml version=\"1.0\"?>"
//				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head xmlns=\"\">"
//				+ "<style>div {border: 2px solid black; margin: 5pt; padding: 5pt;}</style></head>"
//				+ "<div xmlns=\"\"><h2>us-patent-grant</h2><h1>BIBLIOGRAPHIC</h1><b>US08978162B220150317:::</b><b>document-id:: </b>"
//				+ "<span title=\"country\""
//				);
				"<?xml version=\"1.0\"?>"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head>"
				+ " <style>div {border: 2px solid black; margin: 5pt; padding: 5pt;}</style>"
				+ " </head>"
				+ " <body>"
				+ "<div>"
				+ " <h2>us-patent-grant</h2> <h1>BIBLIOGRAPHIC</h1>"
				+ " <b> US 08978162 B2 20150317 ::: </b> <b>document-id:: </b> <span title=\"country\">U"
				);
	}

	@Test
	@Ignore // fails ScholarlyHtml does not start correctly: 
	public void testReadCProject() throws IOException {
		File target = new File("target/us08978/");
		CMineTestFixtures.cleanAndCopyDir(NormaFixtures.TEST_USPTO08978_DIR, target);
		String args = "-i fulltext.xml --transform uspto2html -o scholarly.html --project "+target; 
		DefaultArgProcessor norma = new NormaArgProcessor(args);
		norma.runAndOutput();
		File shtmlFile = new File(target, "US08978162-20150317");
		CProject project = new CProject(target);
		Assert.assertEquals("ctrees", 8, project.getOrCreateCTreeList().size());
		NormaTestFixtures.checkScholarlyHtml(shtmlFile, 
//				"<?xml version=\"1.0\"?>"
//				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
//				+ "<head xmlns=\"\"><style>div {border: 2px solid black; margin: 5pt; padding: 5pt;}</style></head>"
//				+ "<div xmlns=\"\">"
//				+ "<h2>us-patent-grant</h2>"
//				+ "<h1>BIBLIOGRAPHIC</h1>"
//				+ "<b>US08978162B220150317:::</b><b>document-id:: </b><span title=\"country\""
//				);
				"<?xml version=\"1.0\"?>"
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>"
				+ " <style>div {border: 2px solid black; margin: 5pt; padding: 5pt;}</style>"
				+ " </head> <body><div> <h2>us-patent-grant</h2> "
				+ "<h1>BIBLIOGRAPHIC</h1> <b> US 08978162 B2 20150317 ::: </b>"
				+ " <b>document-id:: </b> <span title=\"country\">U"
				);

	}
}

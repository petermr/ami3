package org.contentmine.norma.table;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaFixtures;
import org.junit.Test;

import net.minidev.json.JSONObject;

public class CSVTest {
	
	public static final Logger LOG = LogManager.getLogger(CSVTest.class);
@Test
	public void testReadCSV() throws Exception {
		CSVTransformer csvTransformer = new CSVTransformer();
		csvTransformer.readFile(new File(NormaFixtures.TEST_TABLE_DIR, "table.csv"));
		HtmlTable table = csvTransformer.createTable();
		HtmlHtml html = HtmlHtml.createUTF8Html();
		html.getOrCreateBody().appendChild(table);
		XMLUtil.debug(html, new File("target/table/table.html"), 1);
	}

	@Test
	public void testCSV2TSV() throws Exception {
		CSVTransformer csvTransformer = new CSVTransformer();
		csvTransformer.readFile(new File(NormaFixtures.TEST_TABLE_DIR, "table.csv"));
		String tsvString = csvTransformer.createTSV();
		FileUtils.write(new File("target/table/table.tsv"), tsvString, CMineUtil.UTF8_CHARSET);
	}

	@Test
	public void testCSV2JSON() throws Exception {
		CSVTransformer csvTransformer = new CSVTransformer();
		csvTransformer.readFile(new File(NormaFixtures.TEST_TABLE_DIR, "table.csv"));
		JSONObject object= csvTransformer.createJSON();
		FileUtils.write(new File("target/table/table.json"), object.toJSONString(), CMineUtil.UTF8_CHARSET);
	}

	@Test
	public void testTransformCSV() throws Exception {
		File targetDir= new File("target/table/project1/");
		File projectDir = new File(NormaFixtures.TEST_TABLE_DIR, "project1");
		CMineTestFixtures.cleanAndCopyDir(projectDir, targetDir);
		String args = "--project "+targetDir.toString()+" --transform csv2html";
		Norma norma = new Norma();
		norma.run(args);
	}

	@Test
	public void testMoveCSV() throws Exception {
		File targetDir= new File("target/table/project2/");
		File projectDir = new File(NormaFixtures.TEST_TABLE_DIR, "project2");
		CMineTestFixtures.cleanAndCopyDir(projectDir, targetDir);
		String args = "--project "+targetDir.toString()+" --move csv,table/";
		Norma norma = new Norma();
		norma.run(args);
	}

	@Test
	public void testMoveAndProcessTFCSV() throws Exception {
		String args;
		Norma norma;
		File targetDir= new File("target/table/joer/");
		File projectDir = new File(NormaFixtures.TEST_TABLE_DIR, "joer");
		CMineTestFixtures.cleanAndCopyDir(projectDir, targetDir);
		args = "--project "+targetDir.toString()+" --move csv,table/";
		norma = new Norma();
		norma.run(args);
		args = "--project "+targetDir.toString()+" --transform csv2html";
		norma = new Norma();
		norma.run(args);
	}


}

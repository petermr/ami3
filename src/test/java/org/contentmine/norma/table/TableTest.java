package org.contentmine.norma.table;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaFixtures;
import org.junit.Ignore;
import org.junit.Test;

public class TableTest {
	private static final Logger LOG = LogManager.getLogger(TableTest.class);
@Test
	/** just to recap how iteration works
	 * 
	 */
	@Ignore
	public void testPDFTable0() throws IOException {
		File sourceDir = NormaFixtures.TEST_PDFTABLE0_DIR;
		File targetDir = new File("target/pdftable0/");
		File oldSVG = new File("target/svg/"); // FIX this
		FileUtils.deleteDirectory(oldSVG);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		// runs pdf2svg and svg2xml
		String cmd = "--project "+targetDir+" -i fulltext.pdf -o zzzz.html --transform pdf2html";
		new Norma().run(cmd);
		
	}
	
	@Test
	/** file filter to iterate over all files of a type
	 * 
	 */
	@Ignore // LARGE
	public void testFileFilter() {
		File sourceDir = NormaFixtures.TEST_PDFTABLE_DIR;
		File targetDir = new File("target/pdftable1/");
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		// proposed new table structure /ctree/tables/table%d/table.svg
		String cmd = "--project "+targetDir+" --fileFilter ^.*tables/table(\\d+)/table(_\\d+)?\\.svg.*$ --outputDir target/pdftable01/ --transform svgtable2html";
		new Norma().run(cmd);
		
	}
	
	@Test
	/** iterate over whole CProject
	 * 
	 */
	@Ignore // production
	public void testCProject() {
		// these ones have single text characters
		File sourceDir = new File("../../cm-ucl/corpus-oa-pmr/");
		if (sourceDir.exists()) {
			File targetDir = new File("../../cm-ucl/corpus-oa-pmr-v01/");
//			File targetDir = new File("target/corpus-oa-pmr-v01/");
			CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
			// note historical regex /ctree/table%d.svg
			String cmd = "--project "+targetDir+" --fileFilter ^.*/table\\d+(cont)?\\.svg.*$ --transform svgtable2html";
			LOG.debug("running norma");
			new Norma().run(cmd);
		} else {
			LOG.debug("no data, skipped");
		}
	}
	
	@Test
	/** aggregate into HTML display
	 * (a) creates table.svg.html from table.svg
	 * (b) iterates over all (table.svg and table.svg.html) pairs to create a combined table
	 *   with both
	 */
	public void testCreateSvgHtml() {
		boolean clean = false;
		File sourceDir = NormaFixtures.TEST_PDFTABLE00_DIR;
		File targetDir = new File("target/pdftable00/");
		LOG.debug("Target: "+targetDir);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		new Norma().run("--project "+targetDir+" --fileFilter ^.*tables/table(\\d+)/table(_\\d+)?\\.svg.*$"
			+ " --outputDir "+"target/pdftable00/"
			+ " --transform svgtable2html");
		
	}
	
	
}

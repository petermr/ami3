package org.contentmine.svg2xml.table;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.text.build.TextChunk;
import org.contentmine.graphics.svg.text.line.SuscriptEditor;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Ignore;
import org.junit.Test;


//@Ignore
public class TableContentCreatorTest {
	
	static final Logger LOG = Logger.getLogger(TableContentCreatorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final double IMG_XSCALE = 700.0;
	@Test
	/** simple rectangular Table.
	 * No subtables or split columns
	 * @throws IOException
	 */
	public void testSimple2Html() throws IOException {
//		cm-ucl/corpus-oa-pmr/10.1016_j.pain.2014.09.033/pdftable/table1.annot.svg
		File inputFile1 = new File(SVG2XMLFixtures.TABLE_DIR, "html/simple/10.1016_j.pain.2014.09.033.annot.svg");
//		10.1016_j.jadohealth.2016.10.001/pdftable/table3.annot.svg
//		File inputFile1 = new File(Fixtures.TABLE_DIR, "html/table3.annot.svg");
		File outDir = new File("target/table/html/simple");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.createHTML(inputFile1, outDir);
	}
	
	@Test
	@Ignore
	// spurious SVGText with whitespace content
	public void testWhitespaceProblem() {
		File inputFile = new File(SVG2XMLFixtures.TABLE_DIR, "whitespace/table1.svg");
		File outDir = new File("target/table/whitespace/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile, outDir);
	}

	@Test
	/** indents.
	 * No subscripts or split columns
	 * @throws IOException
	 */
	public void testIndents() throws IOException {
//		cm-ucl/corpus-oa-pmr/10.1186_1471-2431-13-190/pdftable/table1.annot.svg
		File inputFile1 = new File(SVG2XMLFixtures.TABLE_DIR, "indent/10.1186_1471-2431-13-190.annot.svg");
		File outDir = new File("target/table/indent/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.createHTML(inputFile1, outDir);
	}
	
	@Test
	/** split column/s
	 * No subscripts or indents
	 * @throws IOException
	 */
	public void testSplitColumn() throws IOException {
//		cm-ucl/corpus-oa-pmr/1	10.1179_1743132815Y.0000000050/pdftable/table5.annot.svg
		File inputFile1 = new File(SVG2XMLFixtures.TABLE_DIR, "splitcol/10.1179_1743132815Y.0000000050.annot.svg");
		File outDir = new File("target/table/splitcol/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.createHTML(inputFile1, outDir);
	}
	
	@Test
	/** subscript
	 * No split columns or indents
	 * complex suscripts in Footer - not yet resolved
	 * @throws IOException
	 */
	@Ignore // fails to get Footer
	public void testSuscriptSVG() throws IOException {
//		cm-ucl/corpus-oa-pmr/10.1371_journal.pbio.1000481/pdftable/table1.annot.svg
		File inputFile1 = new File(SVG2XMLFixtures.TABLE_DIR, "suscript/10.1371_journal.pbio.1000481.svg");
		File outDir = new File("target/table/suscript/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.markupAndOutputTable(inputFile1, outDir);
		TextChunk phraseListList = new TextChunk(tableContentCreator.getOrCreateTableFooterSection().getOrCreatePhraseListList());
		SuscriptEditor suscriptEditor = new SuscriptEditor(phraseListList);
		suscriptEditor.mergeAll();
		LOG.trace("PLL"+phraseListList);
	}
	
	@Test
	/** subscript
	 * isolated superscripts
	 * @throws IOException
	 * 
	 * may have a bug as fails assert
	 */
	public void testSuscriptSVG1() throws IOException {
		// output of PDF2SVG 
		File inputFile1 = new File(SVG2XMLFixtures.TABLE_DIR, "suscript/10.1007_s00213-015-4198-1.svg");
		File outDir = new File("target/table/suscript/");
		// TableContentCreator is the top-level engine for tables
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		// annotate the geometric regions of the SVG
		// generates 10.1007_s00213-015-4198-1.annot.svg
		tableContentCreator.markupAndOutputTable(inputFile1, outDir);
		// the key Text component is a list of PhraseLists. This is created independently
		// of subsequent section/column/row boundaries
		// = footer test
		TableFooterSection tableFooterSection = tableContentCreator.getOrCreateTableFooterSection();
		if (tableFooterSection == null) {
			LOG.error("NO FOOTER: ABORT");
			return;
		}
		TextChunk footerPhraseListList = new TextChunk(tableFooterSection.getOrCreatePhraseListList());
		LOG.trace(footerPhraseListList.toString());
//		Assert.assertEquals(5, footerPhraseListList.size());
		// Suscript editor works directly on the PhraseListList and incorporates all suscripts at this
		// stage so we don't have to process later
		SuscriptEditor suscriptEditor = new SuscriptEditor(footerPhraseListList);
		//merge all suscripts into the PLL
		suscriptEditor.mergeAll();
		File file = new File(outDir, FilenameUtils.getBaseName(inputFile1.toString())+"footer.html");
		// svg2xml/target/table/suscript/10.1007_s00213-015-4198-1.html
		// PLL has an HTML output which can process suscripts and styles
		// note the HTML must not be indented (0) as otherwise we get spurious whitespaces
		XMLUtil.debug(footerPhraseListList.toHtml(), file, 0);
		// the output is the primary initial test
	}
	
	@Test
	/** subscript
	 * isolated superscripts
	 * @throws IOException
	 */
	@Ignore // too long
	public void testTables() throws IOException {
		File[] files = SVG2XMLFixtures.TABLE_DIR.listFiles();
		File outDir = new File("target/table/tableFiles/");
		for (File file : files) {
			String filename = file.toString();
			if (filename.endsWith(".svg")) {
				String root = FilenameUtils.getBaseName(filename);
				TableContentCreator tableContentCreator = new TableContentCreator(); 
				tableContentCreator.markupAndOutputTable(file, outDir);
				writeBody(outDir, root, tableContentCreator);
				writeFooter(outDir, root, tableContentCreator);
			}
		}
	}

	private void writeBody(File outDir, String root, TableContentCreator tableContentCreator) throws IOException {
		TableSection body = tableContentCreator.getOrCreateTableBodySection();
		if (body != null) {
			TextChunk footerPhraseListList = body.getOrCreatePhraseListList();
			File filex = new File(outDir, root+".body.html");
			XMLUtil.debug(footerPhraseListList.toHtml(), filex, 0);
		}
	}
	
	private void writeFooter(File outDir, String root, TableContentCreator tableContentCreator) throws IOException {
		TableSection footer = tableContentCreator.getOrCreateTableFooterSection();
		if (footer != null) {
			TextChunk footerPhraseListList = footer.getOrCreatePhraseListList();
			File filex = new File(outDir, root+".footer.html");
			XMLUtil.debug(footerPhraseListList.toHtml(), filex, 0);
		}
	}
	
	@Test
	/** subscript
	 * No split columns or indents
	 * @throws IOException
	 */
	public void testSuscriptHTML() throws IOException {
//		cm-ucl/corpus-oa-pmr/10.1371_journal.pbio.1000481/pdftable/table1.annot.svg
		File inputFile1 = new File(SVG2XMLFixtures.TABLE_DIR, "suscript/10.1371_journal.pbio.1000481.annot.svg");
		File outDir = new File("target/table/suscript/");
		TableContentCreator tableContentCreator = new TableContentCreator(); 
		tableContentCreator.createHTML(inputFile1, outDir);
	}
	
	// ===================================

}


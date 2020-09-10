package org.contentmine.ami.tools;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.jupiter.api.Test;



/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMIFilterTest extends AbstractAMITest {
	private static final Logger LOG = LogManager.getLogger(AMIFilterTest.class);
	private static final File TARGET_DIR = new AMIFilterTest().createAbsoluteTargetDir();

	@Test
	public void testGenericHelp() {
		/** fails with NPE */
		AbstractAMITool abstractTool;
		// to get help for `ami`
//		AMI ami = AMI.execute(AMI.class, "--help");
//		Assert.assertEquals("" +
//				"--duplicate         : d      null\n" + 
//				"--maxheight         : d      1000\n" + 
//				"--maxwidth          : d      1000\n" + 
//				"--minheight         : d       100\n" + 
//				"--minwidth          : d       100\n" + 
//				"--monochrome        : d      null\n" + 
//				"--small             : d      null\n" + 
//				"--help              : m      true\n" + 
//				"--version           : d     false\n" + 
//				"", ami);
//				"", ami.getOptionsValue());

		/**
		abstractTool = AMI.execute(AMIFilterTool.class, "ami --help");
		Assert.assertEquals(""
				+ "  assert               Makes assertions about objects created by AMI.\n" + 
				"  clean                Cleans specific files or directories in project.\n" + 
				"  display              Displays files in CTree.\n" + 
				"  download             Downloads content from remote site.\n" + 
				"  dummy                Minimal AMI Tool for editing into more powerful classes.\n" + 
				"  filter               FILTERs images (initally from PDFimages), but does not transform the contents.\n" + 
				"  forest               Analyzes ForestPlot images.\n" + 
				"  graphics             Transforms graphics contents (often from PDF/SVG).\n" + 
				"  grobid               Runs grobid.\n" + 
				"  image                Transforms image contents but only provides basic filtering (see ami-filter).\n" + 
				"  makeproject          Processes a directory (CProject) containing files (e.g.*.pdf, *.html, *.xml) to be made into\n" + 
				"                         CTrees.\n" + 
				"  metadata             Manages metadata for both CProject and CTrees.\n" + 
				"  ocr                  Extracts text from OCR and (NYI) postprocesses HOCR output to create HTML.\n" + 
				"  pdfbox               Convert PDFs to SVG-Text, SVG-graphics and Images.\n" + 
				"  pixel                Analyzes bitmaps - generally binary, but may be oligochrome.\n" + 
				"  regex                Searches with regex.\n" + 
				"  search               Searches text (and maybe SVG).\n" + 
				"  section              Splits XML files into sections using XPath.\n" + 
				"  summary              Summarizes the specified dictionaries, genes, species and words.\n" + 
				"  svg                  Takes raw SVG from PDF2SVG and converts into structured HTML and higher graphics primitives.\n" + 
				"  table                Writes cProject or cTree to summary table.\n" + 
				"  transform            Runs XSLT transformation on XML (NYFI).\n" + 
				"  words                Analyzes word frequencies.\n" + 
				"  help                 Displays help information about the specified command\n" + 
				"  generate-completion  Generate bash/zsh completion script for ami.",
				abstractTool.getOptionsValue());
*/
		// to get help for `ami filter`
		// TODO check out https://stefanbirkner.github.io/system-rules/#SystemErrAndOutRule
		//   for asserting on tool's output to system.out/system.err
		abstractTool = AMI.execute(AbstractAMITool.class, "filter --help");
		Assert.assertEquals("" +
				"--duplicate         : d      null\n" + 
				"--maxheight         : d      1000\n" + 
				"--maxwidth          : d      1000\n" + 
				"--minheight         : d       100\n" + 
				"--minwidth          : d       100\n" + 
				"--monochrome        : d      null\n" + 
				"--small             : d      null\n" + 
				"--help              : m      true\n" + 
				"--version           : d     false\n" + 
				"", abstractTool.getOptionsValue());



		if (false) Assert.assertEquals(""
				+ "  assert               Makes assertions about objects created by AMI.\n" +
				"  clean                Cleans specific files or directories in project.\n" + 
				"  display              Displays files in CTree.\n" + 
				"  download             Downloads content from remote site.\n" + 
				"  dummy                Minimal AMI Tool for editing into more powerful classes.\n" + 
				"  filter               FILTERs images (initally from PDFimages), but does not transform the contents.\n" + 
				"  forest               Analyzes ForestPlot images.\n" + 
				"  graphics             Transforms graphics contents (often from PDF/SVG).\n" + 
				"  grobid               Runs grobid.\n" + 
				"  image                Transforms image contents but only provides basic filtering (see ami-filter).\n" + 
				"  makeproject          Processes a directory (CProject) containing files (e.g.*.pdf, *.html, *.xml) to be made into\n" + 
				"                         CTrees.\n" + 
				"  metadata             Manages metadata for both CProject and CTrees.\n" + 
				"  ocr                  Extracts text from OCR and (NYI) postprocesses HOCR output to create HTML.\n" + 
				"  pdfbox               Convert PDFs to SVG-Text, SVG-graphics and Images.\n" + 
				"  pixel                Analyzes bitmaps - generally binary, but may be oligochrome.\n" + 
				"  regex                Searches with regex.\n" + 
				"  search               Searches text (and maybe SVG).\n" + 
				"  section              Splits XML files into sections using XPath.\n" + 
				"  summary              Summarizes the specified dictionaries, genes, species and words.\n" + 
				"  svg                  Takes raw SVG from PDF2SVG and converts into structured HTML and higher graphics primitives.\n" + 
				"  table                Writes cProject or cTree to summary table.\n" + 
				"  transform            Runs XSLT transformation on XML (NYFI).\n" + 
				"  words                Analyzes word frequencies.\n" + 
				"  help                 Displays help information about the specified command\n" + 
				"  generate-completion  Generate bash/zsh completion script for ami.",
				abstractTool.getOptionsValue());
	}
	
	@Test
	public void testHelp() {
		AMI.execute("-vv filter --help");
	}

	@Test
	public void testFallback() {
		AMIFilterTool filterTool = AMI.execute(AMIFilterTool.class, "-vv filter --duplicate --monochrome");
		Assert.assertEquals("options value", "" +
				"--duplicate         : m duplicate\n" + 
				"--maxheight         : d      1000\n" + 
				"--maxwidth          : d      1000\n" + 
				"--minheight         : d       100\n" + 
				"--minwidth          : d       100\n" + 
				"--monochrome        : m monochrome\n" + 
				"--small             : d      null\n" + 
				"--help              : d     false\n" + 
				"--version           : d     false\n" + 
				"", filterTool.getOptionsValue());
	}



}

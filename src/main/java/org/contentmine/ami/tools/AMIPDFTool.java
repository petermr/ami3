package org.contentmine.ami.tools;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.pdf2svg2.PDFDocumentProcessor;
import org.contentmine.pdf2svg2.PageParserRunner;
import org.contentmine.pdf2svg2.PageParserRunner.ParserDebug;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

	@Command(
	name = "ami-pdf", 
	aliases = "pdf",
	version = "ami-pdf 0.1",
	description = "Convert PDFs to SVG-Text, SVG-graphics and Images. Does not process images, graphics or text."
			+ "often followed by ami-image and ami-xml?"
	)


public class AMIPDFTool extends AbstractAMITool {
	private static final Logger LOG = Logger.getLogger(AMIPDFTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum ParserType {
		early,
		ami,
		zero,
		one,
		two;
	}
	
	public enum PDFTidySVG {
		concat, // concatenate characters (maybe in beginText...endText)
		spaces  // add spaces using character widths
	}
	
	public AMIPDFTool() {
	}
	
    public AMIPDFTool(CProject cProject) {
    	this.cProject = cProject;
	}

    @Option(names = {"--debug"}, 
    		arity="0",
    		paramLabel="DEBUG",
   		    description = "debug level (experimental, AMI_ZERO, AMI_ONE, AMI_TWO)"
    		)
    private ParserDebug parserDebug = ParserDebug.AMI_BRIEF;

    @Option(names = {"--imagedir"}, 
    		arity="0..1",
    		paramLabel="IMAGE_DIR",
   		    description = "Directory for Image files created from PDF. Do not use/change this unless you are testing "
   		    		+ "or developing AMI as other components rely on this."
    		)
    private String pdfImagesDirname = "pdfimages/";
    
	@Option(names = {"--maxpages"}, 
    		arity="0..1",
   		    description = "maximum PDF pages. If less than actual pages, will repeat untill all pages processed. "
   		    		+ "The normal reason is that lists get full (pseudo-memory leak, this is a bug). If you encounter "
   		    		+ "out of memory errors, try setting this lower."
    		)
    private int maxpages = 5;
    
	@Option(names = {"--maxprimitives"}, 
    		arity="0..1",
   		    description = "maximum number pf SVG primitives. Some diagrams have hundreds of thousands of"
   		    		+ " graphics primitives and create quadratic or memory problems. Setting maxprimitives"
   		    		+ " allows the job to continue but loses data. We have to find a better approach."
    		)
    private int maxprimitives = 5000;
    
    /** this should be a Mixin, with SVGTool
     * NYI
     */
    @Option(names = {"--pages"},
    		arity = "1..*",
            description = "pages to extract; if omitted processes all")
    private List<Integer> pages = null;

    @Option(names = {"--parser"}, 
    		arity="1",
   		    description = "Parser type (early or ami). early is being phased out."
    		)
    private ParserType parserType = 
//        	ParserType.ami
        	ParserType.two
    	;
    
    @Option(names = {"--pdfimages"}, 
    		arity="0..1",
   		    description = "output PDFImages pages. Default true "
    		)
    private boolean outputPdfImages = true;
    
    @Option(names = {"--svgdir"}, 
    		arity="0..1",
   		    description = "Directory for SVG files created from PDF. Do not use/change this unless you are testing "
   		    		+ "or developing AMI as other components rely on this."
    		)
    private String svgDirectoryName = "svg/";

    @Option(names = {"--svgpages"}, 
    		arity="0..1",
   		    description = "output SVG pages. Default true"
    		)
    private boolean outputSVG = true;
    
    @Option(names = {"--tidysvg"}, 
    		arity="0..1",
   		    description = "Tidy SVG (currently text, default concat spaces"
    		)
    private List<PDFTidySVG> tidySVGList = Arrays.asList(new PDFTidySVG[]{PDFTidySVG.concat, PDFTidySVG.spaces});
    
    public static void main(String[] args) throws Exception {
    	AMIPDFTool amiProcessorPDF = new AMIPDFTool();
    	amiProcessorPDF.runCommands(args);
    }

	@Override
	protected void parseSpecifics() {
		printDebug();
		if (pages == null) {
			pages = new ArrayList<Integer>();
			pages.add(-1);
		}

	}

	@Override
	protected void runSpecifics() {
    	if (processTrees()) { 
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
	    }
    }

	private void printDebug() {
		System.out.println("maxpages            "+maxpages);
		System.out.println("svgDirectoryName    "+svgDirectoryName);
		System.out.println("outputSVG           "+outputSVG);
		System.out.println("imgDirectoryName    "+pdfImagesDirname);
		System.out.println("outputPDFImages     "+outputPdfImages);
		System.out.println("parserDebug         "+parserDebug);
		return;
	}

	protected void processTree() {
		System.out.println("cTree: "+cTree.getName());
		File pdfImagesDir = cTree.getExistingPDFImagesDir();
//		if (ParserType.early.equals(parserType)) {
		if (ParserDebug.AMI_ZERO.equals(parserDebug)) {
			docProcRunPDF();
		} else if (ParserDebug.AMI_TWO.equals(parserDebug)) {
			docProcRunPDF();
//			amiPDF();
		} else if (ParserType.ami.equals(parserType)) {
			amiPDF();
		}
	}

	private void amiPDF() {
		File inputPdf = cTree.getExistingFulltextPDF();
		if (inputPdf == null || !inputPdf.exists()) {
			LOG.warn("file does not exist: "+inputPdf);
		} else {
			boolean debug = false; // change 
			PageParserRunner pageParserRunner = new PageParserRunner(inputPdf, parserDebug, debug);
			pageParserRunner.setTidySVGList(tidySVGList);
			for (int pageIndex : pages) {
				pageParserRunner.runPages(cTree.getName(), pageIndex);
			}
		}
	}

	/** early approach from PDFBox1
	 * now uses PDFBox2 but soon to be obsoleted
	 */
    public void docProcRunPDF() {
		PDFDocumentProcessor pdfDocumentProcessor = cTree.getOrCreatePDFDocumentProcessor();
		pdfDocumentProcessor.setOutputSVG(outputSVG);
		pdfDocumentProcessor.setOutputPDFImages(outputPdfImages);
		pdfDocumentProcessor.setMaxPages(maxpages);
		pdfDocumentProcessor.setMaxPrimitives(maxprimitives);
		pdfDocumentProcessor.setParserType(parserType);
        cTree.setPDFDocumentProcessor(pdfDocumentProcessor);
        cTree.setForceMake(forceMake);
		cTree.processPDFTree();
    }


}

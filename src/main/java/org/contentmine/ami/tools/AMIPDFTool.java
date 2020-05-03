package org.contentmine.ami.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.pdf2svg2.PDFDocumentProcessor;
import org.contentmine.pdf2svg2.PageParserRunner;
import org.contentmine.pdf2svg2.PageParserRunner.ParserDebug;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

	@Command(
	name = "pdfbox",
	description = {
			"Convert PDFs to SVG-Text, SVG-graphics and Images.",
			"Does not process images, graphics or text. "
					+ "Often followed by ami-image and ami-xml."
					+ "EXAMPLE%n"
					+ "    ami -t /Users/pm286/projects/chess pdf%n"
					+ "        processes the chess CTree (which must contain fulltext.pdf%n"
					+ "        by default this produces svg/fulltext-page.<1...n>.svg and pdfimages/image.p.n.x_x.y_y.png%n"
	})
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
		spaces, // add spaces using character widths
		styles, // add empirical styles
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
   		    		+ " allows the job to continue but loses data. Some SVGs could be 150 Mbyte so selection"
   		    		+ " by user will be important"
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
    
    @Option(names = {"--pdf2html"}, 
    		paramLabel="PDF2HTML",
   		    description = "Use PDFBox pdf2Html to create automatic html (NYI)"
    		)
    private boolean pdf2html = false;
    
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
		System.out.println("pdf2html            "+pdf2html);
		System.out.println("imgDirectoryName    "+pdfImagesDirname);
		System.out.println("outputPDFImages     "+outputPdfImages);
		System.out.println("parserDebug         "+parserDebug);
		return;
	}

	protected boolean processTree() {
		processedTree = false;
		System.out.println("cTree: "+cTree.getName());
		File pdfImagesDir = cTree.getExistingPDFImagesDir();
		if (pdf2html) {
			pdf2html();
		}
//		if (ParserType.early.equals(parserType)) {
		boolean processed = false;
		if (ParserDebug.AMI_ZERO.equals(parserDebug)) {
			processedTree = docProcRunPDF();
		} else if (ParserDebug.AMI_TWO.equals(parserDebug)) {
			processedTree = docProcRunPDF();
		} else if (ParserDebug.AMI_BRIEF.equals(parserDebug)) {
			processedTree = docProcRunPDF();
//			amiPDF();
		} else if (ParserType.ami.equals(parserType)) {
			processedTree = amiPDF();
		} else {
			processedTree = false;
		}
		return processedTree;
	}

	private void pdf2html() {
		PDDocument pdDocument = null;
		File inputPdf = cTree.getExistingFulltextPDF();
		if (inputPdf == null || !inputPdf.exists()) {
			throw new RuntimeException("PDF file does not exist: " + inputPdf);
		}
		try {
			pdDocument = PDDocument.load(inputPdf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (pdDocument != null) {
			try {
				System.err.println("DOC"+pdDocument.getDocumentInformation());
				runPdf2Html(pdDocument);
			} catch (IOException e) {
				throw new RuntimeException("run pdf", e);
			}
		}
	}

	private void runPdf2Html(PDDocument document) throws IOException {
//		AMIPDF2HTML pdf2HTML = AMIPDF2HTML.createAMIPDF2HTML();
//		LOG.debug("runPDF "+document);
//		pdf2HTML.readDocument(document);
		PDFTextStripper textStripper = new PDFTextStripper();
		String text = textStripper.getText(document);
		File textFile = getTextFile();
		IOUtils.write(text, new FileOutputStream(textFile), "UTF-8");
		System.out.println("wrote "+textFile.getAbsoluteFile());
	}

	private File getTextFile() {
		return new File(getCTree().getDirectory(), "fulltext.pdf.txt");
	}

	private boolean amiPDF() {
		processedTree = true;
		File inputPdf = cTree.getExistingFulltextPDF();
		if (inputPdf == null || !inputPdf.exists()) {
			LOG.warn("file does not exist: "+inputPdf);
			processedTree = false;
		} else {
			boolean debug = false; // change 
			PageParserRunner pageParserRunner = new PageParserRunner(inputPdf, parserDebug, debug);
			pageParserRunner.setTidySVGList(tidySVGList);
			for (int pageIndex : pages) {
				pageParserRunner.runPages(cTree.getName(), pageIndex);
				System.out.println(">finished: "+pageIndex);
			}
		}
		return processedTree;
	}

	/** early approach from PDFBox1
	 * now uses PDFBox2 but soon to be obsoleted
	 */
    public boolean docProcRunPDF() {
		PDFDocumentProcessor pdfDocumentProcessor = cTree.getOrCreatePDFDocumentProcessor();
		pdfDocumentProcessor.setOutputSVG(outputSVG);
		pdfDocumentProcessor.setOutputPDFImages(outputPdfImages);
		pdfDocumentProcessor.setMaxPages(maxpages);
		pdfDocumentProcessor.setMaxPrimitives(maxprimitives);
		pdfDocumentProcessor.setParserType(parserType);
		pdfDocumentProcessor.setTidySVGList(tidySVGList);
        cTree.setPDFDocumentProcessor(pdfDocumentProcessor);
        cTree.setForceMake(getForceMake());
		processedTree = cTree.processPDFTree();
		return processedTree;
    }


}

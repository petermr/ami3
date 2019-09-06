package org.contentmine.ami.tools;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.pdf2svg2.PDFDocumentProcessor;

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
	
	public AMIPDFTool() {
	}
	
    public AMIPDFTool(CProject cProject) {
    	this.cProject = cProject;
	}

	@Option(names = {"--maxpages"}, 
    		arity="0..1",
   		    description = "maximum PDF pages. If less than actual pages, will repeat untill all pages processed. "
   		    		+ "The normal reason is that lists get full (pseudo-memory leak, this is a bug). If you encounter "
   		    		+ "out of memory errors, try setting this lower."
    		)
    private int maxpages = 5;
    
    /** this should be a Mixin, with SVGTool
     * NYI
     */
    @Option(names = {"--pages"},
    		arity = "1..*",
            description = "pages to extract")
    private List<Integer> pages = null;

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
    
    @Option(names = {"--imagedir"}, 
    		arity="0..1",
    		paramLabel="IMAGE_DIR",
   		    description = "Directory for Image files created from PDF. Do not use/change this unless you are testing "
   		    		+ "or developing AMI as other components rely on this."
    		)
    private String pdfImagesDirname = "pdfimages/";
    
    @Option(names = {"--pdfimages"}, 
    		arity="0..1",
   		    description = "output PDFImages pages. Default true "
    		)
    private boolean outputPdfImages = true;
    
    public static void main(String[] args) throws Exception {
    	AMIPDFTool amiProcessorPDF = new AMIPDFTool();
    	amiProcessorPDF.runCommands(args);
    }

	@Override
	protected void parseSpecifics() {
		printDebug();
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
		return;
	}

	protected void processTree() {
		System.out.println("cTree: "+cTree.getName());
		File pdfImagesDir = cTree.getExistingPDFImagesDir();
        runPDF();
	}


    public void runPDF() {
		PDFDocumentProcessor pdfDocumentProcessor = cTree.getOrCreatePDFDocumentProcessor();
		pdfDocumentProcessor.setOutputSVG(outputSVG);
		pdfDocumentProcessor.setOutputPDFImages(outputPdfImages);
		pdfDocumentProcessor.setMaxPages(maxpages);
        cTree.setPDFDocumentProcessor(pdfDocumentProcessor);
        cTree.setForceMake(forceMake);
		cTree.processPDFTree();
    }


}

package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.norma.pdf.GrobidRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses bitmaps
 * 
 * @author pm286
 *
 */
@Command(
name = "ami-grobid", 
aliases = "grobid",
version = "ami-grobid 0.1",
description = {
		"Runs grobid.",
		"Internally, this uses:%n"
		+ "java -jar /Users/pm286/workspace/grobid/grobid-0.5.3/grobid-core/build/libs/grobid-core-0.5.3-onejar.jar "
		+ "  -gH /Users/pm286/workspace/grobid/grobid-0.5.3/grobid-home"
		+ "  -teiCoordinates "
		+ "  -exe processFullText"
		,
		"This is very slow as grobid has to boot each time but it only has to be done once. "
		+ "We can set up a server if it becomes useful. "
		+ ""
})
public class AMIGrobidTool extends AbstractAMITool {
	private static final String FULLTEXT_TEI_HTML = "fulltext.tei.html";

	private static final String FULLTEXT_TEI_XML = "fulltext.tei.xml";

	private static final Logger LOG = Logger.getLogger(AMIGrobidTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
    @Option(names = {"--exe"},
    		arity = "1",
    		defaultValue = "processFullText",
            description = "Grobid option from: 	"
        		+ "close,"
        		+ " processFullText, processHeader, processDate, processAuthorsHeader,"
        		+ " processAuthorsCitation, processAffiliation, processRawReference, processReferences,"
        		+ ""
        		+ " createTraining, createTrainingMonograph, createTrainingBlank, createTrainingCitationPatent,"
        		+ ""
        		+ " processCitationPatentTEI, processCitationPatentST36, processCitationPatentTXT,"
        		+ " processCitationPatentPDF, processPDFAnnotation")
    private String exeOption = null;

	private File pdfImagesDir;

	private File outputDir;

    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIGrobidTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIGrobidTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIGrobidTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
		System.out.println("exeOption            " + exeOption);
		System.out.println();
	}


    @Override
    protected void runSpecifics() {
    	if (processTrees()) { 
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree");
	    }
    }

	protected boolean processTree() {
//		this.cTree = cTree;
		System.out.println("\n" + "grobid CTree: "+cTree.getName());
		processedTree = false;
		try {
			runGrobid();
			convertTEIToHtml();
			processedTree = true;
		} catch (Exception e) {
			LOG.error("Bad read: "+cTree+" ("+e.getMessage()+")");
		}
		return processedTree;
	}
	
	private void runGrobid() {
		
		File inputDir = cTree.getDirectory();
		File inputFile = new File(inputDir, CTree.FULLTEXT_PDF);
		outputDir = new File(cTree.getDirectory(), "tei/");
		outputDir.mkdirs();
		File outputFile = new File(outputDir, FULLTEXT_TEI_XML);
		boolean debug = false;
		if (CMFileUtil.shouldMake(forceMake, outputFile , debug, inputFile)) { 
			GrobidRunner grobidRunner = new GrobidRunner();
			grobidRunner.setTryCount(200); // fix this later
			grobidRunner.convertPDFToTEI(inputDir, outputDir, exeOption);
		}
	}
	
	private void convertTEIToHtml() {
		File inputFile = new File(outputDir, FULLTEXT_TEI_XML);
		File outputFile = new File(outputDir, FULLTEXT_TEI_HTML);
		boolean debug = false;
		if (true || CMFileUtil.shouldMake(forceMake, outputFile , debug, inputFile)) { 
			TEI2HtmlConverter converter = new TEI2HtmlConverter();
			HtmlHtml html = converter.createHtmlElement(inputFile);
			if (html != null) {
				try {
					XMLUtil.debug(html, outputFile, 1);
				} catch (IOException e) {
					System.err.println("Cannot write Html from TEI");
				}
			}
		}
	}

	


}

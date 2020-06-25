package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMI.CTreeOptions.BaseOptions;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.norma.NormaArgProcessor;
import org.contentmine.norma.NormaTransformer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/** transforms file using XSLT and maybe other things
 * 
 * @author pm286
 *
 */
@Command(
name = "transform",
description = {
		"Runs XSLT transformation on XML (NYFI)."
})
public class AMITransformTool extends AbstractAMITool {
	private static final Logger LOG = LogManager.getLogger(AMITransformTool.class);
public enum Tidier {
		jsoup,
		tidy,
	}
	

	protected static class XSLOptions {
	    @Option(names = {"-x", "--stylesheet"}, paramLabel = "XSL", order = 24,
	    		arity = "1",
	    		defaultValue="nlm2html",
	            description = "XSLT stylesheet")
	    protected String stylesheet;
	    
	    @Option(names = {"--tidy"}, paramLabel = "TIDY", order = 25,
	    		arity = "0..1",
	    		fallbackValue = "jsoup",
	            description = "HTML tidier. The options tend to vary because new methods are developed and old ones stagnate")
	    protected Tidier tidier = null;
	    
	}

	@ArgGroup(exclusive = true, multiplicity = "0..1", heading = "", order = 21)
	XSLOptions xslOptions = new XSLOptions();

    
    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMITransformTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMITransformTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMITransformTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
    	
    	super.parseSpecifics();
	}


    @Override
    protected void runSpecifics() {
    	if (cProject != null) {
    		processProject();
    	}
    }

	protected void processProject() {
		if (xslOptions.tidier != null) {
			runTidy();
		} else {
			runNorma();
		}
//		CommandProcessor commandProcessor = new CommandProcessor(cProject.getDirectory());
//		commandProcessor.runNormaIfNecessary();
		
//		NormaArgProcessor argProcessor = new NormaArgProcessor();
//		NormaTransformer normaTransformer = new NormaTransformer(argProcessor);
//		System.out.println("cTree: "+cTree.getName());
	}
	
	private void runNorma() {
		NormaArgProcessor argProcessor = new NormaArgProcessor();
		argProcessor.setStandalone(true);
		for (CTree cTree : cProject.getOrCreateCTreeList()) {
			transformTreeToScholarlyHtml(argProcessor, cTree);
		}

			
//			String args = "-i fulltext.xml -o scholarly.html --transform nlm2html --project "+cProject.getDirectory();
//			LOG.debug("running NORMA "+args);
//			new Norma().run(args);
	}
	
	private void runTidy() {
		Document doc = null;
		System.out.println("reading " + input());
		try {
//			doc = Jsoup.connect("https://en.wikipedia.org/").get();
			doc = Jsoup.connect(input()).get();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("D "+doc.title());
		Elements newsHeadlines = doc.select("#mp-itn b a");
		for (Element headline : newsHeadlines) {
		  System.out.println("%s\n\t%s" + 
		    headline.attr("title") + headline.absUrl("href"));
		}	
	}

	private void transformTreeToScholarlyHtml(NormaArgProcessor argProcessor, CTree cTree) {
		File existingFulltextXML = cTree.getExistingFulltextXML();
		File scholarlyHtmlFile = cTree.getOrCreateScholarlyHtmlFile();
		NormaTransformer normaTransformer = new NormaTransformer(argProcessor);
		normaTransformer.setCurrentCTree(cTree);
		if (CMFileUtil.shouldMake(getForceMake(), scholarlyHtmlFile, existingFulltextXML)) {
			transformXML2Html(existingFulltextXML, scholarlyHtmlFile, normaTransformer);
		}
	}

	private void transformXML2Html(File existingFulltextXML, File scholarlyHtmlFile, NormaTransformer normaTransformer) {
		normaTransformer.setInputFile(existingFulltextXML);
		String htmlString = normaTransformer.createAndApplyXSLDocument(NormaTransformer.NLM2HTML);
		try {
			FileUtils.write(scholarlyHtmlFile, htmlString, CMineUtil.UTF8_CHARSET);
		} catch (IOException e) {
			LOG.error("Cannot write Htmlfile: "+scholarlyHtmlFile, e);
		}
	}

}

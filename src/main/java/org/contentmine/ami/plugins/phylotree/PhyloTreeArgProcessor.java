package org.contentmine.ami.plugins.phylotree;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.phylotree.nexml.NWKTree;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNEXML;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtu;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlTree;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.graphics.svg.text.SVGPhrase;
import org.contentmine.norma.editor.Extraction;
import org.contentmine.norma.image.ocr.HOCRReaderOLD;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class PhyloTreeArgProcessor extends AMIArgProcessor {

	public enum Message {
		ERR_BAD_SYNTAX("syntax of the field did not fit regex"),
		ERR_PHYLO_BAD_INPUT("input does not exist or is not an image"),
		ERR_PHYLO_NO_COMPLETE("the analysis process fails to terminate"),
		ERR_PIXEL_TREE_CYCLE("the tree contains a cycle"),
		WARN_SPECIES_LOOKUP_FAIL("the species cannot be looked up in the online resource"),
		WARN_EGID_LOOKUP_FAIL("the EGID cannot be looked up in the online resource"),
		WARN_NEWICK_NULL("there is only null;' in the output newick file"),
		WARN_EMPTY_TIP_LABEL("empty tip label"),
		WARN_GARBLED_TIP_LABEL("garbeld tip label"),
		WARN_MISSING_TIP("tip/s are missing from the tree");
		
		private String msg;

		private Message(String msg) {
			this.msg = msg;
		}
		
		public String getMsg() {
			return msg;
		}

	}
	public static final Logger LOG = Logger.getLogger(PhyloTreeArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private PhyloCore phyloCore;
	private static final String TREES = "trees";

	public PhyloTreeArgProcessor() {
		super();
		phyloCore = new PhyloCore(this);
	}

	public PhyloTreeArgProcessor(String[] args) {
		this();
		super.parseArgs(args);
	}

	public PhyloTreeArgProcessor(String argString) {
		this();
		super.parseArgs(argString);
	}

	// =============== METHODS ==============

	public void parseNewickFile(ArgumentOption option, ArgIterator argIterator) {
		phyloCore.nexmlProcessor.setNewickFilename(argIterator.getString(option));
		PROJECT_LOG().info("newick file");
	}
	

	public void parseNexmlFile(ArgumentOption option, ArgIterator argIterator) {
		phyloCore.nexmlProcessor.setNexmlFilename(argIterator.getString(option));
		
	}
	
	public void parseSVGFile(ArgumentOption option, ArgIterator argIterator) {
		phyloCore.setSvgFilename(argIterator.getString(option));
	}
	
	public void parseHOCRSVGFile(ArgumentOption option, ArgIterator argIterator) {
		phyloCore.setHocrSvgFilename (argIterator.getString(option));
	}
	
	public void parseHOCRHTMLFile(ArgumentOption option, ArgIterator argIterator) {
		phyloCore.setHocrHtmlFilename(argIterator.getString(option));
	}
	
	public void parseSpeciesPattern(ArgumentOption option, ArgIterator argIterator) {
		phyloCore.nexmlProcessor.setSpeciesPatternString(argIterator.getString(option));
//		speciesPatternXML = XMLUtil.parseQuietlyToDocument(speciesPatternInputStream).getRootElement();
	}

	/** this looks WRONG.
	 * we shouldn't iterate over input here.
	 * 
	 * @param option
	 */
	public void runPhylo(ArgumentOption option) {
		LOG.warn("runPhylo on: "+inputList+"; looks invalid strategy");
		for (String input : inputList) {
			File inputFile = new File(currentCTree.getDirectory(), input);
			getPhyloCore().createTree(inputFile);
		}
	}
	
	public void output(ArgumentOption option) {
		phyloCore.outputResults();
		outputResultsElement(option);
	}

	public void outputResultsElement(ArgumentOption option) {
		ResultsElement resultsElement = new ResultsElement(TREES);
		LOG.trace("outputResultElement NYI "+output+"; need to add tree");
	}
	
	public void summarize(ArgumentOption option) {
		LOG.trace(PROJECT_LOG().toXML());
	}
	
	// =============================

	public File getCurrentCTreeDirectory() {
		return currentCTree == null ? null : currentCTree.getDirectory();
	}

	public NexmlNEXML getNexml() {
		return phyloCore.nexmlProcessor.getNexml();
	}

	public boolean mergeOCRAndPixelTree(File infile) {
		try {
			return phyloCore.mergeOCRAndPixelTree(infile);
		} catch (Exception e) {
			LOG.error("ERROR: "+e);
			return false;
		}
	}

	public HOCRReaderOLD getOrCreateHOCRReader() {
		return phyloCore.getOrCreateHOCRReader();
	}

	public void setSpeciesPatternInputString(String patternString) {
		phyloCore.nexmlProcessor.setSpeciesPatternInputString(patternString);
	}

	public void setSpeciesPattern(Pattern speciesPattern) {
		phyloCore.nexmlProcessor.setSpeciesPattern(speciesPattern);
	}

	public void setOutputRoot(String root) {
		phyloCore.setOutputRoot(root);
	}

	public void setOutputDir(File file) {
		phyloCore.setOutputDir(file);
	}

	public static void convertPngToHTML_SVG_NEXML_NWK(File infile, File outdir) throws IOException, InterruptedException {
		PhyloCore.convertPngToHTML_SVG_NEXML_NWK(infile, outdir);
	}

	public void mergeFiles(File hocrFile, File nexmlFile) throws Exception {
		phyloCore.mergeFiles(hocrFile, nexmlFile);
	}

	public InputStream getOrCreateSpeciesPatternInputStream() {
		return phyloCore.nexmlProcessor.getOrCreateSpeciesPatternInputStream();
	}

	public void matchPhrasesToNodes(List<SVGPhrase> phraseList,
			NexmlTree nexmlTree) {
		phyloCore.matchPhrasesToNodes(phraseList, nexmlTree);
	}

	public void annotateOtuWithExtractions(NexmlOtu otu, List<Extraction> extractionList) {
		phyloCore.nexmlProcessor.annotateOtuWithExtractions(otu, extractionList);
	}
	
	public PhyloCore getPhyloCore() {
		return phyloCore;
	}
}

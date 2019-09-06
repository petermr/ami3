package org.contentmine.ami.plugins.simple;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.cproject.files.ResultsElement;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SimpleArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(SimpleArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected List<String> words;

	public SimpleArgProcessor() {
		super();
	}

	public SimpleArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public SimpleArgProcessor(String argString) {
		this(argString.split(WHITESPACE));
	}

	// =============== METHODS ==============

	public void parseSimple(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
	}
	
	public void countWords(ArgumentOption option) {
		words = currentCTree.extractWordsFromScholarlyHtml();
	}

	public void outputWordCounts(ArgumentOption option) {
		String outputFilename = getOutput();
		if (!CTree.isReservedFilename(outputFilename)) {
			throw new RuntimeException("Output is not a reserved file: "+outputFilename);
		}
		ResultsElement resultsElement = new ResultsElement();
		ResultElement resultElement = new ResultElement();
		resultElement.setValue("wordCount", String.valueOf(words.size()));
		resultsElement.appendChild(resultElement);
		getOrCreateContentProcessor().writeResults(outputFilename, resultsElement);
	}
	
	// =============================

	@Override
	/** parse args and resolve their dependencies.
	 * 
	 * (don't run any argument actions)
	 * 
	 */
	public void parseArgs(String[] args) {
		super.parseArgs(args);
	}

	
}

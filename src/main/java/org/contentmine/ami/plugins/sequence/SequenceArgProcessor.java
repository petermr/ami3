package org.contentmine.ami.plugins.sequence;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;
import org.contentmine.eucl.euclid.IntRange;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SequenceArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = Logger.getLogger(SequenceArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private IntRange lengthRange;

	public SequenceArgProcessor() {
		super();
	}

	public SequenceArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public SequenceArgProcessor(String argString) {
		this(argString.split(WHITESPACE));
	}

	// =============== METHODS ==============
	
	public void initSequences(ArgumentOption option) {
		createAndStoreNamedSearchers(option);
	}

	public void parseTypes(ArgumentOption option, ArgIterator argIterator) {
		createSearcherList(option, argIterator);
	}

	public void parseLength(ArgumentOption option, ArgIterator argIterator) {
		lengthRange = argIterator.getIntRange(option);
	}

	public void runExtractSequences(ArgumentOption option) {
		searchSectionElements();
	}

	public void outputSequences(ArgumentOption option) {
		getOrCreateContentProcessor().outputResultElements(option.getName(), this);
	}
	
	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
//		summaryMethods = argIterator.getStrings(option);
		LOG.debug("summary methods not yet written");
	}
	
	public void finalSummary(ArgumentOption option) {
		LOG.debug("final summary not yet written");
	}

	// =============================



}

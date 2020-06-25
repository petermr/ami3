package org.contentmine.ami.plugins.dummy;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.NamedPattern;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;

/** 
 * Processes commandline arguments.
 * Stores variable.
 * Adds methods called by reflection
 * 
 * @author pm286
 */
public class DummyArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = LogManager.getLogger(DummyArgProcessor.class);
	
/** CONSTRUCTORS - generally leave untouched */
	
	public DummyArgProcessor() {
		super();
	}

	public DummyArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public DummyArgProcessor(String argString) {
		this(argString.split(WHITESPACE));
	}

	// =============== METHODS ==============
	
	/**
	 * Initialises (static) variables for plugin.
	 * 
	 * Delete if not required
	 * 
	 * @param option
	 */
	public void initDummy(ArgumentOption option) {
		createAndStoreNamedSearchers(option);
	}

	/** parse option within plugin.
	 * 
	 * mots plugins will have one or more named options
	 * options are normally defined by an element in args.xml , e.g. 
	 * <arg name="foo"> 
	 * </arg>
	 * 
	 * @param option
	 * @param argIterator
	 */
	public void parseFooOption(ArgumentOption option, ArgIterator argIterator) {
	}

	public void runExtractDummy(ArgumentOption option) {
		searchSectionElements();
	}

	public void outputDummy(ArgumentOption option) {
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

	/** create Subclassed Searcher.
	 * 
	 * //PLUGIN
	 * 
	 * Most plugins should Override this and create a FooSearcher.
	 * 
	 * @param namedPattern 
	 * @return subclassed Plugin
	 */
	protected AMISearcher createSearcher(NamedPattern namedPattern) {
		return new DummySearcher(this, namedPattern);
	}



}

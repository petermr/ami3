package org.contentmine.ami.plugins.species;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.dictionary.gene.HGNCDictionary;
import org.contentmine.ami.dictionary.species.TaxDumpGenusDictionary;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.NamedPattern;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;
import org.contentmine.cproject.files.ResultsElement;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SpeciesArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = LogManager.getLogger(SpeciesArgProcessor.class);
	private Boolean expandAbbreviations;
public SpeciesArgProcessor() {
		super();
	}

	public SpeciesArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public SpeciesArgProcessor(String argString) {
		this(argString.split(WHITESPACE));
	}

	// =============== METHODS ==============
	
	public void initSpecies(ArgumentOption option) {
		createAndStoreNamedSearchers(option);
	}

	public void parseTypes(ArgumentOption option, ArgIterator argIterator) {
		createSearcherList(option, argIterator);
	}

	public void parseAbbreviations(ArgumentOption option, ArgIterator argIterator) {
		expandAbbreviations = argIterator.getBoolean(option);
	}

	public void runExtractSpecies(ArgumentOption option) {
		LOG.trace("SEARCH SPECIES: extract");
		searchSectionElements();
	}

	public void outputSpecies(ArgumentOption option) {
		getOrCreateContentProcessor().outputResultElements(option.getName(), this);
	}
	
	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
//		summaryMethods = argIterator.getStrings(option);
		LOG.debug("summary methods not yet written");
	}

	@Override
	public void finalLookup(ArgumentOption option) {
		LOG.debug("final species lookup NYI; please add code: names are: "+lookupNames+"; override");
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
		return new SpeciesSearcher(this, namedPattern);
	}

	@Override
	protected ResultsElement createResultsElement() {
		return new SpeciesResultsElement();
	}

	/** currently only HGNC hardcoded but will allow dictionary choice later
	 * 
	 * @return
	 */
	public DefaultAMIDictionary getOrCreateCurrentDictionary() {
		if (currentDictionary == null) {
			currentDictionary = new TaxDumpGenusDictionary();
		}
		return currentDictionary;
	}


}

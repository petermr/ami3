package org.contentmine.ami.plugins.identifier;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.NamedPattern;
import org.contentmine.ami.plugins.regex.RegexArgProcessor;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;

import nu.xom.Element;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class IdentifierArgProcessor extends RegexArgProcessor {
	
	
	public static final Logger LOG = LogManager.getLogger(IdentifierArgProcessor.class);
public IdentifierArgProcessor() {
		super();
	}

	public IdentifierArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public IdentifierArgProcessor(String argString) {
		this(argString.split(WHITESPACE));
	}

	// =============== METHODS ==============
	
	public void initIdentifiers(ArgumentOption option) {
		createAndStoreNamedSearchers(option);
	}

	public void parseRegex(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens= argIterator.createTokenListUpToNextNonDigitMinus(option);
		createRegexElementList(option, tokens); // compoundRegexList
		createSearchers();
	}

	private void createSearchers() {
		ensureSearcherList();
		for (Element regexElement : regexElementList) {
			NamedPattern namedPattern = NamedPattern.createFromRegexElement(regexElement);
			createSearcherAndAddToMap(namedPattern);
		}
		LOG.trace("MAP: "+searcherByNameMap);
	}

	public void parseTypes(ArgumentOption option, ArgIterator argIterator) {
		createSearcherList(option, argIterator);
	}

	public void runExtractIdentifiers(ArgumentOption option) {
		searchSectionElements();
	}

	public void outputIdentifiers(ArgumentOption option) {
		getOrCreateContentProcessor().outputResultElements(option.getName(), this);
	}
	
	// =============================

}

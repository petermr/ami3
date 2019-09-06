package org.contentmine.ami.plugins.regex;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;
import org.contentmine.cproject.files.ContentProcessor;
import org.contentmine.cproject.files.ResourceLocation;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class RegexArgProcessor extends AbstractSearchArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(RegexArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	static final String TILDE = "~";
	static final String TILDE_SUFFIX = "(?:[^\\\\s]*\\\\p{Punct}?)";
	
	private Map<String, ResultsElement> resultsByCompoundRegex;
	protected List<String> words;
	protected List<Element> regexElementList;
	
	public RegexArgProcessor() {
		super();
		this.addVariableAndExpandReferences(TILDE, TILDE_SUFFIX);
	}

	public RegexArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public RegexArgProcessor(String argString) {
		this(argString.split(WHITESPACE));
	}

	
	// =============== METHODS ==============

	public void parseRegex(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens= argIterator.createTokenListUpToNextNonDigitMinus(option);
		createCompoundRegexes(option, tokens);
	}

	public void runRegex(ArgumentOption option) {
		runRegex();
	}

	public void outputResultElements(ArgumentOption option) {
		outputResultElements(option.getName());
	}

	// =========================

	/** create Subclassed Searcher.
	 * 
	 * //PLUGIN
	 * 
	 * Most plugins should Override this and create a FooSearcher.
	 * 
	 * @param argProcessor
	 * @param compoundRegex
	 * @return subclassed Plugin
	 */
	public AMISearcher createSearcher(AMIArgProcessor argProcessor, CompoundRegex compoundRegex) {
		RegexSearcher regexSearcher = RegexSearcher.createSearcher(argProcessor);
		regexSearcher.setCompoundRegex(compoundRegex);
		return regexSearcher;
	}


	/** .
	 * 
	 * @param name of option
	 */
	private void outputResultElements(String name) {
		ContentProcessor currentContentProcessor = currentCTree.getOrCreateContentProcessor();
		currentContentProcessor.clearResultsElementList();
		if (resultsByCompoundRegex == null) {
			LOG.warn("have not run regex (runRegex)");
			return;
		}
		for (CompoundRegex compoundRegex : compoundRegexList) {
			String regexTitle = compoundRegex.getTitle();
			ResultsElement resultsElement = resultsByCompoundRegex.get(regexTitle);
			resultsElement.setTitle(regexTitle);
			currentContentProcessor.addResultsElement(resultsElement);
		}
		currentContentProcessor.createResultsDirectoriesAndOutputResultsElement(name);
	}

	private void runRegex() {
		LOG.debug("Running regex");
		ensureSectionElements();
		resultsByCompoundRegex = new HashMap<String, ResultsElement>();
		for (CompoundRegex compoundRegex : compoundRegexList) {
			AMISearcher regexSearcher = createSearcher(this, compoundRegex);
			ResultsElement resultsElement = regexSearcher.search(sectionElements, createResultsElement());
			resultsByCompoundRegex.put(compoundRegex.getTitle(), resultsElement);
		}
	}

	@Override
	/** parse args and resolve their dependencies.
	 * 
	 * (don't run any argument actions)
	 * 
	 */
	public void parseArgs(String[] args) {
		super.parseArgs(args);
	}

	private void ensureRegexElementList() {
		if (regexElementList == null) {
			regexElementList = new ArrayList<Element>();
		}
	}

	protected void createRegexElementList(ArgumentOption option, List<String> tokens) {
			List<String> regexLocations = option.processArgs(tokens).getStringValues();
			ensureRegexElementList();
			for (String regexLocation : regexLocations) {
				LOG.trace("RegexLocation "+regexLocation);
				try {
	//				InputStream is = new ResourceLocation().getInputStreamHeuristically(regexLocation);
					InputStream is = new ResourceLocation().getInputStreamHeuristically(AMIArgProcessor.class, regexLocation);
					Element rawCompoundRegex = new Builder().build(is).getRootElement();
					List<Element> elements = XMLUtil.getQueryElements(rawCompoundRegex, ".//*[local-name()='regex']");
					regexElementList.addAll(elements);
				} catch (Exception e) {
					LOG.debug("RXXX "+regexLocations);
					LOG.error("Cannot parse regexLocation: ("+e+")"+regexLocation);
				}
			}
		}

	public CompoundRegexList getOrCreateCompoundRegexList() {
		if (compoundRegexList == null) {
			compoundRegexList = new CompoundRegexList();
		}
		return compoundRegexList;
	}

	protected void createCompoundRegexes(ArgumentOption option, List<String> tokens) {
			List<String> regexLocations = option.processArgs(tokens).getStringValues();
			getOrCreateCompoundRegexList();
			for (String regexLocation : regexLocations) {
				LOG.trace("RegexLocation "+regexLocation);
				try {
	//				InputStream is = new ResourceLocation().getInputStreamHeuristically(regexLocation);
					InputStream is = new ResourceLocation().getInputStreamHeuristically(AMIArgProcessor.class, regexLocation);
					if (is == null) {
						throw new RuntimeException("cannot find regex: "+regexLocation);
					}
					CompoundRegex compoundRegex = readAndCreateCompoundRegex(is);
					compoundRegexList.add(compoundRegex);
				} catch (Exception e) {
					LOG.debug("RX "+regexLocations);
					LOG.error("Cannot parse regexLocation: ("+e+")"+regexLocation);
				}
				
			}
		}

	/** creates a regex from InputStream if possible
	 * 	 * 
	 * @param file
	 * @param is TODO
	 * @return null if not a regex file
	 * @exception RuntimeException if cannot read/parse
	 */
	public CompoundRegex readAndCreateCompoundRegex(InputStream is) {
		Element rootElement = null;
		try {
			Document doc = new Builder().build(is);
			rootElement = doc.getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot read or parse regexInputStream", e);
		}
		return new CompoundRegex(this, rootElement);
	}

	@Override
	public void parseSearch(ArgumentOption option, ArgIterator argIterator) {
		// TODO Auto-generated method stub
		
	}

}

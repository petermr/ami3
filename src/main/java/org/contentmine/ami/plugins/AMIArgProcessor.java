package org.contentmine.ami.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.plugins.regex.CompoundRegexList;
import org.contentmine.ami.plugins.regex.RegexComponent;
import org.contentmine.ami.plugins.word.WordCollectionFactory;
import org.contentmine.ami.wordutil.WordSetWrapper;
import org.contentmine.cproject.args.AbstractTool;
import org.contentmine.cproject.args.ArgIterator;
import org.contentmine.cproject.args.ArgumentOption;
import org.contentmine.cproject.args.ValueElement;
import org.contentmine.cproject.args.VersionManager;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.ContentProcessor;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.cproject.lookup.AbstractLookup;
import org.contentmine.cproject.lookup.DefaultStringDictionary;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.NormaArgProcessor;

import nu.xom.Element;

/** 
 * Processes commandline arguments.
 * for Norma
 * 
 * @author pm286
 */
public class AMIArgProcessor extends NormaArgProcessor {
	
	public static final Logger LOG = LogManager.getLogger(AMIArgProcessor.class);
private static final String AMI = "ami";
	private static final String ARG_PROCESSOR = "ArgProcessor";
	protected static String RESOURCE_NAME_TOP = NAConstants.AMI_RESOURCE;
	protected static String PLUGIN_RESOURCE = RESOURCE_NAME_TOP+"/plugins";
	public final static String DICTIONARY_RESOURCE = PLUGIN_RESOURCE+"/dictionary";
	// should this have a slash?
	private static String ARGS_RESOURCE = "/"+PLUGIN_RESOURCE+"/"+"args.xml";
	public static final VersionManager AMI_PLUGIN_VERSION_MANAGER = new VersionManager();


	protected static final String NAME = "name";
	public final static String ABBREVIATION = "abbreviation";
	public final static String IGNORE = "ignore";
	public final static String PRESERVE = "preserve";
	public final static String CAPITALIZED = "capitalized";
	public final static String ACRONYM = "acronym";
	public final static List<String> CASE_TYPES = Arrays.asList(
	new String[]{
			IGNORE,
			ABBREVIATION,
			PRESERVE
	});
	public final static List<String> WORD_TYPES = Arrays.asList(
	new String[]{
			ABBREVIATION,
			ACRONYM,
			CAPITALIZED
	});
	

	
	
	private Integer[] contextCount = new Integer[] {98, 98};
	private List<String> params;
	
	private String plugin;
    Map<String,AbstractLookup> lookupInstanceByName;
	protected CompoundRegexList compoundRegexList;
	protected List<? extends Element> sectionElements;
	protected List<String> lookupNames;
	protected WordCollectionFactory wordCollectionFactory;
	protected HashMap<String, AMISearcher> searcherByNameMap; // req
	// searching
	protected List<AMISearcher> searcherList; // req
	protected DefaultAMIDictionary currentDictionary;
	private List<String> wordCaseList = new ArrayList<String>();
	private Boolean stemming;
	private List<WordSetWrapper> stopwordSetList;
	public List<String> chosenWordTypes;
	
	public AMIArgProcessor() {
		super();
		readArgsResourcesIntoOptions();
	}

	public static VersionManager getVersionManager() {
		return AMI_PLUGIN_VERSION_MANAGER;
	}

	private void readArgsResourcesIntoOptions() {
		// the default ami options
		String args = ARGS_RESOURCE;
		super.readArgumentOptions(AMIArgProcessor.class, args);
		LOG.trace("args "+ARGS_RESOURCE);
		// the plugin-specific options
		super.readArgumentOptions(createPluginArgsResourceName());
	}

	public AMIArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}
	
	public AMIArgProcessor(String cmd) {
		this(cmd.split("\\s+"));
	}

	public AMIArgProcessor(AbstractTool abstractTool) {
		super(abstractTool);
	}

	protected String createPluginArgsResourceName() {
		String clazz = this.getClass().getSimpleName();
		plugin = clazz.replace(ARG_PROCESSOR, "").toLowerCase();
		String resourceName = null;
		if (plugin.equals(AMI)) {
			// no ami/ subdirectory
			resourceName = AMIArgProcessor.PLUGIN_RESOURCE + "/"+ARGS_XML;
		} else {
			resourceName = AMIArgProcessor.PLUGIN_RESOURCE + "/"+plugin+"/"+ARGS_XML;
		}
//		return resourceName;
		return "/"+resourceName;
	}

	// ============= METHODS =============
	
	/** caseSensitive?
	 * 
	 * @param option list of methods (none gives help)
	 * @param argIterator
	 */
	public void parseCase(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		wordCaseList = new ArrayList<String>();
		if (tokens.size() == 0) {
			wordCaseList.add(PRESERVE);
		} else {
			wordCaseList = tokens;
		}
		checkWordCaseList();
	}



	/** use stemming?
	 * 
	 * will have to use import org.apache.lucene.analysis.en.PorterStemFilter;
	 * 
	 * @param option list of methods (none gives help)
	 * @param argIterator
	 */
	public void parseStem(ArgumentOption option, ArgIterator argIterator) {
		stemming = argIterator.getBoolean(option);
	}
	
	public AMIArgProcessor setStemming(Boolean stemming) {
		this.stemming = stemming;
		return this;
	}

	public void parseStopwords(ArgumentOption option, ArgIterator argIterator) {
		List<String> stopwordLocations = argIterator.createTokenListUpToNextNonDigitMinus(option);
		populateStopwordSetList(stopwordLocations);
	}

	public AMIArgProcessor setStopwords(List<String> stopwordLocations) {
		populateStopwordSetList(stopwordLocations);
		return this;
	}


	public void parseContext(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			throw new IllegalArgumentException("required argument/s missing");
		}
		contextCount[0] = new Integer(tokens.get(0));
		if (tokens.size() == 2) {
			contextCount[1] = new Integer(tokens.get(1));
		} else {
			contextCount[1] = contextCount[0];
		}
	}

	public void parseUpdate(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() != 1) {
			throw new IllegalArgumentException("required single argument missing");
		}
		update = tokens.get(0);
	}

	public void parseParam(ArgumentOption option, ArgIterator argIterator) {
		setParams(argIterator.createTokenListUpToNextNonDigitMinus(option));
		for (String param : getParams()) {
//			if (!HARDCODED_PARAMS.contains(param)) {
//				LOG.debug("The parameters can be "+HARDCODED_PARAMS +"found..."+getParams()+";");
//				throw new RuntimeException("Bad param: "+param);
//			}
		}
	}

	public void parseTest(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		LOG.debug("The test strings are..."+tokens+"; override this if you want to use your own parseTest()");
	}

	public void parseLookup(ArgumentOption option, ArgIterator argIterator) {
		lookupNames = argIterator.getStrings(option);
		loadLookupClassesFromArgValues(option);
	}

	public void finalLookup(ArgumentOption option) {
		LOG.debug("final lookup NYI; please add code or override: names are: "+lookupNames+"; override");
	}


	// =============run=============
	@Override
	/** In AMI we can split the document by Xpath string which allows per-section searching.
	 * 
	 */
	public void runRunMethodsOnChosenArgOptions() {
		ensureSectionElements();
		super.runRunMethodsOnChosenArgOptions();
	}

	// ============output options==============

	// ==========================

	protected  void printVersion() {
		getVersionManager().printVersion();
		NormaArgProcessor.getVersionManager().printVersion();
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	public Integer[] getContextCount() {
		return contextCount;
	}

	public List<? extends Element> ensureSectionElements() {
		if (xPathProcessor != null) {
			sectionElements = currentCTree.extractSectionsFromScholarlyHtml(xPathProcessor.getXPath());
		} else {
			sectionElements = extractPSectionElements(currentCTree);
		}
		truncateLargeLists(getMaxSectionElements());
		return sectionElements;
	}

	/** maximum number of sections. Currently arbitrary at 1000.
	 * this should be settable, but it's quite a distamce between new and old tools
	 * @return
	 */
	private int getMaxSectionElements() {
		return 500;
	}

	private void truncateLargeLists(int maxsize) {
		if (sectionElements.size() > maxsize) {
			System.out.println("\nlarge document (" + sectionElements.size() + ") for "
		        + currentCTree.getName() + " truncated to " + maxsize + " sections");
			List<Element> newList = new ArrayList<Element>();
			for (int i = 0; i < maxsize; i++) {
				newList.add(sectionElements.get(i));
			}
			sectionElements = newList;
		}
	}

	public String getPlugin() {
		return plugin;
	}

	private void loadLookupClassesFromArgValues(ArgumentOption option) {
		getOrCreateLookupInstanceByName();
		List<ValueElement> valueElements = option.getOrCreateValueElements();
		for (ValueElement valueElement : valueElements) {
			String name = valueElement.getName();
			String className = valueElement.getClassName();
			if (name == null || className == null) {
				LOG.error("Missing name or class: "+valueElement.toXML());
				continue;
			}
			Class<? extends AbstractLookup> lookupClass;
			try {
				lookupClass = (Class<? extends AbstractLookup>)Class.forName(className);
			} catch (ClassNotFoundException e) {
				LOG.error("Cannot find class, skipping: "+className);
				continue;
			}
			AbstractLookup lookupInstance;
			try {
				lookupInstance = (AbstractLookup) lookupClass.newInstance();
				LOG.debug("lookup "+lookupInstance);
			} catch (Exception e) {
				LOG.error("Cannot instantiate, skipping: "+lookupClass+"; "+e.getMessage());
				continue;
			}
			lookupInstanceByName.put(name, lookupInstance);
		}
	}

	Map<String, AbstractLookup> getOrCreateLookupInstanceByName() {
		if (lookupInstanceByName == null) {
			lookupInstanceByName = new HashMap<String, AbstractLookup>(); 
		}
		return lookupInstanceByName;
	}

	/** create Subclassed Searcher.
	 * 
	 * //PLUGIN
	 * 
	 * Most plugins should Override this and create a FooSearcher.
	 * 
	 * @param namedPattern may be null for non-regex-based searchers
	 * @return subclassed Plugin
	 */
	protected AMISearcher createSearcher(NamedPattern namedPattern) {
		AMISearcher amiSearcher = new AMISearcher(this);
		amiSearcher.setNamedPattern(namedPattern);
		return amiSearcher;
	}

	protected AMISearcher createSearcher(DefaultStringDictionary dictionary) {
		AMISearcher defaultSearcher = new AMISearcher(this);
		defaultSearcher.setDictionary(dictionary);
		return defaultSearcher;
	}

	public List<AMISearcher> createSearcherList(List<String> names) {
		ensureSearcherBySearcherNameMap();
		ensureSearcherList();
		for (String name : names) {
			AMISearcher optionSearcher = (AMISearcher) searcherByNameMap.get(name);
			if (optionSearcher == null) {
				LOG.error("unknown optionType: "+name+"; allowed: "+searcherByNameMap);
			} else {
				searcherList.add(optionSearcher);
			}
		}
		return searcherList;
	}

	private void ensureSearcherBySearcherNameMap() {
		if (searcherByNameMap == null) {
			searcherByNameMap = new HashMap<String, AMISearcher>();
		}
	}

	protected void searchSectionElements() {
		if (currentCTree != null) {
			ensureSectionElements();
			if (searcherList == null) {
				throw new RuntimeException("No searchers created");
			}
			for (AMISearcher searcher : searcherList) {
				String name = searcher.getName();
				this.TREE_LOG().info("search "+name);
				LOG.trace("search "+name);
				ResultsElement resultsElement = searcher.search(sectionElements, createResultsElement());
				resultsElement.lookup(lookupInstanceByName, lookupNames);
				LOG.trace("exactList "+resultsElement.getExactList());
				resultsElement.setAllResultElementNames(name);
				currentCTree.putInContentProcessor(name, resultsElement);
			}
		}
	}

	/** normally overridden
	 * 
	 * @return
	 */
	protected ResultsElement createResultsElement() {
		return new ResultsElement();
	}
	

	protected void createAndStoreNamedSearchers(ArgumentOption option) {
		List<ValueElement> values = option.getOrCreateValueElements();
		createNamedSearchers(values);
	}

	protected void createNamedSearchers(List<ValueElement> values) {
		for (ValueElement valueElement : values) {
			try {
				if (RegexComponent.REGEX.equals(valueElement.getLocalName())) {
					NamedPattern namedPattern = NamedPattern.createFromRegexElement(valueElement);
					createSearcherAndAddToMap(namedPattern);
				} else {
					createSearcherAndAddToMap(valueElement);
				}
			} catch (Exception e) {
				LOG.error("Could not create NamedSearcher "+valueElement.getAttributeValue(NAME)+"; "+e.getCause());
				continue;
			}
		}
		LOG.trace("searchers "+searcherByNameMap);
	}

	public void createSearcherAndAddToMap(Element valueElement) {
		NamedPattern namedPattern = NamedPattern.createFromValueElement(valueElement);
		createSearcherAndAddToMap(namedPattern);
	}

	public void createSearcherAndAddToMap(NamedPattern namedPattern) {
		if (namedPattern != null) {
			ensureSearcherByNameMap();
			LOG.trace("added named pattern "+namedPattern);
			AMISearcher searcher = createSearcher(namedPattern);
			searcherByNameMap.put(namedPattern.getName(), searcher);
		}
	}

	private void ensureSearcherByNameMap() {
		if (searcherByNameMap == null) {
			searcherByNameMap = new HashMap<String, AMISearcher>();
		}
	}

	protected void createSearcherList(ArgumentOption option, ArgIterator argIterator) {
		List<String> types = argIterator.getStrings(option);
		createSearcherList(types);
	}

	public CTree getCurrentCTree() {
		return currentCTree;
	}

	public ContentProcessor getOrCreateContentProcessor() {
		return (currentCTree == null) ? null : currentCTree.getOrCreateContentProcessor();
	}

	public void addResultsElement(ResultsElement resultsElement) {
		if (resultsElement != null) {
			getOrCreateContentProcessor().addResultsElement(resultsElement);
		}
	}

	public WordCollectionFactory getOrCreateWordCollectionFactory() {
		if (wordCollectionFactory == null) {
			this.wordCollectionFactory = new WordCollectionFactory(this);
		}
		return wordCollectionFactory;
	}

	protected void ensureSearcherList() {
		if (searcherList == null) {
			searcherList = new ArrayList<AMISearcher>();
		}
	}

	public List<AMISearcher> getSearcherList() {
		return searcherList;
	}

	public DefaultAMIDictionary getOrCreateCurrentDictionary() {
		return currentDictionary;
	}

	private void checkWordCaseList() {
		if (wordCaseList.size() == 1 && PRESERVE.equals(wordCaseList.get(0))) {
			// OK
		} else {
			for (int i = wordCaseList.size() - 1; i >= 0; i--) {
				String word = wordCaseList.get(i);
				if (wordCaseList.contains(PRESERVE) || !CASE_TYPES.contains(word)) {
					LOG.error("Removed forbidden/unknown word: "+word);
					wordCaseList.remove(i);
				}
			}
		}
	}

	private void addStopwords(String stopwordLocation) {
		ensureStopwordSetList();
		WordSetWrapper stopwordSet = WordSetWrapper.createStopwordSet(stopwordLocation);
		if (stopwordSet != null) {
			stopwordSetList.add(stopwordSet);
		} else {
			LOG.warn("Stopword set should not be null");
		}
	}

	private void ensureStopwordSetList() {
		if (stopwordSetList == null) {
			stopwordSetList = new ArrayList<WordSetWrapper>();
		}
	}

	private List<WordSetWrapper> populateStopwordSetList(List<String> stopwordLocations) {
		ensureStopwordSetList();
		for (String stopwordLocation : stopwordLocations) {
			addStopwords(stopwordLocation);
		}
		return stopwordSetList;
	}

	public boolean getStemming() {
		return stemming == null ? false : stemming;
	}

	public List<WordSetWrapper> getStopwordSetList() {
		ensureStopwordSetList();
		return stopwordSetList;
	}

	public List<String> getWordCaseList() {
		return wordCaseList;
	}

	public List<String> getChosenWordTypes() {
		ensureChosenWordTypes();
		return chosenWordTypes;
	}

	public void ensureChosenWordTypes() {
		if (chosenWordTypes == null) {
			chosenWordTypes = new ArrayList<String>();
		}
	}

	public void setWordCollectionFactory(WordCollectionFactory wordCollectionFactory) {
		this.wordCollectionFactory = wordCollectionFactory;
	}



}

package org.contentmine.ami.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.plugins.word.WordCollectionFactory;
import org.contentmine.cproject.files.AbstractSearcher;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.cproject.lookup.AbstractLookup;
import org.contentmine.cproject.lookup.DefaultStringDictionary;
import org.contentmine.eucl.xml.XPathGenerator;

import nu.xom.Attribute;
import nu.xom.Element;

public class AMISearcher extends AbstractSearcher {

	private static final String NOT_FOUND = "NOT_FOUND";
	public static final Logger LOG = LogManager.getLogger(AMISearcher.class);
	private String exactMatch;
	private AbstractLookup lookup;
	private NamedPattern namedPattern;
	private AMIArgProcessor amiArgProcessor;
	public static int DEFAULT_POST_WORD_COUNT = 10;
	public static int DEFAULT_PRE_WORD_COUNT = 10;
	public static final String EXACT = "exact";
	public static final String POST = "post";
	public static final String PRE = "pre";
	protected Integer[] contextCounts;
	protected DefaultStringDictionary dictionary;
	public int maxPostWordCount = DEFAULT_POST_WORD_COUNT;
	public int maxPreWordCount = DEFAULT_PRE_WORD_COUNT;
	protected String name;
	public Pattern pattern;
	public List<String> stringList;
	
	public AMISearcher(AMIArgProcessor argProcessor) {
		this.amiArgProcessor = argProcessor;
		contextCounts = argProcessor.getContextCount();
		if (this.amiArgProcessor == null) {
			throw new RuntimeException("null argProcessor");
		}
	}

	public AMISearcher(AMIArgProcessor argProcessor, NamedPattern namedPattern) {
		this(argProcessor);
		this.setNamedPattern(namedPattern);
	}

	public AMISearcher(AMIArgProcessor argProcessor, DefaultStringDictionary dictionary) {
		this(argProcessor);
		this.setDictionary(dictionary);
		this.name = dictionary.getTitle();
	}

	protected void matchAndAddPrePost(String value, Matcher matcher, ResultElement resultElement) {
		String exactMatch = matcher.group(0);
		int preEnd = matcher.start();
		int preStart = Math.max(0, preEnd - contextCounts[0]);
		int postStart = matcher.end();
		int postEnd = Math.min(value.length(), postStart + contextCounts[1]);
		resultElement.setPre(flattenHtmlInlineTags(value.substring(preStart, preEnd)));
		exactMatch = flattenHtmlInlineTags(exactMatch);
		resultElement.setExact(exactMatch);
		resultElement.setPost(flattenHtmlInlineTags(value.substring(postStart, postEnd)));
		lookupMatchAndAddLookupRefs(resultElement);
	}

	private void lookupMatchAndAddLookupRefs(ResultElement resultElement) {
		Map<String, AbstractLookup> lookupInstanceByName = amiArgProcessor.getOrCreateLookupInstanceByName();
		for (String lookupName : lookupInstanceByName.keySet()) {
			AbstractLookup lookup = lookupInstanceByName.get(lookupName);
			Map<String, String> lookupRefByMatch = lookup.getOrCreateLookupRefByMatch();
			String lookupRef = lookupRefByMatch.get(exactMatch);
			if (lookupRef == null) {
				try {
					lookupRef = lookup.lookup(exactMatch);
				} catch (IOException e) {
					LOG.debug("Cannot find match: "+exactMatch+" in "+lookupName);
				}
				lookupRef = lookupRef == null ? NOT_FOUND : lookupRef;
				lookupRefByMatch.put(exactMatch,  lookupRef);
			}
			if (!(NOT_FOUND.equals(lookupRef))) {
				resultElement.addAttribute(new Attribute(lookupName, lookupRef));
			}
		}
	}
	
	protected String flattenHtmlInlineTags(String s) {
		s = s.replaceAll("<[^>]*>", "");
		return s;
	}

	public ResultElement createResultElement(String value, Matcher matcher) {
		ResultElement resultElement = createResultElement();
		matchAndAddPrePost(value, matcher, resultElement);
		return resultElement;
	}
	
	/**
	 *  //PLUGIN
	 */
	public ResultElement createResultElement() {
		return new AMIResultElement();
	}


	protected void addXpathAndAddtoResultsElement(Element elementToSearch, ResultsElement resultsElement,
			ResultsElement resultsElementToAdd) {
		if (resultsElementToAdd == null) {
			LOG.warn("null resultsElement");
		} else {
			for (ResultElement resultElement : resultsElementToAdd) {
				resultElement.detach();
				LOG.trace(">>> "+resultElement.toXML());
				XPathGenerator xPathGenerator = new XPathGenerator(elementToSearch);
				xPathGenerator.setShort(true);
				String xpath = xPathGenerator.getXPath();
				LOG.info("xpath "+xpath);
				resultsElement.setXPath(xpath);
				resultsElement.appendChild(resultElement);
				LOG.trace("XPATH added "+resultsElement.toXML());
			}
		}
	}
	public AMIArgProcessor getArgProcessor() {
		return amiArgProcessor;
	}

	public String getTitle() {
		return dictionary == null ? null : dictionary.getTitle();
	}

	public void setNamedPattern(NamedPattern namedPattern) {
		this.namedPattern = namedPattern; // could be null
		this.pattern = namedPattern == null ? null : namedPattern.getPattern();
		this.name = namedPattern == null ? null : namedPattern.getName();
	}

	/**
	 * 
	 * iterates over lists of lists of possible trailing words looking for first possible match of all words in order
	 * currently no proximity matching, 
	 * 
	 * [[mental,health,study],[mental,health]] matches "mental health study" but 
	 * [[mental,health],[mental,health,study]] matches "mental health" but 
	 * 
	 * [[health,study]] matches "health study" but not "health and safety study"
	 * 
	 *  This should ultimately work with stemming and lowercasing
	 * 
	 * @param trailingListList List of Lists of trailing strings
	 * @param strings tokens to match
	 * @param pos index of firstword
	 * @return
	 */
	public int canFitTrailing(List<List<String>> trailingListList, List<String> strings, int pos) {
		for (List<String> trailingList : trailingListList) {
			LOG.trace("match: "+strings.get(pos));
			boolean matched = true;
			int offset;
			for (offset = 0; offset < trailingList.size(); offset++) {
				int stringPos = pos + 1 + offset;
				if (stringPos >= strings.size()) {
					matched = false;
				} else if (!matchIncludingTrailingPunctuation(strings.get(stringPos), trailingList.get(offset))) {
					matched = false;
				}
				if (!matched) {
					LOG.trace(">> "+strings.get(stringPos - 1));
					break;
				}
			}
			if (matched) {
				return offset;
			}
		}
		return -1;
	}

	public List<String> createExactStringList(int pos, int offset) {
		List<String> exactStringList = new ArrayList<String>();
		for (int i = pos; i <= pos + offset; i++) {
			exactStringList.add(stringList.get(i));
		}
		return exactStringList;
	}

	public List<String> createPostStringList(int pos) {
		List<String> postStringList = new ArrayList<String>();
		for (int i = pos + 1; i < Math.min(stringList.size(), pos + getMaxPostWordCount()); i++) {
			postStringList.add(stringList.get(i));
		}
		return postStringList;
	}

	public List<String> createPreStringList(int pos) {
		List<String> preStringList = new ArrayList<String>();
		for (int i = Math.max(0, pos - getMaxPreWordCount()); i < pos; i++) {
			preStringList.add(stringList.get(i));
		}
		return preStringList;
	}

	public ResultElement createResultElement(List<String> strings, int pos, int offset) {
		this.stringList = strings;
		ResultElement resultElement = new ResultElement();
		addAttribute(resultElement, PRE, StringUtils.join(createPreStringList(pos).iterator(), " "));
		addAttribute(resultElement, EXACT, StringUtils.join(createExactStringList(pos, offset).iterator(), " "));
		addAttribute(resultElement, POST, StringUtils.join(createPostStringList(pos + offset).iterator(), " "));
		return resultElement;
	}

	private void addAttribute(ResultElement resultElement, String name, String value) {
		try {
			resultElement.addAttribute(new Attribute(name, value));
		} catch (Exception e) {
			// get rid of this later
			value = value.replaceAll(String.valueOf((char)23), "");
			resultElement.addAttribute(new Attribute(name, value));
		}
	}

	protected ResultElement createResultElement(String value, DefaultStringDictionary dictionary) {
		throw new RuntimeException("createResultElement(dictionary) NYI");
	}

	public DefaultStringDictionary getDictionary() {
		return dictionary;
	}

	public int getMaxPostWordCount() {
		return maxPostWordCount;
	}

	public int getMaxPreWordCount() {
		return maxPreWordCount;
	}

	public String getName() {
		return name;
	}

	protected Pattern getPattern() {
		return pattern;
	}

	/** flatten all tags.
	 * 
	 * @param xomElement
	 * @return
	 */
	public String getValue(Element xomElement) {
		return xomElement.getValue();
	}

	public boolean matchIncludingTrailingPunctuation(String raw, String term) {
		int difflength = raw.length() - term.length();
		if (difflength < 0 || difflength > 1) {
			return false;
		} else if (raw.equals(term)) {
			return true;
		}
		// ignore trailing punctuation
		if (difflength == 1 && raw.startsWith(term)) {
			char c = raw.charAt(raw.length() - 1);
			return c == ';' || c == ',' || c == '.' || c == '!' || c== '?';
		}
		return false;
	}

	public ResultsElement search(List<? extends Element> elements, ResultsElement resultsElement) {
		for (Element element : elements) {
			ResultsElement resultsElementToAdd = this.searchXomElement(element);
			addXpathAndAddtoResultsElement(element, resultsElement, resultsElementToAdd);
		}
		postProcessResultsElement(resultsElement);
		markFalsePositives(resultsElement, this.getOrCreateCurrentDictionary());
		return resultsElement;
	}

	private DefaultAMIDictionary getOrCreateCurrentDictionary() {
		return this.getArgProcessor().getOrCreateCurrentDictionary();
	}

	/** create resultsElement.
	 * 
	 * May be empty if no hits
	 * 
	 * @param xomElement
	 * @return
	 */
	public ResultsElement searchXomElement(Element xomElement) {
		String value = getValue(xomElement);
		ResultsElement resultsElement = search(value); // crude to start with
		return resultsElement;
	}


	public ResultsElement search(String value) {
		ResultsElement resultsElement = null;
		if (getDictionary() != null) {
			resultsElement = searchWithDictionary(value);
		} else if (getPattern() != null) {
			resultsElement = searchWithPattern(value);
		}
		return resultsElement;
	}

	public ResultsElement searchWithDictionary(List<String> wordsToSearch) {
		LOG.debug("SEARCH with dictionary");
		ResultsElement resultsElement = new ResultsElement();
		if (wordsToSearch != null) {
			for (int pos = 0; pos < wordsToSearch.size(); pos++) {
				String firstword = wordsToSearch.get(pos);
				List<List<String>> trailingListList = dictionary.getTrailingWords(firstword);
				if (trailingListList != null) {
					int trailingOffset = canFitTrailing(trailingListList, wordsToSearch, pos);
					if (trailingOffset != -1) {
						ResultElement resultElement = createResultElement(wordsToSearch, pos, trailingOffset);
						resultsElement.appendChild(resultElement);
					}
				}
			}
		}
		return resultsElement;
	}

	private ResultsElement searchWithDictionary(String value) {
		ResultsElement resultsElement = new ResultsElement();
		WordCollectionFactory wordCollectionFactory = amiArgProcessor.getOrCreateWordCollectionFactory();
		List<String> stringList = wordCollectionFactory.createWordList();
		resultsElement = searchWithDictionary(stringList);
		return resultsElement;
	}

	private ResultsElement searchWithPattern(String value) {
		ResultsElement resultsElement = new ResultsElement();
		Matcher matcher = getPattern().matcher(value);
		int start = 0;
		while (matcher.find(start)) {
			ResultElement resultElement = createResultElement(value, matcher);
			resultsElement.appendChild(resultElement);
			start = matcher.end();
		}
		return resultsElement;
	}


	public void setDictionary(DefaultStringDictionary dictionary) {
		this.dictionary = dictionary;
	}

	/** sometimes overridden by subclasses with complex terms.
	 * 
	 * default is resultsElement.getMatch(), but subclasses may need more 
	 * processing
	 * 
	 * @param resultElement
	 * @return
	 */
	protected String getDictionaryTerm(ResultElement resultElement) {
		String term = resultElement.getMatch();
		if (term == null) {
			term = resultElement.getExact();
		}
//		LOG.debug("Term: "+term);
		return term;
	}

	/** checks whether term (in resultElement) is in dictionary.
	 * I am not sure why...
	 * if not , sets dictionary check false on resultElement.
	 * not sure whether this is simply trapping bugs.
 	 */

	protected void markFalsePositives(ResultsElement resultsElement, DefaultAMIDictionary dictionary) {
		if (dictionary != null && resultsElement != null) {
			for (int i = resultsElement.size() - 1; i >= 0; i--) {
				ResultElement resultElement = resultsElement.get(i);
				if (resultElement != null) {
					String term = getDictionaryTerm(resultElement);
					if (!dictionary.contains(term)) {
						LOG.trace("marking potential false positive: "+resultElement.toXML());
						resultsElement.get(i).setDictionaryCheck(dictionary, false);
					}
				}
			}
		}
	}

	/** maybe overridden by specialist subclasses
	 * 
	 * this defaults to no-op
	 * 
	 * @param resultsElement
	 */
	protected void postProcessResultsElement(ResultsElement resultsElement) {
		// no-op
	}

	public void setName(String name) {
		this.name = name;
	}

}

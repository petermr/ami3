package org.contentmine.ami.tools.table;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/**
 * parses and runs table template queries
 * @author pm286
 *
 */
public class TQueryTool {
	
	private static final Logger LOG = Logger.getLogger(TQueryTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String QUERY = "query";
	private static final String FIND = "find";
	private static final String MATCH = "match";
	private static final String AND = "AND";
	private static final String NOT = "NOT";
	private static final String OR = "OR";
//	public static int DEFAULT_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.COMMENTS;
	public static int DEFAULT_FLAGS = Pattern.CASE_INSENSITIVE ;
	private static final String MODE = "mode";
	public static final String LOOKUP = "lookup";

	private static final int TRUE = 1;
	private static final int FALSE = 0;
	private static int flags = DEFAULT_FLAGS;
	
	
	private List<Pattern> andPatternList;
	private List<Pattern> notPatternList;
	private List<Pattern> orPatternList;
	private AbstractTTElement containingQueryElement;
	protected String lookupTarget;
	// row-wise results as integers
	private IntArray matchIntArray;
	private TTemplateList templateList;
	private File lookupFile;
	private Element lookupElement;
	private DefaultAMIDictionary dictionary;
	private List<String> unmatchedValues;
	private List<String> matchedValues;
	
	private TQueryTool() {
		init();
	}
	
	private void init() {
		andPatternList = new ArrayList<>();
		notPatternList = new ArrayList<>();
		orPatternList = new ArrayList<>();
	}

	public List<Pattern> getAndPatternList() {
		return andPatternList;
	}

	public List<Pattern> getNotPatternList() {
		return notPatternList;
	}

	public List<Pattern> getOrPatternList() {
		return orPatternList;
	}


	public TQueryTool(HasQuery containingQuery) {
		this();
		this.containingQueryElement = (AbstractTTElement) containingQuery;
		templateList = containingQueryElement.templateList;

		Element queryChild = XMLUtil.getSingleChild(containingQueryElement, QueryMatcher.TAG);
		String modeValue = queryChild == null ? null : queryChild.getAttributeValue(MODE);
		if (LOOKUP.equals(modeValue)) {
			lookupTarget = templateList.substituteVariables(containingQueryElement.getValue().trim());
		}
	}
	
	
	public void parseQueries() {
		String queryContent0 = Util.normalizeWhitespace(containingQueryElement.getValue());
		String queryContent = ((AbstractTTElement) containingQueryElement).templateList.substituteVariables(queryContent0);
		notPatternList = new ArrayList<>();
		String pre= addChunksToPatternListAndGetUnparsed(queryContent, NOT, notPatternList);
//		if (notPatternList.size() > 0) LOG.debug("NOT "+notPatternList);
		orPatternList = new ArrayList<>();
		pre = addChunksToPatternListAndGetUnparsed(pre, OR, orPatternList);
		if (orPatternList.size() > 0) {
			LOG.debug("OR "+orPatternList+"; "/*+queryElement.toXML()*/);
		} else {
			LOG.debug("NO OR");
		}
		andPatternList = new ArrayList<>();
		// omit AND at present
//		pre = addChunksToPatternListAndGetUnparsed(pre, AND, andPatternList);
//		if (andPatternList.size() > 0) LOG.debug("AND "+andPatternList);
		
	}

	private String addChunksToPatternListAndGetUnparsed(String queryContent, String oper, List<Pattern> patternList) {
		int start = 0;
		if (NOT.equals(oper)) {
			start = 1; 
		}
		List<String> chunks = new ArrayList<String>(Arrays.asList(queryContent.split(oper))); 
		// omit leading chunk if any as not governed by operator
		for (int i = chunks.size() - 1; i >= start ; i--) {
			String regex = chunks.get(i).trim();
			if (regex.length() > 0) {
				Pattern pattern = Pattern.compile(regex, flags);
				patternList.add(pattern);
			}
		}
		return chunks.get(0);
	}
	
	private static boolean isMatchFormat(String chunk) {
//		flags = Pattern.CASE_INSENSITIVE + Pattern.COMMENTS;
		Pattern MATCH_FORMAT = Pattern.compile("\\^.*\\$", flags);
		return MATCH_FORMAT.matcher(chunk).matches();
		
	}

	/** temporary until we can get a proper parser
	 * 
	 * @param target
	 * @return
	 */
	public boolean matches(String target) {
		target = target.trim();

		if (lookupElement != null) {
			getOrCreateDictionary();
			if (dictionary.contains(target.toLowerCase())) return true;
		}
		if (andPatternList.size() > 0 ) {
			if (!andMatches(target)) return false;
		}
		if (orPatternList.size() > 0 ) {
			if (!orMatches(target)) return false;
		}
		if (andPatternList.size() + orPatternList.size() == 0) {
			return false;
		}
		if (notPatternList.size() == 0) {
			return true;
		}
		return (notMatches(target));
	}

	private DefaultAMIDictionary getOrCreateDictionary() {
		if (dictionary == null) {
			dictionary = new DefaultAMIDictionary().readDictionaryElement(lookupElement);
		}
		return dictionary;
	}

	private boolean andMatches(String target) {
		for (Pattern pattern : andPatternList) {
			boolean matches = pattern.matcher(target).find();
			if (!matches) return false;
		}
		return true;
	}

	private boolean orMatches(String target) {
		for (Pattern pattern : orPatternList) {
//			System.err.println("flags "+pattern.flags());
//			System.err.println("@"+pattern+"@"+target+"@"+target.indexOf(pattern.pattern()));
			Matcher matcher = pattern.matcher(target);
			boolean matches = matcher.find();
			if (matches) {
//				System.err.println("TRUE: ");
				return true;
			}
		}
		return false;
	}

	private boolean notMatches(String colHeader) {
		for (Pattern pattern : notPatternList) {
			boolean matches = pattern.matcher(colHeader).find(0);
			if (matches) {
//				System.err.println("NOT: ");
				return false;
			}
		}
		return true;
	}

	/** convenience method when Matcher only has one regex.
	 * 
	 * @return
	 */
	public String getSingleRegex() {
		return ((Element) containingQueryElement).getValue().trim();
	}

	public IntArray match(List<String> values) {
		int size = values.size();
		matchIntArray = new IntArray(size);
		unmatchedValues = new ArrayList<>();	
		matchedValues = new ArrayList<>();	
		makeLookupElement();
		

		for (int i = 0; i < size; i++) {
			String value = values.get(i);
			if (value == null) value = "";
			if (this.matches(value)) {
				matchIntArray.setElementAt(i, TRUE);
				matchedValues.add(value);
				unmatchedValues.add(null);
			} else {
				matchIntArray.setElementAt(i, FALSE);
				unmatchedValues.add(value);
				matchedValues.add(null);
			}
		}
		return matchIntArray;
	}

	public List<String> getUnmatchedValues() {
		return unmatchedValues;
	}

	public List<String> getMatchedValues() {
		return matchedValues;
	}

	private void makeLookupElement() {
		if (lookupTarget != null) {
			lookupFile = new File(lookupTarget);
			lookupElement = XMLUtil.parseQuietlyToRootElement(lookupFile);
			if (lookupFile == null || lookupElement == null) {
				throw new RuntimeException("lookup file not found or can't be read: "+lookupFile);
			}
		}
	}

	public IntArray getMatchIntArray() {
		return matchIntArray;
	}

}

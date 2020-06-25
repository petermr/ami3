package org.contentmine.ami.plugins.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Text;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.MatcherResult;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.args.VariableProcessor;

/** a component of a regular expression
 * 
 * So that people can edit bits without destroying the lot.
 * 
 * @author pm286
 *
 */
public class RegexComponent {

	private static final Logger LOG = LogManager.getLogger(RegexComponent.class);
public static final String FIELDS = "fields";
	private static final String WEIGHT = "weight";
	private static final String PATTERN = "pattern";
	public static final String REGEX = "regex";
	private static final String CASE = "case";
	public static final String INSENSITIVE = "insensitive";
	public static final String REQUIRED = "required";
	private static final String TITLE = "title";
	private static final String URL = "url";
	private static final Double DEFAULT_WEIGHT = 0.5;

	private static final int QUERY_FIELD = 0;
	private static final int PRE_FIELD = 1;
	private static final int WORD_FIELD = 2;
	private static final int POST_FIELD = 3;

	private static final String START_CONTEXT = "(.{0,";
	private static final String END_CONTEXT = "})";

	// ((.{1,50})( ... )\p{Punct}?\s+(.{1,50}))
//	private static String PRE_POST = "\\(\\.\\{\\d+,\\d+\\}\\)(.*)\\p{Punct}?\\\\s\\+\\(\\.\\{\\d+,\\d+\\}\\)";
	// works most of the time but may eat spaces 
	private static String PRE_POST = "\\(\\.\\{\\d+,\\d+\\}\\)(.*)\\\\s\\+\\(\\.\\{\\d+,\\d+\\}\\)";
	private static final Pattern PRE_POST_PATTERN = Pattern.compile(PRE_POST);
	// (...)
	private static String SINGLE_BRACKET = "\\(.*\\)";
	private static final Pattern SINGLE_BRACKET_PATTERN = Pattern.compile(SINGLE_BRACKET);

	private static final String QUERY_FIELD_NAME = "query";
	private static final String PRE_FIELD_NAME = "pre";
	private static final String POST_FIELD_NAME = "post";
	private static final List<String> RESERVED_FIELD_NAMES = new ArrayList<String>(Arrays.asList(new String[]{QUERY_FIELD_NAME, PRE_FIELD_NAME, POST_FIELD_NAME}));
	private static final String[] FIELD_NAMES = {QUERY_FIELD_NAME, PRE_FIELD_NAME, "word", POST_FIELD_NAME};
	
	private Element regexElement;
	private Pattern pattern;
	private Double weight = null;
	private List<String> fieldList;
	private Integer count;
	private String value;
	private String casex;
	private AMIArgProcessor regexArgProcessor;
	private String title;
	private String fieldsString;
	private CompoundRegex compoundRegex;
	private String centralRegex;

	public RegexComponent(CompoundRegex compoundRegex, AMIArgProcessor regexArgProcessor) {
		this(compoundRegex);
		this.regexArgProcessor = regexArgProcessor;
		
	}

	public RegexComponent(CompoundRegex compoundRegex) {
		this.compoundRegex = compoundRegex;
	}

	void createPatternAndFields() {
		getOrCreatePattern();
		getOrCreateCase();
		getOrCreateValue();
		getURL();
	}
	
	private void getOrCreateTitleAndUpdateXML() {
		if (title == null) {
			title = regexElement.getAttributeValue(TITLE);
			if (title == null) {
				getOrCreateFieldList();
				List<String> novelFields = new ArrayList<String>();
				for (String field : fieldList) {
					if (!RESERVED_FIELD_NAMES.contains(field)) {
						novelFields.add(field);
					}
				}
				String novel = novelFields.toString();
				novel = novel.replaceAll("[\\[\\]\\s\\\\\\$\\%\\(\\)\\.]", "");
				title = novel.replaceAll(",", "_");
			}
			regexElement.addAttribute(new Attribute(TITLE, title));
		}
	}

	void expandAddDefaultsAndVerifyRegex() {
		getOrCreateValue();
		addBracketsAndContexts();
		getOrCreateFieldList();
		getOrCreateTitleAndUpdateXML();
		getOrCreateWeight();
	}


	private void addBracketsAndContexts() {
		if (PRE_POST_PATTERN.matcher(value).matches()) {
			return;
		} else if (SINGLE_BRACKET_PATTERN.matcher(value).matches()) {
			value = addPrePost(value);
		} else {
			value = addPrePost(addSingle(value));
		}
		Text childText = (Text) regexElement.getChild(0);
		childText.setValue(value);
		pattern = null; // reset 
	}

	private String addPrePost(String value) {
		Integer[] contextCounts = regexArgProcessor.getContextCount();
//		return "("+START_CONTEXT+contextCounts[0]+END_CONTEXT+value+"\\s+"+START_CONTEXT+contextCounts[1]+END_CONTEXT+")";
		return START_CONTEXT+contextCounts[0]+END_CONTEXT+value+DefaultArgProcessor.WHITESPACE+START_CONTEXT+contextCounts[1]+END_CONTEXT;
	}

	private String addSingle(String value) {
		return "("+value+")";
	}

	void setRegexElement(Element regexElement) {
		this.regexElement = regexElement;
	}
	
	public Element getRegexElement() {
		return regexElement;
	}

	public String getOrCreateValue() {
		if (value == null) {
			value = regexElement.getValue();
		}
		String newValue = substituteVariables(value);
		if (newValue == null) {
			LOG.error("Cannot expand variables in :"+value);
		} else {
			value = newValue;
		}
		return value;
	}

	private String substituteVariables(String value) {
		// crude
		value = value.replaceAll(RegexArgProcessor.TILDE, RegexArgProcessor.TILDE_SUFFIX);
//		if (value.startsWith(RegexArgProcessor.TILDE)) {
//			value = RegexArgProcessor.TILDE_PREFIX + value;
//		}
//		if (value.endsWith(RegexArgProcessor.TILDE)) {
//			value = value+RegexArgProcessor.TILDE_SUFFIX;
//		}
		VariableProcessor variableProcessor = regexArgProcessor.ensureVariableProcessor();
		value = variableProcessor.substituteVariables(value);
		return value;
	}

	public String getOrCreateCase() {
		return regexElement.getAttributeValue(CASE);
	}

	public String getCase() {
		if (casex == null) {
			casex = regexElement.getAttributeValue(CASE);
			if (casex == null) {
				casex = INSENSITIVE;
				regexElement.addAttribute(new Attribute(CASE, casex));
			}
		}
		return casex;
	}

	public String getURL() {
		return regexElement.getAttributeValue(URL);
	}

	List<String> getOrCreateFieldList() {
		if (fieldList == null) {
			fieldList = new ArrayList<String>();
			String fields = regexElement.getAttributeValue(FIELDS);
			boolean hasWord = true;
			if (fields != null) {
				fieldList = new ArrayList<String>(Arrays.asList(fields.split(DefaultArgProcessor.WHITESPACE)));
				if (fieldList.size() == 4) {
					if (!FIELD_NAMES[1].equals(fieldList.get(1)) ||
						!FIELD_NAMES[3].equals(fieldList.get(3))) {
							throw new RuntimeException("Fields should be [<query_name>]["+FIELD_NAMES[1]+"][<word_name>]["+FIELD_NAMES[3]+"]");
					}
				} else if (fieldList.size() == 1) {
					fieldList.add(FIELD_NAMES[3]);
					fieldList.add(0, FIELD_NAMES[1]);
					fieldList.add(0, FIELD_NAMES[0]);
					LOG.trace(fieldList);
				} else {
//					LOG.debug(value);
					LOG.warn("Unusual fieldList: "+fieldList+" in "+(compoundRegex == null ? "unknown" : compoundRegex.getTitle())+"; found: "+regexElement.toXML());
					hasWord = false;
				}
			} else {
				createFieldList(createNameFromRegexString());
				LOG.trace(">>>"+fieldList);
			}
			createFieldsFromValueAndUpdateXML();
//			createFieldStringAttributeValue();
		}
		return fieldList;
	}

	private void createFieldStringAttributeValue() {
		fieldsString = fieldList.toString();
		fieldsString = fieldsString.replaceAll("[\\[\\]\\s]", "");
		fieldsString = fieldsString.replaceAll(",", " ");
		LOG.trace("fields=\""+fieldsString+"\"");
	}

	private void createFieldList(String fieldname) {
		fieldList = new ArrayList<String>();
		fieldList.add(FIELD_NAMES[0]);
		fieldList.add(FIELD_NAMES[1]);
		fieldList.add(fieldname);
		fieldList.add(FIELD_NAMES[3]);
	}

	private String createNameFromRegexString() {
		getOrCreateCentralRegex();
		String name = centralRegex.replaceAll("[\\(\\)]", "");
		name = name.replaceAll("(\\\\W|\\s(\\+)?|\\d(\\+)?)", "");
		name=name.toLowerCase();
		return name;
	}

	private List<String> createFieldsFromValueAndUpdateXML() {
		getOrCreateCentralRegex();
		createFieldStringAttributeValue();
		regexElement.addAttribute(new Attribute(FIELDS, fieldsString));
		LOG.trace("???"+regexElement.toXML());
		return fieldList;
	}

	private void getOrCreateCentralRegex() {
		getOrCreateValue();
		if (centralRegex == null) {
			Matcher matcher = PRE_POST_PATTERN.matcher(value);
			if (matcher.matches()) {
				LOG.trace(PRE_POST_PATTERN);
				LOG.trace(matcher.groupCount()+"; "+value);
				centralRegex = matcher.group(1);
				LOG.trace(centralRegex);
			} else {
				throw new RuntimeException("Cannot parse regex as : "+value+"; "+PRE_POST_PATTERN);
			}
		}
	}

	private double getOrCreateWeight() {
		if (weight == null) {
			String w = regexElement.getAttributeValue(WEIGHT);
			if (w != null) {
				try {
					weight = new Double(w);
				} catch (Exception e) {
					throw new RuntimeException("bad weight: "+w);
				}
			} else {
				weight = DEFAULT_WEIGHT;
				regexElement.addAttribute(new Attribute(WEIGHT, String.valueOf(weight)));
			}
		}
		return weight;
	}
	
	/** get or create Pattern.
	 * 
	 * if null, compiles Pattern, If case="insensitive", ignores case.
	 * 
	 * @return
	 */
	private Pattern getOrCreatePattern() {
		if (pattern == null) {
			if (RegexComponent.INSENSITIVE.equals(getOrCreateCase())) {
				pattern = Pattern.compile(getOrCreateValue(), Pattern.CASE_INSENSITIVE);
			} else {
				pattern = Pattern.compile(getOrCreateValue());
			}
		}
		return pattern;
	}

	MatcherResult searchWithPattern(String value) {
		Pattern pattern = getOrCreatePattern();
//		pattern = Pattern.compile("who");
//		pattern = Pattern.compile("(.{0,25})(\\w([Ww]hen|[Ww]hy|[Ww]hat|[Ww]ho|[Ww]here)\\w)\\s+(.{0,40})");
//		pattern = Pattern.compile("(\\w([Ww]hen|[Ww]hy|[Ww]hat|[Ww]ho|[Ww]here)\\w)");
//		pattern = Pattern.compile("(.{0,25})\\w([Ww]hen|[Ww]hy|[Ww]hat|[Ww]ho|[Ww]here)(.{0,40})");
		Matcher matcher = pattern.matcher(value);
//		LOG.debug(">>"+pattern);
		int start = 0;
		count = 0;
		MatcherResult matcherResult = new MatcherResult(fieldList);
		while (matcher.find(start)) {
			matcherResult.captureNextMatch(matcher);
			int end = matcher.end();
//			LOG.debug("matched: "+value+"("+start+"/"+end+")");
			start = end;
			count++;
		}
		if (count == 0) {
//			LOG.debug("couldn't match: "+value+"/"+matcher);
		}
		LOG.debug(">>"+matcherResult);
		return matcherResult;
	}
	
	/**
	private Element regexElement;
	private Pattern pattern;
	private Double weight = null;
	private List<String> fieldList;
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(regexElement.toXML()+"; ");
//		sb.append(((pattern == null) ? "NULL" : pattern.toString())+"; ");
//		sb.append(((fieldList == null) ? "NULL" : fieldList.toString())+"; ");
		return sb.toString();
	}

	public void setValue(String rawRegex) {
		getOrCreateRegexElement();
		regexElement.appendChild(rawRegex);
	}

	private void getOrCreateRegexElement() {
		if (regexElement == null) {
			regexElement= new Element(REGEX);
			compoundRegex.getOrCreateCompoundRegexElement().appendChild(regexElement);
		}
	}

	public void setField(String fieldname) {
		this.createFieldList(fieldname);
	}

//	private Element createElement() {
//		/**
//		private Element regexElement;
//		private Pattern pattern;
//		private Double weight = null;
//		private List<String> fieldList;
//		private List<NamedGroup> namedGroupList;
//		private Integer count;
//		*/
//		
//		Element regex = new Element(REGEX);
//		if (pattern != null) {
//			Element patternElement = new Element(PATTERN);
//			patternElement.appendChild(pattern.toString());
//			regex.appendChild(patternElement);
//		}
//		if (weight != null) {
//			regex.addAttribute(new Attribute(WEIGHT, String.valueOf(weight)));
//		}
//		if (fieldList != null) {
//			regex.addAttribute(new Attribute(FIELDS, String.valueOf(fieldList)));
//		}
//		return regex;
//
//	}
}

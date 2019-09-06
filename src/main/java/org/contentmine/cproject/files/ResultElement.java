package org.contentmine.cproject.files;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.lookup.DefaultStringDictionary;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;

/** a container for a "result" from an action on a CTree.
 * 
 * Normally output to the "results" directory
 * 
 * @author pm286
 *
 */
public class ResultElement extends Element {
	
	private static final Logger LOG = Logger.getLogger(ResultElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String COUNT      = "count";
	private static final String DICTIONARY = "dictionary";
	private static final String DICTIONARY_CHECK = "dictionaryCheck";
	private static final String EXACT     = "exact";
	private static final String ID        = "id";
	public  static final String MATCH     = "match";
	private static final String NAME      = "name";
	public  static final String POST      = "post";
	public  static final String PRE       = "pre";
	public  static final String FREQUENCY = "frequency";
	public  static final String TAG       = "result";
	public  static final String TITLE     = "title";
	private static final String VALUE0    = "value0";
	private static final String WORD      = "word";
	private static final String XPATH     = "xpath";

	public ResultElement() {
		super(TAG);
	}

	public ResultElement(String title) {
		this();
		this.setTitle(title);
	}

	private void setTitle(String title) {
		if (title == null) {
			throw new RuntimeException("title cannot be null");
		}
		this.addAttribute(new Attribute(TITLE, title));
	}

	public String getExact() {
		return this.getAttributeValue(EXACT);
	}
	
	public void setExact(String value) {
		setValue(EXACT, value);
	}
	
	public String getMatch() {
		return this.getAttributeValue(MATCH);
	}
	
	public void setMatch(String value) {
		setValue(MATCH, value);
	}
	
	public String getName() {
		return this.getAttributeValue(NAME);
	}
	
	public void setName(String value) {
		setValue(NAME, value);
	}
	
	public String getPre() {
		return this.getAttributeValue(PRE);
	}
	
	public void setPre(String value) {
		setValue(PRE, value);
	}
	
	public String getPost() {
		return this.getAttributeValue(POST);
	}
	
	public void setPost(String value) {
		setValue(POST, value);
	}
	
	public String getWord() {
		return this.getAttributeValue(WORD);
	}
	
	public String getCountedWord() {
		String word = getWord();
		if (word == null) {
			return null;
		}
		return word+" x "+getCount();
	}
	
	public void setWord(String value) {
		setValue(WORD, value);
	}
	
	public void setXPath(String xpath) {
		this.addAttribute(new Attribute(XPATH, xpath));
	}

	public String getXPath() {
		return this.getAttributeValue(XPATH);
	}

	public void setValue(String name, String value) {
		Attribute attribute = new Attribute(name, value);
		this.addAttribute(attribute);
	}

	public void setId(String lookupName, String lookupId) {
		if (lookupName != null && lookupId != null) {
			this.addAttribute(new Attribute("_LOOKUP_"+lookupName, lookupId));
		}
	}

	/** creates attribute of form:
	 * 
	 * dictionary="my/dictionary" dictionaryCheck="false" (or "true")
	 * 
	 * doesn't really work for multiple dictionaries
	 * 
	 * @param dictionary
	 * @param checked
	 */
	public void setDictionaryCheck(DefaultStringDictionary dictionary, boolean checked) {
		String title = dictionary.getTitle();
		if (title != null) {
			this.addAttribute(new Attribute(DICTIONARY, title));
			this.addAttribute(new Attribute(DICTIONARY_CHECK, String.valueOf(checked)));
		}
	}

	public void setCount(int count) {
		LOG.trace("set "+count);
		this.addAttribute(new Attribute(COUNT, String.valueOf(count)));
	}

	public Integer getCount() {
		String countString = this.getAttributeValue(COUNT);
		try {
			int count = Integer.parseInt(countString);
			return new Integer(count);
		} catch (Exception e) {
			LOG.debug("Bad count: "+countString);
		}
		return null;
	}

	public static ResultElement createResultElement(Element element) {
		ResultElement resultElement  = null;
		if (element != null && element.getLocalName().equals(ResultElement.TAG)) {
			resultElement = new ResultElement();
			XMLUtil.copyAttributes(element, resultElement);
		}
		return resultElement;
	}

	public String getTerm() {
		String term = getMatch();
		if (term == null) {
			term = getExact();
		}
		if (term == null) {
			term = getCountedWord();
		}
		if (term == null) {
			term = getValue0Attribute();
		}
		return term;
	}
	
	private String getValue0Attribute() {
		return this.getAttributeValue(VALUE0);
	}

	public String toString() {
		return this.toXML();
	}

}

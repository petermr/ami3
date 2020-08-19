package org.contentmine.cproject.lookup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;
import nu.xom.Elements;

public class DefaultStringDictionary {

	private static final Logger LOG = LogManager.getLogger(DefaultStringDictionary.class);
	
	public static final String ENTRY = "entry";
	public static final String TERM = "term";
	public static final String SYNONYM = "synonym";
	public static final String TITLE = "title";

	
	protected String title;
	protected boolean addSynonyms = 
			true
//			false
			;
	
	/** all words or phrases indexed by lead word.
	 * Thus (I think)
	 * University of Cambridge
	 * University of East Anglia
	 * 
	 * creates two phrases indexed by "University" 
	 * 
	 */
	protected Map<String, List<List<String>>> trailingWordsByLeadWord;
	private int minSynonymLength = 5;
	
	public static DefaultStringDictionary createDictionary(String dictionarySource, InputStream is) {
		DefaultStringDictionary dictionary = null;
		if (false) {
			// should test special cases here
		} else {
//			File file = new File(dictionaryName);
			dictionary = new DefaultStringDictionary();
			try {
				dictionary.createFromInputStream(dictionarySource, is);
			} catch (IOException e) {
				throw new RuntimeException("Cannot read dictionary File: "+dictionarySource, e);
			}
		}
		return dictionary;
	}

	public Map<String, List<List<String>>> getTrailingWordsByLeadWord() {
		return trailingWordsByLeadWord;
	}
	
	public String getTitle() {
		return title;
	}
	public void createFromInputStream(String name, InputStream is) throws IOException {
		Element dictionaryElement = XMLUtil.parseQuietlyToDocument(is).getRootElement();
		createDictionaryFromXML(dictionaryElement);
	}
	private void createDictionaryFromXML(Element dictionaryElement) {
		title = dictionaryElement.getAttributeValue(TITLE);
		if (title == null || title.trim().equals("")) {
			throw new RuntimeException("dictionary must have title");
		}
		List<Element> entryList = XMLUtil.getQueryElements(dictionaryElement, ENTRY);
		if (entryList.size() == 0) {
			throw new RuntimeException("dictionary must have entries");
		}
		LOG.trace("creating dictionary "+title+" / "+entryList.size());
		trailingWordsByLeadWord = new HashMap<String, List<List<String>>>();
		for (Element entry : entryList) {
			addEntryToDictionary(entry);
		}
	}

	private void addEntryToDictionary(Element entry) {
		String term = entry.getAttributeValue(TERM);
		if (term == null) {
			throw new RuntimeException("missing term attribute");
		}
		term = term.trim();
//		System.out.println("V "+term);
		if (term.length() > 0) {
			addStringToTrailingWordsMap(term);
		}
		if (addSynonyms) {
			addSynonyms(entry);
		}
	}

	private void addSynonyms(Element entry) {
		Elements synonymElements = entry.getChildElements(SYNONYM);
		for (Element synonym : synonymElements) {
			String value = synonym.getValue();
			if (value.length() >= minSynonymLength) {
				addStringToTrailingWordsMap(value);
			}
		}
	}

	private void addStringToTrailingWordsMap(String term) {
		if (term == null) return;
		String[] wordList = term.split("\\s+");
		String key = wordList[0];
		LOG.trace("Key "+key);
		List<List<String>> lists = trailingWordsByLeadWord.get(key);
		if (lists == null) {
			lists = new ArrayList<List<String>>();
			trailingWordsByLeadWord.put(key, lists);
			LOG.debug("added: "+key);
		}
		List<String> trailingList = new ArrayList<String>();
		lists.add(trailingList);
		for (int i = 1; i < wordList.length; i++) {
			trailingList.add(wordList[i]);
		}
	}
	
	public boolean contains(String s) {
		List<List<String>> words = getTrailingWords(s);
		return words == null;
	}
	
	public List<List<String>> getTrailingWords(String headWord) {
		return trailingWordsByLeadWord != null ? trailingWordsByLeadWord.get(headWord) : null;
	}
	
	public boolean isAddSynonyms() {
		return addSynonyms;
	}

	public void setAddSynonyms(boolean addSynonyms) {
		this.addSynonyms = addSynonyms;
	}
	
	public int getMinSynonymLength() {
		return minSynonymLength;
	}

	public void setMinSynonymLength(int minSynonymLength) {
		this.minSynonymLength = minSynonymLength;
	}


}

package org.contentmine.cproject.lookup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

public class DefaultStringDictionary {

	private static final Logger LOG = Logger.getLogger(DefaultStringDictionary.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected String title;
	protected Map<String, List<List<String>>> trailingWordsByLeadWord;
	
	
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
		title = dictionaryElement.getAttributeValue("title");
		if (title == null || title.trim().equals("")) {
			throw new RuntimeException("dictionary must have title");
		}
		List<Element> entryList = XMLUtil.getQueryElements(dictionaryElement, "entry");
		if (entryList.size() == 0) {
			throw new RuntimeException("dictionary must have entries");
		}
		LOG.trace("creating dictionary "+title+" / "+entryList.size());
		trailingWordsByLeadWord = new HashMap<String, List<List<String>>>();
		for (Element entry : entryList) {
			String term = entry.getAttributeValue("term");
			if (term == null) {
				throw new RuntimeException("missing term attribute");
			}
			term = term.trim();
			LOG.trace("V "+term);
			if (term.length() == 0) {
				continue;
			}
			String[] wordList = term.split("\\s+");
			String key = wordList[0];
			LOG.trace("K "+key);
			List<List<String>> lists = trailingWordsByLeadWord.get(key);
			if (lists == null) {
				lists = new ArrayList<List<String>>();
				trailingWordsByLeadWord.put(key, lists);
				LOG.trace("added: "+key);
			}
			List<String> trailingList = new ArrayList<String>();
			lists.add(trailingList);
			for (int i = 1; i < wordList.length; i++) {
				trailingList.add(wordList[i]);
			}
		}
	}
	
	public boolean contains(String s) {
		List<List<String>> words = getTrailingWords(s);
		return words == null;
	}
	
	public List<List<String>> getTrailingWords(String headWord) {
		return trailingWordsByLeadWord != null ? trailingWordsByLeadWord.get(headWord) : null;
	}
}

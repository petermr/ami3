package org.contentmine.ami.tools.dictionary;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;
import nu.xom.Node;


/** from taxdump
 * 
 * @author pm286
 *
 */
public class SimpleDictionary extends DefaultAMIDictionary {
	
	private static final Logger LOG = LogManager.getLogger(SimpleDictionary.class);
	
	
	public SimpleDictionary() {
		init();
	}
	
	public SimpleDictionary(String dictionaryName) {
		dictionaryElement = DefaultAMIDictionary.createDictionaryElementWithTitle(dictionaryName);
	}

	public SimpleDictionary(String dictionaryName, Element dictionaryElement) {
		this(dictionaryName);
		super.dictionaryElement = dictionaryElement;
		if (dictionaryName == null || !dictionaryName.equals(this.getTitle())) {
			throw new RuntimeException("null or non-matching title for dictionary: "+dictionaryName+"/"+this.getTitle());
		}
	}

	private void init() {
	}

	public static SimpleDictionary createDictionary(String dictionaryName, InputStream inputStream) {
		SimpleDictionary simpleDictionary = null;
		if (inputStream != null && dictionaryName != null) {
			Element element = XMLUtil.parseQuietlyToRootElement(inputStream);
			simpleDictionary = new SimpleDictionary(dictionaryName, element);
		}
		return simpleDictionary;
	}

	public List<Node> getNodes(String xpath) {
		return dictionaryElement == null || xpath == null ? new ArrayList<>() 
				: XMLUtil.getQueryNodes(this.dictionaryElement, xpath);
	}

	public Map<String, Element> getEntryByTerm() {
		String xpath = ".//entry";
		List<Element> entryList = XMLUtil.getQueryElements(dictionaryElement, xpath);
		Map<String, Element> entryByTermMap = new HashMap<>();
		for (Element entry : entryList) {
			String term = entry.getAttributeValue(TERM);
			if (entryByTermMap.containsKey(term)) {
				LOG.error("duplicate term: " + term);
			} else {
				entryByTermMap.put(term, entry);
			}
		}
		return entryByTermMap;
	}

	public void addEntry(Element element) {
		this.getDictionaryElement().appendChild(element);
	}

	public List<Element> getDescElements() {
		List<Element> descElements = XMLUtil.getQueryElements(getDictionaryElement(), "./desc");
		return descElements;
	}


}

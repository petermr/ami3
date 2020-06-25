package org.contentmine.ami.wordutil;

/** creates an XMLTree for pre- and post- words 
 * 
 */
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

public class WordTree extends Element {

	private static final String WORD = "word";
	private static final String WORD_TREE = "wordTree";
	private static final Logger LOG = LogManager.getLogger(WordTree.class);
public WordTree() {
		this("Unknown");
	}
	
	public WordTree(String word) {
		super(WORD_TREE);
		this.addAttribute(new Attribute(WORD, word));
	}
	
	public void addPostString(String string) {
		List<String> wordList = Arrays.asList(string.split("\\s+"));
		addListElement(this, wordList, 0);
	}

	private void addListElement(Element parent, List<String> wordList, int counter) {
		if (counter >= wordList.size()) {
			return;
		}
		String word = wordList.get(counter);
		Elements childElements = parent.getChildElements();
		WordElement current = null;
		int index = 0;
		for (; index < childElements.size(); index++) {
			WordElement childElement = (WordElement) childElements.get(index);
			String next = childElement.getWord();
			if (word.equals(next)) {
				current = childElement;
				current.increment();
				break;
			} else if (word.compareTo(next) > 0) {
				current = createElementAndAdd(parent, word, index);
				break;
			}
		}
		if (index == childElements.size()) {
			current = createElementAndAdd(parent, word, index);
		}
		addListElement(current, wordList, ++counter);
	}
	
	public void addPreString(String string) {
		List<String> wordList = Arrays.asList(string.split("\\s+"));
		Collections.reverse(wordList);
		addListElement(this, wordList, 0);
	}

	/** remove all words which only occur once, unless parent is non-singleton
	 * 
	 */
	public void trimInsignificantSingletons() {
		String xpath = "//word[@count=1]/word[@count=1]";
		List<Element> insignificants = XMLUtil.getQueryElements(this, xpath);
		LOG.trace("> "+insignificants.size());
		for (Element insignificant : insignificants) {
			insignificant.detach();
		}
	}

	public void addStrings(List<String> ss) {
		for (String s : ss) {
			addPostString(s);
		}
	}
	
	// =====================
	
	private WordElement createElementAndAdd(Element parent, String word, int index) {
		WordElement current;
		current = new WordElement();
		current.setWord(word);
		parent.insertChild(current, index);
		return current;
	}


	
	
}

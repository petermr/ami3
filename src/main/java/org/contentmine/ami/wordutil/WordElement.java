package org.contentmine.ami.wordutil;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Attribute;
import nu.xom.Element;

public class WordElement extends Element {

	private static final String WORD = "word";
	private static final String COUNT = "count";
	private static final Logger LOG = LogManager.getLogger(WordElement.class);
public WordElement() {
		super(WORD);
		this.setCount(1);
	}

	public void setWord(String word) {
		this.addAttribute(new Attribute(WORD, word));
	}
	
	public void increment() {
		this.setCount(this.getCount() + 1);
	}

	private void setCount(int i) {
		this.addAttribute(new Attribute(COUNT, String.valueOf(i)));
	}

	private int getCount() {
		return Integer.parseInt(this.getAttributeValue(COUNT));
	}

	public String getWord() {
		return this.getAttributeValue(WORD);
	}
	
}

package org.contentmine.ami.plugins.word;

import nu.xom.Attribute;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.eucl.xml.XMLUtil;

public class WordResultElement extends ResultElement {

	private static final Logger LOG = LogManager.getLogger(WordResultElement.class);
public static final String COUNT_ATT  = "count";
	public static final String LENGTH_ATT = "length";
	public static final String WORD_ATT   = "word";

	public WordResultElement(ResultElement resultElement) {
		XMLUtil.copyAttributes(resultElement, this);
	}
	
	public WordResultElement(String title) {
		super(title);
	}
	
	public Integer getCount() {
		return new Integer(this.getAttributeValue(COUNT_ATT));
	}

	public String getWord() {
		return getAttributeValue(WORD_ATT);
	}

	public Integer getLength() {
		String lengthValue = this.getAttributeValue(LENGTH_ATT);
		return lengthValue == null ? null : new Integer(lengthValue);
	}

	public void setWord(String word) {
		this.addAttribute(new Attribute(WORD_ATT, word));
	}

	public void setCount(Integer count) {
		this.addAttribute(new Attribute(COUNT_ATT, String.valueOf(count)));
	}
	
	public void setLength(Integer length) {
		this.addAttribute(new Attribute(LENGTH_ATT, String.valueOf(length)));
	}
}

package org.contentmine.ami.plugins.phylotree;

import nu.xom.Attribute;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNEXML;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.eucl.xml.XMLUtil;

public class PhyloResultElement extends ResultElement {

	private static final Logger LOG = LogManager.getLogger(PhyloResultElement.class);
//	public static final String COUNT_ATT  = "count";
//	public static final String LENGTH_ATT = "length";
//	public static final String WORD_ATT   = "word";

	public PhyloResultElement(ResultElement resultElement) {
		XMLUtil.copyAttributes(resultElement, this);
	}
	
	public PhyloResultElement(String title) {
		super(title);
	}
	
	public void addNexml(NexmlNEXML nexml) {
		this.appendChild(nexml.copy());
	}
	
//	public Integer getCount() {
//		return new Integer(this.getAttributeValue(COUNT_ATT));
//	}
//
//	public String getWord() {
//		return getAttributeValue(WORD_ATT);
//	}
//
//	public Integer getLength() {
//		return new Integer(this.getAttributeValue(LENGTH_ATT));
//	}
//
//	public void setWord(String word) {
//		this.addAttribute(new Attribute(WORD_ATT, word));
//	}
//
//	public void setCount(Integer count) {
//		this.addAttribute(new Attribute(COUNT_ATT, String.valueOf(count)));
//	}
//	
//	public void setLength(Integer length) {
//		this.addAttribute(new Attribute(LENGTH_ATT, String.valueOf(length)));
//	}
}

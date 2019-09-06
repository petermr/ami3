package org.contentmine.cproject.args;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

public class XPathProcessor {

	
	private static final Logger LOG = Logger.getLogger(XPathProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private String xpath;

	public XPathProcessor(String xpath) {
		this.xpath = xpath;
		testPath();
	}

	private void testPath() {
		Element element = new Element("xyz");
		XMLUtil.getQueryElements(element, xpath);
	}

	public String getXPath() {
		return xpath;
	}

}

package org.contentmine.cproject.args;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

public class XPathProcessor {

	
	private static final Logger LOG = LogManager.getLogger(XPathProcessor.class);
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

package org.contentmine.graphics.svg.pubstyle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

public class Publisher {
	private static final Logger LOG = Logger.getLogger(Publisher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String doi;
	private String pubstyle;
	private String name;

	public Publisher(Element publisherElement) {
		doi = publisherElement.getAttributeValue("doiPrefix");
		pubstyle = publisherElement.getAttributeValue("pubstyle");
		name = publisherElement.getAttributeValue("name");
	}

	public String getPubstyleString() {
		return pubstyle;
	}

	public String getDoi() {
		return doi;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Publisher [doi=" + doi + ", pubstyle=" + pubstyle + ", name=" + name + "]";
	}
	
	

}

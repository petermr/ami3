package org.contentmine.graphics.svg.pubstyle;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Element;

public class Publisher {
	private static final Logger LOG = LogManager.getLogger(Publisher.class);
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

package org.contentmine.ami.plugins.gene;

import org.apache.log4j.Level;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.NamedPattern;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.lookup.DefaultStringDictionary;

import nu.xom.Element;

public class GeneSearcher extends AMISearcher {

	
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public GeneSearcher(AMIArgProcessor argProcessor, NamedPattern namedPattern) {
		super(argProcessor, namedPattern);
	}

	public GeneSearcher(AMIArgProcessor argProcessor, DefaultStringDictionary dictionary) {
		super(argProcessor, dictionary);
	}

	@Override 
	// this will probably disappear
	public String getValue(Element xomElement) {
		String xmlString = xomElement.toXML();
		// this is ucky, but since we know the HTML is normalized it's probably OK
		xmlString = xomElement.toXML().replaceAll(DefaultArgProcessor.WHITESPACE, " ");
		// some markup is of form <i>Foo</i>. <i>bar</i>
		xmlString = xmlString.replaceAll("</i>\\.\\s+<i>", ". ");
		xmlString = xmlString.replaceAll("<span[^>]*>", "");
		xmlString = xmlString.replaceAll("</span[^>]*>", "");
		xmlString = xmlString.replaceAll("<b>", "");
		xmlString = xmlString.replaceAll("</b>", "");
		xmlString = xmlString.replaceAll("<a>", "");
		xmlString = xmlString.replaceAll("</a>", "");
		xmlString = xmlString.replaceAll("<p>", "");
		xmlString = xmlString.replaceAll("</p>", "");
		xmlString = xmlString.replaceAll("<div>", "");
		xmlString = xmlString.replaceAll("</div>", "");
		return xmlString;
	}

	/**
	 *  //PLUGIN
	 */
	public GeneResultElement createResultElement() {
		return new GeneResultElement();
	}

}

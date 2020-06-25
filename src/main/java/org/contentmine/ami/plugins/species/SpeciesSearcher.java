package org.contentmine.ami.plugins.species;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.NamedPattern;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.cproject.files.ResultsElement;

import nu.xom.Element;

public class SpeciesSearcher extends AMISearcher {

	private static final Logger LOG = LogManager.getLogger(SpeciesSearcher.class);
public SpeciesSearcher(AMIArgProcessor argProcessor, NamedPattern namedPattern) {
		super(argProcessor, namedPattern);
	}

	@Override 
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

	@Override
	protected void postProcessResultsElement(ResultsElement resultsElement) {
		List<String> exactList = resultsElement.getExactList();
		LinneanNamer linneanNamer = new LinneanNamer();
		List<String> matchList = linneanNamer.expandAbbreviations(exactList);
		resultsElement.addMatchAttributes(matchList);
	}
	

	@Override
	public String getDictionaryTerm(ResultElement resultElement) {
		String genus = LinneanNamer.createGenus(resultElement.getMatch());
		return genus;
	}

	/**
	 *  //PLUGIN
	 */
	public SpeciesResultElement createResultElement() {
		return new SpeciesResultElement();
	}

}

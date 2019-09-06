package org.contentmine.ami.plugins.dummy;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMISearcher;
import org.contentmine.ami.plugins.NamedPattern;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.cproject.files.ResultsElement;

public class DummySearcher extends AMISearcher {

	
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public DummySearcher(AMIArgProcessor argProcessor, NamedPattern namedPattern) {
		super(argProcessor, namedPattern);
	}

	@Override 
	public String getValue(Element xomElement) {
		String xmlString = xomElement.toXML();
		return xmlString;
	}

//	@Override
//	public ResultsElement search(List<? extends Element> elements) {
//		ResultsElement resultsElement = null;
////		for (Element element : elements) {
////			String xmlString = getValue(element);
////			LOG.trace(xmlString);
////			List<ResultElement> resultElementList = this.search(xmlString);
////			addXpathAndAddtoResultsElement(element, resultsElement, resultElementList);
////		}
//		return resultsElement;
//	}

	/**
	 *  //PLUGIN
	 */
	public DummyResultElement createResultElement() {
		return new DummyResultElement();
	}

}

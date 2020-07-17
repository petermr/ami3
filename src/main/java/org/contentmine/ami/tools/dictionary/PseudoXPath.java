package org.contentmine.ami.tools.dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;
import nu.xom.Node;


public class PseudoXPath {
	private static final Logger LOG = LogManager.getLogger(PseudoXPath.class);

	private List<String> mutables;
	private String value;
	private String elementName;
	private String attname;

	public PseudoXPath(String deleteString) {
//	@wikidata=Q123456781,@wikidata,@description,
		parse(deleteString);
	}

	private void parse(String deleteString) {
		String[] nodeBits = deleteString.split(",");
		String[] locator = nodeBits[0].split("=");
		mutables = new ArrayList<>(Arrays.asList(nodeBits));
		mutables.remove(0);
		String[] names = locator[0].split("@");
		elementName = names[0];
		attname = names[1];
		value = locator[1];
//		LOG.info(">"+elementName+"@"+attname+"="+value+"/"+mutables);
	}

	public List<Element> createElementList(Element element) {
		String xpath = "//" + elementName + "[" + "@" + attname + "=" + "'" + value + "'" + "]";
		List<Element> elementList = XMLUtil.getQueryElements(element, xpath);
//		LOG.info("n>"+xpath+":"+elementList);
		return elementList;
	}
	
	public List<Node> createNodeList(List<Element> elementList) {
		List<Node> nodeList = new ArrayList<>();
		for (Element element : elementList) {
			for (String mutable : mutables) {
				if (element.getLocalName().contentEquals(mutable)) {
					nodeList.add(element);
				} else {
					List<Node> nodes = XMLUtil.getQueryNodes(element, mutable);
					nodeList.addAll(nodes);
				}
			}
		}
//		LOG.info("l>"+nodeList);
		return nodeList;
	}

	public List<Node> createNodeList(Element dictionaryElement) {
		List<Element> elementList = createElementList(dictionaryElement);
		List<Node> nodeList = createNodeList(elementList);
		return nodeList;
	}
	
	

}

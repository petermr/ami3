package org.contentmine.graphics.html.util;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jsoup.Jsoup;

public class JsoupWrapper {
	private static final Logger LOG = LogManager.getLogger(JsoupWrapper.class);
public JsoupWrapper() {
		
	}
	
	/** parses with Jsoup and tries to correct any bad XML
	 * 
	 * see tests for examples
	 * 
	 * @param s
	 * @return well-formed XML, maybe with "JUNK" inserted locally in place of bad html
	 */
	public static String parseAndCorrect(String s) {
		org.jsoup.nodes.Document doc= Jsoup.parse(s);
		String ss = doc.toString().replaceAll("\\\"[^\\\"]*\\\"=\\\"", "JUNK");
		return ss;
	}
	
	public static nu.xom.Document jSoupToXOM(org.jsoup.nodes.Document jdoc) {
		nu.xom.Document xom = null;
		for (org.jsoup.nodes.Node jNode : jdoc.childNodes()) {
			if (jNode instanceof org.jsoup.nodes.Element) {
				nu.xom.Element root = jSoup2XOM((org.jsoup.nodes.Element) jNode);
				xom = new nu.xom.Document(root);
			}
		}
		return xom;
	}

	private static nu.xom.Element jSoup2XOM(org.jsoup.nodes.Element jElement) {
		nu.xom.Element xElement = new nu.xom.Element(jElement.nodeName());
		for (org.jsoup.nodes.Attribute attribute : jElement.attributes()) {
			nu.xom.Attribute xAttribute = jSoup2XOM(attribute);
			xElement.addAttribute(xAttribute);
		}
		for (org.jsoup.nodes.Node jNode : jElement.childNodes()) {
//			System.err.println("J"+jNode.getClass()+"/"+jNode);
			nu.xom.Node xNode = jSoup2XOM(jNode);
			xElement.appendChild(xNode);
		}
		return xElement;
	}
	
	private static nu.xom.Attribute jSoup2XOM(org.jsoup.nodes.Attribute jAttribute) {
		nu.xom.Attribute xAttribute = new nu.xom.Attribute(jAttribute.getKey(), jAttribute.getValue());
		return xAttribute;
	}
	
	private static nu.xom.Comment jSoup2XOM(org.jsoup.nodes.Comment jComment) {
		nu.xom.Comment xComment = new nu.xom.Comment(jComment.getData());
		return xComment;
	}

	/** not sure how to manage this.
	 * Have flattened CData to a Text
	 * 
	 * @param jData
	 * @return
	 */
	private static nu.xom.Text jSoup2XOM(org.jsoup.nodes.DataNode jData) {
		nu.xom.Text text = new nu.xom.Text(jData.toString());
		return text;
	}
	
	private static nu.xom.Text jSoup2XOM(org.jsoup.nodes.TextNode jText) {
		nu.xom.Text text = new nu.xom.Text(jText.text());
		return text;
	}
	
//	private static nu.xom.ProcessingInstruction jSoup2XOM(org.jsoup.nodes.PseudoTextElement  ProcessingInstruction jProcessingInstruction) {
//		nu.xom.ProcessingInstruction xProcessingInstruction = new nu.xom.ProcessingInstruction(jProcessingInstruction.getData());
//		return xProcessingInstruction;
//	}
//	
	private static nu.xom.Node jSoup2XOM(org.jsoup.nodes.Node jNode) {
		nu.xom.Node xNode = null;
		if (jNode instanceof org.jsoup.nodes.Element) {
			xNode = jSoup2XOM((org.jsoup.nodes.Element) jNode);
		} else if (jNode instanceof org.jsoup.nodes.Comment) {
			xNode = jSoup2XOM((org.jsoup.nodes.Comment) jNode);
		} else if (jNode instanceof org.jsoup.nodes.TextNode) {
			xNode = jSoup2XOM((org.jsoup.nodes.TextNode) jNode);
		} else if (jNode instanceof org.jsoup.nodes.DataNode) {
			xNode = jSoup2XOM((org.jsoup.nodes.DataNode) jNode);
		} else {
			LOG.error("Unknown node: "+jNode.getClass()+"/"+jNode);
		}
		return xNode;
	}
	

}

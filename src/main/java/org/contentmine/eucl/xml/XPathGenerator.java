package org.contentmine.eucl.xml;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

/** generates an xpath for an element in context.
 * 
 * Heuristic.
 * 
 * Because of the problems of namespaces, variable use of IDs etc., There are roughly three strategies:
 *  (1) ancestor local names e.g. *[local-name()='body']/*[local-name()='div'][1]/*[local-name='p'][2]
 *  (2) namespaced h:body/h:div[1]/h:p[2]
 *  (3) ids *[@id='id23']/*[@id='id45']/*[@id='id99']
 *  
 *  If the namespaces are not clear then (2) cannot be used. If there are no ids, then maybe we have to generate
 *  them. but this is fragile against others doing the same with different strategy. (1) is the safest - it fails
 *  ony when there are elements with different namespaces. so (4) we could probably extract the namespaces and use them explicitli
 *  (4) *[local-name()='body' and namespace-uri()='http://www.w3.org/1999/xhtml']/*[local-name()='div'][1]/*[local-name='p'][2]
 *  
 *  I think (4) is safe, but verbose!!

 * 
 * @author pm286
 *
 */
public class XPathGenerator {

	
	private static final Logger LOG = Logger.getLogger(XPathGenerator.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String TAGX = "tagx";
	private static final String[] TAGS = {ID, NAME, TAGX};
	
	private Element xmlElement;
	private boolean shortFlag = false;

	public XPathGenerator(Element xmlElement) {
		this.xmlElement = xmlElement;
	}
	
	public void setShort(boolean shortFlag) {
		this.shortFlag  = shortFlag;
	}
	
    public String getXPath() {
		StringBuilder sb = new StringBuilder();
		addAncestors(sb, xmlElement);
		return sb.toString();
	}

	private void addAncestors(StringBuilder sb, Element element) {
		if (element == null)
			return;
		String name = element.getLocalName();
		String el = element.toXML();
		StringBuilder sb1 = new StringBuilder();
		sb1.append(((shortFlag) ? "/" + name : "/*[local-name()='" + name + "']"));
		// FIXME this is NOT UNIQUE
		Attribute attribute = getFirstUsefulAttribute(element);
		attribute = null; // FIXME
		if (attribute != null) {
			sb1.append("[@" + attribute.getLocalName() + "='"
					+ attribute.getValue() + "']");
		} else {
			int ordinal = getOrdinalOfChildWithName(element, name);
			sb1.append("[" + ordinal + "]");
		}
		String ss = sb1.toString();
		sb.insert(0, ss);
		ParentNode parent = element.getParent();
		if (parent != null && parent instanceof Element) {
			addAncestors(sb, (Element) parent);
		}
	}

	private int getOrdinalOfChildWithName(Element element, String name) {
		int ordinal = 1; // we count from 1 in XPath
		ParentNode parent = element.getParent();
		if (parent != null) {
			int position = parent.indexOf(element);
			for (int i = 0; i < position; i++) {
				Node sibling = parent.getChild(i);
				if (sibling instanceof Element
						&& name.equals(((Element) sibling).getLocalName())) {
					ordinal++;
				}
			}
		}
		return ordinal;
	}

	private Attribute getFirstUsefulAttribute(Element element) {
		for (String tag : TAGS) {
			Attribute attribute = element.getAttribute(tag);
			if (attribute != null) {
				if (attribute.getValue().trim().length() > 0) {
					return attribute;
				}
			}
		}
		return null;
	}

}

package org.contentmine.graphics.svg.util;

import java.util.ArrayList;
import java.util.List;

import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGConstants;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGUtil;

import nu.xom.Attribute;
import nu.xom.Element;

public class Compacter {
	
	private List<String> attnames;
	private List<Attribute> lastAttributes;
	private List<SVGG> gList;
	private SVGSVG topG;

	public Compacter() {
	}
	
	public void setAttributeNames(List<String> attnames) {
		this.attnames = attnames;
	}

	public SVGSVG compactChildAttributesIntoGroups(List<? extends Element> elementList) {
		topG = new SVGSVG();
		topG.addNamespaceDeclaration(SVGConstants.SVGX_PREFIX, SVGConstants.SVGX_NS);
		lastAttributes = null;
		createGroupsAndMoveLocallyUbiquitousAttributesUp(elementList);
		moveUbiquitousGroupAttributesToTop();
		return topG;
	}

	private List<SVGG> createGroupsAndMoveLocallyUbiquitousAttributesUp(List<? extends Element> elementList) {
		SVGG currentG = null;
		Element lastElement = null;
		gList = new ArrayList<SVGG>();
		for (Element element : elementList) {
			if (attributesChanged(lastElement, element)) {
				currentG = new SVGG();
				gList.add(currentG);
				addAttributes(currentG, element);
				topG.appendChild(currentG);
				lastElement = element;
			}
			Element elementCopy = (Element) element.copy();
			removeAttributes(elementCopy);
			currentG.appendChild(elementCopy);
		}
		return gList;
	}

	private void moveUbiquitousGroupAttributesToTop() {
		for  (String attname : attnames) {
			Attribute ubiquitousAttribute = null;
			boolean ubiquitous = true;
			for (SVGG g : gList) {
				Attribute att = getDefaultOrSVGXAttribute(g, attname);
				if (att == null) {
					// no defaults
					ubiquitous = false;
					break;
				} else if (ubiquitousAttribute == null) {
					// first occurrence (on first element)
					ubiquitousAttribute = att;
				} else if (!Compacter.isEqual(ubiquitousAttribute, att)) {
					// att value changed
					ubiquitous = false;
					break;
				}
			}
			// if consistent, move attribute up
			if (ubiquitous && ubiquitousAttribute != null) {
				topG.addAttribute((Attribute)ubiquitousAttribute.copy());
				for (SVGG g : gList) {
					Attribute att = getDefaultOrSVGXAttribute(g, attname);
					att.detach();
				}
			}
		}
	}

	public void removeAttributes(Element element) {
		for (String attname : attnames) {
			Attribute att = getDefaultOrSVGXAttribute(element, attname);
			if (att != null) {
				att.detach();
			}
		}
	}

	private Attribute getDefaultOrSVGXAttribute(Element element, String attname) {
		Attribute att = element.getAttribute(attname);
		if (att == null) {
			att = element.getAttribute(attname, SVGConstants.SVGX_NS);
		}
		return att;
	}

	public void addAttributes(SVGG currentG, Element element) {
		for (String attname : attnames) {
			Attribute att = getDefaultOrSVGXAttribute(element, attname);
			if (att != null) {
				currentG.addAttribute((Attribute)att.copy());
			}
		}
	}

	public boolean attributesChanged(Element element1, Element element2) {
		boolean changed = false;
		if (element1 == null || element2 == null) return true;
		for (String attname : attnames) {
			Attribute a1 = getDefaultOrSVGXAttribute(element1, attname);
			Attribute a2 = getDefaultOrSVGXAttribute(element2, attname);
			if (!Compacter.isEqual(a1, a2)) {
				changed = true;
				break;
			}
		}
		return changed;
	}

	public static boolean existsIn(Element element, Attribute attribute) {
		boolean existsIn = false;
		if (element != null) {
			for (int i = 0; i < element.getAttributeCount(); i++) {
				Attribute attributeX = element.getAttribute(i);

				
				if (Compacter.isEqual(attributeX, attribute)) {
					existsIn = true;
					break;
				}
			}
		}
		return existsIn;
	}

	public static boolean isEqual(Attribute a1, Attribute a2) {
		if (a1 == null && a2 == null) return true;
		if (a1 == null || a2 == null) return false;
		boolean equals =  
				a1.getLocalName().equals(a2.getLocalName()) &&
				a1.getValue().equals(a2.getValue());
		return equals;
	}

	public List<SVGG> compactElements(List<SVGElement> elementList) {
		topG = new SVGSVG();
		SVGG currentG = null;
		List<SVGG> gList = new ArrayList<SVGG>();
		String lastElementName = null;
		for (Element element : elementList) {
			String localName = element.getLocalName();
			if (!localName.equals(lastElementName)) {
				currentG = new SVGG();
				topG.appendChild(currentG);
				lastElementName = localName;
				gList.add(currentG);
			}
			currentG.appendChild(element.copy());
		}
		return gList;
	}

	public Element getTopG() {
		return topG;
	}

	public void compactGroups(List<SVGG> gList) {
		for(AbstractCMElement g : gList) {
			List<SVGElement> elementList = SVGUtil.getQuerySVGElements(g,  "*");
			createGroupsAndMoveLocallyUbiquitousAttributesUp(elementList);
			for (Element element : elementList) {
				element.detach();
			}
		}
	}
}

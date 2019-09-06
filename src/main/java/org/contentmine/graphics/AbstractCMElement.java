package org.contentmine.graphics;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

/** intermediary helper class to provide generic subclasses 
 * for CM/XOM-based markup languages.
 * 
 * @author pm286
 *
 */
public abstract class AbstractCMElement extends Element implements XMLConstants {
	
	private static final Logger LOG = Logger.getLogger(AbstractCMElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String ANCESTOR = "ancestor";
	public static final String CLASS = "class";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String TITLE = "title";
	public static final String STYLE = "style";
	public static final String UTF_8 = "UTF-8";
	public static final String TYPE = "type";
	public static final String CHARSET = "charset";

	/** 
	 * Constructor no namespace
	 * 
	 * @param name
	 */
	public AbstractCMElement(String name) {
		super(name);
	}

	/** 
	 * Constructor.
	 * 
	 * @param name
	 * @param namespace
	 */
	public AbstractCMElement(String name, String namespace) {
		super(name, namespace);
	}

	public static AbstractCMElement getSingleChildElement(AbstractCMElement root, String tag) {
		List<HtmlElement> elements = getChildElements(root, tag);
		return (elements.size() != 1) ? null : elements.get(0);
	}

	public static List<HtmlElement> getChildElements(AbstractCMElement root, String tag) {
		tag = tag.toLowerCase();
		List<HtmlElement> elements = new ArrayList<HtmlElement>();
		if (root != null) {
			Nodes nodes = root.query("./*[local-name()='"+tag+"']");
			for (int i = 0; i < nodes.size(); i++) {
				elements.add((HtmlElement)nodes.get(i));
			}
		}
		return elements;
	}

	public static AbstractCMElement getSingleSelfOrDescendant(AbstractCMElement root, String tag) {
		List<? extends AbstractCMElement> elements = getSelfOrDescendants(root, tag);
		return (elements.size() != 1) ? null : elements.get(0);
	}

	public static List<HtmlElement> getSelfOrDescendants(AbstractCMElement root, String tag) {
		tag = tag.toLowerCase();
		String xpath = ".//*[local-name()='"+tag+"']";
		Nodes nodes = root.query(xpath);
		List<HtmlElement> elements = new ArrayList<HtmlElement>();
		for (int i = 0; i < nodes.size(); i++) {
			elements.add((HtmlElement)nodes.get(i));
		}
		return elements;
	}

	/** value of the "class" attribute.
	 * 
	 * @param element
	 * @return null if element is null.
	 */
	public static String getClassAttributeValue(Element element) {
		return element == null ? null : element.getAttributeValue(CLASS);
	}

	/** set "class" attribute.
	 * 
	 * @param element
	 * @param value
	 */
	public static void setClassAttributeValue(Element element, String value) {
		if (element != null && value != null) element.addAttribute(new Attribute(CLASS, value));
	}

	protected void copyAttributesChildrenElements(AbstractCMElement element) {
		copyAttributesFrom(element);
	    copyChildrenFrom(element);
	    copyNamespaces(element);
	}

	/**
	 * Copies attributes. Makes subclass if necessary.
	 * 
	 * @param element to copy from
	 */
	public void copyAttributesFrom(Element element) {
		if (element != null) {
	        for (int i = 0; i < element.getAttributeCount(); i++) {
	            Attribute att = element.getAttribute(i);
	            Attribute newAtt = (Attribute) att.copy();
	            this.addAttribute(newAtt);
	        }
		}
	}

	/** 
	 * Copies children of element make subclasses when required.
	 * 
	 * @param element to copy from
	 */
	public void copyChildrenFrom(Element element) {
	    for (int i = 0; i < element.getChildCount(); i++) {
	        Node childNode = element.getChild(i);
	        Node newNode = childNode.copy();
	        this.appendChild(newNode);
	    }
	}

	/**
	 * Copies namespaces.
	 * @param element to copy from
	 */
	public void copyNamespaces(AbstractCMElement element) {
	    int n = element.getNamespaceDeclarationCount();
	    for (int i = 0; i < n; i++) {
	        String namespacePrefix = element.getNamespacePrefix(i);
	        String namespaceURI = element.getNamespaceURIForPrefix(namespacePrefix);
	        this.addNamespaceDeclaration(namespacePrefix, namespaceURI);
	    }
	}

	/**
	 * Gets namespace.
	 * 
	 * @param prefix
	 * @return namespace
	 */
	public String getNamespaceURIForPrefix(String prefix) {
	    String namespace = null;
	    Element current = this;
	    while (true) {
	        namespace = current.getNamespaceURI(prefix);
	        if (namespace != null) {
	            break;
	        }
	        Node parent = current.getParent();
	        if (parent == null || parent instanceof Document) {
	            break;
	        }
	        current = (Element) parent;
	    }
	    return namespace;
	}

	protected void removeAttributeWithName(String name) {
		Attribute attribute = name == null ? null : this.getAttribute(name);
		if (attribute != null) {
			this.removeAttribute(attribute);
		}
	}

	protected void setAttributeOrRemoveIfNull(String attName, String attVal) {
		if (attName == null) {
			throw new RuntimeException("Null name for attribute");
		} if (attVal == null) {
			this.removeAttributeWithName(attName);
		} else {
			this.addAttribute(new Attribute(attName, attVal));
		}
	}

}

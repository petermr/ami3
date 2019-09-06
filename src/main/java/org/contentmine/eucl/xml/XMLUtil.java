/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.eucl.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ProcessingInstruction;
import nu.xom.Serializer;
import nu.xom.Text;
import nu.xom.XPathContext;
import nu.xom.XPathException;
import nu.xom.canonical.Canonicalizer;

/**
 * 
 * <p>
 * static utilities to help manage common constructs.
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public abstract class XMLUtil implements XMLConstants {

	private static Logger LOG = Logger.getLogger(XMLUtil.class);

	private static final  String DOCTYPE = "\\<\\!DOCTYPE[^\\>]*\\>";
	public  final  static String DTD     = ".dtd\">";
	private static final  String DUMMY   = "dummy";
	private static final  String TEXT    = "#text";


	// ========================== utilities ====================== //

	/**
	 * checks that name is QName.
	 * 
	 * @param name
	 *            of XMLName
	 *             not colonized
	 */
	public final static void checkPrefixedName(String name) {
		if (name == null || name.indexOf(S_COLON) < 1) {
			throw new RuntimeException("Unprefixed name (" + name + S_RBRAK);
		}
	}

	/**
	 * get prefix from qualified name.
	 * 
	 * @param s
	 * @return prefix (or empty string)
	 */
	public static String getPrefix(String s) {
		int idx = s.indexOf(S_COLON);
		return (idx == -1) ? S_EMPTY : s.substring(0, idx);
	}

	/**
	 * get localName from qualified name.
	 * 
	 * @param s
	 * @return localName (or empty string)
	 */
	public static String getLocalName(String s) {
		String ss = null;
		if (s != null) {
			int idx = s.indexOf(S_COLON);
			ss = (idx == -1) ? s : s.substring(idx + 1);
		}
		return ss;
	}

	/**
	 * convenience method to extract value of exactly one node.
	 * uses element.query(xpath, xPathContext);
	 * @param element
	 * @param xpath 
	 * @param xPathContext defines prefix/namespace used in query
	 * @return value if exactly 1 node (0 or many returns null)
	 */
	public static String getSingleValue(Element element, String xpath, XPathContext xPathContext) {
		String  s = null;
		if (element == null) {
			LOG.warn("Null element");
		} else {
			try {
				Nodes nodes = element.query(xpath, xPathContext);
				s = (nodes.size() == 1) ? nodes.get(0).getValue() : null;
			} catch (XPathException e) {
				throw new RuntimeException("Xpath: "+xpath, e);
			}
		}
		return s;
	}
	/**
	 * convenience method to extract value of exactly one node..
	 * uses element.query(xpath, xPathContext);
	 * @param element
	 * @param xpath 
	 * @param xPathContext defines prefix/namespace used in query
	 * @return value if exactly 1 node (0 or many returns null)
	 */
	public static String getSingleValue(Element element, String xpath) {
		String  s = null;
		if (element == null) {
			LOG.warn("Null element");
		} else {
			try {
				Nodes nodes = element.query(xpath);
				s = (nodes.size() == 1) ? nodes.get(0).getValue() : null;
			} catch (XPathException e) {
				throw new RuntimeException("Xpath: "+xpath, e);
			}
		}
		return s;
	}

	/**
	 * convenience method to extract value of the first of one-or-more nodes.
	 * uses element.query(xpath, xPathContext);
	 * @param element
	 * @param xpath 
	 * @param xPathContext defines prefix/namespace used in query
	 * @return value if exactly 1 node (0 or many returns null)
	 */
	public static String getFirstValue(Element element, String xpath, XPathContext xPathContext) {
		String  s = null;
		if (element == null) {
			LOG.warn("Null element");
		} else {
			try {
				Nodes nodes = element.query(xpath, xPathContext);
				s = (nodes.size() >= 1) ? nodes.get(0).getValue() : null;
			} catch (XPathException e) {
				throw new RuntimeException("Xpath: "+xpath, e);
			}
		}
		return s;
	}
	
	/**
	 * convenience method to get exactly one element.
	 * uses element.query(xpath, xPathContext);
	 * @param element
	 * @param xpath 
	 * @param xPathContext defines prefix/namespace used in query
	 * @return value if exactly 1 element (0 or many returns null)
	 */
	public static Element getSingleElement(Element element, String xpath, XPathContext xPathContext) {
		try {
			Nodes nodes = element.query(xpath, xPathContext);
			return (nodes.size() == 1) ? (Element) nodes.get(0) : null;
		} catch (XPathException e) {
			throw new RuntimeException("Xpath: "+xpath, e);
		}

	}
	

	
	/**
	 * converts an Elements to a java array. we might convert code to use
	 * Elements through later so this would be unneeded
	 * 
	 * @param elements
	 * @param obj
	 *            type of array (e.g. "new CMLAtom[0]"
	 * @return the java array 0f objects
	 */
	public final static Object[] toArray(Elements elements, Object[] obj) {
		List<Element> list = new ArrayList<Element>();
		for (int i = 0; i < elements.size(); i++) {
			list.add(elements.get(i));
		}
		return list.toArray(obj);
	}

	/**
	 * debug an element. outputs XML to sysout indent = 2
	 * 
	 * @param el
	 *            the element
	 * @deprecated use debug(el, message) instead
	 */
	public static void debug(Element el) {
		try {
			debug(el, System.out, 2);
		} catch (IOException e) {
			throw new RuntimeException("BUG " + e);
		}
	}

	/**
	 * debug an element. outputs XML to syserr
	 * 
	 * @param el
	 *            the element
	 */
	public static void debugToErr(Element el) {
		try {
			debug(el, System.err, 2);
		} catch (IOException e) {
			throw new RuntimeException("BUG " + e);
		}
	}

	/**
	 * debug an element.
	 * 
	 * @param el
	 *            the element
	 * @param os
	 *            output stream
	 * @param indent
	 *            indentation
	 * @throws IOException
	 */
	public static void debug(Element el, String message) {
		Util.println(">>>>" + message + ">>>>");
		XMLUtil.debug(el);
		Util.println("<<<<" + message + "<<<<");
	}

	/**
	 * debug an element.
	 * 
	 * @param el
	 *            the element
	 * @param os
	 *            output stream
	 * @param indent
	 *            indentation
	 * @throws IOException
	 */
	public static void debug(Element el, OutputStream os, int indent)
			throws IOException {
		Document document;
		if (el != null) {
			Node parent = el.getParent();
			if (parent instanceof Document) {
				document = (Document) parent;
			} else {
				Element copyElem = new Element(el);
				document = new Document(copyElem);
			}
			Serializer serializer = new Serializer(os, "UTF-8");
			if (indent >= 0) {
				serializer.setIndent(indent);
			}
			serializer.write(document);
		} else {
			LOG.warn("null element");
		}
	}
	
	public static void debug(Element el, File file, int indent) throws IOException {
		if (file == null) {
			throw new RuntimeException("null file");
		}
		if (el == null) {
			throw new RuntimeException("null element");
		}
		if (file.isDirectory()) {
			file.mkdirs();
		} else {
			File parent = file.getParentFile();
			if (parent != null) {
				parent.mkdirs();
			}
		}
		debug(el, new FileOutputStream(file), indent);
	}
	
	/** convenience method to avoid trapping exception
	 * 
	 * @param elem
	 * @param file
	 * @param indent
	 */
	public static void outputQuietly(Element elem, File file, int indent) {
		try {
			XMLUtil.debug(elem, new FileOutputStream(file), indent);
		} catch (Exception e) {
			throw new RuntimeException("cannot write file:  " + file, e);
		}
	}

	/**
	 * convenience method to get resource from XMLFile. the resource is packaged
	 * with the classes for distribution. typical filename is
	 * org/contentmine/molutil/elementdata.xml for file elementdata.xml in class
	 * hierarchy org.contentmine.molutil
	 * 
	 * @param filename
	 *            relative to current class hierarchy.
	 * @return document for resource
	 * @throws IOException
	 */
	public static Document getXMLResource(String filename) throws IOException {
		Document document = null;
		InputStream in = null;
		try {
			in = Util.getInputStreamFromResource(filename);
			document = (Document) new Builder().build(in);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("" + e + " in " + filename);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return document;
	}

	/**
	 * convenience routine to get child nodes (iterating through getChild(i) is
	 * fragile if children are removed)
	 * 
	 * @param el
	 *            may be null
	 * @return list of children (immutable) - empty if none
	 */
	public static List<Node> getChildNodes(Element el) {
		List<Node> childs = new ArrayList<Node>();
		if (el != null) {
			for (int i = 0; i < el.getChildCount(); i++) {
				childs.add(el.getChild(i));
			}
		}
		return childs;
	}

	/**
	 * parses XML string into element. convenience method to avoid trapping
	 * exceptions when string is known to be valid
	 * 
	 * @param xmlString
	 * @return root element
	 * @throws RuntimeException
	 */
	public static Element parseXML(String xmlString) throws RuntimeException {
		Element root = null;
		if (xmlString == null) {
			throw new RuntimeException("null xml");
		}
		if (xmlString.trim().length() == 0) {
			throw new RuntimeException("empty xml");
		}
		try {
			Document doc = new Builder().build(new StringReader(xmlString));
			root = doc.getRootElement();
		} catch (Exception e) {
			System.out.println(">xml>"+xmlString+"<");
			throw new RuntimeException(e);
		}
		return root;
	}

	/**
	 * convenience routine to get query nodes (iterating thorugh get(i) is
	 * fragile if nodes are removed)
	 * 
	 * @param node
	 *            (can be null)
	 * @param xpath
	 *            xpath relative to node
	 * @param context
	 * @return list of nodes (immutable) - empty if none
	 */
	public static List<Node> getQueryNodes(Node node, String xpath,
			XPathContext context) {
		List<Node> nodeList = new ArrayList<Node>();
		if (node != null) {
			try {
				Nodes nodes = node.query(xpath, context);
				for (int i = 0; i < nodes.size(); i++) {
					nodeList.add(nodes.get(i));
				}
			} catch (XPathException e) {
				throw new RuntimeException("Xpath: "+xpath, e);
			}
		}
		return nodeList;
	}

	/**
	 * convenience routine to get query nodes (iterating through get(i) is
	 * fragile if nodes are removed)
	 * 
	 * @param node
	 * @param xpath
	 * @return list of nodes (immutable) - empty if none or null node
	 */
	public static List<Node> getQueryNodes(Node node, String xpath) {
		List<Node> nodeList = new ArrayList<Node>();
		if (node != null && xpath != null) {
			try {
				Nodes nodes = node.query(xpath);
				for (int i = 0; i < nodes.size(); i++) {
					nodeList.add(nodes.get(i));
				}
			} catch (Exception e) {
				throw new RuntimeException("Bad xpath: "+xpath, e);
			}
		}
		return nodeList;
	}

	/**
	 * convenience routine to get query Elements 
	 * returns empty list if ANY nodes are not elements
	 * @param node
	 * @param xpath
	 * @return list of nodes (immutable) - empty if none or null node
	 */
	public static List<Element> getQueryElements(Node node, String xpath) {
		List<Node> nodes = getQueryNodes(node, xpath);
		return castNodesToElements(nodes);
	}

	/**
	 * convenience routine to get query Elements 
	 * returns empty list if ANY nodes are not elements
	 * @param node
	 * @param xpath
	 * @return list of nodes (immutable) - empty if none or null node
	 */
	public static List<Element> getQueryElements(Node node, String xpath, XPathContext context) {
		List<Node> nodes = getQueryNodes(node, xpath, context);
		return castNodesToElements(nodes);
	}

	private static List<Element> castNodesToElements(List<Node> nodes) {
		List<Element> elements = new ArrayList<Element>();
		for (Node n : nodes) {
			if (!(n instanceof Element)) {
				return new ArrayList<Element>();
			}
			elements.add((Element) n);
		}
		return elements;
	}

	/**
	 * get next sibling.
	 * 
	 * @author Eliotte Rusty Harold
	 * @param current
	 *            may be null
	 * @return following sibling or null
	 */
	public static Node getFollowingSibling(Node current) {
		Node node = null;
		if (current != null) {
			ParentNode parent = current.getParent();
			if (parent != null) {
				int index = parent.indexOf(current);
				if (index + 1 < parent.getChildCount()) {
					node = parent.getChild(index + 1);
				}
			}
		}
		return node;
	}

	/**
	 * get next sibling element.
	 * 
	 * @param current may be null
	 * @return following sibling or null
	 */
	public static Element getFollowingSiblingElement(Node current) {
		Element element = null;
		if (current != null) {
			ParentNode parent = current.getParent();
			if (parent != null) {
				int index = parent.indexOf(current) + 1;
				for (; index < parent.getChildCount(); index++) {
					Node sibling = parent.getChild(index);
					if (sibling instanceof Element) {
						element = (Element) sibling;
						break;
					}
				}
			}
		}
		return element;
	}

	/**
	 * get preceding sibling element.
	 * 
	 * @param current may be null
	 * @return following sibling or null
	 */
	public static Element getPrecedingSiblingElement(Node current) {
		Element element = null;
		if (current != null) {
			ParentNode parent = current.getParent();
			if (parent != null) {
				int index = parent.indexOf(current) - 1;
				for (; index >= 0; index--) {
					Node sibling = parent.getChild(index);
					if (sibling instanceof Element) {
						element = (Element) sibling;
						break;
					}
				}
			}
		}
		return element;
	}

	/**
	 * get previous sibling.
	 * 
	 * @param current
	 * @return previous sibling
	 */
	public static Node getPrecedingSibling(Node current) {
		Node node = null;
		if (current != null) {
			ParentNode parent = current.getParent();
			if (parent != null) {
				int index = parent.indexOf(current);
				if (index > 0) {
					node = parent.getChild(index - 1);
				}
			}
		}
		return node;
	}

	/**
	 * gets last text descendant of element. this might be referenced from the
	 * following-sibling and will therefore be the immediately preceding chunk
	 * of text in document order if the node is a text node returns itself
	 * 
	 * @param node
	 * @return Text node or null
	 */
	public static Text getLastTextDescendant(Node node) {
		List<Node> l = XMLUtil.getQueryNodes(node, ".//text() | self::text()");
		return (l.size() == 0) ? null : (Text) l.get(l.size() - 1);
	}

	/**
	 * gets first text descendant of element. this might be referenced from the
	 * preceding-sibling and will therefore be the immediately following chunk
	 * of text in document order if the node is a text node returns itself
	 * 
	 * @param node
	 * @return Text node or null
	 */
	public static Text getFirstTextDescendant(Node node) {
		List<Node> l = XMLUtil.getQueryNodes(node, ".//text() | self::text()");
		return (l.size() == 0) ? null : (Text) l.get(0);
	}

	/**
	 * transfers children of 'from' to 'to'.
	 * 
	 * @param from
	 *            (will be left with no children)
	 * @param to
	 *            (will gain 'from' children appended after any existing
	 *            children
	 */
	public static void transferChildren(Element from, Element to) {
		int nc = from.getChildCount();
		int tc = to.getChildCount();
		for (int i = nc - 1; i >= 0; i--) {
			Node child = from.getChild(i);
			child.detach();
			to.insertChild(child, tc);
		}
	}

	/**
	 * copies atributes of 'from' to 'to'
	 * @param element
	 * @throws IllegalArgumentException null arguments
	 */
	public static void copyAttributes(Element from, Element to) throws IllegalArgumentException {
		if (to == null || from == null) {
			throw new IllegalArgumentException("cannot copy null elements");
		}
		int natt = from.getAttributeCount();
        for (int i = 0; i < natt; i++) {
            Attribute newAtt = new Attribute(from.getAttribute(i));
            to.addAttribute(newAtt);
        }
	}

	/**
	 * get XOM default canonical string.
	 * 
	 * @param node
	 * @return the string
	 */
	public static String getCanonicalString(Node node) throws IllegalArgumentException{
		if (node == null) {
			throw new IllegalArgumentException("null node");
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Canonicalizer canon = new Canonicalizer(baos);
		try {
			canon.write(node);
		} catch (IOException e) {
			throw new RuntimeException("should never throw " + e);
		}
		return baos.toString();
	}

	/**
	 * remeoves all whitespace-only text nodes.
	 * 
	 * @param element
	 *            to strip whitespace from
	 */
	public static void removeWhitespaceNodes(Element element) {
		int nChild = element.getChildCount();
		List<Node> nodeList = new ArrayList<Node>();
		for (int i = 0; i < nChild; i++) {
			Node node = element.getChild(i);
			if (node instanceof Text) {
				if (node.getValue().trim().length() == 0) {
					nodeList.add(node);
				}
			} else if (node instanceof Element) {
				Element childElement = (Element) node;
				removeWhitespaceNodes(childElement);
			} else {
			}
		}
		for (Node node : nodeList) {
			node.detach();
		}
	}

	/** removes all elements with given xpath.
	 * 
	 * @param element
	 * @param xpath
	 */
	public static void removeElementsByXPath(Element element, String xpath) {
		List<Element> elements = XMLUtil.getQueryElements(element, xpath);
		for (Element elem : elements) {
			elem.detach();
		}
	}


	/**
	 * sets text content of element. Does not support mixed content.
	 * 
	 * @param element
	 * @param s
	 * @throws RuntimeException
	 *             if element already has element content
	 */
	public static void setXMLContent(Element element, String s) {
		List<Node> elements = XMLUtil.getQueryNodes(element, S_STAR);
		if (elements.size() > 0) {
			throw new RuntimeException(
					"Cannot set text with element children");
		}
		Text text = XMLUtil.getFirstTextDescendant(element);
		if (text == null) {
			text = new Text(s);
			element.appendChild(text);
		} else {
			text.setValue(s);
		}
	}

	/**
	 * sets text content of element. Does not support mixed content.
	 * 
	 * @param element
	 * @return text value
	 * @throws RuntimeException
	 *             if element already has element content
	 */
	public static String getXMLContent(Element element) {
		List<Node> elements = XMLUtil.getQueryNodes(element, S_STAR);
		if (elements.size() > 0) {
			throw new RuntimeException(
					"Cannot get text with element children");
		}
		return element.getValue();
	}

	public static String toXMLString(Element element) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			XMLUtil.debug(element, baos, 0);
		} catch (IOException e) {
		}
		return new String(baos.toByteArray());
	}
	
	/**
	 * bug report.
	 * 
	 * @param message
	 */
	public static void BUG(String message) {
		Util.BUG(message);
	}

	/**
	 * make id from string. convert to lowercase and replace space by underscore
	 * 
	 * @param s
	 * @return new id (null if s is null)
	 */
	public static String makeId(String s) {
		String id = null;
		if (s != null) {
			id = s.toLowerCase();
			id = id.replace(S_SPACE, S_UNDER);
		}
		return id;
	}

	/**
	 * create local Abstract class name. e.g. AbstractFooBar from fooBar
	 * 
	 * @param name
	 * @return name
	 */
	public static String makeAbstractName(String name) {
		return "Abstract" + capitalize(name);
	}

	/**
	 * capitalize name e.g. FooBar from fooBar
	 * 
	 * @param name
	 * @return name
	 */
	public static String capitalize(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * tests 2 XML objects for equality using recursive descent.
	 * includes namespace testing
	 * 
	 * @param refString xml serialization of first Element
	 * @param testNode second Element
	 * @param stripWhite if true remove w/s nodes
	 * @return message of where elements differ (null if identical)
	 */
	public static String equalsCanonically(String refNodeXML, Element testElement,
			boolean stripWhite) {
		Element refElement = null;
		try {
			refElement = new Builder().build(new StringReader(refNodeXML)).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Parsing failed: "+refNodeXML);
		}
		String message = equalsCanonically(refElement, testElement, stripWhite, "/");
		LOG.trace("EQCAN "+message);
		return message;
	}
	
	/**
	 * tests 2 XML objects for equality using recursive descent.
	 * includes namespace testing
	 * 
	 * @param refNode first node
	 * @param testNode second node
	 * @param stripWhite if true remove w/s nodes
	 * @return message of where elements differ (null if identical)
	 */
	public static String equalsCanonically(Element refElement, Element testElement,
			boolean stripWhite) {
		return equalsCanonically(refElement, testElement, stripWhite, "./");
	}
	/**
	 * tests 2 XML objects for equality using recursive descent.
	 * includes namespace testing
	 * 
	 * @param refElement first node
	 * @param testElement second node
	 * @param stripWhite if true remove w/s nodes
	 * @return message of where elements differ (null if identical)
	 */
	public static String equalsCanonically(Element refElement, Element testElement,
			boolean stripWhite, String xpath) {
		String message = null;
		// check if they are different objects
		if (refElement != testElement) {
			if (stripWhite) {
				refElement = new Element(refElement);
				removeWhitespaceNodes(refElement);
				testElement = new Element(testElement);
				removeWhitespaceNodes(testElement);
			}
			xpath = xpath+"*[local-name()='"+refElement.getLocalName()+"']/";
			message = equalsCanonically(refElement, testElement, xpath);
		}
		return message;
	}

	private static String equalsCanonically(Element refElement, Element testElement, String xpath) {
		String message = null;
		// remove this as namespaces may be different in different serializations
//		message = XMLUtil.compareNamespacesCanonically(refElement, testElement, xpath);
//		if (message != null) {
//			return message;
//		}
		String refName = refElement.getLocalName();
		String testName = testElement.getLocalName();
		if (message == null && !refName.equals(testName)) {
			message = "element names differ at "+xpath+": "+refName+" != "+testName;
		}
		String refNamespace = refElement.getNamespaceURI();
		String testNamespace = testElement.getNamespaceURI();
		if (message == null && !refNamespace.equals(testNamespace)) {
			message = "element namespaces differ at "+xpath+": "+refNamespace+" != "+testNamespace;
		}
		if (message == null) {
			message = XMLUtil.compareAttributesCanonically(refElement, testElement, xpath);
		}
		if (message == null) {
			message = XMLUtil.compareChildNodesCanonically(refElement, testElement, xpath);
		}
		return message;
	}

	/** compares two XML files for equality using recursive descent.
	 * 
	 * @param refFile
	 * @param testFile
	 * @param stripWhite
	 * @return null if identical; else a message about errors in files, XML, etc.
	 */
    public static String equalsCanonically(File refFile, File testFile, boolean stripWhite) {
  		String message = isXMLFile(refFile);
  		message = (message != null) ? message : isXMLFile(testFile);
    	if (message == null) {
    		try {
	    	    Element refElement = XMLUtil.parseQuietlyToDocument(refFile).getRootElement();
	    	    Element testElement = XMLUtil.parseQuietlyToDocument(testFile).getRootElement();
	    	    message = XMLUtil.equalsCanonically(refElement, testElement, stripWhite);
    		} catch (Exception e) {
    			message = e.getMessage();
    		}
    	}
	    return message;
	}

	/** compares XML element against reference file for equality using recursive descent.
	 * 
	 * @param refFile
	 * @param testElement
	 * @param stripWhite
	 * @return null if identical; else a message about errors in files, XML, etc.
	 */
    public static String equalsCanonically(File refFile, Element testElement, boolean stripWhite) {
  		String message = isXMLFile(refFile);
    	if (message == null) {
    		try {
	    	    Element refElement = XMLUtil.parseQuietlyToDocument(refFile).getRootElement();
	    	    message = XMLUtil.equalsCanonically(refElement, testElement, stripWhite);
    		} catch (Exception e) {
    			message = e.getMessage();
    		}
    	}
	    return message;
	}

    /** check file is existing file, not null or directory,
     * 
     * @param file to check
     * @return null if no problems else message
     */
	public static String isXMLFile(File file) {
		String message = null;
    	if (file == null) {
    		message = "null ref file";
    	} else if (!file.exists()) {
    		message = "non-existent ref file "+file;
    	} else if (file.isDirectory()) {
    		message = "ref file is directory "+file;
    	}
		return message;
	}
 
    public static String getCommonLeadingString(String s1, String s2) {
        int l = Math.min(s1.length(), s2.length());
        int i;
        for (i = 0; i < l; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                break;
            }
        }
        return s1.substring(0, i);
    }
	/** compare namespaces on two elements
	 * 
	 * @param refNode
	 * @param testNode
	 * @param xpath current ancestry of refNode
	 * @return
	 */
	public static String compareNamespacesCanonically(Element refNode, Element testNode, String xpath) {
		String message = null;
		List<String> refNamespaceURIList = getNamespaceURIList(refNode);
		List<String> testNamespaceURIList = getNamespaceURIList(testNode);
		if (refNamespaceURIList.size() != testNamespaceURIList.size()) {
				message = "unequal namespace count;" +
				" ref "+refNamespaceURIList.size()+"; " +refNamespaceURIList+";"+
				" testCount "+testNamespaceURIList.size()+"; "+testNamespaceURIList;
		} else {
			for (String refNamespaceURI : refNamespaceURIList) {
				if (!testNamespaceURIList.contains(refNamespaceURI)) {
					message = "Cannot find "+refNamespaceURI+
					" in test namespaces ";
					break;
				}
			}
		}
		return message;
	}

	/**
	 * @param node
	 * @param count
	 */
	private static List<String> getNamespaceURIList(Element node) {
		List<String> namespaceURIList = new ArrayList<String>();
		for (int i = 0; i < node.getNamespaceDeclarationCount(); i++) {
			String prefix = node.getNamespacePrefix(i);
			String refNamespaceURI = node.getNamespaceURI(prefix);
			namespaceURIList.add(refNamespaceURI);
		}
		return namespaceURIList;
	}
	
	/** compare attributes on two elements.
	 * includes normalizing attribute values
	 * 
	 * @param refNode
	 * @param testNode
	 * @param xpath current ancestry of refNode
	 * @return
	 */
	public static String compareAttributesCanonically(Element refNode, Element testNode, String xpath) {
		String message = null;
		int refCount = refNode.getAttributeCount();
		int testCount = testNode.getAttributeCount();
		if (refCount != testCount) {
			message = "unequal attribute count at "+xpath+" ("+refCount+" != "+testCount+")";
		}
		if (message == null) {
			for (int i = 0; i < refCount; i++) {
				Attribute attribute = refNode.getAttribute(i);
				String name = attribute.getLocalName();
				String namespace = attribute.getNamespaceURI();
				String value = attribute.getValue();
				Attribute testAttribute = (namespace == null) ?
					testNode.getAttribute(name) :
					testNode.getAttribute(name, namespace);
				if (testAttribute == null) {
					message = "no attribute in test ("+xpath+") for "+XMLUtil.printName(name, namespace);
					break;
				}
				String refValue = XMLUtil.normalizeSpace(value);
				String testValue = XMLUtil.normalizeSpace(testAttribute.getValue());
				if (!refValue.equals(testValue)) {
					message = "normalized attribute values for ("+xpath+"@"+XMLUtil.printName(name, namespace)+") "+refValue+" != "+testValue;
					break;
				}
			}
		}
		LOG.trace("ATT MS "+message);
		return message;
	}
	
	private static String printName(String name, String namespace) {
		return name+((namespace == null || namespace.equals(S_EMPTY)) ? "" : "["+namespace+"]");
	}
	
	public static String normalizeSpace(String value) {
		return value.replaceAll(S_WHITEREGEX, S_SPACE).trim();
	}
	
	/** compare child nodes recursively
	 * 
	 * @param refNode
	 * @param testNode
	 * @param xpath current ancestry of refNode
	 * @return
	 */
	public static String compareChildNodesCanonically(Element refNode, Element testNode, String xpath) {
		String message = null;
		int refCount = refNode.getChildCount();
		int testCount = testNode.getChildCount();
		if (refCount != testCount) {
			message = "unequal child node count at "+xpath+" ("+refCount+" != "+testCount+")";
		}
		if (message == null) {
			for (int i = 0; i < refCount; i++) {
				String xpathChild = xpath+"node()[position()="+(i+1)+"]";
				Node refChildNode = refNode.getChild(i);
				Node testChildNode = testNode.getChild(i);
				Class<?> refClass = refChildNode.getClass();
				Class<?> testClass = testChildNode.getClass();
				if (!refClass.equals(testClass)) {
					message = "child node classes differ at "+xpathChild+" "+refClass+"/"+testClass;
					break;
				} else if (refChildNode instanceof Element) {
					message = XMLUtil.equalsCanonically((Element) refChildNode, (Element) testChildNode,
						xpathChild);
				} else {
					message = XMLUtil.compareNonElementNodesCanonically(refChildNode, testChildNode, xpath);
					if (message != null) {
						break;
					}
				}
			}
		}
		return message;
	}
	
	
	/** compare non-element nodes.
	 * not yet tuned for normalizing adjacent CDATA and other horrors
	 * @param refNode
	 * @param testNode
	 * @param xpath current ancestry of refNode
	 * @return
	 */
	public static String compareNonElementNodesCanonically(Node refNode, Node testNode, String xpath) {
		String message = null;
		String refValue = refNode.getValue();
		String testValue = testNode.getValue();
		if (refNode instanceof Comment) {
			if (!refValue.equals(testValue)) {
				message = "comments at ("+xpath+") differ: "+refValue+" != "+testValue;
			}
		} else if (refNode instanceof Text) {
			if (!refValue.equals(testValue)) {
				message = "text contents at ("+xpath+") differ: ["+refValue+"] != ["+testValue+"]";
			}
		} else if (refNode instanceof ProcessingInstruction) {
			String refTarget = ((ProcessingInstruction) refNode).getTarget();
			String testTarget = ((ProcessingInstruction) testNode).getTarget();
			if (!refTarget.equals(testTarget)) {
				message = "PI targets at ("+xpath+") differ: "+refTarget+" != "+testTarget;
			}
		} else {
			LOG.warn("Unknown XML element in comparison: "+refNode.getClass());
		}
		return message;
	}

	/**
	 * some formatted XML introduces spurious WS after text strings
	 * @param element
	 */
	public static void stripTrailingWhitespaceinTexts(Element element) {
		Nodes texts = element.query("//text()");
		for (int i = 0; i < texts.size(); i++) {
			Text text = (Text) texts.get(i);
			String value = text.getValue();
			value = Util.rightTrim(value);
			text.setValue(value);
		}
	}
	
	public static Element getSingleElement(Element element, String xpath) {
		Nodes nodes;
		try {
			nodes = element.query(xpath);
		} catch (XPathException e) {
			throw new RuntimeException("Xpath: "+xpath, e);
		}
		return (nodes.size() == 1) ? (Element) nodes.get(0) : null;
	}

	public static void detach(nu.xom.Element element) {
		ParentNode parent = (element == null) ? null : element.getParent();
		if (parent != null) {
			if (parent instanceof Document) {
				parent.replaceChild(element, new Element(DUMMY));
			} else {
				element.detach();
			}
		}
	}
	
	/** gets Document for element.
	 * 
	 * <p>
	 * if none exists, creates one. Note that if the element already has ancestry a Document
	 * is created for the rootElement. That may mean that XOM.Serializer writes the whole document.
	 * </p>
	 * 
	 * @param element
	 * @return
	 */
	public static Document ensureDocument(Element element) {
		Document doc = null;
		if (element != null) {
			doc = element.getDocument();
			if (doc == null) {
				Element parent = null;
				Element root = element;
				while (true) {
					parent = (Element) root.getParent();
					if (parent == null) break;
					root = parent;
				}
				doc = new Document(root);
			}
		}
		return doc;
	}
	
	public static Element parseQuietlyToRootElement(File file) {
		Document doc = null;
		try {
			if (file != null /*&& file.exists()*/) { 
				doc = parseQuietlyToDocument(new FileInputStream(file));
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("cannot read file: "+file, e);
		}
		return doc == null ? null : doc.getRootElement();
	}
	
	public static Element parseQuietlyToRootElement(InputStream is) {
		Document doc = parseQuietlyToDocument(is);
		return doc == null ? null : doc.getRootElement();
	}
		
	public static Document parseQuietlyToDocument(InputStream is) {
		Document document = null;
		if (is == null) {
			throw new RuntimeException("null input stream");
		}
		try {
			document = new Builder().build(is);
		} catch (Exception e) {
			throw new RuntimeException("cannot parse/read stream: ", e);
		}
		return document;
	}
	
	public static Document parseResourceQuietlyToDocument(String resource) {
		Document document = null;
		try {
			document = new Builder().build(Util.getInputStreamFromResource(resource));
		} catch (Exception e) {
			throw new RuntimeException("cannot parse/read resource: "+resource, e);
		}
		return document;
	}
	
	/**
	 * WARNING - if DTD present may take ages
	 * consider parseQuietlyToDocumentWithoutDTD
	 * @param xmlFile
	 * @return
	 */
	@Deprecated
	public static Document parseQuietlyToDocument(File xmlFile) {
		Document document = null;
		try {
			document = new Builder().build(xmlFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("cannot find file: "+xmlFile.getAbsolutePath(), e);
		} catch (Exception e) {
			throw new RuntimeException("cannot parse/read file: "+xmlFile.getAbsolutePath(), e);
		}
		return document;
	}
	
	/**
<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'
          'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>
	 * @param s
	 * @return
	 * @throws IOException
	 */
	@Deprecated // relies on string .dtd" and this could be apostrophe. 
	public static Document stripDTDAndOtherProblematicXMLHeadings(String s) throws IOException {
		
		if (s == null || s.length() == 0) {
			throw new RuntimeException("zero length document");
		}
		// strip DTD
		int idx = s.indexOf(DTD);
		String baosS = s;
		if (idx != -1) {
			int ld = idx+DTD.length()+1;
			if (ld < 0) {
				throw new RuntimeException("BUG in stripping DTD");
			}
			try {
				baosS = s.substring(ld);
			} catch (Exception e) {
				throw new RuntimeException("cannot parse string: ("+s.length()+"/"+ld+"/"+idx+") "+s.substring(0, Math.min(500, s.length())),e);
			}
		} 
		// strip HTML namespace
		baosS = baosS.replace(" xmlns=\"http://www.w3.org/1999/xhtml\"", "");
		// strip XML declaration
		baosS = baosS.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		baosS = removeScripts(baosS);
		Document document;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(baosS.getBytes()); // avoid reader
			document = new Builder().build(bais);
		} catch (Exception e) {
			throw new RuntimeException("BUG: DTD stripper failed to create valid XML", e);
		}
		return document;
	}
	
	/**
	 * Removes DOCTYPE 
	 * 
	 * DOCTYPE can cause problems by requiring to load DTD from URL which can
	 * take many seconds or, if offline, can cause failure to parse.
	 * 
	 * This is dangerous but so is the DOCTYPE
	 * 
<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'
          'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static String stripDTD(String s) {
		if (s != null) {
			s = s.replaceAll(DOCTYPE, "");
		}
		return s;
	}

	/**
	 * Removes DOCTYPE and then parses
	 * 
	 * DOCTYPE can cause problems by requiring to load DTD from URL which can
	 * take many seconds or, if offline, can cause failure to parse.
	 * 
	 * This is dangerous but so is the DOCTYPE
	 * 
<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'
          'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static Element stripDTDAndParse(String s) {
		Element root = null;
		if (s != null) {
			s = stripDTD(s);
			root = XMLUtil.parseXML(s);
		}
		return root;
	}

	/** adds missing end tag
	 * 
	 * crude - adds /> and then deletes any /></tag> 
	 * 
	 * 		String s = "<a><meta></a>";
		s = XMLUtil.addMissingEndTags(s, "meta");
		Assert.assertEquals("<a><meta/></a>", s);

	 * @param s
	 * @param tag
	 * @return
	 */
	public static String addMissingEndTags(String s, String tag) {
		// convert balanced tags (<tag ...></tag> to <tag .../>
		s = s.replaceAll(">\\s*</"+tag+">", "/>");
		StringBuilder sb = new StringBuilder(s);
		int i = 0;
		boolean inTag = false;
		String start = "<"+tag;
		int stl = start.length();
		
		while (i < sb.length()) {
			if (i < sb.length() - stl && start.equals(sb.substring(i, i + stl))) {
				inTag = true;
				i += stl;
			} else if (inTag && (sb.charAt(i) == '>')) {
				if (sb.charAt(i-1) != '/') {
					sb.insert(i, '/');
					i++;
				}
				i++;
				inTag = false;
			} else {
				i++;
			}
		}
		return sb.toString();
	}

	public static String removeScripts(String s) {
		return removeTags("script", s);
	}
	
	public static String removeTags(String tag, String ss) {
		int current = 0;
		StringBuilder sb = new StringBuilder();
		String startTag = "<"+tag;
		String endTag = "</"+tag+">";
		while (true) {
			int i = ss.indexOf(startTag, current);
			if (i == -1) {
				sb.append(ss.substring(current));
				break;
			}
			sb.append(ss.substring(current, i));
			i = ss.indexOf(endTag, current);
			if (i == -1) {
				throw new RuntimeException("missing endTag: "+endTag);
			}
			current = (i + endTag.length());
		}
		return sb.toString();
	}

	public static void debugPreserveWhitespace(Element element) {
		try {
			XMLUtil.debug(element, System.out, 0);
		} catch (Exception e) {
			throw new RuntimeException("BUG", e);
		}
	}

	public static Element normalizeWhitespaceInTextNodes(Element element) {
		Nodes texts = element.query(".//text()");
		for (int i = 0; i < texts.size(); i++) {
			Text text = (Text) texts.get(i);
			text.setValue(normalizeSpace(text.getValue()));
		}
		return element;
	}

	/**
	 * copies atributes of 'from' to 'to'
	 * @param element
	 */
	public static void copyAttributesFromTo(Element from, Element to) {
		int natt = from.getAttributeCount();
	    for (int i = 0; i < natt; i++) {
	        Attribute newAtt = new Attribute(from.getAttribute(i));
	        to.addAttribute(newAtt);
	    }
	}

	/** it attribute exists detach it.
	 * @param element
	 * @param attName
	 */
	public static void deleteAttribute(Element element, String attName) {
		Attribute att = element.getAttribute(attName);
		if (att != null) {
			att.detach();
		}
	}

	public static Document parseQuietlyToDocumentWithoutDTD(File file) {
		Document doc = null;
		try {
			String s = IOUtils.toString(new FileInputStream(file));
			doc = XMLUtil.stripDTDAndOtherProblematicXMLHeadings(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return doc;
	}

	/** checks that attribute names are in allowed list.
	 * 
	 * @param element
	 * @param allowedAttributeNames
	 * 
	 * @throws RuntimeException if unknown attribute
	 */
	public static void checkAttributeNames(Element element, List<String> allowedAttributeNames) {
		for (int i = 0; i < element.getAttributeCount(); i++) {
			String attributeName = element.getAttribute(i).getLocalName();
			if (!allowedAttributeNames.contains(attributeName)) {
				throw new RuntimeException("Unknown attribute: "+attributeName+" in element: "+element.getLocalName());
			}
		}
	}
	
	/** checks that childNode names are in allowed list.
	 * 
	 * @param element
	 * @param allowedChildNodeNames 
	 *     can include element names, #text, - other node types are always allowed
	 * 
	 * @throws RuntimeException if unknown childElement
	 */
	public static void checkChildElementNames(Element element, List<String> allowedChildNodeNames) {
		for (int i = 0; i < element.getChildCount(); i++) {
			Node childNode = element.getChild(i);
			if (childNode instanceof Text) {
				// we allow whitespace
				if (childNode.getValue().trim().length() > 0 
						&& !allowedChildNodeNames.contains(TEXT)) {
					
				}
			} else if (childNode instanceof Element) {
				String elementName = element.getAttribute(i).getLocalName();
				if (!allowedChildNodeNames.contains(elementName)) {
					throw new RuntimeException("Unknown element: "+elementName+" in element: "+element.getLocalName());
				}
			} else {
				// OK
			}
		}
	}

	/** substitute all low-order non-xml chars by (char)127
	 * 
	 * @param txt
	 * @return
	 */
	public static String removeNonXML(String txt) {
		StringBuilder sb = new StringBuilder(txt);
		for (int i = 0; i < sb.length(); i++) {
			int ch = sb.codePointAt(i);
			if (ch < 32 && ch != 9 && ch != 10 && ch != 13) {
				sb.setCharAt(i, (char)127);
			}
		}
		txt = sb.toString();
		return txt;
	}

	protected void removeAttributeWithName(String name) {
	}

	public static String toString(List<Element> elements) {
		StringBuilder sb = new StringBuilder();
		for (Element element : elements) {
			sb.append(element.toXML());
		}
		return sb.toString();
	}

	/** create xpath for local-name()
	 * creates *[local-name()='foo']
	 * @param result
	 * @return
	 */
	public static String localNameXPath(String localName) {
		return "*[local-name()='" + localName + "']";
	}

	public static void writeQuietly(Element element, File xmlFile, int indent) {
		if (element != null && xmlFile != null) {
			try {
				XMLUtil.debug(element, xmlFile, indent);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write file: "+xmlFile, e);
			}
		}
	}

}

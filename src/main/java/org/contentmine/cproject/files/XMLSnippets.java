package org.contentmine.cproject.files;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

/** manages a list of XML elements extracted from a document by XPath
 * 
 * example:
 *  <snippets>
 *    <foo>...</foo>
 *    <result>...</result>
 *  </snippets>
 *  
 * @author pm286
 *
 */
public class XMLSnippets extends Element implements Iterable<Element>{


	private static final Logger LOG = Logger.getLogger(XMLSnippets.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String SNIPPETS = "snippets";
	public static final String FILE = "file";
	private static final String TITLE = "title";
	
	private File file;
	private List<Element> elementList;
	
	public static XMLSnippets createXMLSnippets(Element snippets) {
		XMLSnippets xmlSnippets = null;
		if (snippets != null && snippets.getLocalName().equals(XMLSnippets.SNIPPETS)) {
			xmlSnippets = new XMLSnippets();
			XMLUtil.copyAttributes(snippets, xmlSnippets);
			List<Element> childElements = XMLUtil.getQueryElements(snippets, "*");
			for (Element childElement : childElements) {
				xmlSnippets.appendChild(childElement.copy());
			}
		}
		return xmlSnippets;
	}

	public Iterator<Element> iterator() {
		getOrCreateElementChildren();
		return elementList.iterator();
	}

	private List<Element> getOrCreateElementChildren() {
		if (elementList == null) {
			elementList = XMLUtil.getQueryElements(this, "*");
		}
		return elementList;
	}

	public XMLSnippets() {
		super(SNIPPETS);
	}

	public XMLSnippets(List<Element> elementList, File file) {
		this();
		this.file = file;
		addFileAttribute(file);
		for (Element element : elementList) {
			this.appendChild(element.copy());
		}
	}

	public void addFileAttribute(File file) {
		this.addAttribute(new Attribute(FILE, file.toString()));
	}

	@Override
	public void appendChild(Node node) {
		clearElementList();
		super.appendChild(node);
	}

	@Override
	public void appendChild(String s) {
		super.appendChild(s);
	}

	private void clearElementList() {
		elementList = null;
	}
	
	public int size() {
		return getOrCreateElementChildren().size();
	}

	public Element get(int i) {
		getOrCreateElementChildren();
		return i >= elementList.size() || i < 0 ? null : elementList.get(i);
	}

	/** gets XML value of snippet(i).
	 * 
	 * @param i
	 * @return null if out of range else element.get(i).getValue()
	 */
	public String getValue(int i) {
		Element element = this.get(i);
		return element == null ? null : element.getValue();
	}

	public void addFile(File file) {
		this.file = file;
	}
	
	/** returns XML representation.
	 * 
	 */
	public String toString() {
		return this.toXML();
	}

	public String getFilename() {
		return this.getAttributeValue(FILE);
	}

	public void setTitle(String title) {
		if (title != null) {
			this.addAttribute(new Attribute(TITLE, title));
		} else {
			LOG.warn("NO TITLE");
		}
	}

}

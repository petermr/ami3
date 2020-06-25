package org.contentmine.ami.plugins.phylotree.nexml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;

public class NexmlElement extends Element {

	private final static Logger LOG = LogManager.getLogger(NexmlElement.class);
	
	public static final String ID = "id";
	private static final String LABEL = "label";
	static final String NEX = "nex";
	static final String NEXML = "nexml";
	static final String NEXML_NS = "http://www.nexml.org/2009";
	static final String XSI = "xsi";
	static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
	
	public static final String FLOAT_TREE = "FloatTree";

	/** constructor.
	 * 
	 * @param name
	 */
	public NexmlElement(String name) {
		super(name, NEXML_NS);
	}

	public NexmlElement(NexmlElement element) {
        super(element);
	}
	
	/** copy constructor from non-subclassed elements
	 */
	public static NexmlElement readAndCreateNEXML(Element element) {
		NexmlElement newElement = null;
		NexmlTree currentTree = null;
		String tag = element.getLocalName();
		if (tag == null || tag.equals("")) {
			throw new RuntimeException("no tag");
		} else if (tag.equals(NexmlEdge.TAG)) {
			newElement = new NexmlEdge();
		} else if (tag.equals(NexmlNEXML.TAG)) {
			newElement = new NexmlNEXML();
		} else if (tag.equals(NexmlNode.TAG)) {
			newElement = new NexmlNode(currentTree);
		} else if (tag.equals(NexmlOtu.TAG)) {
			newElement = new NexmlOtu();
		} else if (tag.equals(NexmlOtus.TAG)) {
			newElement = new NexmlOtus();
		} else if (tag.equals(NexmlTree.TAG)) {
			newElement = new NexmlTree();
			currentTree = (NexmlTree) newElement;
		} else if (tag.equals(NexmlTrees.TAG)) {
			newElement = new NexmlTrees();
		} else {
			LOG.error("unsupported NexML element: "+tag);
		}
		copyAttributesAndProcessDescendants(element, newElement);
        return newElement;
	}

	private static void copyAttributesAndProcessDescendants(Element element, NexmlElement newElement) {
		if (newElement != null) {
			XMLUtil.copyAttributes(element, newElement);
			for (int i = 0; i < element.getChildCount(); i++) {
				Node child = element.getChild(i);
				if (child instanceof Element) {
					NexmlElement nexmlChild = NexmlElement.readAndCreateNEXML((Element)child);
					newElement.appendChild(nexmlChild);
				} else {
					newElement.appendChild(child.copy());
				}
			}
			
		}
	}
	
	/** converts an NEXML file to NEXMLElement
	 * 
	 * @param file
	 * @return
	 */
	public static NexmlElement readAndCreateNEXML(File file) throws Exception {
		return readAndCreateNEXML(new FileInputStream(file));
	}
	
	/** converts an NEXML file to NEXMLElement
	 * 
	 * @param file
	 * @return
	 */
	public static NexmlNEXML readAndCreateNEXML(InputStream is) {
		Element element = XMLUtil.parseQuietlyToDocument(is).getRootElement();
		NexmlNEXML nexml = null;
		if (element != null) {
			nexml = (NexmlNEXML) readAndCreateNEXML(element);
			nexml.buildTrees();
		}
		return nexml;
	}
	
	public void setId(String id){
		if (id != null) {
			this.addAttribute(new Attribute(ID, id));
		}
	}
	
	public void setLabel(String label){
		this.addAttribute(new Attribute(LABEL, label));
	}
	
	public void debug() {
		XMLUtil.debug(this, "nexml");
	}

	public String getId() {
		return getAttributeValue(ID);
	}

}

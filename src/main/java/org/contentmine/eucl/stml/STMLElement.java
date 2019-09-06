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

package org.contentmine.eucl.stml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.stml.attribute.AttributeFactory;
import org.contentmine.eucl.stml.attribute.DelimiterAttribute;
import org.contentmine.eucl.stml.attribute.DictRefAttribute;
import org.contentmine.eucl.stml.attribute.DoubleSTAttribute;
import org.contentmine.eucl.stml.attribute.IdAttribute;
import org.contentmine.eucl.stml.attribute.IntSTAttribute;
import org.contentmine.eucl.stml.attribute.StringSTAttribute;
import org.contentmine.eucl.stml.attribute.UnitsAttribute;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.IllegalAddException;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.ProcessingInstruction;
import nu.xom.Serializer;
import nu.xom.Text;

/**
 * base class for all STML elements
 * can be sorted on id attribute
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class STMLElement extends Element implements XMLConstants {

	private final static Logger LOG = Logger.getLogger(STMLElement.class);
	
    final static String ID = "id";

    private AbstractSTMTool tool;

	// attribute:   constantToSI
	
	/** cache */
	DoubleSTAttribute _att_constanttosi = null;

	// attribute:   dataType
	
	/** cache */
	StringSTAttribute _att_datatype = null;

	// attribute:   delimiter
	
	/** cache */
	DelimiterAttribute _att_delimiter = null;

	// attribute:   dictRef
	
	/** cache */
	DictRefAttribute _att_dictref = null;

	// attribute:   id
	
	/** cache */
	IdAttribute _att_id = null;

	// attribute:   size
	
	/** cache */
	IntSTAttribute _att_size = null;

	// attribute:   units
	
	/** cache */
	UnitsAttribute _att_units = null;

	protected static AttributeFactory attributeFactory = AttributeFactory.attributeFactory;
    
    protected STMLElement() {
        super("element");
        init();
    }

    /**
     * main constructor.
     * 
     * @param name
     *            tagname
     */
    public STMLElement(String name) {
        super(name, STMLConstants.STML_NS);
        init();
    }
    
	public static STMLElement readAndCreateSTML(Element element) {
		STMLElement newElement = null;
		String tag = element.getLocalName();
		if (tag == null || tag.equals("")) {
			throw new RuntimeException("no tag");
		} else if (tag.equals(STMLArray.TAG)) {
			newElement = new STMLArray();
		} else if (tag.equals(STMLScalar.TAG)) {
			newElement = new STMLScalar();
		} else {
			LOG.error("unsupported cml element: "+tag);
		}
		if (newElement != null) {
	        XMLUtil.copyAttributesFromTo(element, newElement);
	        createSubclassedChildren(element, newElement);
		}
        return newElement;
	}
	
	/** converts a SVG file to SVGElement
	 * 
	 * @param file
	 * @return
	 */
	public static STMLElement readAndCreateSTML(File file) {
		Element element = XMLUtil.parseQuietlyToDocument(file).getRootElement();
		return (element == null) ? null : (STMLElement) readAndCreateSTML(element);
	}
	
	/** converts a SVG file to SVGElement
	 * 
	 * @param file
	 * @return
	 */
	public static STMLElement readAndCreateSTML(InputStream is) {
		Element element = XMLUtil.parseQuietlyToDocument(is).getRootElement();
		return (element == null) ? null : (STMLElement) readAndCreateSTML(element);
	}
	
	protected static void createSubclassedChildren(Element oldElement, STMLElement newElement) {
		if (oldElement != null) {
			for (int i = 0; i < oldElement.getChildCount(); i++) {
				Node node = oldElement.getChild(i);
				Node newNode = null;
				if (node instanceof Text) {
					String value = node.getValue();
					newNode = new Text(value);
				} else if (node instanceof Comment) {
					newNode = new Comment(node.getValue());
				} else if (node instanceof ProcessingInstruction) {
					newNode = new ProcessingInstruction((ProcessingInstruction) node);
				} else if (node instanceof Element) {
					newNode = readAndCreateSTML((Element) node);
				} else {
					throw new RuntimeException("Cannot create new node: "+node.getClass());
				}
				newElement.appendChild(newNode);
			}
		}
	}

    
    private void init() {
    }


    /**
     * copy constructor. copies attributes, children and properties using the
     * copyFoo() routines (q.v.)
     * 
     * @param element
     */
    public STMLElement(STMLElement element) {
        this(element.getLocalName());
        copyAttributesFrom(element);
        copyChildrenFrom(element);
    }

    /**
     * copy node.
     * 
     * @return node
     */
    public Node copy() {
        return new STMLElement(this);
    }

    /**
     * copies attributes. makes subclass if necessary.
     * 
     * @param element to copy from
     */
    public void copyAttributesFrom(Element element) {
        for (int i = 0; i < element.getAttributeCount(); i++) {
            Attribute att = element.getAttribute(i);
            Attribute newAtt = (Attribute) att.copy();
            this.addAttribute(newAtt);
        }
    }

    /** copies children of element make subclasses when required
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
    
    /** copies children of element make subclasses when required
     * 
     * @param element to copy from
     * @param to
     */
    public static void copyChildrenFromTo(Element element, Element to) {
        for (int i = 0; i < element.getChildCount(); i++) {
            Node childNode = element.getChild(i);
            Node newNode = childNode.copy();
            to.appendChild(newNode);
        }
    }
    
    /** override replaceChild.
     * @param oldNode
     * @param newNode
     */
    public void replaceChild(Node oldNode, Node newNode) {
        int pos = this.indexOf(oldNode);
        if (pos == -1) {
            throw new RuntimeException("Cannot replace non-child");
        }
        newNode.detach();
        this.removeChild(oldNode);
        this.insertChild(newNode, pos);
    }

    /** override insertChild.
     * if newNode has parent detach()es first
     * @param newNode
     * @param pos
     */
    public void insertChild(Node newNode, int pos) {
        newNode.detach();
        super.insertChild(newNode, pos);
    }

    /** re-route detach().
     * to parent.removeChild(this);
     */
    public void detach() {
        ParentNode parent = this.getParent();
        if (parent != null) {
            if (parent instanceof Document) {
                parent.replaceChild(this, new Element("dummy"));
            } else {
                parent.removeChild(this);
            }
        }
    }

    /** override setLocalName(localName) to make it immutable.
     * if localname is null sets it, else no-op
     * @param localName
     */
    public void setLocalName(String localName) {
        String lName = this.getLocalName();
        if (lName == null) {
            super.setLocalName(localName);
        }
    }

    // ========================== utilities ====================== //

    /**
     * throws Exception.
     * 
     * @param name
     *            of attribute
     * @throws STMLException
     *             standard message
     */
    protected void unknownAttributeName(String name) {
        throw new RuntimeException("Unknown STML attribute " + name + " on "
                + this.getLocalName());
    }

    protected String getSTMLAttributeValue(String name) {
        STMLAttribute a = (STMLAttribute) this.getAttribute(name);
        return (a == null) ? null : (String) a.getSTMLValue();
    }

    /**
     * remove attribute.
     * 
     * @param attName
     */
    public void removeAttribute(String attName) {
        Attribute att = this.getAttribute(attName);
        if (att != null) {
            this.removeAttribute(att);
        }
    }

    /**
     * copy attributes from one STMLElement to another. overwrites existing
     * atts
     * 
     * @param from
     *            element to copy from
     * @param to
     *            element to copy to
     */

    public static void copyAttributesFromTo(Element from, Element to) {
        for (int i = 0; i < from.getAttributeCount(); i++) {
            Attribute att = from.getAttribute(i);
            Attribute newAtt = (att instanceof STMLAttribute) ? new STMLAttribute(
                    (STMLAttribute) att)
                    : new Attribute(att);
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

    /** debug for element. makes copy if not document root writes to sysout
     * @param message
     */
    public void debug(String message) {
        Util.println("<<<<<<"+message+"<<<<<<");
        debug();
        Util.println(">>>>>>"+message+">>>>>>");
    }

    /** debug for element. makes copy if not document root writes to sysout
     */
    public void debug() {
        try {
            debug(System.out, 2);
        } catch (IOException e) {
            Util.BUG(e);
        }
    }

    /** debug for element. makes copy if not docuemnt root writes to sysout
     * @param indent
     */
    public void debug(int indent) {
        try {
            debug(System.out, indent);
        } catch (IOException e) {
            Util.BUG(e);
        }
    }

    /** debug.
     * 
     * @param os
     * @param indent
     * @throws IOException
     */
    public void debug(OutputStream os, int indent) throws IOException {
        Document document;
        Node parent = this.getParent();
        if (parent instanceof Document) {
            document = (Document) parent;
        } else {
            STMLElement copyElem = new STMLElement(this);
            document = new Document(copyElem);
        }
        Serializer serializer = new Serializer(os);
        serializer.setIndent(indent);
//        if (indent == 0) {
//            serializer.setLineSeparator("\r\n");
//        }
        serializer.write(document);
    }

    /**
     * gets String content. only valid when there is a single Text child. not
     * checked against the schema, so use with care
     * 
     * @return the XML text content or null
     */
    public String getStringContent() {
        Node child = (this.getChildCount() == 0) ? null : this.getChild(0);
        String s = (child == null || !(child instanceof Text)) ? null : child
                .getValue();
        return s;
    }

    /**
     * sets String content. very FRAGILE. not checked against the schema, so use with care. It
     * is almost always better to use the accessors generated from the schema
     * 
     * @param value
     *            the XML text content
     */
    public void setStringContent(String value) {
        Text newText = new Text(value);
        if (this.getChildCount() == 0) {
            this.appendChild(newText);
        } else {
            Node child = this.getChild(0);
            if (child instanceof Text) {
                this.replaceChild(child, newText);
            }
        }
    }

    /**
     * write as HTML. many elements will override this method.
     * 
     * @param w
     *            writer
     * @throws IOException
     */
    public void writeHTML(Writer w) throws IOException {
        w.write("<span class='" + this.getLocalName() + "'>");
        w.write(this.getLocalName());
        w.write("</span>");
    }

    /** convenience method to serialize the element.
     * 
     * @param os
     * @param indent to indent lines by (non-zero may muck up whitespace)
     * @throws IOException
     */
    public void serialize(OutputStream os, int indent) throws IOException {
        Document doc = new Document((STMLElement)this.copy());
        Serializer serializer = new Serializer(os);
        serializer.write(doc);
    }

    /** convenience method to add cmlx:foo attributes.
     * 
     * @param attName WITHOUT prefix
     * @param attValue if null removes any old attributes
     */
    public void setSTMLXAttribute(String attName, String attValue) {
    	if (attValue == null) {
    		Attribute attribute = this.getAttribute(attName, STMLConstants.STMLX_NS);
    		if (attribute != null) {
    			this.removeAttribute(attribute);
    		}
    	} else {
    		addSTMLXAttribute(this, attName, attValue);
    	}
    }

    /**
     * creates a prefixed STMLX attribute (cmlx:foo="bar") on element in STMLX namespace
     * @param element
     * @param attName UNPREFIXED
     * @param attValue
     */
	public static void addSTMLXAttribute(Element element, String attName, String attValue) {
		Attribute attribute = makeSTMLXAttribute(attName, attValue);
		element.addAttribute(attribute);
		element.addNamespaceDeclaration(STMLConstants.CMLX_PREFIX, STMLConstants.STMLX_NS);
	}
    
    /** convenience method to create new cmlx:foo attribute.
     * 
     * @param attName WITHOUT prefix and colon
     * @param value if null undefined
     * @return
     */
	public static Attribute makeSTMLXAttribute(String attName, String value) {
		return new Attribute(STMLConstants.CMLX_PREFIX+S_COLON+attName, STMLConstants.STMLX_NS, value);
	}

	   /** convenience method to get value of cmlx:foo attribute.
     * 
     * @param attName WITHOUT prefix
     */
    public String getSTMLXAttribute(String attName) {
    	String value = null;
    	Attribute attribute = this.getAttribute(attName, STMLConstants.STMLX_NS);
    	if (attribute != null) {
    		value = attribute.getValue();
     	}
    	return value;
    }


    /**
     * <p>
     * Appends a node to the children of this node.
     * </p>
     * 
     * @param child node to append to this node
     * 
     * @throws IllegalAddException if this node cannot have children 
     *     of this type
     * @throws NullPointerException if <code>child</code> is null
     * 
     */
    public void appendChild(Node child) {
        child.detach();
        int childCount = this.getChildCount();
        insertChild(child, childCount);
    }

	/**
	 * @return the tool
	 */
	public AbstractSTMTool getTool() {
		return tool;
	}

	/**
	 * @param tool the tool to set
	 */
	public void setTool(AbstractSTMTool tool) {
		this.tool = tool;
	}

	public void setDataType(String type) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * get namespace.
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

	/** The data type of the object.
	* Normally applied to scalar/array 
	*                 objects but may extend to more complex one.
	* @return String
	*/
	public String getDataType() {
	    StringSTAttribute att = (StringSTAttribute) this.getDataTypeAttribute();
	    if (att == null) {
	        return null;
	    }
	    return att.getString();
	}

	/** The data type of the object.
	* Normally applied to scalar/array 
	*                 objects but may extend to more complex one.
	* @return STMLAttribute
	*/
	public STMLAttribute getDataTypeAttribute() {
	    return (STMLAttribute) getAttribute("dataType");
	}

	/** null
	* @return String
	*/
	public String getDelimiter() {
	    DelimiterAttribute att = (DelimiterAttribute) this.getDelimiterAttribute();
	    if (att == null) {
	        return null;
	    }
	    return att.getString();
	}

	/** null
	* @return STMLAttribute
	*/
	public STMLAttribute getDelimiterAttribute() {
	    return (STMLAttribute) getAttribute("delimiter");
	}

	/** null
	* @return String
	*/
	public String getDictRef() {
	    DictRefAttribute att = (DictRefAttribute) this.getDictRefAttribute();
	    if (att == null) {
	        return null;
	    }
	    return att.getString();
	}
	
    /** null
    * @param value title value
    * @throws RuntimeException attribute wrong value/type
    */
    public void setDictRef(String value) throws RuntimeException {
        DictRefAttribute att = null;
        _att_dictref = (DictRefAttribute) attributeFactory.getAttribute("dictRef", "scalar");
        att = new DictRefAttribute(_att_dictref);
        this.addRemove(att, value);
    }

	/** null
	* @return STMLAttribute
	*/
	public STMLAttribute getDictRefAttribute() {
	    return (STMLAttribute) getAttribute("dictRef");
	}

    protected void addRemove(STMLAttribute att, String value) {
    	if (value == null || value.equals(S_EMPTY)) {
    		this.removeAttribute(att.getLocalName());
    	} else if (att == null) {
    	} else {
    		att.setSTMLValue(value);
    		super.addAttribute(att);
    	}
    }


	/** Additive constant to generate SI equivalent.
	* The amount to add to a quantity in non-SI units to convert its representation to SI Units. This is applied *after* multiplierToSI. It is necessarily zero for SI units.
	* @return double
	*/
	public double getConstantToSI() {
	    DoubleSTAttribute att = (DoubleSTAttribute) this.getConstantToSIAttribute();
	    if (att == null) {
	        return Double.NaN;
	    }
	    return att.getDouble();
	}

	/** Additive constant to generate SI equivalent.
	* The amount to add to a quantity in non-SI units to convert its representation to SI Units. This is applied *after* multiplierToSI. It is necessarily zero for SI units.
	* @return STMLAttribute
	*/
	public STMLAttribute getConstantToSIAttribute() {
	    return (STMLAttribute) getAttribute("constantToSI");
	}

	/** A reference to a convention.
	* There is no controlled vocabulary for conventions, but the author must ensure that the semantics are openly available and that there are mechanisms for implementation. The convention is inherited by all the subelements, 
	* so that a convention for molecule would by default extend to its bond and atom children. This can be overwritten
	*     if necessary by an explicit convention.
	*                     It may be useful to create conventions with namespaces (e.g. iupac:name).
	*     Use of convention will normally require non-STMML semantics, and should be used with
	*     caution. We would expect that conventions prefixed with "ISO" would be useful,
	*     such as ISO8601 for dateTimes.
	*                     There is no default, but the conventions of STMML or the related language (e.g. STML) will be assumed.
	* @return String
	*/
	public String getConvention() {
	    StringSTAttribute att = (StringSTAttribute) this.getConventionAttribute();
	    if (att == null) {
	        return null;
	    }
	    return att.getString();
	}

	/** A reference to a convention.
	* There is no controlled vocabulary for conventions, but the author must ensure that the semantics are openly available and that there are mechanisms for implementation. The convention is inherited by all the subelements, 
	* so that a convention for molecule would by default extend to its bond and atom children. This can be overwritten
	*     if necessary by an explicit convention.
	*                     It may be useful to create conventions with namespaces (e.g. iupac:name).
	*     Use of convention will normally require non-STMML semantics, and should be used with
	*     caution. We would expect that conventions prefixed with "ISO" would be useful,
	*     such as ISO8601 for dateTimes.
	*                     There is no default, but the conventions of STMML or the related language (e.g. STML) will be assumed.
	* @return STMLAttribute
	*/
	public STMLAttribute getConventionAttribute() {
	    return (STMLAttribute) getAttribute("convention");
	}

	/** null
	* @return String
	*/
	public String getId() {
	    IdAttribute att = (IdAttribute) this.getIdAttribute();
	    if (att == null) {
	        return null;
	    }
	    return att.getString();
	}

	/** null
	* @return STMLAttribute
	*/
	public STMLAttribute getIdAttribute() {
	    return (STMLAttribute) getAttribute("id");
	}

	/** null
	* @return String
	*/
	public String getUnits() {
	    UnitsAttribute att = (UnitsAttribute) this.getUnitsAttribute();
	    if (att == null) {
	        return null;
	    }
	    return att.getString();
	}

	/** null
	* @return STMLAttribute
	*/
	public STMLAttribute getUnitsAttribute() {
	    return (STMLAttribute) getAttribute("units");
	}

	/** The size of an array or matrix.
	* No description
	* @return int
	*/
	public int getSize() {
	    IntSTAttribute att = (IntSTAttribute) this.getSizeAttribute();
	    if (att == null) {
	        throw new RuntimeException("int attribute is unset: size");
	    }
	    return att.getInt();
	}

	/** The size of an array or matrix.
	* No description
	* @return STMLAttribute
	*/
	public STMLAttribute getSizeAttribute() {
	    return (STMLAttribute) getAttribute("size");
	}

	/** null
	* @param value title value
	* @throws RuntimeException attribute wrong value/type
	*/
	public void setDelimiter(String value) throws RuntimeException {
	    DelimiterAttribute att = null;
	    if (_att_delimiter == null) {
	        _att_delimiter = (DelimiterAttribute) attributeFactory.getAttribute("delimiter", "array");
	        if (_att_delimiter == null) {
	            throw new RuntimeException("BUG: cannot process attributeGroupName : delimiter probably incompatible attributeGroupName and attributeName");
	        }
	    }
	    att = new DelimiterAttribute(_att_delimiter);
	    this.addRemove(att, value);
	}

	/** The size of an array or matrix.
	* No description
	* @param value title value
	* @throws RuntimeException attribute wrong value/type
	*/
	public void setSize(int value) throws RuntimeException {
	    if (_att_size == null) {
	        _att_size = (IntSTAttribute) attributeFactory.getAttribute("size", "array");
	       if (_att_size == null) {
	           throw new RuntimeException("BUG: cannot process attributeGroupName : size probably incompatible attributeGroupName and attributeName ");
	        }
	    }
	    IntSTAttribute att = new IntSTAttribute(_att_size);
	    super.addAttribute(att);
	    att.setSTMLValue(value);
	}

	/** The size of an array or matrix.
	* No description
	* @param value title value
	* @throws RuntimeException attribute wrong value/type
	*/
	public void setSize(String value) throws RuntimeException {
	    IntSTAttribute att = null;
	    if (_att_size == null) {
	        _att_size = (IntSTAttribute) attributeFactory.getAttribute("size", "array");
	        if (_att_size == null) {
	            throw new RuntimeException("BUG: cannot process attributeGroupName : size probably incompatible attributeGroupName and attributeName");
	        }
	    }
	    att = new IntSTAttribute(_att_size);
	    this.addRemove(att, value);
	}

    /** 
    * 
    * @param value title value
    * @throws RuntimeException attribute wrong value/type
    */
    public void setXMLContent(String value) throws RuntimeException {
        this.removeChildren();
        this.appendChild(value);
    }

    /** 
    * 
    * @return String
    */
    public String getXMLContent() {
        String content = this.getValue();
        return content;
    }

	protected void removeAttributeWithName(String name) {
	}
}

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

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLConstants;

import nu.xom.Attribute;
import nu.xom.NamespaceConflictException;
import nu.xom.Node;

/**
 * generic subclassed Attribute for CML elements. often further subclassed into
 * strongly typed attributes
 * 
 * in CMLSchema an attributeGroup normally wraps the attribute. Normally
 * the names are the same, but sometimes the attribute group has a different
 * name from the attribute. In some cases two different attributes can have the same
 * name  but different attribute groups. The attributeGroup name is only used for 
 * collecting the attributes
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public class STMLAttribute extends Attribute implements XMLConstants {

    final static Logger logger = Logger.getLogger(STMLAttribute.class);

	public static final String CONSTANT_TO_SI = "constantToSI";
    public final static String CONVENTION = "convention";
	public static final String DICTREF = "dictRef";
	public static final String ID = "id";
	public static final String MULTIPLIER_TO_SI = "multiplierToSI";
	public static final String TITLE = "title";
	public static final String UNITS = "units";


    
    protected STMLType cmlType;
    protected String summary;
    protected String description;
    protected String attributeGroupName; // used in code generation

    /**
     * creates attribute without value. do not use directly
     * 
     * @param name
     */
    public STMLAttribute(String name) {
        super(name, "");
    }

    /**
     * creates attribute.
     * 
     * @param name
     * @param value
     */
    public STMLAttribute(String name, String value) {
        super(name, value);
    }

    /**
     * creates attribute.
     * 
     * @param name
     *            must be qualified (colonized)
     * @param URI
     *            namespace
     * @param value
     * @throws NamespaceConflictException
     *             probably no prefix in name
     */
    protected STMLAttribute(String name, String URI, String value)
            throws nu.xom.NamespaceConflictException {
        super(name, URI, value);
    }

    /**
     * copy constructor
     * 
     * @param att
     */
    public STMLAttribute(STMLAttribute att) {
        super(att);
        this.cmlType = att.cmlType;
        // if (att.getLocalName().equals("dictRef")) {
        // new Exception().printStackTrace();
        // }
    }

    /**
     * semi copy constructor
     * 
     * @param att
     */
    public STMLAttribute(Attribute att) {
        super(att);
    }

    /**
     * copy constructor from empty attribute.
     * used to create subclasses
     * @param att to copy
     * @param value to add (may throw CMLRuntime)
     */
    protected STMLAttribute(Attribute att, String value) {
        this(att.getLocalName());
        this.setSTMLValue(value);
    }

    /**
     * makes copy of correct class.
     * shallow copy as most fields are not mutable
     * @return copy of node
     */
    public Node copy() {
    	STMLAttribute newAttribute = new STMLAttribute(this);
        newAttribute.setValue(this.getValue());
        return newAttribute;
    }
    
    /**
     * sets attributeGroup name. normally only useful when generating code when
     * the attributeGroup name may be different from the attribute name. it is
     * required for lookup
     * 
     * @param agn attributeGroup name
     */
    public void setAttributeGroupName(String agn) {
        attributeGroupName = agn;
    }

    /**
     * gets attributeGroup name. normally only useful when generating code when
     * the attributeGroup name may be different from the attribute name. it is
     * required for lookup
     * 
     * @return attributeGroup name
     */
    public String getAttributeGroupName() {
        return attributeGroupName;
    }

    /**
     * compares attributes. As we cannot override Node.equals() which compares
     * identity we have to compare components. order of sorting is: attribute
     * class cmlType name name value
     * 
     * null values of any component return -1
     * 
     * @param att
     *            to compare
     * @return 0 if all content is identical, -1 if this less than att, 1 if
     *         greater value
     * 
     */
    public int compareTo(Attribute att) {
        if (att == null) {
            return -1;
        }
        // same attribute?
        if (this == att) {
            return 0;
        }
        int order = 0;
        if (!(att instanceof STMLAttribute)) {
            order = -1;
        }
        STMLAttribute cmlAtt = (STMLAttribute) att;
        // schemas must either bosth be null or equal
        if (order == -1) {
        } else if (cmlType == null && cmlAtt.cmlType == null) {
        } else if (cmlType != null && cmlAtt.cmlType != null) {
            order = this.cmlType.compareTo(cmlAtt.cmlType);
        } else {
            order = -1;
        }
        if (order == 0) {
            order = this.getClass().getName().compareTo(
                    att.getClass().getName());
        }
        if (order == 0) {
            order = this.getLocalName().compareTo(cmlAtt.getLocalName());
        }
        if (order == 0) {
            order = this.getValue().compareTo(cmlAtt.getValue());
        }
        return (order == 0) ? 0 : order / Math.abs(order);
    }

    /**
     * get JavaType default
     * 
     * @return "String"
     */
    public String getJavaType() {
        return "String";
    }

    /**
     * get Java set method default
     * 
     * @return "setSTMLValue"
     */
    public String getJavaSetMethod() {
        return "setSTMLValue";
    }

    /**
     * get Java get method.
     * 
     * @return "getSTMLValue"
     */
    public String getJavaGetMethod() {
        return "getSTMLValue";
    }

    /**
     * get Java ShortClassName.
     * 
     * @return "STMLAttribute"
     */
    public String getJavaShortClassName() {
    	return this.getClass().getSimpleName();
    }

    /**
     * get schema type.
     * 
     * @return "STMLAttribute"
     */
    public STMLType getSchemaType() {
        return cmlType;
    }

    /**
     * set schema type.
     * 
     * @param schemaType could be null
     */
    public void setSchemaType(STMLType schemaType) {
        this.cmlType = schemaType;
    }

    /**
     * returns value as a typed object. if object is a primitive, return in
     * wrapper (e.g. Integer) object might be an array of primitives (e.g.
     * int[]) types are: String, String[], Integer, int[], Double, double[]
     * 
     * @return the value
     */
    public Object getSTMLValue() {
        return getValue();
    }

    /**
     * sets value. often subclassed which will throw exception if of wrong type
     * 
     * @param s
     *            the value
     */
    public void setSTMLValue(String s) {
        this.setValue(s);
    }

    /**
     * get documentation summary.
     * 
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * set documentation summary.
     * 
     * @param s
     *            the summary
     */
    public void setSummary(String s) {
        if (s != null) {
            summary = s;
            if (!summary.endsWith(S_PERIOD)) {
                summary += STMLConstants.S_PERIOD;
            }
        }
    }

    /**
     * get Documentation.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * set Documentation.
     * 
     * @param d
     *            the description
     */
    public void setDescription(String d) {
        description = d;
    }

	/**
	 * @return the cmlType
	 */
	public STMLType getCmlType() {
		return cmlType;
	}

	/**
	 * @param cmlType the cmlType to set
	 */
	public void setCmlType(STMLType cmlType) {
		this.cmlType = cmlType;
	}

	protected void removeAttributeWithName(String name) {
	}

}

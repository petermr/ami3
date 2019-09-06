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

package org.contentmine.eucl.stml.attribute;

import org.contentmine.eucl.stml.STMLConstants;
import org.contentmine.eucl.stml.STMLElement;
import org.contentmine.eucl.stml.interfacex.HasUnits;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * abstract class supporting attributes with namespaceRef values.
 */
public /*abstract*/ class NamespaceRefAttribute extends StringSTAttribute {

    /** regex for validating prefixes */
    public final static String PREFIX_REGEX = "[A-Za-z][A-Za-z0-9\\.\\-\\_]*";

    /**
     * constructor.
     * 
     * @param name
     */
    public NamespaceRefAttribute(String name) {
        super(name);
        init(name);
    }

    public NamespaceRefAttribute(String name, String value) {
        this(name);
        this.setSTMLValue(value);
    }

    public NamespaceRefAttribute(Attribute att) {
        this(att.getLocalName());
        this.setSTMLValue(att.getValue());
    }

    void init(String name) {
//        SpecialAttribute.updateSTMLAttributeList(name, name, this);
    }

    /**
     * interlude to check for QName value.
     * 
     * @param value
     */
    public void setSTMLValue(String value) {
        super.setSTMLValue(value);
        if (this.getValue().equals(S_EMPTY)) {
            // empty string is created in new attribute
        } else if (this.getPrefix() == null) {
            throw new RuntimeException("attribute value [" + this.getValue()
                    + "] for " + this.getLocalName() + " must be QName");
        }
    }

    /**
     * get parent element.
     * 
     * @return parent
     */
    public Element getElement() {
        return (Element) this.getParent();
    }

    /**
     * gets namespace prefix.
     * 
     * @return null if attribute has no value or no prefix
     */
    public String getPrefix() {
        return getPrefix(this.getValue());
    }

    /** gets prefix from a valid namespaceRef string.
     * 
     * @param value to examine
     * @return prefix (null if absent)
     */
    public static String getPrefix(String value) {
        String prefix = null;
        if (value != null) {
            int idx = value.indexOf(S_COLON);
            if (idx != -1) {
                prefix = value.substring(0, idx);
            }
        }
        return prefix;
    }
    
    

    /**
     * get namespaceURI for this attribute;
     * 
     * @return the namespace
     */
    public String getNamespaceURIString() {
        Element element = this.getElement();
        String prefix = this.getPrefix();
        String namespaceURI = (prefix == null) ? null : element
                .getNamespaceURI(prefix);
        return namespaceURI;
    }

    /**
     * gets idRef. portion of value following the colon
     * 
     * @return null if attribute has no value or no prefix
     */
    public String getIdRef() {
        return getLocalName(this.getValue());
    }
    
    /**
     * sets idRef. portion of value following the colon
     * 
     */
    public void setIdRef(String idRef) {
    	String value = NamespaceRefAttribute.createValue(this.getPrefix(), idRef);
    	this.setSTMLValue(value);
    }
    
    static int count = 0;


    /** create valid prefixed value.
     * 
     * @param prefix
     * @param value
     * @return prefixed value
     */
    public static String createValue(String prefix, String value) {
        if (prefix == null) {
            throw new RuntimeException("null prefix");
        }
        if (value == null) {
            throw new RuntimeException("null value");
        }
        if (prefix.trim().equals("")) {
            throw new RuntimeException("cannot have empty prefix");
        }
        if (value.trim().equals("")) {
            throw new RuntimeException("cannot have empty value");
        }
        if (!prefix.matches(PREFIX_REGEX)) {
            throw new RuntimeException("Prefix [" + prefix + "] incompatible with "
                    + PREFIX_REGEX);
        }
        return prefix + STMLConstants.S_COLON + value;
    }

    /** return the local name after the colon.
     * if prefix is missing return whole string
     * @param name to examine
     * @return localName 
     */
    public static String getLocalName(String name) {
        String localName = null;
        if (name != null) {
            int idx = name.indexOf(S_COLON);
            localName = name.substring(idx+1);
        }
        return localName;
    }

    /** set units on STML Element.
     * 
     * @param hasUnits to set units on
     * @param prefix unit dictionary prefix
     * @param id unit id
     * @param namespaceURI unit dictionary namespace
     */
    public static void setUnits(HasUnits hasUnits, String prefix, String id, String namespaceURI) {
        STMLElement element = (STMLElement) hasUnits;
        String currentNamespace = element.getNamespaceURIForPrefix(prefix);
        if (currentNamespace != null) {
            if (!currentNamespace.equals(namespaceURI)) {
                throw new RuntimeException("Cannot reset units namespace for "+prefix+" from " +
                    ""+currentNamespace+" to "+namespaceURI);
            }
        } else {
            element.addNamespaceDeclaration(prefix, namespaceURI);
        }
        Attribute units = element.getAttribute("units");
        if (units != null) {
            element.removeAttribute(units);
        }
        UnitsAttribute unitAttribute = new UnitsAttribute(
                NamespaceRefAttribute.createValue(prefix, id));
        element.addAttribute(unitAttribute);
    }
}

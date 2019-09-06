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

import org.contentmine.eucl.stml.STMLAttribute;
import org.contentmine.eucl.stml.STMLConstants;

import nu.xom.Attribute;
import nu.xom.Node;

/**
 * attribute representing a string value.
 * 
 */

public class StringSTAttribute extends STMLAttribute {

    /** */
    public final static String JAVA_TYPE = JAVA_STRING;
    /** */
    public final static String JAVA_GET_METHOD = "getString";
    /** */
    public final static String JAVA_SHORT_CLASS = "StringSTAttribute";
    
    /**
     * constructor.
     * 
     * @param name
     */
    public StringSTAttribute(String name) {
        super(name);
    }

    /**
     * from DOM.
     * 
     * @param att
     */
    public StringSTAttribute(Attribute att) {
        this(att.getLocalName());
        this.setSTMLValue(att.getValue());
    }

    /**
     * copy constructor
     * 
     * @param att
     */
    public StringSTAttribute(StringSTAttribute att) {
        super(att);
        if (att.getValue() != null) {
            this.setValue(att.getValue());
        }
    }

    /** copy.
     * uses copy constructor.
     * @return copy
     */
    public Node copy() {
    	return new StringSTAttribute(this);
    }

    /**
     * from DOM.
     * 
     * @param att
     *            to copy, except value
     * @param value
     */
    public StringSTAttribute(Attribute att, String value) {
        super(att, value.trim().replace(S_WHITEREGEX, STMLConstants.S_SPACE));
    }

    /**
     * set and check value.
     * trims by default
     * use setSTMLValue(s, trim)
     * @param s
     */
    public void setSTMLValue(String s) {
    	this.setSTMLValue(s, true);
 	}

    /**
     * set and check value.
     * 
     * @param string
     */
    public void setSTMLValue(String string, boolean trim) {
    	if (string == null) {
    		throw new RuntimeException("Cannot set null attribute value");
    	}
    	if (trim) {
    		string = string.trim();
    	}
		checkValue(string);
		//this.s = string;
		this.setValue(string);
	}


    /**
	 * checks value of simpleType. uses STMLType.checkvalue() fails if type is
	 * int or double or is a list
	 * 
	 * @param s
	 *            the value
	 * @throws RuntimeException
	 *             wrong type or value fails
	 */
    public void checkValue(String s) {
        if (cmlType != null) {
            cmlType.checkValue(s);
        }
    }

    /**
     * get value.
     * 
     * @return value
     */
    public String getString() {
        return this.getValue();
    }

    /**
     * get java type.
     * 
     * @return java type
     */
    public String getJavaType() {
        return JAVA_TYPE;
    }

    /**
     * get java method.
     * 
     * @return java method
     */
    public String getJavaGetMethod() {
        return JAVA_GET_METHOD;
    }

    /**
     * get java short class name.
     * 
     * @return java short className
     */
    public String getJavaShortClassName() {
        return JAVA_SHORT_CLASS;
    }
};

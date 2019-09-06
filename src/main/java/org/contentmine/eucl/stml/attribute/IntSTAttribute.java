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
 * attribute representing an int value.
 */

public class IntSTAttribute extends STMLAttribute {

    /** */
    public final static String JAVA_TYPE = JAVA_INT;
    /** */
    public final static String JAVA_GET_METHOD = "getInt";
    /** */
    public final static String JAVA_SHORT_CLASS = "IntSTAttribute";

    protected Integer i;

    /**
     * constructor.
     * 
     * @param name
     */
    public IntSTAttribute(String name) {
        super(name);
    }

    /**
     * from DOM.
     * 
     * @param att
     */
    public IntSTAttribute(Attribute att) {
        this(att.getLocalName());
        String v = att.getValue();
        if (v != null && !v.trim().equals(S_EMPTY)) {
            this.setSTMLValue(v);
        }
    }

    /**
     * copy constructor
     * 
     * @param att
     */
    public IntSTAttribute(IntSTAttribute att) {
        super(att);
        if (att.i != null) {
            this.i = new Integer(att.i.intValue());
        }
    }
    /** copy.
     * uses copy constructor.
     * @return copy 
     */
    public Node copy() {
    	return new IntSTAttribute(this);
    }


    /**
     * from DOM.
     * 
     * @param att
     *            to copy, except value
     * @param value
     */
    public IntSTAttribute(Attribute att, String value) {
        super(att, value.trim().replace(S_WHITEREGEX, STMLConstants.S_SPACE));
    }

    /**
     * sets value. throws exception if of wrong type or violates restriction
     * 
     * @param s
     *            the value
     * @throws RuntimeException
     */
    public void setSTMLValue(String s) {
    	if (s!= null && !s.trim().equals(S_EMPTY)) {
	        int i;
	        try {
	            i = Integer.parseInt(s.trim());
	        } catch (NumberFormatException nfe) {
	            throw new RuntimeException(S_EMPTY + nfe);
	        }
	        this.setSTMLValue(i);
    	}
    }

    /**
     * set and check value.
     * 
     * @param i
     */
    public void setSTMLValue(int i) {
        checkValue(i);
        this.i = new Integer(i);
        this.setValue(S_EMPTY + i);
    }

    /**
     * checks value of simpleType. if value does not check
     * against SimpleType uses STMLType.checkvalue() fails if type is String or
     * double or is a list
     * 
     * @param i
     *            the value
     * @throws STMLException
     *             wrong type or value fails
     */
    public void checkValue(int i) {
        if (cmlType != null) {
            cmlType.checkValue(i);
        }
    }

    /**
     * returns value as Integer.
     * 
     * @return value
     */
    public Object getSTMLValue() {
        return i;
    }

    /**
     * returns value as int.
     * 
     * @return int
     */
    public int getInt() {
        if (i == null) {
            throw new RuntimeException("integer attribute unset");
        }
        return i.intValue();
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

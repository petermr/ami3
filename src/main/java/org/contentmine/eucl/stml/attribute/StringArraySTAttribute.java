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

import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.stml.STMLAttribute;
import org.contentmine.eucl.stml.STMLConstants;

import nu.xom.Attribute;
import nu.xom.Node;

/**
 * attribute representing an array of Strings.
 */

public class StringArraySTAttribute extends STMLAttribute {

    /** */
    public final static String JAVA_TYPE = JAVA_STRING+JAVA_ARRAY;
    /** */
    public final static String JAVA_GET_METHOD = "getStringArray";
    /** */
    public final static String JAVA_SHORT_CLASS = "StringArraySTAttribute";

    protected String[] ss = null;

    /**
     * constructor.
     * 
     * @param name
     */
    public StringArraySTAttribute(String name) {
        super(name);
    }

    /**
     * from DOM.
     * 
     * @param att
     */
    public StringArraySTAttribute(Attribute att) {
        this(att.getLocalName());
        this.setSTMLValue(att.getValue());
    }

    /**
     * Sets the XOM value, and the STMLValue array.
     */
    @Override
    public void setValue(String s){
    	super.setValue(s);
    	this.setSTMLValue(s);
    }
    
    /**
     * copy constructor
     * 
     * @param att
     */
    public StringArraySTAttribute(StringArraySTAttribute att) {
        super(att);
        if (att.ss != null) {
            this.ss = new String[att.ss.length];
            for (int i = 0; i < ss.length; i++) {
                this.ss[i] = att.ss[i];
            }
        }
    }
    /** copy.
     * uses copy constructor.
     * @return copy 
     */
    public Node copy() {
    	return new StringArraySTAttribute(this);
    }


    /**
     * from DOM.
     * 
     * @param att
     *            to copy, except value
     * @param value
     */
    public StringArraySTAttribute(Attribute att, String value) {
        super(att);
        this.setSTMLValue(value);
    }

    /**
     * sets value. throws exception if of wrong type or violates restriction
     * 
     * @param s
     *            the value
     */
    public void setSTMLValue(String s) {
        this.setSTMLValue(arrayFromString(s));
    }
    
    protected String[] arrayFromString(String s){
    	String[] split = s.trim().split(S_WHITEREGEX);
    	return split;
    }

    protected String stringFromArray(String[] array){
    	return Util.concatenate(array, STMLConstants.S_SPACE);
    }
    
    /**
     * set and check value.
     * 
     * @param ss
     */
    public void setSTMLValue(String[] ss) {
        checkValue(ss);
        this.ss = ss;
        super.setValue(stringFromArray(ss));
    }

    /**
     * checks value of simpleType. if value does not check
     * against SimpleType uses STMLType.checkvalue() fails if type is int or
     * double or is not a list
     * 
     * @param ss
     *            the String array
     * @throws RuntimeException
     *             wrong type or value fails
     */
    public void checkValue(String[] ss) {
        if (cmlType != null) {
            cmlType.checkValue(ss);
        }
    }

    /**
     * get array.
     * 
     * @return null if not set
     */
    public Object getSTMLValue() {
        return ss;
    }

    /**
     * get array.
     * 
     * @return null if not set
     */
    public String[] getStringArray() {
        return ss;
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

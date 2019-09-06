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

import org.contentmine.eucl.euclid.EuclidConstants;

import nu.xom.XPathContext;

/**
 * 
 * <p>
 * Constants
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public interface XMLConstants extends EuclidConstants {

	/** suffix for files */
	String XML_SUFF = ".xml";
	/** suffix for files */
	String XSD_SUFF = ".xsd";
	
    /** xmlns attribute name */
    String XMLNS = "xmlns";

    /**
     * XSD namespace. no trailing slash
     */
    String XSD_NS = "http://www.w3.org/2001/XMLSchema";

    /** XSD prefix = 'xsd' */
    String XSD_PREFIX = "xsd";

    /**
     * namespace declaration for XSD
     * xmlns:xsd='http://www.w3.org/2001/XMLSchema'
     */
    String XSD_XMLNS = XMLNS + S_COLON + XSD_PREFIX + S_EQUALS + S_APOS
            + XSD_NS + S_APOS;

    /** constant */
    String XSD_ANYURI = "xsd:anyURI";

    /** constant */
    String XSD_BOOLEAN = "xsd:boolean";

    /** constant */
    String XSD_DATE = "xsd:date";

    /** constant */
    String XSD_DOUBLE = "xsd:double";

    /** constant */
    String XSD_FLOAT = "xsd:float";

    /** constant */
    String XSD_INTEGER = "xsd:integer";

    /** constant */
    String XSD_MAXEXCLUSIVE = "xsd:maxExclusive";

    /** constant */
    String XSD_MAXINCLUSIVE = "xsd:maxInclusive";

    /** constant */
    String XSD_MINEXCLUSIVE = "xsd:minExclusive";

    /** constant */
    String XSD_MININCLUSIVE = "xsd:minInclusive";

    /** constant */
    String XSD_NONNEGATIVEINTEGER = "xsd:nonNegativeInteger";

    /** constant */
    String XSD_POSITIVEINTEGER = "xsd:positiveInteger";

    /** constant */
    String XSD_POSITIVE_NUMBER = "xsd:positiveNumber";

    /** constant */
    String XSD_STRING = "xsd:string";

    /** constant */
    String XSD_QNAME = "xsd:QName";
    
    /** element types */
    
    /** */
    String XSD_ANNOTATION = "xsd:annotation";
    /** */
    String XSD_ATTRIBUTE = "xsd:attribute";
    /** */
    String XSD_ATTRIBUTE_GROUP = "xsd:attributeGroup";
    /** */
    String XSD_COMPLEX_TYPE = "xsd:complexType";
    /** */
    String XSD_DOCUMENTATION = "xsd:documentation";
    /** */
    String XSD_ELEMENT = "xsd:element";
    /** */
    String XSD_ENUMERATION = "xsd:enumeration";
    /** */
    String XSD_EXTENSION = "xsd:extension";
    /** */
    String XSD_LENGTH = "xsd:length";
    /** */
    String XSD_LIST = "xsd:list";
    /** */
    String XSD_PATTERN = "xsd:pattern";
    /** */
    String XSD_RESTRICTION = "xsd:restriction";
    /** */
	String XSD_SIMPLE_TYPE = "xsd:simpleType";
    /** */
	String XSD_SIMPLE_CONTENT = "xsd:simpleContent";
    /** */
	String XSD_UNION = "xsd:union";
    /** dewisott */
	String FPX_REAL = "fpx:real";
    /** */
	String PATTERN_ANYURI = "http://.*";    //crude!
    /** */
	String PATTERN_QNAME = "[A-Za-z_][A-Za-z0-9_-\\.]*:[A-Za-z_][A-Za-z0-9_-\\.]*";
    /** */
    XPathContext XPATH_XSD = new XPathContext("xsd", XSD_NS);
    

    /** constant */
    String XHTML_NS = "http://www.w3.org/1999/xhtml";
    
    /** namespace for SVG.
     * 
     */
    /** root of all SVG URIs */
    String SVG_NS_BASE = "http://www.w3.org/2000/svg";

    String SVG_NS = SVG_NS_BASE;
    
    /** XPathContext for SVG.
     */
    XPathContext SVG_XPATH = new XPathContext("svg", SVG_NS);
    
    /** namespaces for XLINK
     * 
     */
    String XLINK_PREFIX = "xlink";
    String XLINK_NS = "http://www.w3.org/1999/xlink";
    
    /** XPath 'OR' concatenator*/
    String X_OR = S_PIPE;

    // subdirs of components
    /** constant */
    String TYPES = "types";

    /** constant */
    String ATTRIBUTES = "attributeGroups";

    /** constant */
    String ELEMENTS = "elements";

    /** java classnames */
    String JAVA_BOOLEAN = "Boolean";
    /** java classnames */
    String JAVA_DOUBLE = "Double";
    /** constant */
    String JAVA_INTEGER = "Integer";
    /** String class */
    String JAVA_STRING = "String";
    /** primitive */
    String JAVA_BOOL = "boolean";
    /** primitive */
    String JAVA_INT = "int";
    /** primitive */
    String JAVA_DOUB = "double";
    /** array */
    String JAVA_ARRAY = "[]";


// -----------------------------------------------    
    // format
    /** constant */
    String BANNER_S = "**************************************";

    /** constant */
    String WARNING_S = "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!";

    /** constant */
    String AUTOGENERATED_DONOTEDIT_S = 
        "/*======AUTOGENERATED FROM SCHEMA; DO NOT EDIT BELOW THIS LINE ======*/";

    /** catalog.*/
    String CATALOG_XML = "catalog.xml";
    
    /**
     * units prefix reserved: for several uses
     */
    String CML_UNITS = "units";

    /**
     * siUnits prefix reserved: for several uses
     */
    String CML_SIUNITS = "siUnits";

    // These are IDs, and must match those on the test dictionaries
    /** angstrom. */
    String U_ANGSTROM = CML_UNITS + S_COLON + "ang";

    /** degree. */
    String U_DEGREE = CML_UNITS + S_COLON + "deg";

    /** degree. */
    String U_KCAL = CML_UNITS + S_COLON + "kcal";

    /** celsius. */
    String U_CELSIUS = CML_UNITS + S_COLON + "celsius";


}
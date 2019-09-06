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

    package org.contentmine.graphics.math;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;



/*
 * from W3C spec https://www.w3.org/TR/MathML3/chapter4.html
 * 
        4.2.1 Numbers <cn>
        4.2.2 Content Identifiers <ci>
        4.2.3 Content Symbols <csymbol>
        4.2.4 String Literals <cs>
        4.2.5 Function Application <apply>
        4.2.6 Bindings and Bound Variables <bind> and <bvar>
        4.2.7 Structure Sharing <share>
        4.2.9 Error Markup <cerror>
        4.2.10 Encoded Bytes <cbytes>
 
  4.4.1 Functions and Inverses
            4.4.1.1 Interval <interval>
            4.4.1.2 Inverse <inverse>
            4.4.1.3 Lambda <lambda>
            4.4.1.4 Function composition <compose/>
            4.4.1.5 Identity function <ident/>
            4.4.1.6 Domain <domain/>
            4.4.1.7 codomain <codomain/>
            4.4.1.8 Image <image/>
            4.4.1.9 Piecewise declaration <piecewise>, <piece>, <otherwise>
        4.4.2 Arithmetic, Algebra and Logic
            4.4.2.1 Quotient <quotient/>
            4.4.2.2 Factorial <factorial/>
            4.4.2.3 Division <divide/>
            4.4.2.4 Maximum <max/>
            4.4.2.5 Minimum <min/>
            4.4.2.6 Subtraction <minus/>
            4.4.2.7 Addition <plus/>
            4.4.2.8 Exponentiation <power/>
            4.4.2.9 Remainder <rem/>
            4.4.2.10 Multiplication <times/>
            4.4.2.11 Root <root/>
            4.4.2.12 Greatest common divisor <gcd/>
            4.4.2.13 And <and/>
            4.4.2.14 Or <or/>
            4.4.2.15 Exclusive Or <xor/>
            4.4.2.16 Not <not/>
            4.4.2.17 Implies <implies/>
            4.4.2.18 Universal quantifier <forall/>
            4.4.2.19 Existential quantifier <exists/>
            4.4.2.20 Absolute Value <abs/>
            4.4.2.21 Complex conjugate <conjugate/>
            4.4.2.22 Argument <arg/>
            4.4.2.23 Real part <real/>
            4.4.2.24 Imaginary part <imaginary/>
            4.4.2.25 Lowest common multiple <lcm/>
            4.4.2.26 Floor <floor/>
            4.4.2.27 Ceiling <ceiling/>
        4.4.3 Relations
            4.4.3.1 Equals <eq/>
            4.4.3.2 Not Equals <neq/>
            4.4.3.3 Greater than <gt/>
            4.4.3.4 Less Than <lt/>
            4.4.3.5 Greater Than or Equal <geq/>
            4.4.3.6 Less Than or Equal <leq/>
            4.4.3.7 Equivalent <equivalent/>
            4.4.3.8 Approximately <approx/>
            4.4.3.9 Factor Of <factorof/>
        4.4.4 Calculus and Vector Calculus
            4.4.4.1 Integral <int/>
            4.4.4.2 Differentiation <diff/>
            4.4.4.3 Partial Differentiation <partialdiff/>
            4.4.4.4 Divergence <divergence/>
            4.4.4.5 Gradient <grad/>
            4.4.4.6 Curl <curl/>
            4.4.4.7 Laplacian <laplacian/>
        4.4.5 Theory of Sets
            4.4.5.1 Set <set>
            4.4.5.2 List <list>
            4.4.5.3 Union <union/>
            4.4.5.4 Intersect <intersect/>
            4.4.5.5 Set inclusion <in/>
            4.4.5.6 Set exclusion <notin/>
            4.4.5.7 Subset <subset/>
            4.4.5.8 Proper Subset <prsubset/>
            4.4.5.9 Not Subset <notsubset/>
            4.4.5.10 Not Proper Subset <notprsubset/>
            4.4.5.11 Set Difference <setdiff/>
            4.4.5.12 Cardinality <card/>
            4.4.5.13 Cartesian product <cartesianproduct/>
        4.4.6 Sequences and Series
            4.4.6.1 Sum <sum/>
            4.4.6.2 Product <product/>
            4.4.6.3 Limits <limit/>
            4.4.6.4 Tends To <tendsto/>
        4.4.7 Elementary classical functions
            4.4.7.1 Common trigonometric functions <sin/>, <cos/>, <tan/>, <sec/>, <csc/>, <cot/>
            4.4.7.2 Common inverses of trigonometric functions <arcsin/>, <arccos/>, <arctan/>, <arcsec/>, <arccsc/>, <arccot/>
            4.4.7.3 Common hyperbolic functions <sinh/>, <cosh/>, <tanh/>, <sech/>, <csch/>, <coth/>
            4.4.7.4 Common inverses of hyperbolic functions <arcsinh/>, <arccosh/>, <arctanh/>, <arcsech/>, <arccsch/>, <arccoth/>
            4.4.7.5 Exponential <exp/>
            4.4.7.6 Natural Logarithm <ln/>
            4.4.7.7 Logarithm <log/> , <logbase>
        4.4.8 Statistics
            4.4.8.1 Mean <mean/>
            4.4.8.2 Standard Deviation <sdev/>
            4.4.8.3 Variance <variance/>
            4.4.8.4 Median <median/>
            4.4.8.5 Mode <mode/>
            4.4.8.6 Moment <moment/>, <momentabout>
        4.4.9 Linear Algebra
            4.4.9.1 Vector <vector>
            4.4.9.2 Matrix <matrix>
            4.4.9.3 Matrix row <matrixrow>
            4.4.9.4 Determinant <determinant/>
            4.4.9.5 Transpose <transpose/>
            4.4.9.6 Selector <selector/>
            4.4.9.7 Vector product <vectorproduct/>
            4.4.9.8 Scalar product <scalarproduct/>
            4.4.9.9 Outer product <outerproduct/>
        4.4.10 Constant and Symbol Elements
            4.4.10.1 integers <integers/>
            4.4.10.2 reals <reals/>
            4.4.10.3 Rational Numbers <rationals/>
            4.4.10.4 Natural Numbers <naturalnumbers/>
            4.4.10.5 complexes <complexes/>
            4.4.10.6 primes <primes/>
            4.4.10.7 Exponential e <exponentiale/>
            4.4.10.8 Imaginary i <imaginaryi/>
            4.4.10.9 Not A Number <notanumber/>
            4.4.10.10 True <true/>
            4.4.10.11 False <false/>
            4.4.10.12 Empty Set <emptyset/>
            4.4.10.13 pi <pi/>
            4.4.10.14 Euler gamma <eulergamma/>
            4.4.10.15 infinity <infinity/>
 */
/** base class for lightweight generic HTML element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class MathMLElement extends AbstractCMElement implements MathMLConstants {


	private final static Logger LOG = Logger.getLogger(MathMLElement.class);

	private static final String CLASS = "class";
	private static final String ID = "id";
	private static final String NAME = "name";


	public static final String STYLESHEET = "stylesheet";
	public static final String TEXT_CSS = "text/css";
	public static final String TEXT_JAVASCRIPT = "text/javascript";
	public static final String TITLE = "title";
	public static final String UTF_8 = "UTF-8";
	public static final String TYPE = "type";
	private static final String CHARSET = "charset";

	private static final String UNKNOWN = "unknown";

	public static String[] TAGS = {
		MathMLCi.TAG, 
		"times", 
	};
	public static Set<String> TAGSET;
	static {
		TAGSET = new HashSet<String>();
		for (String tag : TAGS) {
			TAGSET.add(tag);
		}
	};
	
	/** constructor.
	 * 
	 * @param name
	 * @param namespace
	 */
	public MathMLElement() {
		super(UNKNOWN, MATHML_NS);
	}

	
	/** constructor.
	 * 
	 * @param name
	 * @param namespace
	 */
	public MathMLElement(String name) {
		super(name, MATHML_NS);
	}

	/** constructor.
	 * 
	 * @param name
	 * @param namespace
	 */
	public MathMLElement(MathMLElement element) {
        this(element.getLocalName());
        copyAttributesChildrenElements(element);
	}

	/** creates subclassed elements.
	 * 
	 * fails on error.
	 * @param element
	 * @return
	 */
	public static MathMLElement create(Element element) {
		return MathMLElement.create(element, false, false);
	}
		
	/** creates subclassed elements.
	 * 
	 * if an error is encountered and abort = false, outputs message and
	 * continues, else fails;
	 * 
	 * @param element
	 * @param abort 
	 * @param ignores namespaces (e.g. from Jsoup)
	 * @return
	 */
	private static MathMLElement create(Element element, boolean abort, boolean ignoreNamespaces) {
		MathMLElement htmlElement = null;
		String tag = element.getLocalName();
		String namespaceURI = element.getNamespaceURI();
		if (!ignoreNamespaces && !MATHML_NS.equals(namespaceURI)) {
			// might be SVG or MathML 
			if (!namespaceURI.equals("")) {
				LOG.trace("multiple Namespaces "+namespaceURI);
			}
			LOG.trace("Unknown namespace: "+namespaceURI);
			htmlElement = addUnknownTag(namespaceURI,tag);
		} else if(MathMLApply.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new MathMLApply();
		} else if(MathMLCi.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new MathMLCi();
		} else if(MathMLCn.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new MathMLCn();
		} else if(MathMLDiff.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new MathMLDiff();
		} else if(MathMLEq.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new MathMLEq();
		} else if(MathMLExp.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new MathMLExp();
		} else if(MathMLMath.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new MathMLMath();
		} else if(MathMLMinus.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new MathMLMinus();
		} else if(MathMLPlus.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new MathMLPlus();
		} else if(MathMLTimes.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new MathMLTimes();
		} else if (TAGSET.contains(tag.toUpperCase())) {
			htmlElement = new MathMLElement(tag.toLowerCase());
		} else {
			String msg = "Unknown html tag "+tag;
			if (abort) {
				throw new RuntimeException(msg);
			}
			htmlElement = addUnknownTag(namespaceURI,tag);
		}
		XMLUtil.copyAttributes(element, htmlElement);
		for (int i = 0; i < element.getChildCount(); i++) {
			Node child = element.getChild(i);
			if (child instanceof Element) {
				MathMLElement htmlChild = MathMLElement.create((Element)child, abort, ignoreNamespaces);
				if (htmlElement != null) {	
					htmlElement.appendChild(htmlChild);
				}
			} else {
				if (htmlElement != null) {
					htmlElement.appendChild(child.copy());
				}
			}
		}
		return htmlElement;
		
	}

	private static MathMLElement addUnknownTag(String namespaceURI, String tag) {
		MathMLElement htmlElement;
		htmlElement = new MathMLElement();
		htmlElement.addAttribute(new Attribute("class", namespaceURI+"_"+tag));
		return htmlElement;
	}
	
	public void setAttribute(String name, String value) {
		this.addAttribute(new Attribute(name, value));
	}

	public void setContent(String content) {
		this.appendChild(content);
	}
	
	public String getClassAttribute() {
		return this.getAttributeValue(CLASS);
	}

	public void setClassAttribute(String value) {
		this.setAttribute(CLASS, value);
	}

	public void setId(String value) {
		if (value == null) {
			throw new RuntimeException("NULL id");
		}
		this.setAttribute(ID, value);
	}

	public void setName(String value) {
		this.setAttribute(NAME, value);
	}

	public void output(OutputStream os) throws IOException {
		XMLUtil.debug(this, os, 1);
	}

	public void debug(String msg) {
		XMLUtil.debug(this, msg);
	}

	public void setValue(String value) {
		this.removeChildren();
		this.appendChild(value);
	}

	public String getId() {
		return this.getAttributeValue(ID);
	}

	public String getTitle() {
		return this.getAttributeValue(TITLE);
	}

	public void setUTF8Charset(String string) {
		this.addAttribute(new Attribute(CHARSET, UTF_8));
	}

	public void setCharset(String charset) {
		this.addAttribute(new Attribute(CHARSET, charset));
	}

	public void setType(String type) {
		this.addAttribute(new Attribute(TYPE, type));
	}

	public void setTitle(String title) {
		this.addAttribute(new Attribute(TITLE, title));
	}

	public static List<MathMLElement> getSelfOrDescendants(MathMLElement root, String tag) {
		tag = tag.toLowerCase();
		String xpath = ".//*[local-name()='"+tag+"']";
		Nodes nodes = root.query(xpath);
		List<MathMLElement> elements = new ArrayList<MathMLElement>();
		for (int i = 0; i < nodes.size(); i++) {
			elements.add((MathMLElement)nodes.get(i));
		}
		return elements;
	}

	public static MathMLElement getSingleSelfOrDescendant(MathMLElement root, String tag) {
		List<MathMLElement> elements = getSelfOrDescendants(root, tag);
		return (elements.size() != 1) ? null : elements.get(0);
	}

	public static List<MathMLElement> getChildElements(MathMLElement root, String tag) {
		tag = tag.toLowerCase();
		Nodes nodes = root.query("./*[local-name()='"+tag+"']");
		List<MathMLElement> elements = new ArrayList<MathMLElement>();
		for (int i = 0; i < nodes.size(); i++) {
			elements.add((MathMLElement)nodes.get(i));
		}
		return elements;
	}

	public static MathMLElement getSingleChildElement(MathMLElement root, String tag) {
		List<MathMLElement> elements = getChildElements(root, tag);
		return (elements.size() != 1) ? null : elements.get(0);
	}



}
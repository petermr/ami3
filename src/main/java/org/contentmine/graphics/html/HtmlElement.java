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

    package org.contentmine.graphics.html;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.StyleBundle;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;


/*
 Legend: Optional, Forbidden, Empty, Deprecated, Loose DTD, Frameset DTD
Name 	Start Tag 	End Tag 	Empty 	Depr. 	DTD 	Description
A 	  	  	  	  	  	anchor
ABBR 	  	  	  	  	  	abbreviated form (e.g., WWW, HTTP, etc.)
ACRONYM 	  	  	  	  	  	 
ADDRESS 	  	  	  	  	  	information on author
APPLET 	  	  	  	D 	L 	Java applet
AREA 	  	F 	E 	  	  	client-side image map area
B 	  	  	  	  	  	bold text style
BASE 	  	F 	E 	  	  	document base URI
BASEFONT 	  	F 	E 	D 	L 	base font size
BDO 	  	  	  	  	  	I18N BiDi over-ride
BIG 	  	  	  	  	  	large text style
BLOCKQUOTE 	  	  	  	  	  	long quotation
BODY 	O 	O 	  	  	  	document body
BR 	  	F 	E 	  	  	forced line break
BUTTON 	  	  	  	  	  	push button
CAPTION 	  	  	  	  	  	table caption
CENTER 	  	  	  	D 	L 	shorthand for DIV align=center
CITE 	  	  	  	  	  	citation
CODE 	  	  	  	  	  	computer code fragment
COL 	  	F 	E 	  	  	table column
COLGROUP 	  	O 	  	  	  	table column group
DD 	  	O 	  	  	  	definition description
DEL 	  	  	  	  	  	deleted text
DFN 	  	  	  	  	  	instance definition
DIR 	  	  	  	D 	L 	directory list
DIV 	  	  	  	  	  	generic language/style container
DL 	  	  	  	  	  	definition list
DT 	  	O 	  	  	  	definition term
EM 	  	  	  	  	  	emphasis
FIELDSET 	  	  	  	  	  	form control group
FONT 	  	  	  	D 	L 	local change to font
FORM 	  	  	  	  	  	interactive form
FRAME 	  	F 	E 	  	F 	subwindow
FRAMESET 	  	  	  	  	F 	window subdivision
H1 	  	  	  	  	  	heading
H2 	  	  	  	  	  	heading
H3 	  	  	  	  	  	heading
H4 	  	  	  	  	  	heading
H5 	  	  	  	  	  	heading
H6 	  	  	  	  	  	heading
HEAD 	O 	O 	  	  	  	document head
HR 	  	F 	E 	  	  	horizontal rule
HTML 	O 	O 	  	  	  	document root element
I 	  	  	  	  	  	italic text style
IFRAME 	  	  	  	  	L 	inline subwindow
IMG 	  	F 	E 	  	  	Embedded image
INPUT 	  	F 	E 	  	  	form control
INS 	  	  	  	  	  	inserted text
ISINDEX 	  	F 	E 	D 	L 	single line prompt
KBD 	  	  	  	  	  	text to be entered by the user
LABEL 	  	  	  	  	  	form field label text
LEGEND 	  	  	  	  	  	fieldset legend
LI 	  	O 	  	  	  	list item
LINK 	  	F 	E 	  	  	a media-independent link
MAP 	  	  	  	  	  	client-side image map
MENU 	  	  	  	D 	L 	menu list
META 	  	F 	E 	  	  	generic metainformation
NOFRAMES 	  	  	  	  	F 	alternate content container for non frame-based rendering
NOSCRIPT 	  	  	  	  	  	alternate content container for non script-based rendering
OBJECT 	  	  	  	  	  	generic embedded object
OL 	  	  	  	  	  	ordered list
OPTGROUP 	  	  	  	  	  	option group
OPTION 	  	O 	  	  	  	selectable choice
P 	  	O 	  	  	  	paragraph
PARAM 	  	F 	E 	  	  	named property value
PRE 	  	  	  	  	  	preformatted text
Q 	  	  	  	  	  	short inline quotation
S 	  	  	  	D 	L 	strike-through text style
SAMP 	  	  	  	  	  	sample program output, scripts, etc.
SCRIPT 	  	  	  	  	  	script statements
SELECT 	  	  	  	  	  	option selector
SMALL 	  	  	  	  	  	small text style
SPAN 	  	  	  	  	  	generic language/style container
STRIKE 	  	  	  	D 	L 	strike-through text
STRONG 	  	  	  	  	  	strong emphasis
STYLE 	  	  	  	  	  	style info
SUB 	  	  	  	  	  	subscript
SUP 	  	  	  	  	  	superscript
TABLE 	  	  	  	  	  	 
TBODY 	O 	O 	  	  	  	table body
TD 	  	O 	  	  	  	table data cell
TEXTAREA 	  	  	  	  	  	multi-line text field
TFOOT 	  	O 	  	  	  	table footer
TH 	  	O 	  	  	  	table header cell
THEAD 	  	O 	  	  	  	table header
TITLE 	  	  	  	  	  	document title
TR 	  	O 	  	  	  	table row
TT 	  	  	  	  	  	teletype or monospaced text style
U 	  	  	  	D 	L 	underlined text style
UL 	  	  	  	  	  	unordered list
VAR 	  	  	  	  	  	instance of a variable or program argument

 */
/** base class for lightweight generic HTML element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public abstract class HtmlElement extends AbstractCMElement {


	private final static Logger LOG = Logger.getLogger(HtmlElement.class);



	public static final String STYLESHEET = "stylesheet";
	public static final String TEXT_CSS = "text/css";
	public static final String TEXT_JAVASCRIPT = "text/javascript";

	public static String[] tags = {
		"A", 
		"ABBR", 
		"ACRONYM", 
		"ADDRESS", 
		"APPLET", 
		"AREA", 
		"B", 
		"BASE", 
		"BASEFONT", 
		"BDO", 
		"BIG", 
		"BLOCKQUOTE", 
		"BODY", 
		"BR", 
		"BUTTON", 
		"CAPTION", 
		"CENTER", 
		"CITE", 
		"CODE", 
		"COL", 
		"COLGROUP", 
		"DD", 
		"DEL", 
		"DFN", 
		"DIR", 
		"DIV", 
		"DL", 
		"DT", 
		"EM", 
		"FIELDSET", 
		"FONT", 
		"FORM", 
		"FRAME", 
		"FRAMESET", 
		"H1", 
		"H2", 
		"H3", 
		"H4", 
		"H5", 
		"H6", 
		"HEAD", 
		"HR", 
		"HTML", 
		"I", 
		"IFRAME", 
		"IMG", 
		"INPUT", 
		"INS", 
		"ISINDEX", 
		"KBD", 
		"LABEL", 
		"LEGEND", 
		"LI", 
		"LINK", 
		"MAP", 
		"MENU", 
		"META", 
		"NOFRAMES", 
		"NOSCRIPT", 
		"OBJECT", 
		"OL", 
		"OPTGROUP", 
		"OPTION", 
		"P", 
		"PARAM", 
		"PRE", 
		"Q", 
		"S", 
		"SAMP", 
		"SCRIPT", 
		"SELECT", 
		"SMALL", 
		"SPAN", 
		"STRIKE", 
		"STRONG", 
		"STYLE", 
		"SUB", 
		"SUP", 
		"TABLE", 
		"TBODY", 
		"TD", 
		"TEXTAREA", 
		"TFOOT", 
		"TH", 
		"THEAD", 
		"TITLE", 
		"TR", 
		"TT", 
		"U", 
		"UL", 
		"VAR", 
	};
	public static Set<String> TAGSET;
	static {
		TAGSET = new HashSet<String>();
		for (String tag : tags) {
			TAGSET.add(tag);
		}
	};
	
	public enum Target {
		bottom,
		menu,
		separate;
	};
	
	// coordinates
	public final static String X = "x"; 
	public final static String Y = "y"; 
	
	/** constructor.
	 * 
	 * @param name
	 * @param namespace
	 */
	public HtmlElement(String name) {
		super(name, XHTML_NS);
	}

	/** creates subclassed elements.
	 * 
	 * fails on error.
	 * @param element
	 * @return
	 */
	@Deprecated // use HtmlFactory
	// think it can still be used
	public static HtmlElement create(Element element) {
		// changed to ignoreNamespaces = true
		return HtmlElement.create(element, false, true);
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
	@Deprecated // use HtmlFactory instead
	public
	static HtmlElement create(Element element, boolean abort, boolean ignoreNamespaces) {
		HtmlElement htmlElement = null;
		String tag = element.getLocalName();
		String namespaceURI = element.getNamespaceURI();
		if (!ignoreNamespaces && !XHTML_NS.equals(namespaceURI)) {
			// might be SVG 
			if (!namespaceURI.equals("")) {
				LOG.trace("multiple Namespaces "+namespaceURI);
			}
			LOG.trace("Unknown namespace: "+namespaceURI);
			htmlElement = addUnknownTag(namespaceURI,tag);
		} else if(HtmlA.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlA();
		} else if(HtmlB.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlB();
		} else if(HtmlBig.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlBig();
		} else if(HtmlBody.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlBody();
		} else if(HtmlBr.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlBr();
		} else if(HtmlCaption.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlCaption();
		} else if(HtmlDiv.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlDiv();
		} else if(HtmlEm.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlEm();
		} else if(HtmlFrame.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlFrame();
		} else if(HtmlFrameset.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlFrameset();
		} else if(HtmlH1.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlH1();
		} else if(HtmlH2.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlH2();
		} else if(HtmlH3.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlH3();
		} else if(HtmlHead.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlHead();
		} else if(HtmlHr.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlHr();
		} else if(HtmlHtml.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlHtml();
		} else if(HtmlI.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlI();
		} else if(HtmlImg.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlImg();
		} else if(HtmlLi.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlLi();
		} else if(HtmlLink.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlLink();
		} else if(HtmlMeta.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlMeta();
		} else if(HtmlOl.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlOl();
		} else if(HtmlP.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlP();
		} else if(HtmlS.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlS();
		} else if(HtmlScript.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlScript();
		} else if(HtmlSmall.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlSmall();
		} else if(HtmlSpan.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlSpan();
		} else if(HtmlStrong.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlStrong();
		} else if(HtmlStyle.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlStyle();
		} else if(HtmlSub.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlSub();
		} else if(HtmlSup.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlSup();
		} else if(HtmlTable.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTable();
		} else if(HtmlTbody.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTbody();
		} else if(HtmlTfoot.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTfoot();
		} else if(HtmlThead.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlThead();
		} else if(HtmlTd.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTd();
		} else if(HtmlTh.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTh();
		} else if(HtmlTr.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTr();
		} else if(HtmlTt.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlTt();
		} else if(HtmlUl.TAG.equalsIgnoreCase(tag)) {
			htmlElement = new HtmlUl();
		} else if (TAGSET.contains(tag.toUpperCase())) {
			htmlElement = new HtmlGeneric(tag.toLowerCase());
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
				AbstractCMElement htmlChild = HtmlElement.create((Element)child, abort, ignoreNamespaces);
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

	private static HtmlElement addUnknownTag(String namespaceURI, String tag) {
		HtmlElement htmlElement;
		htmlElement = new HtmlDiv();
		htmlElement.addAttribute(new Attribute("class", namespaceURI+"_"+tag));
		return htmlElement;
	}
	
	public HtmlElement setAttribute(String name, String value) {
		this.addAttribute(new Attribute(name, value));
		return this;
	}

	public HtmlElement setContent(String content) {
		this.appendChild(content);
		return this;
	}
	
	public String getClassAttribute() {
		return this.getAttributeValue(CLASS);
	}

	public HtmlElement setClassAttribute(String value) {
		this.setAttribute(CLASS, value);
		return this;
	}

	public HtmlElement setId(String value) {
		if (value == null) {
			throw new RuntimeException("NULL id");
		}
		this.setAttribute(ID, value);
		return this;
	}

	public HtmlElement setName(String value) {
		this.setAttribute(NAME, value);
		return this;
	}

	public void output(OutputStream os) throws IOException {
		XMLUtil.debug(this, os, 1);
	}

	public void debug(String msg) {
		XMLUtil.debug(this, msg);
	}

	public HtmlElement setValue(String value) {
		this.removeChildren();
		this.appendChild(value);
		return this;
	}

	public String getId() {
		return this.getAttributeValue(ID);
	}

	public String getTitle() {
		return this.getAttributeValue(TITLE);
	}

	public HtmlElement setUTF8Charset(String string) {
		this.addAttribute(new Attribute(CHARSET, UTF_8));
		return this;
	}

	public HtmlElement setCharset(String charset) {
		this.addAttribute(new Attribute(CHARSET, charset));
		return this;
	}

	public HtmlElement setType(String type) {
		this.addAttribute(new Attribute(TYPE, type));
		return this;
	}

	public HtmlElement addJavascript(String content) {
		HtmlScript script = new HtmlScript();
		script.setCharset(UTF_8);
		script.setType(TEXT_JAVASCRIPT);
		script.appendChild(content); 
		this.appendChild(script);
		return this;
	}

	public HtmlElement setTitle(String title) {
		this.addAttribute(new Attribute(TITLE, title));
		return this;
	}

	/** the value should be constructed using StyleBundle
	 * 
	 * @param style
	 */
	public HtmlElement setStyle(String style) {
		setAttributeOrRemoveIfNull(STYLE, style);
		return this;
	}

	public boolean isBold() {
		return StyleBundle.isBold(this);
	}
	
	public boolean isItalic() {
		return StyleBundle.isItalic(this);
	}

	public String getFontFamily() {
		return StyleBundle.getFontFamily(this);
	}

	public String getFill() {
		return StyleBundle.getFill(this);
	}

	public String getStroke() {
		return StyleBundle.getStroke(this);
	}

	public Double getStrokeWidth() {
		return StyleBundle.getStrokeWidth(this);
	}

	public Double getOpacity() {
		return StyleBundle.getOpacity(this);
	}

	public Double getFontSize() {
		return StyleBundle.getFontSize(this);
	}

	/** if the final child node is Text return it.
	 * 
	 * @return
	 */
	public Text getFinalTextNode() {
		Text textNode = null;
		int childCount = this.getChildCount();
		if (childCount > 0) {
			Node finalNode = this.getChild(childCount - 1);
			if (finalNode instanceof Text) {
				textNode = (Text) finalNode;
			}
		}
		return textNode;
	}

    public void appendChild(Node child) {
    	if (child == null) {
    		LOG.error("null child");
    	} else {
    		super.appendChild(child);
    	}
    }
    
	public Double getX() {
		Double x = getCoordinateValue(X);
		return x;
	}
	public Double getY() {
		Double y = getCoordinateValue(Y);
		return y;
	}
	
	public Real2 getXY() {
		Double x = getX();
		Double y = getY();
		return x == null || y == null ? null : new Real2(x, y);
	}

	private Double getCoordinateValue(String coordName) {
		String coordString = this.getAttributeValue(coordName);
		Double coord = null;
		if (coordString != null && !"null".equals(coordString)) {
			try {
				coord = (Double) Double.parseDouble(coordString);
			} catch (Exception e) {
				System.err.println("Cannot parse as double "+coordString);
			}
		}
		return coord;
	}



}
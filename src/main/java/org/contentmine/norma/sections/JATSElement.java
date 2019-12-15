package org.contentmine.norma.sections;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

public class JATSElement extends Element {

	private static final Logger LOG = Logger.getLogger(JATSElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String CLASS = "class";
	public static final String ID = "id";
	protected CTree cTree;
	
	/**
<abbrev>
    <abbrev-journal-title>
    <abstract>
    <access-date>
    <ack>
    <addr-line>
    <address>
    <aff>
    <aff-alternatives>
    <ali:free_to_read>
    <ali:license_ref>
    <alt-text>
    <alt-title>
    <alternatives>
    <annotation>
    <anonymous>
    <app>
    <app-group>
    <array>
    <article>
    <article-categories>
    <article-id>
    <article-meta>
    <article-title>
    <article-version>
    <article-version-alternatives>
    <attrib>
    <author-comment>
    <author-notes>
    <award-group>
    <award-id>
    <back>
    <bio>
    <body>
    <bold>
    <boxed-text>
    <break>
    <caption>
    <chapter-title>
    <chem-struct>
    <chem-struct-wrap>
    <citation-alternatives>
    <city>
    <code>
    <col>
    <colgroup>
    <collab>
    <collab-alternatives>
    <comment>
    <compound-kwd>
    <compound-kwd-part>
    <compound-subject>
    <compound-subject-part>
    <conf-acronym>
    <conf-date>
    <conf-loc>
    <conf-name>
    <conf-num>
    <conf-sponsor>
    <conf-theme>
    <conference>
    <contrib>
    <contrib-group>
    <contrib-id>
    <contributed-resource-group>
    <copyright-holder>
    <copyright-statement>
    <copyright-year>
    <corresp>
    <count>
    <country>
    <counts>
    <custom-meta>
    <custom-meta-group>
    <data-title>
    <date>
    <date-in-citation>
    <day>
    <def>
    <def-head>
    <def-item>
    <def-list>
    <degrees>
    <disp-formula>
    <disp-formula-group>
    <disp-quote>
    <edition>
    <element-citation>
    <elocation-id>
    <email>
    <equation-count>
    <era>
    <etal>
    <event>
    <event-desc>
    <ext-link>
    <fax>
    <fig>
    <fig-count>
    <fig-group>
    <fixed-case>
    <floats-group>
    <fn>
    <fn-group>
    <fpage>
    <front>
    <front-stub>
    <funding-group>
    <funding-source>
    <funding-statement>
    <given-names>
    <glossary>
    <glyph-data>
    <glyph-ref>
    <gov>
    <graphic>
    <history>
    <hr>
    <index-term>
    <index-term-range-end>
    <inline-formula>
    <inline-graphic>
    <inline-media>
    <inline-supplementary-material>
    <institution>
    <institution-id>
    <institution-wrap>
    <isbn>
    <issn>
    <issn-l>
    <issue>
    <issue-id>
    <issue-part>
    <issue-sponsor>
    <issue-title>
    <italic>
    <journal-id>
    <journal-meta>
    <journal-subtitle>
    <journal-title>
    <journal-title-group>
    <kwd>
    <kwd-group>
    <label>
    <license>
    <license-p>
    <list>
    <list-item>
    <long-desc>
    <lpage>
    <media>
    <meta-name>
    <meta-value>
    <milestone-end>
    <milestone-start>
    <mixed-citation>
    <mml:math>
    <monospace>
    <month>
    <name>
    <name-alternatives>
    <named-content>
    <nested-kwd>
    <nlm-citation>
    <note>
    <notes>
    <object-id>
    <on-behalf-of>
    <open-access>
    <overline>
    <overline-end>
    <overline-start>
    <p>
    <page-count>
    <page-range>
    <part-title>
    <patent>
    <permissions>
    <person-group>
    <phone>
    <postal-code>
    <prefix>
    <preformat>
    <price>
    <principal-award-recipient>
    <principal-investigator>
    <private-char>
    <product>
    <pub-date>
    <pub-date-not-available>
    <pub-history>
    <pub-id>
    <publisher>
    <publisher-loc>
    <publisher-name>
    <rb>
    <ref>
    <ref-count>
    <ref-list>
    <related-article>
    <related-object>
    <resource-group>
    <resource-id>
    <resource-name>
    <resource-wrap>
    <response>
    <role>
    <roman>
    <rp>
    <rt>
    <ruby>
    <sans-serif>
    <sc>
    <season>
    <sec>
    <sec-meta>
    <see>
    <see-also>
    <self-uri>
    <series>
    <series-text>
    <series-title>
    <sig>
    <sig-block>
    <size>
    <source>
    <speaker>
    <speech>
    <state>
    <statement>
    <std>
    <std-organization>
    <strike>
    <string-conf>
    <string-date>
    <string-name>
    <styled-content>
    <sub>
    <sub-article>
    <subj-group>
    <subject>
    <subtitle>
    <suffix>
    <sup>
    <supplement>
    <supplementary-material>
    <support-description>
    <support-group>
    <support-source>
    <surname>
    <table>
    <table-count>
    <table-wrap>
    <table-wrap-foot>
    <table-wrap-group>
    <target>
    <tbody>
    <td>
    <term>
    <term-head>
    <tex-math>
    <textual-form>
    <tfoot>
    <th>
    <thead>
    <time-stamp>
    <title>
    <title-group>
    <tr>
    <trans-abstract>
    <trans-source>
    <trans-subtitle>
    <trans-title>
    <trans-title-group>
    <underline>
    <underline-end>
    <underline-start>
    <unstructured-kwd-group>
    <uri>
    <verse-group>
    <verse-line>
    <version>
    <volume>
    <volume-id>
    <volume-issue-group>
    <volume-series>
    <word-count>
    <x>
    <xref>
    <year>
	 */

	
	public static final	List<String> FLOAT_TAGS = Arrays.asList(new String[] {
			JATSFigElement.TAG,
			JATSFigGroupElement.TAG,
			JATSGraphicElement.TAG,
			JATSFnGroupElement.TAG,
			JATSNotesElement.TAG,
		});
	
//	protected File currentDir;
	private JATSFloatsGroupElement floatsGroupElement;
	
	public JATSElement(Element element) {
		super(element.getLocalName());
	}
	
	public JATSElement(String tag) {
		super(tag);
	}

	protected void setClassAttribute(String name) {
		this.addAttribute(new Attribute(CLASS, name));
	}

	protected void applyNonXMLSemantics() {
		// no-op override
	}

	public void recurseThroughDescendants(Element element, JATSFactory jatsFactory) {
		XMLUtil.copyAttributes(element, this);
		for (int i = 0; i < element.getChildCount(); i++) {
			Node childNode = element.getChild(i);
			if (childNode instanceof Element) {
				Element childElement = (Element) childNode;
				String tag = childElement.getLocalName();
				List<String> allowedChildNames = getAllowedChildNames();
				if (allowedChildNames.size() > 0 && !allowedChildNames.contains(tag)) {
					String xml = childElement.toXML();
				}
				this.appendChild(jatsFactory.create(childElement));
			} else {
				this.appendChild(childNode.copy());
			}
		}
		applyNonXMLSemantics();
	}
	
	/** overridden by anything that wants to check children
	 * 
	 * @return
	 */
	protected List<String> getAllowedChildNames() {
		return new ArrayList<String>();
	}

	protected String getSingleChildValue(String tag) {
		return XMLUtil.getSingleValue(this, "*[local-name()='"+tag+"']");
	}
	
	protected List<Element> getChildElementList(String tag) {
		return XMLUtil.getQueryElements(this, "*[local-name()='"+tag+"']");
	}
	
	protected JATSElement getSingleChild(String tag) {
		return (JATSElement) XMLUtil.getSingleElement(this,"*[local-name()='"+tag+"']");
	}
	
	protected JATSElement getSingleChild() {
		return (JATSElement) XMLUtil.getSingleElement(this,"*");
	}
	
	protected List<Element> getChildElementList() {
		return XMLUtil.getQueryElements(this, "*");
	}
	
	protected String getSingleValueByClassAttributeValue(String attName) {
		Element element = XMLUtil.getSingleElement(this,"*[@class='"+attName+"']");
		return element == null ? null : element.getValue();
	}

	protected String getSingleChildValueByAttribute(String attName) {
		return XMLUtil.getSingleValue(this,"*[@"+attName+"]");
	}

	protected String getSingleValue(String xpath) {
		return XMLUtil.getSingleValue(this, xpath);
	}


	public String debugString() {
		return debugString(0);
	}

	public String debugString(int level) {
		StringBuilder sb = new StringBuilder();
		sb.append(Util.spaces(level)+"<"+this.getLocalName());
		sb.append(this.getAttributeString(ID));
		sb.append(this.getAttributeString());
		
		sb.append(">");
		List<Element> childElements = this.getChildElementList();
		List<Text> childTexts = this.getNonWhitespaceTextList();
		if (childTexts.size() > 0) {
			String childText = childTexts.get(0).getValue();
			sb.append(Util.truncateAndAddEllipsis(childText, 20));
			sb.append("</"+this.getLocalName()+">");
		} else if (childElements.size() > 0) {
			sb.append("\n");
			for (Element childElement : childElements) {
				if (childElement instanceof JATSElement) {
					JATSElement jatsElement = (JATSElement)childElement;
					sb.append(jatsElement.debugString(level + 1));
				} else {
					sb.append("<H/> "+childElement.toXML());
					sb.append("\n");
				}
			}
			sb.append(Util.spaces(level)+"</"+this.getLocalName()+">");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	protected String getAttributeString() {
		return "";
	}

	public List<Text> getNonWhitespaceTextList() {
		List<Text> textList = new ArrayList<>();
		List<Node> nodes = XMLUtil.getQueryNodes(this, "text()");
		for (Node node : nodes) {
			if (node.getValue().trim().length() > 0) {
				textList.add((Text)node);
			}
		}
		return textList;
	}

	public String getClassValue() {
		return getAttributeValue(CLASS);
	}
	
	public String getID() {
		return getAttributeValue(ID);
	}

	public void setID(String id) {
		this.addAttribute(new Attribute(ID, id));
	}

	protected String getAttributeString(String name) {
		String s = this.getAttributeValue(name);
		return (s == null || s.equals("null")) ? "" : " " + name + "='"+s+"'";
	}

	protected void addNonNull(StringBuilder sb, String value) {
		if (value != null) sb.append(value);
	}

	protected void addNonNullDebugString(StringBuilder sb, JATSElement jatsElement, int level) {
		if (jatsElement != null) sb.append(jatsElement.debugString(level)+"|");
	}

	
	public void writeSections(CTree cTree) {
		this.setCTree(cTree);
		File currentDir = cTree.getSectionsDirectory();
		extractFloatsIntoFloatsGroup(this);
		writeSections(this, currentDir);
	}

	private JATSFloatsGroupElement extractFloatsIntoFloatsGroup(JATSElement jatsElement) {
		floatsGroupElement = this.getOrCreateFloatsGroupElement();
		List<Element> childElements = jatsElement.getChildElementList();
		for (int i = 0; i < childElements.size(); i++) {
			Element childElement = childElements.get(i);
			if (childElement instanceof IsFloat) {
				childElement.detach();
				floatsGroupElement.appendChild(childElement);
			} else if (childElement instanceof JATSElement) {
				extractFloatsIntoFloatsGroup((JATSElement)childElement);
			}
		}
		return floatsGroupElement;
	}

	private JATSFloatsGroupElement getOrCreateFloatsGroupElement() {
		if (floatsGroupElement == null) {
			List<Element> floatsGroupElements = XMLUtil.getQueryElements(
					this, ".//*[local-name()='"+JATSFloatsGroupElement.TAG+"']");
			if (floatsGroupElements.size() > 0) {
				floatsGroupElement = (JATSFloatsGroupElement) floatsGroupElements.get(0);
			} else {
				floatsGroupElement = new JATSFloatsGroupElement();
				this.appendChild(floatsGroupElement);
			}
		}
		return floatsGroupElement;
	}

	protected void writeSections(JATSElement parent, File currentDir) {
		List<Element> childElements = parent.getChildElementList();
		int sec = 0;
		for (Element childElement : childElements) {
			String title = createTitle(childElement);
			if (childElement instanceof JATSElement) {
				JATSElement jatsChildElement = (JATSElement)childElement;
				jatsChildElement.setCTree(cTree);
				if (jatsChildElement instanceof IsBlock) {
					if (jatsChildElement instanceof HasDirectory) {
						String directoryName = title != null ? title : ((HasDirectory)jatsChildElement).directoryName() + "/";
						File childDir = new File(currentDir, sec+"_"+directoryName);
						childDir.mkdirs();
						writeSections(jatsChildElement, childDir);
					} else {
						writeElement(currentDir, sec, title, jatsChildElement);						
					}
				} else if (jatsChildElement instanceof IsInline) {
					writeElement(currentDir, sec, title, jatsChildElement);						
				} else if (jatsChildElement instanceof HasMixedContent) {
					writeElement(currentDir, sec, title, jatsChildElement);						
				} else {
					File childFile = new File(currentDir, sec+"_"+"UNK1_"+title+"."+CTree.XML);
					XMLUtil.writeQuietly(jatsChildElement, childFile, 1);						
					System.err.println("JATSElement untagged element: "+jatsChildElement.getLocalName());
				}
			} else {
				File childFile = new File(currentDir, sec+"_"+"UNK2_"+title);
				XMLUtil.writeQuietly(childElement, childFile, 1);						
				System.err.println("untagged element: "+childElement.getLocalName());
			}
			sec++;
		}
	}

	private void writeElement(File currentDir, int sec, String title, JATSElement jatsChildElement) {
		File childFile = new File(currentDir, sec+"_"+title+"."+CTree.XML);
		XMLUtil.writeQuietly(jatsChildElement, childFile, 1);
	}

	private String createTitle(Element childElement) {
		String title = null;
		if (childElement instanceof HasTitle) {
			title = ((HasTitle)childElement).generateTitle();
			if (title != null) {
				title = Util.makeLowercaseAndDespace(title, 25);
			}
		}
		if (title == null) {
			title = childElement.getLocalName();
		}
		return title;
	}

	protected JATSElement getTitleElement() {
		return null;
	}

	protected String getTitleValue() {
		JATSElement title = this.getTitleElement();
		return title == null ? "" : title.getValue();
	}


	protected void setCTree(CTree cTree) {
		this.cTree = cTree;
	}

	public HtmlElement createHTML() {
		LOG.debug("Overide createHTML in "+this.getLocalName());
		return deepCopyAndTransform(new HtmlDiv());
	}

	public HtmlElement deepCopyAndTransform(HtmlElement htmlElement) {
		XMLUtil.copyAttributes(this, htmlElement);
		for (int i = 0; i < this.getChildCount(); i++) {
			Node child = this.getChild(i);
			if (child instanceof Element) {
				HtmlElement childHtml = ((JATSElement)child).createHTML();
				htmlElement.appendChild(childHtml);
			} else {
				htmlElement.appendChild(child.copy());
			}
		}
		return htmlElement;
	}
}

package org.contentmine.norma.input.html;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.contentmine.norma.NormaArgProcessor;

import nu.xom.Element;

public class HtmlCleaner {

	
	private static final Logger LOG = Logger.getLogger(HtmlCleaner.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum TagPosition {
		ANYWHERE("//"),
		CHILD("./"),
		DESCENDANT(".//"),;
		private String xpathString;
		private TagPosition(String xp) {
			this.xpathString = xp;
		}
		public String getXPath() {
			return xpathString;
		}
	}
	
	public enum HtmlClass {
		ID("id"),
		CLASS("class"),
		ROLE("role"),;
		public String name;
		private HtmlClass(String name) {
			this.name= name;
		}
		public String getName() {
			return name;
		}
	}
	
	private static final String JSOUP = "jsoup";
	private static final String JTIDY = "jtidy";
	private static final String HTMLUNIT = "htmlunit";
	private static final String AT = "@";
	private static final String EQUALS = "=";
	private static final String COMMA = ",";
	private static final String LSQUARE = "[";
	private static final String RSQUARE = "]";
	private static final String LPAREN = "(";
	private static final String RPAREN = ")";
	private static final String AND = "and";
	private static final String SPACE = " ";
	
	private NormaArgProcessor normaArgProcessor;
	private HtmlElement htmlElement;
	private HtmlFactory htmlFactory;
	private boolean testWellFormed;
	private String cleanTag;
	private String containsAttName;
	private String containsAttValue;
	private String equalsAttName;
	private String equalsAttValue;
	private TagPosition tagPosition;
	private String xpath;

	public HtmlCleaner() {
		this.testWellFormed = false;
		createHtmlFactory();
	}

	public HtmlCleaner(NormaArgProcessor argProcessor) {
		this();
		this.normaArgProcessor = argProcessor;
	}

	/** creates cleaner with element to be cleaned
	 * 
	 * @param htmlElement
	 */
	public HtmlCleaner(HtmlElement htmlElement) {
		this.htmlElement = htmlElement;
	}

	private void createHtmlFactory() {
		htmlFactory = new HtmlFactory();
		// change these to take input from args.xml
		htmlFactory.setContentList(Arrays.asList(new String[]{
				"noscript", "script", "style", "iframe", "button", "fieldset", "label"}));
		htmlFactory.setNoContentList(Arrays.asList(new String[]{"input", "link", "form"}));
		htmlFactory.setBalanceList(Arrays.asList(new String[]{"meta"}));
		htmlFactory.setUseJsoup(true);
	}

	/**
	 * 
	 * @param optionValue
	 * @return
	 */
	public HtmlElement cleanHTML2XHTML(String optionValue) {
		
		if (!JSOUP.equalsIgnoreCase(optionValue)) {
			LOG.warn("tidying option not supported:"+optionValue);
		}
		CTree currentCMTree = normaArgProcessor.getCurrentCMTree();
		File inputFile = normaArgProcessor.checkAndGetInputFile(currentCMTree);

		return cleanHtmlFile(inputFile);
	}

	public HtmlElement cleanHtmlFile(File inputFile) {
		htmlElement = null;
		// assume it's well formed already?
		// this may cause more problems than it solves
		if (testWellFormed) {
			try {
				Element element = XMLUtil.parseQuietlyToDocumentWithoutDTD(inputFile).getRootElement();
				htmlElement = HtmlElement.create(element);
			} catch (Exception e) {
				
			}
		}
		// only do this if not well formed
		if (htmlElement == null && inputFile != null) {
			try {
				htmlElement = htmlFactory.parse(inputFile);
			} catch (Exception e) {
				throw new RuntimeException("Cannot transform HTML "+inputFile, e);
			}
		}
		return htmlElement;
	}

	public HtmlElement getHtmlElement() {
		return htmlElement;
	}

	public boolean isTestWellFormed() {
		return testWellFormed;
	}

	/** initially parse as well-formed and only tidy if not.
	 * this may cause as many problems as it solves.
	 * 
	 * @param testWellFormed
	 */
	public void setTestWellFormed(boolean testWellFormed) {
		this.testWellFormed = testWellFormed;
	}

	/** these next few routines are used to create an xpath and then delete sub elements */
	public HtmlCleaner setTag(String tag, TagPosition position) {
		this.cleanTag = tag;
		this.tagPosition = position;
		return this;
	}

	public HtmlCleaner setContainsAttribute(HtmlClass clazz, String attValue) {
		this.containsAttName = clazz.name;
		this.containsAttValue = attValue;
		return this;
	}
	
	public HtmlCleaner setEqualsAttribute(HtmlClass clazz, String attValue) {
		this.equalsAttName = clazz.name;
		this.equalsAttValue = attValue;
		return this;
	}
	
	public void clean() {
		if (cleanTag == null) {
			throw new RuntimeException("no clean tag given");
		}
		createXPath();
		List<Element> elementList = XMLUtil.getQueryElements(htmlElement, xpath);
		for (Element tagElement : elementList) {
//			LOG.debug("deleted " + tagElement.getLocalName());
			tagElement.detach();
		}
	}

	private void createXPath() {
		xpath = tagPosition.getXPath();
		xpath += "*" + LSQUARE + "local-name()="+quote(cleanTag);
		String condition = null;
		if (equalsAttName != null) {
			condition = AT + equalsAttName + EQUALS + quote(equalsAttValue);
		} else if (containsAttName != null) {
			condition = "contains" + LPAREN + AT + containsAttName + COMMA + quote(containsAttValue) + RPAREN;
		}
		if (condition != null) {
			xpath += SPACE + AND + SPACE + LPAREN + condition + RPAREN;
		}
		xpath += RSQUARE;
	}

	private String quote(String s) {
		return "'" + s + "'";
	}

	


}

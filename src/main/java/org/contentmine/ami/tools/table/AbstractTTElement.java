package org.contentmine.ami.tools.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;
import nu.xom.Node;

public abstract class AbstractTTElement extends Element {
	private static final Logger LOG = Logger.getLogger(AbstractTTElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	
	public static final String FIND = "find";
	public static final String NAME = "name";
	public static final String REGEX = "regex";
	private List<Pattern> findPatternList;
	protected TTemplateList templateList;

	protected AbstractTTElement(String tag, TTemplateList templateList) {
		super(tag);
		this.templateList = templateList;
	}
		
	public static AbstractTTElement create(Element element, TTemplateList templateList) {
		AbstractTTElement ttElement = null;
		String tag = element.getLocalName();
		if (false) {
		} else if(TTCell.TAG.equalsIgnoreCase(tag)) {
			ttElement = new TTCell(templateList);
		} else if(TTColumn.TAG.equalsIgnoreCase(tag)) {
			ttElement = new TTColumn(templateList);
		} else if(TTFile.TAG.equalsIgnoreCase(tag)) {
			ttElement = new TTFile(templateList);
		} else if(TTFooter.TAG.equalsIgnoreCase(tag)) {
			ttElement = new TTFooter(templateList);
		} else if(TTitle.TAG.equalsIgnoreCase(tag)) {
			ttElement = new TTitle(templateList);
		} else if(TTemplate.TAG.equalsIgnoreCase(tag)) {
			ttElement = new TTemplate(templateList);
		} else if(TTemplateList.TAG.equalsIgnoreCase(tag)) {
			if (templateList != null) {
				throw new RuntimeException("can only have one templateList in tree");
			}
			ttElement = new TTemplateList();
			templateList = (TTemplateList)ttElement;
		} else if(TTQuery.TAG.equalsIgnoreCase(tag)) {
			ttElement = new TTQuery(templateList);
		} else if(TTVariable.TAG.equalsIgnoreCase(tag)) {
			ttElement = new TTVariable(templateList);

		} else {
			throw new RuntimeException("Unknown tag "+tag);
		}
		XMLUtil.copyAttributes(element, ttElement);
		for (int i = 0; i < element.getChildCount(); i++) {
			Node child = element.getChild(i);
			if (child instanceof Element) {
				AbstractTTElement tableChild = 
						AbstractTTElement.create((Element)child, templateList);
				if (ttElement != null) {	
					ttElement.appendChild(tableChild);
				}
			} else {
				ttElement.appendChild(child.copy());
			}
		}
		ttElement.finalize();
		return ttElement;
	}

	public List<Pattern> getOrCreateFindPatternList() {
		if (findPatternList == null) {
			findPatternList = getPatternList(this, FIND);
		}
		return findPatternList;
		
	}

	public String getFind() {
		return getNonNullAttribute(this, FIND);
	}

	public String getName() {
		return getNonNullAttribute(this, NAME);
	}

	public String getRegex() {
		return getNonNullAttribute(this, REGEX);
	}

	public Pattern getOrCreatePattern() {
		String regex = getRegex();
		return regex == null ? null : Pattern.compile(regex);
	}

	private String getNonNullAttribute(Element element, String name) {
		String value = element.getAttributeValue(name);
		if (value == null) {
			throw new RuntimeException("require attribute: "+name+" on element: "+element.getLocalName());
		}
		return value;
	}

	public boolean find(String string) {
		List<Pattern> findPatternList = this.getOrCreateFindPatternList();
		for (Pattern findPattern : findPatternList) {
//			LOG.debug("|"+findPattern+"|   "+string);
			if (findPattern.matcher(string).find(0)) {
//				LOG.debug("matched!!!");
				return true;
			}
		}
		return false;
	}

	private static List<Pattern> getPatternList(Element element, String name) {
		List<Pattern> patternList = new ArrayList<>();
		String expr = element.getAttributeValue(name);
		if (expr == null) {
			throw new RuntimeException("title must have '"+name+"' attribute");
		}
		expr = expr.trim().replaceAll("\n", " ").replaceAll("\\s+", " "); 
		String OR_delim = "_OR";
		String[] regexList = expr.split(OR_delim);
		for (String regex : regexList) {
			Pattern pattern = Pattern.compile(regex.trim());
			patternList.add(pattern);
		}
		return patternList;
	}
	
	protected void finalize() {
		// overridable
	}


			
}

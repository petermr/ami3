package org.contentmine.ami.tools.table;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;
import nu.xom.Node;

public abstract class AbstractTableTemplateElement extends Element {

	
	public static final String FIND = "find";
	public static final String NAME = "name";
	public static final String REGEX = "regex";
	private List<Pattern> findPatternList;

	protected AbstractTableTemplateElement(String tag) {
		super(tag);
	}
		
	public static AbstractTableTemplateElement create(Element element) {
		AbstractTableTemplateElement tableElement = null;
		String tag = element.getLocalName();
		if (false) {
		} else if(TableColumnElement.TAG.equalsIgnoreCase(tag)) {
			tableElement = new TableColumnElement();
		} else if(TableTableElement.TAG.equalsIgnoreCase(tag)) {
			tableElement = new TableTableElement();
		} else if(TableTitleElement.TAG.equalsIgnoreCase(tag)) {
			tableElement = new TableTitleElement();
		} else if(TableTemplateElement.TAG.equalsIgnoreCase(tag)) {
			tableElement = new TableTemplateElement();
		} else if(TableTemplateListElement.TAG.equalsIgnoreCase(tag)) {
			tableElement = new TableTemplateListElement();

		} else {
			throw new RuntimeException("Unknown tag "+tag);
		}
		XMLUtil.copyAttributes(element, tableElement);
		for (int i = 0; i < element.getChildCount(); i++) {
			Node child = element.getChild(i);
			if (child instanceof Element) {
				AbstractTableTemplateElement tableChild = 
						AbstractTableTemplateElement.create((Element)child);
				if (tableElement != null) {	
					tableElement.appendChild(tableChild);
				}
			}
		}
		return tableElement;
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
			Pattern pattern = Pattern.compile(regex);
			patternList.add(pattern);
		}
		return patternList;
	}
			
}

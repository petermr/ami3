package org.contentmine.ami.tools.table;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/**
 * extractor from JATS and other tables
 * 
 * <templateList>
	<template name="composition">
		<title regex="composition oil"/>
		<table regex=".* /table_\\d+\\.xml"/>
		<column name="compound" regex="[Cc]onstituent.*|[Cc]ompound.*|[Cc]omponent.*"/>
		<column name="percentage" regex=".*[Pp]ercentage.*|.*Area.*|.*%.*"/>
	</template>
	<template name="activity">
		<title regex="activity target"/>
		<table regex=".* /table_\\d+\\.xml"/>
		<column name="activity" regex="activity.*"/>
		<column name="target" regex=".*target"/>
	</template>
</templateList>

 * @author pm286
 *
 */
public class TTemplateList extends AbstractTTElement {
	private static final Logger LOG = Logger.getLogger(TTemplateList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String COLUMN = "column";
	private static final String TEMPLATE = "template";
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("@[^@]+@");
	public static String TAG = "templateList";

	private File templateFile;
	private List<TTemplate> templateElementList;
	private Map<String, Pattern> patternByNameMap;
	private TTemplate currentTemplateElement;
	private Map<String, String> variableMap;
	
	public TTemplateList() {
		super(TAG, (TTemplateList) null);
	}

	public static TTemplateList getOrCreateTemplateListElement(File templateFile) {
		TTemplateList templateListTemplate = null;
		if (templateFile != null) {
			Element templateListElement = XMLUtil.parseQuietlyToRootElement(templateFile);
			templateListTemplate = (TTemplateList) AbstractTTElement.create(templateListElement, (TTemplateList) null);
			templateListTemplate.setTemplateFile(templateFile);
		}
		return templateListTemplate;
	}
	
	public void setTemplateFile(File templateFile) {
		this.templateFile = templateFile;
	}

	public TTemplate getTemplateFor(String templateName) {
		currentTemplateElement = null;
		if (templateName != null) {
			getOrCreateTemplateElementList();
			if (templateElementList != null) {
				for (Element templateElement :templateElementList) {
					String name = templateElement.getAttributeValue(NAME);
					if (templateName.contentEquals(name)) {
						currentTemplateElement = (TTemplate) templateElement;
					}
				}
			}
		}
		return this.currentTemplateElement;
	}

	public List<TTemplate> getOrCreateTemplateElementList() {
		if (templateElementList == null) {
			List<Element> elementList = XMLUtil.getQueryElements(this, "./*[local-name()='" + TEMPLATE + "']");
			templateElementList = new ArrayList<>(); 
			for (Element element : elementList) {
				templateElementList.add((TTemplate)element);
			}
		}
		return templateElementList;
	}
	
	public List<String> getTemplateNames() {
		List<String> nameList = new ArrayList<>();
		getOrCreateTemplateElementList();
		if (templateElementList != null) {
			for (Element templateElement :templateElementList) {
				String name = templateElement.getAttributeValue(NAME);
				nameList.add(name);
			}
		}
		return nameList;
	}

	public Map<String, String> getOrCreateVariableMap() { 
		if (variableMap == null) {
			variableMap = new HashMap<String, String>();
		}
		return variableMap;
	}

	public String substituteVariables(String content) {
		getOrCreateVariableMap();
		List<String> keys = new ArrayList<String> (variableMap.keySet());
		int start = 0;
		int end = 0;
		StringBuilder sb = new StringBuilder();
		while (true) {
			Matcher matcher = VARIABLE_PATTERN.matcher(content);
			if (!matcher.find(start)) {
				break;
			}
			start = matcher.start();
			sb.append(content.substring(end, start));
			end = matcher.end();
			String varname = content.substring(start, end);
			String newString = varname;
			for (int i = 0; i < keys.size(); i++) {
				String key = keys.get(i);
				if (key.equals(varname)) {
					newString = variableMap.get(key);
					break;
				}
			}
			if (newString.equals(varname)) {
				throw new RuntimeException("Cannot find symbol: "+varname);
			}
			sb.append(newString);
			start = end;
		}
		sb.append(content.substring(start));
		return sb.toString();
	}

//	private Map<String, Pattern> getPatternByNameMap(String templateName) {
//		Element templateElement = getTemplateFor(templateName);
//		List<Element> columnElementList = XMLUtil.getQueryElements(templateElement, "./*[local-name()='"+COLUMN+"']");
//		patternByNameMap = new HashMap<>();
//		for (Element columnElement : columnElementList) {
//			String regex = columnElement.getAttributeValue(REGEX);
//			if (regex == null) {
//				throw new RuntimeException("missing regex attribute "+columnElement.toXML());
//			}
//			Pattern pattern = Pattern.compile(regex);
//			String name = columnElement.getAttributeValue(NAME);
//			if (name == null) {
//				throw new RuntimeException("missing name attribute "+columnElement.toXML());
//			}
//			patternByNameMap.put(name, pattern);
//		}
//		return patternByNameMap;
//	}
}
package org.contentmine.ami.tools.table;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class TableTemplateListElement extends AbstractTableTemplateElement {
	private static final String COLUMN = "column";
	private static final String TEMPLATE = "template";
	private static final Logger LOG = Logger.getLogger(TableTemplateListElement.class);
	public static String TAG = "templateList";
	static {
		LOG.setLevel(Level.DEBUG);
	}

//	private Element templateListElement;
	private File templateFile;
	private List<TableTemplateElement> templateElementList;
	private Map<String, Pattern> patternByNameMap;
	private TableTemplateElement currentTemplateElement;
	
	public TableTemplateListElement() {
		super(TAG);
	}

	public static TableTemplateListElement getOrCreateTemplateListElement(File templateFile) {
		TableTemplateListElement templateListTemplate = null;
		if (templateFile != null) {
			Element templateListElement = XMLUtil.parseQuietlyToRootElement(templateFile);
			templateListTemplate = (TableTemplateListElement) AbstractTableTemplateElement.create(templateListElement);
			templateListTemplate.setTemplateFile(templateFile);
		}
		return templateListTemplate;
	}
	
	public void setTemplateFile(File templateFile) {
		this.templateFile = templateFile;
	}

	public TableTemplateElement getTemplateFor(String templateName) {
		currentTemplateElement = null;
		if (templateName != null) {
			getOrCreateTemplateElementList();
			if (templateElementList != null) {
				for (Element templateElement :templateElementList) {
					String name = templateElement.getAttributeValue(NAME);
					if (templateName.contentEquals(name)) {
						currentTemplateElement = (TableTemplateElement) templateElement;
					}
				}
			}
		}
		return this.currentTemplateElement;
	}

	public List<TableTemplateElement> getOrCreateTemplateElementList() {
		if (templateElementList == null) {
			List<Element> elementList = XMLUtil.getQueryElements(this, "./*[local-name()='" + TEMPLATE + "']");
			templateElementList = new ArrayList<>(); 
			for (Element element : elementList) {
				templateElementList.add((TableTemplateElement)element);
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
package org.contentmine.ami.tools.table;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
	private static final Logger LOG = LogManager.getLogger(TTemplateList.class);
private static final String COLUMN = "column";
	private static final String TEMPLATE = "template";
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("@[^@]+@");
	public static String TAG = "templateList";

	private File templateFile;
	private List<TTemplate> templateElementList;
	private Map<String, String> variableMap;
	private FileMatcher fileMatcher;
	private List<HasQuery> hasQueryDescendantList;
	private List<TQueryTool> queryToolList;
	
	public TTemplateList() {
		super(TAG, (TTemplateList) null);
	}

	public static TTemplateList getOrCreateTemplateListElement(File templateFile) {
		TTemplateList templateList = null;
		if (templateFile != null) {
			Element rawElement = XMLUtil.parseQuietlyToRootElement(templateFile);
//			LOG.debug("EL "+rawElement.toXML());
			templateList = (TTemplateList) AbstractTTElement.create(rawElement, (TTemplateList) null);
			templateList.setTemplateFile(templateFile);
			templateList.getOrCreateVariableMap();
			List<Element> variableList = XMLUtil.getQueryElements(templateList, "./*[local-name()='"+VariableMatcher.TAG+"']");
			for (Element variable : variableList) {
				((VariableMatcher)variable).addToMap();
			}
//			LOG.debug("created variable map: "+templateList.getOrCreateVariableMap());
			List<TQueryTool> queryToolList = templateList.addQueryTools();
			for (TQueryTool queryTool : queryToolList) {
				queryTool.parseQueries();
			}
//			templateList.parseDescendantQueries();
			LOG.debug("read and parsed "+templateFile);
		}
		return templateList;
	}
	
	public void setTemplateFile(File templateFile) {
		this.templateFile = templateFile;
	}

	public TTemplate getTemplateFor(String templateName) {
		getOrCreateTemplateList();
		TTemplate template = null;
		if (templateName != null) {
			for (Element templateElement : templateElementList) {
				String name = templateElement.getAttributeValue(NAME);
				if (templateName.contentEquals(name)) {
					template = (TTemplate) templateElement;
				}
			}
		}
		return template;
	}

	public List<TTemplate> getOrCreateTemplateList() {
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
		getOrCreateTemplateList();
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

	public FileMatcher getOrCreateFileMatcher() {
		if (fileMatcher == null) {
//			fileMatcher = (FileMatcher) XMLUtil.getSingleElement(this, "ancestor-or-self::*[local-name()='"+FileMatcher.TAG+"']");
			fileMatcher = (FileMatcher) XMLUtil.getSingleChild(this, FileMatcher.TAG);
		}
		if (fileMatcher == null) {
			throw new RuntimeException("cannot find <file> in templateList ");
		}
		return fileMatcher;
	}

	public List<TQueryTool> addQueryTools() {
		if (queryToolList == null) {
			queryToolList = new ArrayList<>();
			List<HasQuery> hasQueryList = getOrCreateHasQueryDescendants();
			for (HasQuery hasQuery : hasQueryList) {
				TQueryTool queryTool = hasQuery.getOrCreateQueryTool();
				queryToolList.add(queryTool);
			}
//			LOG.debug("Added queryTools: "+queryToolList.size());
		}
		return queryToolList;
	}

	private List<HasQuery> getOrCreateHasQueryDescendants() {
		if (hasQueryDescendantList == null) {
			hasQueryDescendantList = new ArrayList<>();
			List<Element> descendants = XMLUtil.getQueryElements(this, ".//*");
			for (Element descendant : descendants) {
				if (descendant instanceof HasQuery) {
					hasQueryDescendantList.add((HasQuery) descendant);
				}
			}
		}
		return hasQueryDescendantList;
	}
	
	

}
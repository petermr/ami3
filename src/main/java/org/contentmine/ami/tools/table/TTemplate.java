package org.contentmine.ami.tools.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/**
	<tableTemplate name="composition">
		<title find="
		     [Cc]omposition _OR
			 \\b[Oo]il _OR
			 EO _OR
			 [Pp]ercentage
			 "
			 exclude=""
			 />
		<table regex=".* /table_\\d+\\.xml"/>
		<column name="compound" find="
		    [Cc]onstituent _OR
		    [Cc]ompound _OR
		    [Cc]omponent 
		    "/>
		<column name="percentage" match=".*[Pp]ercentage.*|.*Area.*|.*%.*"/>
	</tableTemplate>
 * @author pm286
 *
 */
public class TTemplate extends AbstractTTElement {

	public static String TAG = "template";
	
	public static String TITLE = "title";
	public static String TABLE = "table";
	public static String COLUMN = "column";

	private TTitle titleElement;
	private TTFile tableElement;
	private List<TTColumn> columnElementList;

	private Map<String, List<Pattern>> columnFindListMap;

		public TTemplate(TTemplateList templateList) {
		super(TAG, templateList);
//		getOrCreateTitleElement();
//		getOrCreateTableElement();
//		getOrCreateColumnElementList();
		
	}

	public List<TTColumn> getOrCreateColumnElementList() {
		if (columnElementList == null) {
			columnElementList = new ArrayList<>();
			List<Element> elementList = XMLUtil.getQueryElements(this, "./*[local-name()='"+TTColumn.TAG+"']");
			for (Element element : elementList) {
				columnElementList.add((TTColumn)element);
			}
		}
		return columnElementList;
	}

	public TTitle getOrCreateTitleElement() {
		if (titleElement == null) {
			titleElement = (TTitle) XMLUtil.getSingleElement(this, "./*[local-name()='"+TTitle.TAG+"']");
		}
		return titleElement;
	}

	public TTFile getOrCreateTableElement() {
		if (tableElement == null) {
			tableElement = (TTFile) XMLUtil.getSingleElement(this, "./*[local-name()='"+TTFile.TAG+"']");
		}
		return tableElement;
	}

	public String getTableRegex() {
		getOrCreateTableElement();
		return tableElement == null ? null : tableElement.getRegex();
	}

	public Map<String, List<Pattern>> getColumnPatternListMap() {
	    if (columnFindListMap == null) {
	    	getOrCreateColumnElementList();
	    	columnFindListMap = new HashMap<>();
	    	for (AbstractTTElement columnElement : columnElementList) {
	    		String find = columnElement.getFind();
	    		String name = columnElement.getName();
//	    		String regex = columnElement.getRegex();
	    		List<Pattern> findPatterns = getPatternList(find);
//	    		Pattern pattern = Pattern.compile(regex);
	    		columnFindListMap.put(name, findPatterns);
	    	}
	    }
	    return columnFindListMap;
	}

	private List<Pattern> getPatternList(String find) {
		List<Pattern> patternList = new ArrayList<>();
		String[] findStrings = find.trim().split("\\s+");
		for (String findString : findStrings) {
			Pattern pattern = Pattern.compile(findString);
			patternList.add(pattern);
		}
		return patternList;
	}
	
	public boolean findTitle(String caption) {
		getOrCreateTitleElement();
		return titleElement.find(caption);
	}

	public List<TTColumn> findColumnList(String header) {
		getOrCreateColumnElementList();
		List<TTColumn> columnList = new ArrayList<>();
		for (TTColumn columnElement : columnElementList) {
			List<Pattern> findPatternList = columnElement.getOrCreateFindPatternList();
			for (Pattern findPattern : findPatternList) {
				if (findPattern.matcher(header).find()) {
					columnList.add(columnElement);
				}
			}
		}
		return columnList;
	}

}

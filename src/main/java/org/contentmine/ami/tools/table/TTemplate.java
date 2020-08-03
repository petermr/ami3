package org.contentmine.ami.tools.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;


//	<tableTemplate name="composition">
//		<title find="
//		     [Cc]omposition _OR
//			 \\b[Oo]il _OR
//			 EO _OR
//			 [Pp]ercentage
//			 "
//			 exclude=""
//			 />
//		<table regex=".* /table_\\d+\\.xml"/>
//		<column name="compound" find="
//		    [Cc]onstituent _OR
//		    [Cc]ompound _OR
//		    [Cc]omponent 
//		    "/>
//		<column name="percentage" match=".*[Pp]ercentage.*|.*Area.*|.*%.*"/>
//	</tableTemplate>
//
	/** @author pm286
 *
 */
public class TTemplate extends AbstractTTElement {
	private static final Logger LOG = LogManager.getLogger(TTemplate.class);
private static final String OR = "OR";

	public static String TAG = "template";
	
	public static String TITLE = "title";
	public static String TABLE = "table";
	public static String COLUMN = "column";

	private HasQuery titleMatcher;
	private List<ColumnMatcher> columnMatcherList;


	public TTemplate(TTemplateList templateList) {
		super(TAG, templateList);
	}

	public List<ColumnMatcher> getOrCreateColumnMatcherList() {
		if (columnMatcherList == null) {
			columnMatcherList = new ArrayList<>();
			List<Element> elementList = XMLUtil.getQueryElements(this, "./*[local-name()='"+ColumnMatcher.TAG+"']");
			for (Element element : elementList) {
				columnMatcherList.add((ColumnMatcher)element);
			}
		}
		return columnMatcherList;
	}

	public HasQuery getTitleMatcher() {
		if (titleMatcher == null) {
			titleMatcher = (TitleMatcher) XMLUtil.getSingleChild(this, TitleMatcher.TAG);
		}
		return titleMatcher;
	}
}

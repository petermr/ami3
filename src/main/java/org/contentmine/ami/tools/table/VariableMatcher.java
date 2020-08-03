package org.contentmine.ami.tools.table;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;


//	<tableTemplate name="composition">
//		<title match="
//		     [Cc]omposition|
//			 \\b[Oo]il
//			 EO
//			 [Pp]ercentage
//			 "
//			 exclude=""
//			 />
//			 
//>>>		<table regex=".* /table_\\d+\\.xml"/> <<<
//		<column name="compound" match="
//		    \\b[Cc]onstituent.*\\b|
//		    \\b[Cc]ompound.*\\b|
//		    \\b[Cc]omponent.*\\b
//		    "/>
//		<column name="percentage" match=".*[Pp]ercentage.*|.*Area.*|.*%.*"/>
//	</tableTemplate>
//	
/**
 * @author pm286
 *
 */
public class VariableMatcher extends AbstractTTElement {
	private static final Logger LOG = LogManager.getLogger(VariableMatcher.class);
public static String TAG = "variable";
	private static final String NAME = "name";
	
	public VariableMatcher(TTemplateList templateList) {
		super(TAG, templateList);
	}
	
	public void addToMap() {
		String value = this.getValue();
//		LOG.debug("adding variable: "+value);
		String substitutedContent = templateList.substituteVariables(value);
		XMLUtil.setXMLContent(this, substitutedContent);
		String name = this.getAttributeValue(NAME);
		if (name != null && substitutedContent != null) {
			templateList.getOrCreateVariableMap().put(name, substitutedContent);
		}
	}

}

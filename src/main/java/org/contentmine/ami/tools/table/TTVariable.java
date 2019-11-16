package org.contentmine.ami.tools.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

/**
	<tableTemplate name="composition">
		<title match="
		     [Cc]omposition|
			 \\b[Oo]il
			 EO
			 [Pp]ercentage
			 "
			 exclude=""
			 />
			 
>>>		<table regex=".* /table_\\d+\\.xml"/> <<<
		<column name="compound" match="
		    \\b[Cc]onstituent.*\\b|
		    \\b[Cc]ompound.*\\b|
		    \\b[Cc]omponent.*\\b
		    "/>
		<column name="percentage" match=".*[Pp]ercentage.*|.*Area.*|.*%.*"/>
	</tableTemplate>
 * @author pm286
 *
 */
public class TTVariable extends AbstractTTElement {
	private static final Logger LOG = Logger.getLogger(TTVariable.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String TAG = "variable";
	private static final String NAME = "name";
	
	public TTVariable(TTemplateList templateList) {
		super(TAG, templateList);
	}
	
	void addToMap() {
		String name = this.getAttributeValue(NAME);
		String content = this.getValue();
		String content1 = templateList.substituteVariables(content);
		XMLUtil.setXMLContent(this, content1);
//		LOG.debug("name => "+content1);
		if (name != null && content1 != null) {
			templateList.getOrCreateVariableMap().put(name, content1);
		}
	}

	@Override
	protected void finalize() {
		this.addToMap();
	}


}

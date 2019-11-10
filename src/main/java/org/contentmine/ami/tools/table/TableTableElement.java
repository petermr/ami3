package org.contentmine.ami.tools.table;

import java.util.List;
import java.util.regex.Pattern;

import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

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
public class TableTableElement extends AbstractTableTemplateElement {

	public static String TAG = "table";
	
	public TableTableElement() {
		super(TAG);
	}

	public String getRegex() {
		return this.getAttributeValue(REGEX);
	}


}

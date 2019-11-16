package org.contentmine.ami.tools.table;

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
public class TTFile extends AbstractTTElement {

	public static String TAG = "file";
	private TTQueryTool queryTool;
	
	public TTFile(TTemplateList templateList) {
		super(TAG, templateList);
	}


}

package org.contentmine.ami.tools.table;

/**
	<tableTemplate name="composition">
		<title regex="
		     [Cc]omposition|
			 \\b[Oo]il
			 EO
			 [Pp]ercentage
			 "
			 exclude=""
			 />
		<table regex=".* /table_\\d+\\.xml"/>
>>>
		<column name="compound" regex="
		    \\b[Cc]onstituent.*\\b|
		    \\b[Cc]ompound.*\\b|
		    \\b[Cc]omponent.*\\b
		    "/>
		<column name="percentage" regex=".*[Pp]ercentage.*|.*Area.*|.*%.*"/>
		<<<
	</tableTemplate>
 * @author pm286
 *
 */
public class TableColumnElement extends AbstractTableTemplateElement {

	public static String TAG = "column";
	
	public TableColumnElement() {
		super(TAG);		
	}
	

}

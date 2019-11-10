package org.contentmine.ami.tools.table;

/**
	<tableTemplate name="composition">
	>>>
		<title find="
		     [Cc]omposition _OR
			 [Oo]il _OR
			 EO _OR
			 "
			 exclude=""
			 />
			  <<<
		<table regex=".* /table_\\d+\\.xml"/>
		<column name="compound" match="
		    [Cc]onstituent _OR
		    [Cc]ompound _OR
		    [Cc]omponent _OR
		    "/>
		<column name="percentage" match=".*[Pp]ercentage.*|.*Area.*|.*%.*"/>
	</tableTemplate>
 * @author pm286
 *
 */
public class TableTitleElement extends AbstractTableTemplateElement {

	public static String TAG = "title";
	public TableTitleElement() {
		super(TAG);
	}

}

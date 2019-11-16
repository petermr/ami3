package org.contentmine.ami.tools.table;

/**
	<tableTemplate name="composition">
		<column name="compound" find="
		    [Cc]onstituent _OR
		    [Cc]ompound _OR
		    [Cc]omponent
		    "/>
		<column name="percentage" find="
		[Pp]ercentage _OR
		Area _OR
		%
		"/>
	</tableTemplate>
 * @author pm286
 *
 */
public class TTColumn extends AbstractTTElement {

	public static String TAG = "column";
	
	public TTColumn(TTemplateList templateList) {
		super(TAG, templateList);
	}
	

}

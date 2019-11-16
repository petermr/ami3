package org.contentmine.ami.tools.table;

/**
	<tableTemplate name="composition">
		<title find="
		     composition OR
			 oil OR
			 EO OR
			 "
			 >
		</title>
	</tableTemplate>
 * @author pm286
 *
 */
public class TTCell extends AbstractTTElement {

	public static String TAG = "cell";
	public TTCell(TTemplateList templateList) {
		super(TAG, templateList);
	}

}

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
public class TTFooter extends AbstractTTElement {

	public static String TAG = "footer";
	public TTFooter(TTemplateList templateList) {
		super(TAG, templateList);

	}

}

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
public class TTitle extends AbstractTTElement {

	public static String TAG = "title";
	public TTitle(TTemplateList templateList) {
		super(TAG, templateList);
	}

}

package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSMonthElement extends JATSElement implements IsInline {

	public static String TAG = "month";

	public JATSMonthElement(Element element) {
		super(element);
	}
	
	public String debugString(int level) {
		return "m:"+getValue();
	}
	

}

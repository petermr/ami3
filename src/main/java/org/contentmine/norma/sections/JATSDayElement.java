package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSDayElement extends JATSElement implements IsInline  {

	public static String TAG = "day";

	public JATSDayElement(Element element) {
		super(element);
	}
	
	public String debugString(int level) {
		return "d:"+getValue();
	}
	
	

}

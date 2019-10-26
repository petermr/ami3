package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSSourceElement extends JATSElement implements IsInline {

	public static String TAG = "source";

	public JATSSourceElement(Element element) {
		super(element);
	}
	
	public String debugString(int level) {
		return "s: "+this.getValue();
	}
	


}

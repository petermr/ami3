package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSListElement extends JATSElement implements IsBlock {

	public static String TAG = "list";

	public JATSListElement(Element element) {
		super(element);
	}
	
	
	

}

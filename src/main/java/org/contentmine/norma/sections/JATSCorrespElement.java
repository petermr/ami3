package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSCorrespElement extends JATSElement implements IsBlock, IsNonStandard {

	public static String TAG = "corresp";

	public JATSCorrespElement(Element element) {
		super(element);
	}
	
	
	

}

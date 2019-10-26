package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSMmlMathElement extends AbstractJATSMathmlElement implements IsBlock {

	public static String TAG = "mml:math";

	public JATSMmlMathElement(Element element) {
		super(element);
	}
	
	
	

}

package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSTexMathElement extends JATSElement implements IsBlock {

	public static String TAG = "tex-math";

	public JATSTexMathElement(Element element) {
		super(element);
	}
	
	
	

}

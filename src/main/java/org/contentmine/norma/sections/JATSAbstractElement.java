package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSAbstractElement extends JATSElement {

	public static String TAG = "abstract";

	public JATSAbstractElement(Element element) {
		super(element);
	}
	
	public static boolean matches(Element element) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}

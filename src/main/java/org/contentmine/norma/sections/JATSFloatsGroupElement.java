package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSFloatsGroupElement extends JATSElement implements IsBlock , HasDirectory {

	public static String TAG = "floats-group";

	public JATSFloatsGroupElement(Element element) {
		super(element);
	}

	public JATSFloatsGroupElement() {
		super(TAG);
	}

	public String directoryName() {
		return this.TAG;
	}


}

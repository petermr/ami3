package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSFigGroupElement extends JATSElement implements IsBlock, IsFloat {

	public static String TAG = "fig-group";

	public JATSFigGroupElement(Element element) {
		super(element);
	}

	@Override
	public String directoryName() {
		return TAG;
	}
	
	
	

}

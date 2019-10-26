package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSSupplementaryMaterialElement extends JATSElement implements IsBlock, IsFloat {

	public static String TAG = "supplementary-material";

	public JATSSupplementaryMaterialElement(Element element) {
		super(element);
	}

	@Override
	public String directoryName() {
		return TAG;
	}
	
	
	

}

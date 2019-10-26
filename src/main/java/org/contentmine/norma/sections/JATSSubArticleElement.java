package org.contentmine.norma.sections;

import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSSubArticleElement extends JATSElement implements IsBlock , HasDirectory {

	public static String TAG = "sub-article";

	public JATSSubArticleElement(Element element) {
		super(element);
	}

	@Override
	public String directoryName() {
		return TAG;
	}
	
	
	

}

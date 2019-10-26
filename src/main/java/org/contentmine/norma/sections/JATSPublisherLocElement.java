package org.contentmine.norma.sections;

import nu.xom.Element;

public class JATSPublisherLocElement extends JATSElement implements IsInline {

	static String TAG = "publisher-loc";

	public JATSPublisherLocElement(Element element) {
		super(element);
	}

	public String debugString(int level) {
//		return "y: "+this.getValue();
		return "";
	}
	

}

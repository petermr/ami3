package org.contentmine.norma.sections;

import nu.xom.Element;

public class JATSPublisherNameElement extends JATSElement implements IsInline {

	static String TAG = "publisher-name";

	public JATSPublisherNameElement(Element element) {
		super(element);
	}

	public String debugString(int level) {
		return "p: "+this.getValue();
	}

}

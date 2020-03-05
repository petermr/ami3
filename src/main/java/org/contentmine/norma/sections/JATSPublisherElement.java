package org.contentmine.norma.sections;

import nu.xom.Element;

public class JATSPublisherElement extends JATSElement implements IsBlock {

	static String TAG = "publisher";

	public JATSPublisherElement() {
		super(TAG);
	}

	public JATSPublisherElement(Element element) {
		super(element);
	}


}

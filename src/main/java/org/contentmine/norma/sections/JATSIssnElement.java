package org.contentmine.norma.sections;

import nu.xom.Element;

public class JATSIssnElement extends JATSElement implements IsInline {

	static String TAG = "issn";

	public JATSIssnElement() {
		super(TAG);
	}

	public JATSIssnElement(Element element) {
		super(element);
	}

}

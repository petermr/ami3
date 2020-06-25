package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Element;

public class JATSSurnameElement extends JATSElement implements IsInline {

	private static final Logger LOG = LogManager.getLogger(JATSSurnameElement.class);
static final String TAG = "surname";

	public JATSSurnameElement(Element element) {
		super(element);
	}

}

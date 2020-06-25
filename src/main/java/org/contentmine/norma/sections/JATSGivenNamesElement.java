package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Element;

public class JATSGivenNamesElement extends JATSElement implements IsBlock {

	private static final Logger LOG = LogManager.getLogger(JATSGivenNamesElement.class);
static final String TAG = "given-names";

	public JATSGivenNamesElement(Element element) {
		super(element);
	}


}

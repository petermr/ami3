package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Element;

public class JATSAbbrevElement extends JATSElement {
	private static final Logger LOG = LogManager.getLogger(JATSAbbrevElement.class);
public static String TAG = "abbrev";

	public JATSAbbrevElement(Element element) {
		super(element);
	}


}

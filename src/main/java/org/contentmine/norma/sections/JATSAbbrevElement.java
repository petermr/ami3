package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

public class JATSAbbrevElement extends JATSElement {
	private static final Logger LOG = Logger.getLogger(JATSAbbrevElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
    
	public static String TAG = "abbrev";

	public JATSAbbrevElement(Element element) {
		super(element);
	}


}

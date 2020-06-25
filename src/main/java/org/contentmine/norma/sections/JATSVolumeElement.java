package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Element;

public class JATSVolumeElement extends JATSElement implements IsInline {

	private static final Logger LOG = LogManager.getLogger(JATSVolumeElement.class);
static final String TAG = "volume";

	public JATSVolumeElement(Element element) {
		super(element);
	}

	public static boolean matches(Element element) {
		if (element.getLocalName().equals(TAG)) {
			LOG.trace("VOL "+element.toXML());
			return true;
		}
		return false;
	}
	
	

}

package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

/**
 * 
 * @author pm286
 *
 *
 *Container element used to hold a group of processing alternatives, for example, a single <graphic> 
 *that ships in several formats (tif, gif, and jpeg) or in different resolutions. This element 
 *is a physical grouping to contain multiple logically equivalent (substitutable) versions of the 
 *same information object. Typically these are processing alternatives, and the reader is expected 
 *to see only one version of the object.
 */
public class JATSAlternativesElement extends JATSElement implements IsBlock {

	private static final Logger LOG = Logger.getLogger(JATSAlternativesElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	static final String TAG = "alternatives";

	public JATSAlternativesElement(Element element) {
		super(element);
	}

	

}

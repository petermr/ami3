package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

public class JATSTitleElement extends JATSElement implements IsInline {

	private static final Logger LOG = Logger.getLogger(JATSTitleElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	static final String TAG = "title";

	public JATSTitleElement(Element element) {
		super(element);
	}

	public JATSTitleElement() {
		super(TAG);
	}

	public JATSTitleElement(String value) {
		this();
		this.appendChild(value);
	}

	

}

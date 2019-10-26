package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

public class JATSArticleTitleElement extends JATSElement implements IsBlock {

	private static final Logger LOG = Logger.getLogger(JATSArticleTitleElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	static final String TAG = "article-title";

	public JATSArticleTitleElement(Element element) {
		super(element);
	}

	public String debugString(int level) {
		return "t: "+this.getValue();
	}


}

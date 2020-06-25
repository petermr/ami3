package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Element;

public class JATSArticleTitleElement extends JATSElement implements IsBlock {

	private static final Logger LOG = LogManager.getLogger(JATSArticleTitleElement.class);
static final String TAG = "article-title";

	public JATSArticleTitleElement() {
		super(TAG);
	}

	public JATSArticleTitleElement(Element element) {
		super(element);
	}

	public String debugString(int level) {
		return "t: "+this.getValue();
	}

	public JATSElement appendText(String content) {
		this.appendChild(content);
		return this;
	}


}

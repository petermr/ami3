package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTitle;

import nu.xom.Element;

public class JATSTitleElement extends JATSElement implements IsInline {

	private static final Logger LOG = LogManager.getLogger(JATSTitleElement.class);
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

	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlTitle());
	}

	

}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlLi;

import nu.xom.Element;

public class JATSListItemElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSListItemElement.class);
public static String TAG = "list-item";

    public JATSListItemElement(Element element) {
        super(element);
    }
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		HtmlElement li = new HtmlLi();
		li.setClassAttribute(TAG);
		return deepCopyAndTransform(li);
	}


}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTfoot;

import nu.xom.Element;

public class JATSTfootElement extends AbstractJATSHtmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSTfootElement.class);
public static String TAG = "tfoot";

    public JATSTfootElement(Element element) {
        super(element);
    }
    
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlTfoot());
	}


}

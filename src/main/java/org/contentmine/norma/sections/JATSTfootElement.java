package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTfoot;

import nu.xom.Element;

public class JATSTfootElement extends AbstractJATSHtmlElement {
    private static final Logger LOG = Logger.getLogger(JATSTfootElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

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

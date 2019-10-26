package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

/** converter to HTML from JATSElements of same tag name
 * 
 * all HTML-like elements in JATS should subclass this
 * 
 * @author pm286
 *
 */
public class AbstractJATSHtmlElement extends JATSElement implements IsHtml {
    private static final Logger LOG = Logger.getLogger(AbstractJATSHtmlElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public AbstractJATSHtmlElement(Element element) {
        super(element);
    }
    
    /** creates properly subclassed element
     * e.g. JATS <td> will create HtmlTdElement
     */
    public HtmlElement toHtml() {
    	return HtmlElement.create(this);
    	
    }

}

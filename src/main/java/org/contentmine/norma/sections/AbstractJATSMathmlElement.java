package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

/** converter to Mathml from JATSElements of same tag name
 * 
 * all MathML-like elements in JATS should subclass this
 * 
 * @author pm286
 *
 */
public class AbstractJATSMathmlElement extends JATSElement implements IsMath {
    private static final Logger LOG = LogManager.getLogger(AbstractJATSMathmlElement.class);
public AbstractJATSMathmlElement(Element element) {
        super(element);
    }
    
    /** creates properly subclassed element
     * e.g. JATS <td> will create HtmlTdElement
     * CURRENTLY NO-OP, returns NULL
     */
    public HtmlElement toMathml() {
    	return null;
    }

}

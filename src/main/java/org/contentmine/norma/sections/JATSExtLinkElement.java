package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

/**
    	<ext-link ext-link-type="gen"
    			xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="Y18883"/>)
 * @author pm286
 *
 */
public class JATSExtLinkElement extends JATSElement implements IsInline {
	private static final Logger LOG = Logger.getLogger(JATSExtLinkElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "ext-link";
    public static String XLINK = "http://www.w3.org/1999/xlink";
    private static final String EXT_LINK_TYPE = "ext-link-type";
    
    public JATSExtLinkElement() {
        super(TAG);
    }

    public JATSExtLinkElement(Element element) {
        super(element);
    }

	public JATSExtLinkElement setExtLinkType(String type) {
		this.addAttribute(new Attribute(EXT_LINK_TYPE, type));
		return this;
	}
	
	public JATSExtLinkElement setHref(String target) {
		this.addAttribute(new Attribute("xlink:href", XLINK, target));
		return this;
		
	}
	
    
}

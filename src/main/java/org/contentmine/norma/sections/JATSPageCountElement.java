package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * <page-count count="6"/>
 * 
 * @author pm286
 *
 */
public class JATSPageCountElement extends JATSElement {
	private static final Logger LOG = Logger.getLogger(JATSPageCountElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "page-count";
    public static final String COUNT = "count";

    public JATSPageCountElement() {
        super(TAG);
    }

    public JATSPageCountElement(Element element) {
        super(element);
    }

	public JATSElement setCount(int npages) {
		this.addAttribute(new Attribute(COUNT, String.valueOf(npages)));
		return this;
	}

	public JATSElement setCount(String countS) {
		try {
			int pagecount = Integer.parseInt(countS);
			setCount(pagecount);
		} catch (NumberFormatException nfe) {
			throw new RuntimeException("count not integer: " + countS);
		}
		return this;
	}
	
	/**
	 * 
	 * @return null if absent or unparsable as integer
	 */
	public Integer getCount() {
		String countS = this.getAttributeValue(COUNT);
		Integer count = null;
		try {
			count = Integer.parseInt(countS);
		} catch (NumberFormatException nfe) {
			throw new RuntimeException("count not integer: " + countS);
		}
		return count;
	}
}

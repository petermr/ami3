package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSEmailElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSEmailElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "email";

    public JATSEmailElement() {
        super(TAG);
    }

    public JATSEmailElement(String text) {
    	this();
    	this.appendText(text);
    }

    public JATSEmailElement(Element element) {
        super(element);
    }
}

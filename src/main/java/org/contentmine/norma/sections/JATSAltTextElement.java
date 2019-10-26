package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSAltTextElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSAltTextElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "alt-text";

    public JATSAltTextElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSKwdElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSKwdElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "kwd";

    public JATSKwdElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSCopyrightHolderElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSCopyrightHolderElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "copyright-holder";

    public JATSCopyrightHolderElement(Element element) {
        super(element);
    }
}

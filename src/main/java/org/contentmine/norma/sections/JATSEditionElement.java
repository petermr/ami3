package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSEditionElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSEditionElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "edition";

    public JATSEditionElement(Element element) {
        super(element);
    }
}

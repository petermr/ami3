package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSPartTitleElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSPartTitleElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "part-title";

    public JATSPartTitleElement(Element element) {
        super(element);
    }
}

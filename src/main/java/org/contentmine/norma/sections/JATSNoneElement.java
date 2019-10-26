package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSNoneElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSNoneElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "none";

    public JATSNoneElement(Element element) {
        super(element);
    }
}

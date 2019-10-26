package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSIsbnElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSIsbnElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "isbn";

    public JATSIsbnElement(Element element) {
        super(element);
    }
}

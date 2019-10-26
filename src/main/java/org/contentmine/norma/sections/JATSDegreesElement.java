package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSDegreesElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSDegreesElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "degrees";

    public JATSDegreesElement(Element element) {
        super(element);
    }
}

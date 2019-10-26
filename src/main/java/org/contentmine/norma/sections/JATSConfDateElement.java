package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSConfDateElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSConfDateElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "conf-date";

    public JATSConfDateElement(Element element) {
        super(element);
    }
}

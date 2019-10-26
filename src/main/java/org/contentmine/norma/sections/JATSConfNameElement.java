package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSConfNameElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSConfNameElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "conf-name";

    public JATSConfNameElement(Element element) {
        super(element);
    }
}

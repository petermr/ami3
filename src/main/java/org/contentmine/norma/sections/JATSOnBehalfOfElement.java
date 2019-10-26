package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSOnBehalfOfElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSOnBehalfOfElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "on-behalf-of";

    public JATSOnBehalfOfElement(Element element) {
        super(element);
    }
}

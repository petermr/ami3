package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSAnonymousElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSAnonymousElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "anonymous";

    public JATSAnonymousElement(Element element) {
        super(element);
    }
}

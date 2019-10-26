package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSScElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSScElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "sc";

    public JATSScElement(Element element) {
        super(element);
    }
}

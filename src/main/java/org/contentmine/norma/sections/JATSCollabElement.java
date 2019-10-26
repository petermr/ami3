package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSCollabElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSCollabElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "collab";

    public JATSCollabElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSRelatedObjectElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSRelatedObjectElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "related-object";

    public JATSRelatedObjectElement(Element element) {
        super(element);
    }
}

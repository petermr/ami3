package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSAwardGroupElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSAwardGroupElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "award-group";

    public JATSAwardGroupElement(Element element) {
        super(element);
    }
}

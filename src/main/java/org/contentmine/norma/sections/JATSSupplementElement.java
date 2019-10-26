package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSSupplementElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSSupplementElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "supplement";

    public JATSSupplementElement(Element element) {
        super(element);
    }
}

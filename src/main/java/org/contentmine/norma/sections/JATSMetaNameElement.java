package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMetaNameElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSMetaNameElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "meta-name";

    public JATSMetaNameElement(Element element) {
        super(element);
    }
}

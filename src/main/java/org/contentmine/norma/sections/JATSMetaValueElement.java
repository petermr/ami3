package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMetaValueElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSMetaValueElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "meta-value";

    public JATSMetaValueElement(Element element) {
        super(element);
    }
}

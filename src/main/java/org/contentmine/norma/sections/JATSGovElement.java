package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSGovElement extends JATSElement implements IsInline {
    private static final Logger LOG = Logger.getLogger(JATSGovElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "gov";

    public JATSGovElement(Element element) {
        super(element);
    }
}

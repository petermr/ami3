package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSSecMetaElement extends JATSElement implements IsBlock {
    private static final Logger LOG = Logger.getLogger(JATSSecMetaElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "sec-meta";

    public JATSSecMetaElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSTransAbstractElement extends JATSElement implements IsBlock {
    private static final Logger LOG = Logger.getLogger(JATSTransAbstractElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "trans-abstract";

    public JATSTransAbstractElement(Element element) {
        super(element);
    }
}

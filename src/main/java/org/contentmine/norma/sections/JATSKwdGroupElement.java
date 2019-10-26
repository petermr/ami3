package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSKwdGroupElement extends JATSElement implements IsBlock {
    private static final Logger LOG = Logger.getLogger(JATSKwdGroupElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "kwd-group";

    public JATSKwdGroupElement(Element element) {
        super(element);
    }
}

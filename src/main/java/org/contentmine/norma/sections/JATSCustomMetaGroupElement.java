package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSCustomMetaGroupElement extends JATSElement implements IsBlock {
    private static final Logger LOG = Logger.getLogger(JATSCustomMetaGroupElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "custom-meta-group";

    public JATSCustomMetaGroupElement(Element element) {
        super(element);
    }
}

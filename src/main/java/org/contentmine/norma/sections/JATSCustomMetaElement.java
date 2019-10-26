package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSCustomMetaElement extends JATSElement implements IsBlock {
    private static final Logger LOG = Logger.getLogger(JATSCustomMetaElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "custom-meta";

    public JATSCustomMetaElement(Element element) {
        super(element);
    }
}

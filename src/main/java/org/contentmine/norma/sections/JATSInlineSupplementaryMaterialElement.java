package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSInlineSupplementaryMaterialElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSInlineSupplementaryMaterialElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "inline-supplementary-material";

    public JATSInlineSupplementaryMaterialElement(Element element) {
        super(element);
    }
}

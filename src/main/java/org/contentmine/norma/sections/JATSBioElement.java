package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSBioElement extends JATSElement implements IsBlock {
    private static final Logger LOG = Logger.getLogger(JATSBioElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "bio";

    public JATSBioElement(Element element) {
        super(element);
    }
}

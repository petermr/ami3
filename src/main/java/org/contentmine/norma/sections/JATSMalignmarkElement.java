package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMalignmarkElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMalignmarkElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "malignmark";

    public JATSMalignmarkElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMaligngroupElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMaligngroupElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "maligngroup";

    public JATSMaligngroupElement(Element element) {
        super(element);
    }
}

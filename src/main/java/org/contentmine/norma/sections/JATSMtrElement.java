package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMtrElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMtrElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mtr";

    public JATSMtrElement(Element element) {
        super(element);
    }
}

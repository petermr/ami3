package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMrootElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = Logger.getLogger(JATSMrootElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mroot";

    public JATSMrootElement(Element element) {
        super(element);
    }
}

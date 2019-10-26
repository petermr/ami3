package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSEquationCountElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSEquationCountElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "equation-count";

    public JATSEquationCountElement(Element element) {
        super(element);
    }
}

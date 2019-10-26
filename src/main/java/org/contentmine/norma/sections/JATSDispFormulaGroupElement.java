package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSDispFormulaGroupElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSDispFormulaGroupElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "disp-formula-group";

    public JATSDispFormulaGroupElement(Element element) {
        super(element);
    }
}

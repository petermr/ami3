package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSDispFormulaElement extends JATSElement implements IsInline {
    private static final Logger LOG = Logger.getLogger(JATSDispFormulaElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "disp-formula";

    public JATSDispFormulaElement(Element element) {
        super(element);
    }
}

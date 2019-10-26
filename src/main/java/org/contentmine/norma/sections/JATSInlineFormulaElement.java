package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSInlineFormulaElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSInlineFormulaElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "inline-formula";

    public JATSInlineFormulaElement(Element element) {
        super(element);
    }
}

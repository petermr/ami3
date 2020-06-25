package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSDispFormulaElement extends JATSElement implements IsInline {
    private static final Logger LOG = LogManager.getLogger(JATSDispFormulaElement.class);
public static String TAG = "disp-formula";

    public JATSDispFormulaElement(Element element) {
        super(element);
    }
}

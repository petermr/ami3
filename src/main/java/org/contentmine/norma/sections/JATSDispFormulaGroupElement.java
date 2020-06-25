package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSDispFormulaGroupElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSDispFormulaGroupElement.class);
public static String TAG = "disp-formula-group";

    public JATSDispFormulaGroupElement(Element element) {
        super(element);
    }
}

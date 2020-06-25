package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSEquationCountElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSEquationCountElement.class);
public static String TAG = "equation-count";

    public JATSEquationCountElement(Element element) {
        super(element);
    }
}

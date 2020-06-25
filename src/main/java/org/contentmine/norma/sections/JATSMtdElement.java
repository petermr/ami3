package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMtdElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMtdElement.class);
public static String TAG = "mtd";

    public JATSMtdElement(Element element) {
        super(element);
    }
}

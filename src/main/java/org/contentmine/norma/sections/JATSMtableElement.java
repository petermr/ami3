package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMtableElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMtableElement.class);
public static String TAG = "mtable";

    public JATSMtableElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMfencedElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMfencedElement.class);
public static String TAG = "mfenced";

    public JATSMfencedElement(Element element) {
        super(element);
    }
}

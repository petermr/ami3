package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMfracElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMfracElement.class);
public static String TAG = "mfrac";

    public JATSMfracElement(Element element) {
        super(element);
    }
}

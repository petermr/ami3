package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMoElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMoElement.class);
public static String TAG = "mo";

    public JATSMoElement(Element element) {
        super(element);
    }
}

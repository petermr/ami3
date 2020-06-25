package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMpaddedElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMpaddedElement.class);
public static String TAG = "mpadded";

    public JATSMpaddedElement(Element element) {
        super(element);
    }
}

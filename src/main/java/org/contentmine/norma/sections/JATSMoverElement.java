package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMoverElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMoverElement.class);
public static String TAG = "mover";

    public JATSMoverElement(Element element) {
        super(element);
    }
}

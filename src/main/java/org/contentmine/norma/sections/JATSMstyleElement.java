package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMstyleElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMstyleElement.class);
public static String TAG = "mstyle";

    public JATSMstyleElement(Element element) {
        super(element);
    }
}

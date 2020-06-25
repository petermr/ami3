package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMsqrtElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMsqrtElement.class);
public static String TAG = "msqrt";

    public JATSMsqrtElement(Element element) {
        super(element);
    }
}

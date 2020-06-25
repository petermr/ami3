package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMsupElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMsupElement.class);
public static String TAG = "msup";

    public JATSMsupElement(Element element) {
        super(element);
    }
}

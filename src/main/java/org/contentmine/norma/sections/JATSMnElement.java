package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMnElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMnElement.class);
public static String TAG = "mn";

    public JATSMnElement(Element element) {
        super(element);
    }
}

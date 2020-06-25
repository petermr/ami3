package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMtextElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMtextElement.class);
public static String TAG = "mtext";

    public JATSMtextElement(Element element) {
        super(element);
    }
}

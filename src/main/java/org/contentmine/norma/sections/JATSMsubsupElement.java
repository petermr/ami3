package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMsubsupElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMsubsupElement.class);
public static String TAG = "msubsup";

    public JATSMsubsupElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMspaceElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMspaceElement.class);
public static String TAG = "mspace";

    public JATSMspaceElement(Element element) {
        super(element);
    }
}

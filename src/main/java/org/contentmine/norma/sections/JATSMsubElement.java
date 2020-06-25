package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMsubElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMsubElement.class);
public static String TAG = "msub";

    public JATSMsubElement(Element element) {
        super(element);
    }
}

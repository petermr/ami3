package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMunderElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMunderElement.class);
public static String TAG = "munder";

    public JATSMunderElement(Element element) {
        super(element);
    }
}

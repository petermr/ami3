package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMrowElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMrowElement.class);
public static String TAG = "mrow";

    public JATSMrowElement(Element element) {
        super(element);
    }
}

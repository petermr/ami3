package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMunderoverElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMunderoverElement.class);
public static String TAG = "munderover";

    public JATSMunderoverElement(Element element) {
        super(element);
    }
}

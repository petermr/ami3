package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMrootElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMrootElement.class);
public static String TAG = "mroot";

    public JATSMrootElement(Element element) {
        super(element);
    }
}

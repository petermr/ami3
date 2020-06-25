package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMtrElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMtrElement.class);
public static String TAG = "mtr";

    public JATSMtrElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSTransSourceElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSTransSourceElement.class);
public static String TAG = "trans-source";

    public JATSTransSourceElement(Element element) {
        super(element);
    }
}

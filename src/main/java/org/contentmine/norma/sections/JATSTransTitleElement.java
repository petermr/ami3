package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSTransTitleElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSTransTitleElement.class);
public static String TAG = "trans-title";

    public JATSTransTitleElement(Element element) {
        super(element);
    }
}

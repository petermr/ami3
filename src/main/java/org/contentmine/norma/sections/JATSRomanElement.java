package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSRomanElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSRomanElement.class);
public static String TAG = "roman";

    public JATSRomanElement(Element element) {
        super(element);
    }
}

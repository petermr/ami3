package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSAwardIdElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSAwardIdElement.class);
public static String TAG = "award-id";

    public JATSAwardIdElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSDefItemElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSDefItemElement.class);
public static String TAG = "def-item";

    public JATSDefItemElement(Element element) {
        super(element);
    }
}

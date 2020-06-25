package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSSizeElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSSizeElement.class);
public static String TAG = "size";

    public JATSSizeElement(Element element) {
        super(element);
    }
}

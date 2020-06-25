package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSOnBehalfOfElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSOnBehalfOfElement.class);
public static String TAG = "on-behalf-of";

    public JATSOnBehalfOfElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSNamedContentElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSNamedContentElement.class);
public static String TAG = "named-content";

    public JATSNamedContentElement(Element element) {
        super(element);
    }
}

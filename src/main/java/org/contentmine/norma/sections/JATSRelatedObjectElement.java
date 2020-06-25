package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSRelatedObjectElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSRelatedObjectElement.class);
public static String TAG = "related-object";

    public JATSRelatedObjectElement(Element element) {
        super(element);
    }
}

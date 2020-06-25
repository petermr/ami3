package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSStateElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSStateElement.class);
public static String TAG = "state";

    public JATSStateElement(Element element) {
        super(element);
    }
}

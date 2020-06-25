package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSDegreesElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSDegreesElement.class);
public static String TAG = "degrees";

    public JATSDegreesElement(Element element) {
        super(element);
    }
}

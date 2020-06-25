package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSTermElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSTermElement.class);
public static String TAG = "term";

    public JATSTermElement(Element element) {
        super(element);
    }
}

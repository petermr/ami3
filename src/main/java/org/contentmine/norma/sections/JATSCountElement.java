package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSCountElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSCountElement.class);
public static String TAG = "count";

    public JATSCountElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSTableCountElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSTableCountElement.class);
public static String TAG = "table-count";

    public JATSTableCountElement(Element element) {
        super(element);
    }
}

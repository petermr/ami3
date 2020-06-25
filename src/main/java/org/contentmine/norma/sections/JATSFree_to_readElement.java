package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSFree_to_readElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSFree_to_readElement.class);
public static String TAG = "free_to_read";

    public JATSFree_to_readElement(Element element) {
        super(element);
    }
}

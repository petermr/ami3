package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSRefCountElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSRefCountElement.class);
public static String TAG = "ref-count";

    public JATSRefCountElement(Element element) {
        super(element);
    }
}

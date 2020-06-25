package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSWordCountElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSWordCountElement.class);
public static String TAG = "word-count";

    public JATSWordCountElement(Element element) {
        super(element);
    }
}

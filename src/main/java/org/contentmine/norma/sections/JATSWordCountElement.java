package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSWordCountElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSWordCountElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "word-count";

    public JATSWordCountElement(Element element) {
        super(element);
    }
}

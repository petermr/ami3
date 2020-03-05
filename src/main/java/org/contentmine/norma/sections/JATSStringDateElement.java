package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSStringDateElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSStringDateElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "string-date";

    public JATSStringDateElement() {
        super(TAG);
    }

    public JATSStringDateElement(String content) {
        this();
        appendText(content);
    }

    public JATSStringDateElement(Element element) {
        super(element);
    }
}

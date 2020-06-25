package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSStringDateElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSStringDateElement.class);
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

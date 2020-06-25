package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSStringNameElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSStringNameElement.class);
public static String TAG = "string-name";

    public JATSStringNameElement() {
        super(TAG);
    }

    public JATSStringNameElement(String content) {
        this();
        appendText(content);
    }

    public JATSStringNameElement(Element element) {
        super(element);
    }
}

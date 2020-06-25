package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSAltTextElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSAltTextElement.class);
public static String TAG = "alt-text";

    public JATSAltTextElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMonospaceElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSMonospaceElement.class);
public static String TAG = "monospace";

    public JATSMonospaceElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSKwdElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSKwdElement.class);
public static String TAG = "kwd";

    public JATSKwdElement(Element element) {
        super(element);
    }
}

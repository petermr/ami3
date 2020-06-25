package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSIsbnElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSIsbnElement.class);
public static String TAG = "isbn";

    public JATSIsbnElement(Element element) {
        super(element);
    }
}

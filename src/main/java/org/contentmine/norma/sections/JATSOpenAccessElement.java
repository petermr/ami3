package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSOpenAccessElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSOpenAccessElement.class);
public static String TAG = "open-access";

    public JATSOpenAccessElement(Element element) {
        super(element);
    }
}

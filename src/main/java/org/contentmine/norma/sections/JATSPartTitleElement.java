package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSPartTitleElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSPartTitleElement.class);
public static String TAG = "part-title";

    public JATSPartTitleElement(Element element) {
        super(element);
    }
}

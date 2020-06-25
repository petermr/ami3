package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSEditionElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSEditionElement.class);
public static String TAG = "edition";

    public JATSEditionElement(Element element) {
        super(element);
    }
}

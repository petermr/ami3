package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSPrefixElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSPrefixElement.class);
public static String TAG = "prefix";

    public JATSPrefixElement(Element element) {
        super(element);
    }
}

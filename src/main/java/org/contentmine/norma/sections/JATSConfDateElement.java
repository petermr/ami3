package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSConfDateElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSConfDateElement.class);
public static String TAG = "conf-date";

    public JATSConfDateElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSFaxElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSFaxElement.class);
public static String TAG = "fax";

    public JATSFaxElement(Element element) {
        super(element);
    }
}

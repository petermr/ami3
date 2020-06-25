package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSNoneElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSNoneElement.class);
public static String TAG = "none";

    public JATSNoneElement(Element element) {
        super(element);
    }
}

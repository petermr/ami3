package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSAppElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSAppElement.class);
public static String TAG = "app";

    public JATSAppElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSConfNameElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSConfNameElement.class);
public static String TAG = "conf-name";

    public JATSConfNameElement(Element element) {
        super(element);
    }
}

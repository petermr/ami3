package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSAnonymousElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSAnonymousElement.class);
public static String TAG = "anonymous";

    public JATSAnonymousElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSSelfUriElement extends JATSElement implements IsInline {
    private static final Logger LOG = LogManager.getLogger(JATSSelfUriElement.class);
public static String TAG = "self-uri";

    public JATSSelfUriElement(Element element) {
        super(element);
    }
}

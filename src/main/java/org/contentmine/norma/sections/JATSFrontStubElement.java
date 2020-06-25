package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSFrontStubElement extends JATSElement implements IsBlock {
    private static final Logger LOG = LogManager.getLogger(JATSFrontStubElement.class);
public static String TAG = "front-stub";

    public JATSFrontStubElement(Element element) {
        super(element);
    }
}

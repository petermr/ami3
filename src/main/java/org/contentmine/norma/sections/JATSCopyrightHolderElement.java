package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSCopyrightHolderElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSCopyrightHolderElement.class);
public static String TAG = "copyright-holder";

    public JATSCopyrightHolderElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSTransTitleGroupElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSTransTitleGroupElement.class);
public static String TAG = "trans-title-group";

    public JATSTransTitleGroupElement(Element element) {
        super(element);
    }
}

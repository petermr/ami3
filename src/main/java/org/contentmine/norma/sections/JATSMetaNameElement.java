package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMetaNameElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSMetaNameElement.class);
public static String TAG = "meta-name";

    public JATSMetaNameElement(Element element) {
        super(element);
    }
}

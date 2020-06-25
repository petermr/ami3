package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMetaValueElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSMetaValueElement.class);
public static String TAG = "meta-value";

    public JATSMetaValueElement(Element element) {
        super(element);
    }
}

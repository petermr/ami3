package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSCustomMetaElement extends JATSElement implements IsBlock {
    private static final Logger LOG = LogManager.getLogger(JATSCustomMetaElement.class);
public static String TAG = "custom-meta";

    public JATSCustomMetaElement(Element element) {
        super(element);
    }
}

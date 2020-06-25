package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSCustomMetaGroupElement extends JATSElement implements IsBlock {
    private static final Logger LOG = LogManager.getLogger(JATSCustomMetaGroupElement.class);
public static String TAG = "custom-meta-group";

    public JATSCustomMetaGroupElement(Element element) {
        super(element);
    }
}

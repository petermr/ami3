package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSSecMetaElement extends JATSElement implements IsBlock {
    private static final Logger LOG = LogManager.getLogger(JATSSecMetaElement.class);
public static String TAG = "sec-meta";

    public JATSSecMetaElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSTransAbstractElement extends JATSElement implements IsBlock {
    private static final Logger LOG = LogManager.getLogger(JATSTransAbstractElement.class);
public static String TAG = "trans-abstract";

    public JATSTransAbstractElement(Element element) {
        super(element);
    }
}

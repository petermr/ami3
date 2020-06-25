package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSKwdGroupElement extends JATSElement implements IsBlock {
    private static final Logger LOG = LogManager.getLogger(JATSKwdGroupElement.class);
public static String TAG = "kwd-group";

    public JATSKwdGroupElement(Element element) {
        super(element);
    }
}

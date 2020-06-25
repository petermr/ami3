package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSAppGroupElement extends JATSElement implements IsBlock {
    private static final Logger LOG = LogManager.getLogger(JATSAppGroupElement.class);
public static String TAG = "app-group";

    public JATSAppGroupElement(Element element) {
        super(element);
    }
}

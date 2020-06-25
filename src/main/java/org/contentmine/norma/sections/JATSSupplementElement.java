package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSSupplementElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSSupplementElement.class);
public static String TAG = "supplement";

    public JATSSupplementElement(Element element) {
        super(element);
    }
}

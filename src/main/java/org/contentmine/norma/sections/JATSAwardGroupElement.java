package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSAwardGroupElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSAwardGroupElement.class);
public static String TAG = "award-group";

    public JATSAwardGroupElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSConfLocElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSConfLocElement.class);
public static String TAG = "conf-loc";

    public JATSConfLocElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSConfLocElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSConfLocElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "conf-loc";

    public JATSConfLocElement(Element element) {
        super(element);
    }
}

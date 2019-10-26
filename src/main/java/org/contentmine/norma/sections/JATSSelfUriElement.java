package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSSelfUriElement extends JATSElement implements IsInline {
    private static final Logger LOG = Logger.getLogger(JATSSelfUriElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "self-uri";

    public JATSSelfUriElement(Element element) {
        super(element);
    }
}

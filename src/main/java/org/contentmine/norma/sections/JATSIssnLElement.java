package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSIssnLElement extends JATSElement implements IsInline {
    private static final Logger LOG = Logger.getLogger(JATSIssnLElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "issn-l";

    public JATSIssnLElement(Element element) {
        super(element);
    }
}

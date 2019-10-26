package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSPatentElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSPatentElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "patent";

    public JATSPatentElement(Element element) {
        super(element);
    }
}

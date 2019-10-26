package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSDefElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSDefElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "def";

    public JATSDefElement(Element element) {
        super(element);
    }
}

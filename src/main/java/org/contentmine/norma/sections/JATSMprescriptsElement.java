package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMprescriptsElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSMprescriptsElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mprescripts";

    public JATSMprescriptsElement(Element element) {
        super(element);
    }
}

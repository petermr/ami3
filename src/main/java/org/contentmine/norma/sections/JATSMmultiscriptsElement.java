package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSMmultiscriptsElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSMmultiscriptsElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "mmultiscripts";

    public JATSMmultiscriptsElement(Element element) {
        super(element);
    }
}

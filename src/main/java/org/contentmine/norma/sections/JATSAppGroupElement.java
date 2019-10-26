package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSAppGroupElement extends JATSElement implements IsBlock {
    private static final Logger LOG = Logger.getLogger(JATSAppGroupElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "app-group";

    public JATSAppGroupElement(Element element) {
        super(element);
    }
}

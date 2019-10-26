package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSFigCountElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSFigCountElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "fig-count";

    public JATSFigCountElement(Element element) {
        super(element);
    }
}

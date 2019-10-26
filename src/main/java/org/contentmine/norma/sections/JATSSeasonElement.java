package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSSeasonElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSSeasonElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "season";

    public JATSSeasonElement(Element element) {
        super(element);
    }
}

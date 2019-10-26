package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSSeriesTitleElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSSeriesTitleElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "series-title";

    public JATSSeriesTitleElement(Element element) {
        super(element);
    }
}

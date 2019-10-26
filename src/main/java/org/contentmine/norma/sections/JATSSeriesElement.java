package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSSeriesElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSSeriesElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "series";

    public JATSSeriesElement(Element element) {
        super(element);
    }
}

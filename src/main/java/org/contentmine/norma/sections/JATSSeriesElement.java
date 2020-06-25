package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSSeriesElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSSeriesElement.class);
public static String TAG = "series";

    public JATSSeriesElement(Element element) {
        super(element);
    }
}

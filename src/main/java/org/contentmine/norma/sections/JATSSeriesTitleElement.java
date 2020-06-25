package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSSeriesTitleElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSSeriesTitleElement.class);
public static String TAG = "series-title";

    public JATSSeriesTitleElement(Element element) {
        super(element);
    }
}

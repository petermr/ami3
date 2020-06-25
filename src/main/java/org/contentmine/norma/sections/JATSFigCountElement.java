package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSFigCountElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSFigCountElement.class);
public static String TAG = "fig-count";

    public JATSFigCountElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSSeasonElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSSeasonElement.class);
public static String TAG = "season";

    public JATSSeasonElement(Element element) {
        super(element);
    }
}

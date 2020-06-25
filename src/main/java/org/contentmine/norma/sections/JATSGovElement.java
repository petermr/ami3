package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSGovElement extends JATSElement implements IsInline {
    private static final Logger LOG = LogManager.getLogger(JATSGovElement.class);
public static String TAG = "gov";

    public JATSGovElement(Element element) {
        super(element);
    }
}

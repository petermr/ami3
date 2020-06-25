package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSInlineGraphicElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSInlineGraphicElement.class);
public static String TAG = "inline-graphic";

    public JATSInlineGraphicElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSStyledContentElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSStyledContentElement.class);
public static String TAG = "styled-content";

    public JATSStyledContentElement(Element element) {
        super(element);
    }
}

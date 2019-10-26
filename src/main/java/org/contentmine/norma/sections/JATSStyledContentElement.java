package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSStyledContentElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSStyledContentElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "styled-content";

    public JATSStyledContentElement(Element element) {
        super(element);
    }
}

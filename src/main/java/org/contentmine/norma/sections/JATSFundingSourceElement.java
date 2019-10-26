package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSFundingSourceElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSFundingSourceElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "funding-source";

    public JATSFundingSourceElement(Element element) {
        super(element);
    }
}

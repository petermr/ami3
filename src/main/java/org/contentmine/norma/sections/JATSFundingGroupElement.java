package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSFundingGroupElement extends JATSElement implements IsBlock {
    private static final Logger LOG = Logger.getLogger(JATSFundingGroupElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "funding-group";

    public JATSFundingGroupElement(Element element) {
        super(element);
    }
}

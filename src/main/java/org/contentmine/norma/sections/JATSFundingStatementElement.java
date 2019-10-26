package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSFundingStatementElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSFundingStatementElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "funding-statement";

    public JATSFundingStatementElement(Element element) {
        super(element);
    }
}

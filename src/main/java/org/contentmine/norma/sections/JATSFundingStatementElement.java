package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSFundingStatementElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSFundingStatementElement.class);
public static String TAG = "funding-statement";

    public JATSFundingStatementElement(Element element) {
        super(element);
    }
}

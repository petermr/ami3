package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSFundingSourceElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSFundingSourceElement.class);
public static String TAG = "funding-source";

    public JATSFundingSourceElement(Element element) {
        super(element);
    }
}

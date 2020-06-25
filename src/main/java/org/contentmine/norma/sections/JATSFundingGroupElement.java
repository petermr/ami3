package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSFundingGroupElement extends JATSElement implements IsBlock {
    private static final Logger LOG = LogManager.getLogger(JATSFundingGroupElement.class);
public static String TAG = "funding-group";

    public JATSFundingGroupElement(Element element) {
        super(element);
    }
}

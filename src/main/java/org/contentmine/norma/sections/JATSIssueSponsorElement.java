package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSIssueSponsorElement extends JATSElement implements IsInline {
    private static final Logger LOG = LogManager.getLogger(JATSIssueSponsorElement.class);
public static String TAG = "issue-sponsor";

    public JATSIssueSponsorElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSIssueSponsorElement extends JATSElement implements IsInline {
    private static final Logger LOG = Logger.getLogger(JATSIssueSponsorElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "issue-sponsor";

    public JATSIssueSponsorElement(Element element) {
        super(element);
    }
}

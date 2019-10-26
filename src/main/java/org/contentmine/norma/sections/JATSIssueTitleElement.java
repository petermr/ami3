package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSIssueTitleElement extends JATSElement implements IsInline {
    private static final Logger LOG = Logger.getLogger(JATSIssueTitleElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "issue-title";

    public JATSIssueTitleElement(Element element) {
        super(element);
    }
}

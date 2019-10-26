package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSIssueIdElement extends JATSElement implements IsInline {
    private static final Logger LOG = Logger.getLogger(JATSIssueIdElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "issue-id";

    public JATSIssueIdElement(Element element) {
        super(element);
    }
}

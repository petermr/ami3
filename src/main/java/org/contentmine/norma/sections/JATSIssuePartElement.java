package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSIssuePartElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSIssuePartElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "issue-part";

    public JATSIssuePartElement(Element element) {
        super(element);
    }
}

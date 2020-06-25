package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSIssuePartElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSIssuePartElement.class);
public static String TAG = "issue-part";

    public JATSIssuePartElement(Element element) {
        super(element);
    }
}

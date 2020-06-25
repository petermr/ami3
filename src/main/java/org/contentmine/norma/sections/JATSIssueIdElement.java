package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSIssueIdElement extends JATSElement implements IsInline {
    private static final Logger LOG = LogManager.getLogger(JATSIssueIdElement.class);
public static String TAG = "issue-id";

    public JATSIssueIdElement(Element element) {
        super(element);
    }
}

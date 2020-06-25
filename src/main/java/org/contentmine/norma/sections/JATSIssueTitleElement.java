package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSIssueTitleElement extends JATSElement implements IsInline {
    private static final Logger LOG = LogManager.getLogger(JATSIssueTitleElement.class);
public static String TAG = "issue-title";

    public JATSIssueTitleElement(Element element) {
        super(element);
    }
}

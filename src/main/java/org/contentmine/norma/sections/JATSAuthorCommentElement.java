package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSAuthorCommentElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSAuthorCommentElement.class);
public static String TAG = "author-comment";

    public JATSAuthorCommentElement(Element element) {
        super(element);
    }
}

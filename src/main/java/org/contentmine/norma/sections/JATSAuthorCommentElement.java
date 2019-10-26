package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSAuthorCommentElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSAuthorCommentElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "author-comment";

    public JATSAuthorCommentElement(Element element) {
        super(element);
    }
}

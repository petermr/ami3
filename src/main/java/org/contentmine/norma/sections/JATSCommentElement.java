package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSCommentElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSCommentElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "comment";

    public JATSCommentElement(Element element) {
        super(element);
    }
}

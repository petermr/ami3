package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSCommentElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSCommentElement.class);
public static String TAG = "comment";

    public JATSCommentElement(Element element) {
        super(element);
    }
}

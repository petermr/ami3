package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSChapterTitleElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSChapterTitleElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "chapter-title";

    public JATSChapterTitleElement(Element element) {
        super(element);
    }
}

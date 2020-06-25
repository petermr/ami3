package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSChapterTitleElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSChapterTitleElement.class);
public static String TAG = "chapter-title";

    public JATSChapterTitleElement(Element element) {
        super(element);
    }
}

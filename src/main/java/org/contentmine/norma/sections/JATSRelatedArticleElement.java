package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSRelatedArticleElement extends JATSElement implements IsInline {
    private static final Logger LOG = Logger.getLogger(JATSRelatedArticleElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "related-article";

    public JATSRelatedArticleElement(Element element) {
        super(element);
    }
}

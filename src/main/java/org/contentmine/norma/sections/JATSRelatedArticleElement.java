package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSRelatedArticleElement extends JATSElement implements IsInline {
    private static final Logger LOG = LogManager.getLogger(JATSRelatedArticleElement.class);
public static String TAG = "related-article";

    public JATSRelatedArticleElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSIssnLElement extends JATSElement implements IsInline {
    private static final Logger LOG = LogManager.getLogger(JATSIssnLElement.class);
public static String TAG = "issn-l";

    public JATSIssnLElement(Element element) {
        super(element);
    }
}

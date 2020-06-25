package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSInlineSupplementaryMaterialElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSInlineSupplementaryMaterialElement.class);
public static String TAG = "inline-supplementary-material";

    public JATSInlineSupplementaryMaterialElement(Element element) {
        super(element);
    }
}

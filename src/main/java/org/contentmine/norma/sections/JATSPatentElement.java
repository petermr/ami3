package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSPatentElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSPatentElement.class);
public static String TAG = "patent";

    public JATSPatentElement(Element element) {
        super(element);
    }
}

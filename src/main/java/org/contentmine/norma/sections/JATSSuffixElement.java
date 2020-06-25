package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSSuffixElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSSuffixElement.class);
public static String TAG = "suffix";

    public JATSSuffixElement(Element element) {
        super(element);
    }
}

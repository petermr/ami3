package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSVersionElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSVersionElement.class);
public static String TAG = "version";

    public JATSVersionElement(Element element) {
        super(element);
    }
}

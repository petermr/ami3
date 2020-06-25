package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSDefElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSDefElement.class);
public static String TAG = "def";

    public JATSDefElement(Element element) {
        super(element);
    }
}

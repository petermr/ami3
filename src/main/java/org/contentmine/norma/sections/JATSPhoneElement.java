package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSPhoneElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSPhoneElement.class);
public static String TAG = "phone";

    public JATSPhoneElement(Element element) {
        super(element);
    }
}

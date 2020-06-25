package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSPreformatElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSPreformatElement.class);
public static String TAG = "preformat";

    public JATSPreformatElement(Element element) {
        super(element);
    }
}

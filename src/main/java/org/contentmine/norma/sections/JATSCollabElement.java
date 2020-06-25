package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSCollabElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSCollabElement.class);
public static String TAG = "collab";

    public JATSCollabElement(Element element) {
        super(element);
    }
}

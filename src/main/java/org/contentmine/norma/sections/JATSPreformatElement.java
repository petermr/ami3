package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSPreformatElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSPreformatElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "preformat";

    public JATSPreformatElement(Element element) {
        super(element);
    }
}

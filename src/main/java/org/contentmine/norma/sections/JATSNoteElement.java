package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSNoteElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSNoteElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "note";

    public JATSNoteElement(Element element) {
        super(element);
    }
}

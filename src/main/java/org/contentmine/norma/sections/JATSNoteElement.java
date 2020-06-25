package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSNoteElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSNoteElement.class);
public static String TAG = "note";

    public JATSNoteElement(Element element) {
        super(element);
    }
}

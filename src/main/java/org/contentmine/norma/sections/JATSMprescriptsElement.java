package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMprescriptsElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSMprescriptsElement.class);
public static String TAG = "mprescripts";

    public JATSMprescriptsElement(Element element) {
        super(element);
    }
}

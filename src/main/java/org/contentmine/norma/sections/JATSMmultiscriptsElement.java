package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMmultiscriptsElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSMmultiscriptsElement.class);
public static String TAG = "mmultiscripts";

    public JATSMmultiscriptsElement(Element element) {
        super(element);
    }
}

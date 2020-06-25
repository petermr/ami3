package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMaligngroupElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMaligngroupElement.class);
public static String TAG = "maligngroup";

    public JATSMaligngroupElement(Element element) {
        super(element);
    }
}

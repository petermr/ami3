package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSMalignmarkElement extends AbstractJATSMathmlElement {
    private static final Logger LOG = LogManager.getLogger(JATSMalignmarkElement.class);
public static String TAG = "malignmark";

    public JATSMalignmarkElement(Element element) {
        super(element);
    }
}

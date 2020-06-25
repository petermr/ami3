package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSBioElement extends JATSElement implements IsBlock {
    private static final Logger LOG = LogManager.getLogger(JATSBioElement.class);
public static String TAG = "bio";

    public JATSBioElement(Element element) {
        super(element);
    }
}

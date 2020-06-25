package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSStrikeElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSStrikeElement.class);
public static String TAG = "strike";

    public JATSStrikeElement(Element element) {
        super(element);
    }
}

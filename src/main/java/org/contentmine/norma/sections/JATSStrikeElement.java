package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSStrikeElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSStrikeElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "strike";

    public JATSStrikeElement(Element element) {
        super(element);
    }
}

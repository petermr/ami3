package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import nu.xom.Element;

public class JATSPrincipalAwardRecipientElement extends JATSElement {
    private static final Logger LOG = Logger.getLogger(JATSPrincipalAwardRecipientElement.class);
    static {
        LOG.setLevel(Level.DEBUG);
    }

    public static String TAG = "principal-award-recipient";

    public JATSPrincipalAwardRecipientElement(Element element) {
        super(element);
    }
}

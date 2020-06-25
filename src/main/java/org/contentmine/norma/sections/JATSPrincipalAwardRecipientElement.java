package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSPrincipalAwardRecipientElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSPrincipalAwardRecipientElement.class);
public static String TAG = "principal-award-recipient";

    public JATSPrincipalAwardRecipientElement(Element element) {
        super(element);
    }
}

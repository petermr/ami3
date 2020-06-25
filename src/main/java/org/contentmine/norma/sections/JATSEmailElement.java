package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSEmailElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSEmailElement.class);
public static String TAG = "email";

    public JATSEmailElement() {
        super(TAG);
    }

    public JATSEmailElement(String text) {
    	this();
    	this.appendText(text);
    }

    public JATSEmailElement(Element element) {
        super(element);
    }
}

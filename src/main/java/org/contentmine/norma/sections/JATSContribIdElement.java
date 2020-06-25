package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nu.xom.Element;

public class JATSContribIdElement extends JATSElement {
    private static final Logger LOG = LogManager.getLogger(JATSContribIdElement.class);
public static String TAG = "contrib-id";
	public static final String CONTRIB_ID_TYPE = "contrib_id_type";
	public static final String ORCID = "orcid";

    public JATSContribIdElement() {
        super(TAG);
    }

    public JATSContribIdElement(Element element) {
        super(element);
    }
}

package org.contentmine.norma.tagger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class StylesheetElement extends AbstractTElement {

	private static final Logger LOG = LogManager.getLogger(StylesheetElement.class);
	
	public static final String TAG = "stylesheet";

	public StylesheetElement() {
		super(TAG);
	}

}

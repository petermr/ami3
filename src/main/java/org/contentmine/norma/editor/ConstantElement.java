package org.contentmine.norma.editor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class ConstantElement extends AbstractEditorElement implements IRegexComponent {

	public static final Logger LOG = LogManager.getLogger(ConstantElement.class);
public static final String TAG = "constant";

	public ConstantElement() {
		super(TAG);
	}

	public String createRegex() {
		String pattern = this.getAttributeValue(PATTERN);
		return pattern;
	}

}

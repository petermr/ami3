package org.contentmine.norma.editor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SpaceElement extends AbstractEditorElement implements IRegexComponent {

	private static final String COUNT = "count";
	public static final Logger LOG = LogManager.getLogger(SpaceElement.class);
public static final String TAG = "space";

	public SpaceElement() {
		super(TAG);
	}

	public String createRegex() {
		String s = "\\s";
		String count = this.getAttributeValue(COUNT);
		if (count != null) {
			s += count;
		}
		LOG.trace(this.toXML()+":"+s);
		return s;
	}

	public String getCount() {
		String count = this.getAttributeValue(COUNT);
		return count;
	}



}

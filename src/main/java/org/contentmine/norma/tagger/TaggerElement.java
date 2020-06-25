package org.contentmine.norma.tagger;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

public class TaggerElement extends AbstractTElement {

	private static final Logger LOG = LogManager.getLogger(TaggerElement.class);
	
	public static final String TAG = "tagger";

	public TaggerElement() {
		super(TAG);
	}

	public AbstractTElement getMetadataListElement() {
		List<Element> elements = XMLUtil.getQueryElements(this, "./*[local-name()='"+MetadataListElement.TAG+"']");
		return elements.size() != 1 ? null : (MetadataListElement) elements.get(0);
	}

	public TagListElement getTagListElement() {
		List<Element> elements = XMLUtil.getQueryElements(this, "./*[local-name()='"+TagListElement.TAG+"']");
		return elements.size() != 1 ? null : (TagListElement) elements.get(0);
	}

}

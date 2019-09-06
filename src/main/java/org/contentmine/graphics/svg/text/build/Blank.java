package org.contentmine.graphics.svg.text.build;

import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.rule.horizontal.LineChunk;

import nu.xom.Element;

public class Blank extends LineChunk {

	private static final Logger LOG = Logger.getLogger(Blank.class);
	public final static String TAG = "blank";
	
	private Real2Range boundingBox;

	public Blank(Real2Range bbox) {
		super();
		this.setSVGClassName(TAG);
		this.boundingBox = bbox;
	}

	@Override
	public String toString() {
		return "Blank: "+boundingBox.toString();
	}

	public Element copyElement() {
		return (Element) this.copy();
	}

	protected List<? extends LineChunk> getChildChunks() {
		throw new RuntimeException("not applicable");
	}

}

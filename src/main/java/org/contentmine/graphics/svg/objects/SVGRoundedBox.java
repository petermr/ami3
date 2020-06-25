package org.contentmine.graphics.svg.objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGPath;

public class SVGRoundedBox extends SVGG {
	
	public static final String ROUNDED_BOX = "roundedBox";
	private static final Logger LOG = LogManager.getLogger(SVGRoundedBox.class);
private SVGPath path;
	
	public SVGRoundedBox() {
		super();
		this.setSVGClassName(ROUNDED_BOX);
	}
	
	public SVGRoundedBox(SVGPath path) {
		this();
		this.path = path;
	}

	public static SVGRoundedBox createRoundedBox(SVGPath path) {
		SVGRoundedBox roundedBox = null;
		if ("MCLCLCLCZ".equals(path.getOrCreateSignatureAttributeValue())) {
			roundedBox = new SVGRoundedBox(path);
		}
		return roundedBox;
	}

	public SVGPath getPath() {
		return path;
	}
}

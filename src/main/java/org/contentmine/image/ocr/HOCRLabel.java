package org.contentmine.image.ocr;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGG;

/** a <g><rect/><text>val</text></g>
 * where the rect is a bounding box from HOCR
 * 
 * @author pm286
 *
 */
public class HOCRLabel extends HOCRChunk {

	final static Logger LOG = LogManager.getLogger(HOCRLabel.class);
public HOCRLabel(SVGG g) {
		super(g);
	}

}

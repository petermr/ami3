package org.contentmine.image.ocr;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGG;

/** a <g><rect/><text>val</text></g>
 * where the rect is a bounding box from HOCR
 * 
 * @author pm286
 *
 */
public class HOCRPhrase extends HOCRChunk {

	final static Logger LOG = LogManager.getLogger(HOCRPhrase.class);
public HOCRPhrase() {
		super();
	}

	public HOCRPhrase(SVGG g) {
		super(g);
	}

//	public HOCRPhrase(HOCRText text) {
//		super(g);
//	}

	public void add(SVGG word) {
		if (bboxRect == null) {
			extractBoxAndText(word);
		} else {
			mergeWordIntoCurrentBoxAndText(word);
		}
	}

	private void mergeWordIntoCurrentBoxAndText(SVGG word) {
		HOCRChunk wordChunk = new HOCRChunk(word);
		Real2Range bbox = bboxRect.getBoundingBox();
		bbox.plusEquals(wordChunk.getBboxRect().getBoundingBox());
		bboxRect.setWidth(bbox.getXRange().getRange());
		bboxRect.setHeight(bbox.getYRange().getRange());
		text.setText(text.getText()+" "+wordChunk.getText().getText());
	}

}

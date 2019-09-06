package org.contentmine.image.ocr;

import org.contentmine.graphics.svg.SVGG;

/** holds a chunk of g+text identified by HOCR.
 * 
 * @author pm286
 *
 */
public class HOCRText extends HOCRChunk {

	public HOCRText(SVGG g) {
		super(g);
	}

	public static boolean isWordInPhrase(Double separation, Double meanTextSize,
			double min, double max) {
		return (separation < max * meanTextSize && separation > min * meanTextSize);
	}

}

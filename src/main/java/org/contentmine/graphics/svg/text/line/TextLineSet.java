package org.contentmine.graphics.svg.text.line;

import java.util.LinkedHashSet;
import java.util.List;


/** an ordered set of TextLines.
 * usually created by extracted TextLines out of a Chunk/TextAnalyzer
 * not widely used ?obsolete
 * @author pm286
 *
 */
public class TextLineSet extends LinkedHashSet<TextLine> {

	private static final long serialVersionUID = 3049279902624745797L;
	
	public TextLineSet() {
	}

	public TextLineSet(List<TextLine> textLineList) {
		for (TextLine textLine : textLineList) {
			this.add(textLine);
		}
	}

}

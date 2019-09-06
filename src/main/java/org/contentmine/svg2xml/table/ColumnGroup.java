package org.contentmine.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalRule;
import org.contentmine.graphics.svg.text.build.Phrase;

/** supports a column group within the TableHeader.
 * Usually denoted by a horizontal ruler with text above
 * Maybe will map onto W3C <colgroup>
 * 
 * @author pm286
 *
 */
public class ColumnGroup {

	private static final Logger LOG = Logger.getLogger(ColumnGroup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<Phrase> phrases;
	private HorizontalRule ruler;
	private Real2Range boundingBox;
	
	public void add(Phrase phrase) {
		getOrCreatePhrases();
		phrases.add(phrase);
		Real2Range bbox = phrase.getBoundingBox();
		boundingBox = boundingBox == null ? bbox : boundingBox.plus(bbox);
	}

	private List<Phrase> getOrCreatePhrases() {
		if (phrases == null) {
			phrases = new ArrayList<Phrase>();
		}
		return phrases;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Phrase phrase : phrases) {
			sb.append(">p> "+String.valueOf(phrase)+"\n");
		}
		sb.append("==="+String.valueOf(ruler)+"===");
		return sb.toString();
	}

	public List<Phrase> getPhrases() {
		return phrases;
	}

	public HorizontalRule getRuler() {
		return ruler;
	}

	public void add(HorizontalRule ruler) {
		if (this.ruler != null) {
//			LOG.warn("Existing ruler will be overwritten");
		}
		this.ruler = ruler;
		Real2Range bbox = ((SVGElement)ruler).getBoundingBox();
		boundingBox = boundingBox == null ? bbox : boundingBox.plus(bbox);
	}

	public Real2Range getBoundingBox() {
		return boundingBox;
	}

}

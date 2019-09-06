package org.contentmine.graphics.svg.rule;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.objects.SVGContentBox;
import org.contentmine.graphics.svg.text.build.PhraseChunk;

/** generic horizontally-based object in a table.
 * replaces HorizontalElement
 * Can be a line, PhraseList, GridPanelBox, etc.
 * Messy, because the tables are messy.
 * 
 * 
 * 
 * @author pm286
 *
 */
public class GenericRow {
	

	public enum RowType {
		LONG_HORIZONTAL("H"),
		SHORT_HORIZONTAL("h"),
		LINE_LIST("L"),
		CONTENT_BOX("B"),
		PHRASE_LIST("P"),
		SIBLING_PHRASE_LIST("S"),
//		CONTENT_PANEL("C"), 
		SIBLING_RULES("="), 
		CONTENT_GRID_PANEL("G");
		
		private String abbrev;

		private RowType(String abbrev) {
			this.abbrev = abbrev;
		}
		public String getAbbrev() {
			return abbrev;
		}
	}
	
	private static final Logger LOG = Logger.getLogger(GenericRow.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static double LINE_DELTA_Y = 0.5; // make lines at least 1 pixel thick
	private static final double LINE_THICKNESS_FACTOR  = (1.0 + 1.0) + 0.1;
	private SVGLine line;
	private RowType type;
	private Real2Range box;
	private PhraseChunk phraseChunk;
	private SVGLineList lineList;
	private SVGContentBox contentBox;

	private GenericRow(RowType type) {
		this.type = type;
		if (type == null) {
			throw new RuntimeException("null type in GenericRow");
		}
	}
	
	public GenericRow(SVGLine line, RowType type) {
		this(type);
		this.line = line;
	}

	public GenericRow(Real2Range box, RowType type) {
		this(type);
		this.box = box;
	}
	
	public GenericRow(PhraseChunk phraseChunk, RowType type) {
		this(type);
		this.phraseChunk = phraseChunk;
	}
	
	public GenericRow(SVGLineList lineList, RowType type) {
		this(type);
		this.lineList = lineList;
		
	}

	public GenericRow(SVGContentBox contentBox) {
		this(RowType.CONTENT_BOX);
		this.contentBox = contentBox;
	}

	public Real2Range getOrCreateBoundingBox() {
		Real2Range bbox = null;
		if (bbox == null) {
			if (type.equals(RowType.LONG_HORIZONTAL) || type.equals(RowType.SHORT_HORIZONTAL)) {
				bbox = getLineBBox();
			} else if (type.equals(RowType.CONTENT_BOX) || type.equals(RowType.CONTENT_GRID_PANEL)) {
				bbox = box;
			} else if (type.equals(RowType.PHRASE_LIST)) {
				bbox = phraseChunk == null ? null: phraseChunk.getBoundingBox();
			} else if (type.equals(RowType.SIBLING_RULES)) {
				bbox = lineList == null ? null: lineList.getBoundingBox();
			} else {
				throw new RuntimeException("Unknown type: "+type);
			}
		}
		return bbox;
	}

	private Real2Range getLineBBox() {
		Real2Range bbox = null;
		if (line != null) {
			bbox = line.getBoundingBox();
			if (bbox.getYRange().getRange() < 1.0) {
				bbox = bbox.getReal2RangeExtendedInY(LINE_DELTA_Y, LINE_DELTA_Y);
			}
		}
		return bbox;
	}

	public String getSignature() {
		String s;
		s = type.getAbbrev();
		return s;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type.abbrev);
		sb.append("; ");
		sb.append((line == null ? "" : line.toString()));
		sb.append((contentBox == null ? "" : contentBox.toString()));
		sb.append((box == null ? "" : box.toString()));
		sb.append((phraseChunk == null ? "" : phraseChunk.toString()));
		sb.append((lineList == null ? "" : lineList.toString()));
		return sb.toString();
	}

	public SVGContentBox getContentBox() {
		return contentBox;
	}

	public boolean intersectsBoxRange(RealRange boxYRange) {
		boolean intersects = false;
		RealRange rowYRange = getOrCreateBoundingBox().getYRange();
		// equality
		if (boxYRange.isEqualTo(rowYRange, LINE_DELTA_Y)) {
			LOG.trace("ROW "+this.toString()+" EQUALS "+rowYRange+"; "+getSignature());
			intersects = true;
		} else if (boxYRange.includes(rowYRange)) {
			LOG.trace("ROW "+this.toString()+" INCLUDES "+rowYRange+"; "+getSignature());
			intersects = true;
		} else if (boxYRange.intersects(rowYRange)) {
			boolean touchesLow = Real.isEqual(boxYRange.getMin(), rowYRange.getMin(), LINE_DELTA_Y);
			boolean touchesHigh = Real.isEqual(boxYRange.getMax(), rowYRange.getMax(), LINE_DELTA_Y);
			if (touchesLow && touchesHigh) {
				LOG.trace("ROW "+this.toString()+" TOUCHES_BOTH "+rowYRange+"; "+getSignature());
			} else if (touchesLow) {
				LOG.trace("ROW "+this.toString()+" TOUCHES_LOW "+rowYRange+"; "+getSignature());
			} else if (touchesHigh) {
				LOG.trace("ROW "+this.toString()+" TOUCHES_HIGH "+rowYRange+"; "+getSignature());
			} else {
				LOG.trace("ROW "+this.toString()+" INTERSECTS "+rowYRange+"; "+getSignature());
			}
			if (rowYRange.getRange() < LINE_THICKNESS_FACTOR  * LINE_DELTA_Y) {
				// a line on edge of box is EXCLUDED
				intersects = false;
				LOG.trace("EXCLUDED");
			} else {
				intersects = true;
			}
		} else {
			// doesn't intersect
		}
		return intersects;
	}
	
	public RowType getRowType() {
		return type;
	}

	public SVGElement getLine() {
		return line;
	}

	public PhraseChunk getPhraseList() {
		return phraseChunk;
	}

	public boolean addLineToContentBox(SVGContentBox contentBox) {
		boolean added = false;
		if (line != null) {
			added = contentBox.addLine(line);
		}
		return added;
	}
	
	public boolean addLineListToContentBox(SVGContentBox contentBox) {
		boolean added = false;
		if (lineList != null) {
			added = contentBox.addLineList(lineList);
		}
		return added;
	}

	public boolean addPhraseListToContentBox(SVGContentBox contentBox) {
		boolean added = false;
		if (phraseChunk != null) {
			added = contentBox.addPhraseList(phraseChunk);
		}
		return added;
	}


}

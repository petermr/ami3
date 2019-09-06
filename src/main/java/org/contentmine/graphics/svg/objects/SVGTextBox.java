package org.contentmine.graphics.svg.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Line2;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGText;

/**
 * box with text.
 * May be better to use ContentBox
 * 
 * @author pm286
 *
 */
public class SVGTextBox extends SVGG {

	public static final String TEXT_BOX = "textBox";
	private static final Logger LOG = Logger.getLogger(SVGTextBox.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private SVGRect rect;
	protected List<SVGText> textList;
	private Real2Range bbox;
	private Real2[] corners;
	private List<Line2> lines;
	
	public SVGTextBox() {
		super();
		this.setSVGClassName(TEXT_BOX);
	}

	public SVGTextBox(SVGRect rect) {
		this();
		this.rect = rect;
	}

	public SVGTextBox(SVGRect rect, List<SVGText> textList) {
		this(rect);
		this.textList = textList;
	}

	// this probably should be boxes/Rects not textBoxes
	public static SVGTextBox getTouchingBox(Real2 point, List<SVGTextBox> textBoxList, double delta) {
		for (SVGTextBox textBox : textBoxList) {
			if (textBox.touches(point, delta)) {
				return textBox;
			}
		}
		return null;
	}

	private boolean touches(Real2 point, double delta) {
		if (bbox == null) {
			bbox = rect.getBoundingBox();
			corners = bbox.getLLURCorners();
			lines = new ArrayList<Line2>();
			lines.add(new Line2(corners[0], new Real2(corners[0].getX(), corners[1].getY())));
			lines.add(new Line2(new Real2(corners[0].getX(), corners[1].getY()), corners[1]));
			lines.add(new Line2(corners[1], new Real2(corners[1].getX(), corners[0].getY())));
			lines.add(new Line2(new Real2(corners[1].getX(), corners[0].getY()), corners[0]));
		}
		for (Line2 line : lines) {
			if (line.contains(point, delta, false)) {
				return true;
			}
		}
		return false;
	}

	public void add(SVGText text) {
		ensureTextList();
		this.textList.add(text);
	}

	private void ensureTextList() {
		if (textList == null) {
			textList = new ArrayList<SVGText>();
		}
	}
	
	private List<SVGText> getTextList() {
		return textList;
	}

	public String toString() {
		double delta = 2.0;
		StringBuilder sb = new StringBuilder();
		double yLast = 0.0;
		for (SVGText text : textList) {
			double y = text.getY();
			if (!Real.isEqual(y,  yLast, delta)) {
				sb.append("\n");
				yLast = y;
			}
			sb.append(text.getText());
		}
		return sb.toString();
	}
}

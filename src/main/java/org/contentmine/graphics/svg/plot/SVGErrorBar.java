package org.contentmine.graphics.svg.plot;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;

/** graphical and semantic error bar.
 * 
 * consists of the "T" part so 4 components (Top Right Bottom Left)
 * A "tie-fighter" ("ibeam") consists of Top and Bottom Error bars
 * OR 
 * An "hbeam" consists of Left and Right Error bars
 * 
 *
 * 
 * 
 * @author pm286
 *
 */
public class SVGErrorBar extends SVGG {

	private static Logger LOG = Logger.getLogger(SVGErrorBar.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum BarDirection {
		TOP("TOP", new Vector2(0, 1)),
		RIGHT("RIGHT", new Vector2(1, 0)),
		BOTTOM("BOTTOM", new Vector2(0, -1)),
		LEFT("LEFT", new Vector2(-1, 0));
		private final String label;
		private final Vector2 vector;
		private BarDirection(String label, Vector2 vector) {
			this.label = label;
			this.vector = vector;
		};
		public BarDirection getBarDirection(int serial) {
			for (int j = 0; j < values().length; j++) {
				if (j == serial) return values()[j];
			}
			return null;
		}
		public BarDirection getBarDirection(String label) {
			for (int j = 0; j < values().length; j++) {
				if (values()[j].label.equals(label)) return values()[j];
			}
			return null;
		}
		public Vector2 getVector(int serial) {
			BarDirection direction = getBarDirection(serial);
			return direction == null ? null : direction.vector;
		}
	}

	public final static double ERROR_EPS = 0.5; // pixels
	
	private SVGLine line;
	// direction of bar starting from central point;
	private BarDirection barDirection;
	// optional crossbar (for visual effect only)
	private SVGElement crossbar;
	
	public SVGErrorBar(SVGLine line) {
		this.setLine(line);
	}

	public SVGErrorBar(BarDirection top, SVGLine svgLine, SVGElement crossbar) {
		this.setBarDirection(barDirection);
		this.setLine(svgLine);
		this.setCrossbar(crossbar);
	}

	private void setCrossbar(SVGElement crossbar) {
		this.crossbar = crossbar;
	}

	public SVGLine getLine() {
		return line;
	}

	public void setLine(SVGLine line) {
		this.line = line;
	}

	public BarDirection getBarDirection() {
		return barDirection;
	}

	public void setBarDirection(BarDirection barDirection) {
		this.barDirection = barDirection;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(""+barDirection+"; "+line);
		return sb.toString();
	}

	public AbstractCMElement createSVGElement() {
		AbstractCMElement g = new SVGG();
		if (line != null) {
			g.appendChild(line);
		}
		if (crossbar != null) {
			g.appendChild(crossbar);
		}
		return g;
	}
}

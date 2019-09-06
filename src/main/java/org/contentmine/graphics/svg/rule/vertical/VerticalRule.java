package org.contentmine.graphics.svg.rule.vertical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.rule.Rule;

public class VerticalRule extends Rule {

	private static final Logger LOG = Logger.getLogger(VerticalRule.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** allowed misalignment for "same X"*/
	public static final double X_TOLERANCE = 2.0;
	
	public VerticalRule(SVGElement line) {
		super(line);
	}
	
	/** requires sorted lines.
	 * 
	 * @param lines
	 * @return
	 */
	public static List<VerticalRule> createSortedRulersFromSVGList(List<SVGLine> lines) {
		List<VerticalRule> rulerList = new ArrayList<VerticalRule>();
		for (int i = 0; i < lines.size(); i++) {
			SVGLine line = lines.get(i);
			if (line.isVertical(epsilon)) {
				VerticalRule ruler = new VerticalRule(line);
				rulerList.add(ruler);
			}
		}
		Collections.sort(rulerList, new VerticalRulerComparator());
		return rulerList;
	}
	
	public IntRange getIntRange() {
		return new IntRange(getBoundingBox().getYRange());
	}
	
}
class VerticalRulerComparator implements Comparator<VerticalRule> {

	public int compare(VerticalRule vr1, VerticalRule vr2) {
		if (vr1 == null || vr2 == null || vr1.getIntRange() == null || vr2.getIntRange() == null) {
			return 0;
		}
		if (vr1.getX() < vr2.getX()) return -1;
		if (vr1.getX() > vr2.getX()) return 1;
		return vr1.getIntRange().getMin() - vr2.getIntRange().getMin();
	}

}

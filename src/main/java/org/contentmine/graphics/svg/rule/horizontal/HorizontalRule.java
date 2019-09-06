package org.contentmine.graphics.svg.rule.horizontal;

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
import org.contentmine.graphics.svg.text.build.Word;
public class HorizontalRule extends Rule {

	private static final Logger LOG = Logger.getLogger(HorizontalRule.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** allowed misalignment for "same Y"*/
	public static final double Y_TOLERANCE = 2.0;
	
	private List<Word> wordList;

	public HorizontalRule(SVGElement line) {
		super(line);
	}
	
	/** creates sorted lines.
	 * 
	 * @param lines
	 * @return
	 */
	public static List<HorizontalRule> createSortedRulersFromSVGList(List<SVGLine> lines) {
		List<HorizontalRule> rulerList = new ArrayList<HorizontalRule>();
		for (int i = 0; i < lines.size(); i++) {
			SVGLine line = lines.get(i);
			if (line.isHorizontal(epsilon)) {
				HorizontalRule ruler = new HorizontalRule(line);
				rulerList.add(ruler);
			}
		}
		Collections.sort(rulerList, new HorizontalRulerComparator());
		return rulerList;
	}
	

//	/** sorts lines
//	 * 
//	 * @param lines
//	 * @return
//	 */
//	public static List<HorizontalRuler> createFromSVGList(List<SVGLine> lines) {
//		List<HorizontalRuler> horizontalList = createSortedRulersFromSVGList(lines);
//		List<HorizontalRuler> rulerList = new ArrayList<HorizontalRuler>();
//		HorizontalRuler lastRuler = null;
//		for (int i = 0; i < horizontalList.size(); i++) {
//			lastRuler = i == 0 ? null : horizontalList.get(i - 1);
//			Double lastY = lastRuler == null ? null : lastRuler.getMidPoint().getY();
//			Horizontal line = lines.get(i);
//			LOG.trace("-----> "+line.toXML());
//			HorizontalRuler ruler = new HorizontalRuler(line);
//			lastRuler = ruler;
//			rulerList.add(lastRuler);
//		}
//		return rulerList;
//	}
	
	public IntRange getIntRange() {
		return new IntRange(getBoundingBox().getXRange());
	}
	
}
class HorizontalRulerComparator implements Comparator<HorizontalRule> {

	public int compare(HorizontalRule hr1, HorizontalRule hr2) {
		if (hr1 == null || hr2 == null || hr1.getIntRange() == null || hr2.getIntRange() == null) {
			return 0;
		}
		if (hr1.getY() < hr2.getY()) return -1;
		if (hr1.getY() > hr2.getY()) return 1;
		return hr1.getIntRange().getMin() - hr2.getIntRange().getMin();
	}

}

package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
/**
 * creates a series of nested graphical objects formed from horizontal ad graphical lines
 * may become a Cache later.
 * 
 * Initial motivation to detect axial boxes in a page
 * 
 * @author pm286
 *
 */
public class LineBoxCache extends ComponentCache {
	private static final Logger LOG = Logger.getLogger(LineBoxCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<SVGLine> horizontalLines;
	private List<SVGLine> verticalLines;
	private Set<SVGLine> usedHorizontalLineSet;
	private Set<SVGLine> usedVerticalLineSet;
	private Set<LineBox> boxSet;
	private LineBox currentBox;
	
	public LineBoxCache() {
		super();
	}
	
	@Override
	public List<? extends SVGElement> getOrCreateElementList() {
		throw new RuntimeException("NYI");
	}

	@Override
	public void clearAll() {
		throw new RuntimeException("NYI");
	}

	public void createLineBoxes(List<SVGLine> horizLines, List<SVGLine> vertLines) {
		Comparator<SVGLine> reversedLineLengthComparator
	      = Comparator.comparing(SVGLine::getLength).reversed();
	    this.horizontalLines = new ArrayList<>(horizLines);
		List<String> ss = Arrays.asList(new String[]{"ccc","h","aaaaa"});
		Collections.sort(ss,
			     (first, second) -> Integer.compare(first.length(), second.length()));
		ss.forEach(System.out::println);
		ss.sort(Comparator.comparing(s -> s.length()));
		Collections.sort(this.horizontalLines, 
				(first, second) -> Double.compare(first.getLength(), second.getLength()));
		LOG.debug("H "+horizontalLines.size());
//		horizontalLines.forEach(System.out::println);
		
		this.verticalLines = new ArrayList<>(vertLines);
		Collections.sort(verticalLines,  
				(first, second) -> Double.compare(first.getLength(), second.getLength()));
		LOG.debug("V "+verticalLines.size());
//		verticalLines.forEach(System.out::println);
		
		boxSet = new HashSet<>();
		usedHorizontalLineSet = new HashSet<>();
		usedVerticalLineSet = new HashSet<>();
		for (int i = horizontalLines.size() - 1; i >= 0; i--) {
			SVGLine horizLine = horizontalLines.get(i);
			currentBox = null;
			for (int j = verticalLines.size() - 1;j >= 0; j--) {
				SVGLine vertLine = verticalLines.get(j);
				if (horizLine.getBoundingBox().intersects(vertLine.getBoundingBox(), 1.0)) {
					ensureBoxAndAdd(horizLine, vertLine);
//					verticalLines.remove(vertLine);
				}
			}
		}
		LOG.debug("VL:" +verticalLines);
		
		LOG.debug("BOXES "+boxSet.size());
		LOG.debug("BB "+boxSet);
		SVGG g = new SVGG();
		for (LineBox box : boxSet) {
			LOG.debug(box.getHorizontalLineList().size()+"/"+box.getVerticalLineList().size()+"/"+box.getBoundingBox());
			g.appendChild(box.getSVGElement());
			SVGRect svgRect =  (SVGRect) SVGRect.createFromReal2Range(box.getBoundingBox()/*, 1.5*/).setFill("red").setStroke("blue").setStrokeWidth(0.3).setOpacity(0.4);
			g.appendChild(svgRect);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/cache/lineBox.svg"));
		LOG.debug("HS "+usedHorizontalLineSet.size()+"/"+usedHorizontalLineSet);
		LOG.debug("VS "+usedVerticalLineSet.size()+"/"+usedVerticalLineSet);
	}

	private void ensureBoxAndAdd(SVGLine horizLine, SVGLine vertLine) {
		if (currentBox == null) {
			currentBox = new LineBox();
			boxSet.add(currentBox);
		}
		currentBox.addHorizontalLine(horizLine);
		currentBox.addVerticalLine(vertLine);
		usedHorizontalLineSet.add(horizLine);
		usedVerticalLineSet.add(vertLine);
	}
	


}

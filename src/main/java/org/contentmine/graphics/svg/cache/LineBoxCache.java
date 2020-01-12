package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.GraphicsElement;
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
	private Set<LineBox> lineBoxSet;
	private LineBox currentBox;
	private Set<SVGLine> unusedLineSet;
	private Set<SVGLine> totalLineSet;
	private List<SVGElement> elementList;
	
	public LineBoxCache() {
		super();
	}
	
	@Override
	public List<? extends SVGElement> getOrCreateElementList() {
		if (elementList == null) {
			elementList = new ArrayList<SVGElement>();
			elementList.addAll(lineBoxSet);
			
		}
		return elementList;
	}

	@Override
	public void clearAll() {
		throw new RuntimeException("NYI");
	}

	public void createLineBoxes(List<SVGLine> horizLines, List<SVGLine> vertLines) {
		
	    totalLineSet = sortLinesByLengthCreateLineSet(horizLines, vertLines);
		createBoxesFromHorizontalVerticalIntersection();
		compressLineBoxList();
		createUnusedLineSet();
		addUnusedLinesToBoxes();
		
		LOG.debug("VL:" +verticalLines);
		
		LOG.debug("BOXES "+lineBoxSet.size());
		LOG.debug("BB "+lineBoxSet);
		SVGG g = new SVGG();
		for (LineBox box : lineBoxSet) {
//			LOG.debug(box.getHorizontalLineList().size()+"/"+box.getVerticalLineList().size()+"/"+box.getBoundingBox());
			g.appendChild(box.getSVGElement());
			SVGRect svgRect =  (SVGRect) SVGRect.createFromReal2Range(box.getBoundingBox()/*, 1.5*/).setFill("red").setStroke("blue").setStrokeWidth(0.3).setOpacity(0.4);
//			g.appendChild(svgRect);
		}
		LOG.debug("HS "+usedHorizontalLineSet.size()+"/"+usedHorizontalLineSet);
		LOG.debug("VS "+usedVerticalLineSet.size()+"/"+usedVerticalLineSet);
		LOG.debug("US "+unusedLineSet.size()+"/"+unusedLineSet);
		for (SVGLine unusedLine : unusedLineSet) {
			GraphicsElement line = ((SVGLine)unusedLine.copy()).setStrokeWidth(3.0).setStroke("cyan").setOpacity(0.5);
			g.appendChild(line);
		}
		
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/cache/lineBox.svg"));
	}

	private void addUnusedLinesToBoxes() {
		List<SVGLine> unusedLineList = new ArrayList<>(unusedLineSet); 
		for (LineBox lineBox : lineBoxSet) {
			for (int i = unusedLineList.size() - 1; i >= 0; i--) {
				SVGLine unusedLine = unusedLineList.get(i);
				if (lineBox.intersectsLine(unusedLine)) {
					lineBox.addLine(unusedLine);
					unusedLineSet.remove(unusedLine);
//					System.out.println("ADD line");
					unusedLineList.remove(unusedLine);
				}
			}
		}
	}

	private Set<SVGLine> createUnusedLineSet() {
		unusedLineSet = new HashSet<>(totalLineSet);
		unusedLineSet.removeAll(usedHorizontalLineSet);
		unusedLineSet.removeAll(usedVerticalLineSet);
		return unusedLineSet;
	}

	private void compressLineBoxList() {
		List<LineBox> lineBoxList = new ArrayList<>(lineBoxSet);
		boolean change = true;
		while (change) {
			change = false;
			int size = lineBoxList.size();
			for (int i = size - 1; i > 0; i--) {
				LineBox lineBoxi = lineBoxList.get(i);
				Real2Range bboxi = lineBoxi.getBoundingBox();
				for (int j = i - 1; j >= 0; j--) {
					LineBox lineBoxj = lineBoxList.get(j);
					Real2Range bboxj = lineBoxj.getBoundingBox();
					if (bboxi.intersects(bboxj)) {
						lineBoxi.merge(lineBoxj);
						lineBoxList.remove(j);
						change = true;
						break;
					}
				}
				if (change) break;
			}
		}
		lineBoxSet = new HashSet<>(lineBoxList);
	}

	private Set<SVGLine> sortLinesByLengthCreateLineSet(List<SVGLine> horizLines, List<SVGLine> vertLines) {
		totalLineSet = new HashSet<>();
		this.horizontalLines = new ArrayList<>(horizLines);
		totalLineSet.addAll(horizontalLines);
		Collections.sort(this.horizontalLines, 
				(first, second) -> Double.compare(first.getLength(), second.getLength()));
//		horizontalLines.forEach(System.out::println);
		
		this.verticalLines = new ArrayList<>(vertLines);
		Collections.sort(verticalLines,  
				(first, second) -> Double.compare(first.getLength(), second.getLength()));
		totalLineSet.addAll(verticalLines);
		return totalLineSet;
	}

	private void createBoxesFromHorizontalVerticalIntersection() {
		lineBoxSet = new HashSet<>();
		usedHorizontalLineSet = new HashSet<>();
		usedVerticalLineSet = new HashSet<>();
		for (int i = horizontalLines.size() - 1; i >= 0; i--) {
			SVGLine horizLine = horizontalLines.get(i);
			currentBox = null;
			for (int j = verticalLines.size() - 1;j >= 0; j--) {
				SVGLine vertLine = verticalLines.get(j);
				if (horizLine.getBoundingBox().intersects(vertLine.getBoundingBox(), 1.0)) {
					ensureBoxAndAdd(horizLine, vertLine);
				}
			}
		}
	}

	private void ensureBoxAndAdd(SVGLine horizLine, SVGLine vertLine) {
		if (currentBox == null) {
			currentBox = new LineBox();
			lineBoxSet.add(currentBox);
		}
		currentBox.addHorizontalLine(horizLine);
		currentBox.addVerticalLine(vertLine);
		usedHorizontalLineSet.add(horizLine);
		usedVerticalLineSet.add(vertLine);
	}
	

	
}

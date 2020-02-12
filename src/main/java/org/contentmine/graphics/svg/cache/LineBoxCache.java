package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
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
	private List<LineBox> lineBoxList;
	private LineBox currentBox;
	private Set<SVGLine> totalLineSet;
	private List<SVGElement> elementList;
	
	public LineBoxCache() {
		super();
		init();
	}
	
	private void init() {
		lineBoxList = new ArrayList<>();
	}

	@Override
	public List<? extends SVGElement> getOrCreateElementList() {
		if (elementList == null) {
			elementList = new ArrayList<SVGElement>();
			elementList.addAll(lineBoxList);
			
		}
		return elementList;
	}

	@Override
	public void clearAll() {
		throw new RuntimeException("NYI");
	}

	public void createLineBoxes(List<SVGLine> horizLines, List<SVGLine> vertLines) {
		Level level = LOG.getLevel();
//		LOG.setLevel(Level.TRACE);
		
		LOG.trace("H/L: "+horizLines.size()+"/"+vertLines.size());
	    totalLineSet = sortLinesByLengthCreateLineSet(horizLines, vertLines);
		createBoxesFromHorizontalVerticalIntersection1();
		compressLineBoxList();
//		createUnusedLineSet();
		
//		SVGSVG.wrapAndWriteAsSVG(new ArrayList<>(unusedLineSet), new File("target/cache/lineBox.unused0.svg"));
//		List<SVGLine>  unusedLines = addUnusedLinesToBoxes();
//		SVGSVG.wrapAndWriteAsSVG(unusedLines, new File("target/cache/lineBox.unused.svg"));
		SVGSVG.wrapAndWriteAsSVG(verticalLines, new File("target/cache/lineBox.vertical2.svg"));
		
		LOG.trace("VL:" +verticalLines);
		
		LOG.trace("BOXES "+lineBoxList.size());
		LOG.trace("BB "+lineBoxList);
		SVGG g = new SVGG();
		for (LineBox lineBox : lineBoxList) {
			g.appendChild(lineBox.getSVGElement());
			SVGRect svgRect =  (SVGRect) SVGRect.createFromReal2Range(lineBox.getBoundingBox()/*, 1.5*/).setFill("red").setStroke("blue").setStrokeWidth(0.3).setOpacity(0.4);
		}
//		LOG.trace("US "+unusedLineSet.size()+"/"+unusedLineSet);
//		for (SVGLine unusedLine : unusedLineSet) {
//			GraphicsElement line = ((SVGLine)unusedLine.copy()).setStrokeWidth(3.0).setStroke("cyan").setOpacity(0.5);
//			g.appendChild(line);
//		}
		
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/cache/lineBox.svg"));
		LOG.setLevel(level);
	}

//	private List<SVGLine> addUnusedLinesToBoxes() {
//		List<SVGLine> unusedLineList = new ArrayList<>(unusedLineSet); 
//		LOG.debug("creating line boxes from: "+unusedLineList.size()+"; lineboxes "+lineBoxList);
//		int count = 0;
//		for (LineBox lineBox : lineBoxList) {
//			SVGSVG.wrapAndWriteAsSVG(lineBox, new File("target/cache/linebox"+(count++)+".svg"));
//			for (int i = unusedLineList.size() - 1; i >= 0; i--) {
//				SVGLine unusedLine = unusedLineList.get(i);
//				if (lineBox.intersectsLine(unusedLine)) {
//					lineBox.addLine(unusedLine);
////					unusedLineSet.remove(unusedLine);
////					System.out.println("ADD line");
//					unusedLineList.remove(unusedLine);
//				}
//			}
//		}
//		LOG.debug("lines not in boxes: "+unusedLineList.size());
//		return unusedLineList;
		
//	}

//	private Set<SVGLine> createUnusedLineSet() {
//		LOG.debug("u0 "+totalLineSet.size());
//		unusedLineSet = new HashSet<>(totalLineSet);
//		LOG.debug("u1 "+unusedLineSet.size());
//		unusedLineSet.removeAll(usedHorizontalLineSet);
//		LOG.debug("u2 "+unusedLineSet.size());
//		unusedLineSet.removeAll(usedVerticalLineSet);
//		LOG.debug("u3 "+unusedLineSet.size());
//		return unusedLineSet;
//	}

	/** merges lineBoxes and reduces list */
	private void compressLineBoxList() {
//		List<LineBox> lineBoxList = new ArrayList<>(lineBoxSet);
		LOG.trace("lineBoxList "+lineBoxList);
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
//		lineBoxSet = new HashSet<>(lineBoxList);
	}

	private Set<SVGLine> sortLinesByLengthCreateLineSet(List<SVGLine> horizLines, List<SVGLine> vertLines) {
		totalLineSet = new HashSet<>();
		this.horizontalLines = new ArrayList<>(horizLines);
		totalLineSet.addAll(horizontalLines);
		Collections.sort(this.horizontalLines, 
				(first, second) -> Double.compare(first.getLength(), second.getLength()));
		this.verticalLines = new ArrayList<>(vertLines);
		Collections.sort(verticalLines,  
				(first, second) -> Double.compare(first.getLength(), second.getLength()));
		totalLineSet.addAll(verticalLines);
		return totalLineSet;
	}

	/** creates lineBoxes and depletes horizontalLines and verticalLines
	 * 
	 * @return
	 */
	private List<LineBox> createBoxesFromHorizontalVerticalIntersection1() {
		lineBoxList = new ArrayList<>();
		boolean change = true;
		while (change) {
			change = false;
			for (int ibox = lineBoxList.size() - 1; ibox >= 0; ibox--) {
				LineBox lineBox = lineBoxList.get(ibox);
				change |= addIntersectingLinesToBoxAndRemoveFromList(horizontalLines, LineDirection.HORIZONTAL, lineBox);
				LOG.trace(">h> "+change);
				change |= addIntersectingLinesToBoxAndRemoveFromList(verticalLines, LineDirection.HORIZONTAL, lineBox);
				LOG.trace(">v> "+change);
			}
			for (int ihor = horizontalLines.size() - 1; ihor >= 0; ihor--) {
				SVGLine horizLine = horizontalLines.get(ihor);
				for (int ivert = verticalLines.size() - 1; ivert >= 0; ivert--) {
					SVGLine vertLine = verticalLines.get(ivert);
					if (horizLine.getBoundingBox().intersects(vertLine.getBoundingBox(), 1.0)) {
						change |= makeBoxAndAdd(horizLine, vertLine);
						LOG.trace("added "+horizLine+"/"+vertLine);
						horizontalLines.remove(horizLine);
						verticalLines.remove(vertLine);
						break;
					}
				}
				if (change) break;
			}
		}
//		LOG.debug(message);
		lineBoxList = lineBoxList.stream()
				.sorted((o1,o2)-> o1.getBoundingBox().getYMax().compareTo(o2.getBoundingBox().getYMax()))
				.sorted((o1,o2)-> o1.getBoundingBox().getXMax().compareTo(o2.getBoundingBox().getXMax()))
				.collect(Collectors.toList())
				;
		return lineBoxList;
	}

	private boolean makeBoxAndAdd(SVGLine horizLine, SVGLine vertLine) {
		boolean change = false;
		LineBox lineBox = new LineBox();
		lineBoxList.add(lineBox);
		change |= lineBox.addHorizontalLine(horizLine);
		change |= lineBox.addVerticalLine(vertLine);
		return change;
	}

	private boolean addIntersectingLinesToBoxAndRemoveFromList(List<SVGLine> lines, LineDirection dir, LineBox lineBox) {
		boolean change = false;
		for (int ihor = lines.size() - 1; ihor >= 0; ihor--) {
			SVGLine line = lines.get(ihor);
			if (line.getBoundingBox().intersects(lineBox.getBoundingBox(), 1.0)) {
				change |= LineDirection.HORIZONTAL.equals(dir) ? lineBox.addHorizontalLine(line) :
					lineBox.addVerticalLine(line);
				lines.remove(ihor);
				change = true;
			}
		}
		return change;
	}

//	private boolean addHorizontalLines(LineBox lineBox) {
//		boolean change;
//		return addIntersectingLinesToBox(lineBox);
//	}

//	/** creates lineBoxes and depletes horizontalLines and verticalLines
//	 * 
//	 * @return
//	 */
//	private List<LineBox> createBoxesFromHorizontalVerticalIntersection() {
////		Set<LineBox> lineBoxSet = new HashSet<>();
//		usedHorizontalLineSet = new HashSet<>();
//		usedVerticalLineSet = new HashSet<>();
//		boolean change = true;
////		while (change) {
//			for (int i = horizontalLines.size() - 1; i >= 0; i--) {
//				change = false;
//				SVGLine horizLine = horizontalLines.get(i);
//				currentBox = null;
//				for (int j = verticalLines.size() - 1;j >= 0; j--) {
//					SVGLine vertLine = verticalLines.get(j);
//					if (horizLine.getBoundingBox().intersects(vertLine.getBoundingBox(), 1.0)) {
//						ensureBoxAndAdd(horizLine, vertLine);
//						LOG.debug("added "+horizLine+"/"+vertLine);
//						change = true;
////						horizontalLines.remove(i);
////						verticalLines.remove(j);
////						break;
//					}
//				}
////				if (!change) break;
//			}
////		}
//		lineBoxList = lineBoxList.stream()
//				.sorted((o1,o2)-> o1.getBoundingBox().getYMax().compareTo(o2.getBoundingBox().getYMax()))
//				.sorted((o1,o2)-> o1.getBoundingBox().getXMax().compareTo(o2.getBoundingBox().getXMax()))
//				.collect(Collectors.toList())
//				;
//		return lineBoxList;
//	}

	private void ensureBoxAndAdd(SVGLine horizLine, SVGLine vertLine) {
		if (currentBox == null) {
			currentBox = new LineBox();
			lineBoxList.add(currentBox);
		}
		currentBox.addHorizontalLine(horizLine);
		currentBox.addVerticalLine(vertLine);
	}
	/* cached boundingBox.
	 * The bbox may be reset 
	 * 
	 */
	public Real2Range getBoundingBox() {
		// there's a bug here
		for (SVGLine line : totalLineSet) {
			if (boundingBox == null) {
				boundingBox = line.getBoundingBox();
			} else {
				boundingBox.plusEquals(line.getBoundingBox());
			}
		}
		return boundingBox;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("lineboxList "+lineBoxList+"; ");
		sb.append("currentBox "+((currentBox == null) ? "null" : currentBox.toString())+"; ");
		String s = sb.toString();
		return s;
		
	}

	public List<LineBox> getOrCreateLineBoxList() {
		if (lineBoxList == null) {
			lineBoxList = new ArrayList<>();
		}
		return lineBoxList;
	}
	
}

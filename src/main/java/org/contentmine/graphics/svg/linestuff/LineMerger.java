package org.contentmine.graphics.svg.linestuff;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.linestuff.ComplexLine.Direction;
import org.contentmine.graphics.svg.linestuff.ComplexLine.LineOrientation;

/** a convenience class to help join lines.
 * <p>
 * Lines must be either horizontal or vertical. Merging is for parallel 
 * or optionally antiparallel) lines - i.e. those that are visually indistinguishable
 * from a (normally longer) line.
 * </p>
 * 
 * @author pm286
 *
 */
public class LineMerger extends ElementMerger {

	public enum MergeMethod {
		OVERLAP,
		TOUCHING_LINES, 
	}

	private final static Logger LOG = Logger.getLogger(LineMerger.class);
	private Direction direction; 
	private LineOrientation orientation;
	private MergeMethod method = MergeMethod.OVERLAP; // why is this the default?

	/** constructs a LineMerger.
	 * 
	 * <p>
	 * MergeMethod defaults to OVERLAP
	 * </p>
	 * @param line0
	 * @param eps
	 */
	public LineMerger(SVGLine line0, double eps) {
		this(line0, eps, MergeMethod.OVERLAP);
	}
	
	public LineMerger(SVGLine line0, double eps, MergeMethod method) {
		super(line0, eps);
		direction = ComplexLine.getLineDirection(line0, eps);
		orientation = ComplexLine.getLineOrientation(line0, eps);
		this.method = method;
	}
	
	public static LineMerger createLineMerger(SVGLine line0, double eps, MergeMethod method) {
		LineMerger lineJoin = null;
		if (!line0.isZero(eps)) {
			lineJoin = new LineMerger(line0, eps, method);
		}
		return lineJoin;
	}
	
	public void setMethod(MergeMethod method) {
		this.method = method;
	}
	public SVGElement createNewElement(AbstractCMElement line1x) {
		if (line1x == null) {
			throw new RuntimeException("null line1");
		}
		SVGElement result = null;
		if (line1x instanceof SVGLine) {
			result = mergeLineLine(line1x, method);
		}
		return result;
	}

	private SVGLine mergeLineLine(AbstractCMElement line1x, MergeMethod method) {
		SVGLine line0 = (SVGLine) elem0;
		SVGLine line1 = (SVGLine) line1x;
		
		SVGLine newLine = null;
		LineOrientation orientation1 = ComplexLine.getLineOrientation(line1, eps);
		Direction direction1 = ComplexLine.getLineDirection(line1, eps);
		if (orientation == null || orientation1 == null) {
			// pathological value
		} else if (orientation.equals(orientation1)) {
			if (MergeMethod.TOUCHING_LINES.equals(method)) {
				newLine = createTouchingLine(line0, line1, direction1);
			} else if (MergeMethod.OVERLAP.equals(method)) {
				newLine = createOverlappedLine(line0, line1);
			}
			String style0 = line0.getStyle();
			String style1 = line1.getStyle();
			// very crude - need a styling strategy
			if (style0 != null) {
				newLine.setCSSStyle(style0);
				if (!style0.equals(style1)) {
					LOG.warn("merging lines with different styles: " + style0 + " != " + style1);
				}
			}
			LOG.trace("lines "+line0.getId()+": "+line0.getXY(0)+"/"+line0.getXY(1)+"; "+line1.getXY(0)+"/"+line1.getXY(1)+" "+line1.getId());
			if (newLine != null) {
				newLine.setId(line0.getId()+"x");
			}
		} else {
			LOG.trace("Cannot make lines from: "+line0.getId()+" / "+line1.getId());
		}
		return newLine;
	}

	private SVGLine createTouchingLine(SVGLine line0, SVGLine line1, Direction direction1) {
		SVGLine newLine = null;
		Real2 point00 = line0.getXY(0);
		Real2 point01 = line0.getXY(1);
		Real2 point10 = line1.getXY(0);
		Real2 point11 = line1.getXY(1);
		if (direction.equals(direction1)) {
			if (point01.isEqualTo(point10, eps)) {
				newLine = new SVGLine(line0);
				newLine.setXY(point11, 1);
			} else if (point00.isEqualTo(point11, eps)) {
				newLine = new SVGLine(line0);
				newLine.setXY(point10, 0);
			}
		} else {
			// antiparallel
			if (point01.isEqualTo(point11, eps)) {
				newLine = new SVGLine(line0);
				newLine.setXY(point10, 1);
			} else if (point00.isEqualTo(point10, eps)) {
				newLine = new SVGLine(line0);
				newLine.setXY(point11, 0);
			}
		}
		return newLine;
	}
	
	private SVGLine createOverlappedLine(SVGLine line0, SVGLine line1) {
		SVGLine newLine = null;
		Real2Range bbox0 = BoundingBoxManager.createExtendedBox(line0, eps);
		Real2Range bbox1 = BoundingBoxManager.createExtendedBox(line1, eps);
		Real2Range inter = bbox1.intersectionWith(bbox0);
		if (inter != null) {
			Real2Range bbox00 = line0.getBoundingBox();
			Real2Range bbox10 = line1.getBoundingBox();
			Real2Range bbox01 = bbox00.plusEquals(bbox10);
			newLine = new SVGLine(bbox01.getLLURCorners()[0], bbox01.getLLURCorners()[1]);
		}
		return newLine;
	}
	
	/** merge the lines in the lits and create a new list.
	 * 
	 * should probably not be static, so we can add optios for merging
	 * e.g. check styles of lines
	 * 
	 * @param lineList
	 * @param eps
	 * @param method
	 * @return
	 */
	public static List<SVGLine> mergeLines(List<SVGLine> lineList, double eps, MergeMethod method) {
		LOG.trace("lines "+lineList.size());
		ElementNeighbourhoodManager enm = new ElementNeighbourhoodManager(lineList);
		List<SVGElement> elems;
		while (true) {
			enm.createTouchingNeighbours(eps);
			elems = enm.getElementList();
			LOG.trace("elems "+elems.size());
			SVGElement newElem = null;
			SVGElement oldElem = null;
			SVGElement oldElem1 = null;
			for (int i = 0; i < elems.size(); i++) {
				oldElem1 = elems.get(i);
				LineMerger lineMerger = LineMerger.createLineMerger((SVGLine)oldElem1, eps, method);
				ElementNeighbourhood neighbourhood = enm.getNeighbourhood(oldElem1);
				if (neighbourhood == null) {
					continue;
				}
				List<SVGElement> neighbours = neighbourhood.getNeighbourList();
				for (AbstractCMElement neighbour : neighbours) {
					if (neighbour instanceof SVGLine) {
						oldElem = (SVGLine) neighbour;
						newElem = lineMerger.createNewElement(oldElem);
						if (newElem != null) {
							LOG.trace(((SVGLine)oldElem1).getEuclidLine()+" + "+((SVGLine)oldElem).getEuclidLine()+" => "+((SVGLine)newElem).getEuclidLine());
							break;
						}
					}
				}
				if (newElem != null) {
					enm.replaceElementsByElement(newElem, Arrays.asList(new SVGElement[] {oldElem, oldElem1}));
					break;
				}
			} // end of loop through elements
			if (newElem == null) {
				break;
			}
		} // end of infinite loop
		List<SVGLine> lines = SVGLine.extractLines(enm.getElementList());
		return lines;
	}
}

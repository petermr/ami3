package org.contentmine.graphics.svg.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPathPrimitive;
import org.contentmine.graphics.svg.SVGText;

/** 
 * Container and manager for primitives from an SVGPath.
 * 
 * @author pm286
 */
public class PathPrimitiveList implements Iterable<SVGPathPrimitive> {
	private static final Logger LOG = Logger.getLogger(PathPrimitiveList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<SVGPathPrimitive> primitiveList;
	private boolean isClosed;
	private SVGPath svgPath;
	private String signature;
	private Real2Array coords;
	
	public PathPrimitiveList() {
	}
	
	public void add(SVGPathPrimitive primitive) {
		ensurePathPrimitiveList();
		primitiveList.add(primitive);
		setFirstPoints();
	}

	public void add(PathPrimitiveList primitiveList) {
		ensurePathPrimitiveList();
		this.primitiveList.addAll(primitiveList.getPrimitiveList());
		setFirstPoints();
	}

	private void ensurePathPrimitiveList() {
		if (primitiveList == null) {
			primitiveList = new ArrayList<SVGPathPrimitive>();
		}
	}
	
	/**
	 * sets first points of primitives to last coord of precedingPrimitive
	 * if last primitive (j) is Z, or isClosed,  set firstCoord of primitive(0) to lastCoord of primitive(j-1) 
	 * @param primitiveList
	 */
	void setFirstPoints() {
		ensurePathPrimitiveList();
		int nprim = primitiveList.size();
		for (int i = 1; i < nprim; i++) {
			primitiveList.get(i).setFirstPoint(primitiveList.get(i-1).getLastCoord());
		}
		if (primitiveList.get(nprim - 1) instanceof ClosePrimitive) {
			if (nprim > 1) {
				primitiveList.get(0).setFirstPoint(primitiveList.get(nprim-2).getLastCoord());
			}
		} else if (isClosed()) {
			if (nprim > 1) {
				primitiveList.get(0).setFirstPoint(primitiveList.get(nprim-1).getLastCoord());
			}
		}
	}
	
	public Iterator<SVGPathPrimitive> iterator() {
		ensurePathPrimitiveList();
		return primitiveList.iterator();
	}

	public int size() {
		ensurePathPrimitiveList();
		return primitiveList.size();
	}

	public SVGPathPrimitive get(int i) {
		ensurePathPrimitiveList();
		return (i < 0 || i >= primitiveList.size()) ? null : primitiveList.get(i);
	}

	public List<SVGPathPrimitive> getPrimitiveList() {
		return primitiveList;
	}

	/** does the end turn through PI.
	 * 
	 * Can be very messy.
	 * 
	 * @param i
	 * @param angleEps
	 * @return
	 */
	public Boolean isUTurn(int i, Angle angleEps) {
		Boolean uTurn = false;
		Integer turn = quadrantValue(i, angleEps) + quadrantValue(i + 1, angleEps);
		// does it make 2 quarter turns?
		if (Math.abs(turn) == 2) {
			uTurn = true;
		}
		// are existing lines antiparallel?
		if (!uTurn && isAntiParallel(i - 1,  i + 2, angleEps)) {
			uTurn = true;
		}
		// is it the last one? (this is -2 from end)
		if (!uTurn &&  i == this.size() - 2 &&
				isAntiParallel(i - 1,  1, angleEps)) {
			uTurn = true;
		}
		return uTurn;
	}

	/** get value of a quadrant.
	 * 
	 * @param i index of primitive
	 * @param angleEps max deviation from PI/2.
	 * @return 1 ifPI/2 turn, -1 if -PI/2 turn else 0
	 */
	public int quadrantValue(int i, Angle angleEps) {
		Integer value = 0;
		Angle angle = this.getAngle(i);
		if (angle != null && this.get(i) instanceof CubicPrimitive) {
			double delta = Math.abs( Math.abs(angle.getRadian()) - Math.PI / 2.);
			if (delta < angleEps.getRadian()) {
				value = angle.greaterThan(Math.PI / 4.) ? 1 : -1;
			}
		}
		return value;
	}

	public Angle getAngle(int i) {
		Angle angle = null;
		if (i >= 0 && i < primitiveList.size()) {
			angle = primitiveList.get(i).getAngle();
			if (angle != null) {
				angle.normalizeToPlusMinusPI();
			}
		}
		return angle;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
	
	public boolean isClosed() {
		return isClosed;
	}

	public List<Integer> getUTurnList(Angle angleEps) {
		List<Integer> quadStartList = new ArrayList<Integer>();
		for (int i = 0; i < primitiveList.size() - 1; i++) {
			SVGPathPrimitive primitive0 = primitiveList.get(i);
			SVGPathPrimitive primitive1 = primitiveList.get(i + 1);
			if (primitive0 instanceof CubicPrimitive &&
				primitive1 instanceof CubicPrimitive) {
				if (isUTurn(i, angleEps)) {
					quadStartList.add(i);
				}
			}
		}
		return quadStartList;
	}

	/**
	 * @param quad
	 * @deprecated Use replaceUTurnsByButt(int, false).
	 */
	public void replaceUTurnsByButt(int quad) {
		replaceUTurnsByButt(quad, false);
	}

	public void replaceUTurnsByButt(int quad, boolean extend) {
		LinePrimitive linePrimitive;
		if (extend) {
			CubicPrimitive cubic1 = (CubicPrimitive) primitiveList.get(quad);
			Real2 endPoint = cubic1.getLastCoord();
			Real2Array line1Coords = primitiveList.get(quad - 1).getCoordArray();
			Real2 newLine1End = getLine(quad - 1).getNearestPointOnLine(endPoint);
			line1Coords.setElement(line1Coords.size() - 1, newLine1End);
			//Real2Array coordsLine2 = primitiveList.get(quad + 2).getCoordArray();
			Real2 newLine2Start;
			if (getLine(quad + 2) == null) {
				newLine2Start = getLine(1).getNearestPointOnLine(endPoint);
				Real2Array moveCoords = primitiveList.get(0).getCoordArray();
				moveCoords.setElement(0, getLine(1).getNearestPointOnLine(endPoint));
			} else {
				newLine2Start = getLine(quad + 2).getNearestPointOnLine(endPoint);
			}
			//coordsLine2.setElement(coordsLine2.size() - 1, newLine2Start);
			linePrimitive = new LinePrimitive(newLine2Start);
		} else {
			//maybe test radius later
			CubicPrimitive cubic2 = (CubicPrimitive) primitiveList.get(quad + 1);
			Real2 point = cubic2.getLastCoord();
			linePrimitive = new LinePrimitive(point);
		}
		primitiveList.remove(quad + 1);
		primitiveList.remove(quad);
		primitiveList.add(quad, linePrimitive);
		setFirstPoints();
	}

	/** 
	 * Interprets primitive as line if possible.
	 * 
	 * @param i must be > 0
	 * @return line else null if i == 0
	 */
	public SVGLine getLine(int i) {
		SVGLine line = null;
		if (i > 0) {
			SVGPathPrimitive primitive = get(i);
			if (primitive instanceof LinePrimitive) {
				Real2 point0 = get(i - 1).getLastCoord();
				Real2 point1 = primitive.getFirstCoord();
				line = new SVGLine(point0, point1);
			}
		}
		return line;
	}

	public Arc getQuadrant(int i, Angle angleEps) {
		Arc quadrant = null;
		SVGPathPrimitive prim = primitiveList.get(i);
		if (prim instanceof CubicPrimitive) {
			Angle angle = prim.getAngle();
			if (angle.getRightAngle(angleEps) != 0) {
				quadrant = new Arc((CubicPrimitive) prim);
			}
		}
		return quadrant;
	}

	/** replaces coordinate array in given primitive.
	 * 
	 * if indexed primitive is of wrong type, no operation.
	 * 
	 * @param cubicPrimitive
	 * @param i
	 */
	public void replaceCoordinateArray(CubicPrimitive cubicPrimitive, int i) {
		if (primitiveList != null) {
			if (this.get(i) instanceof CubicPrimitive) {
				CubicPrimitive thisCubicPrimitive = (CubicPrimitive) this.get(i);
				thisCubicPrimitive.setCoordArray(cubicPrimitive.getCoordArray());
			}
		}
	}

	public String getDString() {
		return SVGPath.constructDString(this);
	}

	public void replaceCoordinateArray(Real2Array coordArray, int i) {
		if (primitiveList != null) {
			if (this.get(i) instanceof LinePrimitive) {
				this.get(i).setCoordArray(coordArray);
			}
		}
	}

	private LinePrimitive calculateMeanLine(int i, int j) {
		LinePrimitive linei = this.getLinePrimitive(i);
		LinePrimitive linej = this.getLinePrimitive(j);
		return (linei == null || linej == null) ? null : linei.calculateMeanLine(linej);
	}

	public LinePrimitive getLinePrimitive(int i) {
		SVGPathPrimitive primitive = get(i);
		return (primitive == null || !(primitive instanceof LinePrimitive) ?
			null : (LinePrimitive) primitive);
	}

	public LinePrimitive createMeanLine(int i, int j) {
		LinePrimitive line = calculateMeanLine(i, j);
		replaceCoordinateArray(line.getCoordArray(), i);
		replaceCoordinateArray(line.getReverseCoordArray(), j);
		return line;
	}

	public CubicPrimitive getCubicPrimitive(int i) {
		SVGPathPrimitive primitive = get(i);
		return (primitive == null || !(primitive instanceof CubicPrimitive) ?
			null : (CubicPrimitive) primitive);
	}

	public Arc createMeanCubic(int i, int j) {
		CubicPrimitive cubic0 = getCubicPrimitive(i);
		CubicPrimitive cubic1 = getCubicPrimitive(j);
		Arc arc0 = new Arc(cubic0);
		Arc arc1 = new Arc(cubic1);
		Arc meanArc = arc0.calculateMeanArc(arc1);
		replaceCoordinateArray(meanArc.getCubicPrimitive(), i);
		replaceCoordinateArray(meanArc.getReverseCubicPrimitive(), j);
		return meanArc;
	}

	public void remove(int i) {
		SVGPathPrimitive primitive = get(i);
		if (primitive != null) {
			primitiveList.remove(i);
		}
	}

	/** creates a line from thin rectangle.
	 * 
	 * @param angleEps
	 * @param maxWidth if great than this assumes it's a real rectangle.
	 * @return
	 */
	public SVGLine createLineFromMLLLL(Angle angleEps, double maxWidth) {
		SVGLine line1 = null;
		SVGLine line2 = null;
		if (isAntiParallel(1, 3, angleEps) && isShort(2, maxWidth) && isShort(4, maxWidth)) {
			line1 = createLineFromMidPoints(2, 4);
		}
		if (isAntiParallel(2, 4, angleEps) && isShort(1, maxWidth) && isShort(3, maxWidth)) {
			line2 = createLineFromMidPoints(1, 3);
		}
		return (line1 == null ? line2 : (line2 == null ? line1 : (line1.getLength() > line2.getLength() ? line1 : line2)));
	}

	/** creates a line from thin unclosed rectangle.
	 * 
	 * assumes M0-------------L1
	 *                        |
	 *         L3-------------L2
	 *       
	 *         where the numbers are the primitives (and also the indexes of the coord array)
	 *         
	 * @param angleEps tolerance for parallelity
	 * @param maxWidth if greater than this assumes it's a real unclosed rectangle.
	 * @return
	 */
	public SVGLine createLineFromMLLL(Angle angleEps, double maxWidth) {
		SVGLine line = null;
		Real2Array coords = getOrCreateCoordinates();
		if (coords.size() != 4) {
			throw new RuntimeException("coords "+coords.size()+" != 4");
		}
		SVGLine line1 = new SVGLine(coords.get(0), coords.get(1));
		SVGLine line2 = new SVGLine(coords.get(2), coords.get(3));
		
		if (line1.isAntiParallelTo(line2, angleEps)) {
			if (coords.get(1).getDistance(coords.get(2)) < maxWidth) {
				line = new SVGLine(coords.get(1).getMidPoint(coords.get(2)), coords.get(3).getMidPoint(coords.get(0)));
			}
		}
		return line;
	}

	private SVGLine createLineFromMidPoints(int i, int j) {
		SVGLine linei = getLine(i);
		SVGLine linej = getLine(j);
		return (linei == null || linej == null ? null : 
			new SVGLine(linei.getMidPoint(), linej.getMidPoint()));
	}

	private boolean isShort(int i, double maxWidth) {
		SVGLine line = getLine(i);
		return (line == null ? false : line.getLength() < maxWidth);
	}

	private boolean isAntiParallel(int i, int j, Angle angleEps) {
		SVGLine linei = getLine(i);
		SVGLine linej = getLine(j);
		return (linei == null || linej == null ? false : linei.isAntiParallelTo(linej, angleEps));
	}
	
	public String toString() {
		return Arrays.toString(primitiveList.toArray());
	}

	public static PathPrimitiveList createPrimitiveList(String d) {
		SVGPath path = new SVGPath(d);
		PathPrimitiveList primitiveList = path.getOrCreatePathPrimitiveList();
		return primitiveList;
	}

	public void format(int places) {
		for (SVGPathPrimitive primitive : this) {
			primitive.format(places);
		}
	}

	public String createSignature() {
		long millis = System.currentTimeMillis();
		StringBuilder sig = new StringBuilder();
		for (SVGPathPrimitive primitive : this) {
			sig.append(primitive.getTag());
		}
		long tt = (System.currentTimeMillis() - millis)/1000;
		if (tt > 1) {
			LOG.debug("SIG "+tt);
		}
		return sig.toString();
	}

	public String createD() {
		StringBuilder sb = new StringBuilder();
		for (SVGPathPrimitive primitive : this) {
			sb.append(primitive.toString());
		}
		return sb.toString();
	}

	/** splits primitiveList after a primitive.
	 * The most likely primitive is Z (ClosePrimitive)
	 * 
	 * @param splitPrimitive
	 * @return
	 */
	public List<PathPrimitiveList> splitAfter(Class<? extends SVGPathPrimitive> splitClass) {
		List<PathPrimitiveList> pathPrimitiveListList = new ArrayList<PathPrimitiveList>();
		Iterator<SVGPathPrimitive> iterator = this.iterator();
		PathPrimitiveList pathPrimitiveList = new PathPrimitiveList();
		pathPrimitiveListList.add(pathPrimitiveList);
		while (iterator.hasNext()) {
			SVGPathPrimitive pathPrimitive = iterator.next();
			pathPrimitiveList.add(pathPrimitive);
			if (pathPrimitive instanceof ClosePrimitive) {
				if (iterator.hasNext()) {
					pathPrimitiveList = new PathPrimitiveList();
					pathPrimitiveListList.add(pathPrimitiveList);
				}
			}
		}
		return pathPrimitiveListList;
	}

	/** splits primitiveList after a primitive.
	 * The most likely primitive is M (MoveePrimitive)
	 * 
	 * @param splitPrimitive
	 * @return
	 */
	public List<PathPrimitiveList> splitBefore(Class<? extends SVGPathPrimitive> pathClass) {
		List<PathPrimitiveList> pathPrimitiveListList = new ArrayList<PathPrimitiveList>();
		Iterator<SVGPathPrimitive> iterator = this.iterator();
		PathPrimitiveList currentPathPrimitiveList = null;
		PathPrimitiveList lastList = null;
		while (iterator.hasNext()) {
			boolean merged = false;
			SVGPathPrimitive pathPrimitive = iterator.next();
			if (pathPrimitive.getClass().equals(pathClass)) {
				if (lastList != null) {
					Real2Range lastBBox = lastList.getBoundingBox();
					Real2Range currentBBox = currentPathPrimitiveList.getBoundingBox();
					if (lastBBox.includes(currentBBox)) {
						lastList.add(currentPathPrimitiveList);
//						LOG.debug("P0 "+pathPrimitiveListList.size());
						pathPrimitiveListList.remove(currentPathPrimitiveList);
//						LOG.debug("P1 "+pathPrimitiveListList.size());
						merged = true;
					}
					if (currentBBox.includes(lastBBox)) {
						throw new RuntimeException("Unprocessed: "+currentBBox+" CONTAINS2 "+lastBBox);
					}
				}
				if (!merged) {
					lastList = currentPathPrimitiveList;
				}
				currentPathPrimitiveList = new PathPrimitiveList();
				pathPrimitiveListList.add(currentPathPrimitiveList);
			}
			currentPathPrimitiveList.add(pathPrimitive);
		}
		return pathPrimitiveListList;
	}

	public AbstractCMElement createAnnotatedSVG(String title) {
		AbstractCMElement g = new SVGG();
		String sig = createSignature();
		SVGPath path = new SVGPath(this);
		path.setCSSStyle("fill:none;stroke-width:0.3;stroke:blue;");
		g.appendChild(path);
		SVGText num = SVGText.createDefaultText(path.getXY(), title);
		num.setFontSize(2.0);
		g.appendChild(num);
		SVGCircle circle = new SVGCircle(path.getXY(), 0.7);
		circle.setCSSStyle("fill:none;stroke:cyan;stroke-width:0.1;");
		g.appendChild(circle);
		return g;
	}

	/** create a wrapping SVGPath.
	 * 
	 * @return
	 */
	public SVGPath getOrCreateSVGPath() {
		if (svgPath == null) {
			svgPath = new SVGPath(this);
		}
		return svgPath;
	}

	public String getOrCreateSignature() {
		if (signature == null) {
			signature = getOrCreateSVGPath().getOrCreateSignatureAttributeValue();
		}
		return signature;
	}
	
	/** transforms THIS
	 * iterates over primitives
	 * 
	 * @param t2
	 */
	public void transformBy(Transform2 t2) {
		for (SVGPathPrimitive primitive : primitiveList) {
			primitive.transformBy(t2);
		}
	}

	Real2Range getBoundingBox() {
		return getOrCreateSVGPath().getBoundingBox();
	}
	
	public Real2Array getOrCreateCoordinates() {
		if (coords == null) {
			coords = new Real2Array(this.size());
			for (int i = 0; i < primitiveList.size(); i++) {
				Real2 xy = primitiveList.get(i).getFirstCoord();
				coords.setElement(i, xy);
			}
		}
		return coords;
	}
}

/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.graphics.svg;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Angle.Units;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.path.Arc;
import org.contentmine.graphics.svg.path.ClosePrimitive;
import org.contentmine.graphics.svg.path.CubicPrimitive;
import org.contentmine.graphics.svg.path.LinePrimitive;
import org.contentmine.graphics.svg.path.MovePrimitive;
import org.contentmine.graphics.svg.path.PathPrimitiveList;
import org.contentmine.graphics.svg.path.SVGPathParser;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

/** 
 * @author pm286
 */
public class SVGPath extends SVGShape {

	private static final String M_NULL = "M";
	private static Logger LOG = Logger.getLogger(SVGPath.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String SIGNATURE = "signature";
	private static final String MZ = "MZ";
	private static final String ML = "ML";
	private static final String NOT_L = "[^L]";
	private static final String NOT_C = "[^C]";
	private static final String SP = " ";
	private static final String Z = "Z";
	private static final String C = "C";
	private static final String Q = "Q";
	private static final String L = "L";
	private static final String M = "M";
	public final static String MLQCZ = "MLQCZ"; // order of segTypes
	private static final String BLUE = "blue";
	public static final String MCCCC = "MCCCC";
	public static final String MCCCCZ = "MCCCCZ";
	public static final String NONE = "none";
	public static final String BLACK = "black";
	public static final String MLLL = "MLLL";
	public static final String MLLLZ = "MLLLZ";
	public static final String MLLLL = "MLLLL";
	public static final String MLLLLZ = "MLLLLZ";
	public final static String CC = "CC";
	public final static String D = "d";
	public final static String TAG ="path";
	private final static double EPS1 = 0.000001;
	private final static double MIN_COORD = .00001;
	public final static String ALL_PATH_XPATH = ".//svg:path";
	public final static String REMOVED = "removed";
	public final static String ROUNDED_CAPS = "roundedCaps";
	private static final Double ANGLE_EPS = 0.01;
	private static final Double MAX_WIDTH = 2.0;
	public final static Pattern REPEATED_ML = Pattern.compile("ML(ML)*");
	private static final double CIRCLE_EPSILON = 0.01;
	private static final int DSTRING_MAX = 100000 ; // omit longer attributes 
	
	protected GeneralPath path2;
	protected boolean isClosed = false;
	protected Real2Array coords = null; // for diagnostics
	protected SVGPolyline polyline;
	protected Real2Array allCoords;
	protected PathPrimitiveList primitiveList;
	protected Boolean isPolyline;
	protected Real2Array firstCoords;
//	protected String signature;

	/** 
	 * Constructor
	 */
	public SVGPath() {
		super(TAG);
		init();
	}
	
	/** 
	 * Constructor
	 */
	public SVGPath(SVGPath element) {
        super(element);
	}
	
	/** 
	 * Constructor
	 */
	public SVGPath(GeneralPath generalPath) {
        super(TAG);
        String d = SVGPath.constructDString(generalPath);
        setDString(d);
	}
	
	public SVGPath(Shape shape) {
		super(TAG);
		PathIterator pathIterator = shape.getPathIterator(new AffineTransform());
		String pathString = SVGPath.getPathAsDString(pathIterator);
		this.setDString(pathString);
	}

	
	/** constructor
	 */
	public SVGPath(Element element) {
        super((SVGElement) element);
	}
	
	public SVGPath(Real2Array xy) {
		this(createD(xy));
	}
	
	public SVGPath(String d) {
		this();
		setDString(d);
	}
	
	public SVGPath(PathPrimitiveList primitiveList, SVGPath reference) {
		this();
		if (primitiveList == null) {
			throw new RuntimeException("null primitiveList");
		}
		if (reference != null) {
			XMLUtil.copyAttributes(reference, this);
			// might be cached
			Attribute sig = this.getAttribute(SIGNATURE);
			if (sig != null) {
				this.removeAttribute(sig);
			}
		}
		String d = primitiveList.createD();
		setDString(d);
	}
	
	public SVGPath(PathPrimitiveList primitives) {
		this(primitives, null);
	}

	/**
     * Copies node.
     *
     * @return Node
     */
    public Node copy() {
        return new SVGPath(this);
    }

	protected void init() {
		super.setDefaultStyle();
//		setDefaultStyle(this);
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	public static void setDefaultStyle(SVGPath path) {
		path.setStroke(BLACK);
		path.setStrokeWidth(0.5);
		path.setFill(NONE);
	}
	
//	/** 
//	 * Creates a list of primitives
//	 * <p>
//	 * At present Move, Line, Curve, Z
//	 * 
//	 * NOTE - use getOrCreate
//	 * @param d
//	 * @return
//	 */
//	public PathPrimitiveList parseDString() {
//		String d = getDString();
//		return (d == null ? null : new SVGPathParser().parseDString(d));
//	}
	
    private static String createD(Real2Array xy) {
		String s = XMLConstants.S_EMPTY;
		StringBuilder sb = new StringBuilder();
		if (xy.size() > 0) {
			sb.append(M);
			sb.append(xy.get(0).getX()+S_SPACE);
			sb.append(xy.get(0).getY()+S_SPACE);
		}
		if (xy.size() > 1) {
			for (int i = 1; i < xy.size(); i++ ) {
				sb.append(L);
				sb.append(xy.get(i).getX()+S_SPACE);
				sb.append(xy.get(i).getY()+S_SPACE);
			}
			sb.append(Z);
		}
		s = sb.toString();
		return s;
	}
	
	public void setD(Real2Array r2a) {
		this.setDString(createD(r2a));
	}
	
	public void setDString(String d) {
		if (d != null) {
			this.addAttribute(new Attribute(D, d));
		}
	}
	
	public String getDString() {
		return this.getAttributeValue(D);
	}
	
	
//  <g style="stroke-width:0.2;">
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="black" style="stroke-width:0.36;"/>
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="white" style="stroke-width:0.12;"/>
//</g>
	
	protected void drawElement(Graphics2D g2d) {
		saveGraphicsSettingsAndApplyTransform(g2d);
		setAntialiasing(g2d, true);
//		setAntialiasing(g2d, false);
		GeneralPath path = createPath2D();
		path.transform(cumulativeTransform.getAffineTransform());
		drawFill(g2d, path);
		restoreGraphicsSettingsAndTransform(g2d);
	}

	/** 
	 * Extract polyline if path is M followed by Ls
	 * 
	 * @return
	 */
	public void createCoordArray() {
		polyline = null;
		allCoords = new Real2Array();
		firstCoords = new Real2Array();
		getOrCreatePathPrimitiveList();
		isPolyline = true;
		for (SVGPathPrimitive primitive : primitiveList) {
			if (primitive instanceof CubicPrimitive) {
				isPolyline = false;
				Real2Array curveCoords = primitive.getCoordArray();
				allCoords.add(curveCoords);
				firstCoords.add(primitive.getFirstCoord());
				//break;
			} else if (primitive instanceof ClosePrimitive) {
				isClosed = true;
			} else {
				Real2 r2 = primitive.getFirstCoord();
				allCoords.add(r2);
				firstCoords.add(r2);
			}
		}
	}
	
	public SVGPoly createPolyline() {
		createCoordArray();
		if (isPolyline && allCoords.size() > 1) {
			polyline = new SVGPolyline(allCoords);
			polyline.setClosed(isClosed);
		}
		return polyline;
	}
	
	public SVGRect createRectangle(double epsilon) {
		createPolyline();
		return polyline == null ? null : polyline.createRect(epsilon);
	}

	public SVGSymbol createSymbol(double maxWidth) {
		createCoordArray();
		SVGSymbol symbol = null;
		Real2Range r2r = getBoundingBox();
		if (Math.abs(r2r.getXRange().getRange()) < maxWidth && Math.abs(r2r.getYRange().getRange()) < maxWidth) {
			symbol = new SVGSymbol();
			SVGPath path = (SVGPath) copy();
			Real2 orig = path.getOrigin();
			path.normalizeOrigin();
			SVGElement line = path.createHorizontalOrVerticalLine(SVGLine.EPS);
			symbol.appendChild(path);
			symbol.setId(path.getId()+".s");
			List<SVGElement> defsNodes = SVGUtil.getQuerySVGElements(this, "/svg:svg/svg:defs");
			defsNodes.get(0).appendChild(symbol);
		}
		return symbol;
	}

	private SVGElement createHorizontalOrVerticalLine(double eps) {
		SVGLine  line = null;
		Real2Array coords = getCoords();
		if (coords.size() == 2) {
			line = new SVGLine(coords.get(0), coords.get(1));
			if (!line.isHorizontal(eps) && !line.isVertical(eps)) {
				line = null;
			}
		}
		return  line;
	}

	/** sometimes polylines represent circles
	 * crude algorithm - assume points are roughly equally spaced
	 * @param maxSize
	 * @param epsilon
	 * @return
	 */
	public SVGCircle createCircle(double epsilon) {
		createCoordArray();
		SVGCircle circle = null;
		String signature = getOrCreateSignatureAttributeValue();
		if (signature.equals(MCCCCZ) || signature.equals(MCCCC) && isClosed) {
			PathPrimitiveList primList = getOrCreatePathPrimitiveList();
			Angle angleEps = new Angle(0.05, Units.RADIANS);
			Real2Array centreArray = new Real2Array();
			RealArray radiusArray = new RealArray();
			for (int i = 1; i < 5; i++) {
				Arc arc = primList.getQuadrant(i, angleEps);
				if (arc != null) {
					Real2 centre = arc.getCentre();
					if (centre != null) {
						centreArray.add(centre);
						double radius = arc.getRadius();
						radiusArray.addElement(radius);
					}
				} else {
					LOG.trace("null quadrant");
				}
			}
			Real2 meanCentre = centreArray.getMean();
			Double meanRadius = radiusArray.getMean();
			if (meanCentre != null) {
				circle = new SVGCircle(meanCentre, meanRadius);
			}
		} else if (isClosed && allCoords.size() >= 8) {
			// no longer useful I think
		}
		return circle;
	}

	@Deprecated // use getOrCreatePrimitives()
	public PathPrimitiveList ensurePrimitives() {
		return getOrCreatePathPrimitiveList();
	}
	
	public PathPrimitiveList getOrCreatePathPrimitiveList() {
		isClosed = false;
		if (primitiveList == null) {
			primitiveList = getOrCreatePathPrimitives();
		}
		if (primitiveList.size() > 1) {
			SVGPathPrimitive primitive0 = primitiveList.get(0);
			SVGPathPrimitive primitiveEnd = primitiveList.get(primitiveList.size() - 1);
			Real2 coord0 = primitive0.getFirstCoord();
			Real2 coordEnd = primitiveEnd.getLastCoord();
			isClosed = coord0.getDistance(coordEnd) < EPS1;
			primitiveList.setClosed(isClosed);
		}
		return primitiveList;
	}

	/**
	 * Do two paths have identical coordinates?
	 * 
	 * @param svgPath 
	 * @param path2
	 * @param epsilon tolerance allowed
	 * @return
	 */
	public boolean hasEqualCoordinates(SVGPath path2, double epsilon) {
		Real2Array r2a = getCoords();
		Real2Array r2a2 = path2.getCoords();
		return r2a.isEqualTo(r2a2, epsilon);
	}
	
	public Real2Array getCoords() {
		coords = new Real2Array();
		PathPrimitiveList primitives = getOrCreatePathPrimitives();
		for (SVGPathPrimitive primitive : primitives) {
			Real2 coord = primitive.getFirstCoord();
			Real2Array coordArray = primitive.getCoordArray();
			if (coord != null) {
				coords.addElement(coord);
			} else if (coordArray != null) {
				coords.add(coordArray);
			}
		}
		return coords;
	}
	
	/**
	 * Scale of bounding boxes
	 * <p>
	 * scale = Math.sqrt(xrange2/this.xrange * yrange2/this.yrange)
	 * <p> 
	 * (Can be used to scale vector fonts or other scalable objects)
	 * 
	 * @param path2
	 * @return null if problem (e.g. zero ranges). result may be zero
	 */
	public Double getBoundingBoxScalefactor(SVGPath path2) {
		Double s = null;
		if (path2 != null) {
			Real2Range bb = this.getBoundingBox();
			Real2Range bb2 = path2.getBoundingBox();
			double xr = bb.getXRange().getRange();
			double yr = bb.getYRange().getRange();
			if (xr > SVGConstants.EPS && yr > SVGConstants.EPS) {
				s = Math.sqrt(bb2.getXRange().getRange()/xr * bb2.getYRange().getRange()/yr);
			}
		}
		return s;
	}
	
	/**
	 * compares paths scaled by bounding boxes and then compares coordinates
	 * @param path2
	 * @return null if paths cannot be scaled else bounding box ratio
	 */
	public Double getScalefactor(SVGPath path2, double epsilon) {
		Double s = this.getBoundingBoxScalefactor(path2);
		if (s != null) {
			SVGPath path = (SVGPath) this.copy();
			path.normalizeOrigin();
			Transform2 t2 = new Transform2(new double[]{s,0.,0.,0.,s,0.,0.,0.,1.});
			path.applyTransformPreserveUprightText(t2);
			SVGPath path22 = (SVGPath) path2.copy();
			path22.normalizeOrigin();
			if (!path.hasEqualCoordinates(path22, epsilon)) {
				s = null;
			}
		}
		return s;
	}

	private PathPrimitiveList getOrCreatePathPrimitives() {
		if (primitiveList == null) {
			String dString = getDString();
			if (dString == null) {
				primitiveList = new PathPrimitiveList();
			} else if (dString.length() > DSTRING_MAX) {
				LOG.debug("skipped long DString: "+dString.length());
				primitiveList = new PathPrimitiveList();
			} else {
				primitiveList = new SVGPathParser().parseDString(dString);
			}
		}
		return primitiveList;
	}

	/** 
	 * Gets bounding box
	 * <p>
	 * Uses coordinates given and ignores effect of curves
	 */
	@Override
	public Real2Range getBoundingBox() {
		if (boundingBox == null) {
			getCoords();
			if (coords.size() == 0) {
//				LOG.debug("X"+this.toXML());
//				LOG.debug("D"+this.getDString());
//				LOG.debug("sig "+getOrCreateSignatureAttributeValue());
//				throw new RuntimeException("XX");
			} else {
				boundingBox = coords.getRange2();
			}
		}
		return boundingBox;
	}
	
	/** 
	 * Property of graphic bounding box
	 * <p>
	 * Can be overridden
	 * 
	 * @return default none
	 */
	protected String getBBFill() {
		return NONE;
	}

	/** 
	 * Property of graphic bounding box
	 * <p>
	 * Can be overridden
	 * 
	 * @return default blue
	 */
	protected String getBBStroke() {
		return BLUE;
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default 0.1
	 */
	protected double getBBStrokeWidth() {
		return 0.1;
	}

	public GeneralPath createPath2D() {
		path2 = new GeneralPath();
		getOrCreatePathPrimitiveList();
		for (SVGPathPrimitive pathPrimitive : primitiveList) {
			pathPrimitive.operateOn(path2);
		}
		return path2;
	}
	
	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public GeneralPath getPath2() {
		return path2;
	}

	public void setPath2(GeneralPath path2) {
		this.path2 = path2;
	}
	
	public void applyTransformPreserveUprightText(Transform2 t2) {
		PathPrimitiveList pathPrimitives = this.getOrCreatePathPrimitives();
		for (SVGPathPrimitive primitive : pathPrimitives) {
			primitive.transformBy(t2);
		}
		setD(pathPrimitives);
	}
	
	public void format(int places) {
		super.format(places);
		String d = getDString();
		d = SVGPathPrimitive.formatDString(d, places);
		this.setDString(d);
	}

	@Override
	public String createSignatureFromDStringPrimitives() {
		String signature = null;
		String dString = getDString();
		long millis = System.currentTimeMillis();
		if (dString != null) {
			int length = dString.length();
			boolean longString = length > DSTRING_MAX;
			if (longString) {
				LOG.debug("skipped long string: "+length);
				signature = M_NULL;
				primitiveList = new PathPrimitiveList(); // empty, don't recompute
			} else {
				if (length > 10000) {
					LOG.debug("longSig: "+length);
				}
	 			getOrCreatePathPrimitiveList();
	 			long mm = System.currentTimeMillis();
	 			long tt = (mm - millis)/1000;
	 			boolean long1 = false;
	 			if (tt > 1) {
	 				LOG.debug("T "+tt+"; "+";"+length+";"+longString);
	 				long1 = true;
	 			}
				signature = primitiveList.createSignature();
	 			long mmm = System.currentTimeMillis();
	 			long ttt = (mmm - mm)/1000;
	 			if (long1) {
//	 				LOG.debug("TT "+ttt+"; "+";"+length+";"+longString);
	 			}
	 			if (length > 10000) {
	 				LOG.trace("long path: "+primitiveList.size());
	 			}
			}
		} else {
			signature = M_NULL;
		}
		return signature;
	}

	public void normalizeOrigin() {
		boundingBox = null; // fprce recalculate
		Real2Range boundingBox = this.getBoundingBox();
		if (boundingBox == null) {
			throw new RuntimeException("NULL BoundingBox");
		}
		RealRange xr = boundingBox.getXRange();
		RealRange yr = boundingBox.getYRange();
		Real2 xymin = new Real2(xr.getMin(), yr.getMin());
		xymin = xymin.multiplyBy(-1.0);
		Transform2 t2 = new Transform2(new Vector2(xymin));
		applyTransformationToPrimitives(t2);
	}

	private void applyTransformationToPrimitives(Transform2 t2) {
		PathPrimitiveList primitives = this.getOrCreatePathPrimitiveList();
		for (SVGPathPrimitive primitive : primitives) {
			primitive.transformBy(t2);
		}
		this.setD(primitives);
	}

	private void setD(PathPrimitiveList primitives) {
		String d = constructDString(primitives);
		this.addAttribute(new Attribute(D, d));
	}


	public static String constructDString(GeneralPath generalPath) {
		// should create a new Iterator
		PathIterator pathIterator = generalPath.getPathIterator(new AffineTransform());
		return getPathAsDString(pathIterator);
	}

	public static String getPathAsDString(PathIterator pathIterator) {
		// if the iterator is constructed with isDone() = true something has gone wrong. 
		// This kludge allows us to navigate to the first Z and hope
		boolean kludgeIterate = pathIterator.isDone();
		if (kludgeIterate) {System.err.print("!K");}
		if (kludgeIterate) {
			return createKludgedDString(pathIterator);
		} else {
			return createDString(pathIterator);
		}
	}

	private static String createDString(PathIterator pathIterator) {
		double[] coords = new double[6];
		StringBuilder dd = new StringBuilder();
		while (!pathIterator.isDone()) {
			int segType = pathIterator.currentSegment(coords);
			coords = normalizeSmallCoordsToZero(coords);
			if (PathIterator.SEG_MOVETO == segType) {        // 0
				dd.append(SP+M+SP+coords[0]+SP+coords[1]);
			} else if (PathIterator.SEG_LINETO == segType) { // 1
				dd.append(SP+L+SP+coords[0]+SP+coords[1]);
			} else if (PathIterator.SEG_QUADTO == segType) { // 2
				dd.append(SP+Q+SP+coords[0]+SP+coords[1]+SP+coords[2]+SP+coords[3]);
			} else if (PathIterator.SEG_CUBICTO == segType) { // 3
				dd.append(SP+C+SP+coords[0]+SP+coords[1]+SP+coords[2]+SP+coords[3]+SP+coords[4]+SP+coords[5]);
			} else if (PathIterator.SEG_CLOSE == segType) {  // 4
				dd.append(SP+Z+SP);
			} else {
				LOG.debug("ST: "+segType);
				throw new RuntimeException("UNKNOWN "+segType);
			}
			try {
				pathIterator.next();
			} catch (ArrayIndexOutOfBoundsException aioobe) {
				LOG.debug("AIOOBE in getPathAsDString()");
				break;
			}
		}
		return dd.toString();
	}

	private static String createKludgedDString(PathIterator pathIterator) {
		double[] coords = new double[6];
		StringBuilder dd = new StringBuilder();
		while (true) {
			int segType;			
			// this is horrible but the only way of escaping from a zero-element iteration
			try {
				segType = pathIterator.currentSegment(coords);
			} catch (ArrayIndexOutOfBoundsException aioobe) {
				break;
			}
			coords = normalizeSmallCoordsToZero(coords);
			if (PathIterator.SEG_MOVETO == segType) {        // 0
				dd.append(SP+M+SP+coords[0]+SP+coords[1]);
			} else if (PathIterator.SEG_LINETO == segType) { // 1
				dd.append(SP+L+SP+coords[0]+SP+coords[1]);
			} else if (PathIterator.SEG_QUADTO == segType) { // 2
				dd.append(SP+Q+SP+coords[0]+SP+coords[1]+SP+coords[2]+SP+coords[3]);
			} else if (PathIterator.SEG_CUBICTO == segType) { // 3
				dd.append(SP+C+SP+coords[0]+SP+coords[1]+SP+coords[2]+SP+coords[3]+SP+coords[4]+SP+coords[5]);
			} else if (PathIterator.SEG_CLOSE == segType) {  // 4
				dd.append(SP+Z+SP);
			} else {
				LOG.debug("ST: "+segType);
				throw new RuntimeException("UNKNOWN "+segType);
			}
			try {
				pathIterator.next();
			} catch (ArrayIndexOutOfBoundsException aioobe) {
				break;
			}
		}
		String dString = dd.toString().trim();
		return stripTrailingMoves(dString);
	}

	private static String stripTrailingMoves(String d) {
		// split and trim off trailing moves
		List<String> dList = new ArrayList<String>(Arrays.asList(d.split("\\s+")));
		int l = dList.size();
		while (l >= 3) {
			l -= 3;
			if (!dList.get(l).equals(M)) {
				l += 3;
				break;
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < l; i++) {
			sb.append(dList.get(i)+" ");
		}
		return sb.toString();
	}

	private static double[] normalizeSmallCoordsToZero(double[] coords) {
		for (int i = 0; i < coords.length; i++) {
			if (!Double.isNaN(coords[i]) && Math.abs(coords[i]) < MIN_COORD) {
				coords[i] = 0.0;
			}
		}
		return coords;
	}

	public static String constructDString(PathPrimitiveList primitives) {
		StringBuilder dd = new StringBuilder();
		for (SVGPathPrimitive primitive : primitives) {
			dd.append(primitive.toString());
		}
		return dd.toString();
	}

	public Real2 getOrigin() {
		Real2Range r2r = this.getBoundingBox();
		return new Real2(r2r.getXMin(), r2r.getYMin());
	}

	/** opposite corner to origin
	 * 
	 * @return
	 */
	public Real2 getUpperRight() {
		Real2Range r2r = this.getBoundingBox();
		return new Real2(r2r.getXMax(), r2r.getYMax());
	}

	// there are some polylines which contain a small number of curves and may be transformable
	public SVGPoly createHeuristicPolyline(int minL, int maxC, int minPrimitives) {
		SVGPoly polyline = null;
		String signature = this.getOrCreateSignatureAttributeValue();
		if (signature.length() < 3) {
			return null;
		}
		// must start with M
		if (signature.charAt(0) != 'M') {
			return null;
		}
		// can only have one M
		if (signature.substring(1).indexOf(M) != -1) {
			return null;
		}
		signature.replaceAll(NOT_C, "").length();
		StringBuilder sb = new StringBuilder();
		if (signature.length() >= minPrimitives) {
			int cCount = signature.replaceAll(NOT_C, "").length();
			int lCount = signature.replaceAll(NOT_L, "").length();
			if (lCount >= minL && maxC >= cCount) {
				for (SVGPathPrimitive primitive : primitiveList) {
					if (primitive instanceof CubicPrimitive) {
						sb.append(L+primitive.getLastCoord().toString());
					} else {
						sb.append(primitive.toString());
					}
				}
			}
			SVGPath path = new SVGPath(sb.toString());
			polyline = new SVGPolyline(path);
		}
		return polyline;
	}
	/** currently a no-op */
	public SVGShape createRoundedBox(double roundedBoxEps) {
		return null;
	}

	/** 
	 * Makes a new list composed of the paths in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGPath> extractPaths(List<SVGElement> elements) {
		List<SVGPath> pathList = new ArrayList<SVGPath>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGPath) {
				pathList.add((SVGPath) element);
			}
		}
		return pathList;
	}

	@Override
	public String getGeometricHash() {
		return getDString();
	}

	/** 
	 * Convenience method to extract list of svgPaths in element
	 * 
	 * @param svgElement
	 * @return
	 */
	public static List<SVGPath> extractPaths(AbstractCMElement svgElement) {
		return SVGPath.extractPaths(SVGUtil.getQuerySVGElements(svgElement, ALL_PATH_XPATH));
	}

	public static List<SVGPath> extractSelfAndDescendantPaths(AbstractCMElement svgElement) {
		return SVGPath.extractPaths(SVGUtil.getQuerySVGElements(svgElement, ALL_PATH_XPATH));
	}

	/** 
	 * Not finished
	 * 
	 * @param svgPath
	 * @param distEps
	 * @param angleEps
	 * @return
	 * @deprecated Use replaceAllUTurnsByButt(Angle, false).
	 */
	public SVGPath replaceAllUTurnsByButt(Angle angleEps) {
		return replaceAllUTurnsByButt(angleEps, false);
	}

	/** 
	 * Not finished
	 * 
	 * @param svgPath
	 * @param distEps
	 * @param angleEps
	 * @return
	 */
	public SVGPath replaceAllUTurnsByButt(Angle angleEps, boolean extend) {
		SVGPath path = null;
		if (getOrCreateSignatureAttributeValue().contains(CC)) {
			PathPrimitiveList primList = getOrCreatePathPrimitiveList();
			List<Integer> quadrantStartList = primList.getUTurnList(angleEps);
			if (quadrantStartList.size() > 0) {
				for (int quad = quadrantStartList.size() - 1; quad >= 0; quad--) {
					primList.replaceUTurnsByButt(quadrantStartList.get(quad), extend/*, maxCapRadius*/);
				}
				path = new SVGPath(primList, this);
				SVGUtil.setSVGXAttribute(path, ROUNDED_CAPS, REMOVED);
			}
		}
		return path;
	}

	/** 
	 * Creates a line from path with signature "MLLLL", "MLLLLZ".
	 * 
	 * creates a line from thin rectangle.

	 * <p>
	 * Uses primitiveList.createLineFromMLLLL().
	 * 
	 * @param angleEps
	 * @param maxWidth above this assumes it's a rectangle
	 * @return null if line has wrong signature or is too wide or not antiParallel.
	 */
	public SVGLine createLineFromMLLLL(Angle angleEps, Double maxWidth) {
		SVGLine line = null;
		String sig = getOrCreateSignatureAttributeValue();
		if (MLLLL.equals(sig) || MLLLLZ.equals(sig)) {
			getOrCreatePathPrimitiveList();
			line = primitiveList.createLineFromMLLLL(angleEps, maxWidth);
		}
		return line;
	}
	
	/** 
	 * Creates a line from path with signature "MLLL"
	 * normally requires fill
	 * 
	 * creates a line from thin unclosed rectangle.

	 * <p>
	 * Uses primitiveList.createLineFromMLLL().
	 * 
	 * @param angleEps
	 * @param maxWidth above this assumes it's a rectangle
	 * @return null if line has wrong signature or is too wide or not antiParallel.
	 */
	public SVGLine createLineFromMLLL(Angle angleEps, Double maxWidth) {
		SVGLine line = null;
		String sig = getOrCreateSignatureAttributeValue();
		if (MLLL.equals(sig)) {
			getOrCreatePathPrimitiveList();
			line = primitiveList.createLineFromMLLL(angleEps, maxWidth);
		}
		return line;
	}
	
	/**
	 * a signature of MLMLML... indicates separate lines (either ladders or dashed lines)
	 * 
	 * @param empty or null, use any MLML... ; or specific MLML
	 * @return list of lines (one for each ML)
	 */
	public List<SVGLine> createSeparatedLinesFromRepeatedML(String refSig) {
		String sig = getOrCreateSignatureAttributeValue();
		List<SVGLine> lineList = new ArrayList<SVGLine>();
		if (isRepeatedML(sig)) {
			if (refSig == null || "".equals(refSig.trim()) ||
			refSig.equals(sig)) {
				getOrCreatePathPrimitiveList();
				for (int i = 0; i < primitiveList.size(); i += 2) {
					MovePrimitive movePrimitive = (MovePrimitive) primitiveList.get(i);
					LinePrimitive linePrimitive = (LinePrimitive) primitiveList.get(i + 1);
					SVGLine line = new SVGLine(movePrimitive.getFirstCoord(), linePrimitive.getFirstCoord());
					lineList.add(line);
				}
			}
		}
		return lineList;
	}

	public static boolean isRepeatedML(String sig) {
		return sig.startsWith(ML) && sig.replaceAll(ML, "").length() == 0;
	}

	public static List<SVGLine> createLinesFromPaths(List<SVGPath> pathList) {
		List<SVGLine> allLines = new ArrayList<SVGLine>();
		for (SVGPath path : pathList) {
			SVGLineList lineList = path.createLineListFromRepeatedML(null);
			if (lineList != null) {
				allLines.addAll(lineList.getLineList());
			}
		}
		return allLines;
	}

	private SVGLineList createLineListFromRepeatedML(String repeatedMLSignature) {
		SVGLineList lineList = null;
		List<SVGLine> lines = this.createSeparatedLinesFromRepeatedML(repeatedMLSignature);
		if (lines.size() > 0) {
			lineList = new SVGLineList(lines);
		}
		return lineList;
	}

	public static List<SVGCircle> createCirclesFromPaths(List<SVGPath> pathList) {
		List<SVGCircle> circleList = new ArrayList<SVGCircle>();
		for (SVGPath path : pathList) {
			SVGCircle circle = path.createCircle(CIRCLE_EPSILON);
			if (circle != null) {
				circleList.add(circle);
			} else {
				LOG.trace(path.getOrCreateSignatureAttributeValue());
			}
		}
		return circleList;
	}

	public static List<SVGLineList> createLineListListFromPaths(
			List<SVGPath> pathList, String repeatedMLSignature) {
		List<SVGLineList> lineListList = new ArrayList<SVGLineList>();
		for (SVGPath path : pathList) {
			SVGLineList lineList = path.createLineListFromRepeatedML(repeatedMLSignature);
			if (lineList != null) {
				lineListList.add(lineList);
			}
		}
		return lineListList;
	}

	/** translates "m" into "M" to create an absolute coordinate system.
	 * Also epxands implicit L commands into absolute ones
	 * @return
	 */
	public void makeRelativePathsAbsolute() {
		String d = getDString();
		if (d != null) {
			primitiveList = PathPrimitiveList.createPrimitiveList(d);
			String d1 = primitiveList.getDString();
			this.setDString(d1);
		}
	}

	@Override
	public String toString() {
		return (this.toXML());
	}

	@Override
	public boolean isZeroDimensional() {
		String signature = this.getOrCreateSignatureAttributeValue();
		if (signature == null) return false;
		signature = signature.toUpperCase();
		return (signature.equals(MZ) || signature.equals(M));
	}

	@Override
	protected boolean isGeometricallyEqualTo(SVGElement shape, double epsilon) {
		if (shape != null && shape instanceof SVGPath) {
			return this.hasEqualCoordinates((SVGPath) shape, epsilon);
		}
		return false;
	}


	public String getOrCreateSignatureAttributeValue() {
		String signature = super.getAttributeValue(SIGNATURE);
		if (signature == null) {
			signature = createSignatureFromDStringPrimitives();
			if (signature != null) {
				addAttribute(new Attribute(SIGNATURE, signature));
			}
		}
		return signature;
	}

	/** force create signature
	 * 
	 */
	public void forceCreateSignatureAttributeValue() {
		String signature = createSignatureFromDStringPrimitives();
		if (signature != null) {
			addAttribute(new Attribute(SIGNATURE, signature));
		}
	}

	/** some diagrams contain multiple copies of a primitive with different attributes. Remove all but one.
	 * Example
	 * 
	 * <path stroke="black" clip-path="url(#clipPath2)" fill="#cccaca" stroke-width="0.27300000190734863" d="M330.14 556.419 L330.114 556.134 L330.063 555.875 L329.985 555.643 L329.856 555.411 L329.727 555.18 L329.547 554.999 L329.34 554.817 L329.135 554.663 L328.902 554.559 L328.644 554.457 L328.386 554.405 L327.87 554.405 L327.612 554.457 L327.355 554.559 L327.122 554.663 L326.916 554.817 L326.71 554.999 L326.529 555.18 L326.4 555.411 L326.271 555.643 L326.193 555.875 L326.142 556.134 L326.116 556.419 L326.142 556.674 L326.193 556.934 L326.271 557.165 L326.529 557.631 L326.71 557.81 L326.916 557.992 L327.122 558.146 L327.355 558.249 L327.612 558.352 L327.87 558.404 L328.386 558.404 L328.644 558.352 L328.902 558.249 L329.135 558.146 L329.34 557.992 L329.547 557.81 L329.727 557.631 L329.985 557.165 L330.063 556.934 L330.114 556.674 L330.14 556.419 "/>
	 * <path fill="none" clip-path="url(#clipPath2)" stroke="#292425" stroke-width="0.27300000190734863" d="M330.14 556.419 L330.114 556.134 L330.063 555.875 L329.985 555.643 L329.856 555.411 L329.727 555.18 L329.547 554.999 L329.34 554.817 L329.135 554.663 L328.902 554.559 L328.644 554.457 L328.386 554.405 L327.87 554.405 L327.612 554.457 L327.355 554.559 L327.122 554.663 L326.916 554.817 L326.71 554.999 L326.529 555.18 L326.4 555.411 L326.271 555.643 L326.193 555.875 L326.142 556.134 L326.116 556.419 L326.142 556.674 L326.193 556.934 L326.271 557.165 L326.529 557.631 L326.71 557.81 L326.916 557.992 L327.122 558.146 L327.355 558.249 L327.612 558.352 L327.87 558.404 L328.386 558.404 L328.644 558.352 L328.902 558.249 L329.135 558.146 L329.34 557.992 L329.547 557.81 L329.727 557.631 L329.985 557.165 L330.063 556.934 L330.114 556.674 L330.14 556.419 M328.128 558.404 L328.128 564.415 L326.116 562.429 L328.128 564.415 L330.14 562.429 "/>
	 * 
	 * The paths are exactly the same but have different fill / stroke attributes. They are an artefact of the drawing 
	 * tool and convey exactly the same structural information. To avoid duplicates we delete all but the first version
	 * 
	 * Simply compare DStrings. In our experience the shadow has a lexically identical DString, so simply take the
	 * first. We might later hash this with stroke-width, etc.
	 * 
	 * @param pathList
	 * @return
	 */
	public static List<SVGPath> removeShadowedPaths(List<SVGPath> pathList) {
		List<SVGPath> newPathList = new ArrayList<SVGPath>();
		Set<String> dStringSet = new HashSet<String>();
		for (SVGPath path : pathList) {
			if (path == null) continue;
			String d = path.getDString().trim();
// sometimes this ends with Z - trim it off
			if (d.endsWith(Z)) {
				d = d.substring(0,  d.length() - 1).trim();
			}
			if (dStringSet.contains(d)) {
				continue;
			}
			dStringSet.add(d);
			newPathList.add(path);
		}
		return newPathList;
	}

	/** remove paths with no primitives
	 * some PDFs seems to contains paths with no primitives
	 * and d=""
	 * @param pathList unchanged
	 * @return new list without emptypaths
	 */
	public static List<SVGPath> createPathsWithNoEmptyD(List<SVGPath> pathList) {
		List<SVGPath> newPaths = new ArrayList<SVGPath>();
		for (SVGPath path : pathList) {
			if (!"".equals(path.getDString())) {
				newPaths.add(path);
			}
		}
		return newPaths;
	}

	/** remove paths with just a single "Move"
	 * some PDFs seems to contains paths with just a move
	 * @param pathList unchanged
	 * @return new list without single moves
	 */
	public static List<SVGPath> createPathsWithNoNullMove(List<SVGPath> pathList) {
		List<SVGPath> newPaths = new ArrayList<SVGPath>();
		for (SVGPath path : pathList) {
			PathPrimitiveList primitiveList = path.getOrCreatePathPrimitiveList();
			if (primitiveList.size() != 1 || primitiveList.get(0) instanceof MovePrimitive) {
				newPaths.add(path);
			}
		}
		return newPaths;
	}

	/** remove paths without original"Move"
	 * some PDFs seems to contains paths with CC etc.
	 * @param pathList unchanged
	 * @return new list without single moves
	 */
	public static List<SVGPath> createPathsWithNoMissingMove(List<SVGPath> pathList) {
		List<SVGPath> newPaths = new ArrayList<SVGPath>();
		for (SVGPath path : pathList) {
			PathPrimitiveList primitiveList = path.getOrCreatePathPrimitiveList();
			if (primitiveList.size() >0 && primitiveList.get(0) instanceof MovePrimitive) {
				newPaths.add(path);
			}
		}
		return newPaths;
	}

	/** paths outside y=0 are not part of the plot but confuse calculation of
	 * bounding box 
	 * @param new pathList
	 * @return
	 */
	public static List<SVGPath> removePathsWithNegativeY(List<SVGPath> pathList) {
		List<SVGPath> newPaths = new ArrayList<SVGPath>();
		for (SVGPath path : pathList) {
			Real2Range bbox = path.getBoundingBox();
			if (bbox.getYMax() >= 0.0) {
				newPaths.add(path);
			}
		}
		return newPaths;
	}

	public static void addSignatures(List<SVGPath> pathList) {
		for (SVGPath path : pathList) {
			String sig = path.getOrCreateSignatureAttributeValue();
		}
	}

	@Override
	public Real2 getXY() {
		Real2 xy = null;
		getOrCreatePathPrimitiveList();
		if (primitiveList.size() > 0) {
			SVGPathPrimitive primitive0 = primitiveList.get(0);
			xy = primitive0.getFirstCoord();
		}
		return xy;
	}


}
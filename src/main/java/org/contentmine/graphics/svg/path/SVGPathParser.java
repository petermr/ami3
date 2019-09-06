package org.contentmine.graphics.svg.path;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.graphics.svg.SVGPathPrimitive;

public class SVGPathParser {
	private static final String PRIMITIVE_ABBREVS= "mMcClLqQhHvVZz";
	private static final Logger LOG = Logger.getLogger(SVGPathParser.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** THESE STATICS ARE CODE SMELLS */
	private /*static*/ Real2 firstPoint;
	private /*static*/ Real2 currentPoint;
	
	private PathPrimitiveList primitiveList;
	private List<String> tokenList;
	private String d;
	private SVGPathPrimitive lastPrimitive;
	
	public PathPrimitiveList parseDString(String d) {
		long millis = System.currentTimeMillis();
		LOG.trace(">d>"+d);
		this.d = d;
		primitiveList = new PathPrimitiveList();
		if (d == null) {
			return primitiveList;
		}
		tokenList = extractTokenList(d);
		long mm = System.currentTimeMillis();
		long tt = (mm-millis)/1000;
		if (tt > 1) {
			throw new RuntimeException("long: "+tt);
		}
		int itok = 0;
		firstPoint = null;
		currentPoint = null;
		char t = (char)0;
		while (itok <tokenList.size()) {
			String token = tokenList.get(itok);
			t = token.charAt(0);
			LOG.trace(">t>"+t);
			itok++;
			
			if (false) {
			} else if (isMove(t)) {
				itok = addMovePrimitives(itok, t);
			} else if (isLine(t)) {
				itok = addLinePrimitives(itok, t);
			} else if (isHorizontal(t) || isVertical(t)) {
				itok = addHorizontalVerticalPrimitives(itok, t);
			} else if (isCubic(t)) {
				itok = addCubicPrimitives(itok, t);
			} else if (isQuadratic(t)) {
				itok = addQuadraticPrimitives(itok, t);
			} else if (isArc(t)) {
				itok = addArcPrimitives(itok, t);
			} else if (isClose(t)) {
				itok = addClosePrimitive(itok, t);
			} else {
				System.out.println();
				for (int i = Math.max(itok - 10, 0); i < itok; i++) {
					System.out.print(tokenList.get(i)+" ");
				}
				System.out.print(" ["+tokenList.get(itok)+"] ");
				for (int i = itok + 1; i < Math.min(tokenList.size(), itok + 10); i++) {
					System.out.print(tokenList.get(i)+" ");
				}
				throw new RuntimeException("unknown or unsupported primitive "+t+" in token "+itok+"("+token+") in "+d);
			}
		}
		mm = System.currentTimeMillis();
		tt = (mm-millis)/1000;
		if (tt > 1) {
			LOG.debug("longParse: "+tt+"; d "+d.length());
			if (d.length() == 225300) {
				LOG.debug("stop");
			}
		}

		return primitiveList;
	}

	/**
A (absolute)
a (relative)	elliptical arc	(rx ry x-axis-rotation large-arc-flag sweep-flag x y)+	
    Draws an elliptical arc from the current point to (x, y). 
    The size and orientation of the ellipse are defined by two radii (rx, ry) and an x-axis-rotation, 
    which indicates how the ellipse as a whole is rotated relative to the current coordinate system. 
    The center (cx, cy) of the ellipse is calculated automatically to satisfy the constraints imposed 
    by the other parameters. large-arc-flag and sweep-flag contribute to the automatic calculations
     and help determine how the arc is drawn.
     
     * @param itok
	 * @param t
	 * @return
	 */
	private int addArcPrimitives(int itok, char t) {
		checkExistingFirstXY(t);
		while (isCoordinate(tokenList, itok)) {
			double[] dd = readDoubles(tokenList, 7, itok);
			itok += 7;
			LOG.error("ARC not implemented in SVGPathParser; line drawn instead");
			Real2 r2 = new Real2(dd[5], dd[6]);
			if (isRelative(t)) {
				r2 = r2.plus(currentPoint);
			}
			SVGPathPrimitive pp = new LinePrimitive(r2);
			primitiveList.add(pp);
			lastPrimitive = pp;
			currentPoint = r2;
		}
		return itok;
	}

	/**
Q (absolute)
q (relative)	quadratic Bézier curveto	(x1 y1 x y)+	
    Draws a quadratic Bézier curve from the current point to (x,y) using (x1,y1) as the control point. 
    Q (uppercase) indicates that absolute coordinates will follow; 
    q (lowercase) indicates that relative coordinates will follow. 
    Multiple sets of coordinates may be specified to draw a polybézier. 
    At the end of the command, the new current point becomes the final (x,y) coordinate pair used in the polybézier.
T (absolute)
t (relative)	Shorthand/smooth quadratic Bézier curveto	(x y)+	
    Draws a quadratic Bézier curve from the current point to (x,y). 
    The control point is assumed to be the reflection of the control point on the previous command relative to the current point. 
    (If there is no previous command or if the previous command was not a Q, q, T or t, 
        assume the control point is coincident with the current point.) 
    T (uppercase) indicates that absolute coordinates will follow; 
    t (lowercase) indicates that relative coordinates will follow. 
    At the end of the command, the new current point becomes the final (x,y) coordinate pair used in the polybézier.
    
     * @param itok
	 * @param t
	 * @return
	 */
	private int addQuadraticPrimitives(int itok, char t) {
		checkExistingFirstXY(t);
		while (isCoordinate(tokenList, itok)) {
			int ntok = isSmoothQuadratic(t) ? 2 : 4;
			Real2Array r2a = readReal2Array(tokenList, ntok, itok);
			itok += ntok;
			if (isRelative(t)) {
				r2a = r2a.plusEquals(currentPoint);
			}
			if (isSmoothQuadratic(t)) {
				Real2 firstControlXY = currentPoint;
				if (isQuadratic(lastPrimitive)) {
					Real2 lastControlXY = lastPrimitive.getCoordArray().get(1); // check this
					Real2 vector = lastControlXY.subtract(currentPoint);
					firstControlXY = currentPoint.subtract(vector);
				}
				Real2Array temp = new Real2Array();
				temp.add(firstControlXY);
				temp.add(r2a);
				r2a = temp;
			}
			SVGPathPrimitive pp = new QuadPrimitive(r2a);
			primitiveList.add(pp);
			lastPrimitive = pp;
			currentPoint = r2a.get(1);
		}
		return itok;
	}

	/**
C (absolute)
c (relative)	curveto	(x1 y1 x2 y2 x y)+	
    Draws a cubic Bézier curve from the current point to (x,y) using (x1,y1) as the control point 
    at the beginning of the curve and (x2,y2) as the control point at the end of the curve. 
    C (uppercase) indicates that absolute coordinates will follow; 
    c (lowercase) indicates that relative coordinates will follow. 
    Multiple sets of coordinates may be specified to draw a polybézier. 
    At the end of the command, the new current point becomes the final (x,y) coordinate pair used in the polybézier.
S (absolute)
s (relative)	shorthand/smooth curveto	(x2 y2 x y)+	
    Draws a cubic Bézier curve from the current point to (x,y). 
    The first control point is assumed to be the reflection of the second control point 
    on the previous command relative to the current point. 
    (If there is no previous command or if the previous command was not an C, c, S or s, 
    assume the first control point is coincident with the current point.) 
    (x2,y2) is the second control point (i.e., the control point at the end of the curve). 
    S (uppercase) indicates that absolute coordinates will follow; 
    s (lowercase) indicates that relative coordinates will follow. 
    Multiple sets of coordinates may be specified to draw a polybézier. 
    At the end of the command, the new current point becomes the final (x,y) coordinate pair used in the polybézier.

	 * @param itok
	 * @param t
	 * @return
	 */
	private int addCubicPrimitives(int itok, char t) {
		checkExistingFirstXY(t);
		while (isCoordinate(tokenList, itok)) {
			int ntok = isSmoothCubic(t) ? 4 : 6;
			Real2Array r2a = readReal2Array(tokenList, ntok, itok);
			itok += ntok;
			if (isRelative(t)) {
				r2a = r2a.plusEquals(currentPoint);
			}
			if (isSmoothCubic(t)) {
				Real2 firstControlXY = currentPoint;
				if (isCubic(lastPrimitive)) {
					Real2 lastControlXY = lastPrimitive.getCoordArray().get(2); // check this
					Real2 vector = lastControlXY.subtract(currentPoint);
					firstControlXY = currentPoint.subtract(vector);
				}
				Real2Array temp = new Real2Array();
				temp.add(firstControlXY);
				temp.add(r2a);
				r2a = temp;
			}
			SVGPathPrimitive pp = new CubicPrimitive(r2a);
			primitiveList.add(pp);
			lastPrimitive = pp;
			currentPoint = r2a.get(2);
		}
		return itok;
	}

	public static boolean isCubic(SVGPathPrimitive primitive) {
		return primitive.getClass().equals(CubicPrimitive.class);
	}

	private boolean isQuadratic(SVGPathPrimitive primitive) {
		return primitive.getClass().equals(QuadPrimitive.class);
	}

	/**
Command	Name	Parameters	Description
Z or
z	closepath	(none)	
Close the current subpath by drawing a straight line from the current point to current subpath's initial point. 
Since the Z and z commands take no parameters, they have an identical effect.	 * @param t
	 */
	private int addClosePrimitive(int itok, char t) {
		checkExistingFirstXY(t);
		firstPoint = firstPoint.format(3);
		currentPoint = firstPoint;
		SVGPathPrimitive pp = new ClosePrimitive(currentPoint);
		primitiveList.add(pp);
		lastPrimitive = pp;
		return itok;
	}

	/**
The various "lineto" commands draw straight lines from the current point to a new point:
H (absolute)
h (relative)	horizontal lineto	x+	
    Draws a horizontal line from the current point (cpx, cpy) to (x, cpy). 
    H (uppercase) indicates that absolute coordinates will follow; 
    h (lowercase) indicates that relative coordinates will follow. 
    Multiple x values can be provided (although usually this doesn't make sense). 
    At the end of the command, the new current point becomes (x, cpy) for the final value of x.
V (absolute)
v (relative)	vertical lineto	y+	
    Draws a vertical line from the current point (cpx, cpy) to (cpx, y). 
    V (uppercase) indicates that absolute coordinates will follow; 
    v (lowercase) indicates that relative coordinates will follow. 
    Multiple y values can be provided (although usually this doesn't make sense). 
    At the end of the command, the new current point becomes (cpx, y) for the final value of y.	 
    
     * @param itok
	 * @param t
	 * @return
	 */
	private int addHorizontalVerticalPrimitives(int itok, char t) {
		checkExistingFirstXY(t);
		while (isCoordinate(tokenList, itok)) {
			double[] dd = readDoubles(tokenList, 1, itok);
			itok += 1;
			Real2 r2 = null;
			double lastX = currentPoint.getX();
			double lastY = currentPoint.getY();
			if (isRelative(t)) {
				if (isHorizontal(t)) {
					r2 = new Real2(lastX + dd[0], lastY);
				} else {
					r2 = new Real2(lastX, lastY + dd[0]);
				}
			} else {
				if (isHorizontal(t)) {
					r2 = new Real2(dd[0], currentPoint.getY());
				} else {
					r2 = new Real2(lastX, dd[0]);
				}
			} 
			SVGPathPrimitive pp = new LinePrimitive(r2);
			primitiveList.add(pp);
			lastPrimitive = pp;
			currentPoint = r2;
		}
		return itok;
	}

	/**
	 * The various "lineto" commands draw straight lines from the current point to a new point:
L (absolute)
l (relative)	lineto	(x y)+	Draw a line from the current point to the given (x,y) coordinate which becomes the new current point. 
    L (uppercase) indicates that absolute coordinates will follow; 
    l (lowercase) indicates that relative coordinates will follow. 
    A number of coordinates pairs may be specified to draw a polyline. 
    At the end of the command, the new current point is set to the final set of coordinates provided.
H (absolute)
h (relative)	horizontal lineto	x+	Draws a horizontal line from the current point (cpx, cpy) to (x, cpy). H (uppercase) indicates that absolute coordinates will follow; h (lowercase) indicates that relative coordinates will follow. Multiple x values can be provided (although usually this doesn't make sense). At the end of the command, the new current point becomes (x, cpy) for the final value of x.
V (absolute)
v (relative)	vertical lineto	y+	Draws a vertical line from the current point (cpx, cpy) to (cpx, y). V (uppercase) indicates that absolute coordinates will follow; v (lowercase) indicates that relative coordinates will follow. Multiple y values can be provided (although usually this doesn't make sense). At the end of the command, the new current point becomes (cpx, y) for the final value of y.
	 * @param itok
	 * @param t
	 * @return
	 */
	private int addLinePrimitives(int itok, char t) {
		while (isCoordinate(tokenList, itok)) {
			double[] dd = readDoubles(tokenList, 2, itok);
			Real2 r2 = new Real2(dd[0], dd[1]);
			itok += 2;
			if (isRelative(t)) {
				checkExistingFirstXY(t);
				r2 = r2.plus(currentPoint);
			}
			SVGPathPrimitive pp = new LinePrimitive(r2);
			primitiveList.add(pp);
			lastPrimitive = pp;
			currentPoint = r2;
		}
		LOG.trace(">l>"+primitiveList);
		return itok;
	}

	private int addMovePrimitives(int itok, char t) {
		/*
M (absolute)
m (relative)	
moveto	(x y)+	Start a new sub-path at the given (x,y) coordinate. M (uppercase) indicates that 
absolute coordinates will follow; m (lowercase) indicates that relative coordinates will follow. 
If a moveto is followed by multiple pairs of coordinates, the subsequent pairs are treated as 
implicit lineto commands. Hence, implicit lineto commands will be relative if the moveto is relative, 
and absolute if the moveto is absolute. If a relative moveto (m) appears as the first element of the path, 
then it is treated as a pair of absolute coordinates. In this case, subsequent pairs of coordinates are 
treated as relative even though the initial moveto is interpreted as an absolute moveto.
 */
		int count = 0;
		while (isCoordinate(tokenList, itok)) {
			double[] dd = readDoubles(tokenList, 2, itok);
			itok += 2;
			Real2 r2 = new Real2(dd[0], dd[1]);
			if (currentPoint != null && isRelative(t)) {
				r2 = r2.plus(currentPoint);
			}
			if (count == 0) {
				firstPoint= r2;
			}
			SVGPathPrimitive pp = (count == 0) ? new MovePrimitive(r2) : new LinePrimitive(r2);
			primitiveList.add(pp);
			lastPrimitive = pp;
			currentPoint = r2;
			count++;
		}
		return itok;
	}

	private static boolean isArc(char t) {
		return SVGPathPrimitive.ARC == Character.toUpperCase(t);
	}

	private static boolean isHorizontal(char t) {
		return SVGPathPrimitive.HORIZ == Character.toUpperCase(t);
	}

	private static boolean isVertical(char t) {
		return SVGPathPrimitive.VERT == Character.toUpperCase(t);
	}

	private static boolean isRelative(char t) {
		return Character.isLowerCase(t);
	}

	private static boolean isClose(char t) {
		return SVGPathPrimitive.CLOSE == Character.toUpperCase(t);
	}

	private static boolean isMove(char t) {
		return SVGPathPrimitive.MOVE == Character.toUpperCase(t);
	}

	private static boolean isLine(char t) {
		return SVGPathPrimitive.LINE == Character.toUpperCase(t);
	}

	private static boolean isQuadratic(char t) {
		return isSimpleQuadratic(t) || isSmoothQuadratic(t);
	}

	private static boolean isSimpleQuadratic(char t) {
		return SVGPathPrimitive.QUAD == Character.toUpperCase(t);
	}

	private static boolean isSmoothQuadratic(char t) {
		return SVGPathPrimitive.QUAD_SMOOTH == Character.toUpperCase(t);
	}

	private static boolean isCubic(char t) {
		return isSimpleCubic(t) || isSmoothCubic(t);
	}

	private static boolean isSimpleCubic(char t) {
		return SVGPathPrimitive.CUBIC == Character.toUpperCase(t);
	}

	private static boolean isSmoothCubic(char t) {
		return SVGPathPrimitive.CUBIC_SMOOTH == Character.toUpperCase(t);
	}

	private void checkExistingFirstXY(char t) {
		if (firstPoint == null && !Character.isUpperCase(t)) {
			LOG.error("M/m or absolute must be first in path; found "+t+" in "+d);
		}
	}

	private static boolean isCoordinate(List<String> tokenList, int itok) {
		return itok < tokenList.size() && !Character.isAlphabetic(tokenList.get(itok).charAt(0));
	}

	private static Real2Array readReal2Array(List<String> tokenList, int ntoread, int itok) {
		double[] dd = readDoubles(tokenList, ntoread, itok);
		return Real2Array.createFromPairs(new RealArray(dd));
	}

	private static double[] readDoubles(List<String> tokenList, int ntoread, int itok) {
		if (itok + ntoread > tokenList.size()) {
			throw new RuntimeException("Ran out of tokens at "+itok+" wanted "+ntoread);
		}
		double[] dd = new double[ntoread];
		for (int i = 0; i < ntoread; i++) {
			Double d = null;
			String token = null;
			try {
				token = tokenList.get(itok + i);
				dd[i] = Double.valueOf(token);
			} catch (Exception e) {
				throw new RuntimeException("Cannot parse as double ("+token+") at : "+itok+i);
			}
		}
		return dd;
	}

	private static List<String> extractTokenList(String d) {
		List<String> tokenList = new ArrayList<String>();
		int numberStart = -1;
		for (int i = 0; i < d.length(); i++) {
			char c = d.charAt(i);
			if (Character.isWhitespace(c) || c == ',') {
				String token = getCurrentNumber(d, tokenList, numberStart, i);
				addToken(tokenList, token);
				numberStart = -1;
			} else if (Character.isDigit(c) || c == '+' || c == '-' || c == '.') {
				if (numberStart == -1) {
					numberStart = i;
				}
			} else if ("EeDd".indexOf(c) != -1) {  // floats
				LOG.trace("processed E-notation");
			} else if (PRIMITIVE_ABBREVS.indexOf(c) != -1) {
				String token = getCurrentNumber(d, tokenList, numberStart, i);
				addToken(tokenList, token);
				token = String.valueOf(c);
				addToken(tokenList, token);
				numberStart = -1;
			} else {
				throw new RuntimeException("Unknown character in dString: "+c+" path: "+d);
			}
		}
		String token = getCurrentNumber(d, tokenList, numberStart, d.length());
		addToken(tokenList, token);
		LOG.trace(tokenList);
		return tokenList;
	}

	private static void addToken(List<String> tokenList, String token) {
		if (token != null) {
			tokenList.add(token);
		}
	}

	private static String getCurrentNumber(
			String d, List<String> tokenList, int numberStart, int i) {
		String token = null;
		if (numberStart != -1) {
			token = d.substring(numberStart, i);
		}
		return  token;
	}

}

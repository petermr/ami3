package org.contentmine.graphics.svg.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPoly;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGPolyline;

public class SVGTriangle extends AbstractSubtypedPolygon {
	
	private static final Logger LOG = Logger.getLogger(SVGTriangle.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	
	public static final String TRIANGLE = "triangle";
	public static final String CONVEX_ARROWHEAD = "MCLCCC";
	
	public SVGTriangle() {
		super();
		this.setSVGClassName(TRIANGLE);
	}

	public SVGTriangle(SVGPolygon polygon) {
		super(polygon);
	}

	public SVGTriangle(SVGPolyline polyline) {
		super(polyline);
	}
	
	public SVGTriangle(Real2Array real2Array) {
		super(real2Array);
		this.setSVGClassName(TRIANGLE);
	}

	/** "aesthetic triangle as arrowhead.
	 * not sure how to generalize this.
         path has d="M486.364 534.218 
                     C486.612 533.39 486.843 532.852 487.108 532.19  // second left
                     L484.587 532.19 // base
                     C484.7 532.427 485.083 533.39 485.333 534.218 // second right
                     C485.6 535.102 485.782 535.903 485.848 536.425  // first right
                     
                     C485.913 535.903 486.096 535.102 486.364 534.218 " // first left
                     hmm... maybe crunch it to straight lines?
                     NOT YET IMPLEMENTED
                     	 * @param path
	 */
	public static SVGTriangle getPseudoTriangle(SVGPath path) {
		SVGTriangle triangle = null;
		String signature = path.getOrCreateSignatureAttributeValue();
		if (signature.equals("MCLCCC")) {
			SVGPoly svgPoly = SVGPoly.createSVGPoly(path);
			if (svgPoly instanceof SVGPolygon) {
				SVGPolygon polygon = (SVGPolygon) svgPoly;
				triangle = createTriangleMCLCCC(polygon.getReal2Array());
				LOG.trace("TRIANGLE "+triangle.toXML());
			}
		}
		return triangle;
	}

	private static SVGTriangle createTriangleMCLCCC(Real2Array real2Array) {
		SVGTriangle triangle = null;
		Real2Array triangleR2A = new Real2Array(3);
		triangleR2A.setElement(0, real2Array.get(1));
		triangleR2A.setElement(1, real2Array.get(2));
		triangleR2A.setElement(2, real2Array.get(4));
		triangle = new SVGTriangle(triangleR2A);
		return triangle;
	}

	public SVGElement getLine(int serial) {
		return polyline.getLineList().get(serial % 3);
	}

	public int getLineTouchingPoint(Real2 point, double delta) {
		for (int iline = 0; iline < 3; iline++) {
			SVGLine line = polyline.getLineList().get(iline);
			if (line.getEuclidLine().getUnsignedDistanceFromPoint(point) < delta) {
				return iline;
			}
		}
		return -1;
	}

	public SVGLine getLineStartingFrom(int point) {
		return polyline.getLineList().get(point % 3);
	}

	public boolean hasEqualCoordinates(SVGTriangle triangle0, double delta) {
		return this.getOrCreateClosedPolyline().hasEqualCoordinates(triangle0.getOrCreateClosedPolyline(), delta);
	}

	public SVGPolyline getOrCreateClosedPolyline() {
		if (polyline == null) {
			Real2Array real2Array = this.getReal2Array();
			real2Array.add(new Real2(real2Array.get(0)));
			polyline = new SVGPolyline(this.getReal2Array());
		}
		return polyline;
	}
	
	public String toString() {
		Real2Array real2Array = (polyline == null) ? null : polyline.getReal2Array();
		return real2Array == null ? null : real2Array.toString();
	}

	@Override
	public String getGeometricHash() {
		return String.valueOf(polyline.getReal2Array());
	}
	
	public static List<SVGTriangle> extractTriangles(List<? extends SVGElement> elements) {
		List<SVGTriangle> triangleList = new ArrayList<SVGTriangle>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGTriangle) {
				triangleList.add((SVGTriangle) element);
			}
		}
		return triangleList;
	}



	/** convenience method to extract list of svgTriangles in element
	 * 
	 * @param svgElement
	 * @return
	 */
	public static List<SVGTriangle> extractSelfAndDescendantTriangles(AbstractCMElement g) {
		List<SVGPolygon> polygonList = SVGPolygon.extractSelfAndDescendantPolygons(g);
		return SVGTriangle.extractTriangles(polygonList);
	}

	@Override
	protected int getSubtypeSize() {
		return 3;
	}



}

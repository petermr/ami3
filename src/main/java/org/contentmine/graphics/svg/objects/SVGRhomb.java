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
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGPolyline;

/** a diamond shape with points along the axes
 * 
 * @author pm286
 *
 */
public class SVGRhomb extends AbstractSubtypedPolygon {
	
	private static final Logger LOG = Logger.getLogger(SVGRhomb.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	
	public static final String RHOMB = "rhomb";
	
	public SVGRhomb() {
		super();
		this.setSVGClassName(RHOMB);
	}

	public SVGRhomb(SVGPolygon polygon) {
		super(polygon);
	}

	public SVGRhomb(SVGPolyline polyline) {
		super(polyline);
	}

	@Override
	protected int getSubtypeSize() {
		return 4;
	}
	
	
	public SVGRhomb(Real2Array real2Array) {
		super(real2Array);
		this.setSVGClassName(RHOMB);
	}

	public SVGElement getLine(int serial) {
		return polyline.getLineList().get(serial % size());
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

	public boolean hasEqualCoordinates(SVGRhomb triangle0, double delta) {
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
		return "rhomb: "+this.toXML();
	}

	@Override
	public String getGeometricHash() {
		return String.valueOf(polyline.getReal2Array());
	}
	
	public static List<SVGRhomb> extractRhombs(List<? extends SVGElement> elements) {
		List<SVGRhomb> triangleList = new ArrayList<SVGRhomb>();
		for (AbstractCMElement element : elements) {
			if (SVGRhomb.isRhomb(element)) {
				triangleList.add((SVGRhomb) element);
			}
		}
		return triangleList;
	}



	private static boolean isRhomb(AbstractCMElement element) {
		if (element instanceof SVGRhomb) {
			return true;
		} else if (element instanceof SVGRhomb) {
			return true;
		}
		return false;
	}

	/** convenience method to extract list of svgTriangles in element
	 * 
	 * @param svgElement
	 * @return
	 */
	public static List<SVGRhomb> extractSelfAndDescendantRhombs(AbstractCMElement g) {
		List<SVGPolygon> polygonList = SVGPolygon.extractSelfAndDescendantPolygons(g);
		return SVGRhomb.extractRhombs(polygonList);
	}




}

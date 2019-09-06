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
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.path.PathPrimitiveList;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

/** creates Ellipse from MCCCC and other paths
 * 
 * @author pm286
 *
 */
public class SVGEllipse extends SVGShape {
	private static final Logger LOG = Logger.getLogger(SVGEllipse.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String ELLIPSE_MCCCC = "MCCCC";
	public static final String ELLIPSE_MCCCCZ = "MCCCCZ";
	public final static String ALL_ELLIPSE_XPATH = ".//svg:ellipse";


	@SuppressWarnings("unused")

	private static final String RX = "rx";
	private static final String RY = "ry";
	private static final String R = "r";

	public final static String TAG ="ellipse";
	

	/** constructor
	 */
	public SVGEllipse() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGEllipse(SVGElement element) {
        super(element);
	}
	
	/** constructor
	 */
	public SVGEllipse(Element element) {
        super((SVGElement) element);
	}
	
	protected void init() {
		super.setDefaultStyle();
//		setDefaultStyle(this);
	}
	public static void setDefaultStyle(SVGElement ellipse) {
		ellipse.setStroke("black");
		ellipse.setStrokeWidth(0.5);
		ellipse.setFill("#aaffff");
	}
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGEllipse(this);
    }

	/** constructor.
	 * 
	 * @param x1
	 * @param rad
	 */
	public SVGEllipse(double cx, double cy, double rx, double ry) {
		this();
		this.setCXY(new Real2(cx, cy));
		this.setRXY(new Real2(rx, ry));
	}
	
	public SVGEllipse(Real2 cxy, double rx, double ry) {
		this();
		this.setCXY(cxy);
		this.setRXY(new Real2(rx, ry));
	}

	public SVGEllipse(Real2 cxy, Real2 dxdy) {
		this(cxy, dxdy.getX(), dxdy.getY());
	}

	protected void drawElement(Graphics2D g2d) {
		saveGraphicsSettingsAndApplyTransform(g2d);
		Real2 xy0 = getCXY();
		Real2 rxy = getRXY();
		xy0 = transform(xy0, cumulativeTransform);
		double rrx = transform(rxy.getX(), cumulativeTransform) * 0.5;
		double rry = transform(rxy.getY(), cumulativeTransform) * 0.5;
		
		Ellipse2D ellipse = new Ellipse2D.Double(xy0.x - rrx, xy0.y - rry, rrx + rrx, rry + rry);
		fill(g2d, ellipse);
		draw(g2d, ellipse);
		restoreGraphicsSettingsAndTransform(g2d);
	}
	
	public Real2 getRXY() {
		return new Real2(this.getRX(), this.getRY());
	}
	
	public double getRX() {
		return this.getCoordinateValueDefaultZero(RX);
	}
	
	public double getRY() {
		return this.getCoordinateValueDefaultZero(RY);
	}
	
	public void applyTransformPreserveUprightText(Transform2 transform) {
		Real2 xy = this.getCXY();
		setCXY(xy.getTransformed(transform));
		Real2 rxy = this.getRXY();
		setRXY(rxy.getTransformed(transform));
	}

	public void format(int places) {
		setCXY(getCXY().format(places));
		setRXY(getRXY().format(places));
	}
	
	public void setRXY(Real2 rxy) {
		this.setRX(rxy.getX());
		this.setRY(rxy.getY());
	}

	public void setRX(double x) {
		this.addAttribute(new Attribute(RX, String.valueOf(x)));
	}

	public void setRY(double y) {
		this.addAttribute(new Attribute(RY, String.valueOf(y)));
	}

	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	/**
	 * @param rad the rad to set
	 */
	public void setRad(double rad) {
		this.addAttribute(new Attribute(R, String.valueOf(rad)));
	}

	/** extent of ellipse
	 * 
	 * @return
	 */
	public Real2Range getBoundingBox() {
		if (boundingBoxNeedsUpdating()) {
			boundingBox = new Real2Range();
			Real2 center = getCXY();
			Real2 rad = getRXY();
			boundingBox.add(center.subtract(rad));
			boundingBox.add(center.plus(rad));
		}
		return boundingBox;
	}

	@Override
	public String getGeometricHash() {
		return getAttributeValue(CX)+" "+getAttributeValue(CY)+" "+getAttributeValue(RX)+" "+getAttributeValue(RY);
	}

	public static SVGShape getEllipseOrCircle(SVGPath path, double eps) {
		SVGShape ellipseOrCircle= null;
		String signature = path.getOrCreateSignatureAttributeValue();
		if (signature.equals(ELLIPSE_MCCCC) || signature.equals(ELLIPSE_MCCCCZ)) {
/**
 d="M350.644 164.631 
			C350.644 170.705 327.979 175.631 300.02 175.631 
			C272.06 175.631 249.395 170.705 249.395 164.631 
			C249.395 158.555 272.06 153.631 300.02 153.631 
			C327.979 153.631 350.644 158.555 350.644 164.631 "/>			
 * 			
 */
		}
		PathPrimitiveList primList = path.getOrCreatePathPrimitiveList();
		Real2[] points = new Real2[4];
		for (int i = 1; i < 5; i++) {
			SVGPathPrimitive primitive = primList.get(i);
			points[i-1] = primitive.getLastCoord();
		}
		if (Real.isEqual(points[0].getX(), points[2].getX(), eps)) {
			Real2 centrex = points[0].getMidPoint(points[2]);
			Real2 centrey = points[1].getMidPoint(points[3]);
			Double rx = null;
			Double ry = null;
			if (centrex.isEqualTo(centrey, eps)) {
				if (Real.isEqual(points[0].getX(), points[2].getX(), eps) && Real.isEqual(points[1].getY(), points[3].getY(), eps)) {
					ry = Math.abs(points[0].getY() - points[2].getY()) / 2.;
					rx = Math.abs(points[1].getX() - points[3].getX()) / 2.;
				} else if (Real.isEqual(points[1].getX(), points[3].getX(), eps) && Real.isEqual(points[0].getY(), points[2].getY(), eps)) {
					rx = Math.abs(points[0].getX() - points[2].getX()) / 2.;
					ry = Math.abs(points[1].getY() - points[3].getY()) / 2.;
				} else {
					LOG.trace("Cannot form ellipse");
				}
				if (rx != null) {
					if (Real.isEqual(rx,  ry, eps)) {
						ellipseOrCircle = new SVGCircle(centrex, (double) rx);
					} else {
						ellipseOrCircle = new SVGEllipse(centrex.getX(), centrey.getY(), rx, ry);
					}
				}
			}
		}
		return ellipseOrCircle;
	}

	@Override
	protected boolean isGeometricallyEqualTo(SVGElement shape, double epsilon) {
		if (shape != null && shape instanceof SVGEllipse) {
			SVGEllipse ellipse = (SVGEllipse) shape;
			return (this.getXY().isEqualTo(ellipse.getXY(), epsilon) &&
				Real.isEqual(this.getRX(), ellipse.getRX(), epsilon) &&
				Real.isEqual(this.getRY(), ellipse.getRY(), epsilon));
		}
		return false;
	}

	@Override
	public Double getX() {
		return this.getCX();
	}
	
	@Override
	public Double getY() {
		return this.getCY();
	}
	
	@Override
	public Real2 getXY() {
		return this.getCXY();
	}

	/** convenience method to extract list of svgEllipses in element
	 * 
	 * @param svgElement
	 * @return
	 */
	public static List<SVGEllipse> extractSelfAndDescendantEllipses(AbstractCMElement svgElement) {
		return SVGEllipse.extractEllipses(SVGUtil.getQuerySVGElements(svgElement, ALL_ELLIPSE_XPATH));
	}

	/** makes a new list composed of the ellipses in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGEllipse> extractEllipses(List<SVGElement> elements) {
		List<SVGEllipse> ellipseList = new ArrayList<SVGEllipse>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGEllipse) {
				ellipseList.add((SVGEllipse) element);
			}
		}
		return ellipseList;
	}
	
	
}

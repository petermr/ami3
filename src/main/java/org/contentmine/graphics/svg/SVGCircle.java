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

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.AbstractCMElement;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGCircle extends SVGShape {

	private final static Logger LOG = Logger.getLogger(SVGCircle.class);

	public final static String ALL_CIRCLE_XPATH = ".//svg:circle";
	
	private static final String R = "r";
	private static final String CX = "cx";
	private static final String CY = "cy";
	public final static String TAG ="circle";
	
	private Ellipse2D.Double circle2;

	public Ellipse2D.Double getCircle2() {
		return circle2;
	}

	public void setCircle2(Ellipse2D.Double circle2) {
		this.circle2 = circle2;
	}

	/** constructor
	 */
	public SVGCircle() {
		super(TAG);
		init();
	}
	
	protected void init() {
		super.setDefaultStyle();
//		setDefaultStyle(this);
	}
	
	/** constructor
	 */
	public SVGCircle(SVGElement element) {
        super(element);
	}
	
	/** constructor
	 */
	public SVGCircle(Element element) {
        super((SVGElement) element);
	}
	
	/**
	 * 
	 * @param circle
	 */
	public static void setDefaultStyle(SVGElement circle) {
		circle.setStroke("black");
		circle.setStrokeWidth(0.5);
		circle.setFill("#aaffff");
	}
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGCircle(this);
    }

	
	/** constructor.
	 * 
	 * @param x1
	 * @param rad
	 */
	public SVGCircle(Real2 x1, double rad) {
		this();
		setXY(x1);
		setRad(rad);
		circle2 = new Ellipse2D.Double(x1.getX(), x1.getY(), rad, rad);
	}
	
	protected void drawElement(Graphics2D g2d) {
		saveGraphicsSettingsAndApplyTransform(g2d);
		double x = this.getDouble(CX);
		double y = this.getDouble(CY);
		double r = this.getDouble(R);
		Real2 xy0 = new Real2(x, y);
		xy0 = transform(xy0, cumulativeTransform);
		double rad = transform(r, cumulativeTransform);
		Ellipse2D ellipse = new Ellipse2D.Double(xy0.x-rad, xy0.y-rad, rad+rad, rad+rad);
		fill(g2d, ellipse);
		draw(g2d, ellipse);
		restoreGraphicsSettingsAndTransform(g2d);
	}
	
	/**
	 * @param x1 the x1 to set
	 */
	public void setXY(Real2 x1) {
		this.addAttribute(new Attribute(CX, String.valueOf(x1.getX())));
		this.addAttribute(new Attribute(CY, String.valueOf(x1.getY())));
	}

	/**
	 * @param x1 the x1 to set
	 */
	public Real2 getXY() {
		return new Real2(
				getCX(),
				getCY()
			);
	}
	
	public void applyTransformPreserveUprightText(Transform2 transform) {
		Real2 xy = this.getXY();
		setXY(xy.getTransformed(transform));
		Real2 rxy = new Real2(this.getRad(), 0);
		setRad(rxy.getX());
	}

	public void format(int places) {
		setXY(getXY().format(places));
		setRad(Util.format(getRad(), places));
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
	
	/** get radius
	 * 
	 * @return Double.NaN if not set
	 */
	public double getRad() {
		String r = this.getAttributeValue(R);
		Double d = Double.valueOf(r);
		return (d == null) ? Double.NaN : d.doubleValue();
	}

	public Ellipse2D.Double createAndSetCircle2D() {
		ensureCumulativeTransform();
		double rad = this.getDouble(R);
		double x1 = this.getDouble(CX);
		double y1 = this.getDouble(CX);
		Real2 xy1 = new Real2(x1, y1);
		xy1 = transform(xy1, cumulativeTransform);
		float width = 5.0f;
		String style = this.getAttributeValue("style");
		if (style.startsWith("stroke-width:")) {
			style = style.substring("stroke-width:".length());
			style = style.substring(0, (style+S_SEMICOLON).indexOf(S_SEMICOLON));
			width = (float) Double.parseDouble(style);
			width *= 15.f;
		}
		circle2 = new Ellipse2D.Double(xy1.x - rad, xy1.y - rad, rad+rad, rad+rad);
		return circle2;
	}
	
	/** extent of circle
	 * 
	 * @return
	 */
	public Real2Range getBoundingBox() {
		if (boundingBoxNeedsUpdating()) {
			boundingBox = new Real2Range();
			Real2 center = getCXY();
			double rad = getRad();
			boundingBox.add(new Real2(center.getX() - rad, center.getY() - rad));
			boundingBox.add(new Real2(center.getX() + rad, center.getY() + rad));
		}
		return boundingBox;
	}
	
	/** property of graphic bounding box
	 * can be overridden
	 * @return default none
	 */
	protected String getBBFill() {
		return "none";
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default green
	 */
	protected String getBBStroke() {
		return "green";
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default 0.5
	 */
	protected double getBBStrokeWidth() {
		return 0.5;
	}
	
	public boolean includes(Real2 point) {
		Real2 center = this.getCXY();
		return point != null && point.getDistance(center) < getRad();
	}

	/** tests whether element is geometricallyContained within this
	 * @param element
	 * @return
	 * @Override
	 */
	public boolean includes(SVGElement element) {
		Real2Range thisBbox = this.getBoundingBox();
		Real2Range elementBox = (element == null) ? null : element.getBoundingBox();
		if (thisBbox == null) {
			return false;
		}
		Real2[] corners = elementBox.getLLURCorners();
		if (!this.includes(corners[0]) || !this.includes(corners[1])) {
			return false;
		}
		// generate and test other corners
		if (!this.includes(new Real2(corners[0].x, corners[1].y))) {
			return false;
		}
		if (!this.includes(new Real2(corners[1].x, corners[0].y))) {
			return false;
		}
		return true;
	}
	
	/** makes a new list composed of the circles in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGCircle> extractCircles(List<SVGElement> elements) {
		List<SVGCircle> circleList = new ArrayList<SVGCircle>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGCircle) {
				circleList.add((SVGCircle) element);
			}
		}
		return circleList;
	}

	@Override
	public String getGeometricHash() {
		return getAttributeValue(CX)+" "+getAttributeValue(CY)+" "+getAttributeValue(R);
	}

	public static List<SVGCircle> extractSelfAndDescendantCircles(AbstractCMElement element) {
		return SVGCircle.extractCircles(SVGUtil.getQuerySVGElements(element, ALL_CIRCLE_XPATH));
	}

	@Override
	protected boolean isGeometricallyEqualTo(SVGElement shape, double epsilon) {
		if (shape != null && shape instanceof SVGCircle) {
			SVGCircle circle = (SVGCircle) shape;
			return (this.getXY().isEqualTo(circle.getXY(), epsilon) &&
					Real.isEqual(getRad(), circle.getRad(), epsilon));
		}
		return false;
	}

	@Override
	public Double getX() {
		return getCX();
	}
	
	@Override
	public Double getY() {
		return getCY();
	}
	
	/** draws a circular arc as a series of beziers.
	 * 
	 * @param centre
	 * @param radius
	 * @param start
	 * @param end
	 * @param nsteps
	 * @return
	 */
	public static AbstractCMElement createCircleArc(Real2 centre, double radius, Angle start, Angle end/*, int nsteps*/) {
		double PI4 = Math.PI / 4.0;
		AbstractCMElement arc = new SVGG();
		return arc;
		
	}

	/** calculate distances of an array of points from centre.
	 *  
	 * @return distances
	 */
	public RealArray calculateDistancesFromCentre(Real2Array points) {
		RealArray deviations = new RealArray();
		Real2 centre = getXY();
		for (Real2 point : points) {
			deviations.addElement(centre.getDistance(point));
		}
		return deviations;
		
	}
	
	/** calculate signed distances of an array of points from nearest point on circumference.
	 *  
	 * @return distances
	 */
	public RealArray calculateSignedDistancesFromCircumference(Real2Array points) {
		double rad = getRad();
		RealArray deviations = calculateDistancesFromCentre(points);
		deviations = deviations.addScalar(-1.0 * rad);
		return deviations;
	}
	
	/** calculate unsigned distances of an array of points from nearest point on circumference.
	 *  
	 * @return distances
	 */
	public RealArray calculateUnSignedDistancesFromCircumference(Real2Array points) {
		RealArray deviations = calculateSignedDistancesFromCircumference(points);
		return deviations.getAbsoluteValues();
	}
}

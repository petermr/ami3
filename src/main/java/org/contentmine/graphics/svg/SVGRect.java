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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.AbstractCMElement;

import nu.xom.Element;
import nu.xom.Node;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGRect extends SVGShape {

	private static final Logger LOG = Logger.getLogger(SVGRect.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String ALL_RECT_XPATH = ".//svg:rect";

	public static final String HEIGHT = "height";
	public static final String WIDTH = "width";
	public static final String Y = "y";
	public static final String X = "x";

	private static double MIN_WIDTH = 0.01;
	private static double MIN_HEIGHT = 0.01;
	
	final public static String TAG ="rect";

	/** constructor
	 */
	public SVGRect() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGRect(SVGElement element) {
        super(element);
	}
	
	/** constructor
	 */
	public SVGRect(Element element) {
        super((SVGElement) element);
	}
	
	protected void init() {
		super.setDefaultStyle();
//		setDefaultStyle(this);
	}
	public static void setDefaultStyle(SVGElement rect) {
		rect.setStroke("black");
		rect.setStrokeWidth(1.0);
		rect.setFill("none");
	}
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGRect(this);
    }

	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGRect(double x, double y, double w, double h) {
		this();
		setX(x);
		setY(y);
		setWidth(w);
		setHeight(h);
	}

	/** create from bounding box
	 * mainly for display.
	 * if one or both dimensions of range are zero (e.g. axial line or dot) increases them
	 * by MIN_WIDTH or MIN_HEIGHT
	 * 
	 * @param r2r
	 * @return null if r2r is null
	 */
	public static SVGRect createFromReal2Range(Real2Range r2r) {
		SVGRect rect = null;
		rect = createRect(r2r, rect);
		return rect;
	}


	/** create from bounding box
	 * mainly for display.
	 * if one or both dimensions of range are zero (e.g. axial line or dot) increases them
	 * by MIN_WIDTH or MIN_HEIGHT
	 * 
	 * @param r2r
	 * @return null if r2r is null
	 */
	public static SVGRect createFromReal2Range(Real2Range r2r, double padding) {
		Real2Range r2ra = new Real2Range(r2r);
		r2ra.extendBothEndsBy(org.contentmine.eucl.euclid.RealRange.Direction.HORIZONTAL, padding, padding);
		r2ra.extendBothEndsBy(org.contentmine.eucl.euclid.RealRange.Direction.VERTICAL, padding, padding);
		SVGRect rect = null;
		rect = createRect(r2ra, rect);
		return rect;
	}

	private static SVGRect createRect(Real2Range r2r, SVGRect rect) {
		if (r2r != null) {
			// if rect is a line or dot, extend by small amount
			// vertical line
			if (r2r.getXRange().getRange() < MIN_WIDTH) {
				r2r.extendBothEndsBy(org.contentmine.eucl.euclid.RealRange.Direction.HORIZONTAL, 0.5 * MIN_WIDTH, 0.5 * MIN_WIDTH);
			}
			if (r2r.getYRange().getRange() < MIN_HEIGHT) {
				r2r.extendBothEndsBy(org.contentmine.eucl.euclid.RealRange.Direction.VERTICAL, 0.5 * MIN_HEIGHT, 0.5 * MIN_HEIGHT);
			}
			Real2[] corners = r2r.getLLURCorners();
			
			if (corners != null && corners.length == 2) {
				rect = new SVGRect(corners[0], corners[1]);
			}
		}
		return rect;
	}

	/** create from bounding boxes
	 * mainly for display.
	 * 
	 * @param r2r
	 * @return null if r2r is null
	 */
	public static List<SVGRect> createFromReal2Ranges(List<Real2Range> r2rList) {
		List<SVGRect>  rectList = new ArrayList<SVGRect>();
		for (Real2Range r2r : r2rList) {
			rectList.add(SVGRect.createFromReal2Range(r2r));
		}
		return rectList;
	}
	
	/** create from bounding boxes
	 * mainly for display.
	 * 
	 * @param r2r
	 * @return null if r2r is null
	 */
	public static List<SVGRect> createFromReal2Ranges(List<Real2Range> r2rList, double padding) {
		List<SVGRect>  rectList = new ArrayList<SVGRect>();
		for (Real2Range r2r : r2rList) {
			SVGRect rect = SVGRect.createFromReal2Range(r2r, padding);
			rectList.add(rect);
		}
		return rectList;
	}
	
	/** create from edges
	 * 
	 * @param xRange
	 * @param yRange
	 * @return null if r2r is null
	 */
	public static SVGRect createFromRealRanges(RealRange xRange, RealRange yRange) {
		SVGRect rect = null;
		if (xRange != null && yRange != null) {
			rect = new SVGRect(new Real2(xRange.getMin(), yRange.getMin()), new Real2(xRange.getMax(), yRange.getMax()));
		}
		return rect;
	}
	
	/** constructor.
	 * 
	 * @param x1 "lower left"
	 * @param x2 "upper right"
	 */
	public SVGRect(Real2 x1, Real2 x2) {
		this(x1.getX(), x1.getY(), x2.getX() - x1.getX(), x2.getY() - x1.getY());
	}
//  <g style="stroke-width:0.2;">
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="black" style="stroke-width:0.36;"/>
//  <line x1="-1.9021130325903073" y1="0.6180339887498945" x2="-1.175570504584946" y2="-1.618033988749895" stroke="white" style="stroke-width:0.12;"/>
//</g>
	
	@Deprecated //"use createFromReal2Range which deals with nulls"
	public SVGRect(Real2Range bbox) {
		this(bbox.getXMin(), bbox.getYMin(), bbox.getXRange().getRange(), bbox.getYRange().getRange());
	}
	
	public SVGRect(Point2D p0, Point2D p1) {
		this(new Real2(p0.getX(), p0.getY()), new Real2(p1.getX(), p1.getY()));
	}

	protected void drawElement(Graphics2D g2d) {
		saveGraphicsSettingsAndApplyTransform(g2d);
		ensureCumulativeTransform();
		double x1 = this.getDouble(X);
		double y1 = this.getDouble(Y);
		Real2 xy1 = new Real2(x1, y1);
		xy1 = transform(xy1, cumulativeTransform);
		double w = this.getDouble(WIDTH);
		double h = this.getDouble(HEIGHT);
		Real2 xy2 = new Real2(x1+w, y1+h);
		xy2 = transform(xy2, cumulativeTransform);
		
		Rectangle2D rect = new Rectangle2D.Double(xy1.x, xy1.y, xy2.x-xy1.x, xy2.y-xy1.y);
		fill(g2d, rect);
		draw(g2d, rect);
		restoreGraphicsSettingsAndTransform(g2d);
	}


	/** rotate
	 *  this only works for 0 += PI/2, +- PI
	 *  if you want to rotate by other angles convert to a SVGPolygon
	 */
	public void applyTransformPreserveUprightText(Transform2 t2) {
		//assume scale and translation only
		Real2 xy = getXY();
		xy.transformBy(t2);
		this.setXY(xy);
		double h = getHeight();
		double w = getWidth();
		Angle a = t2.getAngleOfRotation();
		Real2 xxyy = new Real2(xy.getX()+getWidth(), xy.getY()+getHeight());
		xxyy.transformBy(t2);
		if (a.isEqualTo(new Angle(Math.PI / 2.), 0.00001) ||
				a.isEqualTo(new Angle(-Math.PI / 2.0), 0.00001)) {
			setHeight(w);
			setWidth(h);
		}
	}
	
    /** round to decimal places.
     * 
     * @param places
     */
    public void format(int places) {
    	Real2 xy = getXY();
    	xy.format(places);
    	setXY(xy);
    	setHeight(Util.format(getHeight(), places));
    	setWidth(Util.format(getWidth(), places));
    	forceGetBoundingBox();
    	boundingBox = boundingBox.format(places);
    }
	
	/** extent of rect
	 * 
	 * @return
	 */
	public Real2Range getBoundingBox() {
		if (boundingBoxNeedsUpdating()) {
			forceGetBoundingBox();
		}
		return boundingBox;
	}

	private void forceGetBoundingBox() {
		boundingBox = new Real2Range();
		Real2 origin = getXY();
		boundingBox.add(origin);
		boundingBox.add(origin.plus(new Real2(getWidth(), getHeight())));
	}
	
	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public void setBounds(Real2Range r2r) {
		if (r2r != null) {
			RealRange xr = r2r.getXRange();
			RealRange yr = r2r.getYRange();
			this.setXY(new Real2(xr.getMin(), yr.getMin()));
			this.setWidth(xr.getRange());
			this.setHeight(yr.getRange());
		}
	}
	
	/** makes a new list composed of the rects in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGRect> extractRects(List<SVGElement> elements) {
		List<SVGRect> rectList = new ArrayList<SVGRect>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGRect) {
				rectList.add((SVGRect) element);
			}
		}
		return rectList;
	}
	
	@Override
	public String getGeometricHash() {
		return getAttributeValue(X)+" "+getAttributeValue(Y)+" "+getAttributeValue(WIDTH)+" "+getAttributeValue(HEIGHT);
	}

	public static List<SVGRect> extractSelfAndDescendantRects(AbstractCMElement svgElem) {
		return SVGRect.extractRects(SVGUtil.getQuerySVGElements(svgElem, ALL_RECT_XPATH));
	}

	public boolean isEqual(SVGRect otherRect, double delta) {
		Real2[] corners = this.getBoundingBox().getLLURCorners();
		Real2[] otherCorners = otherRect.getBoundingBox().getLLURCorners();
		return corners[0].getDistance(otherCorners[0]) < delta && corners[1].getDistance(otherCorners[1]) < delta;
	}
	
	@Override
	public String toString() {
		if (boundingBox == null) {
			boundingBox = getBoundingBox();
		}
		return boundingBox.toString();
	}

	@Override
	protected boolean isGeometricallyEqualTo(SVGElement shape, double epsilon) {
		if (shape != null && shape instanceof SVGRect) {
			return this.isEqual((SVGRect) shape, epsilon);
		}
		return false;
	}

	public Real2 getXY() {
		return new Real2(getX(), getY());
	}

	public Int2Range createIntBoundingBox() {
		int x = (int)(double)(new Double(getAttributeValue(X)));
		int y = (int)(double)(new Double(getAttributeValue(Y)));
		String dxs = getAttributeValue(SVGRect.WIDTH);
		Integer dx = (int)(double)(Double.valueOf(dxs));
		String dys = getAttributeValue(SVGRect.HEIGHT);
		Integer dy = (int)(double)(Double.valueOf(dys));
		Int2Range boundingBox = new Int2Range(new IntRange(x, x + dx), new IntRange(y, y + dy));
		return boundingBox;
	}

	/** rects outside y=0 are not part of the plot but confuse calculation of
	 * bounding box 
	 * @param rectList
	 * @return
	 */
	public static List<SVGRect> removeRectsWithNegativeY(List<SVGRect> rectList) {
		List<SVGRect> newRects = new ArrayList<SVGRect>();
		for (SVGRect rect : rectList) {
			Real2Range bbox = rect.getBoundingBox();
			if (bbox.getYMax() >= 0.0) {
				newRects.add(rect);
			}
		}
		return newRects;
	}

}

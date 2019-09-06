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
import java.util.ArrayList;
import java.util.List;

import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.graphics.AbstractCMElement;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

/** a virtual point. May not correspond completely with SVG <marker>
 * creates a "point", drawn by an arbitrary symbol
 * @author pm286
 *
 *
 *typical marker as arrowhead
 *<svg width="500" height="500" xmlns="http://www.w3.org/2000/svg" viewBox="0 40 400 200">
    <marker id="triangle"
      viewBox="0 0 10 10" refX="0" refY="5" 
      markerUnits="strokeWidth"
      markerWidth="4" markerHeight="3"
      orient="auto">
      <path d="M 0 0 L 10 5 L 0 10 z" />
    </marker>
	<line x1="100" y1="50.5" x2="300" y2="50.5" marker-end="url(#triangle)" stroke="black" stroke-width="10"/>
	<polyline points="100 100 300 100" marker-end="url(#triangle)" stroke="black" stroke-width="10"/>
	<path d="M100 150.5l200 0" marker-end="url(#triangle)" stroke="black" stroke-width="10"/>
</svg>

marker for truncated line
   <marker id="zeroline"
      viewBox="-5 -5 10 10" refX="0" refY="0" 
      markerUnits="strokeWidth" 
      markerWidth="5" markerHeight="5" orient="auto">
      <circle cx="0" cy="0" r="4" stroke-width="1.5" stroke="blue" fill="yellow"/>
    </marker>

 */
public class SVGMarker extends SVGElement {

	final public static String TAG ="marker";
	public final static String VIEW_BOX = "viewBox";
	public final static String REFX = "refX";
	public final static String REFY = "refY";
	public final static String MARKER_UNITS = "markerUnits";
	public final static String MARKER_WIDTH = "markerWidth";
	public final static String MARKER_HEIGHT = "markerHeight";
	public final static String ORIENT = "orient";
	public final static String AUTO = "auto";
	public static final String STROKE_WIDTH = "strokeWidth";
	public static final String MARKER_END = "marker-end";
	public static final String MARKER_MID = "marker-mid";
	public static final String MARKER_START = "marker-start";
	
//	public static SVGCircle ZEROLINE_CIRCLE;
//	public static SVGMarker ZEROLINE;
//	public static String ZEROLINE_ID = "zeroline";
//	
//	public static SVGCircle ZEROPATH_CIRCLE;
//	public static SVGMarker ZEROPATH;
//	public static String ZEROPATH_ID = "zeropath";
//	static {
//		ZEROLINE_CIRCLE = new SVGCircle(new Real2(0.0, 0.0), 4.0);
//		ZEROLINE_CIRCLE.setStrokeWidth(1.5);
//		ZEROLINE_CIRCLE.setStroke("red");
//		ZEROLINE_CIRCLE.setFill("cyan");
//		ZEROLINE = new SVGMarker();
//		ZEROLINE.setId(ZEROLINE_ID);
//		ZEROLINE.setViewBox("-5 -5 10 10");
//		ZEROLINE.setRefX("0");
//		ZEROLINE.setRefY("0");
//		ZEROLINE.setMarkerUnits(STROKE_WIDTH);
//		ZEROLINE.setMarkerWidth(5);
//		ZEROLINE.setMarkerHeight(5);
//		ZEROLINE.setOrient(AUTO);
//		ZEROLINE.appendChild(ZEROLINE_CIRCLE);
//		
//		ZEROPATH_CIRCLE = new SVGCircle(new Real2(0.0, 0.0), 4.0);
//		ZEROPATH_CIRCLE.setStrokeWidth(1.5);
//		ZEROPATH_CIRCLE.setStroke("blue");
//		ZEROPATH_CIRCLE.setFill("orange");
//		ZEROPATH = new SVGMarker();
//		ZEROPATH.setId(ZEROPATH_ID);
//		ZEROPATH.setViewBox("-5 -5 10 10");
//		ZEROPATH.setRefX("0");
//		ZEROPATH.setRefY("0");
//		ZEROPATH.setMarkerUnits(STROKE_WIDTH);
//		ZEROPATH.setMarkerWidth(5);
//		ZEROPATH.setMarkerHeight(5);
//		ZEROPATH.setOrient(AUTO);
//		ZEROPATH.appendChild(ZEROPATH_CIRCLE);
//		
//	};
	// an addition
	private List<SVGLine> lineList; 
	private static double size = 2;

	/** constructor
	 */
	public SVGMarker() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGMarker(SVGElement element) {
		super(element);
	}
	
	/** constructor
	 */
	public SVGMarker(Element element) {
        super((SVGElement) element);
	}
	
	protected void init() {
	}

    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGMarker(this);
    }


    public void setSymbol(AbstractCMElement symbol) {
    	AbstractCMElement symb = this.getSymbol();
    	if (symb != null) {
    		symb.detach();
    	}
    	this.appendChild(symbol);
    }
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGMarker(double x, double y) {
		this();
//		setX(x);
//		setY(y);
	}

	
	public SVGMarker(Real2 xy) {
		this();
//		SVGElement symbol = new SVGCircle(xy.plus(new Real2(-size, -size)), size);
		AbstractCMElement symbol = new SVGCircle(xy, size);
		this.appendChild(symbol);
	}

	protected void drawElement(Graphics2D g2d) {
		saveGraphicsSettingsAndApplyTransform(g2d);
//		double x1 = this.getDouble("x");
//		double y1 = this.getDouble("y");
//		Real2 xy1 = new Real2(x1, y1);
//		xy1 = transform(xy1, cumulativeTransform);
//		double w = this.getDouble("width");
//		double h = this.getDouble("height");
//		Real2 xy2 = new Real2(x1+w, y1+h);
//		xy2 = transform(xy2, cumulativeTransform);
//		float width = 1.0f;
//		String style = this.getAttributeValue("style");
//		if (style.startsWith("stroke-width:")) {
//			style = style.substring("stroke-width:".length());
//			style = style.substring(0, (style+S_SEMICOLON).indexOf(S_SEMICOLON));
//			width = (float) new Double(style).doubleValue();
//			width *= 15.f;
//		}
//		
//		Stroke s = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
//		g2d.setStroke(s);
//		
//		String colorS = "black";
//		String stroke = this.getAttributeValue("stroke");
//		if (stroke != null) {
//			colorS = stroke;
//		}
//		Color color = colorMap.get(colorS);
//		g2d.setColor(color);
//		Line2D line = new Line2D.Double(xy1.x, xy1.y, xy2.x, xy2.y);
//		g2d.draw(line);
		restoreGraphicsSettingsAndTransform(g2d);
	}
	
	public void applyTransformPreserveUprightText(Transform2 t2) {
		//assume scale and translation only
//		Real2 xy = getXY();
//		xy.transformBy(t2);
//		this.setXY(xy);
//		Real2 xxyy = new Real2(xy.getX()+getWidth(), xy.getY()+getHeight());
//		xxyy.transformBy(t2);
	}
	
    /** round to decimal places.
     * 
     * @param places
     * @return this
     */
    public void format(int places) {
//    	setXY(getXY().format(places));
//    	setHeight(Util.format(getHeight(), places));
//    	setWidth(Util.format(getWidth(), places));
    }
	
	/** extent 
	 * 
	 * @return
	 */

//	public Real2Range getBoundingBox() {
//		if (boundingBoxNeedsUpdating()) {
//			SVGElement element = getSymbol();
//			boundingBox = (element == null) ? null : element.getBoundingBox();
//		}
//		return boundingBox;
//	}
    /** return null
     * 
     */
	public Real2Range getBoundingBox() {
		return null;
	}
	
	public SVGElement getSymbol() {
		if (this.getChildElements().size()  == 1) {
			Element element = this.getChildElements().get(0);
			return (element instanceof SVGElement) ? (SVGElement) element : null;
		}
		return null;
	}

	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	public void addLine(SVGLine line) {
		ensureLineList();
		lineList.add(line);
	}

	private void ensureLineList() {
		if (lineList == null) {
			lineList = new ArrayList<SVGLine>();
		}
	}

	public void setViewBox(String box) {
		this.addAttribute(new Attribute(VIEW_BOX, box));
	}

	public void setRefX(String x) {
		this.addAttribute(new Attribute(REFX, x));
	}

	public void setRefY(String y) {
		this.addAttribute(new Attribute(REFY, y));
	}

	public void setMarkerUnits(String units) {
		this.addAttribute(new Attribute(MARKER_UNITS, units));
	}

	public void setMarkerWidth(double width) {
		this.addAttribute(new Attribute(MARKER_WIDTH, String.valueOf(width)));
	}

	public void setMarkerHeight(double height) {
		this.addAttribute(new Attribute(MARKER_HEIGHT, String.valueOf(height)));
	}

	public void setOrient(String orient) {
		this.addAttribute(new Attribute(ORIENT, orient));
	}

}

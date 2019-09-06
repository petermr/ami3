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

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Array;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

/** draws a straight line.
 * 
 * @author pm286
 *
 */
public class SVGTitle extends SVGElement {
	
	private final static Logger LOG = Logger.getLogger(SVGTitle.class);

	public final static String TAG ="title";

	protected Real2Array real2Array;
	
	/** constructor
	 */
	public SVGTitle() {
		super(TAG);
		init();
	}
	
	/** constructor
	 */
	public SVGTitle(SVGElement element) {
        super(element);
	}
	
	/** constructor
	 */
	public SVGTitle(Element element) {
        super((SVGElement) element);
	}
	
	/** constructor.
	 * 
	 * @param x1
	 * @param x2
	 */
	public SVGTitle(String title) {
		this();
		this.appendChild(new Text(title));
	}
	
	protected void init() {
		super.setDefaultStyle();
//		setDefaultStyle(this);
	}
	public static void setDefaultStyle(SVGElement line) {
		line.setStroke("black");
		line.setStrokeWidth(1.0);
	}
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGTitle(this);
    }
	
	protected void drawElement(Graphics2D g2d) {
		LOG.error("SVGTitle.drawElement NYI");
//		processTransformToAffineTransform(g2d);
////		Path2D path = createAndSetPath2D();
//		applyAttributes(g2d);
////		g2d.draw(path);
//		resetAffineTransform(g2d);
	}

	public void applyAttributes(Graphics2D g2d) {
		if (g2d != null) {
//			float width = (float) this.getStrokeWidth();
//			Stroke s = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
//			g2d.setStroke(s);
			super.applyAttributes(g2d);
		}
	}

	/** get tag.
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

}

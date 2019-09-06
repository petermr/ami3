package org.contentmine.graphics.svg.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;

/** a grid of rectangular boxes, touching or overlapping.
 * not fully worked out.
 * At present these boxes are simply boxes even though elsewhere they may have semantics and content
 * 
 * 
 * @author pm286
 *
 */
public class ContentBoxGrid {

	private static final double OPACITY = 0.3;
	private static final double STROKE_WIDTH = 1.0;
	private static final String FILL = "yellow";
	private static final Logger LOG = Logger.getLogger(ContentBoxGrid.class);
	private static final String CONTEXT_BOX_GRID = "contextBoxGrid";
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<Real2Range> bboxList;
	private double delta = 0.5;

	public ContentBoxGrid() {
		bboxList = new ArrayList<Real2Range>();
	}
	
	/** adds a bbox to the bbox list.
	 * if the box touches existing boxes it is agglomerated into a large box.
	 * For many applications the final results may be one or a few superboxes
	 * formed from many touching smaller boxes.
	 * 
	 * uses Real2Range.agglomerateIntersections()
	 * 
	 * @param rect
	 * @return
	 */
	public boolean add(SVGRect rect) {
		return Real2Range.agglomerateIntersections(rect.getBoundingBox(), bboxList, delta);
	}

	public void add(List<SVGRect> rectList) {
		for (SVGRect rect : rectList) {
			add(rect);
		}
	}

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	/** the list of agglomerated bboxes.
	 * Elsewhere these may be referred to as "panels" or superpanels
	 * 
	 * @return
	 */
	public List<Real2Range> getBboxList() {
		return bboxList;
	}

	/** an SVG rendering of the contents.
	 * May not preserve semantics.
	 * 
	 * @return
	 */
	public AbstractCMElement getOrCreateSVGElement() {
		SVGG g = new SVGG();
		g.setSVGClassName(CONTEXT_BOX_GRID);
		for (Real2Range box : bboxList) {
			SVGRect rect = SVGRect.createFromReal2Range(box);
			g.appendChild(rect);
			rect.setCSSStyle("stroke-width:" + STROKE_WIDTH + ";fill:" + FILL + ";opacity:" + OPACITY + ";");
		}
		return g;
	}
	
	public String toString() {
		return bboxList == null ? null : bboxList.toString();
	}

}

package org.contentmine.graphics.svg.linestuff;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGText;

import nu.xom.Node;

/** simple tool for joining lines.
 * might supersede old Joining routines
 * probably closely related to PixelNode
 * 
 * @author pm286
 *
 */
public class SVGNode extends SVGG {
	private static final Logger LOG = Logger.getLogger(SVGNode.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<SVGEdge> edges;
	private Real2 point;
	protected String label;
	private double radius = 5.0;
	
	public SVGNode() {
		
	}

	public SVGNode(SVGText text) {
		point = new Real2(text.getXY());
		label = text.getText();
		this.appendChild(text.copy());
	}

	public boolean joinEdge(SVGEdge edge, double delta) {
		boolean joined = joinEdge(edge, 0, delta);
		joined |= joinEdge(edge, 1, delta);
		return joined;
	}

	public boolean joinEdge(SVGEdge edge, int iend, double delta) {
		boolean joined = false;
		Real2 xy = edge.getXY(iend);
		if (xy.getDistance(point) < delta) {
			addEdge(edge, iend);
			joined = true;
		}
		return joined;
	}

	/** will adjust coords of point to weighted mean
	 * 
	 * @param edge
	 * @param iend
	 */
	public void addEdge(SVGEdge edge, int iend) {
		if (iend == 0 || iend == 1) {
			Real2 xy = edge.getXY(iend);
			getOrCreateEdges();
			edges.add(edge);
			
			if (point == null) {
				point = xy;
			} else {
				// weighted mean
				point = point.multiplyBy(edges.size() - 1).plus(xy);
				point.multiplyBy(1.0 / edges.size());
			}
			edge.addNode(this, iend);

		}
	}

	public List<SVGEdge> getOrCreateEdges() {
		if (edges == null) {
			edges = new ArrayList<SVGEdge>();
		}
		return edges;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getId()+"; ");
		point = point.format(3);
		sb.append(point);
		sb.append("; "+label);
		getOrCreateEdges();
		sb.append("; edges: ");
		for (SVGEdge edge : edges) {
			sb.append(edge.getId()+"; ");
		}
		return sb.toString();
	}

	public AbstractCMElement getOrCreateSVG() {
		SVGG g = new SVGG();
		g.appendChild(this.copy());
		g.appendChild(SVGText.createText(this.getXY(), getId(), "fill:red;font-size:2;"));
		return g;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public void addLine(SVGEdge edge) {
		int end = edge.indexOf(this);
		if (end != -1) {
			addEdge(edge, end);
		} else {
			LOG.error("failed to add edge");
		}
	}
	
	public Real2 getXY() {
		return point;
	}

	@Override
	public Node copy() {
		SVGNode node = new SVGNode();
		node.copyAttributesChildrenElements(this);
		node.point = this.point;
		node.label = this.label;
		node.edges = new ArrayList<SVGEdge>(this.edges);
		return node;
	}
	public static String getSummary(List<SVGNode> nodeList) {
		if (nodeList == null) {
			return "NULL NodeList";
		}
		StringBuilder sb = new StringBuilder();
		for (SVGNode node : nodeList) {
			sb.append(node.toString()+"\n");
		}
		return sb.toString();
	}

	public boolean addEdge(SVGEdge edge, double eps) {
		int end = edge.getIndexOfPoint(point, eps);
		if (end != -1) {
			addEdge(edge, end);
			return true;
		}
		LOG.debug("failed to find "+point+"("+getId()+")"+" in "+edge);
		return false;
	}

	/** add edge if not already in llist.
	 * 
	 * @param svgEdge
	 */
	public void add(SVGEdge svgEdge) {
		List<SVGEdge> edges = this.getOrCreateEdges();
		if (!edges.contains(svgEdge)) {
			edges.add(svgEdge);
		}
	}
	
}

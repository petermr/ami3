package org.contentmine.graphics.svg.linestuff;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGText;

public class SVGEdge extends SVGLine {
	private static final Logger LOG = Logger.getLogger(SVGEdge.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** each node has a list of edges.
	 * ethene has 0,0
	 * propene has 0,1
	 * but-2-ene has 0,2
	 * but-2-ene has 1,1
	 */
	protected List<SVGNode> nodeList;
	
	private double weight = 1;
	protected String label = "";

	public SVGEdge() {
		getOrCreateNodeList();
	}

	public SVGEdge(SVGElement line) {
		super();
		this.copyAttributesChildrenElements(line);
		return;
	}
	
	public void addNode(SVGNode node, int iend) {
		getOrCreateNodeList();
		nodeList.set(iend, node);
		node.add(this);
	}

	/** get nodeList or create a new one.
	 * if new one created, fill with 2 null nodes
	 * @return
	 */
	public List<SVGNode> getOrCreateNodeList() {
		if (nodeList == null) {
			nodeList = new ArrayList<SVGNode>();
			nodeList.add((SVGNode)null);
			nodeList.add((SVGNode)null);
		}
		return nodeList;
	}
	
	public AbstractCMElement getOrCreateSVG() {
		SVGG g = new SVGG();
		SVGLine edgeCopy = (SVGLine) this.copy();
		edgeCopy.setStrokeWidth(this.getWeight());
		g.appendChild(edgeCopy);
		g.appendChild(SVGText.createText(this.getMidPoint(), getId(), "fill:green;font-size:2;"));
		return g;
	}
	
	public String toString() {
		getOrCreateNodeList();
		StringBuilder sb = new StringBuilder();
		sb.append(this.getId()+": ");
		SVGNode node0 = nodeList.get(0);
		sb.append(node0 == null ? "null " : node0.getId()+"; ");
		SVGNode node1 = nodeList.get(1);
		sb.append(node1 == null ? "null ": node1.getId()+"; ");
		sb.append(this.getXY(0)+" "+this.getXY(1)+"; label: "+label+"; wt: "+getWeight()+" ");
		return sb.toString();
	}

	public void removeNode(SVGNode node) {
		int iend = nodeList.indexOf(node);
		removeNode(iend);
	}
	
	public void removeNode(int iend) {
		nodeList.set(iend, (SVGNode)null);
	}

	public SVGNode getNode(int index) {
		getOrCreateNodeList();
		return index >= 2 ? null :  nodeList.get(index);
	}

	/** gets total number of edges hanging off edge.
	 * 
	 * @return
	 */
	public int getBranchEdgeCount() {
		// subtract 1 so as not to count this
		int edges0 = getNode(0).getOrCreateEdges().size() - 1;
		int edges1 = getNode(1).getOrCreateEdges().size() - 1;
		return edges0 + edges1;
	}

	public void setWeight(int w) {
		this.weight = w;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int indexOf(SVGNode node) {
		return (node == null || nodeList == null || nodeList.size() != 2) ? null : nodeList.indexOf(node);
	}
	
}

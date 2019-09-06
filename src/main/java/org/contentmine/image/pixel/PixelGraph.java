package org.contentmine.image.pixel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Angle.Units;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Line2;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGPoly;
import org.contentmine.image.ImageParameters;
import org.contentmine.image.pixel.PixelComparator.ComparatorType;
import org.contentmine.image.pixel.PixelNucleus.PixelJunctionType;

import com.google.common.collect.Multiset;


/**
 * holds PixelNodes and PixelEdges for pixelIsland
 * 
 * a graph may be a number of PixelNodes and PixelEdges. A single cycle will have a 
 * single PixelNode located in an arbitrary position
 * 
 * @author pm286
 * 
 */
public class PixelGraph {

	static final Logger LOG = Logger.getLogger(PixelGraph.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

//	static final double SEGMENT_EPS = 0.1;
	private static final double DEFAULT_SEGMENT_CREATION_TOLERANCE = 1.0;
	private static final int MIN_CYCLE = 6;
	private static final String NODE_PREFIX = "zn";
	private static final Angle ANGLE_EPS = new Angle(0.03, Units.RADIANS);
	private static final double MAX_MEAN_CIRCLE_DEVIATION = 1.0;
	public static String[] COLOURS = new String[] {"red", "green", "pink", "cyan", "orange", "blue", "yellow"};

	private PixelEdgeList edgeList; 
	private PixelNodeList nodeList; 
	private PixelList pixelList;
	private PixelIsland island;
	private Stack<PixelNode> nodeStack;
	private PixelNode rootNode;
	private Boolean isSingleCycle = null; // 3-valued (null, false,true) as needs to flag whether has been set
	
	private double segmentCreationTolerance = DEFAULT_SEGMENT_CREATION_TOLERANCE;
	private boolean hasBeenSegmented = false;
	private PixelNucleusFactory nucleusFactory;
	private ImageParameters imageParameters;
		
	private PixelGraph() {
		
	}
	
	public PixelGraph(PixelIsland island) {
		this(island.getPixelList(), island);

	}
	
	public PixelGraph(PixelList list) {
		this.island = list.getIsland();
		createGraph(list);
	}
	
	/** all pixels have to belong to island
	 * 
	 * @param pixelList
	 * @param island
	 */
	public PixelGraph(PixelList pixelList, PixelIsland island) {
		this.island = island;
		createGraph(pixelList);
	}

	private void createGraph(PixelList pixelList) {
		if (pixelList == null) {
			throw new RuntimeException("null pixelList");
		}
		this.pixelList = pixelList;
		this.createNodesAndEdges();
//		this.tidyEdgePixelLists();
//		this.compactCloseNodes(3);
		return;
	}

	/** creates graph without pixels
	 * 
	 * @return
	 */
	public static PixelGraph createEmptyGraph() {
		return new PixelGraph();
	}

	/**
	 * creates graph and fills it.
	 * 
	 * @param island
	 * @return
	 */
	public static PixelGraph createGraph(PixelIsland island) {
		PixelGraph pixelGraph = null;
		if (island != null) {
			pixelGraph = new PixelGraph(island.getPixelList(), island);
		}
		return pixelGraph;
	}

	void createNodesAndEdges() {
		if (edgeList == null) {
			nucleusFactory = getOrCreateNucleusFactory();
			edgeList = nucleusFactory.createPixelEdgeListFromNodeList();
		}
	}

	private void createNodeList() {
		nodeList = getOrCreateNucleusFactory().getOrCreateNodeListFromNuclei();
	}

	
	/**
	 * gets next pixel in chain.
	 * 
	 * @param current
	 * @param last
	 * @param island
	 * @return next pixel or null if no more or branch
	 */
	static Pixel getNextUnusedInEdge(Pixel current, Pixel last,
			PixelIsland island) {
		Pixel next = null;
		PixelList neighbours = current.getOrCreateNeighbours(island);
		neighbours.remove(last);
		next = neighbours.size() == 1 ? neighbours.get(0) : null;
		Long time3 = System.currentTimeMillis();
		return next;
	}

	public PixelNodeList getOrCreateNodeList() {
		if (nodeList == null) {
			if (island != null) {
				getOrCreateNucleusFactory().createNodesAndEdges();
				nodeList = getOrCreateNucleusFactory().getOrCreateNodeListFromNuclei();
				edgeList = getOrCreateNucleusFactory().getEdgeList();
			} else {
				LOG.debug("NULL ISLAND");
				ensureNodes();
			}
		}
		return nodeList;
	}

	public PixelList getPixelList() {
		return pixelList;
	}

	public String toString() {
		getOrCreateEdgeList();
		StringBuilder sb = new StringBuilder();
		sb.append("; edges: " + (edgeList == null ? "none" : edgeList.size()+"; "+edgeList.toString()));
		sb.append("\n     ");
		sb.append("nodes: " + (nodeList == null ? "none" : nodeList.size()+"; "+nodeList.toString()));
		return sb.toString();
	}

	/** get root pixel as middle of leftmost internode edge.
	 * 
	 *  where mid edge is vertical.
	 *  
	 * @return
	 */
	public PixelNode getRootNodeFromExtremeEdge(ComparatorType comparatorType) {
		PixelEdge extremeEdge = getExtremeEdge(comparatorType);
		if (extremeEdge == null) {
			throw new RuntimeException("Cannot find extreme edge for "+comparatorType);
		}
		LOG.trace("extreme "+extremeEdge+"; nodes "+extremeEdge.getNodes().size());
		
		Pixel midPixel = extremeEdge.getNearestPixelToMidPoint();
		rootNode = splitEdgeAndInsertNewNode(extremeEdge, midPixel);
				
		return rootNode;
	}
	
	public PixelNode getRootPixelNode() {
		return rootNode;
	}

	public PixelNode splitEdgeAndInsertNewNode(PixelEdge oldEdge, Pixel midPixel) {
		PixelNode midNode = new PixelNode(midPixel, this);
		PixelList neighbours = midPixel.getOrCreateNeighbours(island);
		if (neighbours.size() != 2) {
			throw new RuntimeException("Should have exactly 2 neighbours "+neighbours.size());
		}

		PixelEdgeList edgeList = splitEdge(oldEdge, midPixel, midNode);
		this.addEdge(edgeList.get(0));
		this.addEdge(edgeList.get(1));
		this.addNode(midNode);
		this.removeEdge(oldEdge);
		return midNode;
	}

	private void removeEdge(PixelEdge edge) {
		edgeList.remove(edge);
		PixelNodeList nodeList = edge.getNodes();
		for (PixelNode node : nodeList) {
			node.removeEdge(edge);
		}
	}

	private void removeNode(PixelNode node) {
		nodeList.remove(node);
		PixelEdgeList edgeList = node.getEdges();
		for (PixelEdge edge : edgeList) {
			edge.removeNode(node);
		}
	}

	private PixelEdgeList splitEdge(PixelEdge edge, Pixel midPixel, PixelNode midNode) {
		
		PixelEdgeList edgeList = new PixelEdgeList();
		PixelNodeList nodes = edge.getNodes();
		if (nodes.size() != 2) {
			LOG.error("Should have exactly 2 extremeNodes found "+nodes.size());
			return edgeList;
		}
		
		PixelList edgePixelList = edge.getPixelList();
		PixelList beforePixelList = edgePixelList.getPixelsBefore(midPixel);
		PixelList afterPixelList = edgePixelList.getPixelsAfter(midPixel);
		
		Pixel beforePixelLast = beforePixelList.last();
		Pixel afterPixelLast = afterPixelList.last();
		if (!beforePixelLast.equals(beforePixelList.last())) {
			beforePixelList.add(beforePixelLast);
		}
		if (!afterPixelLast.equals(afterPixelList.last())) {
			afterPixelList.add(afterPixelLast);
		}
		
		PixelEdge edge0 = createEdge(midNode, nodes.get(0), beforePixelList);
		edgeList.add(edge0);
		PixelEdge edge1 = createEdge(midNode, nodes.get(1), afterPixelList);
		edgeList.add(edge1);
		
		return edgeList;
	}

	private PixelEdge createEdge(PixelNode splitNode, PixelNode newEndNode, PixelList pixelList) {
		PixelEdge edge = new PixelEdge(island);
		edge.addNode(splitNode, 0);
		edge.addNode(newEndNode, 1);
		edge.addPixelList(pixelList);
		return edge;
	}

	@Deprecated
	private PixelEdge getExtremeEdge(ComparatorType comparatorType) {
		PixelEdge extremeEdge = null;
		double extreme = Double.MAX_VALUE;
		for (PixelEdge edge : edgeList) {
			LOG.trace(edge);
			PixelSegmentList segmentList = edge.getOrCreateSegmentList(getParameters().getSegmentTolerance());
			LOG.trace("PL "+segmentList.size()+"  /  "+segmentList.getReal2Array());
			// look for goal post edge
			if (segmentList.size() != 3) {
				continue;
			}
			Line2 crossbar = segmentList.get(1).getEuclidLine();
			Real2 midPoint = crossbar.getMidPoint();
			// LHS
			if (ComparatorType.LEFT.equals(comparatorType) && crossbar.isVertical(ANGLE_EPS)) {
				if (midPoint.getX() < extreme) {
					extreme = midPoint.getX();
					extremeEdge = edge;
					LOG.trace("edge "+midPoint);
				}
			// RHS
			} else if (ComparatorType.RIGHT.equals(comparatorType) && crossbar.isVertical(ANGLE_EPS)) {
				if (midPoint.getX() > extreme) {
					extreme = midPoint.getX();
					extremeEdge = edge;
				}
			// TOP
			} else if (ComparatorType.TOP.equals(comparatorType) && crossbar.isHorizontal(ANGLE_EPS)) {
				if (midPoint.getY() < extreme) {
					extreme = midPoint.getY();
					extremeEdge = edge;
				}
			// BOTTOM
			} else if (ComparatorType.BOTTOM.equals(comparatorType) && crossbar.isHorizontal(ANGLE_EPS)) {
				if (midPoint.getY() > extreme) {
					extreme = midPoint.getY();
					extremeEdge = edge;
				}
			}
		}
		return extremeEdge;
	}

	/** assume node in middle of 3-segment path.
	 * 
	 * @return
	 */
	public PixelNodeList getPossibleRootNodes() {
		PixelNodeList nodeList = new PixelNodeList();
		PixelEdge rootEdge = null;
		PixelNode midNode = null;
		for (PixelEdge edge : edgeList) {
			LOG.trace(edge.getNodes());
			PixelSegmentList segmentList = edge.getOrCreateSegmentList(getParameters().getSegmentTolerance());
			Angle deviation = segmentList.getSignedAngleOfDeviation();
			if (Math.abs(deviation.getRadian()) < 2.0) continue;
			LOG.trace("POLY "+segmentList.get(0)+"/"+segmentList.getLast()+"/"+deviation);
			if (segmentList.size() == 3) {
				SVGLine midline = segmentList.get(1).getSVGLine();
				Pixel midPixel = edge.getNearestPixelToMidPoint(midline.getMidPoint());
				midNode = new PixelNode(midPixel, this);
				addNode(nodeList, midNode);
				rootEdge = edge;
			}
		}
		if (nodeList.size() == 1) {
			rootNode = nodeList.get(0);
			removeOldEdgeAndAddNewEdge(rootNode, rootEdge, 0);
			removeOldEdgeAndAddNewEdge(rootNode, rootEdge, 1);
		}
		return nodeList;
	}

	private void addNode(PixelNodeList nodeList, PixelNode node) {
		if (node == null) {
			throw new RuntimeException("Cannot add null node");
		}
		if (nodeList == null) {
			throw new RuntimeException("Null nodeList");
		}
		nodeList.add(node);
	}

	private void removeOldEdgeAndAddNewEdge(PixelNode rootNode, PixelEdge rootEdge, int nodeNum) {
		PixelNode childNode = rootEdge.getPixelNode(nodeNum);
		this.removeEdgeFromNode(childNode, rootEdge);
		addNewEdge(rootNode, childNode);
	}

	private void addNewEdge(PixelNode node0, PixelNode node1) {
		PixelEdge edge = new PixelEdge(this);
		if (node0 != null) {
	 		edge.addNode(node0, 0);
			node0.addEdge(edge);
		}
		if (node1 != null) {
			edge.addNode(node1, 1);
			node1.addEdge(edge);
		}
		this.edgeList.add(edge);
	}

	private void removeEdgeFromNode(PixelNode node, PixelEdge edge) {
		if (node != null) {
			node.removeEdge(edge);
		}
		edgeList.remove(edge);
	}

	public void addNode(PixelNode node) {
		ensureNodes();
		if (!nodeList.contains(node)) {
			addNode(nodeList, node);
		}
	}

	public void addEdge(PixelEdge edge) {
		getOrCreateEdgeList();
		if (!edgeList.contains(edge)) {
			edgeList.add(edge);
			addNode(edge.getPixelNode(0));
			addNode(edge.getPixelNode(1));
		}
	}

	private void ensureNodes() {
		if (nodeList == null) {
			nodeList = new PixelNodeList();
		}
	}

	public void numberTerminalNodes() {
		int i = 0;
		for (PixelNode node : getOrCreateNodeList()) {
//			if (node != instanceof TerminalNode) {
				node.setLabel(NODE_PREFIX + i);
//			}
			Pixel pixel = node.getCentrePixel();
			Int2 int2 = pixel == null ? null : pixel.getInt2();
			Integer x = (int2 == null) ? null : int2.getX();
			Integer y = (int2 == null) ? null : int2.getY();
			if (x == null || y == null) {
				node.setLabel("N"+i);
			} else {
				node.setLabel(x+"_"+y);
			}
			i++;
		}
	}

	public SVGG drawEdgesAndNodes() {
		String[] colours = {"red", "green", "blue", "yellow", "pink", "gray", "brown", "orange"};
		return drawEdgesAndNodes(colours);
	}
	
	public SVGG drawEdgesAndNodes(String[] colours) {
		getOrCreateEdgeList();
		SVGG g = new SVGG();
		SVGG rawPixelG = pixelList.plotPixels("magenta");
		g.appendChild(rawPixelG);
		drawEdges(colours, g);
		drawNodes(colours, g);
		return g;
	}

	public void drawNodes(String[] colours, SVGG g) {
		ensureNodeList();
		if (nodeList.size() == 0) {
			LOG.warn("No nodes in graph");
		}
		for (int i = 0; i < nodeList.size(); i++) {
			String col = colours[i % colours.length];
			PixelNode node = nodeList.get(i);
			if (node != null) {
				SVGG nodeG = node.createSVG(1.0);
				nodeG.setStroke(col);
				nodeG.setStrokeWidth(0.3);
				nodeG.setOpacity(0.5);
				nodeG.setFill("none");
				g.appendChild(nodeG);
			}
		}
	}

	public void drawEdges(String[] colours, SVGG g) {
		for (int i = 0; i < edgeList.size(); i++) {
			String col = colours[i % colours.length];
			PixelEdge edge = edgeList.get(i);
			SVGG edgeG = edge.createPixelSVG(col);
			edgeG.setFill(col);
			g.appendChild(edgeG);
			SVGG lineG = edge.createLineSVG();
			lineG.setFill(col);
			g.appendChild(lineG);
		}
	}

	@Deprecated
	public PixelNode createRootNodeEmpirically(ComparatorType rootPosition) {
		throw new RuntimeException("MEND or KILL");
//		PixelNode rootNode = null;
//		PixelNodeList rootNodes = getPossibleRootNodes(null);
//		Collections.sort(rootNodes.getList());
//		if (rootNodes.size() > 0) {
//			rootNode = rootNodes.get(0);
//		} else {
//			try {
//				rootNode = getRootNodeFromExtremeEdge(rootPosition);
//			} catch (RuntimeException e) {
//					throw(e);
//			}
//		}
//		return rootNode;
	}

	public ImageParameters getParameters() {
		return getIsland() == null ? getDefaultParameters() : getIsland().getParameters();
	}

	private PixelIsland getIsland() {
		if (island == null) {
			throw new RuntimeException("Island is required");
		}
		return island;
	}

	/** creates segmented lines from pixels adds them to edges and draws them.
	 *  
	 *  uses parameters to 
	 * @return
	 */
	public SVGG createSegmentedEdgesSVG() {
		SVGG g = new SVGG();
		for (PixelEdge edge: edgeList) {
			double segmentTolerance = getParameters().getSegmentTolerance();
			PixelSegmentList pixelSegmentList = edge.getOrCreateSegmentList(segmentTolerance);
			pixelSegmentList.setStroke(getParameters().getStroke());
			pixelSegmentList.setWidth(getParameters().getLineWidth());
			pixelSegmentList.setFill(getParameters().getFill());
			g.appendChild(pixelSegmentList.getOrCreateSVG());
		}
		return g;
	}

	public void createAndDrawGraph(SVGG g) {
		PixelEdgeList edgeList = getOrCreateEdgeList();
		PixelNodeList nodeList = getOrCreateNodeList();
		for (PixelNode node : nodeList) {	
			if (node == null) {
				throw new RuntimeException("null node in list size "+nodeList.size());
			}
			String color = node.getEdges().size() == 0 ? "red" : "green";
			SVGG nodeCircle = node.createSVG(2.0, color);
			nodeCircle.setOpacity(0.3);
			g.appendChild(nodeCircle);
		}
		if (nodeList.size() > 1) {
			for (PixelEdge edge : edgeList) {
				SVGG edgeLine = edge.createLineSVG();
				edgeLine.setFill("blue");
				edgeLine.setStroke("purple");
				edgeLine.setStrokeWidth(2.0);
				g.appendChild(edgeLine);
			}
		}
	}

	private PixelNode getNode(Pixel pixel) {
		for (PixelNode node : nodeList) {
			if (pixel.equals(node.getCentrePixel())) {
				return node;
			}
		}
		return null;
	}

	public PixelEdge createEdge(PixelNode node) {
		PixelEdge edge = null;
		if (node.hasMoreUnusedNeighbours()) {
			Pixel neighbour = node.getNextUnusedNeighbour();
			edge = createEdge(node, neighbour);
		}
		return edge;
	}

	public PixelEdge createEdge(PixelNode node, Pixel next) {
		PixelEdge edge = new PixelEdge(this);
		Pixel current = node.getCentrePixel();
		edge.addNode(node, 0);
		LOG.trace("start "+node);
		node.removeUnusedNeighbour(next);
		edge.addPixel(current);
		edge.addPixel(next);
		while (true) {
			PixelList neighbours = next.getOrCreateNeighbours(island);
			if (neighbours.size() != 2) {
				break;
			}
			Pixel next0 = neighbours.getOther(current);
			edge.addPixel(next0);
			current = next;
			next = next0;
			LOG.trace(current);
		}
		LOG.trace("end "+next);
		PixelNode node2 = getNode(next);
		if (node2 == null) {
			throw new RuntimeException("cannot find node for pixel: "+next);
		}
		node2.removeUnusedNeighbour(current);
		edge.addNode(node2, 1);
		return edge;
	}

	private Stack<PixelNode> createNodeStack() {
		createNodeList();
		nodeStack = new Stack<PixelNode>();
		for (PixelNode node : nodeList) {
			nodeStack.push(node);
		}
		return nodeStack;
	}

	private PixelNucleusFactory getOrCreateNucleusFactory() {
		if (nucleusFactory == null) {
			if (island == null) {
				throw new RuntimeException("Island must not be null");
			}
		}
		return island.getOrCreateNucleusFactory();
	}

	public PixelNucleusList getPixelNucleusList() {
		return getOrCreateNucleusFactory().getOrCreateNucleusList();
	}

	public PixelEdgeList getOrCreateEdgeList() {
		getOrCreateNodeList();
		if (edgeList == null) {
			if (island != null) {
				edgeList = getOrCreateNucleusFactory().getEdgeList();
			} else {
				edgeList = new PixelEdgeList();
			}
		}
		return edgeList;
	}

	public double getSegmentCreationTolerance() {
		return segmentCreationTolerance;
	}

	public void setSegmentCreationTolerance(double segmentCreationTolerance) {
		this.segmentCreationTolerance = segmentCreationTolerance;
	}

	public void numberAllNodes() {
		ensureNodeList();
		int i = 0;
		for (PixelNode node : nodeList) {
			node.setLabel("n" + (i++));
		}
	}

	public void addCoordsToNodes() {
		ensureNodeList();
		for (PixelNode node : nodeList) {
			Int2 coord = node.getInt2();
			if (coord != null) {
				String label = String.valueOf(coord).replaceAll("[\\(\\)]", "").replaceAll(",","_");
				node.setLabel(label);
			}
		}
	}

	private void ensureNodeList() {
		if (nodeList == null) {
			nodeList = new PixelNodeList();
		}
	}

	public void debug() {
		LOG.debug("graph...");
		for (PixelNode node : this.getOrCreateNodeList()) {
			LOG.debug("n> "+ node.toString());
			for (PixelEdge edge : node.getEdges()) {
				LOG.debug("  e: "+edge.getNodes());
			}
		}
	}
	
	public <T> PixelTree<T> getPixelTree() {
		//return getFirstTree(new ArrayList<PixelNode>());
		List<PixelTree<T>> tree = getPixelTrees(1);
		return tree.get(0);
	}
	
	public <T> List<PixelTree<T>> getPixelTrees() {
		return getPixelTrees(Integer.MAX_VALUE);
	}

	public <T> List<PixelTree<T>> getPixelTrees(int n) {
		getOrCreateEdgeList();
		List<PixelTree<T>> trees = new ArrayList<PixelTree<T>>();
		int i = 0;
		for (PixelNode node : getOrCreateNodeList()) {
			PixelTree<T> tree = new PixelTree<T>();
			if (node.getEdges().size() == 0) {
				if (node.getNucleus().getJunctionType() == PixelJunctionType.DOT) {
					tree.addEdgelessNode(node);
					trees.add(tree);
				}
				continue;
			}
			trees.add(tree);
			for (PixelEdge e : node.getEdges()) {
				addLineToTree(tree, e, node);
			}
			i++;
			if (i == n) {
				break;
			}
		}
		return trees;
		/*List<PixelNode> nodes = new ArrayList<PixelNode>();
		PixelTree<T> tree1 = getFirstTree(nodes);
		PixelTree<T> tree2 = new PixelTree<T>();
		for (int i = nodes.size() - 1; i >= 0; i--) {
			PixelNode node = nodes.get(i);
			for (PixelEdge e : node.getEdges()) {
				addLineToTree(tree2, e, node);
			}
		}
		List<PixelTree<T>> trees = new ArrayList<PixelTree<T>>();
		trees.add(tree1);
		trees.add(tree2);
		return trees;*/
		/*PixelNode firstNode;
		try {
			firstNode = getNucleusFactory().getOrCreateTerminalJunctionList().get(0).getNode();//getNodeList().get(0);
		} catch (IndexOutOfBoundsException e) {
			firstNode = getNucleusFactory().getOrCreateNucleusList().get(0).getNode();
		}
		PixelEdgeList edges = getEdgeList();
		Set<PixelEdge> setOfEdges = new HashSet<PixelEdge>(edges.getList());
		//Pixel firstSpike = getNucleusFactory().getOrCreateSpikePixelList().get(0);
		PixelTree<T> tree = new PixelTree<T>();
		addLineToTree(tree, setOfEdges.iterator().next(), firstNode, );
		while (setOfEdges.size() > 0) {
			addLineToTree(tree, setOfEdges.iterator().next(), firstNode, );
		}*/
	}

	//private <T> PixelTree<T> getFirstTree(List<PixelNode> nodes) {
		//getEdgeList();
		/*PixelTree<T> tree = new PixelTree<T>();
		for (PixelNucleus terminal : getNucleusFactory().getOrCreateTerminalJunctionList()) {
			if (terminal.getNode().getEdges().size() == 1) {
				addLineToTree(tree, terminal.getNode().getEdges().get(0), terminal.getNode());
				nodes.add(terminal.getNode());
			} else {
				System.out.println("Error: tricky nucleus");
			}
		}
		for (PixelNucleus other : getNucleusFactory().getOrCreateNucleusList()) {
			if (other instanceof DotNucleus) {
				tree.addEdgelessNode(other.getNode());
				//nodes.add(other.getNode());
			} else if (other.getNode().getEdges().size() > 0) {
				//nodes.add(other.getNode());
				for (PixelEdge e : other.getNode().getEdges()) {
					addLineToTree(tree, e, other.getNode());
				}
			} else {
				System.out.println("Error: tricky nucleus");
			}
		}
		return tree;*/
	//}

	private <T> void addLineToTree(PixelTree<T> tree, PixelEdge edge, PixelNode startNode) {
		if (tree.edgeEncountered(edge)) {
			return;
		}
		/*PixelNucleusFactory nucleusFactory = getNucleusFactory();
		PixelList line = nucleusFactory.findLine(nucleusFactory.getNucleusBySpikePixel(firstSpike), firstSpike);*/
		tree.addPixelsFromEdge(startNode, edge);
		PixelNode endNode = edge.getOtherNode(startNode);
		if (!tree.nodeEncountered(endNode)) {
			Set<PixelEdge> edges = new LinkedHashSet<PixelEdge>(endNode.getEdges().getList());
			for (PixelEdge e : edges) {
				if (!tree.edgeEncountered(e)) {
					addLineToTree(tree, e, endNode);
				}
			}
		}
		//nucleusFactory.getNucleusByPixel(line.get(line.size())).createSpikePixelList()
	}

	public void tidyNodesAndEdges(int largestSmallEdgeAllowed) {
		getOrCreateEdgeList();
		getOrCreateNodeList();
		tidyEdges(largestSmallEdgeAllowed);
		tidyNodes();
	}

	private void tidyEdges(int largestSmallEdgeAllowed) {
		List<PixelEdge> smallEdges = new ArrayList<PixelEdge>();
		for (PixelEdge edge : edgeList) {
			if (edge.getNodes().size() != 2) {
				LOG.trace("Edge with missing node or nodes: " + edge.getNodes().size());
			} else {
				PixelNode first = edge.getNodes().get(0);
				PixelNode last = edge.getNodes().get(1);
				if ((first.getEdges().size() == 1 || last.getEdges().size() == 1) && edge.size() <= largestSmallEdgeAllowed) {
					smallEdges.add(edge);
				}
			}
		}
		for (PixelEdge e : smallEdges) {
			removeEdge(e);
		}
	}

	private void tidyNodes() {
		LOG.trace("Nodes: " + nodeList.size());
		PixelNodeList copyList = new PixelNodeList(nodeList);
		for (PixelNode node : copyList) {
			remove2ConnectedNode(node);
			if (node.getEdges().size() == 0) {
				removeNode(node);
			}
		}
		LOG.trace("Nodes after: " + nodeList.size());
	}

	private void remove2ConnectedNode(PixelNode node) {
		if (node.getEdges().size() == 2) {
			PixelEdge edge0 = node.getEdges().get(0);
			PixelEdge edge1 = node.getEdges().get(1);
			if (edge0 != edge1) {
				PixelNode node0 = edge0.getOtherNode(node);
				PixelNode node1 = edge1.getOtherNode(node);
				LOG.trace("Others: " + node0 + ", " + node1);
				PixelEdge edge01 = createEdgeWithoutPixels(node0, node1);
				Pixel first0 = edge0.getFirst();
				Pixel first1 = edge1.getFirst();
				Pixel last0 = edge0.getLast();
				Pixel last1 = edge1.getLast();
				double dist1 = first0.getInt2().getEuclideanDistance(node.getInt2());
				double dist2 = last0.getInt2().getEuclideanDistance(node.getInt2());
				double dist3 = first1.getInt2().getEuclideanDistance(node.getInt2());
				double dist4 = last1.getInt2().getEuclideanDistance(node.getInt2());
				if (dist1 < dist2) {
					Collections.reverse(edge0.getPixelList().getList());
				}
				if (dist4 < dist3) {
					Collections.reverse(edge1.getPixelList().getList());
				}
				edge01.addPixelList(edge0.getPixelList());
				edge01.addPixelList(edge1.getPixelList());
				addEdge(edge01);
				removeEdge(edge0);
				removeEdge(edge1);
				removeNode(node);
				LOG.trace("Removed: " + node);
			}
		}
	}

	private PixelEdge createEdgeWithoutPixels(PixelNode node0, PixelNode node1) {
		PixelEdge pixelEdge = new PixelEdge(this);
		pixelEdge.addNode(node0, 0);
		pixelEdge.addNode(node1, 1);
		return pixelEdge;
		
	}

	/** analyzes graph as potentially a single cycle.
	 * this could be:
	 *  - a single cyclic edge with no node
	 *  - a single edge and node (ouroboros)
	 *  - two nodes with two edges 
	 *  - a chain of edges returning to the start
	 *  (The last 3 are really all the same).
	 *  
	 * @return true or false (initially set to null)
	 */
	public Boolean isSingleCycle() {
		if (isSingleCycle == null) {
			getOrCreateNodeList();
			isSingleCycle = false;
			if (edgeList == null) {
				LOG.trace("NULL EDGE");
			} else if (edgeList.size() == 1) {
				if (nodeList == null || nodeList.size() == 1) {
					// before creation of segments
					isSingleCycle = true;
				}
			} else if (edgeList.size() == nodeList.size()) {
				// all nodes must be 2-connected
				isSingleCycle = true;
				for (PixelNode node : nodeList) {
					if (node.getEdges().size() != 2) {
						isSingleCycle = false;
						break;
					}
				}
			}
		}
		return isSingleCycle;
	}

	/** used when the graph is a single cycle.
	 * ignore very small <=6 rings
	 * @param tolerance
	 */
	public void createCyclicSegments(double tolerance) {
		if (!isSingleCycle()) {
			return;
		}
		PixelEdge edge = edgeList.get(0);
		if (edge.size() <= MIN_CYCLE) return;// ignore small cycles
		
		PixelNode node = getOrCreateSingleCyclicNode(edge.get(0));
		int midPointIndex = edge.size() / 2;
		
		PixelList totalPixelList = edge.pixelList;
		Pixel midPixel = totalPixelList.get(midPointIndex);
		PixelNode midNode = new PixelNode(midPixel, this);
		this.addNode(midNode);
		
		PixelList pixelList0 = totalPixelList.getPixelsBackToStartInclusive(midPointIndex);
		PixelEdge edge0 = createEdge(node, midNode, pixelList0);
		this.addEdge(edge0);
		edge0.getOrCreateSegmentList(tolerance);
		
		PixelList pixelList1 = totalPixelList.getPixelsForwardToEndInclusive(midPointIndex);
		PixelEdge edge1 = createEdge(node, midNode, pixelList1);
		this.addEdge(edge1);
		edge1.getOrCreateSegmentList(tolerance);
		
		this.removeEdge(edge);

	}

	private PixelNode getOrCreateSingleCyclicNode(Pixel startPixel) {
		if (nodeList == null || nodeList.size() == 0) {
			PixelNode node = new PixelNode(startPixel, this);
			this.addNode(node);
		}
		return nodeList.get(0);
	}

	public void doEdgeSegmentation() {
		if (!hasBeenSegmented) {
			getOrCreateNodeList();
			PixelEdgeList edgeList = getOrCreateEdgeList();
			if (isSingleCycle()) {
				createCyclicSegments(segmentCreationTolerance);
			}
			edgeList.segmentAllEdges(segmentCreationTolerance);
			// REMOVE OLD NODES AND EDGES
			if (isSingleCycle() && edgeList.size() == 2) {
				merge2edgesIntoOuroborosEdge(edgeList.get(0), edgeList.get(1));
			}
			hasBeenSegmented  = true;
		}
	}

//	private List<PixelSegmentList> createNewEdgeSegmentList() {
//		List<PixelSegmentList> newSegmentListList = new ArrayList<PixelSegmentList>();
//		
//		PixelSegmentList segmentList0 = edgeList.get(0).getOrCreateSegmentList(segmentCreationTolerance);
//		PixelSegmentList segmentList1 = edgeList.get(1).getOrCreateSegmentList(segmentCreationTolerance);
//		
//		// note addition in reverse order
//		PixelSegmentList segmentList0 = new 
//		for (int i = segmentList0.size() - 1; i >= 0; i--) {
//			newSegmentList.add(segmentList0.get(i));
//		}
//		// normal order
//		for (int i = 0; i < segmentList1.size(); i++) {
//			newSegmentList.add(segmentList1.get(i));
//		}
//		return newSegmentList;
//	}

	private PixelEdge merge2edgesIntoOuroborosEdge(PixelEdge edge0, PixelEdge edge1 ) {
		PixelEdge newPixelEdge = new PixelEdge(this);
		/*
		 * create a new ourouboros edge with initially 2 nodes 
		 */
		PixelSegmentList segmentList0 = edge0.getOrCreateSegmentList(segmentCreationTolerance);
		
		// note addition in reverse order
		PixelSegmentList newSegmentList = new PixelSegmentList();
		for (int i = segmentList0.size() - 1; i >= 0; i--) {
			newSegmentList.add(segmentList0.get(i));
		}
		newPixelEdge.addPixelList(edge0.getPixelList());
		
		PixelSegmentList segmentList1 = edge1.getOrCreateSegmentList(segmentCreationTolerance);
		// normal order
		for (int i = 0; i < segmentList1.size(); i++) {
			newSegmentList.add(segmentList1.get(i));
		}
		edge1.getPixelList().reverse();
		newPixelEdge.addPixelList(edge1.getPixelList());
		newPixelEdge.setPixelSegmentList(newSegmentList);

		PixelNode newPixelNode = new PixelNode(newPixelEdge.get(0), this);
		addSameNodeToBothEndsOfEdge(newPixelEdge, newPixelNode);
		newPixelNode.addEdge(newPixelEdge);

		/** and replace all nodes and edges with a single new one
		 * add new ones to graph
		 */
		this.addEdge(newPixelEdge);
		this.addNode(newPixelNode);

		/** and now remove old ones from graph */
		this.removeNode(edgeList.get(0).getPixelNode(1));
		this.removeNode(edgeList.get(0).getPixelNode(0));
		this.removeEdge(edgeList.get(1));
		this.removeEdge(edgeList.get(0));
		return newPixelEdge;
	}

	private void addSameNodeToBothEndsOfEdge(PixelEdge newPixelEdge, PixelNode newPixelNode) {
		newPixelEdge.addNode(newPixelNode, 0);
		newPixelEdge.addNode(newPixelNode, 1);
	}

	private void addPixelsInOppositeDirections(PixelEdge edge0, PixelEdge edge1, PixelEdge newPixelEdge) {
		newPixelEdge.addPixelList(edge0.getPixelList());
		edge1.getPixelList().reverse();
		newPixelEdge.addPixelList(edge1.getPixelList());
	}

	public SVGG normalizeSVGElements() {
		getOrCreateNodeList();
		SVGG g = new SVGG();
		double maxMeanDeviation = MAX_MEAN_CIRCLE_DEVIATION;
		if (nodeList.size() == 1 && edgeList.size() == 1) {
			PixelSegmentList cyclicSegmentList = edgeList.get(0).getOrCreateSegmentList(segmentCreationTolerance);
			SVGCircle circle = cyclicSegmentList.createCircle(maxMeanDeviation);
			if (circle != null) {
				plotCircle(g, circle);
			}
		} else if (edgeList.size() == 1 && nodeList.size() == 2){
			PixelEdge edge = edgeList.get(0);
			edge.getOrCreateSegmentList(segmentCreationTolerance);
			SVGLine line = edge.createLine(maxMeanDeviation);
			if (line != null) {
				line.setCSSStyle("stroke:green;stroke-width:1.2;");
			} else {
				SVGPoly polyline = edge.createPolylineFromSegmentList();
				if (polyline != null) {
					line.setCSSStyle("blue;stroke-width:1.2;");					
				}
			}
			g.appendChild(line);
		} else {
			for (PixelEdge edge : edgeList) {
				SVGLine line = edge.createLine(maxMeanDeviation);
				line.setCSSStyle("stroke:blue;stroke-width:1.2;");
				g.appendChild(line);
			}
		}
		return g;
	}

	/** creates new Islands from crossing/overlaps
	 * 
	 * @return
	 */
	public PixelIslandList resolveCyclicCrossing() {
		PixelIslandList islandList = new PixelIslandList();
		Map<PixelNode,Multiset<PixelNode>> multipleNodeSetByNode = createMultipleNodeSetByNodeMap();
		return islandList;
	}

	private Map<PixelNode,Multiset<PixelNode>> createMultipleNodeSetByNodeMap() {
		Map<PixelNode,Multiset<PixelNode>> multipleNodeSetByNode =
				new HashMap<PixelNode,Multiset<PixelNode>>();
		for (int i = 0; i < nodeList.size(); i++) {
			PixelNode node = nodeList.get(i);
			PixelNodeList connectedNodes = node.getConnectedNodes();
			if (connectedNodes.contains(node)) {
				LOG.trace("ouroboros");
			}
			Multiset<PixelNode> multipleNodeSet = connectedNodes.getMultipleNodes();
			multipleNodeSetByNode.put(node, multipleNodeSet);
		}
		return multipleNodeSetByNode;
	}

	/** if multiply connected nodes are "close" condense into a single node.
	 *  1     2
	 *   $   $
	 *    3$4
	 *   $   $
	 *  5     6
	 *  
	 *  has two close nodes (3/4). After condensation the graph is
	 *  
	 *  1     2
	 *   $   $
	 *    $7$
	 *   $   $
	 *  5     6
	 *  
	 *  Note that 3 and 4 have been removed , 7 is new node and paths may share
	 *  pixels. 
	 *  
	 *  This is a mess, of course, and may be edited later.
	 *  
	 *  
	 */
	public void compactCloseNodes(int minLength) {
		PixelEdgeList shortEdgeList = getShortEdges(minLength);
		for (PixelEdge shortEdge : shortEdgeList) {
			PixelNode node0 = shortEdge.getPixelNode(0);
			PixelNode node1 = shortEdge.getPixelNode(1);
			// only analyse if both are branched nodes
			if (node0.getEdges().size() >= 2 && node1.getEdges().size() >= 2) {
				compactShortEdge(shortEdge);
			}
		}
	}

	private void compactShortEdge(PixelEdge shortEdge) {
		PixelList edgePixels = shortEdge.pixelList;
		
		int len = edgePixels.size();
		int midIndex = len / 2;
		Pixel middlePixel = edgePixels.get(midIndex);
		PixelNode newNode = new PixelNode(middlePixel, this);
		PixelList edgePixels0 = new PixelList(edgePixels);
		
		copyPixelsFromShortEdgeChangeNodes(shortEdge, middlePixel, newNode, edgePixels0, 0);
		copyPixelsFromShortEdgeChangeNodes(shortEdge, middlePixel, newNode, edgePixels0, 1);

		this.addNode(newNode);
	}

	private void copyPixelsFromShortEdgeChangeNodes(
			PixelEdge shortEdge, Pixel middlePixel, PixelNode newNode, PixelList edgePixels, int nodeIndex) {
		PixelNode nodeToReplace = shortEdge.getPixelNode(nodeIndex);
		Pixel pixelToReplace = nodeToReplace.getCentrePixel();
		int ipixel = edgePixels.indexOf(pixelToReplace);
		if (ipixel == -1) {
			throw new RuntimeException("node not in edge");
		}
		if (ipixel != 0) {
			// the pixel edge points FROM branch node TO new Node
//			LOG.debug("reverse");
			edgePixels.reverse();
		}
//		LOG.debug("edge: "+edgePixels);
		
		PixelList linkingPixels = edgePixels.getPixelsBefore(middlePixel);
		linkingPixels.remove(pixelToReplace);
//		LOG.debug("linking "+nodeIndex+" : "+linkingPixels);
		linkingPixels.reverse();
		
		addPixelsToLinkingEdgesAndReplaceNodes(shortEdge, middlePixel, nodeToReplace, linkingPixels, newNode);
		this.removeNode(nodeToReplace);
	}

	/** adds pixels in shortEdge to all the other edges from node and replaces old nodes with newNode
	 * 
	 * @param shortEdge
	 * @param middlePixel
	 * @param node
	 * @param linkingPixels
	 * @param newNode
	 */
	private void addPixelsToLinkingEdgesAndReplaceNodes(
			PixelEdge shortEdge, Pixel middlePixel, PixelNode node, PixelList linkingPixels, PixelNode newNode) {
		PixelEdgeList otherEdges = node.getEdges();
		for (PixelEdge otherEdge : otherEdges) {
			if (otherEdge == shortEdge) continue;
			int inode = otherEdge.indexOf(node);
			if (inode == -1) {
				throw new RuntimeException("could not find node");
			}
			otherEdge.replaceNode(newNode, inode);
			if (inode == 1) {
				otherEdge.pixelList.addAll(linkingPixels);
			} else {
				for (int i = 0; i < linkingPixels.size(); i++) {
					otherEdge.pixelList.add(0, linkingPixels.get(i));
				}
			}
		}
		this.removeEdge(shortEdge);
	}

	/** gets short edges (measured by pixel count.
	 * 
	 * @param minLength including nodes; must be >= 3
	 * @return
	 */
	public PixelEdgeList getShortEdges(int minLength) {
		minLength = Math.max(minLength, 3);
		getOrCreateEdgeList();
		PixelEdgeList pixelEdgeList = new PixelEdgeList();
		for (int i = 0; i < edgeList.size(); i++) {
			PixelEdge edge = edgeList.get(i);
			PixelList pixelList = edge.getPixelList();
			int length = pixelList.size();
			if (length == 1) {
				throw new RuntimeException("nodes overlap");
			}
			if (length == 2) {
				throw new RuntimeException("nodes ditrectly joined");
			}
			if (pixelList.size() <= minLength) {
				pixelEdgeList.add(edge);
			}
		}
		return pixelEdgeList;
	}


	
	private void plotCircle(SVGG g, SVGCircle circle) {
		g.appendChild(circle);
		circle.setCSSStyle("fill:none;stroke:red;stroke-width:0.8;");
	}

	/**sometimes the nodes don't correspond with the pixel lists, so add new pixels to lists
	 * this is presumably a bug from PixelNuclei which haven't been properly processed
	 * 
	 */
	public void tidyEdgePixelLists() {
		getOrCreateEdgeList();
		for (PixelEdge edge : edgeList) {
			edge.tidyPixelList();
		}
		
	}
	
	public void mergeNodesCloserThan(double d) {
		boolean omit1connected = false;
		mergeNodesCloserThan(d, omit1connected);
	}

	/** merge nodes closer than a given distance.
	 * nodes must be already connected by an edge
	 * option to omit nodes which are 1-connected 
	 * exploratory! 
	 * @param d 
	 * @param omit1connected if true do not use 1-connected nodea as this trims the branches
	 * 
	 */
	public void mergeNodesCloserThan(double d, boolean omit1connected) {
		getOrCreateEdgeList();
//		getOrCreateNodeList();
		boolean change = true;
		while (change) {
			change = false;
			for (int i = 0; i < this.edgeList.size(); i++) {
				PixelEdge edge = edgeList.get(i);
				if (edge.getLength() < d) {
					change = true;
					PixelNode node0 = edge.getPixelNode(0);
					PixelNode node1 = edge.getPixelNode(1);
					if (!omit1connected || 
							(node0.getEdges().size() > 1 && node1.getEdges().size() > 1)) {
						condenseEdgeAndRemoveOneNode(edge);
					}
				}
			}
		}
	}

	/** replace node1 by node0 and tidy nodeLists and edgeLists
	 * 
	 * @param edge
	 */
	public void condenseEdgeAndRemoveOneNode(PixelEdge edge) {
		PixelNode node0 = edge.getPixelNode(0);
		PixelNode node1 = edge.getPixelNode(1);
		// randomly choose a node to remove 
		Int2 newXY = node0.getInt2().getMidPoint(node1.getInt2());
		node0.setInt2(newXY);
		// remove node 1
		this.nodeList.remove(node1);
		// remove the edge
		this.edgeList.remove(edge);
		node0.removeEdge(edge);
		node1.removeEdge(edge);
		// move existing edges from node 1
		PixelEdgeList edges1 = node1.getEdges();
		for (PixelEdge edge1 : edges1) {
			if (edge1.equals(edge)) {
				throw new RuntimeException("failed to remove edge");
			} else {
				// check for triangles
				if (edge.getOtherNode(node0).equals(edge1.getOtherNode(node1))) {
					LOG.trace("removed triangle edge");
					this.edgeList.remove(edge1);
				} else {
					// move node0 to take role of node1
					edge1.replaceNode(node1, node0);
				}
			}
		}
	}

	private ImageParameters getDefaultParameters() {
		if (imageParameters == null) {
			imageParameters = new ImageParameters();
		}
		return imageParameters;
	}

	/** creates a new line using coordinates of edge.
	 * 
	 * @return
	 */
	public SVGLineList createLinesFromEdges() {
		SVGLineList lineList = new SVGLineList();
		for (PixelEdge edge : edgeList) {
			PixelNode node0 = edge.getPixelNode(0);
			Int2 xy0 = node0.getInt2();
			PixelNode node1 = edge.getPixelNode(1);
			Int2 xy1 = node1.getInt2();
			SVGLine line = new SVGLine(new Real2(xy0), new Real2(xy1));
			line.setStrokeWidth(1.0);
			lineList.add(line);
		}
		return lineList;
	}


}
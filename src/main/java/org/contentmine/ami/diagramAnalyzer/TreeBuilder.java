package org.contentmine.ami.diagramAnalyzer;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.image.pixel.Pixel;
import org.contentmine.image.pixel.PixelComparator.ComparatorType;
import org.contentmine.image.pixel.PixelEdge;
import org.contentmine.image.pixel.PixelEdgeList;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelNode;
import org.contentmine.image.pixel.PixelNodeList;

/** Builds org.contentmine.diagrams.Tree
 * 
 * @author pm286
 *
 */
public class TreeBuilder {

	private final static Logger LOG = LogManager.getLogger(TreeBuilder.class);
	
	private final static int OFFSET = 20;
	private PixelGraph graph;
	protected Set<PixelNode> usedNodes;
	private Set<PixelEdge> usedEdges;
	private ComparatorType comparatorType;
	private DiagramTree diagramTree;
	private PixelNode rootPixelNode;

	public TreeBuilder(PixelGraph graph, ComparatorType comparatorType) {
		this.setGraph(graph);
		this.setComparatorType(comparatorType);
	}

	void setComparatorType(ComparatorType comparatorType) {
		this.comparatorType = comparatorType;
	}
	
	private void setGraph(PixelGraph graph) {
		this.graph = graph;
	}
	
	public DiagramTree createFromGraph() {
		diagramTree = null;
		if (graph != null) {
			LOG.trace("ROOTORIG: "+rootPixelNode);
			if (rootPixelNode == null) {
				rootPixelNode = this.getPossibleRootNode();
			}
			if (rootPixelNode == null) {
				LOG.error("CANNOT FIND ROOT");
			} else {
				LOG.trace("ROOT: "+rootPixelNode);
				diagramTree = new DiagramTree();
				diagramTree.setGraph(graph);
				createTree(rootPixelNode);
				diagramTree.setRootPixelNode(rootPixelNode);
			}
		}
		LOG.trace(diagramTree.rootXMLNode.toXML());
		return diagramTree;
	}

	public void createTree(PixelNode rootPixelNode) {
		this.rootPixelNode = rootPixelNode;
		diagramTree.rootXMLNode = createXMLNode(rootPixelNode);
		LOG.trace(">root>"+rootPixelNode);
		usedEdges = new HashSet<PixelEdge>();
		usedNodes = new HashSet<PixelNode>();
		this.addNode(diagramTree.rootXMLNode, rootPixelNode);
		removeUnnecessaryRootChild();
	}

	public XMLNode createXMLNode(PixelNode pixelNode) {
		XMLNode xmlNode = null;
		if (pixelNode != null) {
			xmlNode = new XMLNode(diagramTree);
			xmlNode.setLabel(pixelNode.getLabel());
			xmlNode.setXY2(pixelNode.getReal2());
		}
		return xmlNode;
	}


	XMLNode addNode(XMLNode parentXMLNode, PixelNode pixelNode) {
		XMLNode xmlNode = null;
		if (usedNodes.contains(pixelNode)) {
			LOG.error("Probable cycle in graph: "+pixelNode);
//			throw new RuntimeException("Probable cycle in graph: "+pixelNode);
		} else {
			xmlNode = createAndAddXMLNode(parentXMLNode, pixelNode);
			PixelEdgeList edgeList = pixelNode.getEdges();
			addEdges(xmlNode, pixelNode, edgeList);
		}
		return xmlNode;
	}

	private XMLNode createAndAddXMLNode(XMLNode parentXmlNode, PixelNode pixelNode) {
		LOG.trace("adding node "+pixelNode);
		XMLNode xmlNode;
		usedNodes.add(pixelNode);
		xmlNode = createXMLNode(pixelNode);
		if (xmlNode != null) {
			parentXmlNode.addNode(xmlNode);
//			currentNode = xmlNode;
			
		} else {
			LOG.error("cannot create New XMLNode");
		}
		return xmlNode;
	}

	private void addEdges(XMLNode parentXMLNode, PixelNode pixelNode, PixelEdgeList edgeList) {
		for (PixelEdge edge : edgeList) {
			if (!usedEdges.contains(edge)) {
				usedEdges.add(edge);
				LOG.trace("adding edge "+edge.getNodes());
				PixelNode nextNode = edge.getOtherNode(pixelNode);
				if (nextNode == null) {
					LOG.error("Cannot find other node in edge: "+edge+"; "+pixelNode);
				} else {
					addNode(parentXMLNode, nextNode);
				}
			}
		}
	}

	// is this used?
	private XMLNode createXMLTree() {
		graph.numberTerminalNodes();
//		graph.debug();
		usedNodes = new HashSet<PixelNode>();
		diagramTree.rootXMLNode = diagramTree.createXMLNode();
		processNodeAndDescendantsXML(rootPixelNode, null, diagramTree.rootXMLNode);
		return diagramTree.rootXMLNode;
	}

	/** really messy , unnecessary and needs tidying.
	 * 
	 */
	private void removeUnnecessaryRootChild() {
		if (diagramTree.rootXMLNode.getChildElements().size() == 1) {
			XMLNode child = (XMLNode) diagramTree.rootXMLNode.getChildElements().get(0);
			child.detach();
			diagramTree.rootXMLNode = child;
		}
	}
	
	private void processNodeAndDescendantsXML(PixelNode pixelNode, PixelNode parent, XMLNode parentElement) {
		parentElement.setXY2(pixelNode.getReal2());
		usedNodes.add(pixelNode);
		PixelEdgeList edges = pixelNode.getEdges();
		PixelNodeList descendants = new PixelNodeList();
		for (PixelEdge edge : edges) {
			PixelNode other = edge.getOtherNode(pixelNode);
			if (other != null && !other.equals(parent) && !usedNodes.contains(other)) {
				usedNodes.add(other);
				descendants.add(other);
			}
		}
		for (int i = 0; i < descendants.size(); i++) {
			XMLNode newElement = new XMLNode(diagramTree);
			parentElement.appendChild(newElement);
			String label = descendants.get(i).getLabel();
			if (label != null) {
				newElement.setLabel(label);
			}
			processNodeAndDescendantsXML(descendants.get(i), pixelNode, newElement);
		}
	}

	public DiagramTree createTreeWithUnbranchedRoot() {
		DiagramTree tree = new DiagramTree();
		tree.rootXMLNode = new XMLNode(tree);
		tree.rootXMLNode.setLabel(DiagramTree._ROOT);
		return tree;
	}

	
	public PixelNode getPossibleRootNode() {
		comparatorType = (comparatorType == null) ? ComparatorType.LEFT : comparatorType;
		graph.getOrCreateEdgeList();
		PixelNode newNode = getOrConstructRootNodeFromExtrema();
		return newNode;
	}

	private Int2 createOffset() {
		Int2 offset = null;
		if (ComparatorType.LEFT.equals(comparatorType)) {
			offset = new Int2(-OFFSET, 0);
		} else if (ComparatorType.RIGHT.equals(comparatorType)) {
			offset = new Int2(OFFSET, 0);
		} else if (ComparatorType.TOP.equals(comparatorType)) {
			offset = new Int2(0, -OFFSET);
		} else if (ComparatorType.BOTTOM.equals(comparatorType)) {
			offset = new Int2(0, OFFSET);
		}
		return offset;
	}

	private PixelNode getOrConstructRootNodeFromExtrema() {
		rootPixelNode = null;
		graph.getOrCreateNodeList().sort(comparatorType);
		rootPixelNode = graph.getOrCreateNodeList().get(0);
		if (rootPixelNode.getEdges().size() == 1) {
			LOG.trace("found single existing root node "+rootPixelNode);
		} else {
			rootPixelNode.getEdges().sort(comparatorType);
			if (rootPixelNode.getEdges().size() > 0) {
				addNewBranchingChildOfRootNode();
			}

		}
		return rootPixelNode;
	}

	private void addNewBranchingChildOfRootNode() {
		PixelEdge extremeEdge = rootPixelNode.getEdges().get(0);
		Int2Range extremeBox = extremeEdge.getInt2BoundingBox();
		Int2 extremeMidPoint = getExtremeMidPoint(extremeBox);
		Pixel midPixel = extremeEdge.getClosestPixel(new Real2(extremeMidPoint));
		PixelNode newMidNode = graph.splitEdgeAndInsertNewNode(extremeEdge, midPixel);
		LOG.trace("created new mid-point node "+newMidNode);
		Int2 offset = createOffset();
		offset = new Int2(0,0); // Horrible kludge
		Int2 pixelCoord = offset.plus(newMidNode.getInt2());
		rootPixelNode = new PixelNode(new Pixel(pixelCoord.getX(), pixelCoord.getY()), graph);
		LOG.trace("created new root node "+rootPixelNode);
		PixelEdge edge = new PixelEdge(graph);
		edge.addNode(rootPixelNode, 0);
		edge.addNode(newMidNode, 1);
	}

	private void addNewBranchingChildOfRootNodeNew() {
		PixelEdge extremeEdge = rootPixelNode.getEdges().get(0);
		Int2Range extremeBox = extremeEdge.getInt2BoundingBox();
		Int2 extremeMidPoint = getExtremeMidPoint(extremeBox);
		Pixel midPixel = extremeEdge.getClosestPixel(new Real2(extremeMidPoint));
		LOG.trace("nodes "+graph.getOrCreateNodeList().size());
		rootPixelNode = graph.splitEdgeAndInsertNewNode(extremeEdge, midPixel);
		LOG.trace("nodes "+graph.getOrCreateNodeList().size());
		LOG.trace("created new root node "+rootPixelNode+"; "+rootPixelNode.getEdges().get(0));
		LOG.trace("created new root node "+rootPixelNode+"; "+rootPixelNode.getEdges().get(1));
//		Int2 offset = createOffset();
//		Int2 pixelCoord = offset.plus(newMidNode.getInt2());
//		rootPixelNode = new PixelNode(new Pixel(pixelCoord.getX(), pixelCoord.getY()), graph);
//		LOG.debug("created new root node "+rootPixelNode);
//		PixelEdge edge = new PixelEdge(graph);
//		edge.addNode(rootPixelNode, 0);
//		edge.addNode(newMidNode, 1);
	}

	public Int2 getExtremeMidPoint(Int2Range extremeBox) {
		Int2 midPoint = new Int2();
		IntRange xRange = extremeBox.getXRange();
		IntRange yRange = extremeBox.getYRange();
		if (comparatorType == null) {
			midPoint = null;
		} else if (comparatorType.equals(ComparatorType.LEFT)) {
			midPoint.setX(xRange.getMin());
			midPoint.setY(yRange.getMidPoint());
		} else if (comparatorType.equals(ComparatorType.RIGHT)) {
			midPoint.setX(xRange.getMax());
			midPoint.setY(yRange.getMidPoint());
		} else if (comparatorType.equals(ComparatorType.TOP)) {
			midPoint.setY(yRange.getMin());
			midPoint.setX(xRange.getMidPoint());
		} else if (comparatorType.equals(ComparatorType.BOTTOM)) {
			midPoint.setY(yRange.getMax());
			midPoint.setX(xRange.getMidPoint());
		} else {
			midPoint = null;
		}
		return midPoint;
	}

	public DiagramTree getTree() {
		return diagramTree;
	}

	public void setRootPixelNode(PixelNode rootPixelNode) {
		this.rootPixelNode = rootPixelNode;
	}

	

}

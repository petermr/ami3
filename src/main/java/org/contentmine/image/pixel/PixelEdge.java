package org.contentmine.image.pixel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPoly;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.image.geom.DouglasPeucker;

public class PixelEdge {

	private final static Logger LOG = Logger.getLogger(PixelEdge.class);
	
	static Pattern EDGE_PATTERN = Pattern.compile("\\{([^\\}]*)\\}\\/\\[([^\\]]*)\\]");

	PixelNodeList nodeList;
	PixelList pixelList; // pixels in order
	private PixelIsland island;
	private PixelSegmentList segmentList;
	private String id;
	private PixelGraph pixelGraph;
	private SVGG svgg;
	private Int2Range boundingBox;
	private Boolean cyclic;

	PixelEdge() {
		
	}
	
	public PixelEdge(PixelIsland island) {
		this();
		this.island = island;
		this.pixelList = new PixelList();
		this.nodeList = new PixelNodeList();
	}

	public PixelEdge(PixelGraph pixelGraph) {
		this();
		this.pixelGraph = pixelGraph;
	}
	
	/** copy constructor.
	 * deep copies edge and node lists.
	 * does not copy id, segments, svgg
	 * 
	 * @param edge
	 */
	public PixelEdge(PixelEdge edge) {
		this();
		this.pixelList = new PixelList(edge.pixelList);
		this.nodeList = new PixelNodeList(edge.nodeList);
		this.island = edge.island;
		this.pixelGraph = edge.pixelGraph;
	}

	/** adds node and pixel contained within it.
	 * 
	 * @param node
	 * @param pos 0 or 1
	 */
	public void addNode(PixelNode node, int pos) {
		ensureNodes();
		if (this.nodeList.size() != pos) {
			LOG.trace("Cannot add node");
		} else if (node == null) {
			LOG.trace("Cannot add null node");
		} else {
			nodeList.add(node);
			node.addEdge(this);
			LOG.trace("size "+nodeList.size());
		}
	}
	
	private void ensureNodes() {
		if (nodeList == null) {
			nodeList = new PixelNodeList();
		}
	}

	public void addPixel(Pixel pixel) {
		ensurePixelList();
		pixelList.add(pixel);
	}
	
	private void ensurePixelList() {
		if (pixelList == null) {
			pixelList = new PixelList();
		}
	}

	public void addPixelList(PixelList pixelList) {
		ensurePixelList();
		this.pixelList.addAll(pixelList);
	}
	
	public PixelList getPixelList() {
		return pixelList;
	}
	
	/** gets pixelNodes at end of edge.
	 * 
	 * normally 2; but for single cycles there are no nodes.
	 * 
	 * @return
	 */
	public PixelNodeList getNodes() {
		return nodeList;
	}
	
	/** gets pixel from list.
	 * 
	 * @param i
	 * @return null if no list or i is outside range
	 */
	public Pixel get(int i) {
		return (pixelList == null || size() == 0 || i < 0 || i >= size()) ? null : pixelList.get(i);
	}
	
	public Pixel getFirst() {
		return get(0);
	}

	public Pixel getLast() {
		return get(size() - 1);
	}
	
	public int size() {
		return (pixelList == null) ? 0 : pixelList.size();
	}

	public PixelNode getPixelNode(int i) {
		return (nodeList == null || i < 0 || i >= nodeList.size()) ? null : nodeList.get(i);
	}
	
	public void removeNodes() {
		while (nodeList != null && nodeList.size() > 0) {
			nodeList.remove(0);
		}
	}
	
	public String toString() {
		String s = "pixelList: "+pixelList+"; nodeList: "+nodeList;
		return s;
	}

	public boolean equalsIgnoreOrder(String listString) {
		boolean equals = pixelList.toString().equals(listString);
		if (!equals) {
			PixelList newList = new PixelList(pixelList);
			newList.reverse();
			equals = newList.toString().equals(listString);
		}
		return equals;
	}

	/** assume segmentList has already been calculated.
	 * use getOrCreateSegmentList if new lists wanted.
	 * 
	 * @return list or null
	 */
	public PixelSegmentList getExistingSegmentList() {
		return segmentList;
	}


	public PixelSegmentList getOrCreateSegmentList(double tolerance) {
		if (segmentList == null) {
			segmentList = getOrCreateSegmentList(tolerance, null, null, null, null);
		}
		return segmentList;
	}

	/** think this is what it was before Andy changed the signature.
	 * 
	 * @param tolerance
	 * @param cornerFindingWindow
	 * @param relativeCornernessThresholdForCornerAggregation
	 * @return
	 */
	public PixelSegmentList getOrCreateSegmentList(double tolerance, Integer cornerFindingWindow, 
			Double relativeCornernessThresholdForCornerAggregation) {
		if (segmentList == null) {
			segmentList = getOrCreateSegmentList(tolerance, cornerFindingWindow, relativeCornernessThresholdForCornerAggregation, null, null);
		}
		return segmentList;
	}

	public PixelSegmentList getOrCreateSegmentList(double tolerance, Integer cornerFindingWindow, Double relativeCornernessThresholdForCornerAggregation, Double allowedDifferenceCornerMaximumDeviating, Integer maxNumberCornersToSearch) {
		if (segmentList == null) {
			boolean improvedDouglasPeucker = cornerFindingWindow != null && relativeCornernessThresholdForCornerAggregation != null && allowedDifferenceCornerMaximumDeviating != null && maxNumberCornersToSearch != null;
			DouglasPeucker douglasPeucker = (improvedDouglasPeucker ? new DouglasPeucker(tolerance, cornerFindingWindow, relativeCornernessThresholdForCornerAggregation, allowedDifferenceCornerMaximumDeviating, maxNumberCornersToSearch) : new DouglasPeucker(tolerance));
			Real2Array points = pixelList.getOrCreateReal2Array();
			Real2Array pointArray = null;
			if (nodeList == null) {
				throw new RuntimeException("Segmentation requires nodes");
			} else if (nodeList.size() == 2 || true) {
				pointArray = douglasPeucker.reduceToArray(points);
			} else if (isCyclic()) {
				Real2 point0 = pointArray.get(0);
				pointArray.setElement(pointArray.size() - 1, new Real2(point0));
			}
			LOG.trace(pointArray);
			segmentList = new PixelSegmentList(pointArray);
		}
		return segmentList;
	}

	public PixelNode getOtherNode(PixelNode pixelNode) {
		if (nodeList.size() != 2) {
			return null;
		} else if (nodeList.get(0).equals(pixelNode)) {
			return nodeList.get(1);
		} else if (nodeList.get(1).equals(pixelNode)) {
			return nodeList.get(0);
		} else {
			return null;
		}
	}

	public Pixel getNearestPixelToMidPoint(Real2 midPoint) {
		Pixel midPixel = null;
		double distMin = Double.MAX_VALUE;
		for (Pixel pixel :pixelList) {
			if (midPixel == null) {
				midPixel = pixel;
			} else {
				Real2 xy = new Real2(pixel.getInt2());
				double dist = midPoint.getDistance(xy);
				if (dist < distMin) {
					distMin = dist;
					midPixel = pixel;
				}
			}
		}
		return midPixel;
	}

	public SVGG createPixelSVG(String colour) {
		SVGG g = new SVGG();
		for (Pixel pixel : pixelList) {
			SVGRect rect = pixel.getSVGRect(1, colour);
			g.appendChild(rect);
		}
		return g;
	}

	/** some edges are zero or one pixels and return to same node.
	 * 
	 * (Algorithm needs mending)
	 * 
	 * examples:
	 * {(19,48)}/[(19,48), (19,48)]
	 * {(22,36)}/[(23,36), (23,36)]
	 * {(29,30)}/[(29,31), (29,31)]
	 * {(29,29)}/[(29,31), (29,31)]
	 * 
	 * 
	 * @return
	 */
	public boolean isZeroCircular() {
		boolean circular = false;
		if (nodeList.size() == 0) {
			circular = pixelList.size() <= 1;
		} else if (nodeList.size() == 2) {
			circular = pixelList.size() <= 1;
		}
		return circular;
	}

	public SVGG createLineSVG() {
		SVGG g = new SVGG();
		if (getFirst() != null && getLast() != null) {
			Real2 firstXY = new Real2(getFirst().getInt2()).plus(new Real2(0.5, 0.5));
			Real2 lastXY = new Real2(getLast().getInt2()).plus(new Real2(0.5, 0.5));
			SVGLine line = new SVGLine(firstXY, lastXY);
			line.setWidth(0.5);
			g.appendChild(line);
		}
		return g;
	}

	public Real2 getMidPoint() {
		Pixel first = getFirst();
		Real2 firstXY = first == null ? null : new Real2(first.getInt2());
		Pixel last = getLast();
		Real2 lastXY = last == null ? null : new Real2(last.getInt2());
		Real2 mid = (lastXY == null || firstXY == null) ? null : firstXY.getMidPoint(lastXY);
		return mid;
	}

	public Pixel getNearestPixelToMidPoint() {
		Real2 midPoint = getMidPoint();
		return midPoint == null ? null : getNearestPixelToMidPoint(midPoint);
		
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public SVGG getOrCreateSVG() {
		if (svgg == null) {
			svgg = new SVGG();
			svgg.appendChild(pixelList.plotPixels("blue"));
		}
		return svgg;
	}
	
	public Int2Range getInt2BoundingBox() {
		if (boundingBox == null) {
			boundingBox = pixelList == null ? null : pixelList.getIntBoundingBox();
		}
		return boundingBox;
	}

	public Pixel getClosestPixel(Real2 point) {
		return (pixelList == null || pixelList.size() == 0) ? null : pixelList.getClosestPixel(point);
	}

	public boolean removeNode(PixelNode node) {
		return nodeList.remove(node);
	}

	public SVGPoly createPolylineFromSegmentList() {
		SVGPolyline polyline = null;
		if (segmentList != null) {
			polyline = new SVGPolyline();
			for (int i = 0; i < segmentList.size(); i++) {
				PixelSegment pixelSegment = segmentList.get(i);
				polyline.add(pixelSegment.getSVGLine());
			}
			polyline.setReal2Array(polyline.getReal2Array());
		}
		return polyline;
	}
	
	SVGLine createLine(double maxMeanDeviation) {
		SVGLine line = new SVGLine();
		PixelSegmentList segmentList = getOrCreateSegmentList(maxMeanDeviation);
//		LOG.debug("segmentList: "+segmentList);
		line = new SVGLine(segmentList.get(0).getPoint(0), segmentList.getLast().getPoint(1));
		return line;
	}

	public void setPixelSegmentList(PixelSegmentList newSegmentList) {
		this.segmentList = newSegmentList;
	}

	/** gets index of node.
	 * 
	 * uses identity (==) not equals()
	 * 
	 * @param node search node
	 * @return 0 or 1 if found else -1
	 */
	public int indexOf(PixelNode node) {
		return nodeList.get(0) == node ? 0 : (nodeList.get(1) == node ? 1 : -1);
	}

	public void replaceNode(PixelNode newNode, int inode) {
		nodeList.set(inode, newNode);
		newNode.addEdge(this);
	}

	/** sometimes the node is not actually in the pixel list, so add it and intervening pixels.
	 * 
	 */
	public void tidyPixelList() {
		tidyPixelList(nodeList.get(0).getCentrePixel());
		tidyPixelList(nodeList.get(1).getCentrePixel());
	}

	private void tidyPixelList(Pixel nodePixel) {
		Pixel pixel0 = pixelList.get(0);
		Pixel pixel1 = pixelList.getLast();
		if (pixel0 == nodePixel) {
			// found 0
		} else if (pixel1 == nodePixel) {
				// found last
		} else {
//			LOG.debug("Cannot find pixel: " + nodePixel + " in: " + pixelList);
			Int2 delta0 = pixel0.subtract(nodePixel);
			Int2 delta1 = pixel1.getInt2().subtract(nodePixel.getInt2());
			if (delta0.getManhattanDistance(Int2.ZERO) < 3) {
				addPixels(nodePixel, pixel0);
			} else if (delta1.getManhattanDistance(Int2.ZERO) < 3) {
				addPixels(nodePixel, pixel1);
			}
//			LOG.debug(">"+delta0+" // "+delta1);
		}
	}

	private void addPixels(Pixel nodePixel, Pixel edgePixel) {
		Int2 nodeXY = nodePixel.getInt2();
		Int2 edgeXY = edgePixel.getInt2();
		Int2 delta = nodeXY.subtract(edgeXY);
		while (!delta.equals(Int2.ZERO)) {
			Int2 delta1 = new Int2(delta);
			delta1.stepToZero();
			Int2 shift = delta.subtract(delta1);
			edgeXY = edgeXY.plus(shift);
			Pixel newPixel = new Pixel(edgeXY);
			this.addPixel(newPixel);
			delta = nodeXY.subtract(edgeXY);
		}
	}

	/** creates a (segmented) set of points at regular positions.
	 * 
	 * npoints must be >= 2 (normally <= size / 2, allowing for segments of 2 or more pixels)
	 * 
	 * 
	 * NOT TESTED
	 * @param npoints >=2 ideally less than size() / 2
	 * @return
	 */
	@Deprecated
	public PixelEdge createSegmentedEdge(int nsegments) {
		// copy the pixelEdge
		PixelEdge subEdge = new PixelEdge(this);
		PixelList segmentedPixelList = subEdge.createSegmentedPixelList(nsegments, this);
		subEdge.segmentList = new PixelSegmentList();
		int size = segmentedPixelList.size();
		for (int i = 0; i < size - 1; i++) {
			Int2 int20 = segmentedPixelList.get(i).getInt2();
			Int2 int21 = segmentedPixelList.get(i + 1).getInt2();
			PixelSegment segment = new PixelSegment(new Real2(int20), new Real2(int21));
			subEdge.segmentList.add(segment);
		}
		return subEdge;
	}

	public PixelEdge join(PixelEdge edge, PixelNode thisNode, PixelNode edgeNode) {
		PixelEdge newPixelEdge = null;
		if (thisNode.getCentrePixel().equals(edgeNode.getCentrePixel())) {
			newPixelEdge = new PixelEdge();
			PixelNode thisOther = this.getOtherNode(thisNode);
			newPixelEdge.addNode(thisOther, 0);
			PixelList newEdgePixelList0 = new PixelList(this.pixelList);
			newPixelEdge.addPixelList(newEdgePixelList0);
			PixelList newEdgePixelList1 = new PixelList(edge.pixelList);
			newEdgePixelList1.remove(0);
			newPixelEdge.addPixelList(newEdgePixelList1);
			PixelNode edgeOther = edge.getOtherNode(edgeNode);
			newPixelEdge.addNode(edgeOther, 1);
		}
		return newPixelEdge;
	}

	/** join a list of edges to give a single edge.
	 * order is irrelevant.
	 * if all nodes in edges (B..D, C..F, D..A, etc.), except 2 (A..M , N..Z)  belong to exactly 2 edges 
	 * join to give A..Z
	 * If any edge is ouroboros return null
	 *
	 * 
	 * 
	 * @param edges
	 * @return
	 */
	public static PixelEdge join(PixelEdge... edges) {
		PixelEdgeList edgeList = createPixelEdgeList(edges);
		List<PixelEdge> singleEndedEdgeList = PixelEdge.getSingleEndedEdgeList(edgeList);
		if (singleEndedEdgeList == null) {
			return null;
		}
		PixelEdge growingEdge = singleEndedEdgeList.get(0);
		IntArray singleNodeIndexes = growingEdge.getSinglyConnectedNodeIndexes();
		if (singleNodeIndexes.size() == 2) {
			if (edgeList.size() > 1) {
				throw new RuntimeException("OUROBOROS edge");
			} else {
				growingEdge = growingEdge.createOuroboros();
				return growingEdge;
			}
		}
		return growingEdge;
//		return edgeList;
	}
//		List<PixelEdge> singleEndedEdgeList = new ArrayList<PixelEdge>();
//		if (thisNode.getCentrePixel().equals(edgeNode.getCentrePixel())) {
//			LOG.debug("JOIN "+this.size()+"; "+edge.size());
//			newPixelEdge = new PixelEdge();
//			PixelNode thisOther = this.getOtherNode(thisNode);
//			newPixelEdge.addNode(thisOther, 0);
//			PixelList newEdgePixelList0 = new PixelList(this.pixelList);
//			newPixelEdge.addPixelList(newEdgePixelList0);
//			PixelList newEdgePixelList1 = new PixelList(edge.pixelList);
//			LOG.debug("newEdgePixelList1 "+newEdgePixelList1.size());
//			newEdgePixelList1.remove(0);
//			newPixelEdge.addPixelList(newEdgePixelList1);
//			LOG.debug(newPixelEdge.size());
//			PixelNode edgeOther = edge.getOtherNode(edgeNode);
//			newPixelEdge.addNode(edgeOther, 1);
//		}
//		return null;
//	}

	private PixelEdge createOuroboros() {
		throw new RuntimeException("Ouroboros not yet supported");
	}

	public IntArray getSinglyConnectedNodeIndexes() {
		IntArray singlyConnectedIndexes = new IntArray();
		for (int i = 0; i < 2; i++) {
			int nodeCount = this.getEdgesForNode(i).size();
			if (nodeCount == 1) {
				singlyConnectedIndexes.addElement(i);
			}
		}
		return singlyConnectedIndexes;
	}

	private static PixelEdgeList createPixelEdgeList(PixelEdge... edges) {
		PixelEdgeList edgeList = new PixelEdgeList();
		for (PixelEdge edge : edges) {
			edgeList.add(edge);
		}
		return edgeList;
	}

	/** gets edges from given node.
	 * 
	 * @param nodeIndex
	 * @return
	 */
	public PixelEdgeList getEdgesForNode(int nodeIndex) {
		PixelEdgeList edgeList = new PixelEdgeList();
		PixelNodeList nodeList = getNodes();
		if (nodeList == null || nodeList.size() - 1 < nodeIndex) {
			LOG.trace("Too few edges "+nodeList.size()+" for index: "+nodeIndex);
			return edgeList;
		}
		return nodeList.get(nodeIndex).getEdges();
	}

	/** gets list of exactly 2 edges with single nodes.
	 * any errors returns null
	 * 
	 * @param edgeList list of edges
	 * @return
	 */
	private static List<PixelEdge> getSingleEndedEdgeList(PixelEdgeList edgeList) {
		List<PixelEdge> singleEndedEdgeList = new ArrayList<PixelEdge>();
		if (edgeList.size() == 1) {
			LOG.error("only one edge: cannot join");
			return null;
		}
		int totalSingleNodeCount = 0;
		for (PixelEdge edge : edgeList) {
			int singleNodeCount = 0;
			if (edge.getEdgesForNode(0).size() == 1) {
				singleNodeCount++;
			}
			if (edge.getEdgesForNode(1).size() == 1) {
				singleNodeCount++;
			}
			if (singleNodeCount == 2) {
				LOG.debug("OUROBOROS edge");
				return null;
			}
			totalSingleNodeCount += singleNodeCount;
			if (totalSingleNodeCount == 1) {
				singleEndedEdgeList.add(edge);
			}
		}
		if (totalSingleNodeCount != 2) {
			LOG.error("need exactly 2 singleNode edges: found: "+totalSingleNodeCount);
			return null;
		}
		return singleEndedEdgeList;
	}

	public void reverse() {
		nodeList.reverse();
		pixelList.reverse();
	}

	/**
	 * 
	 * @param node to match
	 * @return -1 if not found
	 */
	public int matchByCoordinate(PixelNode node) {
		if (node == null) {
			return -1;
		}
		int nodeIndex = nodeList.indexOf(node.getCentrePixel());
		return nodeIndex;
	}

	/** join two edges.
	 * assumes that joining nod on "this" is node 1.
	 * 
	 * @param edge to join
	 * @return
	 */
	public PixelEdge join(PixelEdge edge) {
		PixelNode node1 = this.getPixelNode(1);
		int nodeIndex = edge.matchByCoordinate(node1);
		if (nodeIndex == -1) {
			throw new RuntimeException("Cannot find joining node");
		}
		if (nodeIndex == 1) {
			edge.reverse();
		}
		PixelEdge newEdge = join(edge, node1, node1);
		return newEdge;
	}

	/** turn edge into a cycle with single node.
	 * for this to work has to have two singly connected nodes with identical coordinates
	 * @return null if impossible
	 */
	public PixelEdge cyclise() {
		PixelEdge newEdge = null;
		PixelNode node0 = this.getPixelNode(0);
		PixelNode node1 = this.getPixelNode(1);
		if (node0.getInt2().equals(node1.getInt2())) {
			newEdge = new PixelEdge(this);
			newEdge.removeNode(node1);
			newEdge.pixelList.remove(newEdge.pixelList.size() -1);
			newEdge.cyclic = true;
		}
		return newEdge;
	}

	/** use stored value or compute 
	 * cyclic if one node and pixel0 is joined to last pixel.
	 * will not force cyclize() 
	 * 
	 * @return not null
	 */
	public Boolean isCyclic() {
		if (cyclic == null) {
			cyclic = false;
			if (nodeList.size() == 1 && size() >= 3 && pixelList.get(0).isNeighbour(pixelList.last())) {
				cyclic = true;
			} 
		}
		return cyclic;
	}

	public SVGLine getLine() {
		SVGLine line = null;
		PixelNodeList nodeList = this.getNodes();
		if (nodeList.size() == 2) {
			Real2 xy0 = nodeList.get(0).getReal2();
			Real2 xy1 = nodeList.get(1).getReal2();
			line = new SVGLine(xy0, xy1);
		}
		return line;
	}

	public double getLength() {
		PixelNodeList nodeList = this.getNodes();
		Real2 xy0 = new Real2(getFirst().getInt2());
		Real2 xy1 = new Real2(getLast().getInt2());
		return xy0.getDistance(xy1);
	}

	/** replace node 
	 * 
	 * @param node0 to replace
	 * @param node1 replacement
	 * @return true if successful
	 */
	public boolean replaceNode(PixelNode node0, PixelNode node1) {
		int index0 = this.indexOf(node0);
		boolean changed = false;
		if (index0 != -1) {
			nodeList.set(index0, node1);
			changed = true;
		}
		return changed;
	}

	public PixelSegmentList getPixelSegmentList() {
		return segmentList;
	}

	/** creates a new sub PixelList at coarser frequency
	 * 
	 * delta = size / nsegments
	 *  at 0, delta*1 , delta*2 ... size 
	 * 
	 * @param nsegments to split list into 
	 * @param pixelEdge
	 * @return
	 */
	public PixelList createSegmentedPixelList(int nsegments, PixelEdge pixelEdge) {
		PixelList segmentedPixelList = new PixelList();
		IntArray integers = IntArray.naturalNumbers(pixelEdge.size() - 1);
		IntArray segmentIndexes = integers.createSegmentedArray(nsegments);
		for (int i = 0; i < segmentIndexes.size(); i++) {
			int idx = segmentIndexes.elementAt(i);
			Pixel pixel = pixelEdge.get(idx);
//			LOG.debug(pixel);
			segmentedPixelList.add(pixel);
		}
		return segmentedPixelList;
	}
	

}

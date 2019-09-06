package org.contentmine.image.pixel;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;

public class PixelNode implements Comparable<PixelNode> {

	private final static Logger LOG = Logger.getLogger(PixelNode.class);

	private static final String START_STRING = "<";
	private static final String END_STRING = ">";

	private static final double RADIUS = 3.;
	private static final String STROKE = "blue";
	private static final double STROKE_WIDTH = 1.0;
	private static final double OPACITY = 0.4;
	
	private double radius = RADIUS;
	private String stroke = STROKE;
	private double strokeWidth = STROKE_WIDTH;
	private double opacity = OPACITY;
	
	Pixel centrePixel; // pixel 1
	private PixelEdgeList edgeList;
	private String label;
	private String id;
	private PixelSet unusedNeighbours;
	private PixelIsland island;
	private PixelNucleus pixelNucleus;
	private PixelGraph pixelGraph; // is this used?

	private SVGG svgg;

	protected PixelNode() {
	}
	
	public PixelNode(Pixel pixel, PixelGraph pixelGraph) {
		this.centrePixel = pixel;
		this.pixelGraph = pixelGraph;
	}
	
	public PixelNode(Pixel pixel, PixelIsland island) {
		this(pixel, (PixelGraph) null);
		this.island = island;
		addNeighboursToUnusedSet(pixel, island);
	}

	private void addNeighboursToUnusedSet(Pixel pixel, PixelIsland island) {
		ensureUnusedNeighbours();
		if (pixel == null) {
			throw new RuntimeException("Null Pixel");
		}
		unusedNeighbours.addAll(pixel.getOrCreateNeighbours(island).getList());
	}
	
	public Pixel getCentrePixel() {
		return centrePixel;
	}

	/** compare Y values of centrePixels then X.
	 * if Y values are equal compare X
	 * 
	 */
	public int compareTo(PixelNode node1) {
		int compare = -1;
		if (node1 != null) {
			Pixel centrePixel1 = node1.getCentrePixel();
			compare = this.centrePixel.compareTo(centrePixel1);
		}
		return compare;
	}
	
	public String toString() {
		getCentrePixel();
		getNucleus();
		
		StringBuilder sb = new StringBuilder();
		sb.append(START_STRING);
		sb.append((id == null) ? "" : id);
		sb.append((label == null) ? "" : " l:"+label);
		sb.append((centrePixel == null) ? "" : String.valueOf(centrePixel));
		sb.append(END_STRING);
		return sb.toString();
	}

	public PixelNucleus getNucleus() {
		ensurePixelNucleus();
		return pixelNucleus;
	}

	private void ensurePixelNucleus() {
		if (pixelNucleus == null && island != null) {
			pixelNucleus = island.getOrCreateNucleusFactory().getNucleusByPixel(centrePixel);
		}
	}

	public PixelList getDiagonalNeighbours(PixelIsland island) {
		return centrePixel.getDiagonalNeighbours(island);
	}

	public PixelList getOrthogonalNeighbours(PixelIsland island) {
		return centrePixel.getOrthogonalNeighbours(island);
	}

	public void addEdge(PixelEdge pixelEdge) {
		ensureEdgeList();
		this.edgeList.add(pixelEdge);
	}

	private void ensureEdgeList() {
		if (edgeList == null) {
			edgeList = new PixelEdgeList();
		}
	}

	public PixelEdgeList getEdges() {
		ensureEdgeList();
		return edgeList;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public boolean removeEdge(PixelEdge edge) {
		return edgeList.remove(edge);
	}

	public SVGG createSVG(double rad) {
		return createSVG(rad, "none");
	}

	public SVGG createSVG(double rad, String color) {
		SVGG g = new SVGG();
		SVGCircle circle = new SVGCircle(new Real2(centrePixel.getInt2()).plus(new Real2(0.5, 0.5)), rad);
		g.appendChild(circle);
		circle.setFill(color);
		return g;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public PixelSet getUnusedNeighbours() {
		ensureUnusedNeighbours();
		return unusedNeighbours;
	}

	private void ensureUnusedNeighbours() {
		if (unusedNeighbours == null) {
			unusedNeighbours = new PixelSet();
		}
	}

	public boolean hasMoreUnusedNeighbours() {
		return getUnusedNeighbours().size() > 0;
	}

	/** gets an unusued neighbour and removes from unusedNeighbours set.
	 * 
	 * @return
	 */
	public Pixel getNextUnusedNeighbour() {
		ensureUnusedNeighbours();
		Pixel nextUnused = unusedNeighbours.iterator().next();
		unusedNeighbours.remove(nextUnused);
		return nextUnused;
	}

	public void removeUnusedNeighbour(Pixel neighbour) {
		ensureUnusedNeighbours();
		unusedNeighbours.remove(neighbour);
		LOG.trace(this+" removed: "+neighbour+" unused: "+unusedNeighbours);
	}

	public void setCentrePixel(Pixel pixel) {
		this.centrePixel = pixel;
	}

	public void setIsland(PixelIsland island) {
		this.island = island;
		
	}

	public Real2 getReal2() {
		return (getInt2() == null) ? null : new Real2(getInt2());
	}

	public Int2 getInt2() {
		Pixel pixel = getCentrePixel();
		return pixel == null ? null : pixel.getInt2();
	}

	public SVGG getOrCreateSVG() {
		if (svgg == null) {
			svgg = new SVGG();
			SVGCircle svgCircle = new SVGCircle(new Real2(centrePixel.getInt2()), radius);
			svgCircle.setStroke(stroke);
			svgCircle.setStrokeWidth(strokeWidth);
			svgCircle.setOpacity(opacity);
			svgg.appendChild(svgCircle);
		}
		return svgg;
	}

	/** get all nodes connected to this by an edge.
	 * if a node is connected twice by >= 2 edges it occurs multiple times
	 * if a node is connected to itself (ouroboros) it is also included
	 * 
	 * thus "A" would have 2 multiply connected nodes and two spikes
	 * thus "O" would have 1 node connected to itself 
	 * thus "P" would have 1 node connected to itself and one spike
	 * 
	 * @return
	 */
	public PixelNodeList getConnectedNodes() {
		PixelNodeList nodeList = new PixelNodeList();
		for (PixelEdge edge : edgeList) {
			PixelNode node = edge.getOtherNode(this);
			nodeList.add(node);
		}
		return nodeList;
	}

	public int edgeCountLongerThan(int minLength) {
		PixelEdgeList nodeEdges = getEdges();
		int count = 0;
		for (PixelEdge edge : nodeEdges) {
			double len = edge.getLength();
			if (len > minLength) {
				count++;
			}
		}
		return count;
	}

	/** change coordinates
	 * don't change any other parameters
	 * 
	 * @param newXY
	 */
	public void setInt2(Int2 newXY) {
		this.getCentrePixel().setInt2(newXY);
	}

	/** snap coordinates to nearest point in array.
	 * 
	 * @param yArray
	 */
	public void snapToArray(IntArray array, Axis2 axis) {
		boolean isx = Axis2.X.equals(axis);
		Int2 xy = this.getInt2();
		int xNode = xy.getX();
		int yNode = xy.getY();
		int idx = array.getIndexOfClosestElement(isx ? xNode : yNode);
		if (idx != -1) {
			int snapped = array.elementAt(idx);
			int xnew = isx ? snapped : xNode;
			int ynew = isx ? yNode : snapped;
			Int2 newXY = new Int2(xnew, ynew);
		}
	}
	
//	/** snap coordinates to nearest point in array.
//	 * 
//	 * @param yArray
//	 */
//	public void snapToYArray(IntArray yArray) {
//		Int2 xy = this.getInt2();
//		int xNode = xy.getX();
//		int yNode = xy.getY();
//		int idx = yArray.getIndexOfClosestElement(yNode);
//		int snappedY = yArray.elementAt(idx);
//		Int2 newXY = new Int2(xNode, snappedY);
//		if (!xy.equals(newXY)) {
//			LOG.debug(xy + " => " + newXY);
//			this.setInt2(newXY);
//		}
//	}
	
}

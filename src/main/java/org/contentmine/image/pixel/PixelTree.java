package org.contentmine.image.pixel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.contentmine.eucl.euclid.Int2;
import org.contentmine.image.pixel.PixelNucleus.PixelJunctionType;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class PixelTree<T> {

	public class PixelTreeNode {
		
		public Pixel pixel;
		public List<PixelTreeNode> children = new ArrayList<PixelTreeNode>();
		public PixelList childrenAsPixels = new PixelList();
		public List<PixelTreeNode> parents = new ArrayList<PixelTreeNode>();
		public PixelList parentsAsPixels = new PixelList();
		public T data;
		private int marked;
		public boolean cyclic = false;
		
		private PixelTreeNode(Pixel pixel) {
			this.pixel = pixel;
			pixelMap.put(pixel, this);
			size++;
		}
		
		private PixelTreeNode(PixelTreeNode p) {
			pixel = p.pixel;
			data = p.data;
			cyclic = p.cyclic;
		}
		
		@Override
		public String toString() {
			return pixel + " " + data;
		}

		public void addChild(PixelTreeNode node) {
			children.add(node);
			childrenAsPixels.add(node.pixel);
			node.parents.add(this);
			node.parentsAsPixels.add(pixel);
		}
		
		public PixelTree<T> getTree() {
			return PixelTree.this;
		}
		
	}
	
	public abstract class PixelTreeWalker {
		
		public void walk() {
			for (PixelTreeNode s : starts) {
				walk(s);
			}
			markedCount++;
		}
		
		private void walk(PixelTreeNode n) {
			n.marked++;
			n.data = process(n);
			for (PixelTreeNode c : n.children) {
				if (c.marked == markedCount) {
					walk(c);
				}
			}
		}
		
		public abstract T process(PixelTreeNode n);
		
	}

	public List<PixelTreeNode> starts = new ArrayList<PixelTreeNode>();
	private Map<Pixel, PixelTreeNode> pixelMap = new HashMap<Pixel, PixelTreeNode>();
	private Map<PixelNode, PixelTreeNode> specialMap = new HashMap<PixelNode, PixelTreeNode>();
	public Multiset<PixelTreeNode> terminals = HashMultiset.create();
	private Set<PixelEdge> edges = new HashSet<PixelEdge>();
	public int size;
	public boolean cyclic = true;
	
	private int markedCount;
	
	public PixelTree() {
		//this.start = new PixelTreeNode(start.centrePixel);
	}
	
	public PixelList getChildren(Pixel pixel) {
		return pixelMap.get(pixel).childrenAsPixels;
	}
	
	public PixelList getParents(Pixel pixel) {
		return pixelMap.get(pixel).parentsAsPixels;
	}
	
	public void addEdgelessNode(PixelNode node) {
		PixelTreeNode treeNodeForStart = new PixelTreeNode(node.getCentrePixel());
		specialMap.put(node, treeNodeForStart);
		pixelMap.put(node.getCentrePixel(), treeNodeForStart);
		starts.add(treeNodeForStart);
		terminals.add(treeNodeForStart);
	}

	public void addPixelsFromEdge(PixelNode start, PixelEdge edge) {
		cyclic &= start.getNucleus().getJunctionType() != null && start.getNucleus().getJunctionType().equals(PixelJunctionType.CYCLIC);
		edges.add(edge);
		PixelNode end = edge.getOtherNode(start);
		boolean reverse = (edge.getNodes().getList().indexOf(start) != 0);//(edge.getFirst().getInt2().getEuclideanDistance(start.getInt2()) < edge.getLast().getInt2().getEuclideanDistance(start.getInt2()) ? false : true);
		PixelTreeNode treeNodeForStart = specialMap.get(start);
		if (treeNodeForStart == null) {
			treeNodeForStart = new PixelTreeNode(start.getCentrePixel());//reverse ? edge.getLast() : edge.getFirst());
			specialMap.put(start, treeNodeForStart);
			pixelMap.put(treeNodeForStart.pixel, treeNodeForStart);
			starts.add(treeNodeForStart);
		} else {
			if (!nodeEncountered(start)) {
				terminals.remove(treeNodeForStart, Integer.MAX_VALUE);
			}
		}
		PixelTreeNode treeNodeForEnd = specialMap.get(end);
		boolean cyclic = false;
		if (treeNodeForEnd == null) {
			treeNodeForEnd = new PixelTreeNode(end.getCentrePixel());//reverse ? edge.getLast() : edge.getFirst());
			specialMap.put(end, treeNodeForEnd);
			pixelMap.put(treeNodeForEnd.pixel, treeNodeForEnd);
		} else {
			cyclic = true;
		}
		terminals.add(treeNodeForEnd);
		PixelTreeNode mostRecent = treeNodeForStart;
		for (int i = (reverse ? edge.size() - 1 : 0); (reverse ? i >= 0 : i < edge.size()); i = (reverse ? i - 1 : i + 1)) {
			Pixel p = edge.get(i);
			PixelTreeNode node = pixelMap.get(p);
			if (node == null) {
				node = new PixelTreeNode(p);
			}
			if (mostRecent != node) {
				mostRecent.addChild(node);
				if (node == treeNodeForEnd) {
					mostRecent.cyclic = cyclic;
				}
				mostRecent = node;
			}
		}
		if (mostRecent != treeNodeForEnd) {
			mostRecent.addChild(treeNodeForEnd);
			mostRecent.cyclic = cyclic;
		}
	}

	public boolean contains(Pixel centrePixel) {
		return pixelMap.containsKey(centrePixel);
	}

	public boolean nodeEncountered(PixelNode endNode) {
		PixelTreeNode node = specialMap.get(endNode);
		if (node != null) {
			return (node.children.size() > 0);
		}
		return false;
	}

	public boolean edgeEncountered(PixelEdge edge) {
		return edges.contains(edge);
	}

	public void add(final PixelTree<T> pixelTree) {
		final Map<PixelTreeNode, PixelTreeNode> oldToNew = new HashMap<PixelTreeNode, PixelTreeNode>();
		pixelTree.new PixelTreeWalker() {

			@Override
			public T process(PixelTreeNode n) {
				PixelTreeNode newN = oldToNew.get(n);
				if (newN == null) {
					newN = new PixelTreeNode(n);
					newN.marked = markedCount;
					oldToNew.put(n, newN);
				}
				for (PixelTreeNode c : n.children) {
					PixelTreeNode newC = oldToNew.get(c);
					if (newC == null) {
						newC = new PixelTreeNode(c);
						newC.marked = markedCount;
						oldToNew.put(c, newC);
					}
					newN.children.add(newC);
					newN.childrenAsPixels.add(newC.pixel);
				}
				for (PixelTreeNode p : n.parents) {
					PixelTreeNode newP = oldToNew.get(p);
					if (newP == null) {
						newP = new PixelTreeNode(p);
						newP.marked = markedCount;
						oldToNew.put(p, newP);
					}
					newN.parents.add(newP);
					newN.parentsAsPixels.add(newP.pixel);
				}
				if (pixelTree.starts.contains(n)) {
					starts.add(newN);
				}
				int countOfTerminals = pixelTree.terminals.count(n);
				if (countOfTerminals > 0) {
					terminals.add(newN, countOfTerminals);
				}
				pixelMap.put(newN.pixel, newN);
				return n.data;
			}
			
		}.walk();
		
		for (Entry<PixelNode, PixelTreeNode> special : pixelTree.specialMap.entrySet()) {
			specialMap.put(special.getKey(), oldToNew.get(special.getValue()));
		}
		
		/*starts.addAll(pixelTree.starts);
		terminals.addAll(pixelTree.terminals);
		specialMap.putAll(pixelTree.specialMap);
		pixelMap.putAll(pixelTree.pixelMap);*/
		
		cyclic |= pixelTree.cyclic;		
		edges.addAll(pixelTree.edges);
		size += pixelTree.size;
	}

	//public PixelTree<T> resize(final int widthOfTree, final int newWidth, final int heightOfTree, final int newHeight) {
	public PixelTree<T> resize(final double wScale, final double hScale) {
		final Map<PixelTreeNode, PixelTreeNode> oldToNew = new HashMap<PixelTreeNode, PixelTreeNode>();
		final PixelTree<T> expanded = new PixelTree<T>();
		final PixelIsland newIsland = new PixelIsland();
		newIsland.setDiagonal(starts.get(0).pixel.getIsland().getDiagonal());
		
		new PixelTreeWalker() {
			
			double widthPointer;
			double heightPointer;

			@Override
			public T process(PixelTreeNode n) {
				Int2 oldPosition = n.pixel.getInt2();
				long newX = Math.round(wScale * oldPosition.getX());
				long newY = Math.round(hScale * oldPosition.getY());
				Pixel newPixel = new Pixel((int) newX, (int) newY);
				PixelTreeNode newN = oldToNew.get(n);
				if (newN == null) {
					newN = expanded.new PixelTreeNode(n);
					oldToNew.put(n, newN);
					newN.pixel = newPixel;
					expanded.pixelMap.put(newN.pixel, newN);
					newPixel.setIsland(newIsland);
					newIsland.addPixelWithoutComputingNeighbours(newPixel);
					//System.out.println("New node");
				}
				for (PixelTreeNode c : n.children) {
					Int2 oldPositionOfC = c.pixel.getInt2();
					long newXOfC = Math.round(wScale * oldPositionOfC.getX());
					long newYOfC = Math.round(hScale * oldPositionOfC.getY());
					Pixel newPixelOfC = new Pixel((int) newXOfC, (int) newYOfC);
					if (newPixel.equals(newPixelOfC)) {
						oldToNew.put(c, newN);
						//System.out.println("Ignored");
					} else if (!newPixel.isNeighbour(newPixelOfC)) {
						PixelTreeNode newC = oldToNew.get(c);
						if (newC == null) {
							newC = expanded.new PixelTreeNode(c);
							oldToNew.put(c, newC);
							newC.pixel = newPixelOfC;
							expanded.pixelMap.put(newC.pixel, newC);
							newPixelOfC.setIsland(newIsland);
							newIsland.addPixelWithoutComputingNeighbours(newPixelOfC);
							//System.out.println("New node as child");
						}
						PixelTreeNode extra = expanded.new PixelTreeNode(new Pixel((newPixel.getInt2().getX() + newPixelOfC.getInt2().getX()) / 2, (newPixel.getInt2().getY() + newPixelOfC.getInt2().getY()) / 2));
						extra.pixel.setIsland(newIsland);
						newIsland.addPixelWithoutComputingNeighbours(extra.pixel);
						expanded.pixelMap.put(extra.pixel, extra);
						newN.children.add(extra);
						newN.childrenAsPixels.add(extra.pixel);
						extra.children.add(newC);
						extra.childrenAsPixels.add(newPixelOfC);
						newC.parents.add(extra);
						newC.parentsAsPixels.add(extra.pixel);
						extra.parents.add(newN);
						extra.parentsAsPixels.add(newN.pixel);
						//System.out.println("Skipped");
					} else {
						PixelTreeNode newC = oldToNew.get(c);
						if (newC == null) {
							newC = expanded.new PixelTreeNode(c);
							oldToNew.put(c, newC);
							newC.pixel = newPixelOfC;
							expanded.pixelMap.put(newC.pixel, newC);
							newPixelOfC.setIsland(newIsland);
							newIsland.addPixelWithoutComputingNeighbours(newPixelOfC);
							//System.out.println("New node as child");
						}
						newN.children.add(newC);
						newN.childrenAsPixels.add(newC.pixel);
						newC.parents.add(newN);
						newC.parentsAsPixels.add(newN.pixel);
						//System.out.println("Continued");
					}
				}
				//for (PixelTreeNode p : n.parents) {
					//PixelTreeNode newP = oldToNew.get(p);
					//if (newP == null) {
						//newP = new PixelTreeNode(p);
						//newP.marked = markedCount;
						//oldToNew.put(p, newP);
					//}
					//newN.parents.add(newP);
					//newN.parentsAsPixels.add(newP.pixel);
				//}
				if (starts.contains(n)) {
					expanded.starts.add(newN);
				}
				int countOfTerminals = terminals.count(n);
				if (countOfTerminals > 0) {
					expanded.terminals.add(newN, countOfTerminals);
				}
				return n.data;
			}
			
		}.walk();
		
		for (Entry<PixelNode, PixelTreeNode> special : specialMap.entrySet()) {
			expanded.specialMap.put(special.getKey(), oldToNew.get(special.getValue()));
		}
		
		expanded.cyclic = cyclic;
		expanded.edges.addAll(edges);
		expanded.size = expanded.pixelMap.size();
		
		return expanded;
		/*final PixelTree<T> expanded = new PixelTree<T>();
		int extraWidth = newWidth - widthOfTree;
		int extraHeight = newHeight - heightOfTree;
		double spacingForExtraWidth = widthOfTree / ((double) Math.abs(extraWidth) + 1);
		double spacingForExtraHeight = heightOfTree / ((double) Math.abs(extraHeight) + 1);
		final long[] locationsForExtraWidth = new long[extraWidth];
		for (int i = 0; i < extraWidth; i++) {
			locationsForExtraWidth[i] = Math.round((i + 1) * spacingForExtraWidth);
		}
		final long[] locationsForExtraHeight = new long[extraHeight];
		for (int i = 0; i < extraHeight; i++) {
			locationsForExtraHeight[i] = Math.round((i + 1) * spacingForExtraHeight);
		}
		final Map<PixelTreeNode, PixelTreeNode> oldToNew = new HashMap<PixelTreeNode, PixelTreeNode>();
		new PixelTreeWalker() {
			
			double widthPointer;
			double heightPointer;

			@Override
			public T process(PixelTreeNode n) {
				if (n.parents.size() == 0) {
					for (int i = 0; i < locationsForExtraWidth.length; i++) {
						long l = locationsForExtraWidth[i];
						if (l > n.pixel.getInt2().getX()) {
							widthPointer = i - 0.5;
						}
					}
					for (int i = 0; i < locationsForExtraHeight.length; i++) {
						long l = locationsForExtraHeight[i];
						if (l > n.pixel.getInt2().getY()) {
							heightPointer = i - 0.5;
						}
					}
				}
				PixelTreeNode newN = oldToNew.get(n);
				if (newN == null) {
					newN = new PixelTreeNode(n);
					newN.marked = markedCount;
					oldToNew.put(n, newN);
				}
				for (PixelTreeNode c : n.children) {
					PixelTreeNode newC = oldToNew.get(c);
					if (newC == null) {
						newC = new PixelTreeNode(c);
						newC.marked = markedCount;
						oldToNew.put(c, newC);
					}
					int nextWidthLocation = locationsForExtraWidth
					newN.children.add(newC);
					newN.childrenAsPixels.add(newC.pixel);
				}
				//for (PixelTreeNode p : n.parents) {
					//PixelTreeNode newP = oldToNew.get(p);
					//if (newP == null) {
						//newP = new PixelTreeNode(p);
						//newP.marked = markedCount;
						//oldToNew.put(p, newP);
					//}
					//newN.parents.add(newP);
					//newN.parentsAsPixels.add(newP.pixel);
				//}
				if (starts.contains(n)) {
					expanded.starts.add(newN);
				}
				int countOfTerminals = terminals.count(n);
				if (countOfTerminals > 0) {
					expanded.terminals.add(newN, countOfTerminals);
				}
				expanded.pixelMap.put(newN.pixel, newN);
				return n.data;
			}
			
		}.walk();
		
		for (Entry<PixelNode, PixelTreeNode> special : specialMap.entrySet()) {
			expanded.specialMap.put(special.getKey(), oldToNew.get(special.getValue()));
		}
		
		expanded.cyclic = cyclic;
		expanded.edges.addAll(edges);
		expanded.size = size;
		
		return expanded;*/
	}

}
package org.contentmine.ami.plugins.phylotree;

import nu.xom.Attribute;

import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.linestuff.ComplexLine;
import org.contentmine.graphics.svg.linestuff.Joint;

public class SVGXTreeEdge extends SVGG {

	static final String CHILD = "child";
	static final String PARENT = "parent";
	
	private ComplexLine complexLine;
	private SVGXTreeNode childNode;
	private PhyloTreeSVGAnalyzer treeAnalyzer;
	private SVGXTreeNode parentNode;
	private Double length;

	public SVGXTreeEdge(PhyloTreeSVGAnalyzer treeAnalyzer, ComplexLine line) {
		this.complexLine = line;
		this.treeAnalyzer = treeAnalyzer;
		treeAnalyzer.edgeList.add(this);
		treeAnalyzer.tree.appendChild(this);
		setId((line == null) ? "null" : line.getBackbone().getId());
		treeAnalyzer.treeEdgeByIdMap.put(getId(), this);
		SVGLine backBoneLine = line.getBackbone();
		backBoneLine.detach();
		this.appendChild(backBoneLine);
	}
	
	void setChildNode(SVGXTreeNode svgxTreeNode) {
		this.childNode = svgxTreeNode;
		this.addAttribute(new Attribute(CHILD, childNode.getId()));
	}

	void setParentNode(SVGXTreeNode svgxTreeNode) {
		this.parentNode = svgxTreeNode;
		this.addAttribute(new Attribute(PARENT, parentNode.getId()));
	}

	SVGXTreeNode createAndAddParentNode() {
		SVGXTreeNode parentNode = null;
		Joint joint = complexLine.getJointAtEnd(treeAnalyzer.sideOrientation.getOtherOrientation());
		if (joint != null) {
			SVGLine line = joint.getLine();
			parentNode = treeAnalyzer.getOrCreateNode(line);
			this.setParentNode(parentNode);
		}
		return parentNode;
	}

	/*
	private ComplexLine line;
	private SVGXTreeNode childNode;
	private SVGXTreeNode parentNode;
	 */
	public String getString() {
		String s = "";
		s += " CHILD: "+childNode.getId();
		s += " LINE: "+complexLine.getBackbone().getId();
		s += " PARENT: "+((parentNode == null) ? "NULL" : parentNode.getId());
		return s;
	}

	public SVGXTreeNode getChildNode() {
		return childNode;
	}

	public SVGXTreeNode getParentNode() {
		return parentNode;
	}

	public SVGLine getLine() {
		return complexLine.getBackbone();
	}

	public double getLength() {
		if (length ==null) {
			length = complexLine.getBackbone().getEuclidLine().getLength();
		}
		return length;
	}

}

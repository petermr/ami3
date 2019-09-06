package org.contentmine.ami.plugins.phylotree;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.linestuff.ComplexLine;
import org.contentmine.graphics.svg.linestuff.Joint;

import nu.xom.Attribute;
import nu.xom.Nodes;

/** manages nodes in the SVGXtree
 * 
 * @author pm286
 *
 */
public class SVGXTreeNode extends SVGG {

	private final static Logger LOG = Logger.getLogger(SVGG.class);
	
	private static final String CHILD = "child";
	private static final String CHILD_EDGE = "childEdge";
	private static final String PARENT = "parent";
	private static final String PARENT_EDGE = "parentEdge";
	
	public final static double DEFAULT_RAD = 2.0;
	private SVGElement svgElement;
	private ComplexLine tempVarComplexLine;
	private PhyloTreeSVGAnalyzer treeAnalyzer;
//	String id;
	private SVGXTreeEdge edge;
	private SVGXTreeNode parentNode;
	List<SVGXTreeNode> childNodeList;
	private SVGXTree tree;
	private double idSize = 5.0;
	private String idColor = "blue";
	private double primitiveWidth = 1.0;
	private String primitiveColor = "green";
	private double textSize = 7.0;
	SVGText text;

	private Double distanceToClosestWord;

	public SVGXTreeNode(PhyloTreeSVGAnalyzer treeAnalyzer, Real2 point) {
		this.svgElement = SVGXTreeNode.createNewSVGPrimitive(point);
		this.treeAnalyzer = treeAnalyzer;
		initx();
	}

	public SVGXTreeNode(PhyloTreeSVGAnalyzer treeAnalyzer, ComplexLine complexLine) {
		this.svgElement = complexLine.getBackbone();
		this.tempVarComplexLine = complexLine;
		this.treeAnalyzer = treeAnalyzer;
		initx();
	}

	private void initx() {
		this.tree = treeAnalyzer.tree;
		treeAnalyzer.ensureNodeList();
		treeAnalyzer.nodeList.add(this);
		treeAnalyzer.tree.appendChild(this);
		createAndSetId();
		Real2 point = addSVGPrimitive();
		addGraphicalId(point);
	}

	private Real2 addSVGPrimitive() {
		svgElement.detach();
		this.appendChild(svgElement);
		Real2 point = svgElement.getBoundingBox().getCentroid();
		SVGElement primitive = SVGXTreeNode.createNewSVGPrimitive(point);
		this.appendChild(primitive);
		primitive.setStrokeWidth(primitiveWidth);
		primitive.setFill(primitiveColor);
		return point;
	}

	private void addGraphicalId(Real2 point) {
		SVGText text = new SVGText(point.plus(new Real2(idSize/2., -idSize)), this.getId());
		text.setFontSize(idSize);
		text.setFill(idColor);
		this.appendChild(text);
	}

	private void createAndSetId() {
		setId("N"+treeAnalyzer.nodeList.size());
	}
	
	private static SVGElement createNewSVGPrimitive(Real2 point) {
		SVGCircle circle = new SVGCircle(point, DEFAULT_RAD);
		return circle;
	}

	SVGXTreeEdge addParentEdge(ComplexLine singleEndedLine) {
		SVGXTreeEdge edge  = new SVGXTreeEdge(this.treeAnalyzer, singleEndedLine);
		edge.setChildNode(this);
		return edge;
	}

	SVGXTreeEdge getParentEdge() {
		SVGXTreeEdge parentEdge = null;
		if (tempVarComplexLine != null) {
			List<Joint> joints = tempVarComplexLine.getJoints(treeAnalyzer.sideOrientation.getOtherOrientation()); 
			if (joints.size() == 0) {
				
			} else if (joints.size() > 1) {
				LOG.trace("too many joints: "+joints.size());
			} else {
				Joint joint = joints.get(0);
				ComplexLine complexLine1 = treeAnalyzer.complexLineByLineMap.get(joint.getLine());
				String line1Id = complexLine1.getBackbone().getId();
				parentEdge = treeAnalyzer.treeEdgeByIdMap.get(line1Id);
				if (parentEdge == null) {
					parentEdge = new SVGXTreeEdge(treeAnalyzer, complexLine1);
					parentEdge.setChildNode(this);
					treeAnalyzer.treeEdgeByIdMap.put(line1Id, parentEdge);
				}
			}
		}
		return parentEdge;
	}

	void addParentAndChild(SVGXTreeNode pNode, SVGXTreeEdge edge) {
		if (this.parentNode == null) {
			this.parentNode = pNode;
			if (parentNode != null) {
				this.addAttribute(new Attribute(PARENT, parentNode.getId()));
				parentNode.addAttribute(new Attribute(CHILD, this.getId()));
				parentNode.addAttribute(new Attribute(CHILD_EDGE, this.getId()));
			}
		}
		if (this.edge == null) {
			this.edge = edge;
			this.addAttribute(new Attribute(PARENT_EDGE, edge.getId()));
		}
		if (parentNode != null) {
			parentNode.ensureChildNodeList();
			this.ensureChildNodeList();
			if (!parentNode.childNodeList.contains(this)) {
				parentNode.addChildNode(this);
			}
		}
	}

	private void addChildNode(SVGXTreeNode svgxTreeNode) {
		ensureChildNodeList();
		childNodeList.add(svgxTreeNode);
	}

	private void ensureChildNodeList() {
		if (childNodeList == null) {
			childNodeList = new ArrayList<SVGXTreeNode>();
		}
	}

	public Real2 getCentroid() {
		Real2 point = this.svgElement.getBoundingBox().getCentroid();
		return point;
	}

	public Real2 getXY() {
		return this.svgElement.getXY();
	}
	
	public Real2Range getBoundingBox() {
		return svgElement.getBoundingBox();
	}

	public List<SVGXTreeNode> getChildTreeNodeChildren() {
		return childNodeList;
	}

	public SVGXTreeNode getParentTreeNode() {
		if (this.parentNode == null) {
			String parentId = this.getAttributeValue(PARENT);
			if (parentId == null) {
				return null;
			}
			Nodes nodes = tree.query(".//*[@id='"+parentId+"']");
			parentNode = (nodes.size() == 1) ? (SVGXTreeNode) nodes.get(0) : null;
		}
		return parentNode;
	}

	/**
	private SVGElement svgElement;
	private ComplexLine complexLine;
	private SVGXTree tree;
	private String id;
	 */
	public String getString() {
		String s = "NODE "+getId()+"\n";
		s += " ELEM: "+svgElement.getClass().getName();
		s += " XY: "+svgElement.getBoundingBox().getCentroid();
		s += " LINE: "+((tempVarComplexLine == null) ? null : tempVarComplexLine.getBackbone().getId());
		s += " PARENT: "+((parentNode == null) ? null : parentNode.getId());
		return s;
	}

	public void addClosestWord(String title, Word word) {
		Double d = word.getXY().getDistance(this.getXY());
		if (distanceToClosestWord == null) {
			distanceToClosestWord = d;
			text = createTextChild(title, word);
			this.appendChild(text);
		} else if (d < distanceToClosestWord) {
			distanceToClosestWord = d;
			SVGText newText = createTextChild(title, word);
			text.getParent().replaceChild(text, newText);
			text = newText;
		}
	}

	private SVGText createTextChild(String title, Word word) {
		SVGText newText = new SVGText(this.getXY().plus(new Real2(textSize, textSize/2.)), word.getValue());
		newText.setFontSize(textSize );
		newText.setTitle(title);
		return newText;
	}

}

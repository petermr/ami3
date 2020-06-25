package org.contentmine.ami.plugins.phylotree;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.linestuff.BoundingBoxManager;
import org.contentmine.graphics.svg.linestuff.ComplexLine;
import org.contentmine.graphics.svg.linestuff.ComplexLine.LineOrientation;
import org.contentmine.graphics.svg.linestuff.ComplexLine.SideOrientation;

//import org.contentmine.svg2xml.builder.GeometryBuilder;
//import org.contentmine.svg2xml.text.Word;

/** 
 * Internal engine that does the hard work of creating the tree.
 * Output is delegated from SVGXTree.
 * Holds annotation of lines and also... TODO
 * 
 * @author pm286
 */
// FIXME commented out
// does not build with new svghtml and norma
// probably worth refactoring if we get use for it.

public class PhyloTreeSVGAnalyzer {
	
	private static final Logger LOG = LogManager.getLogger(PhyloTreeSVGAnalyzer.class);

	public static final double DEFAULT_PIXEL_EPS = 0.01;

	private LineContainer horizontal;
	private LineContainer vertical;
	private List<ComplexLine> singleEndedLines;
	private Stack<SVGXTreeEdge> edgeStack;
	private Map<SVGLine, SVGXTreeEdge> treeEdgeByLineMap;
	private Map<String, SVGXTreeNode> treeNodeByIdMap;
	private Map<SVGLine, SVGXTreeNode> treeNodeByLineMap;
	private LineOrientation treeOrientation;
	SVGXTree tree;
	
	Map<SVGLine, ComplexLine> complexLineByLineMap;
	Map<String, SVGXTreeEdge> treeEdgeByIdMap;
	List<SVGXTreeEdge> edgeList;
	List<SVGXTreeNode> nodeList;
	SideOrientation sideOrientation;

	private double minNodeDelta;
	private double maxNodeDelta;
	private List<Word> wordList;

	SVGElement parentSVGElement;
	private ArrayList<SVGXTreeNode> childlessNodeList;
	private int selectedIslandIndex;

	private double eps = DEFAULT_PIXEL_EPS;

	public PhyloTreeSVGAnalyzer(SVGXTree tree) {
		this.tree = tree;
		parentSVGElement = tree.parentSVGElement;
	}
	
	public PhyloTreeSVGAnalyzer() {
//		setDefaults();
//		clearVariables();
	}


	/** 
	 * Separates lines into horizontal and vertical and classifies them by
	 * what branches or none they have at the ends.
	 * These can be returned by
	 * extractLinesWithBranchAtEnd(LineOrientation, SideOrientation).
	 * 
	 * @param svgxTree TODO
	 * @param svgLines
	 */
	 @Deprecated // first arg not used 
	public void analyzeBranchesAtLineEnds(SVGXTree tree, List<SVGLine> svgLines, double eps) {
		analyzeBranchesAtLineEnds(svgLines, eps);
	}
	 
	void analyzeBranchesAtLineEnds(List<SVGLine> svgLines, double eps) {
		setPixelEpsilon(eps);
		analyzeBranchesAtLineEnds(svgLines);
	}
	public void setPixelEpsilon(double eps) {
		this.eps = eps;
	}
	
	public void analyzeBranchesAtLineEnds(List<SVGLine> svgLines) {
		ensureVerticalContainer();
		ensureHorizontalContainer();
		
		vertical.createLines(svgLines, LineOrientation.VERTICAL);
		horizontal.createLines(svgLines, LineOrientation.HORIZONTAL);
		vertical.setPerpendicularLines(horizontal);
		horizontal.setPerpendicularLines(vertical);
		
		vertical.createAllComplexLines();
		horizontal.createAllComplexLines();
	
		indexComplexLines();
		checkEndLineCounts();
	}
	
	private void ensureVerticalContainer() {
		if (horizontal == null) {
			horizontal = new LineContainer(eps);
		}
	}
	
	private void ensureHorizontalContainer() {
		if (vertical == null) {
			vertical = new LineContainer(eps);
		}
	}
	
	private boolean checkEndLineCounts() {
		singleEndedLines = null;
		setTreeOrientation(null);
		sideOrientation = null;
		boolean goodTree = true;
		if (horizontal.getLines().size() > vertical.getLines().size()) {
			setTreeOrientation(LineOrientation.HORIZONTAL);
			if (horizontal.getMinusEndedLines().size() > horizontal.getPlusEndedLines().size()) {
				sideOrientation = SideOrientation.PLUS;
				goodTree = (horizontal.getPlusEndedLines().size() - 1 != vertical.getDoubleEndedLines().size());
				goodTree |= (horizontal.getDoubleEndedLines().size() + 1 != vertical.getDoubleEndedLines().size());
				singleEndedLines = horizontal.getMinusEndedLines();
			} else if (horizontal.getMinusEndedLines().size() < horizontal.getPlusEndedLines().size()) {
				sideOrientation = SideOrientation.MINUS;
				goodTree = (horizontal.getMinusEndedLines().size() - 1 != vertical.getDoubleEndedLines().size());
				goodTree |= (horizontal.getDoubleEndedLines().size() + 1 != vertical.getDoubleEndedLines().size());
				singleEndedLines = horizontal.getPlusEndedLines();
			}
		} else if (vertical.getLines().size() > horizontal.getLines().size()) {
			setTreeOrientation(LineOrientation.VERTICAL);
			if (vertical.getMinusEndedLines().size() > vertical.getPlusEndedLines().size()) {
				sideOrientation = SideOrientation.PLUS;
				goodTree = (vertical.getPlusEndedLines().size() - 1 != horizontal.getDoubleEndedLines().size());
				goodTree |= (vertical.getDoubleEndedLines().size() + 1 != horizontal.getDoubleEndedLines().size());
				singleEndedLines = vertical.getMinusEndedLines();
			} else if (horizontal.getMinusEndedLines().size() < horizontal.getPlusEndedLines().size()) {
				sideOrientation = SideOrientation.MINUS;
				goodTree = (vertical.getMinusEndedLines().size() - 1 != horizontal.getDoubleEndedLines().size());
				goodTree |= (vertical.getDoubleEndedLines().size() + 1 != horizontal.getDoubleEndedLines().size());
				singleEndedLines = vertical.getPlusEndedLines();
			}
		}
		return goodTree;
	}
	
	private void indexComplexLines() {
		complexLineByLineMap = new HashMap<SVGLine, ComplexLine>();
		for (ComplexLine complexLine : vertical.getComplexLines()) {
			complexLineByLineMap.put(complexLine.getBackbone(), complexLine);
		}
		for (ComplexLine complexLine : horizontal.getComplexLines()) {
			complexLineByLineMap.put(complexLine.getBackbone(), complexLine);
		}
	}

	public List<ComplexLine> extractLinesWithBranchAtEnd(LineOrientation lineOrientation, List<SideOrientation> sideOrientationList) {
		ensureHorizontalContainer();
		ensureVerticalContainer();
		List<ComplexLine> complexLineList = null;
		if (LineOrientation.HORIZONTAL.equals(lineOrientation)) {
			if (SideOrientation.EMPTYLIST.equals(sideOrientationList)) {
				complexLineList = horizontal.getEmptyEndedLines();
			} else if (SideOrientation.MINUSPLUSLIST.equals(sideOrientationList)) {
				complexLineList = horizontal.getDoubleEndedLines();
			} else if (SideOrientation.MINUSLIST.equals(sideOrientationList)) {
				complexLineList = horizontal.getMinusEndedLines();
			} else if (SideOrientation.PLUSLIST.equals(sideOrientationList)) {
				complexLineList = horizontal.getPlusEndedLines();
			}
			
		} else if (LineOrientation.VERTICAL.equals(lineOrientation)) {
			
			if (SideOrientation.EMPTYLIST.equals(sideOrientationList)) {
				complexLineList = vertical.getEmptyEndedLines();
			} else if (SideOrientation.MINUSPLUSLIST.equals(sideOrientationList)) {
				complexLineList = vertical.getDoubleEndedLines();
			} else if (SideOrientation.MINUSLIST.equals(sideOrientationList)) {
				complexLineList = vertical.getMinusEndedLines();
			} else if (SideOrientation.PLUSLIST.equals(sideOrientationList)) {
				complexLineList = vertical.getPlusEndedLines();
			}
		}
		return complexLineList;
	}

	private void findEmptyEndsAndPushCreatedEdgesOntoStack() {
		if (singleEndedLines != null) {
			for (ComplexLine singleEndedLine : singleEndedLines) {
				Real2 otherPoint = singleEndedLine.getCornerAt(sideOrientation);
				SVGXTreeNode node = new SVGXTreeNode(this, otherPoint);
				SVGXTreeEdge edge = node.addParentEdge(singleEndedLine);
				edgeStack.push(edge);
			}
		}
	}

	public void buildTree() {
		edgeStack = new Stack<SVGXTreeEdge>();
		Set<SVGXTreeEdge> edgeSet = new HashSet<SVGXTreeEdge>();
		nodeList = new ArrayList<SVGXTreeNode>();
		edgeList = new ArrayList<SVGXTreeEdge>();
		treeEdgeByIdMap = new HashMap<String, SVGXTreeEdge>();
		// starting nodes
		findEmptyEndsAndPushCreatedEdgesOntoStack();
		processEdgeStackTillEmpty(edgeSet);
		addEdgesToNodes();
	}
	
	private void processEdgeStackTillEmpty(Set<SVGXTreeEdge> edgeSet) {
		while (!edgeStack.empty()) {
			SVGXTreeEdge edge = edgeStack.pop();
			SVGXTreeNode node = edge.createAndAddParentNode();
			if (node != null) {
				edge = node.getParentEdge();
				if (edge != null && !edgeSet.contains(edge)) {
					edgeStack.push(edge);
					edgeSet.add(edge);
				}
			}
		}
	}
	
	private void addEdgesToNodes() {
		for (SVGXTreeEdge edge : edgeList) {
			LOG.trace("\n"+edge.getString());
			SVGXTreeNode childNode = edge.getChildNode();
			SVGXTreeNode parentNode = edge.getParentNode();
			childNode.addParentAndChild(parentNode, edge);
			
		}
	}

	void ensureNodeList() {
		if (nodeList == null) {
			nodeList = new ArrayList<SVGXTreeNode>();
		}
	}

	void ensureEdgeList() {
		if (edgeList == null) {
			edgeList = new ArrayList<SVGXTreeEdge>();
		}
	}

	Map<String, SVGXTreeEdge> ensureTreeEdgeByIdMap() {
		if (treeEdgeByIdMap == null) {
			treeEdgeByIdMap = new HashMap<String, SVGXTreeEdge>();
		}
		return treeEdgeByIdMap;						
	}

	Map<SVGLine, SVGXTreeEdge> ensureTreeEdgeByLineMap() {
		if (treeEdgeByLineMap == null) {
			treeEdgeByLineMap = new HashMap<SVGLine, SVGXTreeEdge>();
		}
		return treeEdgeByLineMap;						
	}

	private Map<String, SVGXTreeNode> ensureTreeNodeByIdMap() {
		if (treeNodeByIdMap == null) {
			treeNodeByIdMap = new HashMap<String, SVGXTreeNode>();
		}
		return treeNodeByIdMap;
	}

	private Map<SVGLine, SVGXTreeNode> ensureTreeNodeByLineMap() {
		if (treeNodeByLineMap == null) {
			treeNodeByLineMap = new HashMap<SVGLine, SVGXTreeNode>();
		}
		return treeNodeByLineMap;
	}

	SVGXTreeNode getOrCreateNode(SVGLine line) {
		SVGXTreeNode node = null;
		ensureTreeNodeByLineMap();
		ensureTreeNodeByIdMap();
		node = treeNodeByLineMap.get(line);
		if (node == null) {
			ComplexLine complexLine = complexLineByLineMap.get(line);
			node = new SVGXTreeNode(this, complexLine);
			treeNodeByLineMap.put(line, node);
			treeNodeByIdMap.put(node.getId(), node);
		}
		return node;
	}
	
	public List<SVGXTreeNode> getNodeList() {
		ensureNodeList();
		return nodeList;
	}

	public List<SVGXTreeEdge> getEdgeList() {
		ensureEdgeList();
		return edgeList;
	}
	
	public SideOrientation getSideOrientation() {
		return sideOrientation;
	}
	
	public LineOrientation getTreeOrientation() {
		return treeOrientation;
	}
	
	public void setTreeOrientation(LineOrientation treeOrientation) {
		this.treeOrientation = treeOrientation;
	}
	
	public LineContainer getVerticalLineContainer() {
		return vertical;
	}
	
	public LineContainer getHorizontalLineContainer() {
		return horizontal;
	}
	public List<ComplexLine> getSingleEndedLines() {
		return singleEndedLines;
	}
	
	void addLinkLinesLengthsAndWords() {
		addSVGLinkLinesAndRootNode();
		addTexts();
	}
	
	private void addSVGLinkLinesAndRootNode() {
		for (SVGXTreeNode node : getNodeList()) {
			SVGXTreeNode parentNode = node.getParentTreeNode();
			if (parentNode != null) {
				Real2 thisPoint = node.getCentroid();
				Real2 parentPoint = parentNode.getCentroid();
				SVGLine line = new SVGLine(thisPoint, parentPoint);
				line.setStroke("blue");
				tree.appendChild(line);
			} else {
				tree.ensureRootNodeList();
				tree.rootNodeList.add(node);
			}
			
		}
	}

	private void addTexts() {
		minNodeDelta = 2.0;
		maxNodeDelta = 4.0;
		createWordList();
		List<Real2Range> childlessBBoxes = createExtendedBoxes(ensureChildlessNodeList(),
				sideOrientation);
		if (sideOrientation != null) {
			addWordsToChildlessNodes(childlessBBoxes, wordList);
		}
	}

	public Real2Range getXYExtensionBox(SideOrientation direction) {
		Real2Range xyExtension = new Real2Range();
		RealRange xExtension = new RealRange(0.0, 0.0);
		RealRange yExtension = new RealRange(0.0, 0.0);
		if (LineOrientation.HORIZONTAL.equals(treeOrientation)) {
			yExtension = new RealRange(-minNodeDelta, minNodeDelta);
			if (SideOrientation.PLUS.equals(direction)) {
				xExtension = new RealRange(-minNodeDelta, maxNodeDelta);
			} else if (SideOrientation.MINUS.equals(direction)) {
				xExtension = new RealRange(-maxNodeDelta, minNodeDelta);
			}
		} else if (LineOrientation.VERTICAL.equals(treeOrientation)) {
			xExtension = new RealRange(-minNodeDelta, minNodeDelta);
			if (SideOrientation.PLUS.equals(direction)) {
				yExtension = new RealRange(-minNodeDelta, maxNodeDelta);
			} else if (SideOrientation.MINUS.equals(direction)) {
				yExtension = new RealRange(-maxNodeDelta, minNodeDelta);
			}
		}
		LOG.trace(xExtension+" | "+yExtension);
		xyExtension.setXRange(xExtension);
		xyExtension.setYRange(yExtension);
		return xyExtension;
	}

	private List<Real2Range> createExtendedBoxesW(List<Word> wordList, SideOrientation direction) {
		Real2Range xyExtension = getXYExtensionBox(direction);
		List<Real2Range> wordBBoxList = Word.createBBoxList(wordList); 
		List<Real2Range> extendedBBoxes = BoundingBoxManager.createExtendedBBoxes(
				wordBBoxList, xyExtension.getXRange(), xyExtension.getYRange());
		for (Real2Range r2r : extendedBBoxes) {
			LOG.trace(r2r.format(3));
		}
		return extendedBBoxes;
	}
	
	private List<Real2Range> createExtendedBoxes(List<? extends SVGElement> nodeList, SideOrientation direction) {
		Real2Range xyExtension = getXYExtensionBox(direction);
		List<Real2Range> extendedBBoxes = BoundingBoxManager.createExtendedBBoxList(
				nodeList, xyExtension.getXRange(), xyExtension.getYRange());
		for (Real2Range r2r : extendedBBoxes) {
			LOG.trace(r2r);
		}
		return extendedBBoxes;
	}
	
	private void addWordsToChildlessNodes(List<Real2Range> childlessBBoxes, List<Word> wordList) {
		List<Real2Range> wordBBoxes = createExtendedBoxesW(wordList, sideOrientation.getOtherOrientation());
		Real2Range.format(wordBBoxes, 3);
		List<SVGXTreeNode> childlessNodeList = ensureChildlessNodeList();
		for (SVGXTreeNode node : childlessNodeList) {
			LOG.trace(node.getBoundingBox());
		}
		for (Word word : wordList) {
			LOG.trace(word+" / "+word.getBoundingBox().format(3));
		}
		for (int i = 0; i < ensureChildlessNodeList().size(); i++) {
			Real2Range nodeBox = childlessBBoxes.get(i);
			for (int j = 0; j < wordBBoxes.size(); j++) {
				Real2Range textBox = wordBBoxes.get(j);
				Real2Range inter = nodeBox.intersectionWith(textBox);
				if (inter != null) {
					Word word = wordList.get(j);
					childlessNodeList.get(i).addClosestWord(SVGXTree.OTU, word);
				}
			}
		}
	}
	
	private void createWordList() {
		GeometryBuilder geometryBuilder = new GeometryBuilder(parentSVGElement);
		wordList = geometryBuilder.getWordList();
		for (Word word : wordList) {
			LOG.trace("word: "+word);
		}
	}

	public List<SVGXTreeNode> ensureChildlessNodeList() {
		if (childlessNodeList == null) {
			this.childlessNodeList = new ArrayList<SVGXTreeNode>();
			for (SVGXTreeNode node : getNodeList()) {
				if (node.childNodeList == null || node.childNodeList.size() == 0) {
					childlessNodeList.add(node);
				}
			}
		}
		return childlessNodeList;
	}
	
	public void setSelectedIslandIndex(int islandIndex) {
		this.selectedIslandIndex = islandIndex;
	}



}

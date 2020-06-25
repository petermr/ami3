package org.contentmine.ami.plugins.phylotree.nexml;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.eucl.xml.XMLUtil;

public class NexmlNEXML extends NexmlElement {

	private final static Logger LOG = LogManager.getLogger(NexmlNEXML.class);
public final static String TAG = "nexml";
	private List<NexmlNode> rootList;
	private List<NexmlTree> treeList;

	/** constructor.
	 * 
	 */
	public NexmlNEXML() {
		super(TAG);
		this.addNamespaceDeclaration(NEX, NEXML_NS);
		this.addNamespaceDeclaration(XSI, XSI_NS);
	}

	public String createNewick() {
		StringBuilder sb = new StringBuilder();
		List<NexmlTree> treeList = getTreeList();
		for (NexmlTree tree : treeList) {
			sb.append(tree.getNewick());
		}
		sb.append(";");
		return sb.toString();
	}

	private void splitTrees() {
		List<NexmlTree>  treeList = getTreeList();
		for (NexmlTree nexmlTree : treeList) {
			List<NexmlNode> rootNodes = nexmlTree.getRootList();
			if (rootNodes.size() > 1) {
				LOG.error("splitTrees not yet written");
			}
		}
	}

	private List<NexmlTree> getTreeList() {
		NexmlTrees nexmlTrees = getTreesElement();
		treeList = nexmlTrees.getOrCreateTreeList();
		return treeList;
	}

	public NexmlTrees getTreesElement() {
		List<Element> elementList = XMLUtil.getQueryElements(this, "./*[local-name()='trees']");
		return elementList.size() != 1 ? null : (NexmlTrees) elementList.get(0);
	}

	void buildTrees() {
		getTreeList();
		for (NexmlTree tree : treeList) {
			tree.buildTree();
		}
	}

	public NexmlOtus getSingleOtusElement() {
		return (NexmlOtus) XMLUtil.getSingleElement(this, "*[local-name()='"+NexmlOtus.TAG+"']");
	}

	public NexmlTree getSingleTree() {
		NexmlTrees nexmlTrees = getTreesElement();
		return (nexmlTrees == null) ? null : (nexmlTrees.size() != 1 ? null : nexmlTrees.get(0)); 
	}
	
	public SVGElement createSVG() {
		NexmlTrees trees = getTreesElement();
		if (trees != null) {
			SVGSVG svg = new SVGSVG();
			svg.appendChild(trees.createSVG());
			return svg;
		}
		return null;
	}

	public void addOtus(NexmlOtus otus) {
		if (this.getSingleOtusElement() == null) {
			this.appendChild(otus);
		} else {
			LOG.debug("already has a otus");
		}
	}

	public void addTrees(NexmlTrees trees) {
		if (this.getTreesElement() == null) {
			this.appendChild(trees);
		} else {
			LOG.error("Cannot add 2 treesElement");
		}
	}

	public NexmlNode getNodeById(String id) {
		List<Element> nodes = XMLUtil.getQueryElements(this, ".//*[local-name()='node' and @id='"+id+"']");
		return (nodes == null || nodes.size() != 1) ? null : (NexmlNode) nodes.get(0);
	}

	public void deleteTipAndElideIfParentHasSingletonChild(NexmlNode node) {
		if (node != null) {
			NexmlNode parent = node.getParentNexmlNode();
			if (parent != null) {
				deleteTip(node);
				elideIfHasSingletonChild(parent);
			} else {
				throw new RuntimeException("NULL parent node for: "+node.getOtuRef()+"; not deleted");
			}
		}
	}
	
	private void elideIfHasSingletonChild(NexmlNode node) {
		List<NexmlNode> childNodes = node.getNexmlChildNodes();
		if (childNodes.size() == 1) {
			NexmlNode child = childNodes.get(0);
			NexmlNode parent = node.getParentNexmlNode();
			if (parent != null) { // not root node
				NexmlEdge parentEdge = getEdge(parent, node);
				if (parentEdge == null) {
					throw new RuntimeException("null parentEdge in elideIfHasSingletonChild");
				}
				NexmlEdge childEdge = getEdge(node, child);
				if (childEdge == null) {
					throw new RuntimeException("null childEdge in elideIfHasSingletonChild");
				}
				parentEdge.detach();
				parent.removeNexmlChild(node);
				parent.addChildNode(child);
				child.setParentNexmlNode(parent);
				childEdge.setSource(parent.getId());
				node.detach();
			}
		}
	}


	private void deleteTip(NexmlNode node) {
		List<NexmlNode> childNodes = node.getNexmlChildNodes();
		if (childNodes.size() != 0) {
			throw new RuntimeException("Not a tip (has childNodes "+childNodes.size()+"): "+node);
		} else {
			NexmlNode parent = node.getParentNexmlNode();
			if (parent != null) {
				NexmlEdge edge = this.getEdge(parent, node);
				edge.detach();
				parent.removeNexmlChild(node);
				node.detach();
			} else {
				throw new RuntimeException("Null parent: "+node);
			}
		}
	}

	/** assumes edges have source and target.
	 * 
	 * @param parent
	 * @param node
	 * @return
	 */
	private NexmlEdge getEdge(NexmlNode parent, NexmlNode node) {
		String sourceId = parent.getId();
		String targetId = node.getId();
		List<NexmlEdge> edgeList = this.getNexmlEdgeList();
		for (NexmlEdge edge : edgeList) {
			if (sourceId.equals(edge.getSourceId()) && targetId.equals(edge.getTargetId())) {
				return edge;
			}
		}
		return null;
	}

	private List<NexmlEdge> getNexmlEdgeList() {
		List<Element> elements = XMLUtil.getQueryElements(this, ".//*[local-name()='edge']");
		List<NexmlEdge> edgeList = new ArrayList<NexmlEdge>();
		for (Element element : elements) {
			edgeList.add((NexmlEdge)element);
		}
		return edgeList;
	}

	public List<NexmlNode> findTipsWithEmptyOtus() {
		List<NexmlNode> tips = findOtuRefTips();
		List<NexmlNode> otuRefsWithEmptyTips = new ArrayList<NexmlNode>();
		for (NexmlNode node : tips) {
			NexmlOtu otu = node.getOtuWithXPath();
			if (otu == null) {
			} else if (otu.getValue() == null || otu.getValue().trim().length() == 0) {
				otuRefsWithEmptyTips.add(node);
			} else {
			}
		}
		return otuRefsWithEmptyTips;
	}

	private List<NexmlNode> findOtuRefTips() {
		List<Element> elements = XMLUtil.getQueryElements(this, ".//*[local-name()='node' and @otu]");
		List<NexmlNode> nexmlNodeList = new ArrayList<NexmlNode>();
		for (Element element : elements) {
			nexmlNodeList.add((NexmlNode) element);
		}
		return nexmlNodeList;
	}


}

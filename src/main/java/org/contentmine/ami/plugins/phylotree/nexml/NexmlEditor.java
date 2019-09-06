package org.contentmine.ami.plugins.phylotree.nexml;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** edits Nexml Files
 * 
 * @author pm286
 *
 */
public class NexmlEditor {

	private final static Logger LOG = Logger.getLogger(NexmlEditor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private NexmlNEXML nexml;
	private NexmlTrees trees;
	private NexmlTree firstTree;
	private List<NexmlNode> nodeList;
	private List<NexmlNode> nodesWithParent;
	private List<NexmlNode> nodesWithChildNodes;

	public NexmlEditor() {
	}
	
	public NexmlEditor(NexmlNEXML nexml) {
		this.setNexml(nexml);
	}
	
	public void setNexml(NexmlNEXML nexml) {
		this.nexml = nexml;
		ensureNodeList();
	}

	private void ensureTrees() {
		if (trees == null) {
			trees = nexml.getTreesElement();
		}
	}
	
	private void ensureFirstTree() {
		ensureTrees();
		if (firstTree == null) {
			firstTree = trees.getFirstTree();
		}
	}

	private void ensureNodeList() {
		ensureFirstTree();
		firstTree.getNodeListAndMap();
		nodeList = firstTree.getNodeList();
	}
	
	public List<NexmlNode> getNodesWithChildren() {
		List<NexmlNode> nodesWithChildren = new ArrayList<NexmlNode>();
		
		return nodesWithChildren;
	}
	
	public List<NexmlNode> getNodesWithParents() {
		nodesWithParent = new ArrayList<NexmlNode>();
		for (NexmlNode node : nodeList) {
			if (node.getParentNexmlNode() != null) {
				nodesWithParent.add(node);
			}
			
		}
		return nodesWithParent;
	}
	
	public List<NexmlNode> getNodesWithChildNodes() {
		nodesWithChildNodes = new ArrayList<NexmlNode>();
		for (NexmlNode node : nodeList) {
			if (node.getNexmlChildNodes().size() > 0) {
				nodesWithChildNodes.add(node);
			}
		}
		return nodesWithParent;
	}
}

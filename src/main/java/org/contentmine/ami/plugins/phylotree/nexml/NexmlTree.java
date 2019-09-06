package org.contentmine.ami.plugins.phylotree.nexml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.eucl.xml.XMLUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class NexmlTree extends NexmlElement {

	private static final String TYPE = "type";
	private final static Logger LOG = Logger.getLogger(NexmlTree.class);
	public final static String TAG = "tree";
	
	private List<NexmlNode> nodeList;
	private List<NexmlEdge> edgeList;
	private Map<String, NexmlNode> nodeByIdMap;
	private Multimap<String, NexmlEdge> edgeBySourceIdMap;
	private Multimap<String, NexmlEdge> edgeByTargetIdMap;
	private NexmlNode rootNexmlNode;
	private List<NexmlNode> rootList;
	private Set<NexmlNode> unusedNodeSet;
	private Set<NexmlNode> tipSet;
	private HashSet<NexmlNode> branchNodeSet;
	private List<NexmlNode> tipNodeList;
	private Map<Int2, NexmlNode> tipByCoordMap;

	/** constructor.
	 * 
	 */
	public NexmlTree() {
		super(TAG);
	}

	public void setType(String type) {
		this.addAttribute(new Attribute(XSI+":"+TYPE, XSI_NS, type));
	}

	public List<NexmlNode> getNodeListAndMap() {
		if (nodeList == null) {
			nodeList = new ArrayList<NexmlNode>();
			nodeByIdMap = new HashMap<String, NexmlNode>();
			List<Element> nodeElements = XMLUtil.getQueryElements(this, "./*[local-name()='node']");
			unusedNodeSet = new HashSet<NexmlNode>();
			for (Element node : nodeElements) {
				NexmlNode nexmlNode = (NexmlNode) node;
				nodeList.add(nexmlNode);
				nodeByIdMap.put(nexmlNode.getId(), nexmlNode);
				unusedNodeSet.add(nexmlNode);
			}
		}
		return nodeList;
	}
	
	public List<NexmlEdge> getEdgeListAndMaps() {
		if (edgeList == null) {
			edgeList = new ArrayList<NexmlEdge>();
			List<Element> edgeElements = XMLUtil.getQueryElements(this, "./*[local-name()='edge']");
			edgeByTargetIdMap = ArrayListMultimap.create();
			edgeBySourceIdMap = ArrayListMultimap.create();
			for (Element edge : edgeElements) {
				NexmlEdge nexmlEdge = (NexmlEdge) edge;
				edgeList.add(nexmlEdge);
				edgeBySourceIdMap.put(nexmlEdge.getSourceId(), nexmlEdge);
				edgeByTargetIdMap.put(nexmlEdge.getTargetId(), nexmlEdge);
			}
		}
		return edgeList;
	}
	
	private void addChildEdges(NexmlNode node) {
		if (!tipSet.contains(node) && !branchNodeSet.contains(node)) {
			String nodeId = node.getId();
			LOG.trace("adding id "+nodeId);
			List<NexmlEdge> childEdges = getSourceEdges(nodeId);
			if (childEdges.size() == 0) {
				tipSet.add(node);
			} else {
				branchNodeSet.add(node);
			}
			addChildEdges(node, childEdges);
		}
	}

	private void addChildEdges(NexmlNode node, List<NexmlEdge> childEdges) {
		for (NexmlEdge childEdge : childEdges) {
			String childId = childEdge.getTargetId();
			NexmlNode childNode = getNode(childId);
			childNode.setParentNexmlNode(node);
			node.addChildNexmlNode(childNode);
			this.addChildEdges(childNode);
		}
	}

	NexmlNode getNode(String id) {
		getNodeListAndMap();
		return nodeByIdMap.get(id);
	}

	List<NexmlEdge> getSourceEdges(String sourceId) {
		List<NexmlEdge> edgeList = new ArrayList<NexmlEdge>(edgeBySourceIdMap.get(sourceId));
		LOG.trace("E> "+sourceId+"; "+edgeList);
		return new ArrayList<NexmlEdge>(edgeBySourceIdMap.get(sourceId));
	}

	List<NexmlEdge> getTargetEdges(String target) {
		return new ArrayList<NexmlEdge>(edgeByTargetIdMap.get(target));
	}

	/** checks relationship of nodes and edges and adds parent/child to nodes.
	 * 
	 */
	public void buildTree() {
		getNodeListAndMap();
		getEdgeListAndMaps();
		tipSet = new HashSet<NexmlNode>();
		branchNodeSet = new HashSet<NexmlNode>();
		while(unusedNodeSet.size() > 0) {
			NexmlNode nextNode = unusedNodeSet.iterator().next();
			unusedNodeSet.remove(nextNode);
			List<NexmlEdge> edges = getSourceEdges(nextNode.getId());
			if (edges.size() > 0) {
				addChildEdges(nextNode);
			}
		}
//		checkNodeParents();
		LOG.trace(tipSet);
		LOG.trace(branchNodeSet);
		List<NexmlNode> rootNodes = getRootList();
		if (rootNodes.size() == 0) {
			LOG.error("NO Root nodes");
		} else if (rootNodes.size() == 1) {
			this.rootNexmlNode = rootNodes.get(0);
		} else {
			LOG.warn("Cannot process multiple roots: "+rootNodes.size());
		}
	}
	
	public String getNewick() {
		getRootNode();
		return rootNexmlNode == null ? null : rootNexmlNode.getNewick();
	}

	public NexmlNode getRootNode() {
		if (rootNexmlNode == null) {
			DefaultArgProcessor.CM_LOG.error("No root Node ... looking ");
			getNodeListAndMap();
			for (NexmlNode nexmlNode : nodeList) {
				if (NexmlFactory.TRUE.equals(nexmlNode.getRootValue())) {
					rootNexmlNode = nexmlNode;
				}
			}
		}
		return rootNexmlNode;
	}
	
	public void setRootNode(NexmlNode rootNode) {
		this.rootNexmlNode = rootNode;
	}

	// FIXME
	public List<NexmlNode> getRootList() {
		if (rootList == null) {
			rootList = new ArrayList<NexmlNode>();
			for (NexmlNode node : nodeList) {
				if (node.getParentNexmlNode() == null) {
					NexmlNode parentNode = addParent(node);
					if (parentNode == null /*|| true*/) {
						rootList.add(node);
					}
				}
			}
			LOG.trace("rootList "+rootList.size());
		}
		return rootList;
	}

	/** doesn't seem to find anything */
	private NexmlNode addParent(NexmlNode node) {
		String nodeId = node.getId();
		LOG.trace("addParent..."+nodeId);
		for (NexmlEdge edge : edgeList) {
			String sourceId = edge.getSourceId();
			String targetId = edge.getTargetId();
			if (nodeId.equals(targetId)) {
				NexmlNode parentNode = getNode(sourceId);
//				LOG.debug("found parent"+parentNode);
				node.setParentNexmlNode(parentNode);
				return parentNode;
			}
		}
		return null;
	}

	public List<NexmlNode> getOrCreateTipNodeList() {
		if (tipNodeList == null) {
			getNodeListAndMap();
			tipNodeList = new ArrayList<NexmlNode>();
			for (NexmlNode nexmlNode : nodeList) {
				if (nexmlNode.getOtuRef() != null) {
					tipNodeList.add(nexmlNode);
				}
			}
		}
		return tipNodeList;
	}
	
	public List<NexmlNode> getOrCreateNonTipNodeList() {
		getOrCreateTipNodeList();
		List<NexmlNode> nodeListCopy = new ArrayList<NexmlNode>(this.getNodeListAndMap());
		nodeListCopy.removeAll(tipNodeList);
		return nodeListCopy;
	}

	public Map<Int2, NexmlNode> getTipByCoordMap() {
		if (tipByCoordMap == null) {
			tipByCoordMap = new HashMap<Int2, NexmlNode>();
			getOrCreateTipNodeList();
			for (NexmlNode tipNode : tipNodeList) {
				Int2 xy2 = tipNode.getInt2();
				if (xy2 != null) {
					tipByCoordMap.put(xy2, tipNode);
				}
			}
		}
		return tipByCoordMap;
	}

	public SVGElement createSVG() {
		SVGG g = new SVGG();
		getNodeListAndMap();
		for (NexmlNode node : nodeList) {
			g.appendChild(node.createSVG());
		}
		getEdgeListAndMaps();
		for (NexmlEdge edge : edgeList) {
			if (edge != null) {
				SVGElement svg = edge.createSVG();
				if (svg != null) {
					g.appendChild(svg);
				}
			}
		}
		return g;
	}

	public void addNode(NexmlNode node) {
		if (node != null) {
			String id = node.getId();
			if (id != null) {
				if (getNode(id) == null) {
					this.appendChild(node);
				} else {
					DefaultArgProcessor.CM_LOG.error("Already a node with id: "+id);
				}
			}
		}
	}

	public void addEdge(NexmlEdge edge12) {
		getEdgeListAndMaps();
		this.appendChild(edge12);
		
	}

	public List<NexmlNode> getNodeList() {
		return nodeList;
	}
	
}

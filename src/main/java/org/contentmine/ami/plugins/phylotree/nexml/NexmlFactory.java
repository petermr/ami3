package org.contentmine.ami.plugins.phylotree.nexml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.diagramAnalyzer.DiagramTree;
import org.contentmine.cproject.args.DefaultArgProcessor;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.image.pixel.PixelEdge;
import org.contentmine.image.pixel.PixelEdgeList;
import org.contentmine.image.pixel.PixelNode;
import org.contentmine.image.pixel.PixelNodeList;

/** creates NexmlTrees initially from diagram trees
 * 
 * @author pm286
 *
 */
public class NexmlFactory {

	
	private static final Logger LOG = LogManager.getLogger(NexmlFactory.class);
static final String TRUE = "true";
	private static final String TREE_ID = "T";
	private static final String OTU = "otu";

	private NexmlNEXML nexmlNEXML;
	private NexmlOtus nexmlOtus;
	private NexmlTrees nexmlTrees;
	private Map<PixelNode, NexmlNode> pixelNodeToNexmlNodeMap;
	private Map<String, NexmlNode> idToNexmlNodeMap;
	private PixelNode rootPixelNode;
	private NexmlNode rootNexmlNode;
	private List<NexmlEdge> nexmlEdgeList;
	private NexmlTree nexmlTree;
	private int recursionCounter;
	private Set<NexmlEdge> processedEdges;
	private DefaultArgProcessor argProcessor;

	public NexmlFactory() {
		this(new DefaultArgProcessor());
	}
	
	public NexmlFactory(DefaultArgProcessor argProcessor) {
		pixelNodeToNexmlNodeMap = new HashMap<PixelNode, NexmlNode>();
		idToNexmlNodeMap = new HashMap<String, NexmlNode>();
		nexmlEdgeList = new ArrayList<NexmlEdge>();
		this.argProcessor = argProcessor;
	}
	
	/**
<nexml xmlns="http://www.nexml.org/2009" xmlns:nex="http://www.nexml.org/2009"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:svgx="http://www.xml-cml.org/schema/svgx"
	id="tree.null">
	<otus id="tax1" label="RootTaxaBlock">
		<otu id="t1" />
		...
		<otu id="t5" />
	</otus>
	<trees label="TreesBlockFromXML" id="Trees" otus="tax1">
		<tree id="tree1" label="tree1" xsi:type="FloatTree">
			<node id="N1" otu="t1" label="Luscinia" x="397.018" y="102.384"/>
			...
			<node id="N5" otu="t5" label="Sturnus" x="394.055" y="133.774"/>
			<node id="N6" label="N6" x="318.938" y="129.848"/>
			<node id="N7" label="N7" root="true" x="290.932" y="121.022"/>
			<node id="N8" label="N8" x="303.421" y="112.197"/>
			<node id="N9" label="N9" x="342.88" y="106.311"/>
			<edge id="polyline.14" label="polyline.14" source="N9" target="N1"
				svgx:length="54.138" />
			<edge id="polyline.16" label="polyline.16" source="N9" target="N2"
				svgx:length="66.062" />
				...
			<edge id="polyline.15" label="polyline.15" source="N8" target="N9"
				svgx:length="39.459" />
		</tree>
	</trees>
</nexml>
	 * @return
	 */
	public NexmlNEXML getOrCreateNexmlNEXML() {
		if (nexmlNEXML == null) {
			nexmlNEXML = new NexmlNEXML();
			nexmlOtus = new NexmlOtus();
			nexmlOtus.setLabel("RootTaxaBlock");
			nexmlNEXML.appendChild(nexmlOtus);
			nexmlTrees = new NexmlTrees();
			nexmlNEXML.appendChild(nexmlTrees);
		}
		return nexmlNEXML;
	}
	
	public NexmlTree createAndAddNexmlTree(DiagramTree diagramTree) {
		getOrCreateNexmlNEXML();
		nexmlTree = new NexmlTree();
		nexmlTrees.appendChild(nexmlTree);
		String treeId = TREE_ID+nexmlTrees.getOrCreateTreeList().size();
		nexmlTree.setId(treeId);
		PixelNodeList pixelNodeList = diagramTree.getGraph().getOrCreateNodeList();
		if (rootPixelNode == null) {
			rootPixelNode = diagramTree.getGraph().getRootPixelNode();
			if (rootPixelNode == null) {
				argProcessor.TREE_LOG().error("NO ROOT NODE");
			}
		}
		addNodes(pixelNodeList, rootPixelNode);
		PixelEdgeList pixelEdgeList = diagramTree.getGraph().getOrCreateEdgeList();
		addEdges(nexmlTree, pixelEdgeList);
		checkEdges();
		addEdgesToNodes();
		if (rootNexmlNode == null) {
			argProcessor.TREE_LOG().error("NULL ROOT NODE");
		} else {
			recursionCounter = nexmlEdgeList.size()+50;
			processedEdges = new HashSet<NexmlEdge>();
			addChildrenAndDirectionality(rootNexmlNode);
		}
		return nexmlTree;
	}

	private void checkEdges() {
		Set<String> targetSourceSet = new HashSet<String>();
		for (int i = 0; i < nexmlEdgeList.size(); i++) {
			NexmlEdge edge = nexmlEdgeList.get(i);
			if (edge.getId() == null) {
				edge.setId("e."+i);
			}
			String targetId = edge.getTargetId();
			String sourceId = edge.getSourceId();
			if (targetId == null || sourceId == null) continue;
			int compare = targetId.compareTo(sourceId);
			if (compare == 0) {
				argProcessor.TREE_LOG().error("edge target "+targetId+" == source "+sourceId);
				continue;
			} else if (compare < 0) {
				targetId = edge.getSourceId();
				sourceId = edge.getTargetId();
			}
			String targetSourceIds = targetId+"=>"+sourceId;
			LOG.trace("ts "+targetSourceIds);
			if(targetSourceSet.contains(targetSourceIds)) {
				argProcessor.TREE_LOG().error("duplicate edge: "+edge);
			} else {
				targetSourceSet.add(targetSourceIds);
			}
		}
	}

	private void addNodes(PixelNodeList pixelNodeList,
			PixelNode rootNode) {
		for (PixelNode pixelNode : pixelNodeList) {
			NexmlNode nexmlNode = null;
			PixelEdgeList edgeList = pixelNode.getEdges();
			if (edgeList.size() == 1) {
				nexmlNode = addTerminalNodeAsOtu(pixelNode);
			} else if (edgeList.size() == 2) {
				LOG.trace("node2 remove me??: "+pixelNode.toString()+"; "+pixelNode.getEdges().size());
			} else if (edgeList.size() == 3) {
				nexmlNode = createAndAddNexmlNode(pixelNode);
				LOG.trace("node3: "+pixelNode.toString()+"; "+pixelNode.getEdges().size());
			} else if (edgeList.size() >= 4) {
				nexmlNode = createAndAddNexmlNode(pixelNode);
				LOG.trace("node >= 4 connections: "+pixelNode.toString()+"; "+pixelNode.getEdges().size());
			} else {
				// ???
			}
			if (nexmlNode != null) {
				pixelNodeToNexmlNodeMap.put(pixelNode, nexmlNode);
				String id = nexmlNode.getId();
				if (id != null) {
					idToNexmlNodeMap.put(id, nexmlNode);
				} else {
					argProcessor.TREE_LOG().debug("null id for "+nexmlNode);
				}
			}
			// have to compare coords as Ids, etc have changed
			// have reduced the offset to zero.
			if (nexmlNode != null) {
				Int2 xy0 = rootPixelNode.getInt2();
				Int2 xy1 = pixelNode.getInt2();
				LOG.trace("coords "+xy0+"; "+xy1);
				if (Int2.isEqual(xy0, xy1) || kludgeRoot()) {
					nexmlNode.setRoot(TRUE);
					LOG.trace("ROOT TRUE "+nexmlNode);
					this.rootNexmlNode = nexmlNode;
				} else {
					LOG.trace("failed to create ROOT: "+xy0+"//"+xy1);
				}
			}
		}
		
//		pixelNodeList.sort();
	}

	// this seems to work partially but we can't afford the time to check it
	private boolean kludgeRoot() {
//		LOG.debug("KLUDGED TREE ROOT, check");
//		return true;
		return false;
	}

	private void addEdges(NexmlTree nexmlTree, PixelEdgeList pixelEdgeList) {
		LOG.trace("add pixelEdges: "+pixelEdgeList.size());
		for (PixelEdge pixelEdge : pixelEdgeList) {
			LOG.trace("edge: "+pixelEdge.toString());
			if (pixelEdge.getNodes().get(0) == null || pixelEdge.getNodes().get(1) == null) {
				argProcessor.TREE_LOG().warn("null node in edge: "+pixelEdge );
			}
			NexmlEdge nexmlEdge = createAndAddNexmlEdge(nexmlTree, pixelEdge);
			nexmlEdgeList.add(nexmlEdge);
		}
	}
	
	private void addEdgesToNodes() {
		for (NexmlEdge nexmlEdge : nexmlEdgeList) {
			addEdgeToEndsNodes(nexmlEdge);
		}
	}

	private void addEdgeToEndsNodes(NexmlEdge nexmlEdge) {
		NexmlNode node0 = nexmlEdge.getNexmlNode(0);
		node0.addNexmlEdge(nexmlEdge);
		NexmlNode node1 = nexmlEdge.getNexmlNode(1);
		if (node1 != null) {
			node1.addNexmlEdge(nexmlEdge);
		}
	}

	private void addChildrenAndDirectionality(NexmlNode parentNexmlNode) {
		if (recursionCounter-- <= 0) {
			throw new RuntimeException("Too much recursion");
		}
		LOG.trace("recursionCounter: "+recursionCounter);
		if (parentNexmlNode == null) {
			LOG.error("cannot find rootNexmlNode");
			return;
		}
		NexmlNode grandParentNode = parentNexmlNode.getParentNexmlNode();
		if (grandParentNode != null) {
			LOG.error(parentNexmlNode.getId()+" parent already has parent: "+grandParentNode);
			return;
		}
		String parentId = parentNexmlNode.getId();
		if (parentId == null) {
			LOG.error("parent has no Id");
			return;
		}
		for (NexmlEdge nexmlEdge : parentNexmlNode.nexmlEdges) {
			if (processedEdges.contains(nexmlEdge)) {
				LOG.trace("skipping processed "+nexmlEdge);
				continue;
			} else {
				processedEdges.add(nexmlEdge);
			}
			String edgeSourceId = nexmlEdge.getSourceId();
			String edgeTargetId = nexmlEdge.getTargetId();
			LOG.trace(edgeSourceId+"=>"+edgeTargetId+": "+nexmlEdge.getId()+" :: "+parentNexmlNode.getId());
			if (parentId.equals(edgeTargetId)) {
				grandParentNode = idToNexmlNodeMap.get(edgeSourceId);
			} else if (parentId.equals(edgeSourceId)) {
				grandParentNode = idToNexmlNodeMap.get(edgeTargetId);
				LOG.trace("swapped source/target");
			} else {
				throw new RuntimeException("bad edge");
			}
			if (grandParentNode == null) {
				LOG.warn("null grandParent");
			} else {
				parentNexmlNode.setParentNexmlNode(grandParentNode);
				NexmlNode childNexmlNode = nexmlEdge.getOtherNode(parentNexmlNode);
				if (childNexmlNode != null) {
					nexmlEdge.setSource(parentId);
					nexmlEdge.setTarget(childNexmlNode.getId());
					addChildrenAndDirectionality(childNexmlNode);
					parentNexmlNode.addChildNode(childNexmlNode);
					childNexmlNode.setParentNexmlNode(parentNexmlNode);
				} else {
					throw new RuntimeException("null child node");
				}
			}
		}
	}
	private NexmlEdge createAndAddNexmlEdge(NexmlTree nexmlTree, PixelEdge pixelEdge) {
		NexmlNode nexmlNode0 = pixelNodeToNexmlNodeMap.get(pixelEdge.getNodes().get(0));
		NexmlNode nexmlNode1 = pixelNodeToNexmlNodeMap.get(pixelEdge.getNodes().get(1));
		NexmlEdge nexmlEdge = new NexmlEdge(nexmlNode0, nexmlNode1);
		nexmlTree.appendChild(nexmlEdge);
		return nexmlEdge;
	}

	private NexmlNode addTerminalNodeAsOtu(PixelNode pixelNode) {
		NexmlNode nexmlNode = createAndAddNexmlNode(pixelNode);
		NexmlOtu otu = new NexmlOtu();
		nexmlOtus.appendChild(otu);
		String otuId = OTU+nexmlOtus.getChildCount();
		otu.setId(otuId);
		nexmlNode.setOtuRef(otuId);
		return nexmlNode;
	}

	private NexmlNode createAndAddNexmlNode(PixelNode pixelNode) {
		NexmlNode nexmlNode = new NexmlNode(nexmlTree);
		nexmlTree.appendChild(nexmlNode);
		String nodeId = "N"+nexmlTree.getId()+"."+nexmlTree.getChildCount();
		nexmlNode.setId(nodeId);
		nexmlNode.setLabel(nodeId); // could be overwritten later
		nexmlNode.setXY2(pixelNode.getReal2());
		return nexmlNode;
	}

	public void setRootPixelNode(PixelNode rootPixelNode) {
		this.rootPixelNode = rootPixelNode;
		LOG.trace("set rootPixelNode: "+rootPixelNode);
	}
}

package org.contentmine.ami.plugins.phylotree.nexml;


import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlEdge;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNEXML;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlNode;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtu;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlOtus;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlTree;
import org.contentmine.ami.plugins.phylotree.nexml.NexmlTrees;
import org.junit.Assert;
import org.junit.Test;

/** tests to build and modify tree
 * 
 * @author pm286
 *
 *
 *<nexml xmlns="http://www.nexml.org/2009" xmlns:nex="http://www.nexml.org/2009" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
 <otus label="RootTaxaBlock">
  <otu id="otu1">Pyramidobacter piscolens W5455T (DU723069)</otu>
  <otu id="otu2">Desulfovibrio desu/furicans</otu>
  ...
  <otu id="otu27">Chlamydia trachomatis D/UW-3/CX (AAC68296)</otu>
 </otus>
 <trees>
  <tree id="T1">
   <node id="NT1.1" label="NT1.1" x="38.0" y="230.0"/>
   <node id="NT1.2" x="42.0" y="47.0" label="55"/>
...
   <node id="NT1.51" label="NT1.51" x="581.0" y="601.0" otu="otu27"/>
   <node id="NT1.52" label="NT1.52" x="31.0" y="138.0" root="true"/>
   <edge source="NT1.5" target="NT1.27"/>
   <edge source="NT1.1" target="NT1.28"/>
  ...
   <edge source="NT1.21" target="NT1.39"/>
  </tree>
 </trees>
</nexml>

 */
public class NexmlTest {

	private final static Logger LOG = Logger.getLogger(NexmlTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testBuildTree() {
		NexmlNEXML nexml1 = makeTree1();
		Assert.assertEquals("xml", "<nexml xmlns=\"http://www.nexml.org/2009\" xmlns:nex=\"http://www.nexml.org/2009\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><otus><otu id=\"otu4\" /><otu id=\"otu5\" /><otu id=\"otu6\" /><otu id=\"otu7\" /></otus><trees><tree><node id=\"node1\" /><node id=\"node2\" /><node id=\"node3\" /><node id=\"node4\" otu=\"otu4\" /><node id=\"node5\" otu=\"otu5\" /><node id=\"node6\" otu=\"otu6\" /><node id=\"node7\" otu=\"otu7\" /><edge source=\"node1\" target=\"node2\" id=\"edge12\" /><edge source=\"node1\" target=\"node3\" id=\"edge13\" /><edge source=\"node2\" target=\"node4\" id=\"edge24\" /><edge source=\"node2\" target=\"node5\" id=\"edge25\" /><edge source=\"node3\" target=\"node6\" id=\"edge36\" /><edge source=\"node3\" target=\"node7\" id=\"edge37\" /></tree></trees></nexml>", nexml1.toXML());
		Assert.assertEquals("newick", "((node4,node5)node2,(node6,node7)node3)node1;", nexml1.createNewick());
	}

	@Test
	public void testDeleteTipAndElideNodesWithSingletonChildren() {
		NexmlNEXML nexml1 = makeTree1();
		NexmlNode node7 = nexml1.getNodeById("node7");
		Assert.assertNotNull(node7);
		nexml1.deleteTipAndElideIfParentHasSingletonChild(node7);
		Assert.assertEquals("xml", ""
				+ "<nexml xmlns=\"http://www.nexml.org/2009\" xmlns:nex=\"http://www.nexml.org/2009\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><otus><otu id=\"otu4\" /><otu id=\"otu5\" /><otu id=\"otu6\" /><otu id=\"otu7\" /></otus><trees><tree><node id=\"node1\" /><node id=\"node2\" /><node id=\"node4\" otu=\"otu4\" /><node id=\"node5\" otu=\"otu5\" /><node id=\"node6\" otu=\"otu6\" /><edge source=\"node1\" target=\"node2\" id=\"edge12\" /><edge source=\"node2\" target=\"node4\" id=\"edge24\" /><edge source=\"node2\" target=\"node5\" id=\"edge25\" /><edge target=\"node6\" id=\"edge36\" source=\"node1\" /></tree></trees></nexml>",
					nexml1.toXML());
		Assert.assertEquals("newick", "((node4,node5)node2,node6)node1;", nexml1.createNewick());
	}

	/**
	 * Nodes on different branches
	 */
	@Test
	public void testDelete2TipsOnDifferentBranches() {
		NexmlNEXML nexml1 = makeTree1();
		NexmlNode node7 = nexml1.getNodeById("node7");
		nexml1.deleteTipAndElideIfParentHasSingletonChild(node7);
		NexmlNode node5 = nexml1.getNodeById("node5");
		nexml1.deleteTipAndElideIfParentHasSingletonChild(node5);
		Assert.assertEquals("xml", ""
				+ "<nexml xmlns=\"http://www.nexml.org/2009\" xmlns:nex=\"http://www.nexml.org/2009\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><otus><otu id=\"otu4\" /><otu id=\"otu5\" /><otu id=\"otu6\" /><otu id=\"otu7\" /></otus><trees><tree><node id=\"node1\" /><node id=\"node4\" otu=\"otu4\" /><node id=\"node6\" otu=\"otu6\" /><edge target=\"node4\" id=\"edge24\" source=\"node1\" /><edge target=\"node6\" id=\"edge36\" source=\"node1\" /></tree></trees></nexml>",
					nexml1.toXML());
		Assert.assertEquals("newick", "(node6,node4)node1;", nexml1.createNewick());
	}

	/**
	 * Nodes on same branch
	 */
	@Test
	public void testDelete2TipSameBranch() {
		NexmlNEXML nexml1 = makeTree1();
		NexmlNode node7 = nexml1.getNodeById("node7");
		nexml1.deleteTipAndElideIfParentHasSingletonChild(node7);
		NexmlNode node6 = nexml1.getNodeById("node6");
		nexml1.deleteTipAndElideIfParentHasSingletonChild(node6);
		Assert.assertEquals("xml", ""
				+ "<nexml xmlns=\"http://www.nexml.org/2009\" xmlns:nex=\"http://www.nexml.org/2009\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><otus><otu id=\"otu4\" /><otu id=\"otu5\" /><otu id=\"otu6\" /><otu id=\"otu7\" /></otus><trees><tree><node id=\"node1\" /><node id=\"node2\" /><node id=\"node4\" otu=\"otu4\" /><node id=\"node5\" otu=\"otu5\" /><edge source=\"node1\" target=\"node2\" id=\"edge12\" /><edge source=\"node2\" target=\"node4\" id=\"edge24\" /><edge source=\"node2\" target=\"node5\" id=\"edge25\" /></tree></trees></nexml>",
					nexml1.toXML());
		Assert.assertEquals("newick", "((node4,node5)node2)node1;", nexml1.createNewick());
	}

	@Test
	public void testDeleteAllTips() {
		NexmlNEXML nexml1 = makeTree1();
		NexmlNode node7 = nexml1.getNodeById("node7");
		nexml1.deleteTipAndElideIfParentHasSingletonChild(node7);
		NexmlNode node6 = nexml1.getNodeById("node6");
		nexml1.deleteTipAndElideIfParentHasSingletonChild(node6);
		NexmlNode node5 = nexml1.getNodeById("node5");
		nexml1.deleteTipAndElideIfParentHasSingletonChild(node5);
		NexmlNode node4 = nexml1.getNodeById("node4");
		nexml1.deleteTipAndElideIfParentHasSingletonChild(node4);
		Assert.assertEquals("xml", ""
				+ "<nexml xmlns=\"http://www.nexml.org/2009\" xmlns:nex=\"http://www.nexml.org/2009\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><otus><otu id=\"otu4\" /><otu id=\"otu5\" /><otu id=\"otu6\" /><otu id=\"otu7\" /></otus><trees><tree><node id=\"node1\" /></tree></trees></nexml>",
					nexml1.toXML());
		Assert.assertEquals("newick", "node1;", nexml1.createNewick());
	}
	
	@Test
	public void findTipsWithEmptyOtus() {
		NexmlNEXML nexml1 = makeTree1();
		List<NexmlNode> emptyNodes = nexml1.findTipsWithEmptyOtus();
		Assert.assertEquals(4,  emptyNodes.size());
		NexmlOtu otu4 = nexml1.getSingleOtusElement().getOtuByIdWithXPath("otu4");
		otu4.appendChild("OTU4");
		emptyNodes = nexml1.findTipsWithEmptyOtus();
		Assert.assertEquals(3,  emptyNodes.size());
	}

	// ========================================
	
	private NexmlNEXML makeTree1() {
		NexmlNEXML nexml = new NexmlNEXML();
		NexmlOtus otus = new NexmlOtus();
		nexml.addOtus(otus);
		NexmlOtu otu4 = new NexmlOtu("otu4");
		otus.addOtu(otu4);
		NexmlOtu otu5 = new NexmlOtu("otu5");
		otus.addOtu(otu5);
		NexmlOtu otu6 = new NexmlOtu("otu6");
		otus.addOtu(otu6);
		NexmlOtu otu7 = new NexmlOtu("otu7");
		otus.addOtu(otu7);
		NexmlTrees trees = new NexmlTrees();
		nexml.addTrees(trees);
		NexmlTree tree = new NexmlTree();
		trees.addTree(tree);
		NexmlNode node1 = new NexmlNode("node1");
		tree.addNode(node1);
		tree.setRootNode(node1);
		NexmlNode node2 = new NexmlNode("node2");
		tree.addNode(node2);
		NexmlNode node3 = new NexmlNode("node3");
		tree.addNode(node3);
		NexmlNode node4 = new NexmlNode("node4");
		tree.addNode(node4);
		node4.setOtuRef(otu4.getId());
		NexmlNode node5 = new NexmlNode("node5");
		tree.addNode(node5);
		node5.setOtuRef(otu5.getId());
		NexmlNode node6 = new NexmlNode("node6");
		tree.addNode(node6);
		node6.setOtuRef(otu6.getId());
		NexmlNode node7 = new NexmlNode("node7");
		tree.addNode(node7);
		node7.setOtuRef(otu7.getId());
		NexmlEdge edge12 = new NexmlEdge("edge12", node1, node2);
		tree.addEdge(edge12);
		NexmlEdge edge13 = new NexmlEdge("edge13", node1, node3);
		tree.addEdge(edge13);
		NexmlEdge edge24 = new NexmlEdge("edge24", node2, node4);
		tree.addEdge(edge24);
		NexmlEdge edge25 = new NexmlEdge("edge25", node2, node5);
		tree.addEdge(edge25);
		NexmlEdge edge36 = new NexmlEdge("edge36", node3, node6);
		tree.addEdge(edge36);
		NexmlEdge edge37 = new NexmlEdge("edge37", node3, node7);
		tree.addEdge(edge37);
		return nexml;
	}
}

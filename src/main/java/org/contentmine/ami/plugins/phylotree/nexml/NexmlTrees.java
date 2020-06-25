package org.contentmine.ami.plugins.phylotree.nexml;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.eucl.xml.XMLUtil;

public class NexmlTrees extends NexmlElement {

	private final static Logger LOG = LogManager.getLogger(NexmlTrees.class);
public final static String TAG = "trees";
	private List<NexmlTree> treeList;
	private NexmlTree firstTree;

	/** constructor.
	 * 
	 */
	public NexmlTrees() {
		super(TAG);
	}

	public void setOtus(String otus) {
		this.addAttribute(new Attribute("otus", otus));
	}

	public List<NexmlTree> getOrCreateTreeList() {
		if (treeList == null) {
			List<Element> elementList = XMLUtil.getQueryElements(this, "./*[local-name()='tree']");
			treeList = new ArrayList<NexmlTree>();
			for (Element element : elementList) {
				treeList.add((NexmlTree) element);
			}
		}
		return treeList;
	}

	public int size() {
		getOrCreateTreeList();
		return treeList.size();
	}

	public NexmlTree get(int i) {
		getOrCreateTreeList();
		return treeList.get(i);
	}

	public SVGElement createSVG() {
		getOrCreateTreeList();
		SVGG g = new SVGG();
		for (NexmlTree tree : treeList) {
			g.appendChild(tree.createSVG());	
		}
		return g;
	}

	public void addTree(NexmlTree tree) {
		this.appendChild(tree);
	}

	public NexmlTree getFirstTree() {
		getOrCreateTreeList();
		firstTree = (treeList == null || treeList.size() == 0) ? null : treeList.get(0);
		return firstTree;
	}
	
}

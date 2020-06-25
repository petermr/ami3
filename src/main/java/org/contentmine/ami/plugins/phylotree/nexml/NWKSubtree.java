package org.contentmine.ami.plugins.phylotree.nexml;

import nu.xom.Element;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;

public class NWKSubtree {

	private static final Logger LOG = LogManager.getLogger(NWKSubtree.class);
private NWKBranchSet branchSet;
	private NWKLeaf leaf;

	/**
//      Subtree --> Name | "(" BranchSet ")" Name
PMR maybe:
      Subtree --> Leaf | "(" BranchSet ")" Leaf
      
	 * @param string
	 */
	public NWKSubtree(NWKBranchSet branchSet) {
		this.branchSet = branchSet;
	}

	/**
	public NWKSubtree(NWKName name) {
//		this.name = name;
	}
	*/

	public NWKSubtree(NWKLeaf leaf) {
		this.leaf = leaf;
	}

	public static NWKSubtree createSubtree(StringBuilder sb) {
		NWKSubtree subtree = null;
		if (!sb.toString().startsWith("(")) {
			NWKLeaf leaf = NWKLeaf.createLeaf(sb);
			if (leaf != null) {
				subtree = new NWKSubtree(leaf);
			}
		} else {
			int idx = Util.indexOfBalancedBracket('(', sb.toString());
			if (idx == -1) {
				throw new RuntimeException("unbalanced brackets");
			}
			NWKBranchSet branchSet = NWKBranchSet.createBranchSet(new StringBuilder(sb.substring(1, idx)));
			sb.delete(0, idx+1);
			if (branchSet != null) {
				subtree = new NWKSubtree(branchSet);
				// this might need to be NWKName??
				NWKLeaf leaf = NWKLeaf.createLeaf(sb);
				if (leaf != null) {
					subtree.setLeaf(leaf);
				}
			}
		}
		return subtree;
	}

	private void setLeaf(NWKLeaf leaf) {
		this.leaf = leaf;
	}
	
	public String toString() {
		String s = "";
		if (branchSet != null) {
			s += String.valueOf(branchSet);
		}
		if (leaf != null) {
			s += String.valueOf(leaf);
		}
		return s;
	}

	public void createNewick(StringBuilder sb) {
		if (branchSet != null) {
			branchSet.createNewick(sb);
		} else if (leaf != null) {
			leaf.createNewick(sb);
		}
	}

	public SVGG createSVGOld() {
		SVGG g = new SVGG();
		Real2Range bbox = new Real2Range();
		if (branchSet != null) {
			SVGG gg = branchSet.createSVGOld();
			g.appendChild(gg);
			bbox = bbox.plus(gg.getBoundingBox());
		}
		if (leaf != null) {
			SVGG gg = leaf.createSVGOld();
			g.appendChild(gg);
			bbox = bbox.plus(gg.getBoundingBox());
		}
//		g.setTransform(new Transform2(new Vector2(0.0, bbox.getYRange().getRange()/2.)));
		g.setTransform(new Transform2(new Vector2(0.0, bbox.getYRange().getRange())));
		return g;
	}

	public Element createXML() {
		Element subNode = null;
		if (branchSet != null) {
			subNode = branchSet.createXML();
		}
		if (leaf != null) {
			subNode = leaf.createXML();
		}
		return subNode;
	}

}

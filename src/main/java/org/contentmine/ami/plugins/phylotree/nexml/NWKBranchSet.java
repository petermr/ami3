package org.contentmine.ami.plugins.phylotree.nexml;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;

/**
BranchSet --> Branch | Branch "," BranchSet
Branch --> Subtree Length

 * 
 * @param string
 */

public class NWKBranchSet {
	
	private static final Logger LOG = Logger.getLogger(NWKBranchSet.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
   private List<NWKBranch> branchList;

	public NWKBranchSet(String s) {
		
	}

	public NWKBranchSet() {
	}

	public static NWKBranchSet createBranchSet(StringBuilder sb) {
		NWKBranchSet branchSet = null;
		NWKTree.trim(sb);
		while (sb.length() > 0) {
			NWKBranch branch = NWKBranch.createBranch(sb);
			if (branch == null) {
				if (sb.length() > 0) {
					throw new RuntimeException("unexpected string: "+sb);
				}
				break;
			}
			if (branchSet == null) {
				branchSet = new NWKBranchSet();
			}
			branchSet.add(branch);
			NWKTree.trim(sb);
			if (sb.toString().startsWith(",")) {
				sb.delete(0,  1);
				NWKTree.trim(sb);
			} else {
				break;
			}
		}
		return branchSet;
	}

	private void add(NWKBranch branch) {
		if (branchList == null) {
			branchList = new ArrayList<NWKBranch>();
		}
		branchList.add(branch);
	}
	
	public String toString() {
		String s = "{brSet: ";
		for (NWKBranch branch : branchList) {
			s += String.valueOf(branch);
		}
		s += "}";
		return s;
	}

	public void createNewick(StringBuilder sb) {
		if (branchList != null) {
			int i = 0;
			if (branchList.size() > 1) {
				sb.append("(");
			}
			for (NWKBranch branch : branchList) {
				if (i++ > 0) {
					sb.append(",");
				}
				branch.createNewick(sb);
			}
			if (branchList.size() > 1) {
				sb.append(")");
			}
		}
	}
	
	public SVGG createSVGOld() {
		SVGG g = new SVGG();
		Real2Range bbox = new Real2Range();
		double deltaY = 0.;
		double deltaX = 50.0;
		if (branchList != null) {
			for (NWKBranch branch : branchList) {
				SVGG gg = branch.createSVGOld();
				g.appendChild(gg);
				Real2Range bbox1 = gg.getBoundingBox();
				deltaY += bbox1.getYRange().getRange();
				bbox = bbox.plus(bbox1);
				gg.setTransform(new Transform2(new Vector2(0.0, deltaY)));
			}
		}
		g.setTransform(new Transform2(new Vector2(deltaX, deltaY/2.0)));
		return g;
	}

	public Element createXML() {
		Element node = new Element("bset");
		if (branchList != null) {
			for (NWKBranch branch : branchList) {
				Element subNode = branch.createXML();
				if (subNode != null) {
					node.appendChild(subNode);
				}
			}
		}
		return node;
	}


}

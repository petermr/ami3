package org.contentmine.ami.plugins.phylotree.nexml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;


/*
 * 
Branch --> Subtree Length
 */
public class NWKBranch {

	private static final Logger LOG = Logger.getLogger(NWKBranch.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private final static Pattern BRANCH_SEPARATOR_PATTERN = Pattern.compile("\\s*,\\s*");
	
	private NWKSubtree subtree;
	private NWKLength length;

	public NWKBranch(NWKSubtree subtree) {
		this.subtree = subtree;
	}

	public static NWKBranch createBranch(StringBuilder sb) {
		NWKBranch branch = null;
		NWKSubtree subtree = NWKSubtree.createSubtree(sb);
		if (subtree != null) {
			branch = new NWKBranch(subtree);
			if (sb.length() > 0) {
				NWKLength length = NWKLength.createLengthAndEatSB(sb);
				if (length != null) {
					branch.setLength(length);
				}
				NWKTree.trim(sb);
			}
		}
		return branch;
	}

	private void setLength(NWKLength length) {
		this.length = length;
	}

	public String toString() {
		String s = "<st: "+String.valueOf(subtree);
		if (length != null) {
			s += "; "+length.toString();
		}
		s += ">";
		return s;
	}

	public void createNewick(StringBuilder sb) {
		if (subtree != null) {
			subtree.createNewick(sb);
		} 
		if (length != null) {
			length.createNewick(sb);
		}
	}

	public SVGG createSVGOld() {
		SVGG g = new SVGG();
		Real2Range bbox = new Real2Range();
		if (subtree != null) {
			SVGG gg = subtree.createSVGOld();
			g.appendChild(gg);
			bbox = bbox.plus(gg.getBoundingBox());
		}
		Double deltaX = (length == null) ? 50. : length.getLength();
//		g.setTransform(new Transform2(new Vector2(deltaX, bbox.getYRange().getRange()/2.)));
		g.setTransform(new Transform2(new Vector2(deltaX, 0.)));
		return g;
	}

	public Element createXML() {
		Element node = new Element("br");
		if (subtree != null) {
			Element subNode = subtree.createXML();
			if (subNode != null) {
				node.appendChild(subNode);
			}
		}
		return node;
	}

}

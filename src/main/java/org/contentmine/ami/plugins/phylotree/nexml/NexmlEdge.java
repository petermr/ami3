package org.contentmine.ami.plugins.phylotree.nexml;

import nu.xom.Attribute;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGLine;

public class NexmlEdge extends NexmlElement {

	private final static Logger LOG = LogManager.getLogger(NexmlEdge.class);
	
	public final static String TAG = "edge";
	
	private static final String TARGET = "target";
	private static final String SOURCE = "source";

	private NexmlNode[] node;

	/** constructor.
	 * 
	 */
	public NexmlEdge() {
		super(TAG);
		node = new NexmlNode[2];
	}

	public NexmlEdge(NexmlNode nexmlNode0, NexmlNode nexmlNode1) {
		this();
		addNodes(nexmlNode0, nexmlNode1);
	}

	/** NOTE this requires node0 to be parent and node1 to be child
	 * 
	 * @param id
	 * @param node0
	 * @param node1
	 */
	public NexmlEdge(String id, NexmlNode node0, NexmlNode node1) {
		this(node0, node1);
		this.setId(id);
		node1.setParentNexmlNode(node0);
		node0.addChildNode(node1);
	}

	/**
	 * 
	 * @param nexmlNode0 is source
	 * @param nexmlNode1 is target
	 */
	private void addNodes(NexmlNode nexmlNode0, NexmlNode nexmlNode1) {
		this.node[0] = nexmlNode0;
		this.setSource(node[0].getId());
		this.node[1] = nexmlNode1;
		if (node[1] != null) {
			this.setTarget(node[1].getId());
		}
		
		LOG.trace("edge: "+node[0]+"; "+node[1]);
	}

	public void setTarget(String target) {
		this.addAttribute(new Attribute(TARGET, target));
	}

	public void setSource(String source) {
		this.addAttribute(new Attribute(SOURCE, source));
	}

	public String getSourceId() {
		return getAttributeValue(SOURCE);
	}

	public String getTargetId() {
		return getAttributeValue(TARGET);
	}

	@Override
	public String toString() {
		return getSourceId()+"=>"+getTargetId();
	}

	public NexmlNode getNexmlNode(int i) {
		return (node != null && i >= 0 && i < 2) ? node[i] : null;
	}

	public NexmlNode getOtherNode(NexmlNode nexmlNode) {
		NexmlNode otherNode = null;
		if (nexmlNode == null) {
			// null
		} else if (nexmlNode.equals(node[0])) {
			otherNode = node[1];
		} else if (nexmlNode.equals(node[1])) {
			otherNode = node[0];
		} else {
			// null
		}
		return otherNode;
	}
	
	/**
	 * 
	 * @return null if some or all nodes are null or have no coordinates
	 */
	public SVGLine createSVG() {
		SVGLine line = null;
		if (node != null && node.length == 2) {
			Real2 xy0 = node[0] == null ? null : node[0].getXY2(); 
			Real2 xy1 = node[1] == null ? null : node[1].getXY2(); 
			if (xy0 != null && xy1 != null) {
				line = new SVGLine(xy0, xy1);
				line.setStrokeWidth(0.5);
			}
		}
		return line;
	}
	
	
}

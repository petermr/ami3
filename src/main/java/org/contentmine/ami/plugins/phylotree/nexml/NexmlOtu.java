package org.contentmine.ami.plugins.phylotree.nexml;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.phylotree.PhyloConstants;
import org.contentmine.norma.editor.EditList;

public class NexmlOtu extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlOtu.class);
	public final static String TAG = "otu";

	private static final String EDIT_RECORD = "editRecord";
	private static final String GENUS = "genus";
	private static final String SPECIES = "species";

	/** constructor.
	 * 
	 */
	public NexmlOtu() {
		super(TAG);
	}

	public NexmlOtu(String id) {
		this();
		this.addAttribute(new Attribute("id", id));
	}

	public void setEditRecord(String editRecord) {
		this.addAttribute(new Attribute(EDIT_RECORD, editRecord));
	}

	public void annotateOtuWithEditRecord(EditList editRecord) {
		if (editRecord.size() > 0) {
			String edit = editRecord.toString();
			addAttribute(new Attribute(PhyloConstants.CM_PHYLO_PREFIX+":edit", PhyloConstants.CM_PHYLO_NS, edit));
		}
	}
	
	public void setGenus(String value) {
		setCMPhyloAttribute(GENUS, value);
	}

	public String getGenus() {
		return getCMPhyloAttributeValue(GENUS);
	}

	public void setSpecies(String value) {
		setCMPhyloAttribute(SPECIES, value);
	}

	public String getSpecies() {
		return getCMPhyloAttributeValue(SPECIES);
	}
	
	public String getBinomial() {
		return (getGenus() != null && getSpecies() != null) ? getGenus()+"_"+getSpecies() : null;
	}

	private void setCMPhyloAttribute(String name, String value) {
		try {
			addAttribute(new Attribute(PhyloConstants.CM_PHYLO_PREFIX+":"+name, PhyloConstants.CM_PHYLO_NS, value));
		} catch (Exception e) {
			LOG.error("BAD attribute name: "+name);
		}
	}

	private String getCMPhyloAttributeValue(String name) {
//		return getAttributeValue(PhyloConstants.CM_PHYLO_PREFIX+":"+name, PhyloConstants.CM_PHYLO_NS);
		return getAttributeValue(name, PhyloConstants.CM_PHYLO_NS);
	}
}

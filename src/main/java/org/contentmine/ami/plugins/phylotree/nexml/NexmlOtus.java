package org.contentmine.ami.plugins.phylotree.nexml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

public class NexmlOtus extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlOtus.class);
	public final static String TAG = "otus";
	private Map<String, NexmlOtu> otuByIdMap;
	private List<NexmlOtu> otuList;

	/** constructor.
	 * 
	 */
	public NexmlOtus() {
		super(TAG);
	}

	public List<NexmlOtu> getNexmlOtuList() {
		if (otuList == null) {
			otuList = new ArrayList<NexmlOtu>();
			List<Element> otuElements = XMLUtil.getQueryElements(this, "*[local-name()='"+NexmlOtu.TAG+"']");
			for (Element otuElement : otuElements) {
				otuList.add((NexmlOtu) otuElement);
			}
		}
		return otuList;
	}

	public NexmlOtu getOtuById(String otuId) {
		ensureOtuByIdMap();
		NexmlOtu otu = otuByIdMap.get(otuId);
		return otu;
	}

	private void ensureOtuByIdMap() {
		if (otuByIdMap == null) {
			otuByIdMap = new HashMap<String, NexmlOtu>();
			getNexmlOtuList();
			for (NexmlOtu otu : otuList) {
				otuByIdMap.put(otu.getId(), otu);
			}
		}
	}

	public void addOtu(NexmlOtu otu) {
		if (getOtuById(otu.getId()) == null) {
			this.appendChild(otu);
		} else {
			LOG.error("already has OTU with that id");
		}
	}

	public NexmlOtu getOtuByIdWithXPath(String otuRef) {
		List<Element> elements = XMLUtil.getQueryElements(this, "./*[local-name()='otu' and @id='"+otuRef+"']");
		return (elements.size() != 1) ? null : (NexmlOtu) elements.get(0);
	}

	
}
